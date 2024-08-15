package com.nextClass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentListSelectRequestDto {
    @JsonProperty("post_sequence")
    private Integer postSequence;
    @JsonProperty("comment_sequence")
    private Integer commentSequence;
    private Integer size;
}
