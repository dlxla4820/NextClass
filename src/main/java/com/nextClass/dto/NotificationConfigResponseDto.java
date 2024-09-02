package com.nextClass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NotificationConfigResponseDto {
    private String category;
    @JsonProperty("is_notification_activated")
    private Boolean isNotificationActivated;

    @Override
    public String toString() {
        return new StringJoiner(", ", NotificationConfigResponseDto.class.getSimpleName() + "[", "]")
                .add("category='" + category + "'")
                .add("isNotificationActivated='" + isNotificationActivated + "'")
                .toString();
    }
}
