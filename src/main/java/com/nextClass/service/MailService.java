package com.nextClass.service;

import com.nextClass.dto.*;
import com.nextClass.entity.MailValidation;
import com.nextClass.entity.Member;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

import static com.nextClass.enums.ErrorCode.TOKEN_UNAUTHORIZED;

@Service
@Slf4j
@Transactional
public class MailService {
    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    private final MailRepository mailRepository;
    private static final String code_subject = "[다음 수업] : 이메일 인증";
    private static final String code_context = " 코드를 입력하여 계정이 본인 소유임을 인증하여 주시기 바랍니다.\n" +
            "중요: 인증번호는 5분후에 만료됩니다. 5분 내로 입력하여 주시기 바랍니다.";
    private static final String id_subject = "[다음 수업] : 아이디 찾기";

    private static final String id_context = " 님의 아이디 : ";

    private static final String password_subject = "[다음 수업] : 임시 비밀번호 생성";
    private static final String password_context = " 님의 임시 비밀번호가 생성되었습니다.\n" +
            "임시 비밀번호 : ";


    public MailService(LoginRepository loginRepository, PasswordEncoder passwordEncoder, JavaMailSender javaMailSender, MailRepository mailRepository) {
        this.loginRepository = loginRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
        this.mailRepository = mailRepository;
    }


    public ResponseDto<?> checkEmailCreateCode(EmailCheckRequestDto requestBody){
        log.info("MailService << checkEmailCreateCode >> | requestBody : {}", requestBody);
        if(requestBody.getEmail() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "email"));
        if(requestBody.getCode() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "code"));

        MailValidation mailValidation = mailRepository.getMailValidationByEmail(requestBody.getEmail());
        MailValidation updateMailValidation;
        // 메일 존재 X
        if(mailValidation == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.MAIL_NOT_EXIST.getErrorCode(), ErrorCode.MAIL_NOT_EXIST.getErrorDescription());
        // 5회 이상 FAIL
        if(mailValidation.getFailCount() == 5) {
            mailRepository.deleteRedisEmail(mailValidation);
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.MAIL_CODE_FIVE_FAIL.getErrorCode(), ErrorCode.MAIL_CODE_FIVE_FAIL.getErrorDescription());
        }
        // 인증코드 미 일치
        if(!requestBody.getCode().equals(mailValidation.getCode())) {
            updateMailValidation = MailValidation.builder()
                    .mail(mailValidation.getMail())
                    .code(mailValidation.getCode())
                    .checked(false)
                    .failCount(mailValidation.getFailCount() + 1)
                    .build();
            mailRepository.saveRedisEmail(updateMailValidation);
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.MAIL_CODE_INVALID.getErrorCode(), ErrorCode.MAIL_CODE_INVALID.getErrorDescription(), null, mailValidation.getFailCount());
        }

        updateMailValidation = MailValidation.builder()
                .mail(mailValidation.getMail())
                .code(mailValidation.getCode())
                .checked(true)
                .build();
        mailRepository.saveRedisEmail(updateMailValidation);
        log.info("MailService << checkEmailCode >> | updateMailValidation : {}", mailValidation);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto<?> sendEmailCreateCode(EmailSendCodeRequestDto requestBody){
        log.info("MailService << sendEmailCreateCode >> | requestBody : {}", requestBody);
        // 유효성 검사
        if(requestBody.getEmail() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "email"));
        // 인증 코드 발급
        String code = generateRandomCode();
        // 메일 send
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(requestBody.getEmail());
            mimeMessageHelper.setSubject(code_subject);
            mimeMessageHelper.setText(code + code_context);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e){
            log.error("MailService << sendEmailCode >> | MessagingException e : {}" , e.getMessage(), e);
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),  Description.FAIL, ErrorCode.MAIL_SEND_FAIL.getErrorCode(), ErrorCode.MAIL_SEND_FAIL.getErrorDescription());
        }
        // redis 저장
        MailValidation mailValidation = MailValidation.builder()
                .code(code)
                .mail(requestBody.getEmail())
                .checked(false)
                .failCount(0)
                .build();
        mailRepository.saveRedisEmail(mailValidation);
        log.info("MailService << sendEmailCode >> | mailValidation : {}", mailValidation);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }
    public ResponseDto<?> sendChangeEmailCreateCode(EmailSendChangeCodeRequestDto requestBody){
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("MailService << sendChangeEmailCreateCode >> | memberUuid : {}, requestBody : {}",memberUuid, requestBody);
        // 유효성 검사
        if(memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(),Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());
        if(requestBody.getEmail() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "email"));
        if(requestBody.getPassword() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "password"));
        // uuid 체크
        if(loginRepository.getMyInfoByUuid(memberUuid) == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.MAIL_MEMBER_NOT_EXIST.getErrorCode(), ErrorCode.MAIL_MEMBER_NOT_EXIST.getErrorDescription());
        // email 중복 체크
        if(loginRepository.getMemberByEmail(requestBody.getEmail()) != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.INPUT_DUPLICATED.getErrorCode(), String.format(ErrorCode.INPUT_DUPLICATED.getErrorDescription(), "email"));

        // 현재 비밀번호 검사
        String memberPassword = loginRepository.getMemberPasswordByUuid(memberUuid);
        if(memberPassword == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.TOKEN_MEMBER_NOT_EXIST.getErrorCode(), ErrorCode.TOKEN_MEMBER_NOT_EXIST.getErrorDescription());
        if(!passwordEncoder.matches(requestBody.getPassword(), memberPassword))
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.EXISTING_PASSWORD_NOT_MATCH.getErrorCode(), ErrorCode.EXISTING_PASSWORD_NOT_MATCH.getErrorDescription());

        // 인증 코드 발급
        String code = generateRandomCode();
        // 메일 send
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(requestBody.getEmail());
            mimeMessageHelper.setSubject(code_subject);
            mimeMessageHelper.setText(code + code_context);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e){
            log.error("MailService << sendChangeEmailCreateCode >> | MessagingException e : {}" , e.getMessage(), e);
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),  Description.FAIL, ErrorCode.MAIL_SEND_FAIL.getErrorCode(), ErrorCode.MAIL_SEND_FAIL.getErrorDescription());
        }
        // redis 저장
        MailValidation mailValidation = MailValidation.builder()
                .code(code)
                .mail(requestBody.getEmail())
                .checked(false)
                .failCount(0)
                .build();
        mailRepository.saveRedisEmail(mailValidation);
        log.info("MailService << sendChangeEmailCreateCode >> | mailValidation : {}", mailValidation);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }


    public ResponseDto<?> sendEmailRandomPassword(EmailSendPasswordDto requestBody){
        log.info("MailService << sendEmailRandomPassword >> | requestBody : {}", requestBody);
        // 유효성 검사
        if(requestBody.getId() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "id"));
        // id check
        Member member = loginRepository.getMemberById(requestBody.getId());
        if(member == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.MAIL_MEMBER_NOT_EXIST.getErrorCode(), ErrorCode.MAIL_MEMBER_NOT_EXIST.getErrorDescription());

        String password = generateRandomPassword();
        String encodePassword = passwordEncoder.encode(password);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(member.getEmail());
            mimeMessageHelper.setSubject(password_subject);
            mimeMessageHelper.setText(member.getName() + password_context + password) ;
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e){
            log.error("MailService << sendEmailRandomPassword >> | MessagingException e : {}" , e.getMessage(), e);
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),  Description.FAIL, ErrorCode.MAIL_SEND_FAIL.getErrorCode(), ErrorCode.MAIL_SEND_FAIL.getErrorDescription());
        }

        loginRepository.updateMemberPassword(member.getUuid().toString(), encodePassword);
        log.info("MailService << sendEmailRandomPassword >> | password : {}", encodePassword);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto<?> sendEmailMemberId(EmailSendMemberIdDto requestBody) {
        log.info("MailService << sendEmailMemberId >> | requestBody : {}", requestBody);
        if(requestBody.getEmail() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "email"));
        // email check
        Member member = loginRepository.getMemberByEmail(requestBody.getEmail());
        if(member == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.MAIL_MEMBER_NOT_EXIST.getErrorCode(), ErrorCode.MAIL_MEMBER_NOT_EXIST.getErrorDescription());
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(member.getEmail());
            mimeMessageHelper.setSubject(id_subject);
            mimeMessageHelper.setText(member.getName() + id_context + member.getId()) ;
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e){
            log.error("MailService << sendEmailMemberId >> | MessagingException e : {}" , e.getMessage(), e);
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),  Description.FAIL, ErrorCode.MAIL_SEND_FAIL.getErrorCode(), ErrorCode.MAIL_SEND_FAIL.getErrorDescription());
        }
        log.info("MailService << sendEmailMemberId >> | member : {}", member);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    private String generateRandomCode(){
        SecureRandom random = new SecureRandom();
        int randomNumber = random.nextInt(10000);
        return String.format("%04d", randomNumber);
    }

    private String generateRandomPassword(){
        SecureRandom random = new SecureRandom();
        // 9 ~ 18자리
        int min = 9;
        int max = 18;
        int randomNumber = random.nextInt(max - min + 1) + min;
        return CommonUtils.getRandomPassword(randomNumber);
    }
}
