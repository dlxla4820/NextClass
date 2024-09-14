package com.nextClass.repository;

import com.nextClass.dto.ToDoListRequestDto;
import com.nextClass.entity.ToDoList;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import static com.nextClass.entity.QToDoList.toDoList;

@Repository
public class ToDoListDetailRepository {
    private final JPAQueryFactory queryFactory;
    private final ToDoListRepository toDoListRepository;

    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDateTime nextMinute = now.plusMinutes(1).withSecond(0).withNano(0);

    ToDoListDetailRepository(
            JPAQueryFactory jpaQueryFactory,
            ToDoListRepository toDoListRepository
    ){
        this.queryFactory = jpaQueryFactory;
        this.toDoListRepository = toDoListRepository;
    }

    public ToDoList checkDuplicate(ToDoListRequestDto ToDoListRequestDto){
        return queryFactory.selectFrom(toDoList)
                .where(toDoList.content.eq(ToDoListRequestDto.getContent()))
                .where(toDoList.appToken.eq(ToDoListRequestDto.getAppToken()))
                .where(Expressions.stringTemplate("HEX({0})", toDoList.member_uuid).eq(ToDoListRequestDto.getMemberUuid().toString().replace("-","")))
                .fetchOne();
    }

    public ToDoList checkExist(ToDoListRequestDto ToDoListRequestDto){
        return queryFactory.selectFrom(toDoList)
                .where(Expressions.stringTemplate("HEX({0})", toDoList.uuid).eq(ToDoListRequestDto.getUuid().toString().replace("-","")))
                .fetchOne();
    }

    public ToDoList checkAuthorize(ToDoListRequestDto ToDoListRequestDto){
        return queryFactory.selectFrom(toDoList)
                .where(Expressions.stringTemplate("HEX({0})", toDoList.member_uuid).eq(ToDoListRequestDto.getMemberUuid().toString().replace("-","")))
                .where(Expressions.stringTemplate("HEX({0})", toDoList.uuid).eq(ToDoListRequestDto.getUuid().toString().replace("-","")))
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

    public ToDoList save(ToDoListRequestDto ToDoListRequestDto) {
        ToDoList toDoListData = ToDoList.builder()
                .content(ToDoListRequestDto.getContent())
                .appToken(ToDoListRequestDto.getAppToken())
                .createTime(ToDoListRequestDto.getCreatedTime())
                .updateTime(ToDoListRequestDto.getUpdateTime())
                .goalTime(ToDoListRequestDto.getGoalTime())
                .member_uuid(ToDoListRequestDto.getMemberUuid())
                .alarmTime(ToDoListRequestDto.getAlarmTime())
                .build();
        return toDoListRepository.save(toDoListData);
    }
    public ToDoList update(ToDoListRequestDto ToDoListRequestDto) {
        ToDoList toDoListData = ToDoList.builder()
                .uuid(ToDoListRequestDto.getUuid())
                .content(ToDoListRequestDto.getContent())
                .appToken(ToDoListRequestDto.getAppToken())
                .createTime(ToDoListRequestDto.getCreatedTime())
                .updateTime(ToDoListRequestDto.getUpdateTime())
                .goalTime(ToDoListRequestDto.getGoalTime())
                .member_uuid(ToDoListRequestDto.getMemberUuid())
                .alarmTime(ToDoListRequestDto.getAlarmTime())
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
                .where(toDoList.alarmTime.before(nextMinute))
                .fetch();
    }
}
