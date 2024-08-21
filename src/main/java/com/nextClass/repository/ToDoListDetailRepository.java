package com.nextClass.repository;

import com.nextClass.dto.ToDoListRequsetDto;
import com.nextClass.dto.ToDoListResponseDto;
import com.nextClass.entity.ToDoList;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.nextClass.entity.QToDoList.toDoList;

@Repository
public class ToDoListDetailRepository {
    private final JPAQueryFactory queryFactory;
    private final ToDoListRepository toDoListRepository;

    ToDoListDetailRepository(
            JPAQueryFactory jpaQueryFactory,
            ToDoListRepository toDoListRepository
    ){
        this.queryFactory = jpaQueryFactory;
        this.toDoListRepository = toDoListRepository;
    }

    public ToDoList checkDuplicate(ToDoListRequsetDto toDoListRequsetDto){
        return queryFactory.selectFrom(toDoList)
                .where(toDoList.content.eq(toDoListRequsetDto.getContent()))
                .where(toDoList.appToken.eq(toDoListRequsetDto.getApp_token()))
                .where(Expressions.stringTemplate("HEX({0})", toDoList.member_uuid).eq(toDoListRequsetDto.getMember_uuid().toString().replace("-","")))
                .fetchOne();
    }

    public void save(ToDoListRequsetDto toDoListRequsetDto) {
        ToDoList toDoListData = ToDoList.builder()
                .content(toDoListRequsetDto.getContent())
                .appToken(toDoListRequsetDto.getApp_token())
                .createTime(toDoListRequsetDto.getCreated_time())
                .updateTime(toDoListRequsetDto.getUpdate_time())
                .goalTime(toDoListRequsetDto.getGoal_time())
                .member_uuid(toDoListRequsetDto.getMember_uuid())
                .alarmTime(toDoListRequsetDto.getAlarm_time())
                .build();
        toDoListRepository.save(toDoListData);
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
}
