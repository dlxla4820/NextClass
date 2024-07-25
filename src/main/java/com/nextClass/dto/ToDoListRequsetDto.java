package com.nextClass.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ToDoListRequsetDto {

    private String uuid;
    private String memberUuid;
    private LocalDateTime createdTime;
    private LocalDateTime updateTime;
    private LocalDateTime alarmTime;
    private LocalDateTime doneTime;
    private String content;

    public ToDoListRequsetDto(String content, LocalDateTime createdTime, LocalDateTime alarmTime, LocalDateTime doneTime){
        this.content = content;
        this.createdTime = createdTime;
        this.alarmTime = alarmTime;
        this.doneTime = doneTime;
    }

    public ToDoListRequsetDto(String uuid, String content, LocalDateTime updateTime, LocalDateTime alarmTime, LocalDateTime doneTime){
        this.uuid = uuid;
        this.content = content;
        this.alarmTime = alarmTime;
        this.updateTime = updateTime;
        this.doneTime = doneTime;
    }
}
