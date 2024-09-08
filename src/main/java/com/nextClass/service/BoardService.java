package com.nextClass.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.nextClass.dto.*;
import com.nextClass.entity.Comment;
import com.nextClass.entity.Member;
import com.nextClass.entity.Post;
import com.nextClass.entity.Vote;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.repository.BoardRepository;
import com.nextClass.repository.LoginRepository;
import com.nextClass.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.nextClass.enums.ErrorCode.MEMBER_NOT_EXIST;
import static com.nextClass.enums.ErrorCode.TOKEN_UNAUTHORIZED;
import static java.util.Arrays.stream;

@Service
@Transactional
@Slf4j
public class BoardService {
    private final BoardRepository boardRepository;
    private final LoginRepository loginRepository;
    private final AndroidPushNotificationService androidPushNotificationService;
    private final static String ANONYMOUS_NAME = "익명";
    private final static String PUSH_NOTIFICATION_TITLE = "board";
    private final static String PUSH_NOTIFICATION_BODY = "회원님께서 작성하신 게시글에 새로운 댓글이 달렸어요.";
    private final static String NOTIFICATION_CATEGORY_COMMENT = "comment_notification";
    public BoardService(BoardRepository boardRepository, LoginRepository loginRepository, AndroidPushNotificationService androidPushNotificationService) {
        this.boardRepository = boardRepository;
        this.loginRepository = loginRepository;
        this.androidPushNotificationService = androidPushNotificationService;
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
        if(member == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(),Description.FAIL, MEMBER_NOT_EXIST.getErrorCode(), MEMBER_NOT_EXIST.getErrorDescription());
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

        boardRepository.deleteCommentByPost(requestBody);
        boardRepository.deletePost(requestBody);
        boardRepository.deleteVoteByBoardSequence(requestBody.getPostSequence(), Vote.BoardType.POST, memberUuid);
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

        Vote vote = boardRepository.selectVoteByBoardSequence(postSequence, Vote.BoardType.POST, memberUuid);
        boolean isVote = vote != null;

        PostSelectResponseDto response = PostSelectResponseDto.builder()
                .postSequence(postSequence)
                .author(post.getAuthor())
                .subject(post.getSubject())
                .content(post.getContent())
                .isOwner(isOwner)
                .isVote(isVote)
                .commentCount(post.getCommentCount())
                .voteCount(post.getVoteCount())
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
        boardRepository.updatePostCommentCount(requestBody.getPostSequence(), 1);

        // 푸시 알람
        NotificationRequiredDataDto<?> notificationRequiredDataDto = boardRepository.selectPostAndNotificationByMemberUuid(requestBody.getPostSequence(), NOTIFICATION_CATEGORY_COMMENT);
        if(notificationRequiredDataDto.getIsNotificationActivated()) {
            try {
                Map<String, String> data = new HashMap<>();
                data.put("title", PUSH_NOTIFICATION_TITLE);
                data.put("body", PUSH_NOTIFICATION_BODY);
                data.put("sequence", notificationRequiredDataDto.getData().toString());
                androidPushNotificationService.sendFcmDataToFirebase(data, notificationRequiredDataDto.getAppToken());
            } catch (FirebaseMessagingException e) {
                log.error("BoardService << saveComment >> | Exception : {}", e.getMessage());
                return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), Description.FAIL);
            }
        }
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

    public ResponseDto<?> deleteComment(CommentDeleteRequestDto requestBody){
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("BoardService << deleteComment >> | memberUuid : {}, requestBody : {}",memberUuid, requestBody);

        if(memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());
        //유효성 검사
        if(requestBody.getPostSequence() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "post_sequence"));
        if(requestBody.getCommentSequence() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "comment_sequence"));

        Comment comment = boardRepository.selectComment(requestBody.getCommentSequence());
        if(comment == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL,ErrorCode.COMMENT_NOT_EXIST.getErrorCode(), ErrorCode.COMMENT_NOT_EXIST.getErrorDescription());
        if(!memberUuid.equals(comment.getMember().getUuid().toString()))
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL,ErrorCode.COMMENT_NOT_MATCH_MEMBER.getErrorCode(), ErrorCode.COMMENT_NOT_MATCH_MEMBER.getErrorDescription());

        boardRepository.deleteComment(requestBody);
        boardRepository.updatePostCommentCount(requestBody.getPostSequence(), -1);
        boardRepository.deleteVoteByBoardSequence(requestBody.getCommentSequence(), Vote.BoardType.COMMENT, memberUuid);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto<?> saveOrDeleteVote(VoteSaveOrDeleteRequestDto requestBody){
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("BoardService << saveVote >> | memberUuid : {}, requestBody : {}",memberUuid, requestBody);

        if(memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());
        //유효성 검사
        if(requestBody.getPostSequence() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "comment_sequence"));
        Integer boardSequence = requestBody.getCommentSequence() == null ? requestBody.getPostSequence() : requestBody.getCommentSequence();
        Vote.BoardType boardType = requestBody.getCommentSequence() == null ? Vote.BoardType.POST : Vote.BoardType.COMMENT;
        Member member = loginRepository.getMemberByUuid(memberUuid);
        if(member == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(),Description.FAIL, MEMBER_NOT_EXIST.getErrorCode(), MEMBER_NOT_EXIST.getErrorDescription());

        Vote vote = boardRepository.selectVoteByBoardSequence(boardSequence, boardType, memberUuid);
        if(vote == null) {
            boardRepository.saveVote(requestBody, member);
            boardRepository.updateVoteCount(boardSequence, boardType, 1);
        } else {
            boardRepository.deleteVoteByUuid(vote.getUuid().toString());
            boardRepository.updateVoteCount(boardSequence, boardType, -1);
        }
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }


    public ResponseDto<?> getPostList(PostListSelectRequestDto requestBody){
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("BoardService << getPostList >> | memberUuid : {}, requestBody : {}",memberUuid, requestBody);
        if(memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());

        //유효성 검사
        if(requestBody.getSort() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "sort"));
        if(requestBody.getSize() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "size"));
        if(requestBody.getSearchWord() != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.JSON_INVALID.getErrorCode(), ErrorCode.REQUEST_BODY_NULL.getErrorDescription());
        List<PostListSelectResponseDto> responseList = boardRepository.selectAllPostList(memberUuid, requestBody);
        for(PostListSelectResponseDto data : responseList){
            if(data.getSubject().length() >10)
                data.setSubject(data.getSubject().substring(0,7) + "...");
            if(data.getContent().length() >25)
                data.setContent(data.getContent().substring(0,22) + "...");
        }
        log.info("BoardService << getPostList >> | responseList : {}", responseList);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS, responseList);
    }

    public ResponseDto<?> searchPostList(PostListSelectRequestDto requestBody){
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("BoardService << searchPostList >> | memberUuid : {}, requestBody : {}",memberUuid, requestBody);
        if(memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());
        //유효성 검사
        if(requestBody.getSort() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "sort"));
        if(requestBody.getSize() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "size"));
        if(requestBody.getSearchWord() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "search_word"));
        List<PostListSelectResponseDto> responseList = boardRepository.selectAllPostList(memberUuid, requestBody);

        for(PostListSelectResponseDto data : responseList){
            if(data.getSubject().length() >10)
                data.setSubject(data.getSubject().substring(7) + "...");
            if(data.getContent().length() >25)
                data.setContent(data.getContent().substring(22) + "...");
        }

        log.info("BoardService << searchPostList >> | responseList : {}", responseList);
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


        List<Comment> commentList = boardRepository.selectCommentList(requestBody);
        List<Integer> commentSequenceList = commentList
                .stream()
                .map(Comment::getSequence)
                .toList();
        List<Vote> voteList = boardRepository.selectVoteListByBoardSequence(commentSequenceList, Vote.BoardType.COMMENT, memberUuid);
        Set<Integer> voteSet = voteList.stream()
                .map(Vote::getBoardSequence)
                .collect(Collectors.toSet());
        List<CommentListSelectResponseDto> responseList = new ArrayList<>();
        for( Comment comment : commentList){
            CommentListSelectResponseDto response = CommentListSelectResponseDto.builder()
                    .commentSequence(comment.getSequence())
                    .content(comment.getContent())
                    .author(comment.getAuthor())
                    .voteCount(comment.getVoteCount())
                    .regDate(comment.getRegDate())
                    .isOwner(memberUuid.equals(comment.getMember().getUuid().toString()))
                    .isVote(voteSet.contains(comment.getSequence()))
                    .build();
            responseList.add(response);
        }
        log.info("BoardService << getCommentList >> | responseList : {}", responseList);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS, responseList);
    }

}
