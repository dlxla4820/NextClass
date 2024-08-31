package com.nextClass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;

import java.time.LocalDateTime;
import java.util.StringJoiner;

@Getter
@NoArgsConstructor
public class PostListSelectResponseDto {
    @JsonProperty("post_sequence")
    private Integer postSequence;
    @Setter
    private String subject;
    @Setter
    private String content;
    private String author;
    @JsonProperty("vote_count")
    private Integer voteCount;
    @JsonProperty("comment_count")
    private Integer commentCount;
    @JsonProperty("reg_date")
    private LocalDateTime regDate;

    @Builder
    public PostListSelectResponseDto(Integer postSequence, String subject, String content, String author, Integer voteCount, Integer commentCount, LocalDateTime regDate) {
        this.postSequence = postSequence;
        this.subject = subject;
        this.content = content;
        this.author = author;
        this.voteCount = voteCount;
        this.commentCount = commentCount;
        this.regDate = regDate;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PostListSelectResponseDto.class.getSimpleName() + "[", "]")
                .add("postSequence='" + postSequence + "'")
                .add("subject='" + subject + "'")
                .add("content='" + content + "'")
                .add("author='" + author + "'")
                .add("voteCount='" + voteCount + "'")
                .add("commentCount='" + commentCount + "'")
                .add("regDate='" + regDate + "'")
                .toString();
    }
}
