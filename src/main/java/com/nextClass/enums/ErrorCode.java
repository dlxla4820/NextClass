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

    PARAMETER_INVALID_SPECIFIC("E00201", "%s이(가) 유효하지 않습니다."),
    MEMBER_DUPLICATED("E00202","%s이(가) 중복되었습니다."),
    MEMBER_NOT_EXIST("E00203","Request에 해당하는 회원이 존재하지 않습니다.");

    private final String errorCode;
    private final String errorDescription;
}
