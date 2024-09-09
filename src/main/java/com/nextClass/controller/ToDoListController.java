package com.nextClass.controller;


import com.nextClass.dto.ResponseDto;
import com.nextClass.dto.ToDoListRequsetDto;
import com.nextClass.service.ToDoListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ToDoListController {
    private final ToDoListService toDoListService;

    public ToDoListController(ToDoListService toDoListService){
        this.toDoListService = toDoListService;
    }

    @PostMapping(value="to_do_list_create")
    public ResponseEntity<ResponseDto<?>> createToDoList(@RequestBody ToDoListRequsetDto toDoListDto){
        return ResponseEntity.ok(toDoListService.createToDoList(toDoListDto));
    }

    @PostMapping(value="to_do_list_read_all")
    public ResponseEntity<ResponseDto<?>> readAllToDoList(){
        return ResponseEntity.ok(toDoListService.readAllToDoList());
    }

    @PostMapping(value="to_do_list_update")
    public ResponseEntity<ResponseDto<?>> updateToDoList(@RequestBody ToDoListRequsetDto toDoListDto){
        return ResponseEntity.ok(toDoListService.updateToDoList(toDoListDto));
    }
    @PostMapping(value="to_do_list_delete")
    public ResponseEntity<ResponseDto<?>> deleteToDoList(@RequestBody ToDoListRequsetDto toDoListDto){
        return ResponseEntity.ok(toDoListService.deleteToDoList(toDoListDto));
    }
}
