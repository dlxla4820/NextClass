package com.nextClass.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class ScoreRequestDto {
    private String class_title;
    private Integer class_score;
    private Integer student_score;
    private String semester;
    private

    //entity 생성 필요
    //
    public ScoreRequestDto(){

    }
}
