package com.nextClass.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Builder
@Table(name = "to_do_list_alarm")
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ToDoListAlarm {
    @Id
    private UUID to_do_list_uuid;
}
