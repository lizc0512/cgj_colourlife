package com.youmai.hxsdk.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * 作者：create by YW
 * 日期：2016.07.25 18:45
 * 描述：Sdk的Activity的基类Base
 */
public class SdkBaseActivity extends AppCompatActivity {

    private static final String TAG = "SdkBaseActivity";

    public static final String FROM_PUSH = "from_Push";

    private boolean fromPush = false;

    public Context mContext;

    public View popAttachView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        popAttachView = getWindow().getDecorView();

        fromPush = getIntent().getBooleanExtra(FROM_PUSH, false);

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

        /*boolean isMainActRunning = AppUtils.isTopActiviy(this, "com.tg.coloursteward.ui.MainActivity1");
        if (!isMainActRunning && fromPush) {
            Intent intent = new Intent();
            intent.setClassName(this, "com.tg.coloursteward.ui.MainActivity1");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }*/

        super.onBackPressed();
    }

}
