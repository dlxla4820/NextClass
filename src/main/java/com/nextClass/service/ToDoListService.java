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
import com.querydsl.core.types.dsl.Expressions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.nextClass.entity.QToDoList.toDoList;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ToDoListService {
    private ToDoListDetailRepository toDoListRepository;
    private LoginRepository loginRepository;
    private SchedulerMain schedulerMain;

    public ToDoListService(
            ToDoListDetailRepository toDoListDetailRepository,
            LoginRepository loginRepository,
            SchedulerMain schedulerMain
    ) {
        this.toDoListRepository = toDoListDetailRepository;
        this.loginRepository = loginRepository;
        this.schedulerMain = schedulerMain;
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
        if (alarmTime != null && alarmTime.isAfter(LocalDateTime.now())) {
            toDoListRepository.saveAlarm(result.getUuid());
            schedulerMain.toDoListAlarmScheduler(result);
        }
        log.info("ToDoService << createToDoList >> | toDoList : {}", toDoList);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

        public ResponseDto<?> updateToDoList(ToDoListRequsetDto toDoListRequsetDto){
        //request검증(uuid, updateTime, content, alarmTime,doneTime)
            log.info("ToDoService << updateToDoList >> | requestBody : {}", toDoListRequsetDto);
            String checkRequestDto = checkToDoListRequest(toDoListRequsetDto);
            LocalDateTime alarmTime = toDoListRequsetDto.getAlarm_time();
            //현재 로그인 한 사람 데이터 가져오기
            if (checkRequestDto != null) {
                log.error("ToDoService << updateToDoList >> | PARAMETER_INVALID_SPECIFIC");
                return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), checkRequestDto);
            }
            if (toDoListRequsetDto.getGoal_time() == (null)) {
                log.error("ToDoService << updateToDoList >> | PARAMETER_INVALID_SPECIFIC");
                return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "create_time"));
            }
            toDoListRequsetDto.setMember_uuid(convertToUUID(CommonUtils.getMemberUuidIfAdminOrUser()));
            ToDoList beforeToDoList = toDoListRepository.checkAuthorize(toDoListRequsetDto);
            if (beforeToDoList == null) {
                log.error("ToDoService << updateToDoList >> | UNAUTHORIZED TO DO LIST");
                return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TOKEN_UNAUTHORIZED.getErrorCode(), ErrorCode.TOKEN_UNAUTHORIZED.getErrorDescription());
            }
            toDoListRequsetDto.setCreated_time(beforeToDoList.getCreateTime());
            toDoListRequsetDto.setUpdate_time(LocalDateTime.now());
            toDoListRequsetDto.setApp_token(loginRepository.getMemberByUuid(CommonUtils.getMemberUuidIfAdminOrUser()).getAppToken());

            ToDoList result = toDoListRepository.save(toDoListRequsetDto);
            if (alarmTime != null && alarmTime.isAfter(LocalDateTime.now())) {
                toDoListRepository.saveAlarm(result.getUuid());
                schedulerMain.updateToDoListAlarmScheduler(result);
            }
            log.info("ToDoService << updateToDoList >> | toDoList : {}", toDoList);
            return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto<?> deleteToDoList(ToDoListRequsetDto toDoListRequsetDto){
        log.info("ToDoService << deleteToDoList >> | requestBody : {}", toDoListRequsetDto);
        String checkRequestDto = checkToDoListRequest(toDoListRequsetDto);
        LocalDateTime alarmTime = toDoListRequsetDto.getAlarm_time();
        if (checkRequestDto != null) {
            log.error("ToDoService << deleteToDoList >> | PARAMETER_INVALID_SPECIFIC");
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), checkRequestDto);
        }
        ToDoList currentToDoList = toDoListRepository.checkExist(toDoListRequsetDto);
        if (currentToDoList == (null)) {
            log.error("ToDoService << deleteToDoList >> | TO_DO_LIST_ALREADY_DELETE");
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TO_DO_LIST_ALREADY_DELETE.getErrorCode(), String.format(ErrorCode.TO_DO_LIST_ALREADY_DELETE.getErrorDescription()));
        }
        if (!CommonUtils.getMemberUuidIfAdminOrUser().equals(currentToDoList.getMember_uuid().toString())) {
            log.error("ToDoService << deleteToDoList >> | UNAUTHORIZED TO DO LIST");
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TOKEN_UNAUTHORIZED.getErrorCode(), ErrorCode.TOKEN_UNAUTHORIZED.getErrorDescription());
        }
        toDoListRepository.delete(currentToDoList);
        if (alarmTime != null && alarmTime.isAfter(LocalDateTime.now())) {
            toDoListRepository.deleteAlarm(currentToDoList.getUuid());
            schedulerMain.finishTask(currentToDoList.getUuid());
        }
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

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
