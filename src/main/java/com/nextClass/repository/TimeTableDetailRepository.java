package com.nextClass.repository;

import com.nextClass.dto.TimeTableRequestDto;
import com.nextClass.entity.ClassDetail;
import com.nextClass.entity.TimeTable;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Slf4j
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
    @Transactional
    public boolean saveClassDetailAndTimeTable(TimeTableRequestDto timeTableRequestDto) {
        ClassDetail classDetail = ClassDetail.builder()
                .title(timeTableRequestDto.getTitle())
                .classGrade(timeTableRequestDto.getClass_grade())
                .teacherName(timeTableRequestDto.getTeacher_name())
                .score(timeTableRequestDto.getScore())
                .school(timeTableRequestDto.getSchool())
                .build();
        ClassDetail checkDataAlready = classDetailRepository.findByTitleAndClassGradeAndTeacherNameAndScoreAndSchool(
                timeTableRequestDto.getTitle(),
                timeTableRequestDto.getClass_grade(),
                timeTableRequestDto.getTeacher_name(),
                timeTableRequestDto.getScore(),
                timeTableRequestDto.getSchool()
        );
        if(checkDataAlready == null ){
            classDetailRepository.save(classDetail);
            classDetail = classDetailRepository.findByTitleAndClassGradeAndTeacherNameAndScoreAndSchool(
                    timeTableRequestDto.getTitle(),
                    timeTableRequestDto.getClass_grade(),
                    timeTableRequestDto.getTeacher_name(),
                    timeTableRequestDto.getScore(),
                    timeTableRequestDto.getSchool()
            );
        }else{
            classDetail = checkDataAlready;
        }
        //해결이 도저히 안되서 일단 전체 for문으로 처리
        TimeTable timeTable = null;
        List<TimeTable> timeTableList = timeTableRepository.findAll();
//        TimeTable timeTable = timeTableRepository.findByClassDetailUuidAndClassTimeAndWeekAndSemester(
//                checkDataAlready.getUuid(),
//                timeTableRequestDto.getClass_time(),
//                timeTableRequestDto.getWeek(),
//                timeTableRequestDto.getSemester());

        for(TimeTable i : timeTableList){
            if(i.getClassDetail().getUuid().equals(classDetail.getUuid())){
                if(i.getClassTime() == timeTableRequestDto.getClass_time()){
                    if(i.getWeek().equals(timeTableRequestDto.getWeek())){
                        if(i.getSemester().equals(timeTableRequestDto.getSemester())){
                            timeTable = i;
                            break;
                        }
                    }
                }
            }
        }
        if(timeTable == null){
            timeTable = TimeTable.builder()
//                    .member(UUID.randomUUID()) 나중에 member 나오면 추가하기
                    .classTime(timeTableRequestDto.getClass_time())
                    .classDetail(classDetail)
                    .week(timeTableRequestDto.getWeek())
                    .semester(timeTableRequestDto.getSemester())
                    .build();
            timeTableRepository.save(timeTable);
            return true;}
        else{
            return false;
        }
    }
}
