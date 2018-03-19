package com.youmai.hxsdk.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.youmai.hxsdk.R;

/**
 * Created by yw
 */
public class HxTip3Dialog extends Dialog {

    private TextView tv_message, tv_title;
    private Button sureBtn;
    private Activity mActivity;

    private OnClickListener sureBtnClickListener;

    public HxTip3Dialog(Activity activity) {
        super(activity, R.style.hx_sdk_dialog);
        setCanceledOnTouchOutside(false);
        mActivity = activity;

        setContentView(R.layout.hx_sdk_tip2_dialog);
        init();
    }

    public HxTip3Dialog setSureBtnClickListener(OnClickListener listener) {
        sureBtnClickListener = listener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_sdk_tip2_dialog);
        init();
    }

    private void init() {
        tv_message = (TextView) findViewById(R.id.tv_tip_message);
        tv_title = (TextView) findViewById(R.id.tv_tip_title);
        sureBtn = (Button) findViewById(R.id.sure_btn);

        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (sureBtnClickListener != null) {
                    sureBtnClickListener.onClick(HxTip3Dialog.this, -1);
                }
            }
        });

    }

    public HxTip3Dialog setMessage(String message) {
        tv_message.setText(message);
        return this;
    }

    public HxTip3Dialog setTitle(String title) {
        tv_title.setText(title);
        return this;
    }

    public Button getSureBtn() {
        return sureBtn;
    }

    @Override
    public void onBackPressed() {
        mActivity.finish();
        super.onBackPressed();
    }
}
