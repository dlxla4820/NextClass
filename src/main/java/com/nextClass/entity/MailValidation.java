package com.nextClass.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "email", timeToLive = 300)
@NoArgsConstructor
public class MailValidation {

    @Id
    private String mail;
    private String code;
    private Boolean checked;

    @Builder
    public MailValidation( String mail, String code, Boolean checked) {
        this.mail = mail;
        this.code = code;
        this.checked = checked;
    }
}
