package com.tg.setting.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.entity.BaseContentEntity;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.setting.adapter.KeyDoorAdapter;
import com.tg.setting.adapter.KeyKeyAdapter;
import com.tg.setting.entity.KeyCommunityListEntity;
import com.tg.setting.entity.KeyDoorEntity;
import com.tg.setting.view.KeyChoosePopWindowView;
import com.tg.setting.view.KeyCommunityPopWindowView;
import com.tg.user.model.UserModel;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.youmai.hxsdk.utils.DisplayUtil;
import com.tg.coloursteward.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 乐开-门禁管理
 *
 * @author hxg 2019.07.18
 */
public class KeyDoorManagerActivity extends BaseActivity implements HttpResponse {
    private TextView tv_choose;
    private LinearLayout ll_choose_down;
    private ImageView iv_choose_down;
    private FrameLayout fl_back;
    private EditText et_search;
    private LinearLayout ll_title;
    private ImageView iv_title_down;
    private TextView tv_key_title;
    private ImageView iv_add;
    private ImageView iv_msg;
    private RelativeLayout rl_search;
    private ImageView iv_close;
    private RelativeLayout rl_no_data;
    private RelativeLayout rl_no_door;
    private TextView tv_add_door;
    private RelativeLayout rl_no_permission;
    private TextView tv_permission_back;
    private TextView tv_door;
    private TextView tv_key;

    private SwipeRecyclerView rv_door;
    private SwipeRecyclerView rv_key;
    private List<String> list = new ArrayList<>();
    private KeyDoorAdapter doorAdapter;
    private KeyKeyAdapter keyAdapter;
    private int doorPage = 1;
    private int keyPage = 1;
    private UserModel userModel;
    private List<KeyCommunityListEntity.ContentBeanX.ContentBean> communityList = new ArrayList<>();
    private List<KeyDoorEntity.ContentBeanX.ContentBean> doorList = new ArrayList<>();
    private String accountUuid;
    private String communityUuid = "";
    private int selectPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        headView.setVisibility(View.GONE);
        userModel = new UserModel(this);
        initView();
        initData();
        initListener();
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_key_door_manager, null);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        tv_choose = findViewById(R.id.tv_choose);
        ll_choose_down = findViewById(R.id.ll_choose_down);
        iv_choose_down = findViewById(R.id.iv_choose_down);
        fl_back = findViewById(R.id.fl_back);
        et_search = findViewById(R.id.et_search);
        ll_title = findViewById(R.id.ll_title);
        iv_title_down = findViewById(R.id.iv_title_down);
        tv_key_title = findViewById(R.id.tv_key_title);
        iv_add = findViewById(R.id.iv_add);
        iv_msg = findViewById(R.id.iv_msg);
        rl_search = findViewById(R.id.rl_search);
        iv_close = findViewById(R.id.iv_close);
        rv_door = findViewById(R.id.rv_door);
        rv_key = findViewById(R.id.rv_key);
        rl_no_data = findViewById(R.id.rl_no_data);
        rl_no_door = findViewById(R.id.rl_no_door);
        tv_add_door = findViewById(R.id.tv_add_door);
        rl_no_permission = findViewById(R.id.rl_no_permission);
        tv_permission_back = findViewById(R.id.tv_permission_back);
        tv_door = findViewById(R.id.tv_door);
        tv_key = findViewById(R.id.tv_key);

        tv_choose.setOnClickListener(singleListener);
        ll_choose_down.setOnClickListener(singleListener);
        fl_back.setOnClickListener(singleListener);
        iv_add.setOnClickListener(singleListener);
        iv_msg.setOnClickListener(singleListener);
        iv_close.setOnClickListener(singleListener);
        tv_add_door.setOnClickListener(singleListener);
        tv_permission_back.setOnClickListener(singleListener);
        tv_door.setOnClickListener(singleListener);
        tv_key.setOnClickListener(singleListener);
        ll_title.setOnClickListener(singleListener);
        et_search.setCursorVisible(false);

        rv_door.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        doorAdapter = new KeyDoorAdapter(this, doorList);
        rv_door.setAdapter(doorAdapter);
        rv_door.useDefaultLoadMore();
        rv_door.setLoadMoreListener(() -> {
            doorPage++;
            getDoorList();
        });

        rv_key.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        keyAdapter = new KeyKeyAdapter(this, null);
        rv_key.setAdapter(keyAdapter);
        rv_key.useDefaultLoadMore();
        rv_key.setLoadMoreListener(() -> keyPage++);
    }

    private void initData() {
        DisplayUtil.showInput(false, KeyDoorManagerActivity.this);
        accountUuid = spUtils.getStringData(SpConstants.UserModel.ACCOUNT_UUID, "");
        userModel.getCommunityList(1, accountUuid, 1, 20, true, this);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 1:
                try {
                    if (View.VISIBLE == rl_no_permission.getVisibility()) {
                        rl_no_permission.setVisibility(View.GONE);
                    }
                    if (View.VISIBLE == rl_no_door.getVisibility()) {
                        rl_no_door.setVisibility(View.GONE);
                    }
                    BaseContentEntity baseContentEntity = GsonUtils.gsonToBean(result, KeyCommunityListEntity.class);
                    if (1 == baseContentEntity.getCode()) {
                        rl_no_permission.setVisibility(View.VISIBLE);
                    } else {
                        KeyCommunityListEntity keyCommunityListEntity = GsonUtils.gsonToBean(result, KeyCommunityListEntity.class);
                        KeyCommunityListEntity.ContentBeanX content = keyCommunityListEntity.getContent();
                        if (content.getContent().size() > 0) {
                            iv_title_down.setVisibility(View.VISIBLE);
                            communityList.addAll(content.getContent());
                            tv_key_title.setText(content.getContent().get(0).getName());
                            communityUuid = content.getContent().get(0).getCommunityUuid();
                            if (!TextUtils.isEmpty(communityUuid)) {
                                iv_add.setVisibility(View.VISIBLE);
                            }
                            getDoorList();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    if (View.VISIBLE == rl_no_permission.getVisibility()) {
                        rl_no_permission.setVisibility(View.GONE);
                    }
                    if (View.VISIBLE == rl_no_door.getVisibility()) {
                        rl_no_door.setVisibility(View.GONE);
                    }
                    KeyDoorEntity keyDoorEntity = GsonUtils.gsonToBean(result, KeyDoorEntity.class);
                    KeyDoorEntity.ContentBeanX content = keyDoorEntity.getContent();
                    if (1 == doorPage) {
                        doorList.clear();
                    }
                    doorList.addAll(content.getContent());
                    boolean dataEmpty = doorList.size() == 0;
                    int totalRecord = content.getTotalRecord();
                    boolean hasMore = totalRecord > doorList.size();
                    rv_door.loadMoreFinish(dataEmpty, hasMore);
                    doorAdapter.notifyDataSetChanged();
                    if (1 == doorPage && 0 == doorList.size()) {//无门禁
                        rl_no_door.setVisibility(View.VISIBLE);
                        rv_door.setVisibility(View.GONE);
                    } else {
                        if (View.GONE == rl_no_door.getVisibility()) {
                            rl_no_door.setVisibility(View.GONE);
                            rv_door.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 获取门禁列表
     */
    private void getDoorList() {
        userModel.getDoorList(2, communityUuid, doorPage, 20, this);
    }

    /**
     * 选择小区
     */
    public void selectCommunity(int position, String name, String uuid) {
        selectPosition = position;
        tv_key_title.setText(name);
        communityUuid = uuid;
        getDoorList();
    }

    /**
     * 发送钥匙 绑定门禁
     */
    public void todo(int position, int status) {
        if (status == 0) {
            Intent i = new Intent(this, KeyBindDoorActivity.class);
            i.putExtra(KeyBindDoorActivity.DOOR_ID, doorList.get(position).getId());
            startActivityForResult(i, 1);
        } else {
            Intent i = new Intent(this, KeySendKeyListActivity.class);
            i.putExtra(KeySendKeyListActivity.DOOR_ID, doorList.get(position).getId());
            i.putExtra(KeySendKeyListActivity.COMMUNITY_UUID, communityUuid);
            i.putExtra(KeySendKeyListActivity.KEY_NAME, doorList.get(position).getAccessName());
            startActivityForResult(i, 1);
        }
    }

    private void initListener() {
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    iv_close.setVisibility(View.VISIBLE);
                    et_search.setCursorVisible(true);//显示光标
                } else {
                    iv_close.setVisibility(View.GONE);
                    et_search.setCursorVisible(false);
                    DisplayUtil.showInput(false, KeyDoorManagerActivity.this);
                }
            }
        });
        et_search.setOnEditorActionListener((v, actionId, event) -> {
            String keyword = et_search.getText().toString().trim();
            if (actionId == EditorInfo.IME_ACTION_SEARCH && !TextUtils.isEmpty(keyword)) {
                DisplayUtil.showInput(false, this);
            }
            return false;
        });
    }

    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
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
            case R.id.ll_choose_down:
            case R.id.tv_choose:
                iv_choose_down.setRotation(180);
                KeyChoosePopWindowView choosePop = new KeyChoosePopWindowView(this);
                choosePop.showPopupWindow(tv_choose);
                choosePop.setOnDismissListener(() -> {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 1.0f;
                    getWindow().setAttributes(lp);
                    iv_choose_down.setRotation(0);
                });
                break;
            case R.id.tv_add_door:
            case R.id.iv_add:
                Intent intent = new Intent(this, KeyAddDoorActivity.class);
                intent.putExtra(KeyAddDoorActivity.COMMUNITY_UUID, communityUuid);
                startActivityForResult(intent, 1);
                break;
            case R.id.iv_msg:
                ToastUtil.showShortToast(this, "开发中，敬请期待...");
                break;
            case R.id.iv_close:
                et_search.setText("");
                DisplayUtil.showInput(false, KeyDoorManagerActivity.this);
                break;
            case R.id.tv_permission_back:
                finish();
                break;
            case R.id.tv_door:
                rl_search.setVisibility(View.GONE);
                Drawable drawableDoor = this.getResources().getDrawable(R.drawable.ic_key_door_select);
                tv_door.setCompoundDrawablesWithIntrinsicBounds(null, drawableDoor, null, null);
                Drawable drawableKey = this.getResources().getDrawable(R.drawable.ic_key_key);
                tv_key.setCompoundDrawablesWithIntrinsicBounds(null, drawableKey, null, null);

                rv_door.setVisibility(View.VISIBLE);
                rv_key.setVisibility(View.GONE);
                break;
            case R.id.tv_key:
                ToastUtil.showShortToast(this, "开发中，敬请期待...");
                rl_search.setVisibility(View.GONE);
                Drawable key = this.getResources().getDrawable(R.drawable.ic_key_key_select);
                tv_key.setCompoundDrawablesWithIntrinsicBounds(null, key, null, null);
                Drawable door = this.getResources().getDrawable(R.drawable.ic_key_door);
                tv_door.setCompoundDrawablesWithIntrinsicBounds(null, door, null, null);

                rv_key.setVisibility(View.VISIBLE);
                rv_door.setVisibility(View.GONE);
                break;
        }
        return super.handClickEvent(v);
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
                getDoorList();
            }
        }
    }
}