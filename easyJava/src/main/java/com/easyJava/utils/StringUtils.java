package com.easyJava.utils;

public class StringUtils {
    /**
     * 字符串首字母大写
     * @param field 字符串
     * @return 首字母转换成大写的字符串
     */
    public static String UpperCaseFirstLetter(String field){
        if(org.apache.commons.lang3.StringUtils.isEmpty(field)){
            return field;
        }
        return field.substring(0,1).toUpperCase()+field.substring(1);
    }

    /**
     * 字符串首字母转小写
     * @param field 字符串
     * @return 首字母转换小写的字符串
     */
    public static String LowerCaseFirstLetter(String field){
        if(org.apache.commons.lang3.StringUtils.isEmpty(field)){
            return field;
        }
        return field.substring(0,1).toLowerCase()+field.substring(1);
    }

    public static void main(String[] args) {
        System.out.println(UpperCaseFirstLetter("apple"));
    }
}
