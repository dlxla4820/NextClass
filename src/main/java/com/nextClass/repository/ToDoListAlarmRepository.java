package com.nextClass.repository;

import com.nextClass.entity.ToDoListAlarm;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ToDoListAlarmRepository extends CrudRepository<ToDoListAlarm, UUID> {
}
