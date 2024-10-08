package com.nextClass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostDeleteRequestDto {
    @JsonProperty("post_sequence")
    private Integer postSequence;

    @Override
    public String toString() {
        return new StringJoiner(", ", PostDeleteRequestDto.class.getSimpleName() + "[", "]")
                .add("postSequence='" + postSequence + "'")
                .toString();
    }
}
