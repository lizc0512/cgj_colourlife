package com.youmai.hxsdk.utils;

import android.content.Context;

import com.youmai.hxsdk.entity.MccCode;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 作者：create by YW
 * 日期：2016.09.14 11:02
 * 描述：
 */
public class PhoneNumTypes {

    public final static int PT_MSISDN = 1; // 手机
    public final static int PT_FIXED = 2; // 固话
    public final static int PT_NOFIXED = 3; // 固话
    public final static int PT_SPECIAL = 4; // 特号号码， 如95555, 4001234456
    public final static int PT_INT = 5; // 国际号码
    public final static int PT_OTHER = 6; // 其它


    /*
     * 是否手机号判断,仅限中国大陆
	 */
    public static boolean isMsisdn(String phone) {
        if (StringUtils.isEmpty(phone))
            return false;

        Pattern pattern = Pattern.compile("^1[3,4,7,5,8][0-9]{9}$");
        Matcher matcher = pattern.matcher(phone);

        return matcher.matches();
    }

    /*
     * 获取手机类型 FIX 021112/0755111111问题 2016/3/9 by zhangzh
     */
    public static int getPhoneType(String phone) {
        if (StringUtils.isEmpty(phone))
            return PT_OTHER;

        // 133 0000 1111问题
        phone = trimNumber(phone);
        if (phone.startsWith("0")) {
            switch (phone.length()) {
                case 6: // 021112
                case 7: // 0755112
                case 8: // 02195123
                case 9: // 075598888
                    return PT_SPECIAL;
                case 11:
                case 12:
                    Pattern pattern = Pattern.compile("^0[1-9][0-9]{9,10}$");
                    Matcher matcher = pattern.matcher(phone);
                    if (matcher.matches())
                        return PT_FIXED;
            }
            return PT_OTHER;
        }
        switch (phone.length()) {
            case 3:
                if (phone.startsWith("1"))
                    return PT_SPECIAL;
                break;
            case 4:
                if (phone.length() == 4 && !phone.startsWith("0"))
                    return PT_SPECIAL;
                break;
            case 5:
                if (phone.startsWith("9") || phone.startsWith("1"))
                    return PT_SPECIAL;
                break;
            case 7:
            case 8:
                Pattern pattern1 = Pattern.compile("^[1-8][0-9]{6,7}$");
                Matcher matcher1 = pattern1.matcher(phone);
                if (matcher1.matches())
                    return PT_NOFIXED;
                break;
            case 10:
                if (phone.startsWith("400"))
                    return PT_SPECIAL;

            case 11:
                Pattern pattern = Pattern.compile("^1[3,4,5,7,8][0-9]{9}$");
                Matcher matcher = pattern.matcher(phone);
                if (matcher.matches())
                    return PT_MSISDN;
                break;

            default:
                if (phone.startsWith("00"))
                    return PT_INT;
                break;
        }
        return PT_OTHER;
    }

    /* 号码处理 */
    final static String[] mIpPrefix = {"17951", "12593", "17911", "17909", "19389", "10193"};

    public static String trimNumber(String number) {
        if (number == null)
            return "";

        number = number.replace(" ", "").trim();
        if (number.startsWith("+")) {
            String overNumber = "00" + number.substring(1);

            if (overNumber.startsWith("0086")) {
                String s = overNumber.substring(4);
                if (isMsisdn(s)) {
                    // +8613800138000
                    overNumber = s;
                } else {
                    // +86755xxxx
                    // +860755xxxx
                    // 不考虑+86013800138000
                    Pattern pattern = Pattern.compile("^[1-9][0-9]{9,10}$");
                    Matcher matcher = pattern.matcher(s);
                    if (matcher.matches()) {
                        overNumber = '0' + s;
                    } else {
                        overNumber = s;
                    }
                }
                return overNumber;
            }
        }

        for (String prefix : mIpPrefix) {
            if (number.startsWith(prefix)) {
                number = number.substring(5);
                break;
            }
        }
        return number;
    }

    /**
     * 号码转国际号码，大陆不需要加区号
     * 0086155-->155        (大陆)
     * 0044123-->+44123     (英国)
     * 0095123-->+950123    (缅甸)
     * <p>
     * 默认使用本机号码mcc
     *
     * @param phone 123
     * @return +86123
     */
    public static String changePhone(String phone, Context context) {
        return changePhone(phone, MccUtils.getMccCode(PhoneImsi.getMCC(context), context));
    }

    /**
     * 号码转国际号码，大陆不需要加区号
     * 0086155-->155        (大陆)
     * 0044123-->+44123     (英国)
     * 0095123-->+950123    (缅甸)
     * <p>
     * 国际版的需要提交mcc，此处可自定义
     *
     * @param phone   123
     * @param mccCode 区号
     * @return +86123
     */
    public static String changePhone(String phone, String mccCode) {

        String internationalPhone;
        String code = mccCode;//区号 +86
        if (!StringUtils.isEmpty(code)) {
            String codeStr = code.substring(1);//去掉"+",仅数字区号86      ：+86-->86
            //清掉区号
            if (!phone.contains("+")) {
                //0086132
                phone = phone.replaceFirst("00" + codeStr, "");//去掉区号
            } else {
                //+86123
                phone = phone.replace(code, "");
            }

            //剩下的号码
            switch (code) {
                case "+95"://缅甸(+95)	开头加0
                    phone = phone.replaceFirst("^0*", "");//去掉前面的0
                    phone = "0" + phone;
                    break;
                case "+44"://英国(+44)	省略开头0
                    phone = phone.replaceFirst("^0*", "");//去掉前面的0
                    break;
                case "+52"://墨西哥(+52) 国际区号和电话号码之间添加1
                    phone = "1" + phone;
                    break;
                case "+54"://阿根廷(+54)	国际区号和电话号码之间添加9
                    phone = "9" + phone;
                    break;
            }

            //Socket上大陆不需要加+86，否则发送不成功
            if (code.equals("+86")) {
                internationalPhone = phone;
            } else {
                internationalPhone = code + phone;
            }
        } else {
            internationalPhone = phone;
        }
        return internationalPhone;
    }

    /**
     * 去掉区号
     */
    public static String deleteZoomPhone(Context context, String phone, String mcc) {

        String internationalPhone;
        String code = MccUtils.getMccCode(mcc, context);
        try {
            internationalPhone = phone.replace(code, "");
        } catch (Exception e) {
            internationalPhone = phone;
        }
        return internationalPhone;
    }


    /**
     * 获取mcc的位置
     */
    public static int getIndexForMcc(List<MccCode> mccCodes, String mcc) {
        int index = 0;
        if (mcc.equals("")) {
            mcc = "460";//+86大陆的mcc
        }
        if (mccCodes != null) {
            for (int i = 0; i < mccCodes.size(); i++) {
                if (mccCodes.get(i).getMcc().equals(mcc)) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    /**
     * 国际号码转成正常号码
     * +8618002587121 ---> 18002587121
     *
     * @param phoneNumber
     * @return
     */
    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return "";
        } else {
            if (phoneNumber.startsWith("+86")) {
                return phoneNumber.replace("+86", "");
            }
            return phoneNumber;
        }
    }
}
