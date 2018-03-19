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

import com.youmai.hxsdk.R;

/**
 * 作者：create by YW
 * 日期：2016.11.29 11:22
 * 描述：秀引导提示
 */

public class ShowTipOneDialog extends Dialog implements View.OnClickListener{


    public interface HxCallback {
        void onJumpToSetShow();
    }

    private Button tv_show_close;
    private TextView tv_show_jump;
    private Context mContext;

    private ShowTipOneDialog.HxCallback callback;

    public ShowTipOneDialog(Context context) {
        super(context, R.style.ShowTipDialogTheme);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_dialog_show_tip_one);
        initView(savedInstanceState);
        setDialogFeature();
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
        tv_show_jump = (TextView) findViewById(R.id.tv_dialog_show_jump);

        tv_show_close.setOnClickListener(this);
        tv_show_jump.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.tv_dialog_show_jump) {//跳转到设置秀
            if (callback != null) {
                callback.onJumpToSetShow();
            }
            dismiss();
        } else if (i == R.id.tv_dialog_show_close) {
            dismiss();
        }
    }

    public void setHxSetShowListener(ShowTipOneDialog.HxCallback callback) {
        this.callback = callback;
    }
}
