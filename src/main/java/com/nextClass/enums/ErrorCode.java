package com.nextClass.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    JSON_INVALID("E00101", "유효하지 않는 Json형식입니다."),
    REQUEST_BODY_NULL("E00102","요청본문이 비어있습니다."),
    PARAMETER_INVALID_GENERAL("E00103", "유효하지 않는 Parameter가 포함되어 있습니다."),
    JWT_INVALID("E00104","지원하지 않는 형식의 JWT 토큰입니다."),

    PARAMETER_INVALID_SPECIFIC("E00201", "%s이(가) 유효하지 않습니다."),
    MEMBER_DUPLICATED("E00202","%s이(가) 중복되었습니다."),
    MEMBER_NOT_EXIST("E00203","Request에 해당하는 회원이 존재하지 않습니다.");

    private final String errorCode;
    private final String errorDescription;
}
