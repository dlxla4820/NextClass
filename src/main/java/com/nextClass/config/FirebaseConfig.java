package com.nextClass.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Configuration
public class FirebaseConfig {
    @Bean   
    public void connectFireBase() {
        try {
            FileInputStream serviceAccountType = new FileInputStream("/src/main/resources/next-class-b628b-firebase-adminsdk-hmt06-9553065b92.json");
            FirebaseOptions fireBaseOptions = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountType))
                    .setDatabaseUrl("https://next-class-b628b.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(fireBaseOptions);
        }catch(Exception e){
            log.error("FirebaseConfig << connectFireBase >> Exception : {}", e.getMessage(), e);
        }
    }


}
