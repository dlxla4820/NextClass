package com.nextClass.utils;

public class CommonUtils {

    public String checkLength(String checkData, int mini, int max, String description){
        if(checkData.length() < mini)
            return description;
        if(checkData.length() > max)
            return description;
        return "OK";
    }
}
