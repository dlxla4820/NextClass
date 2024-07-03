package com.nextClass.service;

import com.nextClass.dto.ResponseDto;
import com.nextClass.dto.TimeTableDto;
import com.nextClass.dto.TimeTableRequestDto;
import com.nextClass.entity.ClassDetail;
import com.nextClass.entity.Member;
import com.nextClass.entity.TimeTable;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.repository.LoginRepository;
import com.nextClass.repository.TimeTableDetailRepository;
import com.nextClass.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class TimeTableService {
    private final TimeTableDetailRepository timeTableRepository;
    private final LoginRepository loginRepository;

    @Autowired
    public TimeTableService(
            TimeTableDetailRepository timeTableRepository,
            LoginRepository loginRepository
    ){
        this.timeTableRepository = timeTableRepository;
        this.loginRepository = loginRepository;
    }

    public ResponseDto deleteOneTimeTable(TimeTableRequestDto timeTableRequestDto){
        String errorDescription = checkTimeTableRequest(timeTableRequestDto);
        log.info("Check TimeTableReqeustDto");
        if(errorDescription != null){
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), errorDescription);
        }
        TimeTableDto timeTableDto = new TimeTableDto(CommonUtils.getMemberUuidIfAdminOrUser(), timeTableRequestDto);
        if(timeTableRepository.countClassDetailAsFkey(timeTableRequestDto.))

    }

    public ResponseDto deleteAllTimeTableOnSemester(String  semester ){
        if(semester == null || semester.isBlank()){
            String errorMsg = String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "semester");
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL,ErrorCode.PARAMETER_INVALID_GENERAL.getErrorCode(), errorMsg);
        }
        //member와 semester 2개를 받아서 삭제
        TimeTableDto timeTableDto = new TimeTableDto(CommonUtils.getMemberUuidIfAdminOrUser(), semester);
        //repository에서 해당 학기 데이터 삭제
        //false되면 삭제 실패했다고 보내기
        List<TimeTable> timeTableList = timeTableRepository.getTimeTableListOnSemesterFromUser(timeTableDto);
        List<String> timeTableUuidList = timeTableList.stream().map(TimeTable::getUuid).map(UUID::toString).map(s-> s.replace("-","")).toList();
        List<String> classDetailId = timeTableList.stream().map(TimeTable::getClassDetail).map(ClassDetail::getUuid).distinct().map(UUID::toString).toList();
        //삭제
        timeTableRepository.deleteAllTimeTableSelected(timeTableUuidList);
        //classDetail 검증 및 삭제
        timeTableRepository.deleteAllClassDetailSelected(classDetailId);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

//    public ResponseDto deleteOneTimeTableOnSemester(TimeTableDto timeTableDto){
//        if(timeTableRepository.findTimeTableByUuid())
//        timeTableRepository.deleteAllTimeTable
//    }

    public ResponseDto getPersonalThisSemesterTimeTable(String semester){
        if(semester == null || semester.isBlank()){
            String errorMsg = String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "semester");
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL,ErrorCode.PARAMETER_INVALID_GENERAL.getErrorCode(), errorMsg);
        }
        TimeTableDto timeTableDto = new TimeTableDto(CommonUtils.getMemberUuidIfAdminOrUser(), semester);
        //member와 semester를 가지고 해당 데이터 가져오기(현재는 semester만)
        List<TimeTable> timeTableList = timeTableRepository.getTimeTableListOnSemesterFromUser(timeTableDto);
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
        timeTableDto.addClassDetailUuid(classDetail.getUuid().toString().replace("-",""));
        TimeTable isDataSaved = timeTableRepository.findTimeTable(timeTableDto);
        if(isDataSaved != null){
            //해당 수업이 이미 저장되어 있음
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.DATA_ALREADY_EXIST.getErrorCode(), ErrorCode.DATA_ALREADY_EXIST.getErrorDescription());
        }
        Member member = loginRepository.getMemberByUuid(timeTableDto.getMemberUUID());

        timeTableRepository.saveTimeTable(timeTableDto, member, classDetail);
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
}
