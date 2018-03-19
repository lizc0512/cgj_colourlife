package com.youmai.hxsdk.module.callmsg;

import android.os.Bundle;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;

/**
 * 作者：create by YW
 * 日期：2017.02.06 11:11
 * 描述：通话中IM消息界面
 */
public class MsgActivity extends SdkBaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_call_msg);
        //HuxinSdkManager.instance().getStackAct().addActivity(this);
    }


    @Override
    public void onResume() {
        setFloatView(true);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //HuxinSdkManager.instance().getStackAct().finishActivity(this);
    }
}
