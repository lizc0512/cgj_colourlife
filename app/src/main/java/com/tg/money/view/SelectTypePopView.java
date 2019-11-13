package com.tg.money.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.AppDetailsGridViewAdapter;
import com.tg.coloursteward.view.MyGridView;
import com.tg.money.adapter.SelectTypeAdapter;
import com.tg.money.callback.SelectTypeCallBack;
import com.tg.money.entity.SelectTypeEntity;
import com.youmai.hxsdk.adapter.PaddingItemDecoration;

import java.util.ArrayList;

/**
 * 即时分配记录筛选弹出框
 */

public class SelectTypePopView extends PopupWindow {
    private View conentView;
    private Activity context;
    private RecyclerView rv_select_type;
    private SelectTypeCallBack selectTypeCallBack;
    private SelectTypeAdapter selectTypeAdapter;
    private TextView close;

    public SelectTypePopView(final Activity context, final ArrayList<SelectTypeEntity.ContentBean.ResultBean> list, SelectTypeCallBack click) {
        this.context = context;
        this.selectTypeCallBack = click;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.select_type_popup_window, null);
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
        rv_select_type = conentView.findViewById(R.id.rv_select_type);
        close = conentView.findViewById(R.id.tv_select_close);
        close.setOnClickListener(v -> SelectTypePopView.this.dismiss());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        rv_select_type.setLayoutManager(gridLayoutManager);
        rv_select_type.addItemDecoration(new PaddingItemDecoration(5));
        selectTypeAdapter = new SelectTypeAdapter(R.layout.item_select_type_details, list);
        rv_select_type.setAdapter(selectTypeAdapter);
        selectTypeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                list.get(position).setIsCheck(1);
                selectTypeAdapter.notifyDataSetChanged();
                if (null != selectTypeCallBack) {
                    selectTypeCallBack.backInfo(view, position);
                }
                SelectTypePopView.this.dismiss();
            }
        });
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