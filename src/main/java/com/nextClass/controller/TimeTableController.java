package com.nextClass.controller;

import com.nextClass.dto.ResponseDto;
import com.nextClass.dto.TimeTableDto;
import com.nextClass.dto.TimeTableRequestDto;
import com.nextClass.service.TimeTableService;
import com.nextClass.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class TimeTableController {

    private final TimeTableService timeTableService;
    @Autowired
    public TimeTableController(TimeTableService timeTableService){this.timeTableService = timeTableService;}

    @PostMapping(value="timetable_semester")
    public ResponseEntity<ResponseDto<?>> getTimeTable(@RequestBody String semester){
        //member_uuid를통해서 해당 member의 timetable 만을 조회
        //semester의 데이터 만을 가져오기
        TimeTableDto timeTableDto = new TimeTableDto(CommonUtils.getMemberUuidIfAdminOrUser(), semester);
        return ResponseEntity.ok(timeTableService.getPersonalThisSemesterTimeTable(timeTableDto));
    }

    @PostMapping(value = "timetable")
    public ResponseEntity<ResponseDto<?>> postTimeTable(@RequestBody TimeTableRequestDto timeTableRequestDto){
        TimeTableDto timeTableDto = new TimeTableDto(CommonUtils.getMemberUuidIfAdminOrUser(), timeTableRequestDto);
        return ResponseEntity.ok(timeTableService.makeTimeTable(timeTableDto));
    }

    @DeleteMapping(value="timetable")
    public ResponseEntity<ResponseDto<?>> deleteAllTimeTableOnThisSemester(@RequestBody String semester){
        //member와 semester 2개를 받아서 삭제
        TimeTableDto timeTableDto = new TimeTableDto(CommonUtils.getMemberUuidIfAdminOrUser(), semester);
        return ResponseEntity.ok(timeTableService.deleteAllTimeTableOnSemester(timeTableDto));
    }
}
