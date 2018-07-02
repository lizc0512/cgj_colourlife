package com.youmai.hxsdk.utils;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.R;

/**
 * 作者：create by YW
 * 日期：2016.08.17 09:27
 * 描述：
 */
public class ToastUtil {

    private static Toast mToast;
    private static Toast mBottomToast;
    private static Toast mSaveToast;
    private static Toast mRepToast;
    private static Toast mPublicToast;

    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            if (mToast != null) {
                mHandler.removeCallbacks(r);
                mToast.cancel();
                mToast = null;
            }
        }
    };
    private static Runnable r2 = new Runnable() {
        public void run() {
            if (mSaveToast != null) {
                mSaveToast.cancel();
                mSaveToast = null;
            }
        }
    };

    private static Runnable r3 = new Runnable() {
        public void run() {
            if (mRepToast != null) {
                mHandler.removeCallbacks(r3);
                mRepToast.cancel();
                mRepToast = null;
            }
        }
    };

    private static Runnable r4 = new Runnable() {
        public void run() {
            if (mBottomToast != null) {
                mHandler.removeCallbacks(r4);
                mBottomToast.cancel();
                mBottomToast = null;
            }
        }
    };

    private static Runnable r5 = new Runnable() {
        public void run() {
            if (mPublicToast != null) {
                mHandler.removeCallbacks(r5);
                mPublicToast.cancel();
                mPublicToast = null;
            }
        }
    };

    public static void showToast(Context context, String message) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.hx_custom_toast_view, null);
        TextView text = (TextView) view.findViewById(R.id.toast_message);
        text.setText(message);
        if (mToast == null) {
            mToast = new Toast(context);
            mToast.setDuration(Toast.LENGTH_LONG);
            mToast.setGravity(Gravity.CENTER, 0, 0);//180
            mToast.setView(view);
        } else {
            mToast.setDuration(Toast.LENGTH_LONG);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.setView(view);
        }
        mHandler.postDelayed(r, 1800);// 延迟1秒隐藏toast
        mToast.show();
    }


    public static void showMidToast(Context context, String message) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.hx_mid_tip_toast, null);
        TextView text = (TextView) view.findViewById(R.id.rep_toast_message);
        text.setText(message);
        if (mRepToast == null) {
            mRepToast = new Toast(context);
            mRepToast.setDuration(Toast.LENGTH_LONG);
            mRepToast.setGravity(Gravity.CENTER, 0, 0);//180
            mRepToast.setView(view);
        } else {
            mRepToast.setDuration(Toast.LENGTH_LONG);
            mRepToast.setGravity(Gravity.CENTER, 0, 0);
            mRepToast.setView(view);
        }
        mHandler.postDelayed(r3, 1000);// 延迟1秒隐藏toast
        mRepToast.show();
    }


}
