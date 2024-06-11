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
    private ErrorCode error;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ResponseDto(Integer code, Description description){
        this.code = code;
        this.description = description;
    }
    public ResponseDto(Integer code, Description description, ErrorCode error){
        this.code = code;
        this.description = description;
        this.error = error;
    }
    public ResponseDto(Integer code, Description description, T data){
        this.code = code;
        this.description = description;
        this.data = data;
    }
    public ResponseDto(Integer code, Description description, ErrorCode error, T data){
        this.code = code;
        this.description = description;
        this.error = error;
        this.data = data;
    }

    @Override
    public String toString(){
        return new StringJoiner(", ", ResponseDto.class.getSimpleName() + "[", "]")
                .add("code='" + code + "'" )
                .add("description='"+ description + "'")
                .add("error='"+ error + "'")
                .add("data=" + data)
                .toString();
    }
}
