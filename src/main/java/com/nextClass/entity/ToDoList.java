package com.nextClass.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Builder
@Table(name = "to_do_list")
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ToDoList {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    private String content;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name="update_time")
    private LocalDateTime updateTime;

    @Column(name="alarm_time")
    private LocalDateTime alarmTime;

    @Column(name="done_time")
    private LocalDateTime doneTime;
}
