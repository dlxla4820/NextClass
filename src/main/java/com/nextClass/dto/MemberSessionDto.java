package com.nextClass.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.StringJoiner;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MemberSessionDto {
    private String uuid;
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public String toString(){
        return new StringJoiner(", ", MemberSessionDto.class.getSimpleName() + "[", "]")
                .add("uuid='" + uuid + "'" )
                .add("authorities='"+ authorities + "'")
                .toString();
    }
}
