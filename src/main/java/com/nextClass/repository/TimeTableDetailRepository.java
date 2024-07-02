package com.nextClass.repository;

import com.nextClass.dto.TimeTableDto;
import com.nextClass.dto.TimeTableRequestDto;
import com.nextClass.entity.ClassDetail;
import com.nextClass.entity.Member;
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

    public List<TimeTable> getTimeTableListOnSemesterFromUser(TimeTableDto timeTableDto){
        return timeTableRepository.findAllBySemesterAndMember_Id(timeTableDto.getTimeTableRequestDto().getSemester(),timeTableDto.getMemberUUID());
    }

    public void deleteAllTimeTableSelected(List<String> timeTableUuidList){
        timeTableRepository.deleteAllByUuid(timeTableUuidList);
    }
    public void deleteAllClassDetailSelected(List<String> classDetailUuidList){
        classDetailRepository.DeleteAllWhichIsNotForeignKeyInTimeTable(classDetailUuidList);
    }
    public ClassDetail checkClassDetailAlreadyExist(TimeTableRequestDto timeTableRequestDto){
        return classDetailRepository.findByTitleAndClassGradeAndTeacherNameAndScoreAndSchool(
                timeTableRequestDto.getTitle(),
                timeTableRequestDto.getClass_grade(),
                timeTableRequestDto.getTeacher_name(),
                timeTableRequestDto.getScore(),
                timeTableRequestDto.getSchool()
        );
    }

    public ClassDetail saveClassDetail(TimeTableRequestDto timeTableRequestDto){
        ClassDetail classDetail = ClassDetail.builder()
                .title(timeTableRequestDto.getTitle())
                .classGrade(timeTableRequestDto.getClass_grade())
                .teacherName(timeTableRequestDto.getTeacher_name())
                .score(timeTableRequestDto.getScore())
                .school(timeTableRequestDto.getSchool())
                .build();
        return classDetailRepository.save(classDetail);
    }

    public Member findMember(String uuid){
        return queryFactory.selectFrom(member)
                .where(Expressions.stringTemplate("HEX({0})", member.uuid).eq(uuid.replace("-","")))
                .fetchOne();
    }
    public TimeTable findTimeTable(TimeTableDto timeTableDto){
        return timeTableRepository.findByDetails(
                timeTableDto.getClassDetailUUID(),
                timeTableDto.getMemberUUID(),
                timeTableDto.getTimeTableRequestDto().getWeek(),
                timeTableDto.getTimeTableRequestDto().getSemester(),
                timeTableDto.getTimeTableRequestDto().getClass_start_time(),
                timeTableDto.getTimeTableRequestDto().getClass_end_time()
        );
    }
    public TimeTable saveTimeTable(TimeTableDto timeTableDto){
        TimeTable timeTable = TimeTable.builder()
                .member(timeTableDto.getMemberUUID())
                .classDetail(timeTableDto.getClassDetailUUID())
                .build();
        timeTableRepository.save()
    }
//    public TimeTable findTimeTableByUuid(TimeTableDto timeTableDto){
//        return timeTableRepository.findById(timeTableDto.)
//    }
}
