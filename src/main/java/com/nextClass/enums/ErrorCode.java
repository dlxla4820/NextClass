package com.nextClass.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
    JSON_INVALID("E00101", "유효하지 않는 Json형식입니다."),
    ID_INVALID("E00201", "id이(가) 유효하지 않습니다."),
    NAME_INVALID("E00202","name이(가) 유효하지 않습니다."),
    PASSWORD_INVALID("E00203","password이(가) 유효하지 않습니다."),
    EMAIL_INVALID("E00204","email이(가) 유효하지 않습니다."),
    MEMBER_GRADE_INVALID("E00205","member_grade이(가) 유효하지 않습니다."),
    MEMBER_SCHOOL_INVALID("E00206","member_school이(가) 유효하지 않습니다.");

    private final String errorCode;
    private final String errorDescription;
}
