package com.youmai.thirdbiz;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.thirdbiz.colorful.ui.FeedBackActivity;
import com.youmai.thirdbiz.colorful.ui.SelectCommunityActivity;

/**
 * 作者：create by YW
 * 日期：2017.05.12 18:11
 * 描述：第三方APP跳转界面
 */
public class ThirdActivityHelper {

    //意见反馈
    public static void feedBackActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, FeedBackActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    //设为业主
    public static void selectCommunityActivity(Context context, String dstPhone) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, SelectCommunityActivity.class);
        intent.putExtra("dstPhone", dstPhone);
        context.startActivity(intent);
    }

    //开门
    public static void openDoor(Context context, String packageName, String activityClass) {
        try {
            Intent it = new Intent();
            it.setClassName(packageName, activityClass);
            it.putExtra("extra_float", 1); //不明确
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.e("xx", "pack=" + packageName + " act=" + activityClass);
            context.startActivity(it);
        } catch (Exception e) {
            LogUtils.e("CzyUser", e.toString());
        }
    }

}
