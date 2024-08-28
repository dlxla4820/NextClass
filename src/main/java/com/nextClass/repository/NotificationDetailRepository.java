package com.nextClass.repository;

public class NotificationDetailRepository {
    private NotificationRepository notificationRepository;

    public NotificationDetailRepository(
            NotificationRepository notificationRepository
    ){
        this.notificationRepository = notificationRepository;
    }
    
}
