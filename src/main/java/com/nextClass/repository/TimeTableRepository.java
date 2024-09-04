package com.nextClass.repository;

import com.nextClass.entity.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface TimeTableRepository extends JpaRepository<TimeTable, UUID> {
}
