package com.tg.coloursteward.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间转化工具
 */
public class DateUtils {
    private static final String TAG = DateUtils.class.getSimpleName();

    public static String DATE_FORMAT_DAY = "yyyy-MM-dd";
    public static String DATE_FORMAT_TIME = "yyyy-MM-dd HH:mm:ss";
    public static String DATE_FORMAT_TIME2 = "yyyy-MM-dd HH:mm";
    public static String DATE_FORMAT_MONTH_DAY_TIME = "MM-dd HH:mm";
    public static String DATE_FORMAT_CHINADAY = "yyyy年MM月dd日";
    public static String DATE_FORMAT_CHINATIME = "yyyy年MM月dd日 HH时mm分ss秒";
    public static String DATE_FORMAT_TEAR_MONTH = "yyyy年MM月";
    public static String DATE_FORMAT_DB = "yyyyMMddHHmmss";

    /**
     * 格式化日期
     *
     * @param date
     * @param format 默认格式为：yyyy-MM-dd HH:mm:ss
     * @return String
     */
    public static String getStringDate(Date date, String format) {
        if (StringUtils.isEmpty(format))
            format = DATE_FORMAT_TIME;

        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String getTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(new Date());
    }

    public static String getToday() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        String dateString = formatter.format(new Date());
        return dateString;
    }

    /**
     * 根据长整形格式的时间，获取Date类型的时间
     *
     * @param value 长整形时间（单位：秒）
     */
    public static Date getDateLong(long value) {
        return new Date(value * 1000);
    }

    /**
     * 获取当前的时间（单位：秒）
     */
    public static long getCurrentDate() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取长整形格式的时间
     */
    public static long getLongDate(Date date) {
        return date.getTime() / 1000;
    }

    /**
     * 获取长整形格式的时间，格式为：yyyy-MM-dd HH:mm:ss
     */
    public static long getLongDateString(String value) {
        return getLongDateString(value, null);
    }

    /**
     * 获取长整形格式的时间，默认格式为yyyy-MM-dd HH:mm:ss
     *
     * @param value  被格式化日期
     * @param format 格式化格式
     */
    public static long getLongDateString(String value, String format) {
        if (StringUtils.isEmpty(format)) {
            format = DATE_FORMAT_TIME;
        }
        DateFormat dateFormate = new SimpleDateFormat(format);
        long longDate = 0;
        try {
            Date date = dateFormate.parse(value);
            longDate = getLongDate(date);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
        return longDate;
    }

    /**
     * 根据指定日期，获得指定天数差别的日期
     *
     * @param date 参照日期
     * @param days 差别的天数，正数表示获取之后的日期，负数表示获取之前的日期
     * @return 获得的日期
     */
    public static Date getDistDate(Date date, int days) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, days);
            return cal.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * php时间转换成格式为："yyyy-MM-dd HH:mm:ss"的字符串
     *
     * @param phpDate
     * @return
     */
    public static String phpToTimeString(String phpDate) {

        String date = phpDate + "000";
        String dateTime = "";
        try {
            Long dateLong = Long.parseLong(date);

            SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_TIME);
            dateTime = df.format(dateLong);
        } catch (Exception e) {
        }

        return dateTime;

    }

    /**
     * php时间转换成字符串
     *
     * @param phpDate php时间
     * @param format  转换格式
     * @return
     */
    public static String phpToString(String phpDate, String format) {

        if (format == null) {
            format = DATE_FORMAT_DAY;
        }
        String date = phpDate + "000";
        String dateTime = "";
        try {
            Long dateLong = Long.parseLong(date);

            SimpleDateFormat df = new SimpleDateFormat(format);
            dateTime = df.format(dateLong);
        } catch (Exception e) {
        }

        return dateTime;

    }

    /**
     * 获取某个月的最大日期
     */
    public static int getMonthMaxDay(int year, int month) {
        int[] maxDays = new int[]{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if ((year % 400 == 0) || (year % 4 == 0 && year % 100 != 0))   //是闰年，2月为29天
            maxDays[2]++;
        return maxDays[month];
    }

    /**
     * 获取指定日期的星期
     */
    public static String getWeek(int year, int month, int day) {
        String[] weekStr = {"日", "一", "二", "三", "四", "五", "六"};
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, day);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        return "星期" + weekStr[week - 1];
    }

    /**
     * 比较两个时间
     *
     * @param DATE1
     * @param DATE2
     * @return 1: dt1在dt2后; -1: dt1在dt2前; 0: 相同
     */
    public static int compareDate(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {//dt1在dt2后
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {//dt1在dt2前
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取某月第一天时间
     *
     * @param date
     * @return
     */
    public static long getFirstDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, 0); //获取当前月第一天
        c.set(Calendar.DAY_OF_MONTH, 1); //设置为1号,当前日期既为本月第一天
        c.set(Calendar.HOUR_OF_DAY, 0); //将小时至0
        c.set(Calendar.MINUTE, 0); //将分钟至0
        c.set(Calendar.SECOND, 0); //将秒至0
        c.set(Calendar.MILLISECOND, 0); //将毫秒至0
        return c.getTimeInMillis() / 1000;
    }

    /**
     * 获取某月最后一天时间
     *
     * @param date
     * @return
     */
    public static long getEndDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int lastDay = c.getActualMaximum(c.DAY_OF_MONTH);
        c.add(Calendar.MONTH, 0); //获取当前月最后一天
        c.set(Calendar.DAY_OF_MONTH, lastDay); //设置为最后一号,当前日期既为本月最后一天
        c.set(Calendar.HOUR_OF_DAY, 23); //将小时至23
        c.set(Calendar.MINUTE, 59); //将分钟至59
        c.set(Calendar.SECOND, 59); //将秒至59
        c.set(Calendar.MILLISECOND, 0); //将毫秒至0
        return c.getTimeInMillis() / 1000;
    }
}

