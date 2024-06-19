package com.nextClass.repository;

import com.nextClass.dto.TimeTableRequestDto;
import com.nextClass.entity.ClassDetail;
import com.nextClass.entity.TimeTable;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.nextClass.entity.QClassDetail.classDetail;

@Repository
public class TimeTableDetailRepository {
    private final JPAQueryFactory queryFactory;
    private final TimeTableRepository timeTableRepository;
    private final ClassDetailRepository classDetailRepository;

    TimeTableDetailRepository(
            TimeTableRepository timeTableRepository,
            ClassDetailRepository classDetailRepository,
            JPAQueryFactory queryFactory) {
        this.classDetailRepository = classDetailRepository;
        this.timeTableRepository = timeTableRepository;
        this.queryFactory = queryFactory;
    }
    @Transactional
    public boolean saveClassDetailAndTimeTable(TimeTableRequestDto timeTableRequestDto) {
        ClassDetail classDetail = ClassDetail.builder()
                .title(timeTableRequestDto.getTitle())
                .classGrade(timeTableRequestDto.getClass_grade())
                .teacherName(timeTableRequestDto.getTeacher_name())
                .score(timeTableRequestDto.getScore())
                .build();
        Optional<ClassDetail> checkClassDetailDataAlreadyExist = classDetailRepository.findByTitleAndClassGradeAndTeacherNameAndScore(
                classDetail.getTitle(), classDetail.getClassGrade(), classDetail.getTeacherName(), classDetail.getScore());
        if (checkClassDetailDataAlreadyExist.isEmpty()) {
            //데이터가 존재하지 않으면 해당 데이터를 저장
            classDetailRepository.saveAndFlush(classDetail);
        }
        else if(checkClassDetailDataAlreadyExist.isPresent()){
            //이미 존재하면 해당 데이터를 사용하지 않고, classDetail의 uuid를
            classDetail = checkClassDetailDataAlreadyExist.get();
        }
        Optional<ClassDetail> ff = classDetailRepository.findById(classDetail.getUuid());
        TimeTable timeTable = TimeTable.builder()
//                    .member(UUID.randomUUID()) 나중에 member 나오면 추가하기
                .classTime(timeTableRequestDto.getClass_time())
                .classDetail(classDetail)
                .week(timeTableRequestDto.getWeek())
                .build();
        //완전 똑같은 부분이 있으면 에러가 발생해야됨
        Optional<TimeTable> checkTimeTableDataAlreadyExist
                = timeTableRepository.findByClassDetailAndClassTimeAndWeek(
                        timeTable.getClassDetail(), timeTable.getClassTime(),  timeTable.getWeek()
        );
        if(checkTimeTableDataAlreadyExist.isEmpty()) {
            timeTableRepository.save(timeTable);
            return true;
        } else {
            //동일한 데이터가 이미 저장되어 있음
            return false;
        }
    }
}
