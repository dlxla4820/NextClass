package com.nextClass.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TimeTableRequestDto {
    //member uuid : 로그인 시 세션으로 가져옴, class_uuid : 데이터 등록 시에 생성
    private String uuid;
    private String week;
    private Integer class_start_time;
    private Integer class_end_time;
    private Integer class_grade;
    private String teacher_name;
    private Integer score;
    private String title;
    private String semester;
    private String school;
    private String class_detail_uuid;
    private String category;

    public TimeTableRequestDto(String week, Integer class_start_time, Integer class_end_time, Integer class_grade, String teacher_name, Integer score, String title, String semester, String school, String category){
        this.week = week;
        this.class_start_time = class_start_time;
        this.class_end_time = class_end_time;
        this.class_grade = class_grade;
        this.teacher_name = teacher_name;
        this.score = score;
        this.title = title;
        this.semester = semester;
        this.school = school;
        this.category = category;
    }

    public TimeTableRequestDto(String uuid,String class_detail_uuid, String week, Integer class_start_time, Integer class_end_time, Integer class_grade, String teacher_name, Integer score, String title, String semester, String school, String category){
        this.uuid = uuid;
        this.class_detail_uuid = class_detail_uuid;
        this.week = week;
        this.class_start_time = class_start_time;
        this.class_end_time = class_end_time;
        this.class_grade = class_grade;
        this.teacher_name = teacher_name;
        this.score = score;
        this.title = title;
        this.semester = semester;
        this.school = school;
        this.category = category;
    }

    public TimeTableRequestDto(String semester){
        this.semester = semester;
    }
}
