package com.nextClass.repository;

import com.nextClass.dto.TimeTableDto;
import com.nextClass.dto.TimeTableReponseDto;
import com.nextClass.dto.TimeTableRequestDto;
import com.nextClass.entity.ClassDetail;
import com.nextClass.entity.QTimeTable;
import com.nextClass.entity.TimeTable;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import static com.nextClass.entity.QTimeTable.timeTable;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Repository
public class TimeTableDetailRepository {
    private final JPAQueryFactory queryFactory;
    private final TimeTableRepository timeTableRepository;
    private final ClassDetailRepository classDetailRepository;
    private final MemberRepository memberRepository;

    TimeTableDetailRepository(
            TimeTableRepository timeTableRepository,
            ClassDetailRepository classDetailRepository,
            MemberRepository memberRepository,
            JPAQueryFactory queryFactory) {
        this.classDetailRepository = classDetailRepository;
        this.timeTableRepository = timeTableRepository;
        this.memberRepository = memberRepository;
        this.queryFactory = queryFactory;
    }

    public long deleteTimeTableAllByMemberUuid(String memberUuid){
        return queryFactory.delete(timeTable)
                .where(Expressions.stringTemplate("HEX({0})",timeTable.memberUuid).eq(memberUuid.replace("-","")))
                .execute();
    }

    public List<TimeTableReponseDto> getTimeTableListOnSemesterFromUser(TimeTableDto timeTableDto) {
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
                timeTable.category
                )
                .from(timeTable)
                .where(Expressions.stringTemplate("HEX({0})", timeTable.memberUuid).eq(timeTableDto.getMemberUUID().replace("-","")))
                .where(timeTable.semester.eq(timeTableDto.getTimeTableRequestDto().getSemester()))
                .fetch().stream().map(this::convertTupleToDto).collect(Collectors.toList());
    }

    public List<String> getTimeTableUUIDListOnSemesterFromUser(TimeTableDto timeTableDto) {
        return queryFactory.
                select(timeTable.uuid).
                from(
                        timeTable
                )
                .where(Expressions.stringTemplate("HEX({0})", timeTable.memberUuid).eq(timeTableDto.getMemberUUID().replace("-","")))
                .where(timeTable.semester.eq(timeTableDto.getTimeTableRequestDto().getSemester()))
                .fetch().stream().map(UUID::toString).collect(Collectors.toList());
    }



    public long deleteAllTimeTableSelected(List<String> timeTableUuidList) {
        //execute : 총 몇행이 sql문에 걸렸는지 알려줌
        return queryFactory.delete(timeTable).where(Expressions.stringTemplate("HEX({0})",timeTable.uuid).in(timeTableUuidList)).execute();
    }


    public ClassDetail checkClassDetailAlreadyExist(TimeTableRequestDto timeTableRequestDto) {
        return classDetailRepository.findByTitleAndClassGradeAndTeacherNameAndScoreAndSchool(
                timeTableRequestDto.getTitle(),
                timeTableRequestDto.getClass_grade(),
                timeTableRequestDto.getTeacher_name(),
                timeTableRequestDto.getScore(),
                timeTableRequestDto.getSchool()
        );
    }
    public ClassDetail saveClassDetail(TimeTableRequestDto timeTableRequestDto) {
        ClassDetail classDetail = ClassDetail.builder()
                .title(timeTableRequestDto.getTitle())
                .classGrade(timeTableRequestDto.getClass_grade())
                .teacherName(timeTableRequestDto.getTeacher_name())
                .score(timeTableRequestDto.getScore())
                .school(timeTableRequestDto.getSchool())
                .category(timeTableRequestDto.getCategory())
                .build();
        return classDetailRepository.save(classDetail);
    }
    public TimeTable findTimeTableByUuid(String uuid) {
        return queryFactory.selectFrom(timeTable)
                .where(Expressions.stringTemplate("HEX({0})", timeTable.uuid).eq(uuid.replace("-","")))
                .fetchOne();
    }

    public TimeTable findTimeTable(TimeTableDto timeTableDto) {
        return queryFactory
                .selectFrom(timeTable)
                .where(
                        timeTable.week.eq(timeTableDto.getTimeTableRequestDto().getWeek()),
                        timeTable.classStartTime.eq(timeTableDto.getTimeTableRequestDto().getClass_start_time()),
                        timeTable.classEndTime.eq(timeTableDto.getTimeTableRequestDto().getClass_end_time()),
                        timeTable.semester.eq(timeTableDto.getTimeTableRequestDto().getSemester()),
                        timeTable.title.eq(timeTableDto.getTimeTableRequestDto().getTitle()),
                        timeTable.classGrade.eq(timeTableDto.getTimeTableRequestDto().getClass_grade()),
                        timeTable.teacherName.eq(timeTableDto.getTimeTableRequestDto().getTeacher_name()),
                        timeTable.score.eq(timeTableDto.getTimeTableRequestDto().getScore()),
                        timeTable.school.eq(timeTableDto.getTimeTableRequestDto().getSchool()),
                        timeTable.category.eq(timeTableDto.getTimeTableRequestDto().getCategory())
                )
                .where(Expressions.stringTemplate("HEX({0})", timeTable.classDetailUuid).eq(timeTableDto.getClassDetailUUID().replace("-","")))
                .where(Expressions.stringTemplate("HEX({0})", timeTable.memberUuid).eq(timeTableDto.getMemberUUID().replace("-","")))
                .fetchOne();
    }
    public TimeTable saveTimeTable(TimeTable timeTable) {
        return timeTableRepository.save(timeTable);
    }

    public TimeTable checkCurrentUserIsOwnerOfTimeTable(TimeTableDto timeTableDto) {
        QTimeTable qTimeTable1 = timeTable;
        QTimeTable qTimeTable2 = new QTimeTable("timeTable2");

        return queryFactory.select(qTimeTable1)
                .from(qTimeTable1)
                .where(
                        Expressions.stringTemplate("HEX({0})", qTimeTable1.uuid).eq(timeTableDto.getTimeTableUuid().replace("-", ""))
                                .and(
                                        queryFactory.select(qTimeTable2.uuid)
                                                .from(qTimeTable2)
                                                .where(Expressions.stringTemplate("HEX({0})", qTimeTable2.memberUuid).eq(timeTableDto.getMemberUUID().replace("-", "")))
                                                .exists()
                                )
                ).fetchOne();
    }

    public long deleteTimeTable(String timeTableUuid) {
        return queryFactory.delete(timeTable)
                .where(Expressions.stringTemplate("HEX({0})", timeTable.uuid).eq(timeTableUuid.replace("-",""))).execute();

    }

    public void updateTimeTableWithNewClassDetail(ClassDetail classDetail, TimeTable timeTable) {
        classDetailRepository.save(classDetail);
        timeTableRepository.save(timeTable);
    }

    public void updateTimeTable(TimeTable timeTable){

    }

    public TimeTable isClassExistOnSameTime(TimeTableRequestDto timeTableRequestDto, String memberUuid){
        return queryFactory.selectFrom(timeTable)
                .where(
                        Expressions.stringTemplate("HEX({0})", timeTable.memberUuid).eq(memberUuid.replace("-","")),
                        timeTable.classStartTime.between(timeTableRequestDto.getClass_start_time(), timeTableRequestDto.getClass_end_time()),
                        timeTable.classEndTime.between(timeTableRequestDto.getClass_start_time(), timeTableRequestDto.getClass_end_time())
                        )
                .where(timeTable.week.eq(timeTableRequestDto.getWeek()))
                .fetchFirst();
    }
    public TimeTable isClassExistOnSameTimeUpdate(TimeTableRequestDto timeTableRequestDto, String memberUuid){
        return queryFactory.selectFrom(timeTable)
                .where(
                        Expressions.stringTemplate("HEX({0})", timeTable.memberUuid).eq(memberUuid.replace("-","")),
                        timeTable.classStartTime.between(timeTableRequestDto.getClass_start_time(), timeTableRequestDto.getClass_end_time()),
                        timeTable.classEndTime.between(timeTableRequestDto.getClass_start_time(), timeTableRequestDto.getClass_end_time())
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
                tuple.get(timeTable.category)
        );
    }

}
