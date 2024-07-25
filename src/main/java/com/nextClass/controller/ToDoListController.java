package com.nextClass.controller;


import com.nextClass.dto.ResponseDto;
import com.nextClass.dto.ToDoListRequsetDto;
import com.nextClass.service.ToDoListService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller

public class ToDoListController {
    private final ToDoListService toDoListService;

    public ToDoListController(ToDoListService toDoListService){
        this.toDoListService = toDoListService;
    }

    @PostMapping(value="to_do_list_create")
    public ResponseEntity<ResponseDto<?>> createToDoList(@RequestBody ToDoListRequsetDto toDoListDto){
        return ResponseEntity.ok(toDoListService.createToDoList(toDoListDto));
    }
}
