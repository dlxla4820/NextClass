package com.nextClass.repository;

import com.nextClass.dto.NotificationConfigRequestDto;
import com.nextClass.dto.NotificationConfigResponseDto;
import com.nextClass.entity.Member;
import com.nextClass.entity.NotificationConfig;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import com.nextClass.enums.NotificationConfigCategory;
import java.time.LocalDateTime;
import java.util.List;

import static com.nextClass.entity.QMember.member;
import static com.nextClass.entity.QNotificationConfig.notificationConfig;

@Repository
public class NotificationDetailRepository {
    private final NotificationRepository notificationRepository;
    private final JPAQueryFactory queryFactory;
    public NotificationDetailRepository(
            NotificationRepository notificationRepository,
            JPAQueryFactory queryFactory){
        this.notificationRepository = notificationRepository;
        this.queryFactory = queryFactory;
    }

    public void saveNotificationConfigByCategory(Member member, NotificationConfigCategory notificationConfigCategory ){
        NotificationConfig notificationConfig = NotificationConfig.builder()
                .member(member)
                .category(notificationConfigCategory.getCategory())
                .isNotificationActivated(Boolean.FALSE)
                .req_date(LocalDateTime.now())
                .build();
        notificationRepository.save(notificationConfig);
    }



    public void updateNotificationConfig(NotificationConfigRequestDto notificationConfigRequestDto, String memberUuid){
          queryFactory.update(notificationConfig)
                  .set(notificationConfig.category, notificationConfigRequestDto.getCategory())
                  .set(notificationConfig.isNotificationActivated, notificationConfigRequestDto.getIsNotificationActivated())
                  .set(notificationConfig.mod_date, LocalDateTime.now())
                  .where(Expressions.stringTemplate("HEX({0})", notificationConfig.member.uuid).eq(memberUuid.replace("-","")))
                  .execute();
    }

    public List<NotificationConfigResponseDto> getNotificationConfigByMemberUuid(String memberUuid){
        return queryFactory.select(Projections.fields(NotificationConfigResponseDto.class, notificationConfig.category, notificationConfig.isNotificationActivated)).from(notificationConfig)
                .where(Expressions.stringTemplate("HEX({0})", notificationConfig.member.uuid).eq(memberUuid.replace("-","")))
                .fetch();
    }
}
