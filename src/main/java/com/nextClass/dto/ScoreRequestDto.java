package com.nextClass.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
public class ScoreRequestDto {   //entity 생성 필요
    private List<ScoreInfo> data;

    @Getter
    @NoArgsConstructor
    public static class ScoreInfo{
        private String uuid;
        private String title;
        private Integer credit;
        private String category;
        private String achievement;
        private Integer grade;
        private Double student_score;
        private Double average_score;
        private Double standard_deviation;
        private String semester;

        public ScoreInfo(String title, Integer credit, String category, String achivement, Integer grade, Double student_score, Double average_score, Double standard_deviation, String semester){
            this.title = title;
            this.credit = credit;
            this.category = category;
            this.achievement = achivement;
            this.grade = grade;
            this.student_score = student_score;
            this.average_score = average_score;
            this.standard_deviation = standard_deviation;
            this.semester = semester;
        }
        public ScoreInfo(String uuid, String title, Integer credit, String category, String achivement, Integer grade, Double student_score, Double average_score, Double standard_deviation, String semester){
            this.uuid = uuid;
            this.title = title;
            this.credit = credit;
            this.category = category;
            this.achievement = achivement;
            this.grade = grade;
            this.student_score = student_score;
            this.average_score = average_score;
            this.standard_deviation = standard_deviation;
            this.semester = semester;
        }

    }
    public boolean checkDuplicateList(String uuid) {
        return this.data.stream()
                .filter(scoreInfo -> scoreInfo.getUuid() != null) // uuid가 null이 아닌 경우만 필터링
                .anyMatch(scoreInfo -> scoreInfo.getUuid().equals(uuid));
    }
}

