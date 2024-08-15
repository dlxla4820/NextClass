package com.nextClass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentListSelectResponseDto {
    @JsonProperty("comment_sequence")
    private Integer commentSequence;
    private String content;
    private String author;
    private Boolean isOwner;
    @JsonProperty("vote_count")
    private Integer voteCount;
    @JsonProperty("reg_date")
    private LocalDateTime regDate;

    @Builder
    public CommentListSelectResponseDto(Integer commentSequence, String content, String author, Boolean isOwner, Integer voteCount, LocalDateTime regDate) {
        this.commentSequence = commentSequence;
        this.content = content;
        this.author = author;
        this.isOwner = isOwner;
        this.voteCount = voteCount;
        this.regDate = regDate;
    }
}
