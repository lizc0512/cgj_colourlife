package com.tg.setting.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.tg.coloursteward.R;
import com.tg.setting.activity.KeySendKeyPhoneActivity;
import com.tg.setting.adapter.KeyStringAdapter;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 乐开-普通字符
 * hxg 2019.07.25
 */
public class KeyStringPopWindowView extends PopupWindow {
    private View contentView;
    private Activity context;
    private LinearLayout ll_pop;
    private SwipeRecyclerView rv_string;
    private KeyStringAdapter adapter;

    private List<String> list = new ArrayList<>();

    public KeyStringPopWindowView(final Activity context, List<String> list) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.view_key_pop_string, null);
        this.list.addAll(list);
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
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
//        this.setBackgroundDrawable(new ColorDrawable(0xffffffff));
        this.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
        // 设置SelectPicPopupWindow弹出窗体动画效果
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
        ll_pop = contentView.findViewById(R.id.ll_pop);
        rv_string = contentView.findViewById(R.id.rv_string);
        rv_string.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        adapter = new KeyStringAdapter(context, list, this);
        rv_string.setAdapter(adapter);
    }

    public void setIdentity(int position) {
        dismiss();
        ((KeySendKeyPhoneActivity) context).setIdentity(position);
    }

    /**
     * 显示popupWindow
     **/
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        } else {
            this.dismiss();
        }
    }

}