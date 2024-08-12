package com.nextClass.repository;

import com.nextClass.dto.PostChangeRequestDto;
import com.nextClass.dto.PostDeleteRequestDto;
import com.nextClass.dto.PostSaveRequestDto;
import com.nextClass.entity.Member;
import com.nextClass.entity.Post;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.nextClass.entity.QMember.member;
import static com.nextClass.entity.QPost.post;

@Repository
@Transactional
public class BoardRepository {

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    private final VoteRepository voteRepository;
    private final JPAQueryFactory queryFactory;


    public BoardRepository(PostRepository postRepository, CommentRepository commentRepository, VoteRepository voteRepository, JPAQueryFactory queryFactory) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.voteRepository = voteRepository;
        this.queryFactory = queryFactory;
    }


    public void savePost(PostSaveRequestDto postSaveRequestDto,String uuid, String author){
        Post post = Post.builder()
                .member(selectMember(uuid))
                .subject(postSaveRequestDto.getSubject())
                .content(postSaveRequestDto.getContent())
                .author(author)
                .regDate(LocalDateTime.now())
                .build();
        postRepository.save(post);
    }

    public void updatePost(PostChangeRequestDto postChangeRequestDto, String author){
        queryFactory.update(post)
                .set(post.subject, postChangeRequestDto.getSubject())
                .set(post.content, postChangeRequestDto.getContent())
                .set(post.author, author)
                .set(post.modDate, LocalDateTime.now())
                .where(Expressions.stringTemplate("HEX({0})", post.uuid).eq(postChangeRequestDto.getPostId().replace("-","")))
                .execute();
    }

    public void deletePost(PostDeleteRequestDto postDeleteRequestDto){
        queryFactory.delete(post)
                .where(Expressions.stringTemplate("HEX({0})", post.uuid).eq(postDeleteRequestDto.getPostId().replace("-","")))
                .execute();
    }



    public Post selectPostByUuid(String uuid){
        return queryFactory.selectFrom(post)
                .where(Expressions.stringTemplate("HEX({0})", post.uuid).eq(uuid.replace("-","")))
                .fetchOne();
    }


    private Member selectMember(String uuid){
        return queryFactory.selectFrom(member)
                .where(Expressions.stringTemplate("HEX({0})", member.uuid).eq(uuid.replace("-","")))
                .fetchOne();
    }
}
