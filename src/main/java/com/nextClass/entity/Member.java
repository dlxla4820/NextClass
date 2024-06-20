package com.nextClass.entity;

import com.nextClass.enums.GradeType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
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

    private Integer member_grade;

    private String member_school;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private RoleType roleType = RoleType.USER;

    private LocalDateTime reg_date;

    private LocalDateTime mod_date;

    public enum RoleType {
        ADMIN, USER, ANONYMOUS
    }
}
