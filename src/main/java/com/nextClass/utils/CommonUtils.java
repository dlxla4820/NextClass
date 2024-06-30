package com.nextClass.utils;

import com.nextClass.dto.MemberSessionDto;
import com.nextClass.entity.Member;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.regex.Pattern;

public class CommonUtils {

    public static boolean checkLength(String checkData, int mini, int max){
        if(checkData.length() < mini)
            return false;
        if(checkData.length() > max)
            return false;

        return true;
    }


    public static boolean containsEnglish(String input) {
        return Pattern.compile("[a-zA-Z]").matcher(input).find();
    }

    public static boolean containsNumber(String input) {
        return Pattern.compile("[0-9]").matcher(input).find();
    }

    public static boolean containsSpecialCharacter(String input) {
        return Pattern.compile("[^a-zA-Z0-9]").matcher(input).find();
    }

    public static boolean containsKorean(String input) {
        return Pattern.compile("[가-힣]").matcher(input).find();
    }


    public static MemberSessionDto getUserSession() {
        // 현재 인증된 Authentication 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 사용자 이름 가져오기
        String username = authentication.getName();

        // 사용자 권한 가져오기
        // 여기서 authorities는 Collection<? extends GrantedAuthority> 타입입니다.
        // 필요에 따라 GrantedAuthority 객체의 getAuthority() 메서드를 이용해 권한 이름을 가져올 수 있습니다.
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        return MemberSessionDto.builder()
                .uuid(username)
                .authorities(authorities)
                .build();
    }

    public static String getMemberUuidIfAdminOrUser(){
        MemberSessionDto member = getUserSession();
        String auth = member.getAuthorities().iterator().next().toString();
        if(Member.RoleType.ADMIN.toString().equals(auth) || Member.RoleType.USER.toString().equals(auth))
            return member.getUuid();
        return null;
    }

    public static String getMemberUuidIfAdmin(){
        MemberSessionDto member = getUserSession();
        if(Member.RoleType.ADMIN.toString().equals(member.getAuthorities().iterator().next().toString()))
            return member.getUuid();
        return null;
    }

}
