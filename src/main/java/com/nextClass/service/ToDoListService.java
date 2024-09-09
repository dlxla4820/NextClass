package com.nextClass.service;

import com.nextClass.scheduler.SchedulerMain;
import com.nextClass.dto.ResponseDto;
import com.nextClass.dto.ToDoListRequsetDto;
import com.nextClass.dto.ToDoListResponseDto;
import com.nextClass.entity.ToDoList;
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
    private final ToDoListDetailRepository toDoListRepository;
    private final LoginRepository loginRepository;
    private final SchedulerMain schedulerMain;
    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime nextHour = now.plusHours(1).withMinute(0).withSecond(0).withNano(0);

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
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("ToDoService << createToDoList >> | memberUuid : {}, requestBody : {}", memberUuid, toDoListRequsetDto);
        String checkRequestDto = checkToDoListRequest(toDoListRequsetDto);
        LocalDateTime alarmTime = toDoListRequsetDto.getAlarm_time();
        //현재 로그인 한 사람 데이터 가져오기
        if (checkRequestDto != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), checkRequestDto);
        if (toDoListRequsetDto.getGoal_time() == (null))
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "create_time"));

        toDoListRequsetDto.setMember_uuid(convertToUUID(memberUuid));
        toDoListRequsetDto.setCreated_time(now);
        toDoListRequsetDto.setUpdate_time(toDoListRequsetDto.getCreated_time());
        toDoListRequsetDto.setApp_token(loginRepository.getMemberByUuid(memberUuid).getAppToken());

        if (toDoListRepository.checkDuplicate(toDoListRequsetDto) != null) return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TO_DO_LIST_ALREADY_EXIST.getErrorCode(), ErrorCode.TO_DO_LIST_ALREADY_EXIST.getErrorDescription());
        ToDoList result = toDoListRepository.save(toDoListRequsetDto);
        if (alarmTime != null && alarmTime.isAfter(now) && alarmTime.isBefore(nextHour)) schedulerMain.toDoListAlarmScheduler(result);

        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

        public ResponseDto<?> updateToDoList(ToDoListRequsetDto toDoListRequsetDto){
            String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
            log.info("ToDoService << updateToDoList >> | memberUuid : {}, requestBody : {}", memberUuid, toDoListRequsetDto);
            String checkRequestDto = checkToDoListRequest(toDoListRequsetDto);
            LocalDateTime alarmTime = toDoListRequsetDto.getAlarm_time();
            //현재 로그인 한 사람 데이터 가져오기
            if (checkRequestDto != null)
                return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), checkRequestDto);

            if (toDoListRequsetDto.getGoal_time() == (null))
                return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "create_time"));

            toDoListRequsetDto.setMember_uuid(convertToUUID(memberUuid));
            ToDoList beforeToDoList = toDoListRepository.checkAuthorize(toDoListRequsetDto);

            if (beforeToDoList == null)
                return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TOKEN_UNAUTHORIZED.getErrorCode(), ErrorCode.TOKEN_UNAUTHORIZED.getErrorDescription());

            toDoListRequsetDto.setUuid(beforeToDoList.getUuid());
            toDoListRequsetDto.setCreated_time(beforeToDoList.getCreateTime());
            toDoListRequsetDto.setUpdate_time(now);
            toDoListRequsetDto.setApp_token(loginRepository.getMemberByUuid(memberUuid).getAppToken());
            ToDoList result = toDoListRepository.update(toDoListRequsetDto);

            if (alarmTime != null && alarmTime.isAfter(now)) schedulerMain.updateToDoListAlarmScheduler(result);
            return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto<?> deleteToDoList(ToDoListRequsetDto toDoListRequsetDto){
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("ToDoService << deleteToDoList >> | memberUuid : {}, requestBody : {}", memberUuid, toDoListRequsetDto);
        String checkRequestDto = checkToDoListRequest(toDoListRequsetDto);
        LocalDateTime alarmTime = toDoListRequsetDto.getAlarm_time();
        if (checkRequestDto != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), checkRequestDto);

        ToDoList currentToDoList = toDoListRepository.checkExist(toDoListRequsetDto);
        if (currentToDoList == (null))
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TO_DO_LIST_ALREADY_DELETE.getErrorCode(), String.format(ErrorCode.TO_DO_LIST_ALREADY_DELETE.getErrorDescription()));

        if (!memberUuid.equals(currentToDoList.getMember_uuid().toString()))
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TOKEN_UNAUTHORIZED.getErrorCode(), ErrorCode.TOKEN_UNAUTHORIZED.getErrorDescription());

        toDoListRepository.delete(currentToDoList);
        if (alarmTime != null && alarmTime.isAfter(now) && alarmTime.isBefore(nextHour)) schedulerMain.finishTask(currentToDoList.getUuid());

        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto<?> readAllToDoList(){
        String currentUser = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("ToDoService << readAllToDoList >> | memberUuid : {}", currentUser);
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
                tuple.get(toDoList.goalTime),
                tuple.get(toDoList.alarmTime)
        );
    }

}
