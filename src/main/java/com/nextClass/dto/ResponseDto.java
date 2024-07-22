package com.nextClass.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseDto<T> {

    private Integer code; // return code 200, 400 ...
    private Description description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorDescription;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String accessToken;

    // Success
    public ResponseDto(Integer code, Description description){
        this.code = code;
        this.description = description;
    }
    // Fail
    public ResponseDto(Integer code, Description description, String errorCode, String errorDescription){
        this.code = code;
        this.description = description;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }
    // Success + Data
    public ResponseDto(Integer code, Description description, T data){
        this.code = code;
        this.description = description;
        this.data = data;
    }
    // Fail + Data
    public ResponseDto(Integer code, Description description, String errorCode,String errorDescription, T data){
        this.code = code;
        this.description = description;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.data = data;
    }

    // Fail + accessToken 만료
    public ResponseDto(Integer code, Description description, String errorCode,String errorDescription, String accessToken){
        this.code = code;
        this.description = description;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.accessToken = accessToken;
    }

    @Override
    public String toString(){
        return new StringJoiner(", ", ResponseDto.class.getSimpleName() + "[", "]")
                .add("code='" + code + "'" )
                .add("description='"+ description + "'")
                .add("errorCode='"+ errorCode + "'")
                .add("errorDescription='"+ errorDescription + "'")
                .add("data=" + data)
                .toString();
    }
}
