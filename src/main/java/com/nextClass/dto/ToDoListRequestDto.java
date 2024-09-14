package com.nextClass.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ToDoListRequestDto {

    private UUID uuid;
    @JsonProperty("member_uuid")
    private UUID memberUuid;
    @JsonProperty("created_time")
    private LocalDateTime createdTime;
    @JsonProperty("update_time")
    private LocalDateTime updateTime;
    @JsonProperty("alarm_time")
    private LocalDateTime alarmTime;
    @JsonProperty("goal_time")
    private LocalDateTime goalTime;
    private String content;
    @JsonProperty("app_token")
    private String appToken;

    @Override
    public String toString() {
        return new StringJoiner(", ", PostListSelectRequestDto.class.getSimpleName() + "[", "]")
                .add("uuid='" + uuid + "'")
                .add("memberUuid='" + memberUuid + "'")
                .add("createdTime='" + createdTime + "'")
                .add("updateTime='" + updateTime + "'")
                .add("alarmTime='" + alarmTime + "'")
                .add("goalTime='" + goalTime + "'")
                .add("content='" + content + "'")
                .add("appToken='" + appToken + "'")
                .toString();
    }
}
