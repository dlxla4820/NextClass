package com.nextClass.controller;

import com.nextClass.dto.MemberRequestDto;
import com.nextClass.dto.ResponseDto;
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

    @PostMapping(value = "/changeInfo")
    public ResponseEntity<ResponseDto<?>> login(@RequestBody MemberRequestDto requestBody){
        return ResponseEntity.ok(memberService.changeInfo(requestBody));
    }
}
