package com.nextClass.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;


@Getter
@NoArgsConstructor
public class ScoreRequestDto {   //entity 생성 필요
    private String uuid;
    private String title;
    private Integer credit;
    private String category;
    private String achivement;
    private Integer grade;
    private Double student_score;
    private Double average_score;
    private Double standard_deviation;
    private String semester;

    public ScoreRequestDto(String title, Integer credit, String category, String achivement, Integer grade, Double student_score, Double average_score, Double standard_deviation, String semester){
        this.title = title;
        this.credit = credit;
        this.category = category;
        this.achivement = achivement;
        this.grade = grade;
        this.student_score = student_score;
        this.average_score = average_score;
        this.standard_deviation = standard_deviation;
        this.semester = semester;
    }
    public ScoreRequestDto(String uuid, String title, Integer credit, String category, String achivement, Integer grade, Double student_score, Double average_score, Double standard_deviation, String semester){
        this.uuid = uuid;
        this.title = title;
        this.credit = credit;
        this.category = category;
        this.achivement = achivement;
        this.grade = grade;
        this.student_score = student_score;
        this.average_score = average_score;
        this.standard_deviation = standard_deviation;
        this.semester = semester;
    }
}

