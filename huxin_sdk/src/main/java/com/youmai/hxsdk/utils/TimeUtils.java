package com.youmai.hxsdk.utils;

import android.content.Context;

import com.youmai.hxsdk.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * TimeUtils
 */
public class TimeUtils {

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.CHINA);
    public static final SimpleDateFormat YEAR_MONTH_FORMAT = new SimpleDateFormat(
            "yyyyMM", Locale.CHINA);
    public static final SimpleDateFormat MONTH_FORMAT_DATE = new SimpleDateFormat(
            "MM-dd", Locale.CHINA);
    public static final SimpleDateFormat MINUTE_FORMAT_DATE = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm", Locale.CHINA);


    private TimeUtils() {
        throw new AssertionError();
    }

    /**
     * long time to string
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @param timeInMillis
     * @return
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }

    /**
     * 获取当前天
     *
     * @param timeInMillis
     * @return
     */
    public static String getDate(long timeInMillis) {
        return getTime(timeInMillis, DATE_FORMAT_DATE);
    }

    /**
     * 日期格式中解析日历
     *
     * @param str
     * @param sdf
     * @return
     * @throws ParseException
     */
    public static Calendar parseDate(String str, SimpleDateFormat sdf) throws ParseException {
        Date date = sdf.parse(str);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @return
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    public static String getFormateDate(long timeStamp) {
        Date date = new Date(timeStamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(date);
    }

    public static String getFormateDate2(long timeStamp) {
        Date date = new Date(timeStamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public static String getFormateDate3(long timeStamp) {
        Date date = new Date(timeStamp);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }

    public static String getFormateDate4(long timeStamp) {
        Date date = new Date(timeStamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(date);
    }

    /**
     * @param timeStamp 时间戳
     * @return 格式：月-日（当年）、年-月-日（去年以前）
     */
    public static String getFormateDate5(long timeStamp) {

        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int yearNow = calendar.get(Calendar.YEAR);
        Date date = new Date(timeStamp);
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        SimpleDateFormat sdf;
        if (year == yearNow) {
            sdf = new SimpleDateFormat("MM-dd HH:mm");
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
        return sdf.format(date);
    }

    /**
     * 将毫秒时间转化为 “00:00:00”格式时间
     */
    public static String getTimeFromMillisecond(long time) {

        if (time <= 0) {
            return "00:00";
        }

        int seconds = (int) (time / 1000);  //转换为秒数

        int hour = seconds / 3600;
        int minute = seconds / 60;
        int second = seconds % 60;

        if (hour >= 1) {
            return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format(Locale.CHINA, "%02d:%02d", minute, second);
        }

    }


    /**
     * 将毫秒时间转化为秒 “00”格式时间
     */
    public static String getTimeFromSecond(long time) {

        if (time <= 0) {
            return "00";
        }
        int second = (int) (time / 1000);
        String f = String.valueOf(second);
        return f;
    }

    /**
     * 将毫秒时间转化为秒 “ 00" ”格式时间
     */
    public static String getTimeSeconds(Context context, long time) {
        String format = context.getResources().getString(R.string.collect_voice_second);

        if (time <= 0) {
            return String.format(Locale.CHINA, format, 0);
        }
        int second = (int) (time / 1000);
        return String.format(Locale.CHINA, format, second);
    }


    /**
     * 单位秒转时间
     *
     * @return 20小时18分钟30秒
     */
    public static String calculateTime(Context context, int talkTime) {
        int hours = 0;
        int minute = 0;
        int second = 0;

        int time = talkTime;

        if (time <= 0) {
            time = -time;
        }

        if (time >= 3600) {
            hours = time / 3600;
            minute = time % 3600 / 60;
            second = time % 3600 % 60;
        } else if (time >= 60) {
            minute = time / 60;
            second = time % 60;
        }
        String timeStr;
        if (hours > 0) {
            timeStr = hours + context.getString(R.string.hx_hook_strategy_hour) + minute + context.getString(R.string.hx_hook_strategy_minute) + second + context.getString(R.string.hx_hook_strategy_second);
        } else if (minute > 0 && hours == 0) {
            timeStr = minute + context.getString(R.string.hx_hook_strategy_minute) + second + context.getString(R.string.hx_hook_strategy_second);
        } else {
            timeStr = time + context.getString(R.string.hx_hook_strategy_second);
        }
        return timeStr;
    }

    /**
     * 套餐结束时间
     *
     * @param month 可使用多少个月
     * @return 截止日期
     */
    public static String getComboEndTime(int month) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int yearNow = calendar.get(Calendar.YEAR);
        int monthEnd = calendar.get(Calendar.MONTH) + month;
        int dayNow = calendar.get(Calendar.DAY_OF_MONTH);
        Calendar startCal = Calendar.getInstance(Locale.CANADA);
        startCal.set(yearNow, monthEnd, dayNow);
        calendar.getTimeInMillis();
        Date date = new Date(startCal.getTimeInMillis());
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    // 获得当天24点时间
    public static int getNightTimestamp() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int) (cal.getTimeInMillis() / 1000);
    }

    // 获取系统时间的10位的时间戳
    public static int getCurrentTimestamp() {
        return (int) (System.currentTimeMillis() / 1000);
    }

}
