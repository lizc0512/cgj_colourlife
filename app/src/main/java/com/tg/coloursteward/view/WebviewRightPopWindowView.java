package com.tg.coloursteward.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.WebviewPopAdapter;
import com.tg.coloursteward.entity.WebviewRightEntity;
import com.tg.coloursteward.inter.FragmentMineCallBack;
import com.tg.coloursteward.util.MicroAuthTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class WebviewRightPopWindowView extends PopupWindow {
    private View conentView;
    private MicroAuthTimeUtils microAuthTimeUtils;
    List<WebviewRightEntity.DataBean> list = new ArrayList<>();
    private RecyclerView rv_web_pop;
    private WebviewPopAdapter adapter;
    private Activity mContext;

    public WebviewRightPopWindowView(final Activity context, List<WebviewRightEntity.DataBean> list) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.web_popup_window, null);
        rv_web_pop = conentView.findViewById(R.id.rv_web_pop);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rv_web_pop.setLayoutManager(layoutManager);
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
        microAuthTimeUtils = new MicroAuthTimeUtils();
        if (null == adapter) {
            adapter = new WebviewPopAdapter(context, list);
            rv_web_pop.setAdapter(adapter);
        } else {
            adapter.setData(list);
        }
        adapter.setFragmentMineCallBack(new FragmentMineCallBack() {
            @Override
            public void getData(String result, int positon) {
                microAuthTimeUtils.IsAuthTime(mContext, result, list.get(positon).getAuth_type(), "");
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
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }
}  