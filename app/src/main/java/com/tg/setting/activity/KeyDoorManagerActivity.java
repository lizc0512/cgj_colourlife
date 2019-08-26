package com.tg.setting.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.setting.entity.KeyCommunityListEntity;
import com.tg.setting.fragment.KeyDoorBagsFragment;
import com.tg.setting.fragment.KeyDoorListFragment;
import com.tg.setting.fragment.KeyDoorStatisticsFragment;
import com.tg.setting.view.KeyCommunityPopWindowView;
import com.tg.user.model.UserModel;
import com.youmai.hxsdk.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 乐开-门禁管理
 *
 * @author hxg 2019.07.18
 */
public class KeyDoorManagerActivity extends BaseActivity implements HttpResponse {
    private ImageView back_finish_img;
    private LinearLayout ll_title;
    private TextView tv_key_title;
    private ImageView iv_title_down;
    private ImageView iv_add;
    private ImageView iv_msg;
    private TextView tv_door;
    private TextView tv_key;
    private TextView tv_statistics;
    private List<KeyCommunityListEntity.ContentBeanX.ContentBean> communityList = new ArrayList<>();
    private String accountUuid;
    private String communityUuid = "";
    private String communityName = "";
    private int selectPosition = 0;
    private UserModel userModel;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private KeyDoorListFragment keyDoorListFragment;
    private KeyDoorBagsFragment keyDoorBagsFragment;
    private KeyDoorStatisticsFragment keyDoorStatisticsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        headView.setVisibility(View.GONE);
        userModel = new UserModel(this);
        fragmentManager = getSupportFragmentManager();
        initView();
        initData();
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_key_door_manager, null);
    }

    private void initView() {
        back_finish_img = findViewById(R.id.back_finish_img);
        ll_title = findViewById(R.id.ll_title);
        iv_title_down = findViewById(R.id.iv_title_down);
        tv_key_title = findViewById(R.id.tv_key_title);
        iv_add = findViewById(R.id.iv_add);
        iv_msg = findViewById(R.id.iv_msg);
        tv_door = findViewById(R.id.tv_door);
        tv_key = findViewById(R.id.tv_key);
        tv_statistics = findViewById(R.id.tv_statistics);

        back_finish_img.setOnClickListener(singleListener);
        iv_add.setOnClickListener(singleListener);
        iv_msg.setOnClickListener(singleListener);
        tv_door.setOnClickListener(singleListener);
        tv_key.setOnClickListener(singleListener);
        tv_statistics.setOnClickListener(singleListener);
        ll_title.setOnClickListener(singleListener);
    }

    private void initData() {
        DisplayUtil.showInput(false, KeyDoorManagerActivity.this);
        accountUuid = UserInfo.uid;
        userModel.getCommunityList(1, accountUuid, 1, 20, true, this);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 1:
                try {
                    KeyCommunityListEntity keyCommunityListEntity = GsonUtils.gsonToBean(result, KeyCommunityListEntity.class);
                    KeyCommunityListEntity.ContentBeanX content = keyCommunityListEntity.getContent();
                    communityList.addAll(content.getContent());
                    if (communityList.size() > 0) {
                        iv_title_down.setVisibility(View.VISIBLE);
                        KeyCommunityListEntity.ContentBeanX.ContentBean contentBean = communityList.get(0);
                        communityName = contentBean.getName();
                        tv_key_title.setText(communityName);
                        communityUuid = contentBean.getCommunityUuid();
                        if (!TextUtils.isEmpty(communityUuid)) {
                            iv_add.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                transaction = fragmentManager.beginTransaction();
                if (keyDoorListFragment == null) {
                    keyDoorListFragment = KeyDoorListFragment.getDoorListFragment(communityUuid, communityName);
                    transaction.add(R.id.content_layout, keyDoorListFragment);
                } else {
                    transaction.show(keyDoorListFragment);
                }
                transaction.commitAllowingStateLoss();
                break;
        }
    }


    /**
     * 选择小区
     */
    public void selectCommunity(int position, String name, String uuid) {
        selectPosition = position;
        tv_key_title.setText(name);
        communityName = name;
        communityUuid = uuid;
        if (null != keyDoorListFragment) {
            keyDoorListFragment.changeCommunity(communityUuid, communityName);
        }
        if (null != keyDoorBagsFragment) {
            keyDoorBagsFragment.changeCommunity(communityUuid, communityName);
        }
    }

    /**
     * 发送钥匙 绑定门禁
     */
    public void todo(int position, int status) {
        if (null != keyDoorListFragment) {
            keyDoorListFragment.todo(position, status);
        }
    }

    public void toKeyInfor(int position, int status) {
        if (null != keyDoorListFragment) {
            keyDoorListFragment.toKeyInfor(position, status);
        }
    }

    public void toSendPackge(int position) {
        if (null != keyDoorBagsFragment) {
            keyDoorBagsFragment.toSendPackge(position);
        }
    }


    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.back_finish_img:
                finish();
                break;
            case R.id.ll_title:
                iv_title_down.setRotation(180);
                KeyCommunityPopWindowView areaPop = new KeyCommunityPopWindowView(this, communityList, accountUuid, selectPosition);
                areaPop.showPopupWindow(contentLayout);
                areaPop.setOnDismissListener(() -> {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 1.0f;
                    getWindow().setAttributes(lp);
                    iv_title_down.setRotation(0);
                });
                break;
            case R.id.iv_add:
                Intent intent = new Intent(this, KeyAddDoorActivity.class);
                intent.putExtra(KeyAddDoorActivity.COMMUNITY_UUID, communityUuid);
                startActivityForResult(intent, 1);
                break;
            case R.id.iv_msg:
                ToastUtil.showShortToast(this, "开发中，敬请期待...");
                break;
            case R.id.tv_door:
                setTabStyle(tv_door, R.drawable.ic_key_door_select);
                setTabStyle(tv_key, R.drawable.ic_key_key);
                setTabStyle(tv_statistics, R.drawable.ic_key_door_statistics);
                iv_add.setVisibility(View.VISIBLE);
                hideTransaction();
                if (keyDoorListFragment == null) {
                    keyDoorListFragment = KeyDoorListFragment.getDoorListFragment(communityUuid, communityName);
                    transaction.add(R.id.content_layout, keyDoorListFragment);
                } else {
                    transaction.show(keyDoorListFragment);
                }
                transaction.commitAllowingStateLoss();
                break;
            case R.id.tv_key:
                setTabStyle(tv_door, R.drawable.ic_key_door);
                setTabStyle(tv_key, R.drawable.ic_key_key_select);
                setTabStyle(tv_statistics, R.drawable.ic_key_door_statistics);
                iv_add.setVisibility(View.GONE);
                hideTransaction();
                if (keyDoorBagsFragment == null) {
                    keyDoorBagsFragment = KeyDoorBagsFragment.getKeyBagsFragment(communityUuid, communityName);
                    transaction.add(R.id.content_layout, keyDoorBagsFragment);
                } else {
                    transaction.show(keyDoorBagsFragment);
                }
                transaction.commitAllowingStateLoss();
                break;
            case R.id.tv_statistics:
                setTabStyle(tv_door, R.drawable.ic_key_door);
                setTabStyle(tv_key, R.drawable.ic_key_key);
                setTabStyle(tv_statistics, R.drawable.ic_key_door_statistics_select);
                iv_add.setVisibility(View.GONE);
                hideTransaction();
                if (keyDoorStatisticsFragment == null) {
                    keyDoorStatisticsFragment = KeyDoorStatisticsFragment.getKeyStatisticsFragment(communityUuid, communityName);
                    transaction.add(R.id.content_layout, keyDoorStatisticsFragment);
                } else {
                    transaction.show(keyDoorStatisticsFragment);
                }
                transaction.commitAllowingStateLoss();
                break;
        }
        return super.handClickEvent(v);
    }


    private void setTabStyle(TextView selectText, int drawableId) {
        Drawable drawableDoor = this.getResources().getDrawable(drawableId);
        selectText.setCompoundDrawablesWithIntrinsicBounds(null, drawableDoor, null, null);
    }

    private void hideTransaction() {
        transaction = fragmentManager.beginTransaction();
        if (null != keyDoorBagsFragment) {
            transaction.hide(keyDoorBagsFragment);
        }
        if (null != keyDoorListFragment) {
            transaction.hide(keyDoorListFragment);
        }
        if (null != keyDoorStatisticsFragment) {
            transaction.hide(keyDoorStatisticsFragment);
        }
    }

    @Override
    public String getHeadTitle() {
        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (1 == requestCode) {
                if (null != keyDoorListFragment) {
                    keyDoorListFragment.changeCommunity(communityUuid, communityName);
                }
            }
        }
    }

    public void hideTitleBottomLayout() {  //没有权限时隐藏标题栏 和tab
        findViewById(R.id.title_layout).setVisibility(View.GONE);
        findViewById(R.id.ll_bottom).setVisibility(View.GONE);
    }
}
