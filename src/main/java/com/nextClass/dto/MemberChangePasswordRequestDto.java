package com.nextClass.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.StringJoiner;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberChangePasswordRequestDto {
    @JsonProperty("existing_password")
    private String existingPassword;
    @JsonProperty("new_password")
    private String newPassword;
    @Override
    public String toString() {
        return new StringJoiner(", ", MemberChangePasswordRequestDto.class.getSimpleName() + "[", "]")
                .add("existingPassword='" + existingPassword + "'")
                .add("newPassword='" + newPassword + "'")
                .toString();
    }
}
