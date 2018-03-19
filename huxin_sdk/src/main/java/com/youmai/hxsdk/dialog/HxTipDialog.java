package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.youmai.hxsdk.R;

/**
 * Created by yw
 */
public class HxTipDialog extends Dialog {

    private TextView tv_message;
    private Button sureBtn;
    private Button cancelBtn;
    private Context mContext;

    private OnClickListener sureBtnClickListener;
    private OnClickListener cancelBtnClickListener;

    public HxTipDialog(Context context) {
        super(context, R.style.hx_sdk_dialog);
        setCanceledOnTouchOutside(false);
        mContext =context;

        setContentView(R.layout.hx_sdk_tip_dialog);
        init();
    }

    public HxTipDialog setSureBtnClickListener(OnClickListener listener) {
        sureBtnClickListener = listener;
        return this;
    }

    public HxTipDialog setCancelBtnClickListener(OnClickListener listener) {
        cancelBtnClickListener = listener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_sdk_tip_dialog);
        init();
    }

    private void init() {
        tv_message = (TextView) findViewById(R.id.tv_tip_message);
        sureBtn = (Button) findViewById(R.id.sure_btn);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);

        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(sureBtnClickListener!=null){
                    sureBtnClickListener.onClick(HxTipDialog.this, -1);
                }
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(cancelBtnClickListener!=null){
                    cancelBtnClickListener.onClick(HxTipDialog.this, -1);
                }
            }
        });
    }

    public HxTipDialog setMessage(String message) {
        tv_message.setText(message);
        return this;
    }

    public Button getSureBtn() {
        return sureBtn;
    }

    public Button getCancelBtn() {
        return cancelBtn;
    }
}
