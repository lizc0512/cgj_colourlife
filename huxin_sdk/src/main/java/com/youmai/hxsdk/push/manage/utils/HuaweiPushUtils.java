package com.youmai.hxsdk.push.manage.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.TokenResult;
import com.youmai.hxsdk.push.manage.activity.HuaweiCommonApiActivity;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.StringUtils;


/**
 * 华为推送的工具，用于获取服务、token，是否开启透传功能
 * Created by fylder on 2016/12/27.
 */

public class HuaweiPushUtils {

    private static String TAG = HuaweiPushUtils.class.getSimpleName();

    private static HuaweiPushUtils huaweiPushUtils;

    private HuaweiApiClient huaweiApiClient;
    private Context mContext;


    private class HuaweiConnection implements HuaweiApiClient.ConnectionCallbacks {
        @Override
        public void onConnected() {
            getToken();
        }

        @Override
        public void onConnectionSuspended(int cause) {
            Log.w(TAG, "onConnectionSuspended, cause: " + cause);
        }
    }


    private class HuaweiOnConnectionFail implements HuaweiApiClient.OnConnectionFailedListener {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }
    }


    public static HuaweiPushUtils getIntance(Context context) {
        if (huaweiPushUtils == null) {
            huaweiPushUtils = new HuaweiPushUtils(context);
        }
        return huaweiPushUtils;
    }

    private HuaweiPushUtils(Context context) {
        mContext = context;
        if (huaweiApiClient == null) {
            huaweiApiClient = new HuaweiApiClient.Builder(context)
                    .addApi(HuaweiPush.PUSH_API)
                    .addConnectionCallbacks(new HuaweiConnection())
                    .addOnConnectionFailedListener(new HuaweiOnConnectionFail())
                    .build();
        }
    }


    private boolean isConnected() {
        return huaweiApiClient != null
                && huaweiApiClient.isConnected();
    }


    /**
     * 获取HMS服务
     */
    public void connectToHMS(Context context) {
        Intent intent = new Intent(context, HuaweiCommonApiActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public void register() {
        huaweiApiClient.connect();
    }


    /**
     * 获取token，第一次会未连接
     */
    public void getToken() {
        if (!isConnected()) {
            return;
        }
        // 异步调用方式
        PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(huaweiApiClient);
        tokenResult.setResultCallback(new ResultCallback<TokenResult>() {
            @Override
            public void onResult(TokenResult result) {
                if(result!=null && result.getTokenRes()!=null) {
                    Log.i(TAG, result.toString());
                    String token = result.getTokenRes().getToken();
                    AppUtils.setStringSharedPreferences(mContext, "huawei_token", token);
                }
            }
        });
    }


    /**
     * 应用调用注销token接口成功之后，客户端就不会在接收到push消息。
     */
    public void delToken() {
        final String token = AppUtils.getStringSharedPreferences(mContext, "huawei_token", "");
        if (StringUtils.isEmpty(token)) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    if (!StringUtils.isEmpty(token) && null != huaweiApiClient) {
                        HuaweiPush.HuaweiPushApi.deleteToken(huaweiApiClient, token);
                    } else {
                        Log.w(TAG, "delete token's params is invalid.");
                    }
                } catch (Exception e) {
                    Log.e("PushLog", "delete token exception, " + e.toString());
                }
            }
        }.start();
    }


    /**
     * 开启、关闭接收Push透传消息。若不调用该方法则默认为开启。
     *
     * @param flag
     */
    public void setPassByMsg(boolean flag) {
        HuaweiPush.HuaweiPushApi.enableReceiveNormalMsg(huaweiApiClient, flag);
    }
}
