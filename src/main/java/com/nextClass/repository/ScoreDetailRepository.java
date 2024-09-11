package com.nextClass.repository;

import com.nextClass.dto.TimeTableRequestDto;
import com.nextClass.entity.Score;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;


import java.util.List;
import static com.nextClass.entity.QScore.score;

@Repository
public class ScoreDetailRepository {
    private final ScoreRepository scoreRepository;

    private final LoginRepository loginRepository;
    private final JPAQueryFactory queryFactory;

    ScoreDetailRepository(ScoreRepository scoreRepository, JPAQueryFactory queryFactory,LoginRepository loginRepository){
        this.scoreRepository = scoreRepository;
        this.queryFactory = queryFactory;
        this.loginRepository = loginRepository;
    }

    public List<Score> findSemesterScores(String semester, String currentUser) {
        return queryFactory.selectFrom(score)
                .where(score.semester.eq(semester))
                .where(Expressions.stringTemplate("HEX({0})", score.memberUuid).eq(currentUser.replace("-", "")))
                .fetch();
    }



    public long deleteAllDataAboutCurrentUser(String currentUser){
        return queryFactory.delete(score)
                .where(Expressions.stringTemplate("HEX({0})", score.memberUuid).eq(currentUser.replace("-", "")))
                .execute();
    }

    public void saveScore(TimeTableRequestDto timeTableRequestDto){
        Score score = Score.builder()
                .title(timeTableRequestDto.getTitle())
                .credit(timeTableRequestDto.getScore())
                .achievement("N")
                .grade(0)
                .category(timeTableRequestDto.getCategory())
                .semester(timeTableRequestDto.getSemester())
                .memberUuid(loginRepository.getMemberByUuid(timeTableRequestDto.getMemberUuid()).getUuid())
                .build();
        scoreRepository.save(score);
    }

    public List<String> findSemesterList(String currentUser){
        return queryFactory.select(score.semester)
                .from(score)
                .where(Expressions.stringTemplate("HEX({0})", score.memberUuid).eq(currentUser.replace("-","")))
                .distinct()
                .fetch();
    }

    public void saveAll(List<Score> scoreList){scoreRepository.saveAll(scoreList);}
}
