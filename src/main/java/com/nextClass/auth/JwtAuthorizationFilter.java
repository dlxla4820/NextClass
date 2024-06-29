package com.nextClass.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextClass.dto.ResponseDto;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Order(0)
@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    public JwtAuthorizationFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

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
        try {
            String token = parseBearerToken(request);
            User user = parseUserSpecification(token);
            AbstractAuthenticationToken authenticated = UsernamePasswordAuthenticationToken.authenticated(user, token, user.getAuthorities());
            authenticated.setDetails(new WebAuthenticationDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticated);
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e){
            // refresh 토큰이 있을경우
            if(request.getHeader("refresh-token") != null)
                recreateAccessTokenAndSetErrorResponse(response, request.getHeader("refresh-token"));
            else
                setErrorResponse(response, ErrorCode.JWT_ACCESS_EXPIRED_INVALID);
        }catch (JwtException | IllegalArgumentException e){
            //유효하지 않은 토큰
            setErrorResponse(response, ErrorCode.JWT_ACCESS_INVALID);
        }
    }

    private String parseBearerToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(token -> token.substring(0, 7).equalsIgnoreCase("Bearer "))
                .map(token -> token.substring(7))
                .orElse(null);
    }

    private User parseUserSpecification(String token) {
        String[] split = Optional.ofNullable(token)
                .filter(subject -> subject.length() >= 10)
                .map(tokenProvider::validateTokenAndGetSubject)
                .orElse("anonymous:anonymous")
                .split(":");

        return new User(split[0], "", List.of(new SimpleGrantedAuthority(split[1])));
    }



    private void recreateAccessTokenAndSetErrorResponse(HttpServletResponse response, String refreshToken){
        try {
            String subject = tokenProvider.validateTokenAndGetSubject(refreshToken);
            String accessToken = tokenProvider.createAccessToken(subject);
            setErrorResponse(response,ErrorCode.JWT_ACCESS_EXPIRED_INVALID, accessToken);
        } catch (ExpiredJwtException e){
            setErrorResponse(response, ErrorCode.JWT_REFRESH_EXPIRED_INVALID);
        } catch (JwtException | IllegalArgumentException e){
            //유효하지 않은 토큰
            setErrorResponse(response, ErrorCode.JWT_REFRESH_INVALID);
        }
    }



    private void setErrorResponse(
            HttpServletResponse response,
            ErrorCode errorCode
    ){
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ResponseDto<?> responseDto = new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, errorCode.getErrorCode(), errorCode.getErrorDescription());
        try{
            response.getWriter().write(objectMapper.writeValueAsString(responseDto));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void setErrorResponse(
            HttpServletResponse response,
            ErrorCode errorCode,
            String accessToken
    ){
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ResponseDto<?> responseDto = new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, errorCode.getErrorCode(), errorCode.getErrorDescription(), accessToken);
        try{
            response.getWriter().write(objectMapper.writeValueAsString(responseDto));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}