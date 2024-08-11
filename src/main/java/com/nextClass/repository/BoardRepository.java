package com.nextClass.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class BoardRepository {

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    private final VoteRepository voteRepository;


    public BoardRepository(PostRepository postRepository, CommentRepository commentRepository, VoteRepository voteRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.voteRepository = voteRepository;
    }
}
