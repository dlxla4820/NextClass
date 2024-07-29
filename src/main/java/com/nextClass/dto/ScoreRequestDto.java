package com.nextClass.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;


@Getter
@NoArgsConstructor
public class ScoreRequestDto {
<<<<<<< HEAD

    private List<String> semester_list;
    private String timeTable_uuid;
    private String student_score;

    public ScoreRequestDto(String timeTable_uuid, String student_score){
        this.timeTable_uuid = timeTable_uuid;
        this.student_score = student_score;
    }

=======
    private String class_title;
    private Integer class_score;
    private Integer student_score;
    private String semester;
    private

    //entity 생성 필요
    //
    public ScoreRequestDto(){

    }
>>>>>>> 7c05d14 (학점계산기 controller, service, repository)
}
