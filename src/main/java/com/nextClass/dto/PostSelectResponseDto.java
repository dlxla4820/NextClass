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
    @JsonProperty("post_id")
    private String postId;
    private String subject;
    private String content;
    private String name;
    private boolean isOwner;
    @Builder
    public PostSelectResponseDto(String postId, String subject, String content, String name, boolean isOwner) {
        this.postId = postId;
        this.subject = subject;
        this.content = content;
        this.name = name;
        this.isOwner = isOwner;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PostSelectResponseDto.class.getSimpleName() + "[", "]")
                .add("postId='" + postId + "'")
                .add("subject='" + subject + "'")
                .add("content='" + content + "'")
                .add("name='" + name + "'")
                .add("isOwner='" + isOwner + "'")
                .toString();
    }
}
