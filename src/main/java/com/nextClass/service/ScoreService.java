package com.nextClass.service;

import com.nextClass.dto.ResponseDto;
import com.nextClass.dto.ScoreRequestDto;
import com.nextClass.dto.ScoreResponseDto;
import com.nextClass.entity.Score;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.repository.ScoreDetailRepository;
import com.nextClass.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ScoreService {
    private ScoreDetailRepository scoreRepository;

    public ScoreService(ScoreDetailRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    public ResponseDto<?> getAllScore() {
        log.info("ScoreService << getAllScore >>");
        //현재 로그인한 유저의 저장된 semester 전부 가져오기
        String currentUser = CommonUtils.getMemberUuidIfAdminOrUser();
        List<String> currentMemberScoreSemester = scoreRepository.findSemesterList(currentUser);
        //학기 내에서 학점 계산(for문)
        List<Score> semesterScore;
        List<ScoreResponseDto.SemesterDto.SubjectDto> dataList = new ArrayList<>();
        List<ScoreResponseDto.SemesterDto> semesterList = new ArrayList<>();
        Double semesterScoreSum = 0.0;
        Integer semeseterScoreCount = 0;
        Double semesterScoreSumAll = 0.0;
        Integer semesterScoreCountAll = 0;

        for (String semester : currentMemberScoreSemester) {
            semesterScore = scoreRepository.findSemesterScores(semester, currentUser);
            ScoreResponseDto.SemesterDto semesterDto = ScoreResponseDto.SemesterDto.builder()
                    .semester(semester)
                    .build();
            for (Score scoreInfo : semesterScore) {
                if (scoreInfo.getCategory().equals("창체")) {
                    continue;
                }
                ScoreResponseDto.SemesterDto.SubjectDto scoreDetail = ScoreResponseDto.SemesterDto.SubjectDto.builder()
                        .uuid(scoreInfo.getUuid())
                        .title(scoreInfo.getTitle())
                        .category(scoreInfo.getCategory())
                        .credit(scoreInfo.getCredit())
                        .achievement(scoreInfo.getAchievement())
                        .grade(scoreInfo.getGrade())
                        .build();
                if (scoreInfo.getCategory().equals("선택")) {
                    scoreDetail.setStudentScore(scoreInfo.getStudentScore());
                    //선택 과목의 경우 평균편차와 원점수, 평균 점수를 통해 등급을 유추해 낸 뒤에 해당 당급을 넣음
                }
                semesterScoreSum += scoreInfo.getGrade() * scoreInfo.getCredit();
                semeseterScoreCount += scoreInfo.getCredit();
                dataList.add(scoreDetail);
            }
            //한 학기 점수 가져오기 종료
            semesterDto.setScore(String.format("%.2f", (semesterScoreSum / semeseterScoreCount)));
            semesterDto.setDataList(dataList);
            semesterScoreSumAll += semesterScoreSum;
            semesterScoreCountAll += semeseterScoreCount;
            semesterList.add(semesterDto);
        }
        ScoreResponseDto scoreResponseDto = ScoreResponseDto.builder()
                .average_grade(String.format("%.2f", (semesterScoreSumAll / semesterScoreCountAll)))
                .credit_sum(semesterScoreCountAll)
                .semesterList(semesterList)
                .build();

        log.info("ScoreService << getAllScore >> | Complete");
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS, scoreResponseDto);
    }

    public ResponseDto<?> addScoreOnSemester(ScoreRequestDto scoreRequestDto) {
        log.info("ScoreService << addScoreOnSemester >> | requestBody : {}", scoreRequestDto);
        //대한이가 request body에 대해서 전체적으로 관리해주는거 만들었다고 했었던거 다시 물어보기
        String currentUser = CommonUtils.getMemberUuidIfAdminOrUser();
        UUID duplicateUUID;
        Score duplicateScore;
        Score score;
        //list 안에 있는 모든 객체에 대해서 행동
        for (ScoreRequestDto.ScoreInfo scoreInfo : scoreRequestDto.getData()) {
            //동일 제목, 학점, 학기, 멤버를가지고 있는 수업이 해당 테이블에 존재하는지 확인
            //있으면 해당 값의 점수만 수정해서 저장
            if (scoreInfo.getUuid() == null || scoreInfo.getUuid().isBlank()) {
                duplicateScore = scoreRepository.scoreDuplicateCheck(scoreInfo, currentUser);
                if (duplicateScore==null) {
                    duplicateUUID = UUID.randomUUID();
                }
                else{
                    duplicateUUID = duplicateScore.getUuid();
                }
            } else {
                duplicateUUID = convertToUUID(scoreInfo.getUuid());
            }
            if (scoreInfo.getCategory().equals("공통")) {
                score = Score.builder()
                        .uuid(duplicateUUID)
                        .title(scoreInfo.getTitle())
                        .credit(scoreInfo.getCredit())
                        .category(scoreInfo.getCategory())
                        .achievement(scoreInfo.getAchievement())
                        .grade(scoreInfo.getGrade())
                        .studentScore(null)
                        .averageScore(null)
                        .standardDeviation(null)
                        .semester(scoreInfo.getSemester())
                        .memberUuid(convertToUUID(CommonUtils.getMemberUuidIfAdminOrUser()))
                        .build();

            } else if (scoreInfo.getCategory().equals("선택")) {
                Integer grade = calculateGradeUsingAchievement(scoreInfo.getAverage_score(), scoreInfo.getStudent_score(), scoreInfo.getStandard_deviation());
                if (grade.equals(null)) {
                    log.error("ScoreService << getAllScore >> | INPUT_SCORE_OUT_OF_RANGE scoreInfo : {}", scoreInfo);
                    return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.INPUT_SCORE_OUT_OF_RANGE.getErrorCode(), ErrorCode.INPUT_SCORE_OUT_OF_RANGE.getErrorDescription());
                }
                score = Score.builder()
                        .uuid(duplicateUUID)
                        .title(scoreInfo.getTitle())
                        .credit(scoreInfo.getCredit())
                        .category(scoreInfo.getCategory())
                        .achievement(scoreInfo.getAchievement())
                        .grade(scoreInfo.getGrade())
                        .studentScore(scoreInfo.getStudent_score())
                        .averageScore(scoreInfo.getAverage_score())
                        .standardDeviation(scoreInfo.getStandard_deviation())
                        .semester(scoreInfo.getSemester())
                        .memberUuid(convertToUUID(CommonUtils.getMemberUuidIfAdminOrUser()))
                        .build();
            } else {
                //창체 정보 저장
                score = Score.builder()
                        .uuid(duplicateUUID)
                        .title(scoreInfo.getTitle())
                        .credit(scoreInfo.getCredit())
                        .category(scoreInfo.getCategory())
                        .achievement(scoreInfo.getAchievement())
                        .grade(scoreInfo.getGrade())
                        .studentScore(null)
                        .averageScore(null)
                        .standardDeviation(null)
                        .semester(scoreInfo.getSemester())
                        .memberUuid(convertToUUID(CommonUtils.getMemberUuidIfAdminOrUser()))
                        .build();
            }
            try {
                scoreRepository.saveScore(score);
            } catch (DataAccessException ex) {
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

    private Integer calculateGradeUsingAchievement(Double averageScore, Double studentScore, Double standardDeviation) {
        Double zScore = (studentScore - averageScore) / standardDeviation;
        if (zScore >= 1.76 && zScore <= 3.00) {
            return 1;
        } else if (zScore >= 1.23 && zScore <= 1.75) {
            return 2;
        } else if (zScore >= 0.74 && zScore <= 1.22) {
            return 3;
        } else if (zScore >= 0.26 && zScore <= 0.73) {
            return 4;
        } else if (zScore >= -0.25 && zScore <= 0.25) {
            return 5;
        } else if (zScore >= -0.73 && zScore <= -0.26) {
            return 6;
        } else if (zScore >= -1.22 && zScore <= -0.74) {
            return 7;
        } else if (zScore >= -1.75 && zScore <= -1.23) {
            return 8;
        } else if (zScore >= -3.00 && zScore <= -1.76) {
            return 9;
        }
        //조건문안에서 처리되지 않으면 범위를 벗어난 것이므로 null 값 반환
        return null;
    }

    ;

}
