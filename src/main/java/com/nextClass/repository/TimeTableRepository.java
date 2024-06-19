package com.nextClass.repository;

import com.nextClass.entity.ClassDetail;
import com.nextClass.entity.Member;
import com.nextClass.entity.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


public interface TimeTableRepository extends JpaRepository<TimeTable, UUID> {

    Optional<TimeTable> findByClassDetailAndClassTimeAndWeek(
//            Member member
            ClassDetail classDetail, int class_time, String week );
}
