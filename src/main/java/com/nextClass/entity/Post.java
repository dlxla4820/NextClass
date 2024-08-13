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

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> commentList;

    private String subject;

    private String author;

    private String content;

    @Column(name = "reg_date")
    private LocalDateTime regDate;

    @Column(name = "mod_date")
    private LocalDateTime modDate;
    @Override
    public String toString() {
        return new StringJoiner(", ", Post.class.getSimpleName() + "[", "]")
                .add("sequence='" + sequence + "'")
                .add("member='" + member + "'")
                .add("commentList='" + commentList + "'")
                .add("subject='" + subject + "'")
                .add("author='" + author + "'")
                .add("content='" + content + "'")
                .add("regDate='" + regDate + "'")
                .add("modDate='" + modDate + "'")
                .toString();
    }
}
