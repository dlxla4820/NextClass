package com.nextClass.service;

import com.nextClass.dto.MemberRequestDto;
import com.nextClass.dto.ResponseDto;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
        if(memberRequestDto.getId().isBlank())
            return ErrorCode.ID_INVALID;


        return null;
    }


}
