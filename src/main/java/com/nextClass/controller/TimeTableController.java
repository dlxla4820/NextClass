package com.nextClass.controller;

import com.nextClass.dto.ResponseDto;
import com.nextClass.dto.TimeTableRequestDto;
import com.nextClass.service.TimeTableService;
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

    @GetMapping(value="timetable")
    public ResponseEntity<ResponseDto<?>> getTimeTable(@RequestParam String semester){
        //member_uuid를통해서 해당 member의 timetable 만을 조회
        //semester의 데이터 만을 가져오기
        return ResponseEntity.ok(timeTableService.getPersonalTimeTableAboutThatSemester(semester));
    }

    @PostMapping(value = "timetable")
    public ResponseEntity<ResponseDto<?>> postTimeTable(@RequestBody TimeTableRequestDto timeTableRequestDto){
        return ResponseEntity.ok(timeTableService.updatePersonalTimeTable(timeTableRequestDto));
    }

    @DeleteMapping(value="timetable")
    public ResponseEntity<ResponseDto<?>> deleteAllTimeTableOnThisSemester(@RequestParam String semester){
        //member와 semester 2개를 받아서 삭제
        return ResponseEntity.ok(timeTableService.deleteAllTimeTableOnSemester(semester));
    }
}
