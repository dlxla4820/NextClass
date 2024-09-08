package com.nextClass.repository;

import com.nextClass.dto.ToDoListRequsetDto;
import com.nextClass.entity.ToDoList;
import com.nextClass.entity.ToDoListAlarm;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.nextClass.entity.QToDoList.toDoList;
import static com.nextClass.entity.QToDoListAlarm.toDoListAlarm;

@Repository
public class ToDoListDetailRepository {
    private final JPAQueryFactory queryFactory;
    private final ToDoListRepository toDoListRepository;

    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime nextHour = now.plusHours(1).withMinute(0).withSecond(0).withNano(0);

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

    public ToDoList checkExist(ToDoListRequsetDto toDoListRequsetDto){
        return queryFactory.selectFrom(toDoList)
                .where(Expressions.stringTemplate("HEX({0})", toDoList.uuid).eq(toDoListRequsetDto.getUuid().toString().replace("-","")))
                .fetchOne();
    }

    public ToDoList checkAuthorize(ToDoListRequsetDto toDoListRequsetDto){
        return queryFactory.selectFrom(toDoList)
                .where(Expressions.stringTemplate("HEX({0})", toDoList.member_uuid).eq(toDoListRequsetDto.getMember_uuid().toString().replace("-","")))
                .where(Expressions.stringTemplate("HEX({0})", toDoList.uuid).eq(toDoListRequsetDto.getUuid().toString().replace("-","")))
                .fetchOne();
    }

    public void delete(ToDoList data){
        toDoListRepository.delete(data);
    }

    public long deleteToDoListByMemberId(String memberUuid){
        return queryFactory.delete(toDoList)
                .where(Expressions.stringTemplate("HEX({0})", toDoList.member_uuid).eq(memberUuid.replace("-","")))
                .execute();
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
    public ToDoList update(ToDoListRequsetDto toDoListRequsetDto) {
        ToDoList toDoListData = ToDoList.builder()
                .uuid(toDoListRequsetDto.getUuid())
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



    public List<ToDoList> readAlarmListWorkingAfterOneHour(){
        //현재 시간에서 한시간 이후에 보내져야 하는 알람들 가져오기
        return queryFactory.selectFrom(toDoList)
                .where(toDoList.alarmTime.after(now))
                .where(toDoList.alarmTime.before(nextHour))
                .fetch();
    }
}
