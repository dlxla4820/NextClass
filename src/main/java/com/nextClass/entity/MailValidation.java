package com.nextClass.entity;

import org.springframework.data.annotation.Id;
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
    private Integer failCount;

    @Builder
    public MailValidation( String mail, String code, Boolean checked, Integer failCount) {
        this.mail = mail;
        this.code = code;
        this.checked = checked;
        this.failCount =failCount;
    }
}
