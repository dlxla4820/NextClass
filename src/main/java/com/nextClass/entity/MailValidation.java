package com.nextClass.entity;

import com.nextClass.dto.EmailSendCodeRequestDto;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.StringJoiner;

@Getter
@RedisHash(value = "email", timeToLive = 300)
@NoArgsConstructor
public class MailValidation {

    @Id
    private String mail;

    private String code;
    private Boolean checked;
    private Integer failCount;

    @Builder
    public MailValidation( String mail, String code, Boolean checked, Integer failCount) {
        this.mail = mail;
        this.code = code;
        this.checked = checked;
        this.failCount =failCount;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MailValidation.class.getSimpleName() + "[", "]")
                .add("mail='" + mail + "'")
                .add("code='" + code + "'")
                .add("checked='" + checked + "'")
                .add("failCount='" + failCount + "'")
                .toString();
    }
}
