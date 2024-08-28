package com.nextClass.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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

    @Column(nullable = false)
    private UUID member_uuid;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String is_notification_activated;

    @Column(nullable = false)
    private LocalDateTime create_time;

    @Column(nullable = false)
    private LocalDateTime update_time;

}
