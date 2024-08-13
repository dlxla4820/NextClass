package com.nextClass.service;

import com.nextClass.dto.*;
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
        boardRepository.savePost(requestBody, memberUuid, author);

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
                .name(post.getAuthor())
                .subject(post.getSubject())
                .content(post.getContent())
                .isOwner(isOwner)
                .build();
        log.info("BoardService << getPost >> | response : {}", response);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS, response);
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
        List<Post> postList = boardRepository.selectAllPostList(requestBody.getPostSequence(), requestBody.getSize());
        if(postList.size() == 0)
            return null;
        List<VoteCountDto> voteCountDtoList = boardRepository.selectVoteCountList(postList.get(0).getSequence(), postList.size());
        Map<Integer, Long> voteCountMap = voteCountDtoList.stream()
                .collect(Collectors.toMap(
                        VoteCountDto::getBoardSequence, // Key: post sequence
                        VoteCountDto::getVoteCount     // Value: vote count
                ));
        for (Post post : postList){

        }
    }



}
