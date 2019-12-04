package com.tg.point.utils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * User: howie
 * Date: 13-5-11
 * Time: 下午4:09
 */
public class TimeUtil {

    /**
     * xlistview
     */
    public static String timeAgo(String timeStr) {
        Date date = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss zzz");
            date = format.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        long timeStamp = date.getTime();
        Date currentTime = new Date();
        long currentTimeStamp = currentTime.getTime();
        long seconds = (currentTimeStamp - timeStamp) / 1000;
        long minutes = Math.abs(seconds / 60);
        long hours = Math.abs(minutes / 60);
        long days = Math.abs(hours / 24);
        if (seconds < 60) {
            return "刚刚";
        } else if (seconds < 120) {
            return "1分钟前";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (minutes < 120) {
            return "1小时前";
        } else if (hours < 24) {
            return hours + "小时前";
        } else if (hours < 24 * 2) {
            return "1天前";
        } else if (days < 30) {
            return days + "天前";
        } else if (days < 365) {
            return new BigDecimal(days / 30).setScale(0, BigDecimal.ROUND_HALF_UP) + "个月前";
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
            String dateString = formatter.format(date);
            return dateString;
        }
    }


    public static long getTimeStamp(String s) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        long ts = 0;
        try {
            date = formatter.parse(s);
            ts = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ts / 1000;
    }

    public static int getCurrentMonthLastDay() {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }


    /**
     * 邻里
     ***/
    public static String timeAgoInt(int timeStr) {
        Date date = new Date((long) timeStr * 1000);
        long timeStamp = date.getTime();

        Date currentTime = new Date();
        long currentTimeStamp = currentTime.getTime();
        long seconds = (currentTimeStamp - timeStamp) / 1000;

        long minutes = Math.abs(seconds / 60);
        long hours = Math.abs(minutes / 60);
        long days = Math.abs(hours / 24);

        if (seconds < 60) {
            return "刚刚";
        } else if (seconds < 120) {
            return "1分钟前";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (minutes < 120) {
            return "1小时前";
        } else if (hours < 24) {
            return hours + "小时前";
        } else if (hours < 24 * 2) {
            return "昨天";
        } else if (days < 30) {
            return days + "天前";
        } else if (days < 365) {
            return new BigDecimal(days / 30).setScale(0, BigDecimal.ROUND_HALF_UP) + "个月前";
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = formatter.format(date);
            return dateString;
        }
    }


    /**
     * @param timeStr
     * @return 消息通知页面显示时间类型
     */
    public static String noticeTime(long timeStr) {
        Date date = new Date(timeStr * 1000);
        Calendar todayCalendar = Calendar.getInstance();
        Calendar otherCalendar = Calendar.getInstance();
        otherCalendar.setTimeInMillis(timeStr * 1000);
        boolean yearTemp = todayCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR);
        SimpleDateFormat dateFormat = null;
        String dateString = "";
        if (yearTemp) {
            int todayMonth = todayCalendar.get(Calendar.MONTH);
            int otherMonth = otherCalendar.get(Calendar.MONTH);
            if (todayMonth == otherMonth) {//表示是同一个月
                int temp = todayCalendar.get(Calendar.DATE) - otherCalendar.get(Calendar.DATE);
                switch (temp) {
                    case 0:
                        dateFormat = new SimpleDateFormat("HH:mm");
                        dateString = dateFormat.format(date);
                        break;
                    case 1:
                        dateFormat = new SimpleDateFormat("HH:mm");
                        dateString = "昨天 " + dateFormat.format(date);
                        break;
                    case 2:
                        dateFormat = new SimpleDateFormat("HH:mm");
                        dateString = "前天 " + dateFormat.format(date);
                        break;
                    default:
                        dateFormat = new SimpleDateFormat("MM月dd日 HH:mm");
                        dateString = dateFormat.format(date);
                        break;
                }
            } else {
                dateFormat = new SimpleDateFormat("MM月dd日 HH:mm");
                dateString = dateFormat.format(date);
            }
        } else {
            dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
            dateString = dateFormat.format(date);
        }
        return dateString;
    }

    /**
     * 小于60秒：X秒前
     * 大于60秒小于1小时：X分钟前
     * 大于1小时但在当前日期的24点前：X小时前
     * 次日：昨天
     * 超过“次日”但在当年内：X月X日
     * 超过当年：xxxx/xx/xx
     */
    public static String formatHomeTime(long timeStr) {
        Date date = new Date(timeStr * 1000);
        long timeStamp = date.getTime();
        Date currentTime = new Date();
        long currentTimeStamp = currentTime.getTime();
        long seconds = (currentTimeStamp - timeStamp) / 1000;
        long minutes = Math.abs(seconds / 60);
        long hours = Math.abs(minutes / 60);
        Calendar todayCalendar = Calendar.getInstance();
        Calendar otherCalendar = Calendar.getInstance();
        otherCalendar.setTimeInMillis(timeStr * 1000);
        boolean yearTemp = todayCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR);
        String result = "";
        if (yearTemp) {
            int todayMonth = todayCalendar.get(Calendar.MONTH);
            int otherMonth = otherCalendar.get(Calendar.MONTH);
            if (todayMonth == otherMonth) {//表示是同一个月
                int week = otherCalendar.get(Calendar.DAY_OF_WEEK);
                String weekStr = "";
                /*星期日:Calendar.SUNDAY=1
                 *星期一:Calendar.MONDAY=2
                 *星期二:Calendar.TUESDAY=3
                 *星期三:Calendar.WEDNESDAY=4
                 *星期四:Calendar.THURSDAY=5
                 *星期五:Calendar.FRIDAY=6
                 *星期六:Calendar.SATURDAY=7 */
                if (seconds <= 60) {
                    result = "刚刚";
                } else if (minutes < 60) {
                    result = minutes + "分钟前";
                } else if (hours < 24) {
                    result = hours + "小时前";
                } else {
                    int temp = todayCalendar.get(Calendar.DATE) - otherCalendar.get(Calendar.DATE);
                    switch (temp) {
                        case 0:
                            result = hours + "小时前";
                            break;
                        case 1:
                            result = "昨天";
                            break;
                        case 2:
                            result = "前天";
                            break;
                        default:
                            result = dateformatTime2(date);
                            break;
                    }
                }
            } else {
                result = dateformatTime2(date);
            }
        } else {
            result = getTime(timeStr * 1000, "yyyy-MM-dd");
        }
        return result;
    }


    /**
     * 功能：计算时间差
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static String dateDiff(long startTime, long endTime) {
        long diff = endTime - startTime;
        return dateDiff(diff);
    }

    public static String dateDiff(long diff) {
        // 按照传入的格式生成一个simpledateformate对象
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
        long nh = 1000 * 60 * 60;// 一小时的毫秒数
        long nm = 1000 * 60;// 一分钟的毫秒数
        long day = 0;
        long hour = 0;
        long min = 0;
        // 获得两个时间的毫秒时间差异
        day = diff / nd;// 计算差多少天
        hour = diff % nd / nh + day * 24;// 计算差多少小时
        min = diff % nd % nh / nm + day * 24 * 60;// 计算差多少分钟
        if (day > 0) {
            return day + "天" + (hour - day * 24) + "小时" + (min - day * 24 * 60) + "分钟";
        } else if (hour > 0) {
            return (hour - day * 24) + "小时" + (min - day * 24 * 60) + "分钟";
        } else {
            return min - day * 24 * 60 + "分钟";
        }
    }

    public static String dateDiffTime(int minutes) {
        // 按照传入的格式生成一个simpledateformate对象
        int hour = minutes / 60;
        int day = hour / 24;
        if (day > 0) {
            return day + "天" + (hour - day * 24) + "小时" + (minutes - day * 24 * 60) + "分钟";
        } else if (hour > 1) {
            return hour + "小时" + (minutes - hour * 60) + "分钟";
        } else {
            return minutes + "分钟";
        }
    }


    public static String dateDiff(String begin_time, String end_time) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d1 = df.parse(begin_time);
            Date d2 = df.parse(end_time);
            return dateDiff(d1.getTime(), d2.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getToday() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        String dateString = formatter.format(new Date());
        return dateString;
    }

    public static String getTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(new Date());
    }

    /**
     * @param timeStr
     * @return yyyy-MM-dd HH:mm
     */
    public static String formatTime(String timeStr, String formatRule) {
        Date date = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = format.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(formatRule);
        String dateString = formatter.format(date);
        return dateString;
    }

    public static long formatContractCostTime(String timeStr) {
        Date date = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = format.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return System.currentTimeMillis() / 1000;
        }
        return date.getTime() / 1000;
    }

    /**
     * return XX月XX日
     *
     * @param date
     * @return
     */
    public static String dateformatTime2(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日");
        String dateString = formatter.format(date);
        return dateString;
    }

    public static String getDateToString(long timeStap) {
        Date d = new Date(timeStap * 1000);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sf.format(d);
    }

    public static String getDateToString(Date date) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        return sf.format(date);
    }


    /**
     * 时间戳格式转换
     */
    static String dayNames[] = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    public static String getNewChatTime(long timesamp) {
        String result = "";
        Calendar todayCalendar = Calendar.getInstance();
        Calendar otherCalendar = Calendar.getInstance();
        otherCalendar.setTimeInMillis(timesamp);
        String timeFormat = "M月d日 HH:mm";
        String yearTimeFormat = "yyyy年M月d日 HH:mm";
        String am_pm = "";
        int hour = otherCalendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 0 && hour < 6) {
            am_pm = "凌晨";
        } else if (hour >= 6 && hour < 12) {
            am_pm = "上午";
        } else if (hour == 12) {
            am_pm = "中午";
        } else if (hour > 12 && hour < 18) {
            am_pm = "下午";
        } else if (hour >= 18) {
            am_pm = "晚上";
        }
        timeFormat = "M月d日 " + am_pm + "HH:mm";
        yearTimeFormat = "yyyy年M月d日 " + am_pm + "HH:mm";
        boolean yearTemp = todayCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR);
        if (yearTemp) {
            int todayMonth = todayCalendar.get(Calendar.MONTH);
            int otherMonth = otherCalendar.get(Calendar.MONTH);
            if (todayMonth == otherMonth) {//表示是同一个月
                int temp = todayCalendar.get(Calendar.DATE) - otherCalendar.get(Calendar.DATE);
                switch (temp) {
                    case 0:
                        result = am_pm + getHourAndMin(timesamp);
                        break;
                    case 1:
                        result = "昨天 ";
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        result = dayNames[otherCalendar.get(Calendar.DAY_OF_WEEK) - 1];
//                        int dayOfMonth = otherCalendar.get(Calendar.WEEK_OF_MONTH);
//                        int todayOfMonth = todayCalendar.get(Calendar.WEEK_OF_MONTH);
//                        if (dayOfMonth == todayOfMonth) {//表示是同一周
//                            int dayOfWeek = otherCalendar.get(Calendar.DAY_OF_WEEK);
//                            if (dayOfWeek != 1) {//判断当前是不是星期日     如想显示为：周日 12:09 可去掉此判断
//                               result = dayNames[otherCalendar.get(Calendar.DAY_OF_WEEK) - 1] + getHourAndMin(timesamp);
//                            } else {
//                                result = getTime(timesamp, timeFormat);
//                            }
//                        } else {
//                            timeFormat = "M月d日";
//                            result = getTime(timesamp, timeFormat);
//                        }
                        break;
                    default:
                        result = getTime(timesamp, timeFormat);
                        break;
                }
            } else {
                result = getTime(timesamp, timeFormat);
            }
        } else {
            result = getYearTime(timesamp, yearTimeFormat);
        }
        return result;
    }

    /**
     * 当天的显示时间格式
     *
     * @param time
     * @return
     */
    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    /**
     * 不同一周的显示时间格式
     *
     * @param time
     * @param timeFormat
     * @return
     */
    public static String getTime(long time, String timeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        return format.format(new Date(time));
    }

    /**
     * 不同年的显示时间格式
     *
     * @param time
     * @param yearTimeFormat
     * @return
     */
    public static String getYearTime(long time, String yearTimeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(yearTimeFormat);
        return format.format(new Date(time));
    }

    public static String getYearAndMonth(long timeStap) {
        Calendar todayCalendar = Calendar.getInstance();
        Calendar otherCalendar = Calendar.getInstance();
        otherCalendar.setTimeInMillis(timeStap);
        boolean yearTemp = todayCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR);
        String showDate = "";
        if (yearTemp) {
            int todayMonth = otherCalendar.get(Calendar.MONTH) + 1;
            showDate = todayMonth + "月";
        } else {
            showDate = getYearMonthToString(timeStap);
        }
        return showDate;
    }

    public static String getYearMonthToString(long timeStap) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月");
        return sf.format(timeStap);
    }

    public static String getYearAndMonthFormat(long timeStap) {
        Calendar todayCalendar = Calendar.getInstance();
        Calendar otherCalendar = Calendar.getInstance();
        otherCalendar.setTimeInMillis(timeStap);
        boolean yearTemp = todayCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR);
        String showDate;
        if (yearTemp) {
            int todayMonth = otherCalendar.get(Calendar.MONTH) + 1;
            showDate = todayMonth + "月";
        } else {
            showDate = getYearMonthToStringFormat(timeStap);
        }
        return showDate;
    }

    private static String getYearMonthToStringFormat(long timeStap) {
        String year = getTime(timeStap, "yyyy") + "年";
        String month = getTime(timeStap, "MM").replaceAll("^(0+)", "") + "月";//去除前面的0
        return year + month;
    }
}
