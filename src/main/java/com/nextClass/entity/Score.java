package com.nextClass.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    @Column(length = 50, nullable = false)
    private String title;
    @Column(nullable = false)
    private Integer score;
    @Column(name="student_score", nullable = false)
    private Integer studentScore;
    @Column(nullable = false)
    private String semester;
    @Column(name="member_uuid", nullable = false)
    private UUID memberUuid;
}
