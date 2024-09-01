package com.nextClass.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NotificationConfigRequestDto {
    private String category;
    @Column(name = "is_notification_activated")
    private Boolean isNotificationActivated;

    @Override
    public String toString() {
        return new StringJoiner(", ", NotificationConfigRequestDto.class.getSimpleName() + "[", "]")
                .add("category='" + category + "'")
                .add("isNotificationActivated='" + isNotificationActivated + "'")
                .toString();
    }
}