package com.nextClass.repository;

import com.nextClass.entity.ClassDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.*;

@Repository
public interface ClassDetailRepository extends JpaRepository<ClassDetail, UUID> {
    ClassDetail findByTitleAndClassGradeAndTeacherNameAndScoreAndSchool(
            String title,
            Integer classGrade,
            String teacherName,
            Integer score,
            String school
    );
    @Query("DELETE FROM ClassDetail c WHERE c.uuid in :uuidLists AND c.uuid NOT IN (SELECT t.uuid FROM TimeTable t WHERE t.classDetail.uuid IN :uuidLists)")
    void DeleteAllWhichIsNotForeignKeyInTimeTable(List<String> uuidLists);

    @Query(value="DELETE FROM ClassDetail c where c.uuid = :uuid")
    void deleteClassDetail(@Param("uuid") String uuid);
}
