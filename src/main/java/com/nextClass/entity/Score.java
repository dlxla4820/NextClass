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
    //학점
    @Column(nullable = false)
    private Integer credit;
    //공통, 선택, 창체
    @Column(nullable = false)
    private String category;
    @Column
    private String achievement;//abcde로 저장하는 성취 평가제
    @Column
    private Integer grade;
    @Column(name="student_score")
    private Double studentScore;//학생 원점수
    @Column(name = "average_score")
    private Double averageScore;//학생 평균 점수
    @Column(name = "standard_deviation")
    private Double standardDeviation;//수강생수
    @Column(nullable = false)
    private String semester;
    @Column(name="member_uuid", nullable = false)
    private UUID memberUuid;
}
