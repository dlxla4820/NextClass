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
    @JsonProperty("post_id")
    private String postId;
    private String subject;
    private String content;
    @JsonProperty("is_secret")
    private Boolean isSecret;


    @Override
    public String toString() {
        return new StringJoiner(", ", PostChangeRequestDto.class.getSimpleName() + "[", "]")
                .add("postId='" + postId + "'")
                .add("subject='" + subject + "'")
                .add("content='" + content + "'")
                .add("isSecret='" + isSecret + "'")
                .toString();
    }
}
