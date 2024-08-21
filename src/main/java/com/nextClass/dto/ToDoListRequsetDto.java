package com.nextClass.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ToDoListRequsetDto {

    private String uuid;
    private UUID member_uuid;
    private LocalDateTime created_time;
    private LocalDateTime update_time;
    private LocalDateTime alarm_time;
    private LocalDateTime goal_time;
    private String content;
    private String app_token;

    public ToDoListRequsetDto(String content, LocalDateTime alarm_time, LocalDateTime goal_time, String app_token){
        this.content = content;
        this.alarm_time = alarm_time;
        this.goal_time = goal_time;
    }

    public ToDoListRequsetDto(String uuid, String content, LocalDateTime alarm_time, LocalDateTime goal_time, String app_token){
        this.uuid = uuid;
        this.content = content;
        this.alarm_time = alarm_time;
        this.goal_time = goal_time;
    }
}
