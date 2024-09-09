package com.nextClass.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@Setter
@Table(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sequence;

    @ManyToOne
    @JoinColumn(name = "member_uuid")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "post_sequence")
    private Post post;

    private String author;

    private String content;

    @Builder.Default
    @Column(name = "vote_count")
    private Integer voteCount = 0;

    @Column(name = "reg_date")
    private LocalDateTime regDate;

    @Column(name = "mod_date")
    private LocalDateTime modDate;
}
