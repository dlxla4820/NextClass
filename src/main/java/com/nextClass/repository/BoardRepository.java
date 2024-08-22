package com.nextClass.repository;

import com.nextClass.dto.*;
import com.nextClass.entity.Comment;
import com.nextClass.entity.Member;
import com.nextClass.entity.Post;
import com.nextClass.entity.Vote;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.nextClass.entity.QComment.comment;
import static com.nextClass.entity.QMember.member;
import static com.nextClass.entity.QPost.post;
import static com.nextClass.entity.QVote.vote;

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
    private final static String MY_POST = "my_post";
    private final static String MY_VOTE = "my_vote";
    private final static String MY_COMMENT = "my_comment";
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
    public void saveVote(VoteSaveOrDeleteRequestDto voteSaveRequestDto, Member member){
        Vote vote = Vote.builder()
                .member(member)
                .boardSequence(voteSaveRequestDto.getCommentSequence() == null ? voteSaveRequestDto.getPostSequence() : voteSaveRequestDto.getCommentSequence())
                .boardType(voteSaveRequestDto.getCommentSequence() == null ? Vote.BoardType.POST : Vote.BoardType.COMMENT)
                .regDate(LocalDateTime.now())
                .build();
        voteRepository.save(vote);
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

    public void updatePostCommentCount(int postSequence, int count){
        queryFactory.update(post)
                .set(post.commentCount, post.commentCount.add(count))
                .where(post.sequence.eq(postSequence))
                .execute();
    }
    public void updateVoteCount(int boardSequence, Vote.BoardType boardType, int count){
        if (boardType == Vote.BoardType.POST){
            queryFactory.update(post)
                    .set(post.voteCount, post.voteCount.add(count))
                    .where(post.sequence.eq(boardSequence))
                    .execute();
        } else {
            queryFactory.update(comment)
                    .set(comment.voteCount, comment.voteCount.add(count))
                    .where(comment.sequence.eq(boardSequence))
                    .execute();
        }
    }

    public void deletePost(PostDeleteRequestDto postDeleteRequestDto) {
        queryFactory.delete(post)
                .where(post.sequence.eq(postDeleteRequestDto.getPostSequence()))
                .execute();
    }

    public void deleteComment(CommentDeleteRequestDto commentDeleteRequestDto){
        queryFactory.delete(comment)
                .where(comment.sequence.eq(commentDeleteRequestDto.getCommentSequence()))
                .execute();

    }
    public void deleteCommentByPost(PostDeleteRequestDto postDeleteRequestDto){
        queryFactory.delete(comment)
                .where(comment.post.sequence.eq(postDeleteRequestDto.getPostSequence()))
                .execute();
    }

    public void deleteVoteByUuid(String voteUuid){
        queryFactory.delete(vote)
                .where(Expressions.stringTemplate("HEX({0})", vote.uuid).eq(voteUuid.replace("-", "")))
                .execute();
    }
    public void deleteVoteByBoardSequence(int boardSequence, Vote.BoardType boardType, String memberUuid){
        queryFactory.delete(vote)
                .where(vote.boardSequence.eq(boardSequence))
                .where(vote.boardType.eq(boardType))
                .where(Expressions.stringTemplate("HEX({0})", vote.member.uuid).eq(memberUuid.replace("-", "")))
                .execute();
    }

    public List<PostListSelectResponseDto> selectAllPostList(String memberUuid, PostListSelectRequestDto postListSelectRequestDto) {
        JPAQuery<PostListSelectResponseDto> query = queryFactory.select(Projections.fields(PostListSelectResponseDto.class, post.sequence.as("postSequence"), post.subject, post.content, post.author, post.voteCount, post.commentCount, post.regDate)).from(post);
        if(MY_SCHOOL.equals(postListSelectRequestDto.getSort()))
            query.join(member).on(member.uuid.eq(post.member.uuid));
        if(MY_COMMENT.equals(postListSelectRequestDto.getSort()))
            query.join(comment).on(comment.post.sequence.eq(post.sequence));
        if(MY_VOTE.equals(postListSelectRequestDto.getSort()))
            query.join(vote).on(vote.member.uuid.eq(post.member.uuid));
        return query
                .where(eqPostSequence(postListSelectRequestDto.getPostSequence())) // ALL 인경우
                .where(eqMemberSchool(memberUuid, postListSelectRequestDto.getSort())) // MY_SCHOOL 인경우
                .where(goeVoteCount(postListSelectRequestDto.getSort())) // VOTE 인경우
                .where(eqMyPost(memberUuid, postListSelectRequestDto.getSort())) // MY_POST 인경우
                .where(eqMyComment(memberUuid, postListSelectRequestDto.getSort())) //MY_COMMENT 인경우
                .where(eqMyVote(memberUuid, postListSelectRequestDto.getSort())) //MY_VOTE 인경우
                .orderBy(post.regDate.desc())
                .limit(postListSelectRequestDto.getSize())
                .fetch();
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

    public List<Comment> selectCommentList(CommentListSelectRequestDto commentListSelectRequestDto){
        return queryFactory.selectFrom(comment)
                .where(comment.post.sequence.eq(commentListSelectRequestDto.getPostSequence()))
                .where(eqCommentSequence(commentListSelectRequestDto.getCommentSequence()))
                .orderBy(comment.regDate.asc())
                .limit(commentListSelectRequestDto.getSize())
                .fetch();
    }

    public Vote selectVoteByBoardSequence(Integer BoardSequence, Vote.BoardType boardType, String memberUuid){
        return queryFactory.selectFrom(vote)
                .where(vote.boardSequence.eq(BoardSequence))
                .where(vote.boardType.eq(boardType))
                .where(Expressions.stringTemplate("HEX({0})", vote.member.uuid).eq(memberUuid.replace("-", "")))
                .fetchOne();
    }

    public List<Vote> selectVoteListByBoardSequence(List<Integer> BoardSequenceList, Vote.BoardType boardType, String memberUuid){
        return queryFactory.selectFrom(vote)
                .where(vote.boardSequence.in(BoardSequenceList))
                .where(vote.boardType.eq(boardType))
                .where(Expressions.stringTemplate("HEX({0})", vote.member.uuid).eq(memberUuid.replace("-", "")))
                .fetch();
    }

    private BooleanExpression eqMemberSchool(String memberUuid, String sort){
        if(MY_SCHOOL.equals(sort)){
            String memberSchool = selectMember(memberUuid).getMemberSchool();
            return member.memberSchool.eq(memberSchool);
        }
        return null;
    }
    private BooleanExpression eqMyPost(String memberUuid, String sort){
        if(MY_POST.equals(sort))
            return Expressions.stringTemplate("HEX({0})", post.member.uuid).eq(memberUuid.replace("-", ""));
        return null;
    }
    private BooleanExpression eqMyComment(String memberUuid, String sort){
        if(MY_COMMENT.equals(sort))
            return Expressions.stringTemplate("HEX({0})", comment.member.uuid).eq(memberUuid.replace("-", ""));
        return null;
    }
    private BooleanExpression eqMyVote(String memberUuid, String sort){
        if(MY_VOTE.equals(sort))
            return Expressions.stringTemplate("HEX({0})", vote.member.uuid).eq(memberUuid.replace("-", ""));
        return null;
    }
    private BooleanExpression goeVoteCount(String sort) {
        if (VOTE.equals(sort))
            return post.voteCount.goe(compareVoteCount).and(post.regDate.goe(LocalDate.now().atStartOfDay()));
        return null;
    }
    private BooleanExpression eqPostSequence(Integer postSequence) {
        if (postSequence == null)
            return post.sequence.gt(0);
        return post.sequence.lt(postSequence);
    }
    private BooleanExpression eqCommentSequence(Integer commentSequence) {
        if (commentSequence == null)
            return comment.sequence.gt(0);
        return comment.sequence.lt(commentSequence);
    }
    private BooleanExpression eqBoardSequence(Integer postSequence, Integer commentSequence){
        if(commentSequence ==null)
            return vote.boardSequence.eq(postSequence).and(vote.boardType.eq(Vote.BoardType.POST));
        return vote.boardSequence.eq(commentSequence).and(vote.boardType.eq(Vote.BoardType.COMMENT));
    }

    private Member selectMember(String memberUuid) {
        if(memberUuid == null)
            return null;
        return queryFactory.selectFrom(member)
                .where(Expressions.stringTemplate("HEX({0})", member.uuid).eq(memberUuid.replace("-", "")))
                .fetchOne();
    }


}
