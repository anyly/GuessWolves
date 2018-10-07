package org.idear.util;

/**
 * Created by idear on 2018/9/21.
 */
public class StringUtil {
    public static String simplePokerName(String poker) {
        StringBuilder stringBuilder = new StringBuilder();
        String prefix =  "化身";
        if (poker.startsWith(prefix)) {
            return poker.substring(prefix.length(), prefix.length()+1);
        }
        return poker.substring(0, 1);
    }

    //首字母转小写
    public static String toLowerCaseFirstOne(String s){
        if (s.length() == 0) {
            return s;
        }
        if(Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }


    //首字母转大写
    public static String toUpperCaseFirstOne(String s){
        if (s.length() == 0) {
            return s;
        }
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
}
