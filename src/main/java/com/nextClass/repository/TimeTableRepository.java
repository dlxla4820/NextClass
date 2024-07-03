package com.nextClass.repository;

import com.nextClass.entity.ClassDetail;
import com.nextClass.entity.Member;
import com.nextClass.entity.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface TimeTableRepository extends JpaRepository<TimeTable, UUID> {

    //나중에 학생까지 포함해서 찾기
    List<TimeTable> findAllBySemesterAndMember_Id(String semester, String memberUuid);
    void deleteAllBySemesterIs(String semester);

    @Query("DELETE from TimeTable t where t.uuid in :timeTableUuidList")
    void deleteAllByUuid(List<String> timeTableUuidList);

    List<TimeTable> findAllByClassDetail(ClassDetail classDetail);

    @Query(value = "SELECT t FROM TimeTable t WHERE UNHEX(t.classDetail.uuid)= :classUuid AND " +
            "UNHEX(t.member.uuid) = :memberUuid AND " +
            "t.week = :week AND " +
            "t.semester = :semester AND " +
            "t.classEndTime = :endTime AND " +
            "t.classStartTime = :startTime"
            )
    TimeTable findByDetails(
            @Param("classUuid") String classUuid,
            @Param("memberUuid") String memberUuid,
            @Param("week") String week,
            @Param("semester") String semester,
            @Param("startTime") int startTime,
            @Param("endTime") int endTime
    );

    @Query(value = "SELECT COUNT(t.classDetail.uuid) FROM TimeTable t WHERE t.classDetail.uuid = (SELECT t2.classDetail.uuid FROM TimeTable t2 WHERE t2.uuid = hex(:timeTableUuid))")
    boolean countClassDetailUuid(String timeTableUuid);
}
