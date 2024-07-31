package com.nextClass.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScoreResponseDto {
    private double allScore;
    private SemesterDto semester;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SemesterDto {
        private String semester;
        private double score;
        private List<SubjectDto> dataList;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SubjectDto {
            private String title;
            private int score;
            private double studentScore;
        }
    }
}
