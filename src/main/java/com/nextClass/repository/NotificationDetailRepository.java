package com.nextClass.repository;

import org.springframework.stereotype.Repository;

@Repository
public class NotificationDetailRepository {
    private NotificationRepository notificationRepository;

    public NotificationDetailRepository(
            NotificationRepository notificationRepository
    ){
        this.notificationRepository = notificationRepository;
    }

}
