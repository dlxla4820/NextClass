package com.nextClass.scheduler;

<<<<<<< HEAD:src/main/java/com/nextClass/Scheduler/ToDoListScheduler.java
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
=======
>>>>>>> ea5b0ec (NotificationConfig Entity 생성):src/main/java/com/nextClass/scheduler/ToDoListScheduler.java
import com.nextClass.entity.ToDoList;
import com.nextClass.repository.ToDoListDetailRepository;
import com.nextClass.service.AndroidPushNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ToDoListScheduler {
    private final ToDoListDetailRepository toDoListRepository;
    private final AndroidPushNotificationService androidPushNotificationService;
    public ToDoListScheduler(
            ToDoListDetailRepository toDoListRepository,
            AndroidPushNotificationService androidPushNotificationService
    ){
        this.toDoListRepository = toDoListRepository;
        this.androidPushNotificationService = androidPushNotificationService;
    }
    public Runnable sendToDoListAlarmToFcm(ToDoList toDoList ){
        return () -> {
            // FCM 알림을 보내는 로직을 여기에 추가합니다.
            // 예를 들어, FCM 서비스 호출 등의 작업을 수행
            try {
                String response = androidPushNotificationService.sendPushNotification("To Do List", toDoList.getContent(), toDoList.getAppToken());
                toDoListRepository.deleteAlarm(toDoList.getUuid());
                log.info("ToDoListScheduler << sendToDoListAlarmToFcm >> | Response : {}", response);
            } catch (FirebaseMessagingException e) {
                log.error("ToDoListScheduler << sendToDoListAlarmToFcm >> | Exception : {}", e.getMessage());
            }
        };
    }
}
