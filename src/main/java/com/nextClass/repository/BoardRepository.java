package com.nextClass.repository;

import com.nextClass.dto.*;
import com.nextClass.entity.Comment;
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

import static com.nextClass.entity.QComment.comment;
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


    public void savePost(PostSaveRequestDto postSaveRequestDto, Member member, String author) {
        Post post = Post.builder()
                .member(member)
                .subject(postSaveRequestDto.getSubject())
                .content(postSaveRequestDto.getContent())
                .author(author)
                .regDate(LocalDateTime.now())
                .build();
        postRepository.save(post);
    }

    public void saveComment(CommentSaveRequestDto commentSaveRequestDto,Member member, String author){
        Comment comment = Comment.builder()
                .member(member)
                .post(selectPost(commentSaveRequestDto.getPostSequence()))
                .author(author)
                .content(commentSaveRequestDto.getContent())
                .regDate(LocalDateTime.now())
                .build();
        commentRepository.save(comment);
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
    public void updateComment(CommentChangeRequestDto commentChangeRequestDto, String author) {
        queryFactory.update(comment)
                .set(comment.content, commentChangeRequestDto.getContent())
                .set(comment.author, author)
                .set(comment.modDate, LocalDateTime.now())
                .where(comment.sequence.eq(commentChangeRequestDto.getPostSequence()))
                .execute();
    }

    public void updatePostCommentCount(int postSequence){
        queryFactory.update(post)
                .set(post.commentCount, post.commentCount.add(1))
                .where(post.sequence.eq(postSequence))
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
        if(sequence == null)
            return null;
        return queryFactory.selectFrom(post)
                .where(post.sequence.eq(sequence))
                .fetchOne();
    }

    public Comment selectComment(Integer CommentSequence){
        if(CommentSequence == null)
            return null;
        return queryFactory.selectFrom(comment)
                .where(comment.sequence.eq(CommentSequence))
                .fetchOne();
    }
    public Comment selectCommentByMemberUuidAndPostSequence(int postSequence, String memberUuid){
        if(memberUuid == null)
            return null;
        return queryFactory.selectFrom(comment)
                .where(Expressions.stringTemplate("HEX({0})", comment.member.uuid).eq(memberUuid.replace("-", "")))
                .where(comment.post.sequence.eq(postSequence).and(comment.author.startsWith("익명")))
                .fetchOne();
    }
    public Comment selectLastCommentByPostSequence(int postSequence){
        return queryFactory.selectFrom(comment)
                .where(comment.sequence.eq(postSequence)
                        .and(comment.author.startsWith("익명")))
                .orderBy(comment.regDate.desc())
                .fetchFirst();
    }

    public List<Comment> selectCommentList(int postSequence, int size){
        return queryFactory.select(comment)
                .where(comment.sequence.eq(postSequence))
                .orderBy(comment.regDate.asc())
                .limit(size)
                .fetch();
    }


    private Member selectMember(String memberUuid) {
        if(memberUuid == null)
            return null;
        return queryFactory.selectFrom(member)
                .where(Expressions.stringTemplate("HEX({0})", member.uuid).eq(memberUuid.replace("-", "")))
                .fetchOne();
    }



}
