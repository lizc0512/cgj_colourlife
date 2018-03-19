package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;

/**
 * 作者：create by YW
 * 日期：2016.11.29 11:22
 * 描述：秀引导提示
 */

public class ShowTipTwoDialog extends Dialog implements View.OnClickListener {


    public interface HxCallback {
        void onJumpToDial();
    }

    private Button tv_show_close;
    private TextView tv_show_dial;
    private Context mContext;

    private ShowTipTwoDialog.HxCallback callback;

    public ShowTipTwoDialog(Context context) {
        super(context, R.style.ShowTipDialogTheme);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_dialog_show_tip_two);
        initView(savedInstanceState);
        setDialogFeature();

        String phoneNum = HuxinSdkManager.instance().getPhoneNum();
        HuxinSdkManager.instance().getShowData(phoneNum, null);  //@chenqy 自动刷新本机号码的通话秀
    }

    /**
     * 设置对话框特征
     */
    protected void setDialogFeature() {
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER_HORIZONTAL;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
    }


    public void initView(Bundle savedInstanceState) {

        tv_show_close = (Button) findViewById(R.id.tv_dialog_show_close);
        tv_show_dial = (TextView) findViewById(R.id.tv_dialog_show_dial);

        tv_show_close.setOnClickListener(this);
        tv_show_dial.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.tv_dialog_show_dial) {//跳转到设置秀
            if (callback != null) {
                callback.onJumpToDial();
            }
            dismiss();
        } else if (i == R.id.tv_dialog_show_close) {
            dismiss();
        }
    }

    public void setHxSetShowListener(ShowTipTwoDialog.HxCallback callback) {
        this.callback = callback;
    }
}
