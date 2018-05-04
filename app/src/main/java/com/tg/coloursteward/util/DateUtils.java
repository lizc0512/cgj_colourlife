package com.tg.coloursteward.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

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
	 * 格式化日期，格式为：yyyy-MM-dd HH:mm:ss
	 * 
	 */
	public static String getStringDate(Date date) {
		return getStringDate(date, null);
	}

	/**
	 * 格式化日期
	 * 
	 * @param date
	 * @param format
	 *            默认格式为：yyyy-MM-dd HH:mm:ss
	 * @return String
	 */
	public static String getStringDate(Date date, String format) {
		if (StringUtils.isEmpty(format))
			format = DATE_FORMAT_TIME;

		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}

	/**
	 * 格式化日期，格式为：yyyy-MM-dd HH:mm:ss
	 * 
	 */
	public static Date getDateString(String value) {
		return getDateString(value, null);
	}

	/**
	 * 格式化日期，默认格式为yyyy-MM-dd HH:mm:ss
	 * 
	 * @param value
	 *            被格式化日期
	 * @param format
	 *            格式化格式
	 */
	public static Date getDateString(String value, String format) {
		if (StringUtils.isEmpty(format)) {
			format = DATE_FORMAT_TIME;
		}
		DateFormat dateFormate = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = dateFormate.parse(value);
		} catch (ParseException e) {
			Log.e(TAG, e.getMessage());
		}
		return date;
	}

	/**
	 * 根据长整形格式的时间，获取Date类型的时间
	 * 
	 * @param value
	 *            长整形时间（单位：秒）
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
	 * 
	 */
	public static long getLongDate(Date date) {
		return date.getTime() / 1000;
	}

	/**
	 * 获取长整形格式的时间，格式为：yyyy-MM-dd HH:mm:ss
	 * 
	 */
	public static long getLongDateString(String value) {
		return getLongDateString(value, null);
	}

	/**
	 * 获取长整形格式的时间，默认格式为yyyy-MM-dd HH:mm:ss
	 * 
	 * @param value
	 *            被格式化日期
	 * @param format
	 *            格式化格式
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
	 * @param strDate
	 *            参照日期
	 * @param days
	 *            差别的天数，正数表示获取之后的日期，负数表示获取之前的日期
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
	 * @param phpDate
	 *            php时间
	 * @param format
	 *            转换格式
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

}

