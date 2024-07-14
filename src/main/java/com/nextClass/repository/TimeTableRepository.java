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


    @Query("DELETE from TimeTable t where t.uuid in :timeTableUuidList")
    void deleteAllByUuid(@Param("timeTableUuidList")List<String> timeTableUuidList);




    @Query(value= "DELETE FROM TimeTable t WHERE t.uuid = :timeTableId")
    //uuid 값만을 가지고 timetable과 classDetail2개를 삭제하는 쿼리문 작성
    void deleteTimeTable(@Param("timeTableId") String timeTableId );
}
