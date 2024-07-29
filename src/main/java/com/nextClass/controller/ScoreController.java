package com.nextClass.controller;

import com.nextClass.dto.ResponseDto;
import com.nextClass.dto.ScoreRequestDto;
import com.nextClass.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ScoreController {
    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService){
        this.scoreService = scoreService;
    }

    @PostMapping(value="score_update")
    public ResponseEntity<ResponseDto<?>> addScore(@RequestBody ScoreRequestDto scoreRequestDto){
        return ResponseEntity.ok(scoreService.addScoreOnSemester(scoreRequestDto));
    }
    @PostMapping(value="score_all")
    public ResponseEntity<ResponseDto<?>> getAllScore(@RequestBody ScoreRequestDto scoreRequestDto){
        return ResponseEntity.ok(scoreService.getAllScore(scoreRequestDto));
    }
}
