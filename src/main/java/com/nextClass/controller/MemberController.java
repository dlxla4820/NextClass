package com.nextClass.controller;

import com.nextClass.dto.*;
import com.nextClass.service.MailService;
import com.nextClass.service.MemberService;
import com.nextClass.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class MemberController {
    private final MailService mailService;
    private final MemberService memberService;
    private final NotificationService notificationService;

    public MemberController(MailService mailService, MemberService memberService, NotificationService notificationService) {
        this.mailService = mailService;
        this.memberService = memberService;
        this.notificationService = notificationService;
    }

    @PostMapping(value = "/info_change")
    public ResponseEntity<ResponseDto<?>> changeInfo(@RequestBody MemberChangeNormalInfoRequestDto requestBody){
        return ResponseEntity.ok(memberService.changeNormalInfo(requestBody));
    }

    @PostMapping(value = "/password_change")
    public ResponseEntity<ResponseDto<?>> changePassword(@RequestBody MemberChangePasswordRequestDto requestBody){
        return ResponseEntity.ok(memberService.changePassword(requestBody));
    }

    @PostMapping(value = "/email_change")
    public ResponseEntity<ResponseDto<?>> changeEmail(@RequestBody MemberChangeEmailRequestDto requestBody){
        return ResponseEntity.ok(memberService.changeEmail(requestBody));
    }

    @PostMapping(value = "/my_info")
    public ResponseEntity<ResponseDto<?>> getMyInfo(){
        return ResponseEntity.ok(memberService.getMyInfo());
    }

    @PostMapping(value = "/id_delete")
    public ResponseEntity<ResponseDto<?>> deleteMember(@RequestBody MemberDeleteRequestDto requestBody){
        return ResponseEntity.ok(memberService.deleteMember(requestBody));
    }

    @PostMapping(value = "/changed_mail_send")
    public ResponseEntity<ResponseDto<?>> sendChangeEmail(@RequestBody EmailSendChangeCodeRequestDto requestBody){
        return ResponseEntity.ok(mailService.sendChangeEmailCreateCode(requestBody));
    }
    @PostMapping(value = "/id_find")
    public ResponseEntity<ResponseDto<?>> findMemberId(@RequestBody EmailSendMemberIdDto requestBody){
        return ResponseEntity.ok(mailService.sendEmailMemberId(requestBody));
    }
    @PostMapping(value = "/password_find")
    public ResponseEntity<ResponseDto<?>> findPassword(@RequestBody EmailSendPasswordDto requestBody){
        return ResponseEntity.ok(mailService.sendEmailRandomPassword(requestBody));
    }

    @GetMapping(value = "/notification_config")
    public ResponseEntity<ResponseDto<?>> getNotificationConfig(){
        return ResponseEntity.ok(notificationService.getNotificationConfig());
    }
    @GetMapping(value = "/notification_config_update")
    public ResponseEntity<ResponseDto<?>> updateNotificationConfig(@RequestBody NotificationConfigRequestDto requestBody){
        return ResponseEntity.ok(notificationService.updateNotificationConfig(requestBody));
    }
}
