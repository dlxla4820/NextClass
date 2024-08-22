package com.nextClass.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {
    @Bean
    public FirebaseApp connectFireBase() {
        try {
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource[] resources = ResourcePatternUtils
                    .getResourcePatternResolver(resourceLoader)
                    .getResources("classpath:next-class-b628b-firebase-adminsdk-hmt06-9553065b92.json");

// 첫 번째 리소스를 가져옵니다. (리스트에서 첫 번째 요소)
            if (resources.length > 0) {
                InputStream serviceAccountType = resources[0].getInputStream();

                FirebaseOptions fireBaseOptions = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccountType))
                        .setDatabaseUrl("https://next-class-b628b.firebaseio.com")
                        .build();

                return FirebaseApp.initializeApp(fireBaseOptions);
            } else {
                throw new FileNotFoundException("Resource not found: next-class-b628b-firebase-adminsdk-hmt06-9553065b92.json");
            }
        } catch(Exception e) {
            log.error("FirebaseConfig << connectFireBase >> Exception : {}", e.getMessage(), e);
            return null; // 또는 적절한 예외 처리를 하세요.
        }
    }
}

