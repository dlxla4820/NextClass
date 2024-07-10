package com.nextClass.repository;

import com.nextClass.dto.TimeTableDto;
import com.nextClass.dto.TimeTableRequestDto;
import com.nextClass.entity.ClassDetail;
import com.nextClass.entity.Member;
import com.nextClass.entity.QTimeTable;
import com.nextClass.entity.TimeTable;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.nextClass.entity.QMember.member;
import static com.nextClass.entity.QTimeTable.timeTable;


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
        return timeTableRepository.findAllBySemesterAndMember_Id(timeTableDto.getTimeTableRequestDto().getSemester(), timeTableDto.getMemberUUID());
    }

    public void deleteAllTimeTableSelected(List<String> timeTableUuidList) {
        timeTableRepository.deleteAllByUuid(timeTableUuidList);
    }

    public void deleteAllClassDetailSelected(List<String> classDetailUuidList) {
        classDetailRepository.DeleteAllWhichIsNotForeignKeyInTimeTable(classDetailUuidList);
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
        return timeTableRepository.findByUuid(uuid);
    }

    public TimeTable findTimeTable(TimeTableDto timeTableDto) {
        return timeTableRepository.findByDetails(
                timeTableDto.getClassDetailUUID(),
                timeTableDto.getMemberUUID(),
                timeTableDto.getTimeTableRequestDto().getWeek(),
                timeTableDto.getTimeTableRequestDto().getSemester(),
                timeTableDto.getTimeTableRequestDto().getClass_start_time(),
                timeTableDto.getTimeTableRequestDto().getClass_end_time()
        );
    }
<<<<<<< HEAD
=======

>>>>>>> 535ba5c62abe91954f7303805b6163fab3ac1278
    public TimeTable saveTimeTable(TimeTableDto timeTableDto, Member member, ClassDetail classDetail) {
        TimeTable timeTable = TimeTable.builder()
                .member(member)
                .classDetail(classDetail)
                .week(timeTableDto.getTimeTableRequestDto().getWeek())
                .semester(timeTableDto.getTimeTableRequestDto().getSemester())
                .classStartTime(timeTableDto.getTimeTableRequestDto().getClass_start_time())
                .classEndTime(timeTableDto.getTimeTableRequestDto().getClass_end_time())
                .build();
        return timeTableRepository.save(timeTable);
    }

<<<<<<< HEAD

    public int countClassDetailAsFkey(String timeTableUuid, String memberUUID){
        return timeTableRepository.countClassDetailUuid(timeTableUuid, memberUUID);
    }
    public TimeTable checkCurrentUserIsOwnerOfTimeTable(String timeTableUuid, String memberUUID){
        return timeTableRepository.checkTimeTableMemberUuid(timeTableUuid, memberUUID);
=======
    public int countClassDetailAsFkey(String timeTableUuid) {
        return timeTableRepository.countClassDetailUuid(timeTableUuid);
    }

    public TimeTable checkCurrentUserIsOwnerOfTimeTable(TimeTableDto timeTableDto) {
        QTimeTable qTimeTable1 = QTimeTable.timeTable;
        QTimeTable qTimeTable2 = new QTimeTable("timeTable2");

        return queryFactory.select(qTimeTable1)
                .from(qTimeTable1)
                .where(
                        qTimeTable1.uuid.eq(timeTableDto.getTimeTableUuid().replace("-", ""))
                                .and(
                                        queryFactory.select(qTimeTable2.uuid)
                                                .from(qTimeTable2)
                                                .where(Expressions.stringTemplate("HEX({0})", qTimeTable2.member.uuid).eq(timeTableDto.getMemberUUID().replace("-", "")))
                                                .exists()
                                )
                )
                .fetchOne();
>>>>>>> 535ba5c62abe91954f7303805b6163fab3ac1278
    }



    public void deleteTimeTableAndClassDetail(String timeTableId, String classDetailUuid) {
        timeTableRepository.deleteTimeTable(timeTableId);
        classDetailRepository.deleteClassDetail(classDetailUuid);
    }

    public void deleteTimeTable(String timeTableUuid) {
        timeTableRepository.deleteTimeTable(timeTableUuid);
    }

    public void updateTimeTableWithNewClassDetail(ClassDetail classDetail, TimeTable timeTable) {
        classDetailRepository.save(classDetail);
        timeTableRepository.save(timeTable);
    }
<<<<<<< HEAD
    public void updateTimeTable(TimeTable timeTable){
=======

    public void updateTimeTableWithOutClassDetail(TimeTable timeTable) {
>>>>>>> 535ba5c62abe91954f7303805b6163fab3ac1278
        timeTableRepository.save(timeTable);
    }

}
