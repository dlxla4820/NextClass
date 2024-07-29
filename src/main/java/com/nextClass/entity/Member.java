package com.nextClass.entity;

import com.nextClass.enums.GradeType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
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

    private Integer member_grade;

    private String member_school;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private RoleType roleType = RoleType.USER;

    private String app_token;
    private LocalDateTime reg_date;

    private LocalDateTime mod_date;



    @Override
    public String toString() {
        return new StringJoiner(", ", Member.class.getSimpleName() + "[", "]")
                .add("uuid='" + uuid + "'")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .add("password='" + password + "'")
                .add("email='" + email + "'")
                .add("member_grade='" + member_grade + "'")
                .add("member_school='" + member_school + "'")
                .add("roleType='" + roleType + "'")
                .add("app_token='" + app_token + "'")
                .add("reg_date='" + reg_date + "'")
                .add("mod_date='" + mod_date + "'")
                .toString();
    }
    public enum RoleType {
        ADMIN, USER, ANONYMOUS
    }
}
