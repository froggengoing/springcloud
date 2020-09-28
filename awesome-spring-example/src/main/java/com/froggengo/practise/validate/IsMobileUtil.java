package com.froggengo.practise.validate;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IsMobileUtil{
    private static final Pattern mobile_pattern=Pattern.compile("1\\d{10}");
    public static boolean isMobile(String mobile){
        //必须判断空值
        if (StringUtils.isEmpty(mobile)) {
            return false;
        }
        Matcher matcher = mobile_pattern.matcher(mobile);
        return matcher.matches();
    }

    public static void main(String[] args) {
        System.out.println(isMobile("1234"));
        System.out.println(isMobile("12345678910"));
        System.out.println(isMobile(null));
    }
}
