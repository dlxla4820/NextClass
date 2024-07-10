package com.nextClass.controller;

import com.nextClass.dto.*;
import com.nextClass.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping(value = "/change_info")
    public ResponseEntity<ResponseDto<?>> changeInfo(@RequestBody MemberChangeNormalInfoRequestDto requestBody){
        return ResponseEntity.ok(memberService.changeNormalInfo(requestBody));
    }

    @PostMapping(value = "/change_password")
    public ResponseEntity<ResponseDto<?>> changePassword(@RequestBody MemberChangePasswordRequestDto requestBody){
        return ResponseEntity.ok(memberService.changePassword(requestBody));
    }

    @PostMapping(value = "/change_email")
    public ResponseEntity<ResponseDto<?>> changeEmail(@RequestBody MemberChangeEmailRequestDto requestBody){
        return ResponseEntity.ok(memberService.changeEmail(requestBody));
    }

    @PostMapping(value = "/my_info")
    public ResponseEntity<ResponseDto<?>> getMyInfo(){
        return ResponseEntity.ok(memberService.getMyInfo());
    }
}
