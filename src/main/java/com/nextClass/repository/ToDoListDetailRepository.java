package com.nextClass.repository;

import com.nextClass.dto.ToDoListRequsetDto;
import com.nextClass.dto.ToDoListResponseDto;
import com.nextClass.entity.ToDoList;
import com.nextClass.entity.ToDoListAlarm;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.nextClass.entity.QToDoList.toDoList;
import static com.nextClass.entity.QToDoListAlarm.toDoListAlarm;

@Repository
public class ToDoListDetailRepository {
    private final JPAQueryFactory queryFactory;
    private final ToDoListRepository toDoListRepository;
    private final ToDoListAlarmRepository toDoListAlarmRepository;

    ToDoListDetailRepository(
            JPAQueryFactory jpaQueryFactory,
            ToDoListRepository toDoListRepository,
            ToDoListAlarmRepository toDoListAlarmRepository
    ){
        this.queryFactory = jpaQueryFactory;
        this.toDoListRepository = toDoListRepository;
        this.toDoListAlarmRepository = toDoListAlarmRepository;
    }

    public ToDoList checkDuplicate(ToDoListRequsetDto toDoListRequsetDto){
        return queryFactory.selectFrom(toDoList)
                .where(toDoList.content.eq(toDoListRequsetDto.getContent()))
                .where(toDoList.appToken.eq(toDoListRequsetDto.getApp_token()))
                .where(Expressions.stringTemplate("HEX({0})", toDoList.member_uuid).eq(toDoListRequsetDto.getMember_uuid().toString().replace("-","")))
                .fetchOne();
    }

    public ToDoList save(ToDoListRequsetDto toDoListRequsetDto) {
        ToDoList toDoListData = ToDoList.builder()
                .content(toDoListRequsetDto.getContent())
                .appToken(toDoListRequsetDto.getApp_token())
                .createTime(toDoListRequsetDto.getCreated_time())
                .updateTime(toDoListRequsetDto.getUpdate_time())
                .goalTime(toDoListRequsetDto.getGoal_time())
                .member_uuid(toDoListRequsetDto.getMember_uuid())
                .alarmTime(toDoListRequsetDto.getAlarm_time())
                .build();
        return toDoListRepository.save(toDoListData);
    }
    public ToDoListAlarm saveAlarm(UUID toDoListUuid){
        return toDoListAlarmRepository.save(ToDoListAlarm.builder().to_do_list_uuid(toDoListUuid).build());
    }

    public void deleteAlarm(UUID toDoListUuid){
        toDoListAlarmRepository.delete(ToDoListAlarm.builder().to_do_list_uuid(toDoListUuid).build());
    }

    public List<Tuple> readAll(String currentUser){
        return queryFactory.select(
                toDoList.uuid,
                toDoList.goalTime,
                toDoList.alarmTime,
                toDoList.content
                )
                .from(toDoList)
                .where(Expressions.stringTemplate("HEX({0})", toDoList.member_uuid).eq(currentUser.replace("-","")))
                .fetch();
    }

    public List<ToDoList> readAllAlarmList(){
        List<UUID> alarmList = queryFactory.select(toDoListAlarm.to_do_list_uuid).from(toDoListAlarm).fetch();
        return queryFactory.selectFrom(toDoList)
                .where(toDoList.uuid.in(alarmList))
                .fetch();
    }
}
