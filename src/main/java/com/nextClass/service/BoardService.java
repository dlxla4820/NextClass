package com.nextClass.service;

import com.nextClass.dto.MemberRequestDto;
import com.nextClass.dto.PostChangeRequestDto;
import com.nextClass.dto.PostSaveRequestDto;
import com.nextClass.dto.ResponseDto;
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

import static com.nextClass.enums.ErrorCode.TOKEN_UNAUTHORIZED;

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
        if(requestBody.getPostId() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "post_id"));
        if(requestBody.getSubject() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "subject"));
        if(requestBody.getContent() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "content"));
        if(requestBody.getIsSecret() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "is_secret"));


    }
}
