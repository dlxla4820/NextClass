package com.nextClass.repository;

import com.nextClass.entity.MailValidation;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class MailRepository {

    private final MailRedisRepository mailRedisRepository;

    public MailRepository(MailRedisRepository mailRedisRepository) {
        this.mailRedisRepository = mailRedisRepository;
    }

    public void saveRedisEmail(MailValidation mailValidation){
        mailRedisRepository.save(mailValidation);
    }

    public MailValidation getMailValidationByEmail(String email){
        return mailRedisRepository.findById(email).orElse(null);
    }


}
