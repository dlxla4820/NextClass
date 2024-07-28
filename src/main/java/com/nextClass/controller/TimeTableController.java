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
    public ResponseEntity<ResponseDto<?>> getTimeTable(@RequestBody TimeTableRequestDto timeTableRequestDto){
        //member_uuid를통해서 해당 member의 timetable 만을 조회
        //semester의 데이터 만을 가져오기
        return ResponseEntity.ok(timeTableService.getPersonalThisSemesterTimeTable(timeTableRequestDto));
    }

    @PostMapping(value = "timetable")
    public ResponseEntity<ResponseDto<?>> postTimeTable(@RequestBody TimeTableRequestDto timeTableRequestDto){
        //생성 시에는 student_score는 필요 없음
        return ResponseEntity.ok(timeTableService.makeTimeTable(timeTableRequestDto));
    }

    @PostMapping(value="timetable_semester_delete")
    public ResponseEntity<ResponseDto<?>> deleteAllTimeTableOnThisSemester(@RequestBody TimeTableRequestDto timeTableRequestDto){
        return ResponseEntity.ok(timeTableService.deleteAllTimeTableOnSemester(timeTableRequestDto));
    }
    @PostMapping(value = "timetable_delete")
    public ResponseEntity<ResponseDto<?>> deleteOneTimeTable(@RequestBody TimeTableRequestDto timeTableRequestDto){
        return ResponseEntity.ok(timeTableService.deleteOneTimeTable(timeTableRequestDto));
    }

    @PostMapping(value = "timetable_change")
    public ResponseEntity<ResponseDto<?>> changeTimeTableData(@RequestBody TimeTableRequestDto timeTableRequestDto){
        return ResponseEntity.ok(timeTableService.changeTimeTableData(timeTableRequestDto));
    }
}
