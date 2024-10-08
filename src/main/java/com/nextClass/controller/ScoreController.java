package com.nextClass.controller;

import com.nextClass.dto.ResponseDto;
import com.nextClass.dto.ScoreRequestDto;
import com.nextClass.service.ScoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScoreController {
    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService){
        this.scoreService = scoreService;
    }

    @PostMapping(value="score_update")
    public ResponseEntity<ResponseDto<?>> addScore(@RequestBody ScoreRequestDto scoreRequestDto){
            return ResponseEntity.ok(scoreService.addScoreOnSemester(scoreRequestDto));
    }

    @PostMapping(value="score")
    public ResponseEntity<ResponseDto<?>> getAllScore(){
        return ResponseEntity.ok(scoreService.getAllScore());
    }
}
