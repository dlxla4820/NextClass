package com.nextClass.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Table(name = "class_basic")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
//해당 값들은 post X, 값들은 임의로 등록해놔야 함
public class ClassBasic {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid = UUID.randomUUID();
    private String title;
}
