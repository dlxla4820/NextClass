package com.nextClass.service;

import com.nextClass.dto.*;
import com.nextClass.entity.*;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.repository.LoginRepository;
import com.nextClass.repository.ScoreDetailRepository;
import com.nextClass.repository.TimeTableDetailRepository;
import com.nextClass.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.nextClass.entity.QTimeTable.timeTable;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class TimeTableService {
    private final TimeTableDetailRepository timeTableRepository;
    private final LoginRepository loginRepository;

    private final ScoreDetailRepository scoreRepository;

    @Autowired
    public TimeTableService(
            TimeTableDetailRepository timeTableRepository,
            LoginRepository loginRepository,
            ScoreDetailRepository scoreRepository
    ) {
        this.timeTableRepository = timeTableRepository;
        this.loginRepository = loginRepository;
        this.scoreRepository = scoreRepository;
    }

    //수정하기
    public ResponseDto<?> changeTimeTableData(TimeTableRequestDto timeTableRequestDto) {
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("TimeTableService << changeTimeTableData >> | memberUuid : {}, requestBody : {}", memberUuid, timeTableRequestDto);
        timeTableRequestDto.setMemberUuid(memberUuid);
        //해당 부분에 uuid값의 검증도 추가해야 함
        String errorDescription = checkTimeTableRequest(timeTableRequestDto);
        if (errorDescription != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), errorDescription);

        if (!memberUuid.equals(timeTableRepository.findTimeTableByUuid(timeTableRequestDto.getUuid()).getMemberUuid().toString()))
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TIME_TABLE_UNAUTHORIZED.getErrorCode(), ErrorCode.TIME_TABLE_UNAUTHORIZED.getErrorDescription());

        //요청받은 시간에 해당 회원이 수업이 등록되어 있는지 확인
        if (timeTableRepository.isClassExistOnSameTimeUpdate(timeTableRequestDto, memberUuid) != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.CLASS_ALREADY_EXIST_ON_SAME_TIME.getErrorCode(), ErrorCode.CLASS_ALREADY_EXIST_ON_SAME_TIME.getErrorDescription());

        timeTableRepository.updateTimeTableWithNewClassDetail(timeTableRequestDto);
        log.info("TimeTableService << changeTimeTableData >> | timeTable : {}", timeTable);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }


    public ResponseDto deleteOneTimeTable(TimeTableRequestDto timeTableRequestDto) {
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("TimeTableService << deleteOneTimeTable >> | memberUuid : {}, requestBody : {}", memberUuid, timeTableRequestDto);
        if (timeTableRequestDto.getUuid() == null || timeTableRequestDto.getUuid().isBlank())
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "timeTableUuid"));
        timeTableRequestDto.setMemberUuid(memberUuid);
        if (timeTableRepository.findTimeTableByUuid(timeTableRequestDto.getUuid()) == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.DATA_ALREADY_DELETED.getErrorCode(), ErrorCode.DATA_ALREADY_DELETED.getErrorDescription());
        if (timeTableRepository.checkCurrentUserIsOwnerOfTimeTable(timeTableRequestDto) == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TIME_TABLE_UNAUTHORIZED.getErrorCode(), ErrorCode.TIME_TABLE_UNAUTHORIZED.getErrorDescription());
        long howManyDelete = timeTableRepository.deleteTimeTable(timeTableRequestDto.getUuid());
        log.info("TimeTableService << deleteOneTimeTable >> | howManyDelete : {}", howManyDelete);

        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto deleteAllTimeTableOnSemester(TimeTableRequestDto timeTableRequestDto) {
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("TimeTableService << deleteAllTimeTableOnSemester >> | memberUuid : {}, requestBody : {}", memberUuid, timeTableRequestDto);
        if (timeTableRequestDto.getSemester() == null || timeTableRequestDto.getSemester().isBlank())
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_GENERAL.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "semester"));

        //member와 semester 2개를 받아서 삭제
        timeTableRequestDto.setMemberUuid(memberUuid);
        if (timeTableRepository.checkCurrentUserIsOwnerOfTimeTable(timeTableRequestDto) == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TIME_TABLE_UNAUTHORIZED.getErrorCode(), ErrorCode.TIME_TABLE_UNAUTHORIZED.getErrorDescription());

        //repository에서 해당 학기 데이터 삭제
        List<String> timeTableList = timeTableRepository.getTimeTableUUIDListOnSemesterFromUser(timeTableRequestDto);
        //삭제
        long howManyDelete = timeTableRepository.deleteAllTimeTableSelected(timeTableList);
        log.info("TimeTableService << deleteAllTimeTableOnSemester >> | howManyDelete : {}", howManyDelete);
        //classDetail 검증 및 삭제
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto getPersonalThisSemesterTimeTable(TimeTableRequestDto timeTableRequestDto) {
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("TimeTableService << getPersonalThisSemesterTimeTable >> | memberUuid : {}, requestBody : {}", memberUuid, timeTableRequestDto);
        if (timeTableRequestDto.getSemester() == null || timeTableRequestDto.getSemester().isBlank())
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_GENERAL.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "semester"));
        timeTableRequestDto.setMemberUuid(memberUuid);
        //member와 semester를 가지고 해당 데이터 가져오기(현재는 semester만)
        List<TimeTableReponseDto> timeTableList = timeTableRepository.getTimeTableListOnSemesterFromUser(timeTableRequestDto);
        log.info("TimeTableService << getPersonalThisSemesterTimeTable >> | timeTableList : {}", timeTableList);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS, timeTableList);
    }

    public ResponseDto makeTimeTable(TimeTableRequestDto timeTableRequestDto) {
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("TimeTableService << makeTimeTable >> | memberUuid : {}, requestBody : {}", memberUuid, timeTableRequestDto);
        //DTO의 값이 비어 있으면 해당 값 비어 있다는 error를 담아서 responseDTO return, for문을 통해서 진행
        String errorDescription = checkTimeTableRequest(timeTableRequestDto);
        if (errorDescription != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), errorDescription);

        //요청받은 시간에 해당 회원이 수업이 등록되어 있는지 확인
        if (timeTableRepository.isClassExistOnSameTime(timeTableRequestDto, memberUuid) != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.CLASS_ALREADY_EXIST_ON_SAME_TIME.getErrorCode(), ErrorCode.CLASS_ALREADY_EXIST_ON_SAME_TIME.getErrorDescription());

        timeTableRequestDto.setMemberUuid(memberUuid);

        TimeTable isDataSaved = timeTableRepository.findTimeTable(timeTableRequestDto);
        if (isDataSaved != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.DATA_ALREADY_EXIST.getErrorCode(), ErrorCode.DATA_ALREADY_EXIST.getErrorDescription());

        timeTableRepository.saveTimeTable(timeTableRequestDto);
        scoreRepository.saveScore(timeTableRequestDto);
        log.info("TimeTableService << makeTimeTable >> | timeTable : {}", timeTable);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }


    private String checkTimeTableRequest(TimeTableRequestDto timeTableRequestDto) {
        if (timeTableRequestDto.getWeek() == null || timeTableRequestDto.getWeek().isBlank()) {
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "week");
        } else if (timeTableRequestDto.getClassStartTime() == null || timeTableRequestDto.getClassStartTime() < 0 || timeTableRequestDto.getClassStartTime() > 8) {
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "class_start_time");
        } else if (timeTableRequestDto.getClassEndTime() == null || timeTableRequestDto.getClassEndTime() < 0 || timeTableRequestDto.getClassEndTime() > 8) {
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "class_end_time");
        } else if (timeTableRequestDto.getClassGrade() == null || timeTableRequestDto.getClassGrade() < 1 || timeTableRequestDto.getClassGrade() > 4) {
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "class_grade");
        } else if (timeTableRequestDto.getTeacherName() == null || timeTableRequestDto.getTeacherName().isBlank()) {
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "teacher_name");
        } else if (timeTableRequestDto.getScore() == null || timeTableRequestDto.getScore() < 0 || timeTableRequestDto.getScore() > 3) {
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "score");
        } else if (timeTableRequestDto.getTitle() == null || timeTableRequestDto.getTitle().isBlank()) {
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "title");
        } else if (timeTableRequestDto.getSemester() == null || timeTableRequestDto.getSemester().isBlank()) {
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "semester");
        } else if (timeTableRequestDto.getSchool() == null || timeTableRequestDto.getSchool().isBlank()) {
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "school");
        } else {
            //모든 조건 통과 = error 없음
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


}
