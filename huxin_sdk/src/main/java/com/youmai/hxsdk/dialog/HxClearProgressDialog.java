package com.youmai.hxsdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youmai.hxsdk.R;

public class HxClearProgressDialog extends Dialog {

    public HxClearProgressDialog(Context context) {
        this(context, R.style.hx_sdk_dialog);
    }

    public HxClearProgressDialog(Context context, int theme) {
        super(context, theme);
        init();
    }


    private ImageView spaceshipImage;
    private TextView tv_title;
    private String defaultTipMessage = getContext().getString(R.string.hxm_app_loading);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void init() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.hx_clear_progress_dialog, null);
        spaceshipImage = (ImageView) v.findViewById(R.id.iv_img);
        tv_title = (TextView) v.findViewById(R.id.tv_title);// 提示文字

        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                getContext(), R.anim.hx_clear_progress_anim);
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);// 使用ImageView显示动画

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        tv_title.setText(defaultTipMessage);// 设置加载信息

        setContentView(v, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HxClearProgressDialog setTipMessage(String title) {
        defaultTipMessage = title;
        tv_title.setText(defaultTipMessage);// 设置加载信息
        return this;
    }

}
