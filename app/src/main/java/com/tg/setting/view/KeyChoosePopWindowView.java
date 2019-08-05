package com.tg.setting.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tg.coloursteward.R;

/**
 * 乐开-筛选
 * hxg 2019.07.19
 */
public class KeyChoosePopWindowView extends PopupWindow implements View.OnClickListener {
    private View contentView;
    private Activity context;
    private TextView tv_unbind;
    private TextView tv_normal;
    private TextView tv_exception;
    private EditText et_build_min;
    private EditText et_build_max;
    private EditText et_unit_min;
    private EditText et_unit_max;
    private TextView tv_cancel;
    private TextView tv_submit;

    private int unbind = 0;
    private int normal = 0;
    private int exception = 0;

    public KeyChoosePopWindowView(final Activity context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.view_key_pop_choose, null);
        initWindow();
        initView();
    }

    private void initWindow() {
        // 设置SelectPicPopupWindow的View
        this.setContentView(contentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);
        setWindowBackgroundAlpha();
    }

    /**
     * 设置背景
     */
    private void setWindowBackgroundAlpha() {
        if (context != null) {
            Window window = context.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.alpha = 0.5f;
            window.setAttributes(layoutParams);
        }
    }

    private void initView() {
        tv_unbind = contentView.findViewById(R.id.tv_unbind);
        tv_normal = contentView.findViewById(R.id.tv_normal);
        tv_exception = contentView.findViewById(R.id.tv_exception);
        et_build_min = contentView.findViewById(R.id.et_build_min);
        et_build_max = contentView.findViewById(R.id.et_build_max);
        et_unit_min = contentView.findViewById(R.id.et_unit_min);
        et_unit_max = contentView.findViewById(R.id.et_unit_max);
        tv_cancel = contentView.findViewById(R.id.tv_cancel);
        tv_submit = contentView.findViewById(R.id.tv_submit);
        tv_unbind.setOnClickListener(this);
        tv_normal.setOnClickListener(this);
        tv_exception.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_unbind:
                if (0 == unbind) {
                    unbind = 1;
                    tv_unbind.setTextColor(context.getResources().getColor(R.color.color_1da1f4));
                    tv_unbind.setBackgroundResource(R.drawable.shape_key_choose_select_text);
                } else {
                    unbind = 0;
                    tv_unbind.setTextColor(context.getResources().getColor(R.color.tv_index_color));
                    tv_unbind.setBackgroundResource(R.drawable.shape_key_choose_text);
                }
                break;
            case R.id.tv_normal:
                if (0 == normal) {
                    normal = 1;
                    tv_normal.setTextColor(context.getResources().getColor(R.color.color_1da1f4));
                    tv_normal.setBackgroundResource(R.drawable.shape_key_choose_select_text);
                } else {
                    normal = 0;
                    tv_normal.setTextColor(context.getResources().getColor(R.color.tv_index_color));
                    tv_normal.setBackgroundResource(R.drawable.shape_key_choose_text);
                }
                break;
            case R.id.tv_exception:
                if (0 == exception) {
                    exception = 1;
                    tv_exception.setTextColor(context.getResources().getColor(R.color.color_1da1f4));
                    tv_exception.setBackgroundResource(R.drawable.shape_key_choose_select_text);
                } else {
                    exception = 0;
                    tv_exception.setTextColor(context.getResources().getColor(R.color.tv_index_color));
                    tv_exception.setBackgroundResource(R.drawable.shape_key_choose_text);
                }
                break;
            case R.id.tv_cancel:
                this.dismiss();
                break;
            case R.id.tv_submit:

                this.dismiss();
                break;
        }
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