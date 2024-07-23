package com.nextClass.repository;

import com.nextClass.entity.MailValidation;
import org.springframework.data.repository.CrudRepository;

public interface MailRedisRepository extends CrudRepository<MailValidation, String> {
}
