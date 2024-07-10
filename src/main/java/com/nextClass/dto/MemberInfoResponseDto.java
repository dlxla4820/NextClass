package com.nextClass.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoResponseDto {

    private String id;

    private String name;

    private String email;

    private Integer member_grade;

    private String member_school;

}
