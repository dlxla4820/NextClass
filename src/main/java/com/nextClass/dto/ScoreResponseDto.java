package com.nextClass.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreResponseDto {
    private String average_grade;
    private Integer credit_sum;
    private final Integer require_credit = 174;
    private List<SemesterDto> semester_list;

    @Getter
    @Builder
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SemesterDto {
        private String semester;
        private String score;
        private Integer credit_sum;
        @JsonProperty("dataList")
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
            @JsonProperty("student_score")
            private Double studentScore;
            @JsonProperty("average_score")
            private Double averageScore;
            @JsonProperty("standard_deviation")
            private Double standardDeviation;
            private String semester;

            @Override
            public String toString() {
                return new StringJoiner(", ", SubjectDto.class.getSimpleName() + "[", "]")
                        .add("uuid='" + uuid + "'")
                        .add("title='" + title + "'")
                        .add("category='" + category + "'")
                        .add("credit='" + credit + "'")
                        .add("achievement='" + achievement + "'")
                        .add("grade='" + grade + "'")
                        .add("studentScore='" + studentScore + "'")
                        .add("averageScore='" + averageScore + "'")
                        .add("standardDeviation='" + standardDeviation + "'")
                        .add("semester='" + semester + "'")
                        .toString();
            }
        }
        @Override
        public String toString() {
            return new StringJoiner(", ", SemesterDto.class.getSimpleName() + "[", "]")
                    .add("semester='" + semester + "'")
                    .add("score='" + score + "'")
                    .add("credit_sum='" + credit_sum + "'")
                    .add("dataList='" + dataList + "'")
                    .toString();
        }
    }
}
