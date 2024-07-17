package com.nextClass.service;

import com.nextClass.dto.EmailCheckRequestDto;
import com.nextClass.dto.ResponseDto;
import com.nextClass.entity.MailValidation;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.repository.LoginRepository;
import com.nextClass.repository.MailRepository;
import com.nextClass.utils.CommonUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;

@Service
@Slf4j
public class MailService {
    private final LoginRepository loginRepository;

    private final JavaMailSender javaMailSender;

    private final MailRepository mailRepository;
    private static final String subject = "다음 수업 : 계정 인증";
    private static final String context = " 코드를 입력하여 계정이 본인 소유임을 인증하여 주시기 바랍니다.\n" +
            "\n" +
            "중요: 인증번호는 3분후에 만료됩니다. 3분 내로 입력하여 주시기 바랍니다.";
    public MailService(LoginRepository loginRepository, JavaMailSender javaMailSender, MailRepository mailRepository) {
        this.loginRepository = loginRepository;
        this.javaMailSender = javaMailSender;
        this.mailRepository = mailRepository;
    }


    public ResponseDto<?> checkEmailCode(EmailCheckRequestDto requestBody){
        //TODO : 에러코드 추가, 로그 입력
        log.info("MailService <checkEmailCode> requestBody : {}", requestBody);
        if(requestBody.getEmail() == null)
            return new ResponseDto<>(HttpStatus.OK.value(), Description.FAIL);
        if(requestBody.getCode() == null)
            return new ResponseDto<>(HttpStatus.OK.value(), Description.FAIL);

        MailValidation mailValidation = mailRepository.getMailValidationByEmail(requestBody.getEmail());
        if(mailValidation == null)
            return new ResponseDto<>(HttpStatus.OK.value(), Description.FAIL);
        if(!requestBody.getCode().equals(mailValidation.getCode()))
            return new ResponseDto<>(HttpStatus.OK.value(), Description.FAIL);

        MailValidation updateMailValidation = MailValidation.builder()
                .mail(mailValidation.getMail())
                .code(mailValidation.getCode())
                .checked(true)
                .build();
        mailRepository.saveRedisEmail(updateMailValidation);

        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto<?> sendEmailCode(String email){
        log.info("MailService <sendEmailCode> requestBody : {}", email);
        String code = generateRandomCode();
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(code + context);

            javaMailSender.send(mimeMessage);
            System.out.println("mimeMessageHelper = " + mimeMessageHelper);
            //TODO : 로그 입력
        } catch (MessagingException e){
            //TODO : 로그 입ㄴ력
            System.out.println("e = " + e);
            return new ResponseDto<>(HttpStatus.OK.value(), Description.FAIL);
        }
        MailValidation mailValidation = MailValidation.builder()
                .code(code)
                .mail(email)
                .checked(false)
                .failCount(0)
                .build();
        mailRepository.saveRedisEmail(mailValidation);

        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto<?> sendEmailRandomPassword(String email){
        log.info("MailService <sendEmailCode> requestBody : {}", email);
        String code = generateRandomCode();
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(code + context);

            javaMailSender.send(mimeMessage);
            //TODO : 로그 입력
        } catch (MessagingException e){
            //TODO : 로그 입ㄴ력
            log.error("MessagingException : {}", e.getMessage());
            return new ResponseDto<>(HttpStatus.OK.value(), Description.FAIL);
        }
        MailValidation mailValidation = MailValidation.builder()
                .code(code)
                .mail(email)
                .checked(false)
                .failCount(0)
                .build();
        mailRepository.saveRedisEmail(mailValidation);

        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }


    private String generateRandomCode(){
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        int randomNumber = random.nextInt(10000);
        return String.format("%04d", randomNumber);
    }


}
