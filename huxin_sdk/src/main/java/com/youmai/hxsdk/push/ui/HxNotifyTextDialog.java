package com.youmai.hxsdk.push.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.utils.GlideRoundTransform;

/**
 * 作者：create by YW
 * 日期：2016.11.08 15:48
 * 描述：推送通知界面
 */
public class HxNotifyTextDialog extends Dialog {

    private TextView tv_title;
    private TextView tv_content;
    private TextView mTvSure;

    private Context mContext;

    private OnClickListener sureBtnClickListener;
    private OnClickListener cancelClickListener;

    public HxNotifyTextDialog(Context context) {
        super(context, R.style.hx_sdk_dialog);
        mContext = context;
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }


    public void setType(int type) {
        String title = null;
        if (type == 0) {
            title = "系统公告";
        } else if (type == 1) {
            title = "精选活动";
        } else if (type == 2) {
            title = "玩转呼信";
        } else if (type == 3) {
            title = "行业资讯";
        }
        tv_title.setText(title);
    }


    public void setContent(String content) {
        tv_content.setText("    " + content);
    }


    public void setBtnName(String name) {
        mTvSure.setText(name);
    }

    public HxNotifyTextDialog setGoTaskClickListener(OnClickListener listener) {
        sureBtnClickListener = listener;
        return this;
    }

    public HxNotifyTextDialog setCloseClickListener(OnClickListener listener) {
        cancelClickListener = listener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_notify_text_dialog);
        initView();
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_content = (TextView) findViewById(R.id.tv_content);
        mTvSure = (TextView) findViewById(R.id.tv_notify_start);

        mTvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                /*if (sureBtnClickListener != null) {  //直接关闭 无需打开
                    sureBtnClickListener.onClick(HxNotifyTextDialog.this, -1);
                }*/
            }
        });
    }

}
