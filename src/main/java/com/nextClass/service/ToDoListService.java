package com.nextClass.service;

import com.nextClass.dto.ResponseDto;
import com.nextClass.dto.ToDoListRequestDto;
import com.nextClass.dto.ToDoListResponseDto;
import com.nextClass.entity.ToDoList;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.repository.LoginRepository;
import com.nextClass.repository.ToDoListDetailRepository;
import com.nextClass.scheduler.SchedulerMain;
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
import static com.nextClass.enums.ErrorCode.TOKEN_UNAUTHORIZED;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ToDoListService {
    private final ToDoListDetailRepository toDoListRepository;
    private final LoginRepository loginRepository;
    private final SchedulerMain schedulerMain;
    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime nextMinute = now.plusMinutes(1).withSecond(0).withNano(0);

    public ToDoListService(ToDoListDetailRepository toDoListDetailRepository, LoginRepository loginRepository, SchedulerMain schedulerMain) {
        this.toDoListRepository = toDoListDetailRepository;
        this.loginRepository = loginRepository;
        this.schedulerMain = schedulerMain;
    }

    public ResponseDto<?> createToDoList(ToDoListRequestDto ToDoListRequestDto) {
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("ToDoService << createToDoList >> | memberUuid : {}, requestBody : {}", memberUuid, ToDoListRequestDto);
        if (memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());
        String checkRequestDto = checkToDoListRequest(ToDoListRequestDto);
        LocalDateTime alarmTime = ToDoListRequestDto.getAlarmTime();
        //현재 로그인 한 사람 데이터 가져오기
        if (checkRequestDto != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), checkRequestDto);
        if (ToDoListRequestDto.getGoalTime() == (null))
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "create_time"));

        ToDoListRequestDto.setMemberUuid(convertToUUID(memberUuid));
        ToDoListRequestDto.setCreatedTime(now);
        ToDoListRequestDto.setUpdateTime(ToDoListRequestDto.getCreatedTime());
        ToDoListRequestDto.setAppToken(loginRepository.getMemberByUuid(memberUuid).getAppToken());

        if (toDoListRepository.checkDuplicate(ToDoListRequestDto) != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TO_DO_LIST_ALREADY_EXIST.getErrorCode(), ErrorCode.TO_DO_LIST_ALREADY_EXIST.getErrorDescription());
        ToDoList result = toDoListRepository.save(ToDoListRequestDto);
        if (alarmTime != null && alarmTime.isAfter(now) && alarmTime.isBefore(nextMinute))
            schedulerMain.toDoListAlarmScheduler(result);
        log.info("ToDoService << createToDoList >> |  : ToDoList {}", ToDoListRequestDto);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto<?> updateToDoList(ToDoListRequestDto ToDoListRequestDto) {
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("ToDoService << updateToDoList >> | memberUuid : {}, requestBody : {}", memberUuid, ToDoListRequestDto);
        if (memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());
        String checkRequestDto = checkToDoListRequest(ToDoListRequestDto);
        LocalDateTime alarmTime = ToDoListRequestDto.getAlarmTime();
        //현재 로그인 한 사람 데이터 가져오기
        if (checkRequestDto != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), checkRequestDto);

        if (ToDoListRequestDto.getGoalTime() == (null))
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "create_time"));

        ToDoListRequestDto.setMemberUuid(convertToUUID(memberUuid));
        ToDoList beforeToDoList = toDoListRepository.checkAuthorize(ToDoListRequestDto);

        if (beforeToDoList == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TOKEN_UNAUTHORIZED.getErrorCode(), ErrorCode.TOKEN_UNAUTHORIZED.getErrorDescription());

        ToDoListRequestDto.setUuid(beforeToDoList.getUuid());
        ToDoListRequestDto.setCreatedTime(beforeToDoList.getCreateTime());
        ToDoListRequestDto.setUpdateTime(now);
        ToDoListRequestDto.setAppToken(loginRepository.getMemberByUuid(memberUuid).getAppToken());
        ToDoList result = toDoListRepository.update(ToDoListRequestDto);

        if (alarmTime != null && alarmTime.isAfter(now)) schedulerMain.updateToDoListAlarmScheduler(result);
        log.info("ToDoService << updateToDoList >> |  : ToDoList {}", ToDoListRequestDto);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto<?> deleteToDoList(ToDoListRequestDto ToDoListRequestDto) {
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("ToDoService << deleteToDoList >> | memberUuid : {}, requestBody : {}", memberUuid, ToDoListRequestDto);
        if (memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());
        String checkRequestDto = checkToDoListRequest(ToDoListRequestDto);
        LocalDateTime alarmTime = ToDoListRequestDto.getAlarmTime();
        if (checkRequestDto != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), checkRequestDto);

        ToDoList currentToDoList = toDoListRepository.checkExist(ToDoListRequestDto);
        if (currentToDoList == (null))
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TO_DO_LIST_ALREADY_DELETE.getErrorCode(), String.format(ErrorCode.TO_DO_LIST_ALREADY_DELETE.getErrorDescription()));

        if (!memberUuid.equals(currentToDoList.getMember_uuid().toString()))
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TOKEN_UNAUTHORIZED.getErrorCode(), ErrorCode.TOKEN_UNAUTHORIZED.getErrorDescription());

        toDoListRepository.delete(currentToDoList);
        if (alarmTime != null && alarmTime.isAfter(now) && alarmTime.isBefore(nextMinute))
            schedulerMain.finishTask(currentToDoList.getUuid());
        log.info("ToDoService << deleteToDoList >> |  : ToDoList {}", currentToDoList);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto<?> readAllToDoList() {
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("ToDoService << readAllToDoList >> | memberUuid : {}", memberUuid);
        if (memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());
        List<ToDoListResponseDto> toDoList;
        toDoList = toDoListRepository.readAll(memberUuid).stream().map(this::convertTupleToResponse).collect(Collectors.toList());
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS, toDoList);
    }

    private String checkToDoListRequest(ToDoListRequestDto ToDoListRequestDto) {
        if (ToDoListRequestDto.getContent() == null || ToDoListRequestDto.getContent().isBlank()) {
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "content");
        } else if (ToDoListRequestDto.getGoalTime() == null) {
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "goal_time");
        } else {
            return null;
        }
    }

    private UUID convertToUUID(String uuidString) {
        String formattedUuidString = uuidString.replaceAll("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{12})", "$1-$2-$3-$4-$5");
        return UUID.fromString(formattedUuidString);
    }

    private ToDoListResponseDto convertTupleToResponse(Tuple tuple) {
        return new ToDoListResponseDto(tuple.get(toDoList.uuid), tuple.get(toDoList.content), tuple.get(toDoList.goalTime), tuple.get(toDoList.alarmTime));
    }

}
