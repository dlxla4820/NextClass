package com.nextClass.Scheduler;

import com.nextClass.dto.ToDoListRequsetDto;
import com.nextClass.entity.ToDoList;
import com.nextClass.repository.ToDoListAlarmRepository;
import com.nextClass.repository.ToDoListDetailRepository;
import com.nextClass.repository.ToDoListRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class SchedulerMain {
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private ToDoListScheduler toDoListScheduler;
    private ToDoListDetailRepository toDoListRepository;
    public SchedulerMain(
            ThreadPoolTaskScheduler threadPoolTaskScheduler,
            ToDoListScheduler toDoListScheduler,
            ToDoListDetailRepository toDoListRepository
    ){
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.toDoListScheduler = toDoListScheduler;
        this.toDoListRepository = toDoListRepository;
    }

    @PostConstruct
    public void init(){
        log.info("SchedulerMain << init >> | start");
        List<ToDoList> alarmList = toDoListRepository.readAllAlarmList();
        log.info("SchedulerMain << init >> | Current Alarm List : {}", alarmList.size());
        alarmList.stream().forEach(toDoList -> {threadPoolTaskScheduler.schedule(toDoListScheduler.sendToDoListAlarmToFcm(toDoList),Date.from(toDoList.getAlarmTime().toInstant(ZoneOffset.of("+09:00"))));});
        log.info("SchedulerMain << init >> | finish");
    }

    public void toDoListAlarmScheduler(ToDoList toDoList){
        log.info("SchedulerMain << toDoListAlarmScheduler >> | AlarmStartTime : {}", toDoList.getAlarmTime());
        threadPoolTaskScheduler.schedule(toDoListScheduler.sendToDoListAlarmToFcm(toDoList), Date.from(toDoList.getAlarmTime().toInstant(ZoneOffset.of("+09:00"))) );
        log.info("SchedulerMain << toDoListAlarmScheduler >> | Finish ");
    }
}
