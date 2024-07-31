package com.nextClass.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScoreRequestDto {   //entity 생성 필요
    private String title;
    private Integer score;
    private Integer member_score;
    private String semester;
}

