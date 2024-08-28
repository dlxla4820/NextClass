package com.nextClass.service;

import com.nextClass.dto.ResponseDto;
import com.nextClass.repository.NotificationDetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class NotificationService {

    private NotificationDetailRepository notificationDetailRepository;

    public NotificationService(
            NotificationDetailRepository notificationDetailRepository
    ){
        this.notificationDetailRepository = notificationDetailRepository;
    }


}
