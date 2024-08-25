package com.nextClass.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    SYSTEM_ERROR("E00001","시스템 에러"),
    JSON_INVALID("E00101", "유효하지 않는 Json형식입니다."),
    REQUEST_BODY_NULL("E00102","요청본문이 비어있습니다."),
    PARAMETER_INVALID_GENERAL("E00103", "유효하지 않는 Parameter가 포함되어 있습니다."),
    TOKEN_UNAUTHORIZED("E00104", "해당 요청에 대한 권한이 없습니다."),
    JWT_ACCESS_INVALID("E00105","유효하지 않은 Access 토큰입니다."),
    JWT_ACCESS_EXPIRED_INVALID("E00106","만료된 Access 토큰입니다."),
    JWT_REFRESH_INVALID("E00107", "유효하지 않은 Refresh 토큰입니다."),
    JWT_REFRESH_EXPIRED_INVALID("E00108","만료된 Refresh 토큰입니다."),
    PARAMETER_INVALID_SPECIFIC("E00109", "%s이(가) 유효하지 않습니다."),
    INPUT_DUPLICATED("E00110","%s이(가) 중복되었습니다."),
    TOKEN_MEMBER_NOT_EXIST("E00111","Token에 해당하는 회원이 존재하지 않습니다."),

    MEMBER_NOT_EXIST("E00201","Request에 해당하는 회원이 존재하지 않습니다."),
    EXISTING_PASSWORD_NOT_MATCH("E00202","현재 비밀번호가 일치하지 않습니다."),
    EMAIL_NOT_CHECK("E00203","이메일 인증이 필요합니다."),


    DATA_ALREADY_EXIST("E00301","해당 수업이 시간표 상에 이미 존재합니다."),
    TIME_TABLE_UNAUTHORIZED("E00302", "해당 수업에 대한 접근 권한이 없습니다."),
    DATA_ALREADY_DELETED("E00303", "해당 수업은 존재하지 않습니다"),
    CLASS_ALREADY_EXIST_ON_SAME_TIME("E00304", "해당 시간에 이미 수업이 존재합니다."),

    TO_DO_LIST_ALREADY_EXIST("E00401", "ToDoList가 중복되었습니다"),
    TO_DO_LIST_ALREADY_DELETE("E00402", "ToDoList가 존재하지 않습니다"),

    MAIL_SEND_FAIL("E00501","메일 발송에 실패하였습니다."),
    MAIL_NOT_EXIST("E00502","해당 인증건에 대한 발송 메일이 존재 하지 않습니다."),
    MAIL_CODE_INVALID("E00503","인증코드가 유효하지 않습니다."),
    MAIL_MEMBER_NOT_EXIST("E00504","회원이 존재하지 않습니다."),
    MAIL_CODE_FIVE_FAIL("E00505","인증코드 입력을 5회 실패하였습니다."),

    SCORE_SAVE_FAIL("E00601", "학점 저장에 실패하였습니다."),
    INPUT_SCORE_OUT_OF_RANGE("E00602", "입력된 점수, 평균점수, 표준편차 값이 범위를 벗어났습니다."),

    POST_NOT_EXIST("E00701", "게시글이 존재하지 않습니다."),
    POST_NOT_MATCH_MEMBER("E00702", "해당 게시글에 대한 권한이 없습니다."),
    COMMENT_NOT_EXIST("E00703", "댓글이 존재하지 않습니다."),
    COMMENT_NOT_MATCH_MEMBER("E00704", "해당 댓글에 대한 권한이 없습니다.");



    private final String errorCode;
    private final String errorDescription;
}
