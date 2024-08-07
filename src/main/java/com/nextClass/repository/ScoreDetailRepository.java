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

    public Score scoreDuplicateCheck(ScoreRequestDto.ScoreInfo scoreInfo, String currentUser){
        return queryFactory.selectFrom(score)
                .where(score.category.eq(scoreInfo.getCategory()))
                .where(score.title.eq(scoreInfo.getTitle()))
                .where(Expressions.stringTemplate("HEX({0})", score.memberUuid).eq(currentUser.replace("-","")))
                .where(score.semester.eq(scoreInfo.getSemester()))
                .fetchOne();
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
}
