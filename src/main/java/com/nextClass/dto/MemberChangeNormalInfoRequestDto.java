package com.nextClass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberChangeNormalInfoRequestDto {

    private String name;
    @JsonProperty("member_grade")
    private Integer memberGrade;
    @JsonProperty("member_school")
    private String memberSchool;
    @Override
    public String toString() {
        return new StringJoiner(", ", MemberChangeNormalInfoRequestDto.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("memberGrade='" + memberGrade + "'")
                .add("memberSchool='" + memberSchool + "'")
                .toString();
    }
}
