package com.nextClass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostListSelectResponseDto {
    @JsonProperty("post_sequence")
    private Integer postSequence;
    private String subject;
    private String content;
    private String name;
    @JsonProperty("vote_count")
    private Integer voteCount;
    @JsonProperty("comment_count")
    private Integer commentCount;
    @JsonProperty("reg_date")
    private LocalDateTime regDate;

    @Builder
    public PostListSelectResponseDto(Integer postSequence, String subject, String content, String name, Integer voteCount, Integer commentCount, LocalDateTime regDate) {
        this.postSequence = postSequence;
        this.subject = subject;
        this.content = content;
        this.name = name;
        this.voteCount = voteCount;
        this.commentCount = commentCount;
        this.regDate = regDate;
    }
}
