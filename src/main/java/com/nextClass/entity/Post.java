package com.nextClass.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

@Builder
@Entity
@Getter
@Setter
@Table(name = "post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sequence;

    @ManyToOne
    @JoinColumn(name = "member_uuid")
    private Member member;

    private String subject;

    private String author;

    private String content;

    @Builder.Default
    @Column(name = "comment_count")
    private Integer commentCount = 0;

    @Builder.Default
    @Column(name = "vote_count")
    private Integer voteCount = 0;

    @Column(name = "reg_date")
    private LocalDateTime regDate;

    @Column(name = "mod_date")
    private LocalDateTime modDate;
    @Override
    public String toString() {
        return new StringJoiner(", ", Post.class.getSimpleName() + "[", "]")
                .add("sequence='" + sequence + "'")
                .add("member='" + member + "'")
                .add("subject='" + subject + "'")
                .add("author='" + author + "'")
                .add("content='" + content + "'")
                .add("commentCount='" + commentCount + "'")
                .add("voteCount='" + voteCount + "'")
                .add("regDate='" + regDate + "'")
                .add("modDate='" + modDate + "'")
                .toString();
    }
}
