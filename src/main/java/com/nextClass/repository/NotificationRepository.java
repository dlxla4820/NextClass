package com.nextClass.repository;

import com.nextClass.entity.NotificationConfig;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface NotificationRepository extends CrudRepository<NotificationConfig, UUID> {
}
