package com.nextClass.service;

import com.nextClass.dto.ResponseDto;
import com.nextClass.dto.ScoreRequestDto;
import com.nextClass.repository.ScoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ScoreService {
    private ScoreRepository scoreRepository;

    public ScoreService(ScoreRepository scoreRepository){
        this.scoreRepository = scoreRepository;
    }

    public ResponseDto<?> addScoreOnSemester(ScoreRequestDto scoreRequestDto){
        log.info("ScoreService << addScoreOnSemester >> | requestBody : {}", scoreRequestDto);
        if(scoreRequestDto.)
        //동일 제목, 학점, 학기를가지고 있는 수업이 해당 테이블에 존재하는지 확인
    }

}
