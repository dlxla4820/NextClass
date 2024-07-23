package com.nextClass.dto;

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

    private Integer member_grade;

    private String member_school;

    @Override
    public String toString() {
        return new StringJoiner(", ", MemberInfoResponseDto.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .add("email='" + email + "'")
                .add("member_grade='" + member_grade + "'")
                .add("member_school='" + member_school + "'")
                .toString();
    }
}
