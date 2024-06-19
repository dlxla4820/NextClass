package com.nextClass.controller;

import com.nextClass.dto.ResponseDto;
import com.nextClass.dto.TimeTableRequestDto;
import com.nextClass.service.TimeTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
public class TimeTableController {

    private final TimeTableService timeTableService;
    @Autowired
    public TimeTableController(TimeTableService timeTableService){this.timeTableService = timeTableService;}

    @GetMapping(value="timetable")
    public ResponseEntity<?> getTimeTable(@RequestBody String uuid){
        //member_uuid를통해서 해당 member의 timetable 만을 조회
        return ResponseEntity.ok("test");
    }

    @PostMapping(value = "timetable")
    public ResponseEntity<ResponseDto<?>> postTimeTable(@RequestBody TimeTableRequestDto timeTableRequestDto){
        return ResponseEntity.ok(timeTableService.updatePersonalTimeTable(timeTableRequestDto));
    }
}
