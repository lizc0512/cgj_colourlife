package com.tg.coloursteward.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
     * 电话号码去掉"-"、"+86" 、" "
     *
     * @param str
     * @return
     */
    public static String trimPhone(String str) {

        return str == null ? null : str.trim().replace(" ", "")
                .replace("-", "").replace("+86", "");
    }

    /**
     * 检查email合法
     *
     * @param emailString
     * @return
     */
    public static boolean checkEmail(String emailString) {

        String checkemail = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

        Pattern pattern = Pattern.compile(checkemail);

        Matcher matcher = pattern.matcher(emailString);

        return matcher.matches();

    }

    /**
     * 检查身份证是否合法
     *
     * @param identityno ID身份证
     * @return
     */
    public static boolean checkID(String identityno) {
        String checkID = "^(\\d{14}|\\d{17})(\\d|[xX])$";
        Pattern pattern = Pattern.compile(checkID);

        Matcher matcher = pattern.matcher(identityno);
        return matcher.matches();

    }

    /**
     * 判断密码只能由数字和字母组成
     * @param password
     * @return
     */
    public static boolean checkPwdType(String password) {
        String checkID = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,18}$";
        Pattern pattern = Pattern.compile(checkID);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();

    }

    /**
     * 检查手机号码合法
     *
     * @param strPhoneNum
     * @return
     */
    public static boolean checkPhoneNumberValid(String strPhoneNum) {

        String checkphone = "^((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0-9]))\\d{8}$";

        Pattern pattern = Pattern.compile(checkphone);

        Matcher matcher = pattern.matcher(strPhoneNum);

        return matcher.matches();

    }

    public static boolean isURL(String str) {
        // 转换为小写
        str = str.toLowerCase();
        String regex = "^((https|http|ftp|rtsp|mms)?://)"
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" // ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // first level domain- .com or .museum
                + "(:[0-9]{1,4})?" // 端口- :80
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher isUrl = pattern.matcher(str);
        return isUrl.matches();

    }

    /**
     * 判断字符串是否包含中文
     *
     * @param str
     * @return
     */
    public static boolean isContainsChinese(String str) {
        String regEx = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(regEx);
        Matcher matcher = pat.matcher(str);
        boolean flg = false;
        if (matcher.find()) {
            flg = true;
        }
        return flg;
    }

    /**
     * 判断字符串是否图片的URL
     *
     * @param url
     * @return
     */
    public static boolean isImageUrl(String url) {

        if (isNotEmpty(url)) {

            if (url.lastIndexOf(".") != -1) {

                String suffix = url.substring(url.lastIndexOf(".")).trim();

                if (suffix != null && suffix.trim().length() > 0) {

                    if (".jpg".equalsIgnoreCase(suffix)
                            || ".png".equalsIgnoreCase(suffix)
                            || ".jpeg".equalsIgnoreCase(suffix)) {
                        return true;
                    }
                }
            }

        }

        return false;
    }

    /**
     * 判断字符串图片是什么格式
     *
     * @param url
     * @return
     */
    public static boolean isImageLayout(String url) {

        if (isNotEmpty(url)) {

            if (url.lastIndexOf(".") != -1) {

                String suffix = url.substring(url.lastIndexOf(".")).trim();

                if (suffix != null && suffix.trim().length() > 0) {
                    if (".gif".equalsIgnoreCase(suffix)
                            || ".png".equalsIgnoreCase(suffix)
                            || ".jpeg".equalsIgnoreCase(suffix)
                            || ".jpg".equalsIgnoreCase(suffix)) {
                        return true;
                    }
                }
            }

        }

        return false;
    }

    /**
     * 判断字符串图片是否时png格式
     *
     * @param url
     * @return
     */
    public static boolean isImagePNG(String url) {

        if (isNotEmpty(url)) {

            if (url.lastIndexOf(".") != -1) {

                String suffix = url.substring(url.lastIndexOf(".")).trim();

                if (suffix != null && suffix.trim().length() > 0) {
                    if (".png".equalsIgnoreCase(suffix)) {
                        return true;
                    }
                }
            }

        }

        return false;
    }

    /**
     * 把图片转换成jpg上传
     *
     * @param fileurl
     * @return
     */
    public static String fileUrl(String fileurl) {
        String suffix = null;
        String url = null;

        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            suffix = fileurl.substring(fileurl.lastIndexOf(".")).trim();
            url = fileurl.substring(0, (fileurl.length() - suffix.length()))
                    + ".jpg";
            inputStream = new FileInputStream(new File(fileurl));
            outputStream = new FileOutputStream(url);
            byte b[] = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, len);
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (len != 0) {
                return url + "";
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return null;
    }

    public static String getImei(Context context, String imei) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
        } catch (Exception e) {
            Log.e("StringUtils", e.getMessage());
        }
        return imei;
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
            // TODO
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
            // TODO
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
                // TODO
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
            // TODO
        }

        return result;
    }

}
