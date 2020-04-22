package com.tg.money.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tg.coloursteward.R;
import com.tg.money.adapter.SelectTypeAdapter;
import com.tg.money.callback.SelectTypeCallBack;
import com.tg.money.entity.SelectTypeEntity;
import com.youmai.hxsdk.adapter.PaddingItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * 即时分配记录筛选弹出框
 */

public class SelectTypePopView extends PopupWindow {
    private View contentView;
    private Activity context;
    private RecyclerView rv_select_type;
    private SelectTypeCallBack selectTypeCallBack;
    private SelectTypeAdapter selectTypeAdapter;
    private TextView close;
    private List<SelectTypeEntity.ContentBean.ResultBean> mList = new ArrayList<>();

    public SelectTypePopView(final Activity context, final ArrayList<SelectTypeEntity.ContentBean.ResultBean> list, SelectTypeCallBack click) {
        this.context = context;
        this.selectTypeCallBack = click;
        this.mList = list;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.select_type_popup_window, null);
        initWindow();
        initView();
    }

    private void initView() {
        rv_select_type = contentView.findViewById(R.id.rv_select_type);
        close = contentView.findViewById(R.id.tv_select_close);
        close.setOnClickListener(v -> SelectTypePopView.this.dismiss());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        rv_select_type.setLayoutManager(gridLayoutManager);
        rv_select_type.addItemDecoration(new PaddingItemDecoration(5));

        selectTypeAdapter = new SelectTypeAdapter(R.layout.item_select_type_details, mList);
        rv_select_type.setAdapter(selectTypeAdapter);
        selectTypeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mList.get(position).setIsCheck(1);
                selectTypeAdapter.notifyDataSetChanged();
                if (null != selectTypeCallBack) {
                    selectTypeCallBack.backInfo(view, position);
                }
                SelectTypePopView.this.dismiss();
            }
        });
    }

    private void initWindow() {
        // 设置SelectPicPopupWindow的View
        this.setContentView(contentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(new BitmapDrawable());
        // 设置SelectPicPopupWindow弹出窗体动画效果
        setWindowBackgroundAlpha();
    }

    private void setWindowBackgroundAlpha() {
        if (context != null) {
            Window window = context.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.alpha = 0.5f;
            window.setAttributes(layoutParams);
        }
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