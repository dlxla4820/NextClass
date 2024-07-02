package com.nextClass.service;

import com.nextClass.dto.EmailCheckRequestDto;
import com.nextClass.dto.ResponseDto;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.repository.LoginRepository;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MailService {
    private final LoginRepository loginRepository;

    private final JavaMailSender javaMailSender;

    private Map<String, String> emailCheck;

    public MailService(LoginRepository loginRepository, JavaMailSender javaMailSender) {
        this.loginRepository = loginRepository;
        this.javaMailSender = javaMailSender;
    }


    public ResponseDto<?> checkEmailCode(EmailCheckRequestDto requestBody){




        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    private void sendEmailCode(EmailCheckRequestDto requestBody){

    }


//    private String createCode(){
//
//    }
}
