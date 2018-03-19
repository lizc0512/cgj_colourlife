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
public class HxTip2Dialog extends Dialog {

    private TextView tv_message, tv_title;
    private Button sureBtn;
    private Context mContext;

    private OnClickListener sureBtnClickListener;

    public HxTip2Dialog(Context context) {
        super(context, R.style.hx_sdk_dialog);
        setCanceledOnTouchOutside(false);
        mContext =context;

        setContentView(R.layout.hx_sdk_tip2_dialog);
        init();
    }

    public HxTip2Dialog setSureBtnClickListener(OnClickListener listener) {
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
                if(sureBtnClickListener!=null){
                    sureBtnClickListener.onClick(HxTip2Dialog.this, -1);
                }
            }
        });

    }

    public HxTip2Dialog setMessage(String message) {
        tv_message.setText(message);
        return this;
    }
	
    public HxTip2Dialog setTitle(String title) {
        tv_title.setText(title);
        return this;
    }

    public Button getSureBtn() {
        return sureBtn;
    }

}
