package com.youmai.hxsdk.module.filemanager.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.entity.CallInfo;

/**
 * 作者：create by YW
 * 日期：2017.06.23 09:49
 * 描述：Base基类 -> 区别弹屏的SdkBaseActivity
 */
public abstract class SdkHomeActivity extends AppCompatActivity {

    protected Context mContext;
    protected boolean isFloatView = true;
    protected boolean isPreview = true; //一级一级返回

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getApplicationContext();

        initData();
        initView();
        bindData();
        loadData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        bindClick();
        registerHome(mContext);

        /*if (isFloatView) {
            FloatViewUtil.instance().hideFloatView();
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();

        /*if (isFloatView && CallInfo.IsCalling() && !isPreview) {
            FloatViewUtil.instance().showFloatViewDelay(mContext);
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterHome(mContext);

        HuxinSdkManager.instance().getStackAct().finishActivity(mContext.getClass());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isPreview = false;
    }

    /**
     * 获取传递bundle数据
     */
    public void initData() {

    }

    /**
     * 初始化布局view
     */
    public abstract void initView();

    /**
     * 布局绑定初始化的数据：eg: title.setTextView("Hello world!");
     */
    public void bindData() {

    }

    /**
     * 网络请求
     */
    public void loadData() {

    }

    /**
     * 设置view事件点击
     */
    public void bindClick() {

    }

    /* 监听home键广播 start by 2016.8.22 */
    private BroadcastReceiver homeListenerReceiver = new BroadcastReceiver() {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null && reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    if (CallInfo.IsCalling()) {
                        // 处理自己的逻辑
                    }
                }
            }
        }
    };

    //在创建View时注册Receiver
    protected void registerHome(Context context) {
        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.registerReceiver(homeListenerReceiver, homeFilter);
    }

    //在View消失时反注册Receive 反注册home监听
    protected void unRegisterHome(Context context) {
        try {
            context.unregisterReceiver(homeListenerReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

}
