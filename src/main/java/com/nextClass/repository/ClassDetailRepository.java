package com.nextClass.repository;

import com.nextClass.entity.ClassDetail;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.*;

public interface ClassDetailRepository extends JpaRepository<ClassDetail, UUID> {
    Optional<ClassDetail> findByTitleAndClassGradeAndTeacherNameAndScore(
            String title,
            Integer classGrade,
            String teacherName,
            Integer score
    );
}
