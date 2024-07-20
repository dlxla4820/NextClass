package com.nextClass.dto;


import com.nextClass.enums.GradeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequestDto {

    private String id;

    private String name;

    private String password;

    private String email;

    private Integer member_grade;

    private String member_school;

    @Override
    public String toString() {
        return new StringJoiner(", ", MemberRequestDto.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .add("password='" + password + "'")
                .add("email='" + email + "'")
                .add("member_grade='" + member_grade + "'")
                .add("member_school='" + member_school + "'")
                .toString();
    }

}
