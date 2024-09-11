package com.nextClass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeTableReponseDto {
    private UUID uuid;
    private String week;
    @JsonProperty("class_start_time")
    private Integer classStartTime;
    @JsonProperty("class_end_time")
    private Integer classEndTime;
    private String semester;
    private String title;
    @JsonProperty("class_grade")
    private Integer classGrade;
    @JsonProperty("teacher_name")
    private String teacherName;
    private Integer score;
    private String school;
    private String category;
    private String color;

    @Override
    public String toString() {
        return new StringJoiner(", ", TimeTableReponseDto.class.getSimpleName() + "[", "]")
                .add("uuid='" + uuid + "'")
                .add("week='" + week + "'")
                .add("classStartTime='" + classStartTime + "'")
                .add("classEndTime='" + classEndTime + "'")
                .add("semester='" + semester + "'")
                .add("title='" + title + "'")
                .add("classGrade='" + classGrade + "'")
                .add("teacherName='" + teacherName + "'")
                .add("score='" + score + "'")
                .add("school='" + school + "'")
                .add("category='" + category + "'")
                .add("color='" + color + "'")
                .toString();
    }
}
