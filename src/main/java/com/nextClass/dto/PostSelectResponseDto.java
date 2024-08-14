package com.nextClass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor
public class PostSelectResponseDto {
    @JsonProperty("post_sequence")
    private Integer postSequence;
    private String subject;
    private String content;
    private String author;
    private boolean isOwner;
    @Builder
    public PostSelectResponseDto(Integer postSequence, String subject, String content, String author, boolean isOwner) {
        this.postSequence = postSequence;
        this.subject = subject;
        this.content = content;
        this.author = author;
        this.isOwner = isOwner;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PostSelectResponseDto.class.getSimpleName() + "[", "]")
                .add("postId='" + postSequence + "'")
                .add("subject='" + subject + "'")
                .add("content='" + content + "'")
                .add("author='" + author + "'")
                .add("isOwner='" + isOwner + "'")
                .toString();
    }
}
