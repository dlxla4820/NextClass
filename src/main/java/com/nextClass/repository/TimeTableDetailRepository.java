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
    public boolean deleteAllTimeTableOnSemester(String semester){
        //해당하는 학생의 해당하는 semester인 timetable list 가져오기
        List<TimeTable> timeTableList = timeTableRepository.findAllBySemesterIs(semester);
        //해당 학기에 포함되어 있는 class_detail들의 uuid를 모아서 리스트 생성
        Set<ClassDetail> classDetails = new HashSet<>();
        for(TimeTable currentTimeTable : timeTableList){
            classDetails.add(currentTimeTable.getClassDetail());
        }
        //동일한 조건으로 timetable에서 해당하는 데이터 삭제
        timeTableRepository.deleteAllBySemesterIs(semester);
        //classDetail리스트에서 timeDetail에서 FK로 참조하고 있는 데이터가 있는지 확인
        List<TimeTable> findFKOnTimeTable = new ArrayList<>();
        for(ClassDetail classDetail : classDetails){
            findFKOnTimeTable = timeTableRepository.findAllByClassDetail(classDetail);
            if(findFKOnTimeTable.size() == 0){
                //해당 하는 데이터가 없으므로 해당 ClassDetail 삭제
                classDetailRepository.deleteById(classDetail.getUuid());
            }
            //없을 경우 해당하는 데이터가 존재하므로 삭제
        }
        return true;
    }
    public List<TimeTable> getTimeTableListOnThisSemester(String semester){
        return timeTableRepository.findAllBySemesterIs(semester);
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
        return timeTableRepository.findByClassDetailUuidAndMemberUuidClassStartTimeAAndClassStartTimeAndWeekAndSemester(
                timeTableDto.getClassDetail().getUuid().toString().replace("-",""),
                timeTableDto.getMember().getUuid().toString().replace("-",""),
                timeTableDto.getTimeTableRequestDto().getWeek(),
                timeTableDto.getTimeTableRequestDto().getSemester(),
                timeTableDto.getTimeTableRequestDto().getClass_start_time(),
                timeTableDto.getTimeTableRequestDto().getClass_end_time()
        );
    }
}
