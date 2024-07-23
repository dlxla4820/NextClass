package com.nextClass.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailSendMemberIdDto {

    private String email;
    @Override
    public String toString() {
        return new StringJoiner(", ", EmailSendMemberIdDto.class.getSimpleName() + "[", "]")
                .add("email='" + email + "'")
                .toString();
    }
}
