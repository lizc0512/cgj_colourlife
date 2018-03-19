package com.youmai.hxsdk.module.remind;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.youmai.hxsdk.R;

/**
 * 作者：create by YW
 * 日期：2017.11.27 12:39
 * 描述：
 */
public class HxFirstRemindDialog extends Dialog {

    private TextView tv_message, tv_title;
    private TextView tv_sure;

    private OnClickListener sureClickListener;

    public HxFirstRemindDialog(Context context) {
        super(context, R.style.hx_sdk_dialog);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.hx_remind_tip_dialog);
        init();
    }

    public HxFirstRemindDialog setSureClickListener(OnClickListener listener) {
        sureClickListener = listener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_remind_tip_dialog);
        init();
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.tv_tip_title);
        tv_message = (TextView) findViewById(R.id.tv_tip_message);
        tv_sure = (TextView) findViewById(R.id.tv_sure);

        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (sureClickListener != null) {
                    sureClickListener.onClick(HxFirstRemindDialog.this, -1);
                }
            }
        });
    }

    public HxFirstRemindDialog setMessage(String message) {
        tv_message.setText("你所设置的提醒将会在" + message + "以呼信提醒下发给你。");
        return this;
    }

    public HxFirstRemindDialog setTitle(String title) {
        tv_title.setText(title);
        return this;
    }

}
