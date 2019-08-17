package com.tg.setting.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.tg.coloursteward.R;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.setting.activity.KeySendKeyPhoneActivity;
import com.tg.setting.adapter.KeyAddHorAdapter;
import com.tg.setting.adapter.KeyAddVerAdapter;
import com.tg.setting.entity.KeyBuildEntity;
import com.tg.setting.entity.KeyFloorEntity;
import com.tg.setting.entity.KeyPopEntity;
import com.tg.setting.entity.KeyUnitEntity;
import com.tg.user.model.UserModel;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 乐开-房间号
 * hxg 2019.07.22
 */
public class KeyRoomPopWindowView extends PopupWindow implements HttpResponse {
    private View contentView;
    private Activity context;
    private RelativeLayout rl_pop;
    private SwipeRecyclerView rv_hor;
    private SwipeRecyclerView rv_ver;
    private KeyAddHorAdapter hAdapter;
    private KeyAddVerAdapter vAdapter;

    private int page = 1;
    private int pageSize = 400;
    private Integer floorNum = 0;
    private int type = 1;//1小区、2花园、3楼栋、4房间号
    private String communityUuid = "";
    private String buildUuid = "";
    private String unitUuid = "";
    private String roomName = "";
    private UserModel userModel;
    private List<KeyPopEntity> mList = new ArrayList<>();
    private List<String> hList = new ArrayList<>();

    public KeyRoomPopWindowView(final Activity context, String communityUuid) {
        this.context = context;
        this.communityUuid = communityUuid;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.view_key_pop_address, null);
        userModel = new UserModel(context);
        initWindow();
        initView();
        initData();
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
        this.setBackgroundDrawable(new BitmapDrawable());
        // 设置SelectPicPopupWindow弹出窗体动画效果
        setWindowBackgroundAlpha();
    }

    private void initView() {
        rl_pop = contentView.findViewById(R.id.rl_pop);
        rv_hor = contentView.findViewById(R.id.rv_hor);
        rv_ver = contentView.findViewById(R.id.rv_ver);

        rv_hor.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        hAdapter = new KeyAddHorAdapter(context, hList, this, false);
        rv_hor.setAdapter(hAdapter);
        rv_ver.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        vAdapter = new KeyAddVerAdapter(context, mList, this, false);
        rv_ver.setAdapter(vAdapter);
    }

    private void initData() {
        getBuild();
    }

    /**
     * 根据小区获取楼栋
     */
    private void getBuild() {
        userModel.getBuild(1, communityUuid, page, pageSize, this);
    }

    /**
     * 根据楼栋获取单元
     */
    private void getUnit() {
        userModel.getUnit(2, buildUuid, page, pageSize, this);
    }

    /**
     * 根据楼层获取房屋信息
     */
    private void getFloor() {
        userModel.getFloor(3, unitUuid, page, pageSize, floorNum, this);
    }

    public void selectVer(int position) {
        page = 1;
        type = type + 1;
        String name = vAdapter.list.get(position).getName();
        String id = vAdapter.list.get(position).getId();
        floorNum = vAdapter.list.get(position).getFloorNum();
        vAdapter.list.clear();
        vAdapter.notifyDataSetChanged();
        switch (type) {
            case 2: //楼栋获取单元
                try {
                    hAdapter.list.add(name);
                    buildUuid = id;
                    hAdapter.notifyDataSetChanged();
                    roomName = "";
                    for (String s : hAdapter.list) {
                        roomName += s;
                    }
                    getUnit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                if (hAdapter.list.size() > 1) {
                    hAdapter.list.set(1, name);
                } else {
                    hAdapter.list.add(name);
                }
                unitUuid = id;
                hAdapter.notifyDataSetChanged();
                roomName = "";
                for (String s : hAdapter.list) {
                    roomName += s;
                }
                getFloor();
                break;
            case 4:
                hAdapter.list.add(name);
                hAdapter.notifyDataSetChanged();
                roomName = "";
                for (String s : hAdapter.list) {
                    roomName += s;
                }
                setRoom();
                break;
        }
    }

    public void selectHor(int position) {
        type = position + 1;
        if (type == 1) {  //点击的是获取楼栋
            hAdapter.list.clear();
            hAdapter.notifyDataSetChanged();
            vAdapter.list.clear();
            vAdapter.notifyDataSetChanged();
            getBuild();
        } else {
            List<String> list = new ArrayList<>(hAdapter.list);
            hAdapter.list.clear();
            for (int i = 0; i < type; i++) {
                hAdapter.list.add(list.get(i));  //水平的适配器
            }
            hAdapter.notifyDataSetChanged();
            vAdapter.list.clear();
            vAdapter.notifyDataSetChanged();
            switch (type) {
                case 2:
                    getUnit();
                    break;
                case 3:
                    getFloor();
                    break;
            }
        }
    }

    private void setRoom() {
        dismiss();
        if (context instanceof KeySendKeyPhoneActivity) {
            ((KeySendKeyPhoneActivity) context).setRoom(roomName);
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        if (1 == page) {
                            vAdapter.list.clear();
                        }

                        KeyBuildEntity entity = GsonUtils.gsonToBean(result, KeyBuildEntity.class);
                        KeyBuildEntity.ContentBeanX content = entity.getContent();
                        for (KeyBuildEntity.ContentBeanX.ContentBean c : content.getContent()) {
                            KeyPopEntity bean = new KeyPopEntity();
                            bean.setSelect(false);
                            bean.setId(c.getBuildingUuid());
                            bean.setName(c.getBuildingName());
                            vAdapter.list.add(bean);
                        }

                        boolean dataEmpty = vAdapter.list.size() == 0;
                        int totalRecord = content.getTotalRecord();
                        boolean hasMore = totalRecord > vAdapter.list.size();
                        rv_ver.loadMoreFinish(dataEmpty, hasMore);
                        vAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        if (1 == page) {
                            vAdapter.list.clear();
                        }

                        KeyUnitEntity entity = GsonUtils.gsonToBean(result, KeyUnitEntity.class);
                        KeyUnitEntity.ContentBeanX content = entity.getContent();
                        for (KeyUnitEntity.ContentBeanX.ContentBean c : content.getContent()) {
                            KeyPopEntity bean = new KeyPopEntity();
                            bean.setSelect(false);
                            bean.setId(c.getUnitUuid());
                            bean.setName(c.getUnitName());
                            bean.setFloorNum(c.getFloorNum());
                            vAdapter.list.add(bean);
                        }

                        boolean dataEmpty = vAdapter.list.size() == 0;
                        int totalRecord = content.getTotalRecord();
                        boolean hasMore = totalRecord > vAdapter.list.size();
                        rv_ver.loadMoreFinish(dataEmpty, hasMore);
                        vAdapter.notifyDataSetChanged();
                        if (vAdapter.list.size() == 0) {
                            setRoom();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 3:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        if (1 == page) {
                            vAdapter.list.clear();
                        }

                        KeyFloorEntity entity = GsonUtils.gsonToBean(result, KeyFloorEntity.class);
                        KeyFloorEntity.ContentBeanX content = entity.getContent();
                        for (KeyFloorEntity.ContentBeanX.ContentBean c : content.getContent()) {
                            KeyPopEntity bean = new KeyPopEntity();
                            bean.setSelect(false);
                            bean.setName(c.getRoomNo());
                            vAdapter.list.add(bean);
                        }

                        boolean dataEmpty = vAdapter.list.size() == 0;
                        int totalRecord = content.getTotalRecord();
                        boolean hasMore = totalRecord > vAdapter.list.size();
                        rv_ver.loadMoreFinish(dataEmpty, hasMore);
                        vAdapter.notifyDataSetChanged();
                        if (vAdapter.list.size() == 0) {
                            setRoom();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
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