package com.nextClass.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ToDoListResponseDto {
    private UUID uuid;
    private String content;
    private LocalDateTime goal_time;
    private LocalDateTime alarm_time;
}
