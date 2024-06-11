package com.nextClass.entity;

import com.nextClass.enums.GradeType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    private String id;

    private String name;

    private String password;

    private String email;

    @Enumerated(EnumType.STRING)
    private GradeType member_grade;

    private String member_school;

    private LocalDateTime reg_date;

    private LocalDateTime mod_date;

}
