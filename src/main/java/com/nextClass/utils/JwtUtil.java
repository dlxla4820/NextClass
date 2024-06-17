package com.nextClass.utils;

import com.nextClass.dto.MemberRequestDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.internal.ForeignKeyNameSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ssl.SslBundleProperties;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.KeyStore;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.logging.Logger;

@Component
@Slf4j
public class JwtUtil {
    private final Key key;
    private final long accessTokenExpTime;

    public JwtUtil(@Value("{$jwt.secret}") String key,@Value("{$jwt.expiration_time}") long accessTokenExpTime) {
        this.key = Keys.hmacShaKeyFor(key.getBytes());
        this.accessTokenExpTime = accessTokenExpTime;
    }




    //jwt 생성
    private String createToken(MemberRequestDto memberRequestDto, long expireTime){
        Claims claims = Jwts.claims();
        claims.put("id", memberRequestDto.getId());
        claims.put("password", memberRequestDto.getPassword());
        claims.put("member_grade", memberRequestDto.getMember_grade());
        claims.put("member_school",memberRequestDto.getMember_school());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserId(String token){
        return parseClaims(token).get("id", String.class);
    }
    public String createAccessToken(MemberRequestDto memberRequestDto){
        return createToken(memberRequestDto, accessTokenExpTime);
    }



    // JWT Claims 추출
    public Claims parseClaims(String accessToken){
        try{
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }
    // JWT 검증
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            log.info("Invalid JWT Token: ", e);
        } catch (ExpiredJwtException e){
            log.info("Expired JWT Token: ", e);
        } catch (UnsupportedJwtException e){
            log.info("Unsupported JWT Token: ",e);
        } catch (IllegalArgumentException e){
            log.info("JWT claims string is empty: ",e);
        }
        return false;
    }


}
