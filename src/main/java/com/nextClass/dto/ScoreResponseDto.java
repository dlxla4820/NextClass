package com.nextClass.dto;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreResponseDto {
    private String average_grade;
    private Integer credit_sum;
    private final Integer require_credit = 174;
    private List<SemesterDto> semesterList;

    @Getter
    @Builder
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SemesterDto {
        private String semester;
        private String score;
        private List<SubjectDto> dataList;

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class SubjectDto {
            private UUID uuid;
            private String title;
            private String category;
            private Integer credit;
            private String achievement;
            private Integer grade;
            private Double studentScore;
            private String semester;
        }
    }
}
