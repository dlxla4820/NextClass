package com.nextClass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentChangeRequestDto {
    @JsonProperty("post_sequence")
    private Integer postSequence;
    @JsonProperty("comment_sequence")
    private Integer commentSequence;
    private String content;
    @JsonProperty("is_secret")
    private Boolean isSecret;

    @Override
    public String toString() {
        return new StringJoiner(", ", CommentChangeRequestDto.class.getSimpleName() + "[", "]")
                .add("postSequence='" + postSequence + "'")
                .add("commentSequence='" + commentSequence + "'")
                .add("content='" + content + "'")
                .add("isSecret='" + isSecret + "'")
                .toString();
    }
}
