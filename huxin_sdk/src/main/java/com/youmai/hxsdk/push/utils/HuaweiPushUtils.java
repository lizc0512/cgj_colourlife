package com.youmai.hxsdk.push.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.huawei.android.hms.agent.HMSAgent;
import com.huawei.android.hms.agent.common.handler.ConnectHandler;
import com.huawei.android.hms.agent.push.handler.DeleteTokenHandler;
import com.huawei.android.hms.agent.push.handler.GetTokenHandler;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.StringUtils;


/**
 * 华为推送的工具，用于获取服务、token，是否开启透传功能
 * Created by fylder on 2016/12/27.
 */

public class HuaweiPushUtils {

    private static String TAG = HuaweiPushUtils.class.getSimpleName();

    private static HuaweiPushUtils huaweiPushUtils;


    public static HuaweiPushUtils getIntance() {
        if (huaweiPushUtils == null) {
            huaweiPushUtils = new HuaweiPushUtils();
        }
        return huaweiPushUtils;
    }

    private HuaweiPushUtils() {
    }


    /**
     * 获取token
     */
    public void getToken(Activity activity) {
        HMSAgent.connect(activity, new ConnectHandler() {
            @Override
            public void onConnect(int rst) {
                Log.v(TAG, "get token: begin");
                if (rst == 0) {
                    HMSAgent.Push.getToken(new GetTokenHandler() {
                        @Override
                        public void onResult(int rst) {
                            Log.v(TAG, "get token: end" + rst);
                        }
                    });
                }
            }
        });
    }

    /**
     * 删除token | delete push token
     */
    public void deleteToken(Context context) {
        final String token = AppUtils.getStringSharedPreferences(context, "huawei_token", "");
        if (StringUtils.isEmpty(token)) {
            return;
        }

        Log.v(TAG, "deleteToken:begin");
        HMSAgent.Push.deleteToken(token, new DeleteTokenHandler() {
            @Override
            public void onResult(int rst) {
                Log.v(TAG, "deleteToken:end code=" + rst);
            }
        });
    }


}
