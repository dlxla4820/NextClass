package com.nextClass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.StringJoiner;

@Getter
@Setter
@NoArgsConstructor
public class TimeTableRequestDto {
    //member uuid : 로그인 시 세션으로 가져옴, class_uuid : 데이터 등록 시에 생성
    private String uuid;
    @JsonProperty("member_uuid")
    private String memberUuid;
    private String week;
    @JsonProperty("class_start_time")
    private Integer classStartTime;
    @JsonProperty("class_end_time")
    private Integer classEndTime;
    @JsonProperty("class_grade")
    private Integer classGrade;
    @JsonProperty("teacher_name")
    private String teacherName;
    private Integer score;
    private String title;
    private String semester;
    private String school;
    @JsonProperty("class_detail_uuid")
    private String classDetailUuid;
    private String category;
    private String color;

    @Override
    public String toString() {
        return new StringJoiner(", ", TimeTableRequestDto.class.getSimpleName() + "[", "]")
                .add("uuid='" + uuid + "'")
                .add("memberUuid='" + memberUuid + "'")
                .add("week='" + week + "'")
                .add("classStartTime='" + classStartTime + "'")
                .add("classEndTime='" + classEndTime + "'")
                .add("classGrade='" + classGrade + "'")
                .add("teacherName='" + teacherName + "'")
                .add("score='" + score + "'")
                .add("title='" + title + "'")
                .add("semester='" + semester + "'")
                .add("school='" + school + "'")
                .add("classDetailUuid='" + classDetailUuid + "'")
                .add("category='" + category + "'")
                .add("color='" + color + "'")
                .toString();
    }
}
