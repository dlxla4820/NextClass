package com.nextClass.dto;


import com.nextClass.enums.GradeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto {

    private String id;

    private String name;

    private String password;

    private String email;

    private Integer member_grade;

    private String member_school;


}
