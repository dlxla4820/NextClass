package com.nextClass.repository;

import com.nextClass.entity.ClassDetail;
import com.nextClass.entity.Member;
import com.nextClass.entity.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface TimeTableRepository extends JpaRepository<TimeTable, UUID> {

    @Query("SELECT t FROM TimeTable t WHERE t.classDetail.uuid = :classUuid AND t.classTime = :classTime AND t.week = :week AND t.semester = :semester")
    TimeTable findByClassDetailUuidAndClassTimeAndWeekAndSemester(
            @Param("classUuid") UUID classUuid,
            @Param("classTime") int classTime,
            @Param("week") String week,
            @Param("semester") String semester);
    //나중에 학생까지 포함해서 찾기
    List<TimeTable> findAllBySemesterIs(String semester);
    void deleteAllBySemesterIs(String semester);

    List<TimeTable> findAllByClassDetail(ClassDetail classDetail);

    @Query("SELECT t from TimeTable t where left(hex(t.classDetail.uuid),32) = :classUuid and left(hex(t.member.uuid),32) = :memberUuid and t.week = :week and t.semester = :semester and t.classEndTime = :endTime and t.classStartTime = :startTime")
    TimeTable findByClassDetailUuidAndMemberUuidClassStartTimeAAndClassStartTimeAndWeekAndSemester(
            @Param("classUuid") String classUuid,
            @Param("memberUuid") String memberUuid,
            @Param("week") String week,
            @Param("semester") String semester,
            @Param("startTime") int startTime,
            @Param("endTime") int endTime
    );
}
