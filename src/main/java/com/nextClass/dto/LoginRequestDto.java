package com.nextClass.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    private String id;
    private String password;
    private String app_token;
    @Override
    public String toString() {
        return new StringJoiner(", ", LoginRequestDto.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("password='" + password + "'")
                .add("app_token='" + app_token + "'")
                .toString();
    }
}
