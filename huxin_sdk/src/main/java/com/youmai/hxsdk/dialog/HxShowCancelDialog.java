package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.youmai.hxsdk.R;

/**
 * Created by yw 2017.11.8 15:40
 */
public class HxShowCancelDialog extends Dialog {

    private TextView tv_message, tv_title;
    private TextView tv_replace, tv_cancel;
    private Context mContext;

    private OnClickListener replaceClickListener;
    private OnClickListener cancelClickListener;

    public HxShowCancelDialog(Context context) {
        super(context, R.style.hx_sdk_dialog);
        setCanceledOnTouchOutside(false);
        mContext = context;

        setContentView(R.layout.hx_show_cancel_dialog);
        init();
    }

    public HxShowCancelDialog setReplaceClickListener(OnClickListener listener) {
        replaceClickListener = listener;
        return this;
    }

    public HxShowCancelDialog setCloseClickListener(OnClickListener listener) {
        cancelClickListener = listener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_show_cancel_dialog);
        init();
    }

    private void init() {
        tv_message = (TextView) findViewById(R.id.tv_tip_message);
        tv_title = (TextView) findViewById(R.id.tv_tip_title);
        tv_replace = (TextView) findViewById(R.id.btn_replace);
        tv_cancel = (TextView) findViewById(R.id.btn_cancel);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (replaceClickListener != null) {
                    replaceClickListener.onClick(HxShowCancelDialog.this, -1);
                }
            }
        });

        tv_replace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (cancelClickListener != null) {
                    cancelClickListener.onClick(HxShowCancelDialog.this, -1);
                }
            }
        });
    }

    
    public HxShowCancelDialog setMessage(int resId) {
        tv_message.setText(resId);
        return this;
    }

    public HxShowCancelDialog setMessage(String message) {
        tv_message.setText(message);
        return this;
    }

    public HxShowCancelDialog setTitle(String title) {
        tv_title.setText(title);
        return this;
    }

}
