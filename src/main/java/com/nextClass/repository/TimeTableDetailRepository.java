package com.nextClass.repository;

import com.nextClass.dto.TimeTableDto;
import com.nextClass.dto.TimeTableRequestDto;
import com.nextClass.entity.ClassDetail;
import com.nextClass.entity.Member;
import com.nextClass.entity.QTimeTable;
import com.nextClass.entity.TimeTable;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import static com.nextClass.entity.QTimeTable.timeTable;

import java.util.*;


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

    public List<TimeTable> getTimeTableListOnSemesterFromUser(TimeTableDto timeTableDto) {
        return queryFactory.selectFrom(timeTable)
                .where(Expressions.stringTemplate("HEX({0})", timeTable.member.uuid).eq(timeTableDto.getMemberUUID().replace("-","")))
                .where(timeTable.semester.eq(timeTableDto.getTimeTableRequestDto().getSemester()))
                .fetch();
    }

    public void deleteAllTimeTableSelected(List<String> timeTableUuidList) {
        timeTableRepository.deleteAllByUuid(timeTableUuidList);
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
                        timeTable.school.eq(timeTableDto.getTimeTableRequestDto().getSchool())
                )
                .where(Expressions.stringTemplate("HEX({0})", timeTable.classDetailUuid).eq(timeTableDto.getClassDetailUUID().replace("-","")))
                .where(Expressions.stringTemplate("HEX({0})", timeTable.member.uuid).eq(timeTableDto.getMemberUUID().replace("-","")))
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
                                                .where(Expressions.stringTemplate("HEX({0})", qTimeTable2.member.uuid).eq(timeTableDto.getMemberUUID().replace("-", "")))
                                                .exists()
                                )
                ).fetchOne();
    }

    public void deleteTimeTable(String timeTableUuid) {
        timeTableRepository.deleteTimeTable(timeTableUuid);
    }

    public void updateTimeTableWithNewClassDetail(ClassDetail classDetail, TimeTable timeTable) {
        classDetailRepository.save(classDetail);
        timeTableRepository.save(timeTable);
    }

    public void updateTimeTable(TimeTable timeTable){

    }
}
