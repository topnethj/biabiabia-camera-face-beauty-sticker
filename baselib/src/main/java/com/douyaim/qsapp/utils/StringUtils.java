package com.douyaim.qsapp.utils;

import android.text.TextUtils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    /**
     * @see <a href="http://stackoverflow.com/questions/24070515/rendering-or-deleting-emoji/24071599#24071599
     * ">from stackoverflow</a>
     */

    /**
     * 设置用户名的规则
     */
    private static final String regexUsername =
            "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]]{2,16}$";

    private static final String regexSize = "[^\\s]{2,16}$";

    private static final String regexWallet = "[\\d]{6}$";
    private static final String regexPhone = "[\\d]{11}$";  //11位数字
    private static final String regexCheckCode = "[\\d]{4}$"; //4位数字

    /**
     * @param usename 待验证的用户名
     * @return 验证结果，验证通过返回true，失败返回false
     */
    public static boolean isMatchUsename(String usename) {
        Pattern p = Pattern.compile(regexUsername);
        Matcher m = p.matcher(usename);
        if (m.matches()) {
            return isMatchUserSize(usename);
        }
        return false;
    }

    public static boolean isMatchUserSize(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        int length = str.length();//getStrLength(str);
        if (length < 2 || length > 16) {
            return false;
        }
        return true;
    }

    public static boolean isMatchPhone(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            return false;
        }
        Pattern p = Pattern.compile(regexPhone);
        Matcher m = p.matcher(pwd);
        return m.matches();
    }

    public static boolean isMatchCheckCode(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            return false;
        }
        Pattern p = Pattern.compile(regexCheckCode);
        Matcher m = p.matcher(pwd);
        return m.matches();
    }

    /**
     * @param pwd 待验证的用户名
     * @return 验证结果，验证通过返回true，失败返回false
     */
    public static boolean isMatchWalletPwd(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            return false;
        }
        Pattern p = Pattern.compile(regexWallet);
        Matcher m = p.matcher(pwd);
        return m.matches();
    }

    public static boolean isMatchWalletSize(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        int length = getStrLength(str);
        if (length != 6) {
            return false;
        }
        return true;
    }

    /**
     * 获取字符串的长度，对双字符（包括汉字）按两位计数
     *
     * @param value
     * @return
     */
    public static int getStrLength(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }

    public static String generateCurrentSeconds() {
        String timeStr = String.valueOf(System.currentTimeMillis());

        return timeStr.substring(0, 10);
    }

    public static String generateRandom(int length) {
        Random random = new Random();
        char[] digits = new char[length];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < length; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        return new String(digits);
    }

    public static boolean isEmpty(String str){
        if(TextUtils.isEmpty(str) || str == null || "".equals(str.trim()) || "null".equals(str)){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isPinyin(String pinyin){
        if(TextUtils.isEmpty(pinyin))
            return false;
        int ascii = pinyin.charAt(0);
        return ascii >= 65 && ascii <= 90 || ascii >= 97 && ascii <= 122;
    }

}
