package com.nextClass.controller;

import com.nextClass.dto.ResponseDto;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> requestBodyExceptionHandler(HttpMessageNotReadableException e) {
        if (e.getMessage().contains("Required request body is missing")) {
            // 요청 바디가 누락된 경우
//            System.out.println("요청 바디 누락: " + e.getMessage());
            return ResponseEntity.ok().body(new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.REQUEST_BODY_NULL.getErrorCode(), ErrorCode.REQUEST_BODY_NULL.getErrorDescription()));
        } else if (e.getMessage().contains("JSON parse error")) {
            // JSON 파싱 오류인 경우
//            System.out.println("JSON 파싱 오류: " + e.getMessage());
            return ResponseEntity.ok().body(new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.JSON_INVALID.getErrorCode(), ErrorCode.REQUEST_BODY_NULL.getErrorDescription()));
        } else {
            // 기타 예외인 경우
//            System.out.println("기타 예외: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // JWT 토큰 에러 관리
    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<?> jwtExceptionHandler(UnsupportedJwtException e){

        return ResponseEntity.ok().body(new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, ErrorCode.REQUEST_BODY_NULL.getErrorCode(), ErrorCode.REQUEST_BODY_NULL.getErrorDescription()));
    }
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ResponseDto<?>> handleSignatureException() {
        return ResponseEntity.ok().body(new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, ErrorCode.REQUEST_BODY_NULL.getErrorCode(), ErrorCode.REQUEST_BODY_NULL.getErrorDescription()));
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ResponseDto<?>>  handleMalformedJwtException() {
        return ResponseEntity.ok().body(new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, ErrorCode.REQUEST_BODY_NULL.getErrorCode(), ErrorCode.REQUEST_BODY_NULL.getErrorDescription()));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ResponseDto<?>>  handleExpiredJwtException() {
        return ResponseEntity.ok().body(new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, ErrorCode.REQUEST_BODY_NULL.getErrorCode(), ErrorCode.REQUEST_BODY_NULL.getErrorDescription()));
    }

}
