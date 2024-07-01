package com.nextClass.service;

import com.nextClass.dto.ResponseDto;
import com.nextClass.dto.TimeTableDto;
import com.nextClass.dto.TimeTableRequestDto;
import com.nextClass.entity.ClassDetail;
import com.nextClass.entity.Member;
import com.nextClass.entity.TimeTable;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.repository.TimeTableDetailRepository;
import com.nextClass.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class TimeTableService {
    private final TimeTableDetailRepository timeTableRepository;

    @Autowired
    public TimeTableService(TimeTableDetailRepository timeTableRepository){this.timeTableRepository = timeTableRepository;}

    public ResponseDto deleteAllTimeTableOnSemester(String semester){
        //repository에서 해당 학기 데이터 삭제
        //false되면 삭제 실패했다고 보내기
        boolean isDeleteSuccess = timeTableRepository.deleteAllTimeTableOnSemester(semester);
        if(!isDeleteSuccess){
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), Description.FAIL, ErrorCode.DATA_ALREADY_EXIST.getErrorCode(), ErrorCode.DATA_ALREADY_EXIST.getErrorDescription());
        }
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto getPersonalTimeTableAboutThatSemester(String semester){
        //해당 학기 값이 전달되지 않으면 error 전달
//        if(semester == (null)){
//        }
        //member와 semester를 가지고 해당 데이터 가져오기(현재는 semester만)
        List<TimeTable> timeTableList = timeTableRepository.getTimeTableListOnThisSemester(semester);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS, timeTableList);
    }

    public ResponseDto makeTimeTable(TimeTableRequestDto timeTableRequestDto){
        //DTO의 값이 비어 있으면 해당 값 비어 있다는 error를 담아서 responseDTO return, for문을 통해서 진행
        String errorDescription = checkTimeTableRequest(timeTableRequestDto);
        log.info("Check TimeTableReqeustDto");
        if(errorDescription != null){
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), errorDescription);
        }
        //동일한 classDetail이 존재하는지 확인
        ClassDetail classDetail = timeTableRepository.checkClassDetailAlreqdyExist(timeTableRequestDto);
        if(classDetail == null ){
            //새로 저장
            classDetail = timeTableRepository.saveClassDetail(timeTableRequestDto);
        }
        //member 추가하면 현재 추가한 데이터 같이 넣어주기
        String memberUUID = CommonUtils.getMemberUuidIfAdminOrUser();
        Member currentUser = timeTableRepository.findMember(memberUUID);
        TimeTableDto timeTableDto = new TimeTableDto(currentUser, classDetail, timeTableRequestDto);
        TimeTable isDataSaved = timeTableRepository.findTimeTable(timeTableDto);

        if(!isDataSaved){
            //해당 수업이 이미 저장되어 있음
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.DATA_ALREADY_EXIST.getErrorCode(), ErrorCode.DATA_ALREADY_EXIST.getErrorDescription());
        }
        return new ResponseDto<>(HttpStatus.ACCEPTED.value(), Description.SUCCESS);
    }


    private String checkTimeTableRequest(TimeTableRequestDto timeTableRequestDto){
        if(timeTableRequestDto.getWeek() == null || timeTableRequestDto.getWeek().isBlank()){
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(),"week");
        }
        else if(timeTableRequestDto.getClass_start_time() == null || timeTableRequestDto.getClass_start_time()<0 || timeTableRequestDto.getClass_start_time() > 8){
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(),"class_time");
        }
        else if(timeTableRequestDto.getClass_end_time() == null || timeTableRequestDto.getClass_end_time()<0 || timeTableRequestDto.getClass_end_time() > 8){
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(),"class_time");
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
