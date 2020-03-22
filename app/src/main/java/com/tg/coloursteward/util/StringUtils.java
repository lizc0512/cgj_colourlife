package com.tg.coloursteward.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    /**
     * 判断字符串是否为空
     *
     * @param cs
     * @return
     */
    public static boolean isEmpty(String cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * 判断字符串不为空
     *
     * @param cs
     * @return
     */
    public static boolean isNotEmpty(String cs) {
        return cs != null && cs.trim().length() > 0;
    }

    /**
     * 去掉空字符
     *
     * @param str
     * @return
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }


    /**
     * 判断密码只能由数字和字母组成
     *
     * @param password
     * @return
     */
    public static boolean checkPwdType(String password) {
        String checkID = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,18}$";
        Pattern pattern = Pattern.compile(checkID);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();

    }

    public static String getHandlePhone(String mobile) {
        int mobileLength = mobile.length();
        if (mobileLength == 11) {
            return mobile.substring(0, 3) + "****" + mobile.substring(mobileLength - 4, mobileLength);
        } else {
            return mobile;
        }
    }

    /**
     * 整形转换
     *
     * @param data 输入
     * @return Integer
     */
    public static Integer toInt(String data) {
        Integer result = -1;
        try {
            result = Integer.valueOf(data);
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * Long转换
     *
     * @param data 输入
     * @return Long
     */
    public static Long toLong(String data) {
        Long result = -1L;
        try {
            result = Long.valueOf(data);
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 浮点转换
     *
     * @param data 输入
     * @return Float
     */
    public static Float toFloat(String data) {
        Float result = -1.0f;
        if (data != null && data.length() > 0) {
            try {
                result = Float.valueOf(data);
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * 浮点转换
     *
     * @param data 输入
     * @return Double
     */
    public static Double toDouble(String data) {
        Double result = -1.0;
        try {
            result = Double.valueOf(data);
        } catch (Exception e) {
        }
        return result;
    }

}
