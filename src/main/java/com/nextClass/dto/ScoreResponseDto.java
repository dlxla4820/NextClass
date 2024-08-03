package com.nextClass.dto;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreResponseDto {
    private double allScore;
    private SemesterDto semester;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SemesterDto {
        private String semester;
        private double score;
        private List<SubjectDto> dataList;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SubjectDto {
            private String title;
            private int score;
            private double studentScore;
        }
    }
}
