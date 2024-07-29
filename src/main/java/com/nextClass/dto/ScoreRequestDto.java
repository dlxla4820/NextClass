package com.nextClass.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@NoArgsConstructor
public class ScoreRequestDto {   //entity 생성 필요
    //

    private List<String> semester_list;
    private String timeTable_uuid;
    private String student_score;

    public ScoreRequestDto(String timeTable_uuid, String student_score){
        this.timeTable_uuid = timeTable_uuid;
        this.student_score = student_score;
    }
}
