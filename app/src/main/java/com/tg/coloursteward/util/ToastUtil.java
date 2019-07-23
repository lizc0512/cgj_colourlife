package com.tg.coloursteward.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {

    private static Toast toast;//实现不管我们触发多少次Toast调用，都只会持续一次Toast显示的时长
    private static Boolean isshow = true;

    /**
     * 短时间显示Toast【居下】
     *
     * @param msg 显示的内容-字符串
     */
    public static void showShortToast(Context context, String msg) {
        if (null != context && !TextUtils.isEmpty(msg) && isshow) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 短时间显示Toast【居中】
     *
     * @param msg 显示的内容-字符串
     */
    public static void showShortToastCenter(Context context, String msg) {
        if (null != context && !TextUtils.isEmpty(msg) && isshow == true) {
            if (toast == null) {
                toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            } else {
                toast.setText(msg);
            }
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * 短时间显示Toast【居上】
     *
     * @param msg 显示的内容-字符串
     */
    public static void showShortToastTop(Context context, String msg) {
        if (null != context && !TextUtils.isEmpty(msg) && isshow == true) {
            if (toast == null) {
                toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            } else {
                toast.setText(msg);
            }
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
        }
    }

    /**
     * 长时间显示Toast【居下】
     *
     * @param msg 显示的内容-字符串
     */
    public static void showLongToast(Context context, String msg) {
        if (null != context && !TextUtils.isEmpty(msg) && isshow == true) {
            if (toast == null) {
                toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            } else {
                toast.setText(msg);
            }
            toast.setGravity(Gravity.BOTTOM, 0, dip2px(context, 64));
            toast.show();
        }
    }

    /**
     * 长时间显示Toast【居中】
     *
     * @param msg 显示的内容-字符串
     */
    public static void showLongToastCenter(Context context, String msg) {
        if (null != context && !TextUtils.isEmpty(msg) && isshow == true) {
            if (toast == null) {
                toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            } else {
                toast.setText(msg);
            }
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * 长时间显示Toast【居上】
     *
     * @param msg 显示的内容-字符串
     */
    public static void showLongToastTop(Context context, String msg) {
        if (null != context && !TextUtils.isEmpty(msg) && isshow == true) {
            if (toast == null) {
                toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            } else {
                toast.setText(msg);
            }
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
        }
    }

    /*=================================常用公共方法============================*/
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
