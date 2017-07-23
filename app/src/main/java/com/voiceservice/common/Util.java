package com.voiceservice.common;

/**
 * Created by Administrator on 2017/7/21.
 */

public class Util {
    public static boolean isInteger(String value){
        try{
            Integer.parseInt(value);
            return  true;
        } catch (NumberFormatException e){
            return  false;
        }
    }

    public static boolean isDouble(String value){
        try{
            Double.parseDouble(value);
            if(value.contains(".")){
                return true;
            }
            return false;
        } catch (NumberFormatException e){
            return  false;
        }
    }

    public static boolean isNum(String value){
        return isInteger(value)|| isInteger(value);
    }

}
