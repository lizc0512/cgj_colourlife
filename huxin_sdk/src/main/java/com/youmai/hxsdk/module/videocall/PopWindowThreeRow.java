package com.youmai.hxsdk.module.videocall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youmai.hxsdk.R;

public class PopWindowThreeRow extends PopupWindow {
    private View conentView;
    private Intent intent;
    private Activity mContext;
    private RelativeLayout rl_add_personal;
    private RelativeLayout rl_speak;
    public View.OnClickListener firstListener;
    public View.OnClickListener secondListener;
    public View.OnClickListener threeListenner;
    public String firstText;
    public String secondText;
    public String threeText;
    public int firstIcon;
    public int secondIcon;
    public int threeIcon;
    private ImageView iv_addsome;
    private TextView tv_add_some;
    private ImageView iv_shenqing;
    private TextView tv_shenqing;
    private ImageView iv_three;
    private TextView tv_three;
    private RelativeLayout rl_three;

    public PopWindowThreeRow(Builder builder) {
        this.firstIcon = builder.firstIcon;
        this.firstText = builder.firstText;
        this.secondText = builder.secondText;
        this.firstListener = builder.firstListener;
        this.secondListener = builder.secondListener;
        this.secondIcon = builder.secondIcon;
        this.mContext = builder.mContext;
        this.threeIcon = builder.threeIcon;
        this.threeListenner = builder.threeListener;
        this.threeText = builder.threeText;
        onBuildPop(mContext);
    }

    public void onBuildPop(final Activity context) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popup_video_call3, null);
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
        // 刷新状态  
        this.update();
        // 实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作  
        this.setBackgroundDrawable(dw);
        // 设置SelectPicPopupWindow弹出窗体动画效果  
        // this.setAnimationStyle(R.style.AnimationPreview);
        // 设置SelectPicPopupWindow的View

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
            iv_shenqing.setImageResource(firstIcon);
        }
        if (!TextUtils.isEmpty(firstText)) {
            tv_add_some.setText(firstText);
        }
        if (!TextUtils.isEmpty(secondText)) {
            tv_shenqing.setText(secondText);
        }
        if (!TextUtils.isEmpty(threeText)) {
            tv_three.setText(threeText);
        }
    }

    private void setListener() {
        if (firstListener != null) {
            rl_add_personal.setOnClickListener(firstListener);
        }
        if (secondListener != null) {
            rl_speak.setOnClickListener(secondListener);
        }
        if (threeListenner != null) {
            rl_three.setOnClickListener(threeListenner);
        }
    }

    private void initView() {
        rl_add_personal = conentView.findViewById(R.id.rl_add_personal);
        rl_speak = conentView.findViewById(R.id.rl_speak);
        iv_addsome = conentView.findViewById(R.id.iv_addsome);
        tv_add_some = conentView.findViewById(R.id.tv_add_some);
        iv_shenqing = conentView.findViewById(R.id.iv_shenqing);
        tv_shenqing = conentView.findViewById(R.id.tv_shenqing);
        iv_three = conentView.findViewById(R.id.iv_three);
        tv_three = conentView.findViewById(R.id.tv_three);
        rl_three = conentView.findViewById(R.id.rl_three);
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
            this.showAsDropDown(parent, parent.getWidth()/2, 30);
            //产生背景变暗效果

        } else {
            this.dismiss();
        }
    }

    public static class Builder {
        public View.OnClickListener firstListener;
        public View.OnClickListener secondListener;
        public View.OnClickListener threeListener;
        public String firstText;
        public String secondText;
        public String threeText;
        public int firstIcon = 0;
        public int secondIcon = 0;
        public int threeIcon = 0;
        public Activity mContext;

        public Builder(Activity context) {
            this.mContext = context;
        }

        public Builder firstIcon(int res) {
            this.firstIcon = res;
            return this;
        }

        public Builder secondIcon(int res) {
            this.secondIcon = res;
            return this;
        }

        public Builder threeIcon(int res) {
            this.threeIcon = res;
            return this;
        }

        public Builder setSecondListener(String secondText,View.OnClickListener listener) {
            this.secondListener = listener;
            this.secondText=secondText;
            return this;
        }

        public Builder setFirstListener(String first,View.OnClickListener l) {
            this.firstListener = l;
            this.firstText=first;
            return this;
        }

        public Builder setThreeListener(String str,View.OnClickListener l) {
            this.threeListener = l;
            this.threeText=str;
            return this;
        }

        public PopWindowThreeRow build() {
            return new PopWindowThreeRow(this);
        }


    }
}  