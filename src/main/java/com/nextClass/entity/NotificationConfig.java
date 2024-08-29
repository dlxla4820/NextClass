package com.nextClass.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.UUID;

@Entity
@Getter
@Builder
@Table(name = "notification_config")
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "member_uuid")
    private Member member;

    private String category;

    @Column(name = "is_notification_activated")
    private Boolean isNotificationActivated;

    @Column(name = "reqDate")
    private LocalDateTime req_date;

    @Column(name = "modDate")
    private LocalDateTime mod_date;
    @Override
    public String toString() {
        return new StringJoiner(", ", NotificationConfig.class.getSimpleName() + "[", "]")
                .add("uuid='" + uuid + "'")
                .add("member='" + member.getUuid() + "'")
                .add("category='" + category + "'")
                .add("isNotificationActivated='" + isNotificationActivated + "'")
                .add("req_date='" + req_date + "'")
                .add("mod_date='" + mod_date + "'")
                .toString();
    }
}
