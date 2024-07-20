package com.nextClass.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberChangeEmailRequestDto {

    private String password;
    private String email;

    @Override
    public String toString() {
        return new StringJoiner(", ", MemberChangeEmailRequestDto.class.getSimpleName() + "[", "]")
                .add("password='" + password + "'")
                .add("email='" + email + "'")
                .toString();
    }
}
