package com.nextClass.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDeleteRequestDto {
    private String password;

    @Override
    public String toString() {
        return new StringJoiner(", ", MemberDeleteRequestDto.class.getSimpleName() + "[", "]")
                .add("password='" + password + "'")
                .toString();
    }
}
