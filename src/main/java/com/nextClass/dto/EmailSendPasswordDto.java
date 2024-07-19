package com.nextClass.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailSendPasswordDto {
    private String id;

    @Override
    public String toString() {
        return new StringJoiner(", ", EmailSendPasswordDto.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .toString();
    }
}
