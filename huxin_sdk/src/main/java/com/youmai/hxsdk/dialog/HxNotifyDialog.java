package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.utils.GlideRoundTransform;

/**
 * 作者：create by YW
 * 日期：2016.11.08 15:48
 * 描述：推送通知界面
 */
public class HxNotifyDialog extends Dialog {

    private TextView mTvSure;
    private ImageView mIvClose;
    private ImageView iv_notify_bg;


    private Context mContext;

    private OnClickListener sureBtnClickListener;
    private OnClickListener cancelClickListener;

    public HxNotifyDialog(Context context) {
        super(context, R.style.hx_sdk_dialog);
        setCanceledOnTouchOutside(false);
        mContext = context;

        setContentView(R.layout.hx_notify_dialog);
        init();
    }

    public void setImage(String fid) {
        String url = AppConfig.getImageUrl(mContext, fid);

        try {
            Glide.with(mContext)
                    .load(url)
                    .apply(new RequestOptions()
                            .transform(new GlideRoundTransform(mContext, 10)))
                    .into(iv_notify_bg);
        } catch (Exception e) {

        }

    }


    public void setBtnName(String name) {
        mTvSure.setText(name);
    }

    public HxNotifyDialog setGoTaskClickListener(OnClickListener listener) {
        sureBtnClickListener = listener;
        return this;
    }

    public HxNotifyDialog setCloseClickListener(OnClickListener listener) {
        cancelClickListener = listener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_notify_dialog);
        init();
    }

    private void init() {
        mTvSure = (TextView) findViewById(R.id.tv_notify_start);
        mIvClose = (ImageView) findViewById(R.id.iv_notify_close);
        iv_notify_bg = (ImageView) findViewById(R.id.iv_notify_bg);

        mTvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (sureBtnClickListener != null) {
                    sureBtnClickListener.onClick(HxNotifyDialog.this, -1);
                }
            }
        });

        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (cancelClickListener != null) {
                    cancelClickListener.onClick(HxNotifyDialog.this, -1);
                }
            }
        });
    }

}
