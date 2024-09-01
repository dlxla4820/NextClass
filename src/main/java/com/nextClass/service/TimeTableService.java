package com.nextClass.service;

import com.nextClass.dto.*;
import com.nextClass.entity.*;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.repository.ClassDetailRepository;
import com.nextClass.repository.LoginRepository;
import com.nextClass.repository.ScoreDetailRepository;
import com.nextClass.repository.TimeTableDetailRepository;
import com.nextClass.utils.CommonUtils;
import com.querydsl.core.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.nextClass.entity.QTimeTable.timeTable;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class TimeTableService {
    private final TimeTableDetailRepository timeTableRepository;
    private final LoginRepository loginRepository;
    private final ClassDetailRepository classDetailRepository;

    private final ScoreDetailRepository scoreRepository;

    @Autowired
    public TimeTableService(
            TimeTableDetailRepository timeTableRepository,
            LoginRepository loginRepository,
            ClassDetailRepository classDetailRepository,
            ScoreDetailRepository scoreRepository
    ) {
        this.timeTableRepository = timeTableRepository;
        this.loginRepository = loginRepository;
        this.classDetailRepository = classDetailRepository;
        this.scoreRepository = scoreRepository;
    }

    //수정하기
    public ResponseDto<?> changeTimeTableData(TimeTableRequestDto timeTableRequestDto) {
        log.info("TimeTableService << changeTimeTableData >> | requestBody : {}", timeTableRequestDto);
        //해당 부분에 uuid값의 검증도 추가해야 함
        String errorDescription = checkTimeTableRequest(timeTableRequestDto);
        if (errorDescription != null) {
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), errorDescription);
        }
        if (!CommonUtils.getMemberUuidIfAdminOrUser().equals(timeTableRepository.findTimeTableByUuid(timeTableRequestDto.getUuid()).getMemberUuid().toString())) {
            log.error("TimeTableService << changeTimeTableData >> | TIME_TABLE_UNAUTHORIZED");
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TIME_TABLE_UNAUTHORIZED.getErrorCode(), ErrorCode.TIME_TABLE_UNAUTHORIZED.getErrorDescription());
        }
        //요청받은 시간에 해당 회원이 수업이 등록되어 있는지 확인
        if (timeTableRepository.isClassExistOnSameTime(timeTableRequestDto, CommonUtils.getMemberUuidIfAdminOrUser()) != null) {
            log.error("TimeTableService << changeTimeTableData >> | CLASS_ALREADY_EXIST_ON_SAME_TIME");
            //null 값이 반환되지 않으면 error response
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.CLASS_ALREADY_EXIST_ON_SAME_TIME.getErrorCode(), ErrorCode.CLASS_ALREADY_EXIST_ON_SAME_TIME.getErrorDescription());
        }
        //class_detail의 데이터가 전부 동일할 때, 해당 class_detail의 id와 현재 수정하는 class_detail
        //if 현재 classDetail의 정보들로 가져온 classDetail의 uuid의 값과 timeTableRequetDto에서 받아온 값이 다르면
        ClassDetail newClassDetailUuid = timeTableRepository.checkClassDetailAlreadyExist(timeTableRequestDto);
        if (newClassDetailUuid == null) {
            //classDetail은 새로 만들고, 해당 데이터를 넣어서 timeTable update
            ClassDetail newClassDetail = ClassDetail.builder()
                    .school(timeTableRequestDto.getSchool())
                    .classGrade(timeTableRequestDto.getClass_grade())
                    .score(timeTableRequestDto.getScore())
                    .title(timeTableRequestDto.getTitle())
                    .category(timeTableRequestDto.getCategory())
                    .teacherName(timeTableRequestDto.getTeacher_name())
                    .build();
            TimeTable timeTable = TimeTable.builder()
                    .uuid(UUID.fromString(timeTableRequestDto.getUuid()))
                    .week(timeTableRequestDto.getWeek())
                    .classStartTime(timeTableRequestDto.getClass_start_time())
                    .classEndTime(timeTableRequestDto.getClass_end_time())
                    .memberUuid(convertToUUID(CommonUtils.getMemberUuidIfAdminOrUser()))
                    .semester(timeTableRequestDto.getSemester())
                    .classDetailUuid(newClassDetail.getUuid())
                    .classGrade(newClassDetail.getClassGrade())
                    .category(newClassDetail.getCategory())
                    .teacherName(newClassDetail.getTeacherName())
                    .score(newClassDetail.getScore())
                    .school(newClassDetail.getSchool())
                    .title(newClassDetail.getTitle())
                    .build();
            timeTableRepository.updateTimeTableWithNewClassDetail(newClassDetail, timeTable);
            log.info("TimeTableService << changeTimeTableData >> | timeTable : {}", timeTable);
        } else {
            if (newClassDetailUuid.getUuid().toString().replace("-", "").equals(timeTableRequestDto.getClass_detail_uuid())) {
                //기존 classDetail과 완전히 동일하므로 timeTable의 내용만 update하면 됨
                TimeTable timeTable = TimeTable.builder()
                        .uuid(convertToUUID(timeTableRequestDto.getClass_detail_uuid()))
                        .week(timeTableRequestDto.getWeek())
                        .classStartTime(timeTableRequestDto.getClass_start_time())
                        .classEndTime(timeTableRequestDto.getClass_end_time())
                        .memberUuid(convertToUUID(CommonUtils.getMemberUuidIfAdminOrUser()))
                        .semester(timeTableRequestDto.getSemester())
                        .classDetailUuid(newClassDetailUuid.getUuid())
                        .classGrade(newClassDetailUuid.getClassGrade())
                        .teacherName(newClassDetailUuid.getTeacherName())
                        .score(newClassDetailUuid.getScore())
                        .school(newClassDetailUuid.getSchool())
                        .title(newClassDetailUuid.getTitle())
                        .category(newClassDetailUuid.getCategory())
                        .build();
                timeTableRepository.updateTimeTable(timeTable);
                log.info("TimeTableService << changeTimeTableData >> | timeTable : {}", timeTable);
            } else {
                //현재 timeTable의 classDetail을 해당 classDetail의 uuid값으로 바꿔서 update
                TimeTable timeTable = TimeTable.builder()
                        .week(timeTableRequestDto.getWeek())
                        .classStartTime(timeTableRequestDto.getClass_start_time())
                        .classEndTime(timeTableRequestDto.getClass_end_time())
                        .memberUuid(convertToUUID(CommonUtils.getMemberUuidIfAdminOrUser()))
                        .semester(timeTableRequestDto.getSemester())
                        .classDetailUuid(newClassDetailUuid.getUuid())
                        .classGrade(newClassDetailUuid.getClassGrade())
                        .teacherName(newClassDetailUuid.getTeacherName())
                        .score(newClassDetailUuid.getScore())
                        .school(newClassDetailUuid.getSchool())
                        .category(newClassDetailUuid.getCategory())
                        .build();
                timeTableRepository.updateTimeTable(timeTable);
                log.info("TimeTableService << changeTimeTableData >> | timeTable : {}", timeTable);
            }
        }
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }



    public ResponseDto deleteOneTimeTable(TimeTableRequestDto timeTableRequestDto) {
        log.info("TimeTableService << deleteOneTimeTable >> | requestBody : {}", timeTableRequestDto);
        if (timeTableRequestDto.getUuid() == null || timeTableRequestDto.getUuid().isBlank()) {
            log.error("TimeTableService << deleteOneTimeTable >> | PARAMETER_INVALID_SPECIFIC");
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "timeTableUuid"));
        }
        TimeTableDto timeTableDto = new TimeTableDto(timeTableRequestDto.getUuid(), CommonUtils.getMemberUuidIfAdminOrUser());
        if (timeTableRepository.findTimeTableByUuid(timeTableDto.getTimeTableUuid()) == null) {
            log.error("TimeTableService << deleteOneTimeTable >> | DATA_ALREADY_DELETED");
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.DATA_ALREADY_DELETED.getErrorCode(), ErrorCode.DATA_ALREADY_DELETED.getErrorDescription());
        }
        if (timeTableRepository.checkCurrentUserIsOwnerOfTimeTable(timeTableDto) == null) {
            log.error("TimeTableService << deleteOneTimeTable >> | TIME_TABLE_UNAUTHORIZED");
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TIME_TABLE_UNAUTHORIZED.getErrorCode(), ErrorCode.TIME_TABLE_UNAUTHORIZED.getErrorDescription());
        }
            long howManyDelete = timeTableRepository.deleteTimeTable(timeTableRequestDto.getUuid());
            log.info("TimeTableService << deleteOneTimeTable >> | howManyDelete : {}", howManyDelete);

        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto deleteAllTimeTableOnSemester(TimeTableRequestDto timeTableRequestDto) {
        log.info("TimeTableService << deleteAllTimeTableOnSemester >> | requestBody : {}", timeTableRequestDto);
        if (timeTableRequestDto.getSemester() == null || timeTableRequestDto.getSemester().isBlank()) {
            log.error("TimeTableService << deleteAllTimeTableOnSemester >> | PARAMETER_INVALID_SPECIFIC");
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_GENERAL.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "semester"));
        }
        //member와 semester 2개를 받아서 삭제
        TimeTableDto timeTableDto = new TimeTableDto(CommonUtils.getMemberUuidIfAdminOrUser(), timeTableRequestDto);
        if (timeTableRepository.checkCurrentUserIsOwnerOfTimeTable(timeTableDto) == null) {
            log.error("TimeTableService << deleteAllTimeTableOnSemester >> | TIME_TABLE_UNAUTHORIZED");
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TIME_TABLE_UNAUTHORIZED.getErrorCode(), ErrorCode.TIME_TABLE_UNAUTHORIZED.getErrorDescription());
        }
        //repository에서 해당 학기 데이터 삭제
        //false되면 삭제 실패했다고 보내기
        List<Tuple> timeTableList = timeTableRepository.getTimeTableListOnSemesterFromUser(timeTableDto);
        List<String> timeTableUuidList = timeTableList.stream()
                .map(t -> t.get(0, UUID.class).toString().replace("-", ""))
                .toList();
        //삭제
        long howManyDelete = timeTableRepository.deleteAllTimeTableSelected(timeTableUuidList);
        log.info("TimeTableService << deleteAllTimeTableOnSemester >> | howManyDelete : {}", howManyDelete);
        //classDetail 검증 및 삭제
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto getPersonalThisSemesterTimeTable(TimeTableRequestDto timeTableRequestDto) {
        log.info("TimeTableService << getPersonalThisSemesterTimeTable >> | requestBody : {}", timeTableRequestDto);
        if (timeTableRequestDto.getSemester() == null || timeTableRequestDto.getSemester().isBlank()) {
            log.error("TimeTableService << getPersonalThisSemesterTimeTable >> | PARAMETER_INVALID_SPECIFIC");
            String errorMsg = String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "semester");
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_GENERAL.getErrorCode(), errorMsg);
        }
        TimeTableDto timeTableDto = new TimeTableDto(CommonUtils.getMemberUuidIfAdminOrUser(), timeTableRequestDto);
        //member와 semester를 가지고 해당 데이터 가져오기(현재는 semester만)
        List<TimeTableReponseDto> timeTableList = timeTableRepository.getTimeTableListOnSemesterFromUser(timeTableDto).stream().map(this::convertTupleToDto).collect(Collectors.toList());
        log.info("TimeTableService << getPersonalThisSemesterTimeTable >> | timeTableList : {}", timeTableList);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS, timeTableList);
    }

    public ResponseDto makeTimeTable(TimeTableRequestDto timeTableRequestDto) {
        log.info("TimeTableService << makeTimeTable >> | requestBody : {}", timeTableRequestDto);
        //DTO의 값이 비어 있으면 해당 값 비어 있다는 error를 담아서 responseDTO return, for문을 통해서 진행
        String errorDescription = checkTimeTableRequest(timeTableRequestDto);
        if (errorDescription != null) {
            log.error("TimeTableService << makeTimeTable >> | PARAMETER_INVALID_SPECIFIC");
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), errorDescription);
        }
        //요청받은 시간에 해당 회원이 수업이 등록되어 있는지 확인
        if (timeTableRepository.isClassExistOnSameTime(timeTableRequestDto, CommonUtils.getMemberUuidIfAdminOrUser()) != null) {
            log.error("TimeTableService << makeTimeTable >> | CLASS_ALREADY_EXIST_ON_SAME_TIME");
            //null 값이 반환되지 않으면 error response
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.CLASS_ALREADY_EXIST_ON_SAME_TIME.getErrorCode(), ErrorCode.CLASS_ALREADY_EXIST_ON_SAME_TIME.getErrorDescription());
        }
        TimeTableDto timeTableDto = new TimeTableDto(CommonUtils.getMemberUuidIfAdminOrUser(), timeTableRequestDto);
        //동일한 classDetail이 존재하는지 확인
        ClassDetail classDetail = timeTableRepository.checkClassDetailAlreadyExist(timeTableDto.getTimeTableRequestDto());
        if (classDetail == null) {
            //새로 저장
            classDetail = timeTableRepository.saveClassDetail(timeTableDto.getTimeTableRequestDto());
        }
        log.info("TimeTableService << makeTimeTable >> | classDetail : {}", classDetail);
        //member 추가하면 현재 추가한 데이터 같이 넣어주기
        timeTableDto.addAditionalInfo(classDetail.getUuid().toString().replace("-", ""));
        TimeTable isDataSaved = timeTableRepository.findTimeTable(timeTableDto);
        if (isDataSaved != null) {
            log.error("TimeTableService << makeTimeTable >> | DATA_ALREADY_EXIST");
            //해당 수업이 이미 저장되어 있음
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.DATA_ALREADY_EXIST.getErrorCode(), ErrorCode.DATA_ALREADY_EXIST.getErrorDescription());
        }
        TimeTable timeTable = TimeTable.builder()
                .memberUuid(convertToUUID(timeTableDto.getMemberUUID()))
                .week(timeTableDto.getTimeTableRequestDto().getWeek())
                .semester(timeTableDto.getTimeTableRequestDto().getSemester())
                .classStartTime(timeTableDto.getTimeTableRequestDto().getClass_start_time())
                .classEndTime(timeTableDto.getTimeTableRequestDto().getClass_end_time())
                .classDetailUuid(classDetail.getUuid())
                .title(classDetail.getTitle())
                .classGrade(classDetail.getClassGrade())
                .teacherName(classDetail.getTeacherName())
                .score(classDetail.getScore())
                .school(classDetail.getSchool())
                .category(classDetail.getCategory())
                .build();
        Score score = Score.builder()
                .title(classDetail.getTitle())
                .credit(classDetail.getScore())
                .achievement("N")
                .grade(0)
                .category(classDetail.getCategory())
                .semester(timeTableRequestDto.getSemester())
                .memberUuid(convertToUUID(timeTableDto.getMemberUUID()))
                .build();
            timeTableRepository.saveTimeTable(timeTable);
            scoreRepository.saveScore(score);
        log.info("TimeTableService << makeTimeTable >> | timeTable : {}", timeTable);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }


    private String checkTimeTableRequest(TimeTableRequestDto timeTableRequestDto) {
        if (timeTableRequestDto.getWeek() == null || timeTableRequestDto.getWeek().isBlank()) {
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "week");
        } else if (timeTableRequestDto.getClass_start_time() == null || timeTableRequestDto.getClass_start_time() < 0 || timeTableRequestDto.getClass_start_time() > 8) {
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "class_start_time");
        } else if (timeTableRequestDto.getClass_end_time() == null || timeTableRequestDto.getClass_end_time() < 0 || timeTableRequestDto.getClass_end_time() > 8) {
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "class_end_time");
        } else if (timeTableRequestDto.getClass_grade() == null || timeTableRequestDto.getClass_grade() < 1 || timeTableRequestDto.getClass_grade() > 4) {
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "class_grade");
        } else if (timeTableRequestDto.getTeacher_name() == null || timeTableRequestDto.getTeacher_name().isBlank()) {
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

    private TimeTableReponseDto convertTupleToDto(Tuple tuple) {
        return new TimeTableReponseDto(
                tuple.get(timeTable.uuid),
                tuple.get(timeTable.week),
                tuple.get(timeTable.classStartTime),
                tuple.get(timeTable.classEndTime),
                tuple.get(timeTable.semester),
                tuple.get(timeTable.title),
                tuple.get(timeTable.classGrade),
                tuple.get(timeTable.teacherName),
                tuple.get(timeTable.score),
                tuple.get(timeTable.school)
        );
    }

}
