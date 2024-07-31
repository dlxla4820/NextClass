package com.nextClass.service;

import com.nextClass.dto.ResponseDto;
import com.nextClass.dto.ScoreRequestDto;
import com.nextClass.entity.Score;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.repository.ScoreDetailRepository;
import com.nextClass.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ScoreService {
    private ScoreDetailRepository scoreRepository;

    public ScoreService(ScoreDetailRepository scoreRepository){
        this.scoreRepository = scoreRepository;
    }

    public ResponseDto<?> getAllScore(){
        log.info("ScoreService << getAllScore >>");
        //현재 로그인한 유저의 저장된 semester 전부 가져오기
        List<String> currentMemberScoreSemester = scoreRepository.findSemesterList(CommonUtils.getMemberUuidIfAdminOrUser());
        //학기 내에서 학점 계산(for문)
        //학기별 평점, 과목별 학점 리스트로 저장
        //학기별 평점 저장(더하기)

        //전체 학기 평점 계산
        //ScoreRepsonseDto에 내용담고 return

    }

    public ResponseDto<?> addScoreOnSemester(List<ScoreRequestDto> scoreRequestDto){
        log.info("ScoreService << addScoreOnSemester >> | requestBody : {}", scoreRequestDto);
        //대한이가 request body에 대해서 전체적으로 관리해주는거 만들었다고 했었던거 다시 물어보기

        //list 안에 있는 모든 객체에 대해서 행동
        for(ScoreRequestDto scoreInfo : scoreRequestDto){
            //동일 제목, 학점, 학기, 멤버를가지고 있는 수업이 해당 테이블에 존재하는지 확인
            //있으면 해당 값의 점수만 수정해서 저장
            Score scoreDuplicateCheck = scoreRepository.scoreDuplicateCheck(scoreInfo, CommonUtils.getMemberUuidIfAdminOrUser());
            Score score;
            if(scoreDuplicateCheck.equals(null)){
                score = Score.builder()
                        .title(scoreInfo.getTitle())
                        .memberUuid(convertToUUID(CommonUtils.getMemberUuidIfAdminOrUser()))
                        .semester(scoreInfo.getSemester())
                        .score(scoreInfo.getScore())
                        .studentScore(scoreInfo.getMember_score())
                        .build();
            }
            else{
                score = Score.builder()
                        .title(scoreDuplicateCheck.getTitle())
                        .memberUuid(scoreDuplicateCheck.getMemberUuid())
                        .semester(scoreDuplicateCheck.getSemester())
                        .score(scoreDuplicateCheck.getScore())
                        .studentScore(scoreInfo.getMember_score())
                        .build();
            }
            try{
                scoreRepository.saveScore(score);
            }
            catch(DataAccessException ex){
                log.error("ScoreService << addScoreOnSemester >> | DataAccessException ex : {}", ex.getMessage(), ex);
                return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.SCORE_SAVE_FAIL.getErrorCode(), ErrorCode.SCORE_SAVE_FAIL.getErrorDescription());
            }
        }
        log.info("ScoreService << addScoreOnSemester >> | save complete");
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    private UUID convertToUUID(String uuidString) {
        String formattedUuidString = uuidString.replaceAll(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{12})",
                "$1-$2-$3-$4-$5"
        );
        return UUID.fromString(formattedUuidString);
    }

}
