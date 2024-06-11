package com.nextClass.utils;

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
}
