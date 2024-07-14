package com.nextClass.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberChangeNormalInfoRequestDto {

    private String name;

    private Integer member_grade;

    private String member_school;
}
