package com.nextClass.repository;

import com.nextClass.dto.ScoreRequestDto;
import com.nextClass.entity.Score;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import com.nextClass.entity.QScore;

//Score Table has a score column
import java.util.List;
import static com.nextClass.entity.QScore.score;
import static com.nextClass.entity.QTimeTable.timeTable;

@Repository
public class ScoreDetailRepository {
    private ScoreRepository scoreRepository;
    private JPAQueryFactory queryFactory;

    ScoreDetailRepository(ScoreRepository scoreRepository, JPAQueryFactory queryFactory){
        this.scoreRepository = scoreRepository;
        this.queryFactory = queryFactory;
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

    public void saveScore(Score score){
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
