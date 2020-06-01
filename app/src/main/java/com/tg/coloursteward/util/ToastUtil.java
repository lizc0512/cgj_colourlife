package com.tg.coloursteward.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tg.coloursteward.R;

public class ToastUtil {

    private static Toast toast;//实现不管我们触发多少次Toast调用，都只会持续一次Toast显示的时长
    private static Boolean isshow = true;
    private static Toast onlyToast;
    private static long oneTime = 0;
    private static long twoTime = 0;
    private static CharSequence info = "";

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

    /***显示*/
    public static void onlyShowToast(Context context, CharSequence text) {
        show(context, text, Toast.LENGTH_SHORT);
    }

    private static void show(Context context, CharSequence text, int duration) {
        try {
            if (onlyToast == null) {
                onlyToast = Toast.makeText(context, text, duration);
                onlyToast.show();
                oneTime = System.currentTimeMillis();
            } else {
                twoTime = System.currentTimeMillis();
                if (info.equals(text)) {
                    if (twoTime - oneTime > 2500) {
                        onlyToast.show();
                    }
                } else {
                    info = text;
                    onlyToast.setText(text);
                    onlyToast.show();
                }
            }
            oneTime = twoTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 短时间显示Toast【居中】
     *
     * @param msg 显示的内容-字符串
     */
    public static void showShortToastCenter(Context context, String msg) {
        if (null != context && !TextUtils.isEmpty(msg) && isshow == true) {
            Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 300);
            toast.show();
        }
    }

    public static void showLoginToastCenter(Context context, String msg) {
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.login_toast_layout, null);
        Toast toast = new Toast(context);
        toast.setView(toastRoot);
        TextView tv = toastRoot.findViewById(R.id.toast_notice);
        tv.setText(msg);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
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
