package com.nextClass.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberChangeNormalInfoRequestDto {

    private String name;

    private Integer member_grade;

    private String member_school;
    @Override
    public String toString() {
        return new StringJoiner(", ", MemberChangeNormalInfoRequestDto.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("member_grade='" + member_grade + "'")
                .add("member_school='" + member_school + "'")
                .toString();
    }
}
