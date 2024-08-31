package com.nextClass.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationConfigCategory {
    COMMENT_NOTIFICATION("comment_notification"), TO_DO_LIST_NOTIFICATION("to_do_list_notification");

    private final String category;
}
