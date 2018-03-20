package com.youmai.hxsdk.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.youmai.hxsdk.entity.CallInfo;
import com.youmai.hxsdk.utils.AppUtils;

/**
 * 作者：create by YW
 * 日期：2016.07.25 18:45
 * 描述：Sdk的Activity的基类Base
 */
public class SdkBaseActivity extends AppCompatActivity {

    private static final String TAG = "SdkBaseActivity";

    public static final String FROM_PUSH = "from_Push";
    public static final String SHOW_FLOAT_VIEW = "show_float_view";

    private boolean fromPush = false;
    private boolean isFloatView = true;

    public Context mContext;

    public View popAttachView;

    protected boolean isHookEmo = false;//如果是挂机界面置为true,不接收表情


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                //TODO 去掉锁屏标志，避免锁屏跳闪  2017-11-13
//                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        mContext = this;
        popAttachView = getWindow().getDecorView();

        fromPush = getIntent().getBooleanExtra(FROM_PUSH, false);
        isFloatView = getIntent().getBooleanExtra(SHOW_FLOAT_VIEW, true);

    }


    @Override
    protected void onStart() {
        super.onStart();
        //IMMsgManager.getInstance().registerChatMsg(onChatMsg);
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
        //IMMsgManager.getInstance().unregisterChatMsg(onChatMsg);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {

        boolean isMainActRunning = AppUtils.isTopActiviy(this, "com.youmai.huxin.app.activity.MainAct");
        if (!isMainActRunning && fromPush && !CallInfo.IsCalling()) {
            Intent intent = new Intent();
            intent.setClassName(this, "com.youmai.huxin.app.activity.MainAct");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        super.onBackPressed();
    }


    public void setFloatView(boolean floatView) {
        isFloatView = floatView;
    }

}
