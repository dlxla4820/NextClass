package com.nextClass.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Getter
@Table(name = "class_detail")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();
    private String title;
    @Column(name = "grade")
    private Integer classGrade;
    @Column(name = "teacher_name")
    private String teacherName;
    private Integer score;
}
