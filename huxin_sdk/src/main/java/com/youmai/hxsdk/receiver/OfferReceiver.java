package com.youmai.hxsdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.fragment.LoginFragment;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.service.HuxinService;
import com.youmai.hxsdk.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OfferReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();

            IPostListener listener = new IPostListener() {
                @Override
                public void httpReqResult(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("s").equals("1")) {
                            // TODO: 2017/6/16 success
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            HuxinSdkManager.instance().appInsert(0, packageName, listener);
        }
    }


}