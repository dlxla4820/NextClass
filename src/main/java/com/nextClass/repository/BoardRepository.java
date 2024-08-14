package com.nextClass.repository;

import com.nextClass.dto.*;
import com.nextClass.entity.Member;
import com.nextClass.entity.Post;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.nextClass.entity.QMember.member;
import static com.nextClass.entity.QPost.post;

@Repository
@Transactional
public class BoardRepository {

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    private final VoteRepository voteRepository;
    private final JPAQueryFactory queryFactory;
    private final static int compareVoteCount = 10;
    private final static String MY_SCHOOL = "my_school";
    private final static String VOTE = "vote";

    public BoardRepository(PostRepository postRepository, CommentRepository commentRepository, VoteRepository voteRepository, JPAQueryFactory queryFactory) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.voteRepository = voteRepository;
        this.queryFactory = queryFactory;
    }


    public void savePost(PostSaveRequestDto postSaveRequestDto, String uuid, String author) {
        Post post = Post.builder()
                .member(selectMember(uuid))
                .subject(postSaveRequestDto.getSubject())
                .content(postSaveRequestDto.getContent())
                .author(author)
                .regDate(LocalDateTime.now())
                .build();
        postRepository.save(post);
    }

    public void updatePost(PostChangeRequestDto postChangeRequestDto, String author) {
        queryFactory.update(post)
                .set(post.subject, postChangeRequestDto.getSubject())
                .set(post.content, postChangeRequestDto.getContent())
                .set(post.author, author)
                .set(post.modDate, LocalDateTime.now())
                .where(post.sequence.eq(postChangeRequestDto.getPostSequence()))
                .execute();
    }

    public void deletePost(PostDeleteRequestDto postDeleteRequestDto) {
        queryFactory.delete(post)
                .where(post.sequence.eq(postDeleteRequestDto.getPostSequence()))
                .execute();
    }

    public List<PostListSelectResponseDto> selectAllPostList(String memberUuid, PostListSelectRequestDto postListSelectRequestDto) {
        JPAQuery<PostListSelectResponseDto> query = queryFactory.select(Projections.fields(PostListSelectResponseDto.class, post.sequence.as("postSequence"), post.subject, post.content, post.author, post.voteCount, post.commentCount, post.regDate)).from(post);
        if(MY_SCHOOL.equals(postListSelectRequestDto.getSort()))
            query.join(member).on(member.uuid.eq(post.member.uuid));
        return query
                .where(eqPostSequence(postListSelectRequestDto.getPostSequence()))
                .where(eqMemberSchool(memberUuid, postListSelectRequestDto.getSort()))
                .where(goeVoteCount(postListSelectRequestDto.getSort()))
                .orderBy(post.regDate.desc())
                .limit(postListSelectRequestDto.getSize())
                .fetch();
    }


    private BooleanExpression eqMemberSchool(String memberUuid, String sort){
        if(MY_SCHOOL.equals(sort)){
            String memberSchool = selectMember(memberUuid).getMemberSchool();
            return member.memberSchool.eq(memberSchool);
        }
        return null;
    }
    private BooleanExpression goeVoteCount(String sort) {
        if (VOTE.equals(sort))
            return post.voteCount.goe(compareVoteCount).and(post.regDate.goe(LocalDate.now().atStartOfDay()));
        return null;
    }
    private BooleanExpression eqPostSequence(Integer postSequence) {
        if (postSequence == null)
            return null;
        return post.sequence.gt(postSequence);
    }

    public Post selectPost(Integer sequence) {
        return queryFactory.selectFrom(post)
                .where(post.sequence.eq(sequence))
                .fetchOne();
    }


    private Member selectMember(String uuid) {
        if(uuid == null)
            return null;
        return queryFactory.selectFrom(member)
                .where(Expressions.stringTemplate("HEX({0})", member.uuid).eq(uuid.replace("-", "")))
                .fetchOne();
    }
}
