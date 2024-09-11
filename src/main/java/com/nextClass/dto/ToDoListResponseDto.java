package com.nextClass.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ToDoListResponseDto {
    private UUID uuid;
    private String content;
    @JsonProperty("goal_time")
    private LocalDateTime goalTime;
    @JsonProperty("alarm_time")
    private LocalDateTime alarmTime;

    @Override
    public String toString() {
        return new StringJoiner(", ", PostListSelectRequestDto.class.getSimpleName() + "[", "]")
                .add("uuid='" + uuid + "'")
                .add("content='" + content + "'")
                .add("goalTime='" + goalTime + "'")
                .add("alarmTime='" + alarmTime + "'")
                .toString();
    }
}
