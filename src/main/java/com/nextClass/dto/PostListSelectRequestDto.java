package com.nextClass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostListSelectRequestDto {
    @JsonProperty("post_sequence")
    private Integer postSequence;

    private String sort;
    private Integer size;

    @Override
    public String toString() {
        return new StringJoiner(", ", PostListSelectRequestDto.class.getSimpleName() + "[", "]")
                .add("postSequence='" + postSequence + "'")
                .add("sort='" + sort + "'")
                .add("size='" + size + "'")
                .toString();
    }
}
