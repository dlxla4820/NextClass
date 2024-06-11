package com.nextClass.service;

import com.nextClass.dto.MemberRequestDto;
import com.nextClass.dto.ResponseDto;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import static com.nextClass.utils.CommonUtils.checkLength;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final static String idError = "id(id)가 유효하지 않습니다.";


    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public ResponseDto<?> saveMember(MemberRequestDto requestBody){
        ErrorCode errorCode = checkMemberData(requestBody);
        if(errorCode != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,errorCode);

        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    private ErrorCode checkMemberData(MemberRequestDto memberRequestDto){
        // id check
        if(memberRequestDto.getId().isBlank())
            return ErrorCode.ID_INVALID;
        if(checkId(memberRequestDto.getId()))
            return ErrorCode.ID_INVALID;
        // name check
        if(memberRequestDto.getName().isBlank())
            return ErrorCode.NAME_INVALID;
        if(checkName(memberRequestDto.getName()))
            return ErrorCode.NAME_INVALID;
        // password check
        if(memberRequestDto.getPassword().isBlank())
            return ErrorCode.PASSWORD_INVALID;
        if(checkPassword(memberRequestDto.getPassword()))
            return ErrorCode.PASSWORD_INVALID;
        // email check
        return null;
    }

    private boolean checkId(String id){
        return !checkLength(id,5,20) || !Pattern.compile("[a-zA-Z0-9]+").matcher(id).find();
    }
    private boolean checkName(String name){
        return !checkLength(name, 2, 11) || !Pattern.compile("^[가-힣]*$").matcher(name).find();
    }
    private boolean checkPassword(String password) {
        return !checkLength(password, 8, 16) // 길이 체크
                || !Pattern.compile("(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?])").matcher(password).find() // 숫자, 영어, 특수문자 체크
                || Pattern.compile("^[가-힣]*$").matcher(password).find(); // 한글 체크
    }



}
