package com.nextClass.repository;

import com.nextClass.dto.PostChangeRequestDto;
import com.nextClass.dto.PostDeleteRequestDto;
import com.nextClass.dto.PostSaveRequestDto;
import com.nextClass.dto.VoteCountDto;
import com.nextClass.entity.Member;
import com.nextClass.entity.Post;
import com.nextClass.entity.Vote;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.expression.spel.ast.Projection;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                .where(post.sequence.eq(postChangeRequestDto.getPostSequence()))
                .execute();
    }

    public void deletePost(PostDeleteRequestDto postDeleteRequestDto){
        queryFactory.delete(post)
                .where(post.sequence.eq(postDeleteRequestDto.getPostSequence()))
                .execute();
    }

    public List<Post> selectAllPostList(Integer postSequence, int size){
        return queryFactory.select(post)
                .from(post)
                .where(eqPostSequence(postSequence))
                .orderBy(post.regDate.desc())
                .limit(size)
                .fetch();
    }

    public List<VoteCountDto> selectVoteCountList(Integer postSequence, int size){
        if(size == 0)
            return Collections.emptyList();
        List<Integer> sequenceList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            sequenceList.add(postSequence - i);
        }
        return queryFactory.select(Projections.fields(VoteCountDto.class, vote.boardSequence, vote.count().as("vote_count")))
                .from(vote)
                .where(vote.boardSequence.in(sequenceList).and(vote.boardType.eq(Vote.BoardType.POST)))
                .groupBy(vote.boardSequence)
                .fetch();
    }
    private BooleanExpression eqPostSequence(Integer postSequence){
        if(postSequence == null)
            return null;
        return post.sequence.lt(postSequence);
    }

    public Post selectPost(Integer sequence){
        return queryFactory.selectFrom(post)
                .where(post.sequence.eq(sequence))
                .fetchOne();
    }


    private Member selectMember(String uuid){
        return queryFactory.selectFrom(member)
                .where(Expressions.stringTemplate("HEX({0})", member.uuid).eq(uuid.replace("-","")))
                .fetchOne();
    }
}
