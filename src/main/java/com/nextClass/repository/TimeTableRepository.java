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
}
