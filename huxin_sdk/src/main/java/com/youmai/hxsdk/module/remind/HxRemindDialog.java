package com.youmai.hxsdk.module.remind;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.utils.StringUtils;

/**
 * 作者：create by YW
 * 日期：2017.11.27 12:39
 * 描述：
 */
public class HxRemindDialog extends Dialog {

    private TextView tv_message, tv_title;
    private TextView tv_tip_from, tv_tip_time;
    private TextView tv_sure;
    private ImageView iv_tip_icon;
    private LinearLayout linear_content;

    private TextView tv_quick_dial_number;
    private LinearLayout ll_quick_dial;

    private OnClickListener sureClickListener;
    private OnClickListener quickDialListener;

    public HxRemindDialog(Context context) {
        super(context, R.style.hx_sdk_dialog);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.hx_remind_first_dialog);
        init();
    }

    public HxRemindDialog setSureClickListener(OnClickListener listener) {
        sureClickListener = listener;
        return this;
    }

    public HxRemindDialog setQuickDialListener(OnClickListener listener) {
        quickDialListener = listener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_remind_first_dialog);
        init();
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.tv_tip_title);
        tv_message = (TextView) findViewById(R.id.tv_tip_message);
        tv_tip_from = (TextView) findViewById(R.id.tv_tip_from);
        tv_tip_time = (TextView) findViewById(R.id.tv_tip_time);
        iv_tip_icon = (ImageView) findViewById(R.id.iv_tip_icon);
        tv_sure = (TextView) findViewById(R.id.tv_sure);
        linear_content = (LinearLayout) findViewById(R.id.linear_content);

        tv_quick_dial_number = (TextView) findViewById(R.id.tv_quick_dial_number);
        ll_quick_dial = (LinearLayout) findViewById(R.id.ll_quick_dial);

        linear_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (sureClickListener != null) {
                    sureClickListener.onClick(HxRemindDialog.this, -1);
                }
            }
        });

        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                /*if (sureClickListener != null) {
                    sureClickListener.onClick(HxRemindDialog.this, -1);
                }*/
            }
        });

        ll_quick_dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (quickDialListener != null) {
                    quickDialListener.onClick(HxRemindDialog.this, -1);
                }
            }
        });
    }

    public HxRemindDialog setMsgIcon(int resId) {
        iv_tip_icon.setImageResource(resId);
        return this;
    }

    public HxRemindDialog setMessage(String message) {
        if (StringUtils.isEmpty(message)) {
            tv_message.setText("[暂无备注]");
        } else {
            tv_message.setText(message);
        }
        return this;
    }

    public HxRemindDialog setTitle(String title) {
        tv_title.setText(title);
        return this;
    }

    public HxRemindDialog setRemindFrom(String remindTarget) {
        tv_tip_from.setText("来自 " + remindTarget + " 聊天提醒");
        return this;
    }

    public HxRemindDialog setRemindTime(String remindTime) {
        tv_tip_time.setText("于 " + remindTime + " 设置");
        return this;
    }

    public HxRemindDialog setRemindQuickDial(String remindNum) {
        if (StringUtils.isEmpty(remindNum)) {
            tv_quick_dial_number.setVisibility(View.GONE);
            ll_quick_dial.setVisibility(View.GONE);
        } else {
            tv_quick_dial_number.setText("快速拨打：" + remindNum);
            ll_quick_dial.setVisibility(View.VISIBLE);
        }
        return this;
    }

}
