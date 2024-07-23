package com.nextClass.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailCheckRequestDto {
    private String email;
    private String code;

    @Override
    public String toString() {
        return new StringJoiner(", ", EmailCheckRequestDto.class.getSimpleName() + "[", "]")
                .add("email='" + email + "'")
                .add("code='" + code + "'")
                .toString();
    }
}
