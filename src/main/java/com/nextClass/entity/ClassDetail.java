package com.nextClass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.*;

@Entity
@Getter
@Table(name = "class_detail")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "binary(16)")
    private UUID uuid = UUID.randomUUID();

    @Column(length = 50, nullable = false)
    private String title;

    @Column(name = "grade", nullable = false)
    private Integer classGrade;

    @Column(name = "teacher_name", length = 160, nullable = false)
    private String teacherName;

    @Column(nullable = false)
    private Integer score;

    @Column(length = 21, nullable = false)
    private String school;
}
