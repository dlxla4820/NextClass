package com.nextClass.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nextClass.repository.BoardRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequiredDataDto<T> {
    private String appToken;
    private String category;
    private Boolean isNotificationActivated;
    private T data;

    @Override
    public String toString() {
        return new StringJoiner(", ", NotificationRequiredDataDto.class.getSimpleName() + "[", "]")
                .add("appToken='" + appToken + "'")
                .add("category='" + category + "'")
                .add("isNotificationActivated='" + isNotificationActivated + "'")
                .add("data='" + data + "'")
                .toString();
    }
}
