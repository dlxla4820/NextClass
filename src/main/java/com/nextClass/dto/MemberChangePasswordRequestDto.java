package com.nextClass.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberChangePasswordRequestDto {

    private String existingPassword;
    private String newPassword;
    @Override
    public String toString() {
        return new StringJoiner(", ", MemberChangePasswordRequestDto.class.getSimpleName() + "[", "]")
                .add("existingPassword='" + existingPassword + "'")
                .add("newPassword='" + newPassword + "'")
                .toString();
    }
}
