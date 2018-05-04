package com.tg.coloursteward.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupWindow;

import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.AppDetailsGridViewAdapter;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.AppDetailsGridViewInfo;
import com.tg.coloursteward.serice.HomeService;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * 即时分配记录筛选弹出框
 */

public class AppDetailPopWindowView extends PopupWindow {
    private View conentView;
    private Intent intent;
    private Activity context;
    private HomeService homeService;
    private MyGridView gridview1;
    private AppDetailsGridViewAdapter adapter;

    public AppDetailPopWindowView(final Activity context,final ArrayList<AppDetailsGridViewInfo> list){
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.appdetail_popup_window, null);
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(false);
        this.setOutsideTouchable(false);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AppAnimationPreview);
        conentView.findViewById(R.id.view_bg).getBackground().setAlpha(130);
        gridview1 = (MyGridView) conentView.findViewById(R.id.gv_app_details);
        gridview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               /**
                * EventBus传递消息
                */
               EventBus.getDefault().post(list.get(position).id);
               AppDetailPopWindowView.this.dismiss();

           }
       });
        adapter = new AppDetailsGridViewAdapter(context,list);
        gridview1.setAdapter(adapter);
    }
    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent);
        } else {
            this.dismiss();
        }
    }
}