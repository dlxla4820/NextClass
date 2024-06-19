package com.nextClass.service;

import com.nextClass.dto.ResponseDto;
import com.nextClass.dto.TimeTableRequestDto;
import com.nextClass.entity.ClassDetail;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.repository.TimeTableDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TimeTableService {
    private final TimeTableDetailRepository timeTableRepository;

    @Autowired
    public TimeTableService(TimeTableDetailRepository timeTableRepository){this.timeTableRepository = timeTableRepository;}

    public ResponseDto updatePersonalTimeTable(TimeTableRequestDto timeTableRequestDto){
        //DTO의 값이 비어 있으면 해당 값 비어 있다는 error를 담아서 responseDTO return, for문을 통해서 진행
        String errorDescription = checkTimeTableRequest(timeTableRequestDto);
        System.out.println("Check TimeTableReqeustDto");
        if(errorDescription != null){
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), errorDescription);
        }
        //동일한 data가 존재하는지 확인하고 저장하기
        //member 추가하면 현재 추가한 데이터 같이 넣어주기
        boolean isDataSaved = timeTableRepository.saveClassDetailAndTimeTable(timeTableRequestDto);
        //있을 경우엔 data 찾아서 진행
        //현재 로그인 한 사람으로부터 uuid 값을 가져옴
        //로그인 한 사람의 정보를 가져오지 못하면 에러 return(error를 등록하는)
        //생성된 데이터들을 더해서 time_table entity 생성
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
        else if(timeTableRequestDto.getClass_time() == null || timeTableRequestDto.getClass_time()<0 || timeTableRequestDto.getClass_time() > 8){
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
        else{
            //모든 조건 통과 = error 없음
            return null;
        }
    }
}
