package com.nextClass.service;

import com.nextClass.dto.ResponseDto;
import com.nextClass.dto.ScoreRequestDto;
import com.nextClass.dto.ScoreResponseDto;
import com.nextClass.entity.Score;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.repository.ScoreDetailRepository;
import com.nextClass.utils.CommonUtils;
import com.nextClass.utils.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ScoreService {
    private final ScoreDetailRepository scoreRepository;

    private final TransactionTemplate transactionTemplate;

    public ScoreService(
            ScoreDetailRepository scoreRepository,
            TransactionTemplate transactionTemplate
    ) {
        this.transactionTemplate = transactionTemplate;
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
            dataList.clear();
            for (Score scoreInfo : semesterScore) {
                if (scoreInfo.getCategory().equals("창체")) {
                    ScoreResponseDto.SemesterDto.SubjectDto scoreDetail = ScoreResponseDto.SemesterDto.SubjectDto.builder()
                            .uuid(scoreInfo.getUuid())
                            .title(scoreInfo.getTitle())
                            .category(scoreInfo.getCategory())
                            .credit(scoreInfo.getCredit())
                            .achievement(scoreInfo.getAchievement())
                            .grade(scoreInfo.getGrade())
                            .semester(scoreInfo.getSemester())
                            .student_score(null)
                            .average_score(null)
                            .standard_deviation(null)
                            .build();
                    dataList.add(scoreDetail);
                    continue;
                }
                ScoreResponseDto.SemesterDto.SubjectDto scoreDetail = ScoreResponseDto.SemesterDto.SubjectDto.builder()
                        .uuid(scoreInfo.getUuid())
                        .title(scoreInfo.getTitle())
                        .category(scoreInfo.getCategory())
                        .credit(scoreInfo.getCredit())
                        .achievement(scoreInfo.getAchievement())
                        .grade(scoreInfo.getGrade())
                        .semester(scoreInfo.getSemester())
                        .build();
                if (scoreInfo.getCategory().equals("선택")) {
                    scoreDetail.setStudent_score(scoreInfo.getStudentScore());
                    scoreDetail.setAverage_score(scoreInfo.getAverageScore());
                    scoreDetail.setStandard_deviation(scoreInfo.getStandardDeviation());
                    //선택 과목의 경우 평균편차와 원점수, 평균 점수를 통해 등급을 유추해 낸 뒤에 해당 당급을 넣음
                }
                semesterScoreSum += scoreInfo.getGrade() * scoreInfo.getCredit();
                semeseterScoreCount += scoreInfo.getCredit();
                dataList.add(scoreDetail);
            }
            //한 학기 점수 가져오기 종료
            semesterDto.setScore(String.format("%.2f", (semesterScoreSum / semeseterScoreCount)));
            semesterDto.setData_list(dataList);
            semesterScoreSumAll += semesterScoreSum;
            semesterScoreCountAll += semeseterScoreCount;
            semesterList.add(semesterDto);
        }
        ScoreResponseDto scoreResponseDto = ScoreResponseDto.builder()
                .average_grade(String.format("%.2f", (semesterScoreSumAll / semesterScoreCountAll)))
                .credit_sum(semesterScoreCountAll)
                .semester_list(semesterList)
                .build();

        log.info("ScoreService << getAllScore >> | Complete");
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS, scoreResponseDto);
    }

    public ResponseDto<?> addScoreOnSemester(ScoreRequestDto scoreRequestDto) {
        return transactionTemplate.execute(status -> {
            log.info("ScoreService << addScoreOnSemester >> | requestBody : {}", scoreRequestDto);
            //대한이가 request body에 대해서 전체적으로 관리해주는거 만들었다고 했었던거 다시 물어보기
            String currentUser = CommonUtils.getMemberUuidIfAdminOrUser();
            UUID duplicateUUID;
            Score score;
            //db 리셋
            try {
                scoreRepository.deleteAllDataAboutCurrentUser(currentUser);
            } catch (DataAccessException ex) {
                log.error("ScoreService << addScoreOnSemester >> | DataAccessException ex : {}", ex.getMessage(), ex);
                status.setRollbackOnly();
                throw new CustomException(ErrorCode.SCORE_SAVE_FAIL);
            }
            //list 안에 있는 모든 객체에 대해서 행동
            ScoreRequestDto.ScoreInfo scoreInfo;
            Iterator<ScoreRequestDto.ScoreInfo> iteratorDataList = scoreRequestDto.getData().iterator();
            List<Score> allScore = new ArrayList<>();
            while (iteratorDataList.hasNext()) {
                //iterator를 통해서 해당 리스트를 순차적으로 하나씩 꺼냄
                scoreInfo = iteratorDataList.next();
                //동일 제목, 학점, 학기, 멤버를가지고 있는 수업이 해당 테이블에 존재하는지 확인
                //있으면 해당 값의 점수만 수정해서 저장
                if (scoreInfo.getUuid() == null || scoreInfo.getUuid().isBlank()) {
                    duplicateUUID = UUID.randomUUID();
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
                    if (grade == (null)) {
                        log.error("ScoreService << getAllScore >> | INPUT_SCORE_OUT_OF_RANGE scoreInfo : {}", scoreInfo);
                        status.setRollbackOnly();
                        throw new CustomException(ErrorCode.INPUT_SCORE_OUT_OF_RANGE);
                    } else if (grade.equals(10)) {
                        log.error("ScoreService << getAllScore >> | INPUT_SCORE_OUT_OF_RANGE scoreInfo : {}", scoreInfo);
                        status.setRollbackOnly();
                        throw new CustomException(ErrorCode.INPUT_SCORE_OUT_OF_RANGE);
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
                //해당 부분에서 score 객체를 만드는데 scoreInfo를 사용했으므로 이제 scoreInfo는 제거해도 괜찮음
                //그래서 해당 부분에서 uuid 검증을 함
                iteratorDataList.remove();
                if (scoreRequestDto.checkDuplicateList(duplicateUUID.toString())) {
                    log.error("ScoreService << addScoreOnSemester >> | Uuid Duplicate");
                    status.setRollbackOnly();
                    throw new CustomException(ErrorCode.INPUT_DUPLICATED, "uuid");
                }
                allScore.add(score);
            }
            try {
                scoreRepository.saveAll(allScore);
            } catch (DataAccessException ex) {
                log.error("ScoreService << addScoreOnSemester >> | DataAccessException ex : {}", ex.getMessage(), ex);
                status.setRollbackOnly();
                throw new CustomException(ErrorCode.SCORE_SAVE_FAIL);
            }
            log.info("ScoreService << addScoreOnSemester >> | save complete");
            return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
        });
    }

    private UUID convertToUUID(String uuidString) {
        String formattedUuidString = uuidString.replaceAll(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{12})",
                "$1-$2-$3-$4-$5"
        );
        return UUID.fromString(formattedUuidString);
    }

    private Integer calculateGradeUsingAchievement(Double averageScore, Double studentScore, Double standardDeviation) {
        double zScore;
        try{
            zScore = (studentScore - averageScore) / standardDeviation;
        }catch(Exception e){
            log.error("ScoreService << calculateGradeUsingAchievement >> | Exception e : {}", e.getMessage());
            return 10;
        }
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
        log.error("ScoreService << calculateGradeUsingAchievement >> | zScore Out Of Range");
        return null;
    }

    ;

}
