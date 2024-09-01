package com.nextClass.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeTableReponseDto {
    private UUID uuid;
    private String week;
    private Integer class_start_time;
    private Integer class_end_time;
    private String semester;
    private String title;
    private Integer class_grade;
    private String teacher_name;
    private Integer score;
    private String school;
    private String category;
}
