package com.nextClass.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.StringJoiner;
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

    @Column(name = "member_grade")
    private Integer memberGrade;
    @Column(name = "member_school")
    private String memberSchool;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private RoleType roleType = RoleType.USER;

    @Column(name = "app_token")
    private String appToken;

    @Column(name = "reg_date")
    private LocalDateTime regDate;

    @Column(name = "mod_date")
    private LocalDateTime modDate;



    @Override
    public String toString() {
        return new StringJoiner(", ", Member.class.getSimpleName() + "[", "]")
                .add("uuid='" + uuid + "'")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .add("password='" + password + "'")
                .add("email='" + email + "'")
                .add("memberGrade='" + memberGrade + "'")
                .add("memberSchool='" + memberSchool + "'")
                .add("roleType='" + roleType + "'")
                .add("appToken='" + appToken + "'")
                .add("regDate='" + regDate + "'")
                .add("modDate='" + modDate + "'")
                .toString();
    }
    public enum RoleType {
        ADMIN, USER, ANONYMOUS
    }
}
