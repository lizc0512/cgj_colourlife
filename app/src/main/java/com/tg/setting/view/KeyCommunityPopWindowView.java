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
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.setting.activity.KeyDoorManagerActivity;
import com.tg.setting.adapter.KeyCommunityAdapter;
import com.tg.setting.entity.KeyCommunityListEntity;
import com.tg.user.model.UserModel;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 乐开-选择小区
 * hxg 2019.07.25
 */
public class KeyCommunityPopWindowView extends PopupWindow implements HttpResponse {
    private View contentView;
    private Activity context;
    private LinearLayout ll_pop;
    private SwipeRecyclerView rv_community;
    private KeyCommunityAdapter adapter;

    private int page = 1;
    private List<KeyCommunityListEntity.ContentBeanX.ContentBean> list = new ArrayList<>();
    private UserModel userModel;
    private String accountUuid;
    private int selectPosition;

    public KeyCommunityPopWindowView(final Activity context, List<KeyCommunityListEntity.ContentBeanX.ContentBean> list, String accountUuid, int selectPosition) {
        this.context = context;
        this.accountUuid = accountUuid;
        this.selectPosition = selectPosition;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.view_key_pop_community, null);
        this.list.addAll(list);
        userModel = new UserModel(context);
        initWindow();
        initView();
        initListener();
    }

    private void initListener() {

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
        rv_community = contentView.findViewById(R.id.rv_community);
        rv_community.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        adapter = new KeyCommunityAdapter(context, list, this, selectPosition);
        rv_community.setAdapter(adapter);
        rv_community.useDefaultLoadMore();
        rv_community.setLoadMoreListener(() -> {
            page++;
            userModel.getCommunityList(1, accountUuid, page, 20, false, this);
        });
        rv_community.loadMoreFinish(false, true);
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

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 1:
                try {
                    KeyCommunityListEntity keyCommunityListEntity = GsonUtils.gsonToBean(result, KeyCommunityListEntity.class);
                    KeyCommunityListEntity.ContentBeanX content = keyCommunityListEntity.getContent();
                    list.addAll(content.getContent());
                    boolean dataEmpty = list.size() == 0;
                    int totalRecord = content.getTotalRecord();
                    boolean hasMore = totalRecord > list.size();
                    rv_community.loadMoreFinish(dataEmpty, hasMore);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void selectCommunity(int position, String name, String uuid) {
        dismiss();
        ((KeyDoorManagerActivity) context).selectCommunity(position, name, uuid);
    }
}