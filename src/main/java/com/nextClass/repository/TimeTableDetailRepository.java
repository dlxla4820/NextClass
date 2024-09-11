package com.nextClass.repository;

import com.nextClass.dto.TimeTableReponseDto;
import com.nextClass.dto.TimeTableRequestDto;
import com.nextClass.entity.QTimeTable;
import com.nextClass.entity.TimeTable;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import static com.nextClass.entity.QTimeTable.timeTable;

import java.util.*;
import java.util.stream.Collectors;



@Repository
public class TimeTableDetailRepository {
    private final JPAQueryFactory queryFactory;
    private final TimeTableRepository timeTableRepository;
    private final LoginRepository loginRepository;

    TimeTableDetailRepository(
            TimeTableRepository timeTableRepository,
            LoginRepository loginRepository,
            JPAQueryFactory queryFactory) {
        this.timeTableRepository = timeTableRepository;
        this.loginRepository = loginRepository;
        this.queryFactory = queryFactory;
    }

    public long deleteTimeTableAllByMemberUuid(String memberUuid){
        return queryFactory.delete(timeTable)
                .where(Expressions.stringTemplate("HEX({0})",timeTable.memberUuid).eq(memberUuid.replace("-","")))
                .execute();
    }

    public List<TimeTableReponseDto> getTimeTableListOnSemesterFromUser(TimeTableRequestDto timeTableRequestDto) {
        return queryFactory.select(
                timeTable.uuid,
                timeTable.week,
                timeTable.classStartTime,
                timeTable.classEndTime,
                timeTable.semester,
                timeTable.title,
                timeTable.classGrade,
                timeTable.teacherName,
                timeTable.score,
                timeTable.school,
                timeTable.category,
                timeTable.color
                )
                .from(timeTable)
                .where(Expressions.stringTemplate("HEX({0})", timeTable.memberUuid).eq(timeTableRequestDto.getMemberUuid().replace("-","")))
                .where(timeTable.semester.eq(timeTableRequestDto.getSemester()))
                .fetch().stream().map(this::convertTupleToDto).collect(Collectors.toList());
    }

    public List<String> getTimeTableUUIDListOnSemesterFromUser(TimeTableRequestDto timeTableRequestDto) {
        return queryFactory.
                select(timeTable.uuid).
                from(
                        timeTable
                )
                .where(Expressions.stringTemplate("HEX({0})", timeTable.memberUuid).eq(timeTableRequestDto.getMemberUuid().replace("-","")))
                .where(timeTable.semester.eq(timeTableRequestDto.getSemester()))
                .fetch().stream().map(UUID::toString).collect(Collectors.toList());
    }



    public long deleteAllTimeTableSelected(List<String> timeTableUuidList) {
        //execute : 총 몇행이 sql문에 걸렸는지 알려줌
        return queryFactory.delete(timeTable).where(Expressions.stringTemplate("HEX({0})",timeTable.uuid).in(timeTableUuidList)).execute();
    }

    public TimeTable findTimeTableByUuid(String uuid) {
        return queryFactory.selectFrom(timeTable)
                .where(Expressions.stringTemplate("HEX({0})", timeTable.uuid).eq(uuid.replace("-","")))
                .fetchOne();
    }

    public TimeTable findTimeTable(TimeTableRequestDto timeTableRequestDto) {
        return queryFactory
                .selectFrom(timeTable)
                .where(
                        timeTable.week.eq(timeTableRequestDto.getWeek()),
                        timeTable.classStartTime.eq(timeTableRequestDto.getClassStartTime()),
                        timeTable.classEndTime.eq(timeTableRequestDto.getClassEndTime()),
                        timeTable.semester.eq(timeTableRequestDto.getSemester()),
                        timeTable.title.eq(timeTableRequestDto.getTitle()),
                        timeTable.classGrade.eq(timeTableRequestDto.getClassGrade()),
                        timeTable.teacherName.eq(timeTableRequestDto.getTeacherName()),
                        timeTable.score.eq(timeTableRequestDto.getScore()),
                        timeTable.school.eq(timeTableRequestDto.getSchool()),
                        timeTable.category.eq(timeTableRequestDto.getCategory()),
                        timeTable.color.eq(timeTableRequestDto.getColor())
                )
                .where(Expressions.stringTemplate("HEX({0})", timeTable.memberUuid).eq(timeTableRequestDto.getMemberUuid().replace("-","")))
                .fetchOne();
    }
    public TimeTable saveTimeTable(TimeTableRequestDto timeTableRequestDto) {
        TimeTable timeTable = TimeTable.builder()
                .memberUuid(loginRepository.getMemberByUuid(timeTableRequestDto.getMemberUuid()).getUuid())
                .week(timeTableRequestDto.getWeek())
                .semester(timeTableRequestDto.getSemester())
                .classStartTime(timeTableRequestDto.getClassStartTime())
                .classEndTime(timeTableRequestDto.getClassEndTime())
                .title(timeTableRequestDto.getTitle())
                .classGrade(timeTableRequestDto.getClassGrade())
                .teacherName(timeTableRequestDto.getTeacherName())
                .score(timeTableRequestDto.getScore())
                .school(timeTableRequestDto.getSchool())
                .category(timeTableRequestDto.getCategory())
                .color(timeTableRequestDto.getColor())
                .build();
        return timeTableRepository.save(timeTable);
    }

    public TimeTable checkCurrentUserIsOwnerOfTimeTable(TimeTableRequestDto timeTableRequestDto) {
        QTimeTable qTimeTable2 = new QTimeTable("timeTable2");

        return queryFactory.select(timeTable)
                .from(timeTable)
                .where(
                        Expressions.stringTemplate("HEX({0})", timeTable.uuid).eq(timeTableRequestDto.getUuid().replace("-", ""))
                                .and(
                                        queryFactory.select(qTimeTable2.uuid)
                                                .from(qTimeTable2)
                                                .where(Expressions.stringTemplate("HEX({0})", qTimeTable2.memberUuid).eq(timeTableRequestDto.getMemberUuid().replace("-", "")))
                                                .exists()
                                )
                ).fetchOne();
    }

    public long deleteTimeTable(String timeTableUuid) {
        return queryFactory.delete(timeTable)
                .where(Expressions.stringTemplate("HEX({0})", timeTable.uuid).eq(timeTableUuid.replace("-",""))).execute();

    }

    public void updateTimeTableWithNewClassDetail(TimeTableRequestDto timeTableRequestDto) {
        TimeTable timeTable = TimeTable.builder()
                .memberUuid(loginRepository.getMemberByUuid(timeTableRequestDto.getMemberUuid()).getUuid())
                .week(timeTableRequestDto.getWeek())
                .semester(timeTableRequestDto.getSemester())
                .classStartTime(timeTableRequestDto.getClassStartTime())
                .classEndTime(timeTableRequestDto.getClassEndTime())
                .title(timeTableRequestDto.getTitle())
                .classGrade(timeTableRequestDto.getClassGrade())
                .teacherName(timeTableRequestDto.getTeacherName())
                .score(timeTableRequestDto.getScore())
                .school(timeTableRequestDto.getSchool())
                .category(timeTableRequestDto.getCategory())
                .color(timeTableRequestDto.getColor())
                .build();
        timeTableRepository.save(timeTable);
    }

    public void updateTimeTable(TimeTable timeTable){

    }

    public TimeTable isClassExistOnSameTime(TimeTableRequestDto timeTableRequestDto, String memberUuid){
        return queryFactory.selectFrom(timeTable)
                .where(
                        Expressions.stringTemplate("HEX({0})", timeTable.memberUuid).eq(memberUuid.replace("-","")),
                        timeTable.classStartTime.between(timeTableRequestDto.getClassStartTime(), timeTableRequestDto.getClassEndTime()),
                        timeTable.classEndTime.between(timeTableRequestDto.getClassStartTime(), timeTableRequestDto.getClassEndTime())
                        )
                .where(timeTable.week.eq(timeTableRequestDto.getWeek()))
                .fetchFirst();
    }
    public TimeTable isClassExistOnSameTimeUpdate(TimeTableRequestDto timeTableRequestDto, String memberUuid){
        return queryFactory.selectFrom(timeTable)
                .where(
                        Expressions.stringTemplate("HEX({0})", timeTable.memberUuid).eq(memberUuid.replace("-","")),
                        timeTable.classStartTime.between(timeTableRequestDto.getClassStartTime(), timeTableRequestDto.getClassEndTime()),
                        timeTable.classEndTime.between(timeTableRequestDto.getClassStartTime(), timeTableRequestDto.getClassEndTime())
                )
                .where(timeTable.week.eq(timeTableRequestDto.getWeek()))
                .where(Expressions.stringTemplate("HEX({0})", timeTable.uuid).ne(timeTableRequestDto.getUuid().replace("-","")))
                .fetchFirst();
    }

    private TimeTableReponseDto convertTupleToDto(Tuple tuple) {
        return new TimeTableReponseDto(
                tuple.get(timeTable.uuid),
                tuple.get(timeTable.week),
                tuple.get(timeTable.classStartTime),
                tuple.get(timeTable.classEndTime),
                tuple.get(timeTable.semester),
                tuple.get(timeTable.title),
                tuple.get(timeTable.classGrade),
                tuple.get(timeTable.teacherName),
                tuple.get(timeTable.score),
                tuple.get(timeTable.school),
                tuple.get(timeTable.category),
                tuple.get(timeTable.color)
        );
    }

}
