package com.nextClass.repository;

import com.nextClass.dto.ScoreRequestDto;
import com.nextClass.entity.Score;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import com.nextClass.entity.QScore;

//Score Table has a score column
import java.util.List;

import static com.nextClass.entity.QScore.score1;
import static com.nextClass.entity.QTimeTable.timeTable;

@Repository
public class ScoreDetailRepository {
    private ScoreRepository scoreRepository;
    private JPAQueryFactory queryFactory;

    ScoreDetailRepository(ScoreRepository scoreRepository, JPAQueryFactory queryFactory){
        this.scoreRepository = scoreRepository;
        this.queryFactory = queryFactory;
    }

    public Score scoreDuplicateCheck(ScoreRequestDto score, String currentUser){
        return queryFactory.selectFrom(score1)
                .where(score1.title.eq(score.getTitle()))
                .where(Expressions.stringTemplate("HEX({0})", score1.memberUuid).eq(currentUser.replace("-","")))
                .where(score1.score.eq(score.getScore()))
                .where(score1.semester.eq(score.getSemester()))
                .fetchOne();
    }

    public void saveScore(Score score){
        scoreRepository.save(score);
    }

    public List<String> findSemesterList(String currentUser){
        return queryFactory.select(score1.semester)
                .from(score1)
                .where(Expressions.stringTemplate("HEX({0})", score1.memberUuid).eq(currentUser.replace("-","")))
                .distinct()
                .fetch();
    }
}
