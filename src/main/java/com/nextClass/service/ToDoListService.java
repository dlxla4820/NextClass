package com.nextClass.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.nextClass.Scheduler.SchedulerMain;
import com.nextClass.dto.ResponseDto;
import com.nextClass.dto.TimeTableReponseDto;
import com.nextClass.dto.ToDoListRequsetDto;
import com.nextClass.dto.ToDoListResponseDto;
import com.nextClass.entity.ToDoList;
import com.nextClass.entity.ToDoListAlarm;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.repository.LoginRepository;
import com.nextClass.repository.ToDoListDetailRepository;
import com.nextClass.utils.CommonUtils;
import com.querydsl.core.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.nextClass.entity.QToDoList.toDoList;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ToDoListService {
    private ToDoListDetailRepository toDoListRepository;
    //    private JobLauncher jobLauncher;
//    private Job job;
    private LoginRepository loginRepository;
    private SchedulerMain schedulerMain;
//    private TaskScheduler taskScheduler;

    public ToDoListService(
            ToDoListDetailRepository toDoListDetailRepository,
            LoginRepository loginRepository,
            SchedulerMain schedulerMain
//            TaskScheduler taskScheduler
//            JobLauncher jobLauncher,
//            Job job
    ) {
        this.toDoListRepository = toDoListDetailRepository;
        this.loginRepository = loginRepository;
        this.schedulerMain = schedulerMain;
//        this.jobLauncher = jobLauncher;
//        this.job = job;
//        this.taskScheduler = taskScheduler;
    }

    public ResponseDto<?> createToDoList(ToDoListRequsetDto toDoListRequsetDto) {
        log.info("ToDoService << createToDoList >> | requestBody : {}", toDoListRequsetDto);
        String checkRequestDto = checkToDoListRequest(toDoListRequsetDto);
        LocalDateTime alarmTime = toDoListRequsetDto.getAlarm_time();
        //현재 로그인 한 사람 데이터 가져오기
        if (checkRequestDto != null) {
            log.error("ToDoService << createToDoList >> | PARAMETER_INVALID_SPECIFIC");
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), checkRequestDto);
        }
        if (toDoListRequsetDto.getGoal_time() == (null)) {
            log.error("ToDoService << createToDoList >> | PARAMETER_INVALID_SPECIFIC");
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "create_time"));
        }
        toDoListRequsetDto.setMember_uuid(convertToUUID(CommonUtils.getMemberUuidIfAdminOrUser()));
        toDoListRequsetDto.setCreated_time(LocalDateTime.now());
        toDoListRequsetDto.setUpdate_time(toDoListRequsetDto.getCreated_time());
        toDoListRequsetDto.setApp_token(loginRepository.getMemberByUuid(CommonUtils.getMemberUuidIfAdminOrUser()).getAppToken());
        if (toDoListRepository.checkDuplicate(toDoListRequsetDto) != null) {
            log.error("ToDoService << createToDoList >> | TO_DO_LIST_ALREADY_EXIST");
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TO_DO_LIST_ALREADY_EXIST.getErrorCode(), ErrorCode.TO_DO_LIST_ALREADY_EXIST.getErrorDescription());
        }
        ToDoList result = toDoListRepository.save(toDoListRequsetDto);
        if (alarmTime != null) {
            toDoListRepository.saveAlarm(result.getUuid());
            schedulerMain.toDoListAlarmScheduler(result);
        }
        log.info("ToDoService << createToDoList >> | toDoList : {}", toDoList);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

//        public ResponseDto<?> updateToDoList(ToDoListRequsetDto toDoListDto){
//        //request검증(uuid, updateTime, content, alarmTime,doneTime)
//        //현재 로그인 한 사람 가져오기
//        //해당 uuid를 가진 todoList의 생성자가 해당 멤버인지 검증
//        //아닐 경우 UnAuthorized 에러
//        //맞을 경우 해당 데이터 업데이트 후 firebase의 알람도 update
//        //성공 return
//    }
//
//    public ResponseDto<?> deleteToDoList(ToDoListRequsetDto toDoListRequsetDto){
//        //해당 uuid가 존재하는지 확인
//        //존재하지 않으면 존재하지 않는다 에러
//        //현재 접속 유저와 해당 to_do_list의 생성자가 일치하는지 확인
//        //일치하지 않으면 권한 없음
//        //일치 하면 firebase 알람 삭제 후 해당 ToDoList 삭제 후 return
//    }
//
    public ResponseDto<?> readAllToDoList(){
        log.info("ToDoService << readAllToDoList >> | requestBody : none");
        //로그인 한 유저 확인
        String currentUser = CommonUtils.getMemberUuidIfAdminOrUser();
        //해당 유저가 생성한 ToDoList전부 읽어온 뒤에 return
        List<ToDoListResponseDto> toDoList;
            toDoList = toDoListRepository.readAll(currentUser).stream().map(this::convertTupleToResponse).collect(Collectors.toList());
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS, toDoList);
    }
    private String checkToDoListRequest(ToDoListRequsetDto toDoListRequsetDto) {
        if (toDoListRequsetDto.getContent() == null || toDoListRequsetDto.getContent().isBlank()) {
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "content");
        } else if (toDoListRequsetDto.getGoal_time() == null) {
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "goal_time");
        } else {
            return null;
        }
    }

    private UUID convertToUUID(String uuidString) {
        String formattedUuidString = uuidString.replaceAll(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{12})",
                "$1-$2-$3-$4-$5"
        );
        return UUID.fromString(formattedUuidString);
    }

    private ToDoListResponseDto convertTupleToResponse(Tuple tuple){
        return new ToDoListResponseDto(
                tuple.get(toDoList.uuid),
                tuple.get(toDoList.content),
                tuple.get(toDoList.alarmTime),
                tuple.get(toDoList.goalTime)
        );
    }

}
