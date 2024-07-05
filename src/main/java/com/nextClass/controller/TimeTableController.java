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
        return ResponseEntity.ok(timeTableService.getPersonalThisSemesterTimeTable(semester));
    }

    @PostMapping(value = "timetable")
    public ResponseEntity<ResponseDto<?>> postTimeTable(@RequestBody TimeTableRequestDto timeTableRequestDto){
        return ResponseEntity.ok(timeTableService.makeTimeTable(timeTableRequestDto));
    }

    @DeleteMapping(value="timetable_semster")
    public ResponseEntity<ResponseDto<?>> deleteAllTimeTableOnThisSemester(@RequestBody String semester){
        return ResponseEntity.ok(timeTableService.deleteAllTimeTableOnSemester(semester));
    }
    @DeleteMapping(value = "timetable")
    public ResponseEntity<ResponseDto<?>> deleteOneTimeTable(@RequestBody String timeTableUuid){
        return ResponseEntity.ok(timeTableService.deleteOneTimeTable(timeTableUuid));
    }

    @PostMapping(value = "score")
    public ResponseEntity<ResponseDto<?>> calculateScore(@RequestBody String semester){
        return ResponseEntity.ok(timeTableService.caculateScoreOnSemester(semester));
    }

    @PostMapping(value = "timetable_change")
    public ResponseEntity<ResponseDto<?>> changeTimeTableData(@RequestBody TimeTableRequestDto timeTableRequestDto){
        return ResponseEntity.ok(timeTableService.)
    }
}
