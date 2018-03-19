package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.youmai.hxsdk.R;


/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2016-08-15 11:20
 * Description:
 */
public class HxNotifySoundDialog extends Dialog implements View.OnClickListener {


    public static final String NOTIFY_SOUND = "notify_sound";

    public enum Notify_Sound {
        ONE, TWO, THREE, SYSTEM
    }

    public interface HxCallback {
        void onNotifySound(Notify_Sound notify);
    }


    private Context mContext;
    private Button btn_notify_one, btn_notify_two, btn_notify_three;
    private Button btn_notify_system;
    private Button btnCancel;


    private HxCallback callback;

    public HxNotifySoundDialog(Context context) {
        super(context, R.style.hx_app_dialog);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_dialog_notify_sound);
        initView();
        setDialogFeature();
    }

    /**
     * 设置对话框特征
     */
    protected void setDialogFeature() {
        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
    }


    public void initView() {

        btn_notify_one = (Button) findViewById(R.id.btn_notify_one);
        btn_notify_one.setOnClickListener(this);

        btn_notify_two = (Button) findViewById(R.id.btn_notify_two);
        btn_notify_two.setOnClickListener(this);

        btn_notify_three = (Button) findViewById(R.id.btn_notify_three);
        btn_notify_three.setOnClickListener(this);

        btn_notify_system = (Button) findViewById(R.id.btn_notify_system);
        btn_notify_system.setOnClickListener(this);


        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.btn_notify_one) {//选择性别男
            if (callback != null) {
                callback.onNotifySound(Notify_Sound.ONE);
            }
            dismiss();
        } else if (i == R.id.btn_notify_two) { //选择性别女
            if (callback != null) {
                callback.onNotifySound(Notify_Sound.TWO);
            }
            dismiss();
        } else if (i == R.id.btn_notify_three) { //选择性别女
            if (callback != null) {
                callback.onNotifySound(Notify_Sound.THREE);
            }
            dismiss();
        } else if (i == R.id.btn_notify_system) { //选择性别女
            if (callback != null) {
                callback.onNotifySound(Notify_Sound.SYSTEM);
            }
            dismiss();
        } else if (i == R.id.btn_cancel) {
            dismiss();
        }
    }

    public void setHxSelectSexDialog(HxCallback callback) {
        this.callback = callback;
    }
}
