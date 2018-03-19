package com.youmai.hxsdk.push.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.module.remind.RemindItem;

public class HxRemindCancelDialog extends Dialog {

    private TextView tv_title;
    private TextView tv_msg;
    private ImageView img_type;
    private TextView tv_content;
    private TextView tv_remind_info;
    private TextView tv_remind_time;

    private TextView tv_cancel;
    private TextView tv_confirm;

    private Context mContext;

    private View.OnClickListener confirmListener;
    private View.OnClickListener cancelListener;

    public HxRemindCancelDialog(Context context) {
        super(context, R.style.hx_sdk_dialog);
        mContext = context;
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }


    public void setContent(String content) {
        tv_content.setText(content);
    }


    public void setImageType(int msgIcon) {
        if (msgIcon < RemindItem.ITEM_DRAWABLES.length) {
            int resId = RemindItem.ITEM_DRAWABLES[msgIcon];
            img_type.setImageResource(resId);
        }
    }

    public void setRemindType(String text) {
        tv_remind_info.setText(text);
    }

    public void setRemindTime(String text) {
        tv_remind_time.setText(text);
    }

    public HxRemindCancelDialog setConfirmClickListener(View.OnClickListener listener) {
        confirmListener = listener;
        return this;
    }

    public HxRemindCancelDialog setCancelClickListener(View.OnClickListener listener) {
        cancelListener = listener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_notify_remind_dialog);
        initView();
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_msg = (TextView) findViewById(R.id.tv_msg);
        tv_content = (TextView) findViewById(R.id.tv_content);
        img_type = (ImageView) findViewById(R.id.img_type);
        tv_remind_info = (TextView) findViewById(R.id.tv_remind_info);
        tv_remind_time = (TextView) findViewById(R.id.tv_remind_time);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelListener != null) {
                    cancelListener.onClick(v);
                }
                dismiss();
            }
        });

        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmListener != null) {
                    confirmListener.onClick(v);
                }
                dismiss();
            }
        });
    }

}
