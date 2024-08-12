package com.nextClass.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.nextClass.dto.ResponseDto;
import com.nextClass.dto.ToDoListRequsetDto;
import com.nextClass.entity.ToDoList;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.repository.LoginRepository;
import com.nextClass.repository.ToDoListDetailRepository;
import com.nextClass.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class ToDoListService {
    private ToDoListDetailRepository toDoListRepository;
    //    private JobLauncher jobLauncher;
//    private Job job;
    private LoginRepository loginRepository;

    public ToDoListService(
            ToDoListDetailRepository toDoListDetailRepository,
            LoginRepository loginRepository
//            JobLauncher jobLauncher,
//            Job job
    ) {
        this.toDoListRepository = toDoListDetailRepository;
        this.loginRepository = loginRepository;
//        this.jobLauncher = jobLauncher;
//        this.job = job;
    }

    public ResponseDto<?> createToDoList(ToDoListRequsetDto toDoListRequsetDto) {
        log.info("ToDoService << createToDoList >> | requestBody : {}", toDoListRequsetDto);
        String checkRequestDto = checkToDoListRequest(toDoListRequsetDto);
        //현재 로그인 한 사람 데이터 가져오기
        if (checkRequestDto != null) {
            log.error("ToDoService << createToDoList >> | PARAMETER_INVALID_SPECIFIC");
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), checkRequestDto);
        }
        if (toDoListRequsetDto.getGoal_time() == (null)) {
            log.error("ToDoService << createToDoList >> | PARAMETER_INVALID_SPECIFIC");
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "create_time"));
        }
        toDoListRequsetDto.setMember_uuid(CommonUtils.getMemberUuidIfAdminOrUser());
        toDoListRequsetDto.setCreated_time(LocalDateTime.now());
        toDoListRequsetDto.setUpdate_time(toDoListRequsetDto.getCreated_time());
        if (toDoListRepository.checkDuplicate(toDoListRequsetDto) != null) {
            log.error("ToDoService << createToDoList >> | TO_DO_LIST_ALREADY_EXIST");
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TO_DO_LIST_ALREADY_EXIST.getErrorCode(), ErrorCode.TO_DO_LIST_ALREADY_EXIST.getErrorDescription());
        }
        ToDoList toDoList;
        if (toDoListRequsetDto.getAlarm_time() == (null)) {
            toDoList = ToDoList.builder()
                    .content(toDoListRequsetDto.getContent())
                    .appToken(toDoListRequsetDto.getApp_token())
                    .createTime(toDoListRequsetDto.getCreated_time())
                    .updateTime(toDoListRequsetDto.getUpdate_time())
                    .goalTime(toDoListRequsetDto.getGoal_time())
                    .member_uuid(convertToUUID(toDoListRequsetDto.getMember_uuid()))
                    .build();
        } else {
            toDoList = ToDoList.builder()
                    .content(toDoListRequsetDto.getContent())
                    .appToken(toDoListRequsetDto.getApp_token())
                    .createTime(toDoListRequsetDto.getCreated_time())
                    .updateTime(toDoListRequsetDto.getUpdate_time())
                    .goalTime(toDoListRequsetDto.getGoal_time())
                    .member_uuid(convertToUUID(toDoListRequsetDto.getMember_uuid()))
                    .alarmTime(toDoListRequsetDto.getAlarm_time())
                    .build();
        }
        try {
            toDoListRepository.save(toDoList);
        } catch (DataAccessException e) {
            log.error("ToDoService << createToDoList >> | DataAccessException e : {}", e.getMessage(), e);
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.SYSTEM_ERROR.getErrorCode(), String.format(ErrorCode.SYSTEM_ERROR.getErrorDescription()));
        }
        if (!toDoListRequsetDto.getApp_token().isBlank())
        //firebase에 연결해서 알람도 설정 (alarmTime 시간)
        {
            sendToDoListNotification(toDoListRequsetDto.getContent(), loginRepository.getMemberByUuid(toDoListRequsetDto.getMember_uuid()).getAppToken());
            
        }
        log.info("ToDoService << createToDoList >> | toDoList : {}", toDoList);
        return new ResponseDto<>(HttpStatus.ACCEPTED.value(), Description.SUCCESS);
    }

    //    public ResponseDto<?> updateToDoList(ToDoListRequsetDto toDoListDto){
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
//    public ResponseDto<?> readAllToDoList(){
//        //로그인 한 유저 확인
//        //해당 유저가 생성한 ToDoList전부 읽어온 뒤에 return
//    }
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

    private String sendToDoListNotification(String body, String appToken) {
        try {
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setBody(body)
                            .build())
                    .setToken(appToken)
                    .build();
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("ToDoService << sendToDoListNotification >> | response : {}", response);
            return response;
        } catch (Exception e) {
            log.error("ToDoService << sendToDoListNotification >> | Exception : {}", e.getMessage());
            return null;
        }
    }
}
