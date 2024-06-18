package com.nextClass.config;


import com.nextClass.service.MemberService;
import jakarta.servlet.ServletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.security.web.session.SimpleRedirectSessionInformationExpiredStrategy;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final String REMEMBER_ME_TOKEN ="CHANGE";

//    private final MemberService memberService;
//
//
//    @Autowired
//    public SecurityConfig(MemberService memberService) {
//        this.memberService = memberService;
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


//    private static class RemoveExistingSessionStrategy implements SessionInformationExpiredStrategy {
//        private final SessionInformationExpiredStrategy delegate = new SimpleRedirectSessionInformationExpiredStrategy("/login");
//
//        @Override
//        public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
//            // 기존 세션을 제거하는 로직을 구현
//            event.getRequest().getSession().invalidate();
//
//            // 로그인 페이지로 리디렉션
//            delegate.onExpiredSessionDetected(event);
//        }
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/register","/duplicated_check", "/find", "/login", "/login-process").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .build();

    }


//    @Bean
//    public AuthenticationProvider authenticationProvider(){
//        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
//        authenticationProvider.setPasswordEncoder(passwordEncoder());
//        authenticationProvider.setUserDetailsService(memberService);
//        return authenticationProvider;
//    }



}
