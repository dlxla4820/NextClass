package com.nextClass.auth;

import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Order(0)
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 특정 URL에 대해서는 인증을 수행하지 않도록 설정
        List<String> nonAuthUrls = Arrays.asList("/login", "/login-process", "/register");

        // 요청 URL을 가져옴
        String requestURI = request.getRequestURI();

        // 인증을 필요로 하지 않는 URL인 경우
        if (nonAuthUrls.contains(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bearer 토큰을 파싱하여 UUID 추출
        String token = parseBearerToken(request);
        String uuid = parseUserSpecification(token);

        if (uuid != null) {
            // UUID를 사용하여 인증 토큰 생성
            AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(uuid, token);
            authenticationToken.setDetails(new WebAuthenticationDetails(request));

            // 인증 객체 설정
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // 다음 필터로 진행
            filterChain.doFilter(request, response);
        } else {
            // 유효한 토큰을 찾을 수 없는 경우, 인증 실패 처리
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private String parseBearerToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(token -> token.substring(0, 7).equalsIgnoreCase("Bearer "))
                .map(token -> token.substring(7))
                .orElse(null);
    }

    private String parseUserSpecification(String token) {
        return tokenProvider.validateTokenAndGetSubject(token);
    }
}
