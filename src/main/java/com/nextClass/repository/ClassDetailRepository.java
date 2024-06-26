package com.nextClass.repository;

import com.nextClass.entity.ClassDetail;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
