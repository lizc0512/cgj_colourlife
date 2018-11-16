package com.youmai.hxsdk.module.videocall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.videocall.RoomActivity;

public class PopWindowTwoRow extends PopupWindow {
    private View conentView;
    private Activity mContext;
    private RelativeLayout rl_add_personal;
    private RelativeLayout rl_speak;
    public View.OnClickListener firstListener;
    public View.OnClickListener secondListener;
    public String firstText;
    public String secondText;
    public int firstIcon;
    public int secondIcon;
    private ImageView iv_addsome;
    private TextView tv_add_some;
    private ImageView iv_shenqing;
    private TextView tv_shenqing;

    public PopWindowTwoRow(Builder builder) {
        this.firstText = builder.firstText;
        this.secondText = builder.secondText;
        this.firstListener = builder.firstListener;
        this.secondListener = builder.secondListener;
        this.firstIcon = builder.firstIcon;
        this.secondIcon = builder.secondIcon;
        this.mContext = builder.mContext;
        onBuildPop(mContext);
    }

    public void onBuildPop(final Activity context) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popup_video_call, null);
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽  
        this.setWidth(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高  
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击  
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        //this.setSoftInputMode(InputMethodManager.RESULT_UNCHANGED_HIDDEN);
        // 刷新状态  
        this.update();
        // 实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);

//        });
        initView();
        setData();
        setListener();
    }

    private void setData() {
        if (firstIcon != 0) {
            iv_addsome.setImageResource(firstIcon);
        }
        if (secondIcon != 0) {
            iv_shenqing.setImageResource(secondIcon);
        }
        if (!TextUtils.isEmpty(firstText)) {
            tv_add_some.setText(firstText);
        }
        if (!TextUtils.isEmpty(secondText)) {
            tv_shenqing.setText(secondText);
        }
    }

    private void setListener() {
        if (firstListener != null) {
            rl_add_personal.setOnClickListener(firstListener);
        }
        if (secondListener != null) {
            rl_speak.setOnClickListener(secondListener);
        }
    }

    private void initView() {
        rl_add_personal = conentView.findViewById(R.id.rl_add_personal);
        rl_speak = conentView.findViewById(R.id.rl_speak);
        iv_addsome = conentView.findViewById(R.id.iv_addsome);
        tv_add_some = conentView.findViewById(R.id.tv_add_some);
        iv_shenqing = conentView.findViewById(R.id.iv_shenqing);
        tv_shenqing = conentView.findViewById(R.id.tv_shenqing);
    }

    /**
     * 筛选并跳转url
     *
     * @return
     */


    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow  
            this.showAsDropDown(parent, parent.getWidth() / 2, 30);
            //产生背景变暗效果

        } else {
            this.dismiss();
        }
    }

    public static class Builder {
        public View.OnClickListener firstListener;
        public View.OnClickListener secondListener;
        public String firstText;
        public String secondText;
        public Activity mContext;
        public int firstIcon;
        public int secondIcon;

        public Builder(Activity context) {
            this.mContext = context;
        }

        public Builder setFirstIcon(int imageUrl) {
            this.firstIcon = imageUrl;
            return this;
        }

        public Builder setSecondIcon(int imageUrl) {
            this.secondIcon = imageUrl;
            return this;
        }

        public Builder setSecondListener(String str, View.OnClickListener listener) {
            this.secondListener = listener;
            this.secondText = str;
            return this;
        }

        public Builder setFirstListener(String str, View.OnClickListener l) {
            this.firstText = str;
            this.firstListener = l;
            return this;
        }

        public PopWindowTwoRow build() {
            return new PopWindowTwoRow(this);
        }
    }
}  