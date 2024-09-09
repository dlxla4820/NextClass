package com.nextClass.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@Transactional
public class AndroidPushNotificationService {

    public String sendPushNotification(String title, String body, String appToken) throws FirebaseMessagingException {
        Message message = Message.builder()
                .putData("title", title)
                .putData("body", body)
                .setToken(appToken)
                .build();
        return FirebaseMessaging.getInstance().send(message);
    }

    public String sendFcmDataToFirebase(Map<String, String> fcmData, String appToken) throws FirebaseMessagingException {
        Message.Builder messageBuilder = Message.builder().setToken(appToken);
        fcmData.entrySet().stream().forEach(entry -> messageBuilder.putData(entry.getKey(), entry.getValue()));
        Message message = messageBuilder.build();
        return FirebaseMessaging.getInstance().send(message);
    }
}
