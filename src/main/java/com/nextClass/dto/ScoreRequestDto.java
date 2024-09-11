package com.nextClass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.StringJoiner;


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
        @JsonProperty("student_score")
        private Double studentScore;
        @JsonProperty("student_score")
        private Double averageScore;
        @JsonProperty("standard_deviation")
        private Double standardDeviation;
        private String semester;

        @Override
        public String toString(){
            return new StringJoiner(", ", ScoreInfo.class.getSimpleName() + "[", "]")
                    .add("uuid='" + uuid + "'" )
                    .add("title='"+ title + "'")
                    .add("credit='"+ credit + "'")
                    .add("category='"+ category + "'")
                    .add("achievement=" + achievement)
                    .add("grade=" + grade)
                    .add("studentScore=" + studentScore)
                    .add("averageScore=" + averageScore)
                    .add("standardDeviation=" + standardDeviation)
                    .add("semester=" + semester)
                    .toString();
        }

    }
    public boolean checkDuplicateList(String uuid) {
        return this.data.stream()
                .filter(scoreInfo -> scoreInfo.getUuid() != null) // uuid가 null이 아닌 경우만 필터링
                .anyMatch(scoreInfo -> scoreInfo.getUuid().equals(uuid));
    }
    @Override
    public String toString() {
        return new StringJoiner(", ", ScoreRequestDto.class.getSimpleName() + "[", "]")
                .add("data='" + data + "'")
                .toString();
    }
}

