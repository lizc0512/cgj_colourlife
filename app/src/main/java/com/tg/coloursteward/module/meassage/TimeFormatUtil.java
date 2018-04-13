package com.tg.coloursteward.module.meassage;

import android.content.Context;

import com.tg.coloursteward.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by huangyr on 2017/9/21.
 */

public class TimeFormatUtil {
    //时间转化
    public static String convertTimeMillli(Context context, long timestamp) {
        String timeStr = null;
        String DATE_FORMAT = "yy-MM-dd HH:mm";
        String DATE_FORMAT1 = "HH:mm";
        String DATE_FORMAT2 = "yy-MM-dd";
        String DATE_FORMAT3 = "yyyy";
        String DATE_FORMAT4 = "MM-dd HH:mm";
        String DATE_FORMAT_YMD = "yyyy-MM-dd";
        //今天时间的年月日
        Date date1 = new Date();
        date1.setTime(System.currentTimeMillis());
        String timeStr1 = new SimpleDateFormat(DATE_FORMAT2, Locale.getDefault()).format(date1);
        //今天时间的年
        String timeStr1y = new SimpleDateFormat(DATE_FORMAT3, Locale.getDefault()).format(date1);

        //传入时间的年月日
        Date date2 = new Date();
        date2.setTime(timestamp);
        String timeStr2 = new SimpleDateFormat(DATE_FORMAT2, Locale.getDefault()).format(date2);
        //传入时间的年
        String timeStr2y = new SimpleDateFormat(DATE_FORMAT3, Locale.getDefault()).format(date2);
        //昨天时间的年月日
        Date yesterdayDate = new Date();
        yesterdayDate.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));//当前时间减24小时一定是昨天
        String yesterdayTime = new SimpleDateFormat(DATE_FORMAT2, Locale.getDefault()).format(yesterdayDate);
        //前天时间的年月日
        Date beforeYesterdayDate = new Date();
        beforeYesterdayDate.setTime(System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000));//当前时间减2*24小时是前天
        String beforeYesterdayTime = new SimpleDateFormat(DATE_FORMAT2, Locale.getDefault()).format(beforeYesterdayDate);

        if (timeStr1.equals(timeStr2)) { //今天
            timeStr = new SimpleDateFormat(DATE_FORMAT1, Locale.getDefault()).format(date2);
        } else if (timeStr2.equals(yesterdayTime)) { //昨天
            timeStr = context.getString(R.string.message_time_yesterday) + (new SimpleDateFormat(DATE_FORMAT1, Locale.getDefault()).format(date2));
        } else if (timeStr2.equals(beforeYesterdayTime)) { //前天 
            timeStr = context.getString(R.string.message_time_before_yesterday) + (new SimpleDateFormat(DATE_FORMAT1, Locale.getDefault()).format(date2));
        } else { //昨天前
            /*if (timeStr1y.equals(timeStr2y)) {
                //今年
                timeStr = new SimpleDateFormat(DATE_FORMAT4, Locale.getDefault()).format(date2);
            } else {
                //不是今年
                timeStr = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(date2);
            }*/
            timeStr = new SimpleDateFormat(DATE_FORMAT_YMD, Locale.getDefault()).format(date2);
        }
        return timeStr;
    }

}
