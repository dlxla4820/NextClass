package com.nextClass.utils;

import com.nextClass.dto.MemberSessionDto;
import com.nextClass.entity.Member;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class CommonUtils {

    private static final char[] rndAllCharacters = new char[]{
            //number
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            //uppercase
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            //lowercase
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            //special symbols
            '@', '$', '!', '%', '*', '?', '&'
    };

    private static final char[] numberCharacters = new char[] {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    private static final char[] uppercaseCharacters = new char[] {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private static final char[] lowercaseCharacters = new char[] {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    private static final char[] specialSymbolCharacters = new char[] {
            '@', '$', '!', '%', '*', '?', '&'
    };



    public static String getRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();

        List<Character> passwordCharacters = new ArrayList<>();

        int numberCharactersLength = numberCharacters.length;
        passwordCharacters.add(numberCharacters[random.nextInt(numberCharactersLength)]);

        int uppercaseCharactersLength = uppercaseCharacters.length;
        passwordCharacters.add(uppercaseCharacters[random.nextInt(uppercaseCharactersLength)]);

        int lowercaseCharactersLength = lowercaseCharacters.length;
        passwordCharacters.add(lowercaseCharacters[random.nextInt(lowercaseCharactersLength)]);

        int specialSymbolCharactersLength = specialSymbolCharacters.length;
        passwordCharacters.add(specialSymbolCharacters[random.nextInt(specialSymbolCharactersLength)]);

        int rndAllCharactersLength = rndAllCharacters.length;
        for (int i = 0; i < length-4; i++) {
            passwordCharacters.add(rndAllCharacters[random.nextInt(rndAllCharactersLength)]);
        }

        Collections.shuffle(passwordCharacters);

        for (Character character : passwordCharacters) {
            stringBuilder.append(character);
        }

        return stringBuilder.toString();
    }




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
