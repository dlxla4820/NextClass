package com.nextClass.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("member_grade")
    private Integer memberGrade;
    @JsonProperty("member_school")
    private String memberSchool;

    @Override
    public String toString() {
        return new StringJoiner(", ", MemberRequestDto.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .add("password='" + password + "'")
                .add("email='" + email + "'")
                .add("memberGrade='" + memberGrade + "'")
                .add("memberSchool='" + memberSchool + "'")
                .toString();
    }

}
