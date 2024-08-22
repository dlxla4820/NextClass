package com.nextClass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentListSelectRequestDto {
    @JsonProperty("post_sequence")
    private Integer postSequence;
    @JsonProperty("comment_sequence")
    private Integer commentSequence;
    private Integer size;

    @Override
    public String toString() {
        return new StringJoiner(", ", CommentListSelectRequestDto.class.getSimpleName() + "[", "]")
                .add("postSequence='" + postSequence + "'")
                .add("commentSequence='" + commentSequence + "'")
                .add("size='" + size + "'")
                .toString();
    }
}
