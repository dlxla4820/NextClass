package com.nextClass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDeleteRequestDto {
    @JsonProperty("post_sequence")
    private Integer postSequence;
    @JsonProperty("comment_sequence")
    private Integer commentSequence;
    @Override
    public String toString() {
        return new StringJoiner(", ", CommentDeleteRequestDto.class.getSimpleName() + "[", "]")
                .add("postSequence='" + postSequence + "'")
                .add("commentSequence='" + commentSequence + "'")
                .toString();
    }
}
