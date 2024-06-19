package com.nextClass.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeTableRequestDto {
    //member uuid : 로그인 시 세션으로 가져옴, class_uuid : 데이터 등록 시에 생성
    private String week;
    private Integer class_time;
    private Integer class_grade;
    private String teacher_name;
    private Integer score;
    private String title;
}
