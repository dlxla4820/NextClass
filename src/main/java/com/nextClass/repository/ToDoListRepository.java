package com.nextClass.repository;

import com.nextClass.entity.ToDoList;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ToDoListRepository extends CrudRepository<ToDoList, UUID> {
}
