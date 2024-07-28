package com.nextClass.service;

import com.nextClass.dto.ResponseDto;
import com.nextClass.dto.TimeTableDto;
import com.nextClass.dto.TimeTableReponseDto;
import com.nextClass.dto.TimeTableRequestDto;
import com.nextClass.entity.ClassDetail;
import com.nextClass.entity.Member;
import com.nextClass.entity.QTimeTable;
import com.nextClass.entity.TimeTable;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.repository.ClassDetailRepository;
import com.nextClass.repository.LoginRepository;
import com.nextClass.repository.TimeTableDetailRepository;
import com.nextClass.utils.CommonUtils;
import com.querydsl.core.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Transactional
public class TimeTableService {
    private final TimeTableDetailRepository timeTableRepository;
    private final LoginRepository loginRepository;
    private final ClassDetailRepository classDetailRepository;

    @Autowired
    public TimeTableService(
            TimeTableDetailRepository timeTableRepository,
            LoginRepository loginRepository,
            ClassDetailRepository classDetailRepository
    ){
        this.timeTableRepository = timeTableRepository;
        this.loginRepository = loginRepository;
        this. classDetailRepository = classDetailRepository;
    }

//    public ResponseDto caculateScoreOnSemester(String semester){
//        //학점 계산기
//
//    }

    //수정하기
    public ResponseDto changeTimeTableData(TimeTableRequestDto timeTableRequestDto){
        if(!CommonUtils.getMemberUuidIfAdminOrUser().equals(timeTableRepository.findTimeTableByUuid(timeTableRequestDto.getUuid()).getMember().getUuid().toString())){
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TIME_TABLE_UNAUTHORIZED.getErrorCode(), ErrorCode.TIME_TABLE_UNAUTHORIZED.getErrorDescription());
        }
        //요청받은 시간에 해당 회원이 수업이 등록되어 있는지 확인
        if(timeTableRepository.isClassExistOnSameTime(timeTableRequestDto, CommonUtils.getMemberUuidIfAdminOrUser()) != null){
            //null 값이 반환되지 않으면 error response
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.CLASS_ALREADY_EXIST_ON_SAME_TIME.getErrorCode(), ErrorCode.CLASS_ALREADY_EXIST_ON_SAME_TIME.getErrorDescription());
        }
        //class_detail의 데이터가 전부 동일할 때, 해당 class_detail의 id와 현재 수정하는 class_detail
        //if 현재 classDetail의 정보들로 가져온 classDetail의 uuid의 값과 timeTableRequetDto에서 받아온 값이 다르면
        ClassDetail newClassDetailUuid = timeTableRepository.checkClassDetailAlreadyExist(timeTableRequestDto);
        if(newClassDetailUuid ==null){
            //classDetail은 새로 만들고, 해당 데이터를 넣어서 timeTable update
            ClassDetail newClassDetail = ClassDetail.builder()
                    .school(timeTableRequestDto.getSchool())
                    .classGrade(timeTableRequestDto.getClass_grade())
                    .score(timeTableRequestDto.getScore())
                    .title(timeTableRequestDto.getTitle())
                    .teacherName(timeTableRequestDto.getTeacher_name())
                    .build();
            TimeTable timeTable = TimeTable.builder()
                    .uuid(UUID.fromString(timeTableRequestDto.getUuid()))
                    .week(timeTableRequestDto.getWeek())
                    .classStartTime(timeTableRequestDto.getClass_start_time())
                    .classEndTime(timeTableRequestDto.getClass_end_time())
                    .member(loginRepository.getMemberByUuid(CommonUtils.getMemberUuidIfAdminOrUser()))
                    .semester(timeTableRequestDto.getSemester())
                    .classDetailUuid(newClassDetail.getUuid())
                    .classGrade(newClassDetail.getClassGrade())
                    .teacherName(newClassDetail.getTeacherName())
                    .score(newClassDetail.getScore())
                    .school(newClassDetail.getSchool())
                    .title(newClassDetail.getTitle())
                    .build();
            timeTableRepository.updateTimeTableWithNewClassDetail(newClassDetail, timeTable);
        }else{
            if(newClassDetailUuid.getUuid().toString().replace("-","").equals(timeTableRequestDto.getClass_detail_uuid())){
                //기존 classDetail과 완전히 동일하므로 timeTable의 내용만 update하면 됨
                TimeTable timeTable = TimeTable.builder()
                        .uuid(convertToUUID(timeTableRequestDto.getClass_detail_uuid()))
                        .week(timeTableRequestDto.getWeek())
                        .classStartTime(timeTableRequestDto.getClass_start_time())
                        .classEndTime(timeTableRequestDto.getClass_end_time())
                        .member(loginRepository.getMemberByUuid(CommonUtils.getMemberUuidIfAdminOrUser()))
                        .semester(timeTableRequestDto.getSemester())
                        .classDetailUuid(newClassDetailUuid.getUuid())
                        .classGrade(newClassDetailUuid.getClassGrade())
                        .teacherName(newClassDetailUuid.getTeacherName())
                        .score(newClassDetailUuid.getScore())
                        .school(newClassDetailUuid.getSchool())
                        .title(newClassDetailUuid.getTitle())
                        .build();
                timeTableRepository.updateTimeTable(timeTable);
            }
            else{
                //현재 timeTable의 classDetail을 해당 classDetail의 uuid값으로 바꿔서 update
                TimeTable timeTable = TimeTable.builder()
                        .week(timeTableRequestDto.getWeek())
                        .classStartTime(timeTableRequestDto.getClass_start_time())
                        .classEndTime(timeTableRequestDto.getClass_end_time())
                        .member(loginRepository.getMemberByUuid(CommonUtils.getMemberUuidIfAdminOrUser()))
                        .semester(timeTableRequestDto.getSemester())
                        .classDetailUuid(newClassDetailUuid.getUuid())
                        .classGrade(newClassDetailUuid.getClassGrade())
                        .teacherName(newClassDetailUuid.getTeacherName())
                        .score(newClassDetailUuid.getScore())
                        .school(newClassDetailUuid.getSchool())
                        .build();
                timeTableRepository.updateTimeTable(timeTable);
            }
        }
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto deleteOneTimeTable(TimeTableRequestDto timeTableRequestDto){
        if(timeTableRequestDto.getUuid() == null || timeTableRequestDto.getUuid().isBlank()){
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "timeTableUuid"));
        }
        TimeTableDto timeTableDto = new TimeTableDto(timeTableRequestDto.getUuid(), CommonUtils.getMemberUuidIfAdminOrUser());
        if(timeTableRepository.findTimeTableByUuid(timeTableDto.getTimeTableUuid()) == null){
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.DATA_ALREADY_DELETED.getErrorCode(), ErrorCode.DATA_ALREADY_DELETED.getErrorDescription());
        }
        if(timeTableRepository.checkCurrentUserIsOwnerOfTimeTable(timeTableDto) == null){
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.TIME_TABLE_UNAUTHORIZED.getErrorCode(), ErrorCode.TIME_TABLE_UNAUTHORIZED.getErrorDescription());
        }
        //같은 그거 이므로 동일한 데이터만 삭제한다
        long howManyDelete = timeTableRepository.deleteTimeTable(timeTableRequestDto.getUuid());
        log.info("TimeTable Delete One");
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto deleteAllTimeTableOnSemester(TimeTableRequestDto  timeTableRequestDto ){
        if(timeTableRequestDto.getSemester() == null || timeTableRequestDto.getSemester().isBlank()){
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL,ErrorCode.PARAMETER_INVALID_GENERAL.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "semester"));
        }
        //member와 semester 2개를 받아서 삭제
        TimeTableDto timeTableDto = new TimeTableDto(CommonUtils.getMemberUuidIfAdminOrUser(), timeTableRequestDto);
        //repository에서 해당 학기 데이터 삭제
        //false되면 삭제 실패했다고 보내기
        List<Tuple> timeTableList = timeTableRepository.getTimeTableListOnSemesterFromUser(timeTableDto);
        List<String> timeTableUuidList = timeTableList.stream()
                .map(t -> t.get(0, UUID.class).toString().replace("-", ""))
                .toList();
        //삭제
        long howManyDelete = timeTableRepository.deleteAllTimeTableSelected(timeTableUuidList);
        log.info("TimeTable Delete Semester : ", howManyDelete);
        //classDetail 검증 및 삭제
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto getPersonalThisSemesterTimeTable(TimeTableRequestDto timeTableRequestDto){
        System.out.println("getTimeTable service");
        if(timeTableRequestDto.getSemester() == null || timeTableRequestDto.getSemester().isBlank()){
            String errorMsg = String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "semester");
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL,ErrorCode.PARAMETER_INVALID_GENERAL.getErrorCode(), errorMsg);
        }
        TimeTableDto timeTableDto = new TimeTableDto(CommonUtils.getMemberUuidIfAdminOrUser(), timeTableRequestDto);
        //member와 semester를 가지고 해당 데이터 가져오기(현재는 semester만)
        List<TimeTableReponseDto> timeTableList = timeTableRepository.getTimeTableListOnSemesterFromUser(timeTableDto).stream().map(this::convertTupleToDto).collect(Collectors.toList());
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS, timeTableList);
    }

    public ResponseDto makeTimeTable(TimeTableRequestDto timeTableRequestDto){
        //DTO의 값이 비어 있으면 해당 값 비어 있다는 error를 담아서 responseDTO return, for문을 통해서 진행
        String errorDescription = checkTimeTableRequest(timeTableRequestDto);
        log.info("Check TimeTableReqeustDto");
        if(errorDescription != null){
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), errorDescription);
        }
        TimeTableDto timeTableDto = new TimeTableDto(CommonUtils.getMemberUuidIfAdminOrUser(), timeTableRequestDto);
        //동일한 classDetail이 존재하는지 확인
        ClassDetail classDetail = timeTableRepository.checkClassDetailAlreadyExist(timeTableDto.getTimeTableRequestDto());
        if(classDetail == null ){
            //새로 저장
            classDetail = timeTableRepository.saveClassDetail(timeTableDto.getTimeTableRequestDto());
        }
        //member 추가하면 현재 추가한 데이터 같이 넣어주기
        timeTableDto.addAditionalInfo(classDetail.getUuid().toString().replace("-",""));
        TimeTable isDataSaved = timeTableRepository.findTimeTable(timeTableDto);
        if(isDataSaved != null){
            //해당 수업이 이미 저장되어 있음
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.DATA_ALREADY_EXIST.getErrorCode(), ErrorCode.DATA_ALREADY_EXIST.getErrorDescription());
        }
        Member member = loginRepository.getMemberByUuid(timeTableDto.getMemberUUID());
        TimeTable timeTable =  TimeTable.builder()
                .member(member)
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
                .build();
        TimeTable test = timeTableRepository.saveTimeTable(timeTable);
        log.info("내용 : ", test);
        return new ResponseDto<>(HttpStatus.ACCEPTED.value(), Description.SUCCESS);
    }


    private String checkTimeTableRequest(TimeTableRequestDto timeTableRequestDto){
        if(timeTableRequestDto.getWeek() == null || timeTableRequestDto.getWeek().isBlank()){
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(),"week");
        }
        else if(timeTableRequestDto.getClass_start_time() == null || timeTableRequestDto.getClass_start_time()<0 || timeTableRequestDto.getClass_start_time() > 8){
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(),"class_start_time");
        }
        else if(timeTableRequestDto.getClass_end_time() == null || timeTableRequestDto.getClass_end_time()<0 || timeTableRequestDto.getClass_end_time() > 8){
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(),"class_end_time");
        }
        else if(timeTableRequestDto.getClass_grade() == null || timeTableRequestDto.getClass_grade()<1 || timeTableRequestDto.getClass_grade() > 4){
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "class_grade");
        }
        else if(timeTableRequestDto.getTeacher_name() == null || timeTableRequestDto.getTeacher_name().isBlank()){
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "teacher_name");
        }
        else if(timeTableRequestDto.getScore() == null || timeTableRequestDto.getScore() < 0 || timeTableRequestDto.getScore()>3){
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "score");
        }
        else if(timeTableRequestDto.getTitle() == null || timeTableRequestDto.getTitle().isBlank()){
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "title");
        }
        else if(timeTableRequestDto.getSemester() == null || timeTableRequestDto.getSemester().isBlank()){
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "semester");
        }
        else if(timeTableRequestDto.getSchool() == null || timeTableRequestDto.getSchool().isBlank()){
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "school");
        }
        else{
            //모든 조건 통과 = error 없음
            return null;
        }
    }

    private UUID convertToUUID(String uuidString){
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
