package com.tg.coloursteward.push;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tg.user.activity.LoginActivity;
import com.tg.coloursteward.MainActivity;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 *
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    private Context aContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        aContext = context;
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            processCustomMessage(context, bundle);
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            Boolean mainCreat = Tools.getMainStatus(aContext);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            //打开自定义的Activity
            if(!mainCreat)
            {
                startAPP("com.tg.coloursteward",extras);
            }else{
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    Intent intent2 = new Intent(aContext, MainActivity.class);
                    if (StringUtils.isNotEmpty(extras)) {
                        if (null != extraJson && extraJson.length() > 0) {
                            intent2.putExtra(MainActivity.KEY_EXTRAS, extras);
                        }
                    }
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    aContext.startActivity(intent2);
                } catch (Exception e) {
                    Toast.makeText(aContext, "没有安装", Toast.LENGTH_LONG).show();
                }
            }
           /* if (!isApplicationBroughtToBackground(aContext)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    Intent intent2 = new Intent(aContext, MainActivity.class);
                    if (StringUtils.isNotEmpty(extras)) {
                        if (null != extraJson && extraJson.length() > 0) {
                            intent2.putExtra(MainActivity.KEY_EXTRAS, extras);
                        }
                    }
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    aContext.startActivity(intent2);
                } catch (Exception e) {
                    Toast.makeText(aContext, "没有安装", Toast.LENGTH_LONG).show();
                }
            }*/
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            }else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    Log.d(TAG,"得到获取数据     Extra="+bundle.getString(JPushInterface.EXTRA_EXTRA));
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it =  json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " +json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    private void processCustomMessage(Context context, Bundle bundle) {
        // if (HomeNewFragment.isForeground) {
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String content_type = bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE);
		Log.e(TAG, "processCustomMessage content_type" + content_type);
		Log.e(TAG, "processCustomMessage message" + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		Log.e(TAG, "processCustomMessage extras" + extras);//消息内容详情
        if("sys_upgrade".equals(content_type))
        {
            //系统维护中
           /* Intent msgIntent = new Intent(HomeNewFragment.MESSAGE_RECEIVED_ACTION_UPGRADE);
            context.sendBroadcast(msgIntent);*/
        }
        else
        {
            Boolean mainCreat = Tools.getMainStatus(aContext);
            if(!mainCreat)
            {

            }else{
                Intent msgIntent = new Intent(MainActivity.ACTION_UPDATE_PUSHINFO);
                if (StringUtils.isNotEmpty(extras)) {
                    try {
                        JSONObject extraJson = new JSONObject(extras);
                        if (null != extraJson && extraJson.length() > 0) {
                            msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
                        }
                        context.sendBroadcast(msgIntent);
                    } catch (JSONException e) {

                    }
                 }
            }
        }
    }

    /*
     * 启动一个app
     */
    public void startAPP(String appPackageName, String extras) {
        try {
            JSONObject extraJson = new JSONObject(extras);
            Intent intent = new Intent(aContext, LoginActivity.class);
            if (StringUtils.isNotEmpty(extras)) {
                if (null != extraJson && extraJson.length() > 0) {
                    intent.putExtra(MainActivity.KEY_EXTRAS, extras);
                }
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
            aContext.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(aContext, "没有安装", Toast.LENGTH_LONG).show();
        }
    }
    //判断应用是否在前台
    public boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return false;
            }
        }
        return true;
    }
}
