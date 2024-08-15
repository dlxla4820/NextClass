package com.nextClass.service;

import com.nextClass.dto.*;
import com.nextClass.entity.Comment;
import com.nextClass.entity.Member;
import com.nextClass.entity.Post;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.repository.BoardRepository;
import com.nextClass.repository.LoginRepository;
import com.nextClass.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nextClass.entity.QPost.post;
import static com.nextClass.enums.ErrorCode.TOKEN_UNAUTHORIZED;
import static java.util.Arrays.stream;

@Service
@Transactional
@Slf4j
public class BoardService {
    private final BoardRepository boardRepository;
    private final LoginRepository loginRepository;
    private final static String ANONYMOUS_NAME = "익명";
    public BoardService(BoardRepository boardRepository, LoginRepository loginRepository) {
        this.boardRepository = boardRepository;
        this.loginRepository = loginRepository;
    }


    public ResponseDto<?> savePost(PostSaveRequestDto requestBody){
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("BoardService << savePost >> | memberUuid : {}, requestBody : {}",memberUuid, requestBody);
        if(memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());
        //유효성 검사
        if(requestBody.getSubject() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "subject"));
        if(requestBody.getContent() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "content"));
        if(requestBody.getIsSecret() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "is_secret"));

        Member member = loginRepository.getMemberByUuid(memberUuid);
        String author = requestBody.getIsSecret() ? ANONYMOUS_NAME : member.getId();

        boardRepository.savePost(requestBody, member, author);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto<?> changePost(PostChangeRequestDto requestBody) {
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("BoardService << changePost >> | memberUuid : {}, requestBody : {}",memberUuid, requestBody);
        if(memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());
        //유효성 검사
        if(requestBody.getPostSequence() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "post_sequence"));
        if(requestBody.getSubject() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "subject"));
        if(requestBody.getContent() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "content"));
        if(requestBody.getIsSecret() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "is_secret"));

        Member member = loginRepository.getMemberByUuid(memberUuid);
        String author = requestBody.getIsSecret() ? ANONYMOUS_NAME : member.getId();

        Post post = boardRepository.selectPost(requestBody.getPostSequence());
        if(post == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL,ErrorCode.POST_NOT_EXIST.getErrorCode(), ErrorCode.POST_NOT_EXIST.getErrorDescription());
        if(!memberUuid.equals(post.getMember().getUuid().toString()))
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL,ErrorCode.POST_NOT_MATCH_MEMBER.getErrorCode(), ErrorCode.POST_NOT_MATCH_MEMBER.getErrorDescription());

        boardRepository.updatePost(requestBody, author);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }
    public ResponseDto<?> deletePost(PostDeleteRequestDto requestBody) {
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("BoardService << deletePost >> | memberUuid : {}, requestBody : {}",memberUuid, requestBody);
        if(memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());
        //유효성 검사
        if(requestBody.getPostSequence() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "post_sequence"));

        Post post = boardRepository.selectPost(requestBody.getPostSequence());
        if(post == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL,ErrorCode.POST_NOT_EXIST.getErrorCode(), ErrorCode.POST_NOT_EXIST.getErrorDescription());
        if(!memberUuid.equals(post.getMember().getUuid().toString()))
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL,ErrorCode.POST_NOT_MATCH_MEMBER.getErrorCode(), ErrorCode.POST_NOT_MATCH_MEMBER.getErrorDescription());

        boardRepository.deletePost(requestBody);

        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto<?> getPost(Integer postSequence) {
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("BoardService << getPost >> | memberUuid : {}, postSequence : {}",memberUuid, postSequence);
        if(memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());

        Post post = boardRepository.selectPost(postSequence);
        if(post == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL,ErrorCode.POST_NOT_EXIST.getErrorCode(), ErrorCode.POST_NOT_EXIST.getErrorDescription());
        boolean isOwner = memberUuid.equals(post.getMember().getUuid().toString());

        PostSelectResponseDto response = PostSelectResponseDto.builder()
                .postSequence(postSequence)
                .author(post.getAuthor())
                .subject(post.getSubject())
                .content(post.getContent())
                .isOwner(isOwner)
                .build();
        log.info("BoardService << getPost >> | response : {}", response);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS, response);
    }
    // Comment
    public ResponseDto<?> saveComment(CommentSaveRequestDto requestBody){
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("BoardService << saveComment >> | memberUuid : {}, requestBody : {}",memberUuid, requestBody);
        if(memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());
        //유효성 검사
        if(requestBody.getPostSequence() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "post_sequence"));
        if(requestBody.getContent() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "content"));
        if(requestBody.getIsSecret() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "is_secret"));
        Member member;
        Comment comment = boardRepository.selectCommentByMemberUuidAndPostSequence(requestBody.getPostSequence(), memberUuid);
        Comment lastComment = boardRepository.selectLastCommentByPostSequence(requestBody.getPostSequence());
        String author;
        try {
            author = lastComment == null ? ANONYMOUS_NAME + "1" : (ANONYMOUS_NAME + (Integer.parseInt(lastComment.getAuthor().substring(0, 2)) + 1));
        } catch (NumberFormatException e){
            author = ANONYMOUS_NAME + "(글쓴이)";
        }
        member = comment == null ? loginRepository.getMemberByUuid(memberUuid) : comment.getMember();
        author = comment == null ? author : comment.getAuthor();
        author = requestBody.getIsSecret() ? author : member.getId();

        boardRepository.saveComment(requestBody,member, author);

        boardRepository.updatePostCommentCount(requestBody.getPostSequence());
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto<?> changeComment(CommentChangeRequestDto requestBody){
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("BoardService << saveComment >> | memberUuid : {}, requestBody : {}",memberUuid, requestBody);
        if(memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());
        //유효성 검사
        if(requestBody.getPostSequence() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "post_sequence"));
        if(requestBody.getCommentSequence() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "comment_sequence"));
        if(requestBody.getContent() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "content"));
        if(requestBody.getIsSecret() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "is_secret"));

        Comment selectComment = boardRepository.selectComment(requestBody.getCommentSequence());
        if(selectComment == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL,ErrorCode.COMMENT_NOT_EXIST.getErrorCode(), ErrorCode.COMMENT_NOT_EXIST.getErrorDescription());
        if(!memberUuid.equals(selectComment.getMember().getUuid().toString()))
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL,ErrorCode.COMMENT_NOT_MATCH_MEMBER.getErrorCode(), ErrorCode.COMMENT_NOT_MATCH_MEMBER.getErrorDescription());

        Comment myComment = boardRepository.selectCommentByMemberUuidAndPostSequence(requestBody.getPostSequence(), memberUuid);
        Comment lastComment = boardRepository.selectLastCommentByPostSequence(requestBody.getPostSequence());
        String author;
        try {
            author = lastComment == null ? ANONYMOUS_NAME + "1" : (ANONYMOUS_NAME + (Integer.parseInt(lastComment.getAuthor().substring(0, 2)) + 1));
        } catch (NumberFormatException e){
            author = ANONYMOUS_NAME + "(글쓴이)";
        }
        author = myComment == null ? author : myComment.getAuthor();
        author = requestBody.getIsSecret() ? author : selectComment.getMember().getId();

        boardRepository.updateComment(requestBody, author);

        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }


    public ResponseDto<?> getPostList(PostListSelectRequestDto requestBody){
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("BoardService << getPostList >> | memberUuid : {}, requestBody : {}",memberUuid, requestBody);
        if(memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());

        //유효성 검사
        if(requestBody.getPostSequence() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "post_sequence"));
        if(requestBody.getSort() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "sort"));
        if(requestBody.getSize() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "size"));
        List<PostListSelectResponseDto> responseList = boardRepository.selectAllPostList(memberUuid, requestBody);

        log.info("BoardService << getPostList >> | responseList : {}", responseList);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS, responseList);
    }

    public ResponseDto<?> getCommentList(CommentListSelectRequestDto requestBody){
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("BoardService << getCommentList >> | memberUuid : {}, requestBody : {}",memberUuid, requestBody);
        if(memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());
        //유효성 검사
        if(requestBody.getPostSequence() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "post_sequence"));
        if(requestBody.getSize() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "sort"));


        List<Comment> commentList = boardRepository.selectCommentList(requestBody.getPostSequence(), requestBody.getSize());
        List<CommentListSelectResponseDto> responseList = new ArrayList<>();
        for( Comment comment : commentList){
            CommentListSelectResponseDto response = CommentListSelectResponseDto.builder()
                    .commentSequence(comment.getSequence())
                    .content(comment.getContent())
                    .author(comment.getAuthor())
                    .voteCount(comment.getVoteCount())
                    .regDate(comment.getRegDate())
                    .isOwner(memberUuid.equals(comment.getMember().getUuid().toString()))
                    .build();
            responseList.add(response);
        }
        log.info("BoardService << getCommentList >> | responseList : {}", responseList);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS, responseList);
    }


}
