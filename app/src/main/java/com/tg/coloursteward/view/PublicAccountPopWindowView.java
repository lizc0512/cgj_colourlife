package com.tg.coloursteward.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.tg.coloursteward.R;

public class PublicAccountPopWindowView extends PopupWindow {
    private View conentView;
    private onClickCallBack clickListener;

    public PublicAccountPopWindowView(final Activity context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.account_popup_window, null);
        // 设置SelectPicPopupWindow的View
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
        this.setAnimationStyle(R.style.AnimationPreview);
        //所有
        conentView.findViewById(R.id.rl_all).setOnClickListener(arg0 -> {
            if (null != clickListener) {
                clickListener.callBack(0);
            }
            PublicAccountPopWindowView.this.dismiss();
        });
        //转入
        conentView.findViewById(R.id.rl_in).setOnClickListener(arg0 -> {
            if (null != clickListener) {
                clickListener.callBack(2);
            }
            PublicAccountPopWindowView.this.dismiss();
        });
        //转出
        conentView.findViewById(R.id.rl_out).setOnClickListener(arg0 -> {
            if (null != clickListener) {
                clickListener.callBack(1);
            }
            PublicAccountPopWindowView.this.dismiss();
        });
    }

    public void setOnclickCallBack(onClickCallBack callBack) {
        this.clickListener = callBack;
    }

    public interface onClickCallBack {
        void callBack(int ispay);
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow  
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }
}  