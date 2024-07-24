package com.nextClass.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailSendChangeCodeRequestDto {
    private String email;
    private String password;
    @Override
    public String toString() {
        return new StringJoiner(", ", EmailSendCodeRequestDto.class.getSimpleName() + "[", "]")
                .add("email='" + email + "'")
                .add("password='" + password + "'")
                .toString();
    }
}
