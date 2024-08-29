package com.nextClass.Scheduler;

import com.nextClass.entity.ToDoList;
import com.nextClass.repository.ToDoListDetailRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
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
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private ToDoListScheduler toDoListScheduler;
    private ToDoListDetailRepository toDoListRepository;
    private Map<UUID, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private Environment environment;
    public SchedulerMain(
            ThreadPoolTaskScheduler threadPoolTaskScheduler,
            ToDoListScheduler toDoListScheduler,
            ToDoListDetailRepository toDoListRepository,
            Environment environment
    ){
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.toDoListScheduler = toDoListScheduler;
        this.toDoListRepository = toDoListRepository;
        this.environment = environment;
    }

    @PostConstruct
    public void init(){
        if(!environment.getActiveProfiles()[0].equals("local")){
            log.info("SchedulerMain << init >> | start");
            List<ToDoList> alarmList = toDoListRepository.readAllAlarmList();
            log.info("SchedulerMain << init >> | Current Alarm List : {}", alarmList.size());
            alarmList.stream().forEach(toDoList -> {
                ScheduledFuture<?> newSchedule = threadPoolTaskScheduler.schedule(() -> {
                    // 작업 내용
                    toDoListScheduler.sendToDoListAlarmToFcm(toDoList);
                    scheduledTasks.remove(toDoList.getUuid()); // 스케줄 목록에서 제거
                }, Date.from(toDoList.getAlarmTime().toInstant(ZoneOffset.of("+09:00"))));
                scheduledTasks.put(toDoList.getUuid(), newSchedule);
            });
            log.info("SchedulerMain << init >> | finish");
        }
    }

    public void toDoListAlarmScheduler(ToDoList toDoList) {
        log.info("SchedulerMain << toDoListAlarmScheduler >> | AlarmStartTime : {}", toDoList.getAlarmTime());
        ScheduledFuture<?> newSchedule = threadPoolTaskScheduler.schedule(() -> {
            // Runnable 실행
            Runnable task = toDoListScheduler.sendToDoListAlarmToFcm(toDoList);
            task.run(); // Runnable 실행
            scheduledTasks.remove(toDoList.getUuid()); // 스케줄 목록에서 제거
            log.info("SchedulerMain << toDoListAlarmScheduler >> | ScheduledTask Size : {} ", scheduledTasks.size());
        }, Date.from(toDoList.getAlarmTime().toInstant(ZoneOffset.of("+09:00"))));
        scheduledTasks.put(toDoList.getUuid(), newSchedule);
        log.info("SchedulerMain << toDoListAlarmScheduler >> | ScheduledTask Size : {} ", scheduledTasks.size());
        log.info("SchedulerMain << toDoListAlarmScheduler >> | Finish ");
    }


    public void updateToDoListAlarmScheduler(ToDoList toDoList){
        log.info("SchedulerMain << updateToDoListAlarmScheduler >> | AlarmStartTime : {}", toDoList.getAlarmTime());
        ScheduledFuture<?> newSchedule = threadPoolTaskScheduler.schedule(() -> {
            // 작업 내용
            toDoListScheduler.sendToDoListAlarmToFcm(toDoList);
            scheduledTasks.remove(toDoList.getUuid()); // 스케줄 목록에서 제거
        }, Date.from(toDoList.getAlarmTime().toInstant(ZoneOffset.of("+09:00"))));
        scheduledTasks.replace(toDoList.getUuid(), newSchedule);
        log.info("SchedulerMain << updateToDoListAlarmScheduler >> | Finish ");
    }

    public void finishTask(UUID scheduledTaskUuid) {
        this.scheduledTasks.remove(scheduledTaskUuid);
    }
}
