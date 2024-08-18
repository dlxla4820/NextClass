package com.nextClass.utils;


import com.nextClass.enums.ErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final String errorCode;
    private final String errorDescription;
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getErrorDescription());
        this.errorCode = errorCode.getErrorCode();
        this.errorDescription = errorCode.getErrorDescription();
    }

    public CustomException(ErrorCode errorCode, String parameter){
        super(errorCode.getErrorDescription());
        this.errorCode = errorCode.getErrorCode();
        this.errorDescription = String.format(errorCode.getErrorDescription(), parameter);
    }
}
