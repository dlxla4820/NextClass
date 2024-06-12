package com.nextClass.controller;

import com.nextClass.dto.MemberRequestDto;
import com.nextClass.dto.ResponseDto;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.service.MemberService;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoginController {

    private final MemberService memberService;
    @Autowired
    public LoginController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<ResponseDto<?>> register(@RequestBody MemberRequestDto requestBody){
        return ResponseEntity.ok(memberService.saveMember(requestBody));
    }
    @PostMapping(value = "/duplicated_check")
    public ResponseEntity<ResponseDto<?>> duplicatedCheck(@RequestBody Map<String, String> checkMapData){
        return ResponseEntity.ok(memberService.checkDuplicatedMemberData(checkMapData));
    }


    @GetMapping(value = "/register")
    public ResponseEntity<?> test(){
        return ResponseEntity.ok("test");
    }

}
