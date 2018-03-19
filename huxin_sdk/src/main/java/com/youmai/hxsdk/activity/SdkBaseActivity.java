package com.youmai.hxsdk.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.ChatMsg;
import com.youmai.hxsdk.chat.ContentText;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.entity.CallInfo;
import com.youmai.hxsdk.entity.EmoInfo;
import com.youmai.hxsdk.interfaces.OnChatMsg;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.view.full.FloatViewUtil;

import java.util.List;

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


    private OnChatMsg onChatMsg = new OnChatMsg() {
        @Override
        public void onCallback(ChatMsg msg) {
            
        }
    };


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
        FloatViewUtil.instance().hideFloatViewDelay();
    }


    @Override
    public void onResume() {
        super.onResume();
        FloatViewUtil.instance().registerHome(mContext);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (isFloatView && CallInfo.IsCalling()) {
            FloatViewUtil.instance().showFloatViewDelay(mContext);
        }
        FloatViewUtil.instance().unRegisterHome(mContext);
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
