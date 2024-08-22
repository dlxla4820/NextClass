package com.nextClass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostChangeRequestDto {
    @JsonProperty("post_sequence")
    private Integer postSequence;
    private String subject;
    private String content;
    @JsonProperty("is_secret")
    private Boolean isSecret;


    @Override
    public String toString() {
        return new StringJoiner(", ", PostChangeRequestDto.class.getSimpleName() + "[", "]")
                .add("postSequence='" + postSequence + "'")
                .add("subject='" + subject + "'")
                .add("content='" + content + "'")
                .add("isSecret='" + isSecret + "'")
                .toString();
    }
}
