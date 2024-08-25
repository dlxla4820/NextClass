package com.nextClass.Scheduler;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.nextClass.entity.ToDoList;
import com.nextClass.repository.ToDoListDetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class ToDoListScheduler {
    private ToDoListDetailRepository toDoListRepository;
    public ToDoListScheduler(
            ToDoListDetailRepository toDoListRepository
    ){
        this.toDoListRepository = toDoListRepository;
    }
    public Runnable sendToDoListAlarmToFcm(ToDoList toDoList ){
        return () -> {
            // FCM 알림을 보내는 로직을 여기에 추가합니다.
            // 예를 들어, FCM 서비스 호출 등의 작업을 수행
            try {
                Message message = Message.builder()
                        .setNotification(Notification.builder()
                                .setBody(toDoList.getContent())
                                .build())
                        .setToken(toDoList.getAppToken())
                        .build();
                String response = FirebaseMessaging.getInstance().send(message);
                toDoListRepository.deleteAlarm(toDoList.getUuid());
                log.info("ToDoListScheduler << sendToDoListAlarmToFcm >> | Response : {}", response);
            } catch (Exception e) {
                log.error("ToDoListScheduler << sendToDoListAlarmToFcm >> | Exception : {}", e.getMessage());
            }
        };
    }
}
