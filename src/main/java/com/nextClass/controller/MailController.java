package com.nextClass.controller;

import com.nextClass.dto.EmailCheckRequestDto;
import com.nextClass.dto.MemberRequestDto;
import com.nextClass.dto.ResponseDto;
import com.nextClass.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class MailController {
    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping(value = "/mail_check")
    public ResponseEntity<ResponseDto<?>> checkMail(@RequestBody EmailCheckRequestDto requestBody){
        return ResponseEntity.ok(mailService.checkEmailCode(requestBody));
    }

    @PostMapping(value = "/mail_send")
    public ResponseEntity<ResponseDto<?>> sendMail(@RequestBody String email){
        return ResponseEntity.ok(mailService.sendEmailCode(email));
    }
}
