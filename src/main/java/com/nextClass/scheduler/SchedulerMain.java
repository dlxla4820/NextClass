package com.nextClass.scheduler;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.nextClass.entity.ToDoList;
import com.nextClass.repository.ToDoListDetailRepository;
import com.nextClass.service.AndroidPushNotificationService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
@Slf4j
public class SchedulerMain {
    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private final ToDoListDetailRepository toDoListRepository;
    private final Map<UUID, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final Environment environment;
    private final AndroidPushNotificationService androidPushNotificationService;
    public SchedulerMain(
            ThreadPoolTaskScheduler threadPoolTaskScheduler,
            ToDoListDetailRepository toDoListRepository,
            Environment environment,
            AndroidPushNotificationService androidPushNotificationService
    ){
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.androidPushNotificationService = androidPushNotificationService;
        this.toDoListRepository = toDoListRepository;
        this.environment = environment;
    }

    @PostConstruct
    @Scheduled(cron = "0 0 * * * ?")
    public void addAlarmOnTaskScheduler(){
        //to do list에서 현재 시간 이후 한시간 이내로 울려야 되는 알람들을 전부 가져옴
        if(!environment.getActiveProfiles()[0].equals("local")){
            List<ToDoList> alarmList = toDoListRepository.readAlarmListWorkingAfterOneHour();
            log.info("SchedulerMain << addAlarmOnTaskScheduler >> | Current Alarm List : {}", alarmList.size());
            alarmList.stream().forEach(toDoList -> {
                ScheduledFuture<?> newSchedule = this.createScheduledFutureTaskUsingToDoList(toDoList);
                scheduledTasks.put(toDoList.getUuid(), newSchedule);
            });
        }
    }

    public void toDoListAlarmScheduler(ToDoList toDoList) {
        log.info("SchedulerMain << toDoListAlarmScheduler >> | start");
        ScheduledFuture<?> newSchedule = this.createScheduledFutureTaskUsingToDoList(toDoList);
        scheduledTasks.put(toDoList.getUuid(), newSchedule);
        log.info("SchedulerMain << toDoListAlarmScheduler >> | Finish ");
    }


    public void updateToDoListAlarmScheduler(ToDoList toDoList){
        log.info("SchedulerMain << updateToDoListAlarmScheduler >> | start");
        ScheduledFuture<?> newSchedule = this.createScheduledFutureTaskUsingToDoList(toDoList);
        scheduledTasks.replace(toDoList.getUuid(), newSchedule);
        log.info("SchedulerMain << updateToDoListAlarmScheduler >> | Finish ");
    }



    private ScheduledFuture<?> createScheduledFutureTaskUsingToDoList(ToDoList toDoList){
        return threadPoolTaskScheduler.schedule(() -> {
            // 작업 내용
            this.sendToDoListAlarmToFcm(toDoList);
            scheduledTasks.remove(toDoList.getUuid()); // 스케줄 목록에서 제거
        }, Date.from(toDoList.getAlarmTime().toInstant(ZoneOffset.of("+09:00"))));
    }
    private Runnable sendToDoListAlarmToFcm(ToDoList toDoList ){
        return () -> {
            // FCM 알림을 보내는 로직을 여기에 추가합니다.
            // 예를 들어, FCM 서비스 호출 등의 작업을 수행
            try {
                String response = androidPushNotificationService.sendPushNotification("To Do List", toDoList.getContent(), toDoList.getAppToken());
                log.info("ToDoListScheduler << sendToDoListAlarmToFcm >> | Response : {}", response);
            } catch (FirebaseMessagingException e) {
                log.error("ToDoListScheduler << sendToDoListAlarmToFcm >> | Exception : {}", e.getMessage());
            }
        };
    }

    public void finishTask(UUID scheduledTaskUuid) {
        this.scheduledTasks.remove(scheduledTaskUuid);
    }
}
