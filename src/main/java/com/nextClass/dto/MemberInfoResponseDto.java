package com.nextClass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoResponseDto {

    private String id;

    private String name;

    private String email;

    @JsonProperty("member_grade")
    private Integer memberGrade;

    @JsonProperty("member_school")
    private String memberSchool;

    @Override
    public String toString() {
        return new StringJoiner(", ", MemberInfoResponseDto.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .add("email='" + email + "'")
                .add("memberGrade='" + memberGrade + "'")
                .add("memberSchool='" + memberSchool + "'")
                .toString();
    }
}
