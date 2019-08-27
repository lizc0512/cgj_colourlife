package com.tg.setting.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.CircleImageView;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.setting.adapter.KeyDoorUserRecordAdapter;
import com.tg.setting.entity.KeyByAccessEntity;
import com.tg.setting.entity.KeyDoorOpenLogsEntity;
import com.tg.setting.model.KeyDoorModel;

import java.util.ArrayList;
import java.util.List;

import static com.tg.setting.activity.KeySendKeyListActivity.COMMUNITY_UUID;
import static com.tg.setting.activity.KeySendKeyListActivity.DOOR_ID;
import static com.tg.setting.activity.KeySendKeyListActivity.DOOR_IDENTITY_ID;
import static com.tg.setting.activity.KeySendKeyListActivity.DOOR_IDENTITY_NAME;
import static com.tg.setting.activity.KeySendKeyListActivity.DOOR_ROOM;
import static com.tg.setting.activity.KeySendKeyListActivity.DOOR_USER_NAME;
import static com.tg.setting.activity.KeySendKeyListActivity.FORM_SOURCE;
import static com.tg.setting.activity.KeySendKeyListActivity.KEY_CONTENT;

/**
 * 门禁信息人员的信息
 *
 * @author hxg 2019.07.18
 */
public class KeyDoorUserDetailsActivity extends BaseActivity implements HttpResponse {

    public static final String KEYUSERDETAILS = "keyuserdetails";
    private CircleImageView door_user_photo;
    private TextView tv_door_status;
    private TextView tv_door_username;
    private TextView tv_door_identify;
    private TextView tv_door_phone;
    private TextView tv_username_phone;
    private TextView tv_door_name;
    private TextView tv_door_date;
    private TextView tv_send_key;
    private TextView tv_frozen_key;
    private TextView tv_delete_key;
    private XRecyclerView rv_out_record;
    private TextView tv_empty_record;
    private KeyByAccessEntity.ContentBeanX.ContentBean contentBean;

    private String status;
    private String accessId;
    private String accessName;

    private KeyDoorModel keyDoorModel;
    private int page = 1;
    public List<KeyDoorOpenLogsEntity.ContentBean> mList;

    private KeyDoorUserRecordAdapter keyDoorUserRecordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        door_user_photo = findViewById(R.id.door_user_photo);
        tv_door_status = findViewById(R.id.tv_door_status);
        tv_door_username = findViewById(R.id.tv_door_username);
        tv_door_identify = findViewById(R.id.tv_door_identify);
        tv_door_phone = findViewById(R.id.tv_door_phone);
        tv_username_phone = findViewById(R.id.tv_username_phone);
        tv_door_name = findViewById(R.id.tv_door_name);
        tv_door_date = findViewById(R.id.tv_door_date);
        tv_send_key = findViewById(R.id.tv_send_key);
        tv_frozen_key = findViewById(R.id.tv_frozen_key);
        tv_delete_key = findViewById(R.id.tv_delete_key);
        rv_out_record = findViewById(R.id.rv_out_record);
        tv_empty_record = findViewById(R.id.tv_empty_record);

        tv_send_key.setOnClickListener(singleListener);
        tv_frozen_key.setOnClickListener(singleListener);
        tv_delete_key.setOnClickListener(singleListener);
        rv_out_record.setPullRefreshEnabled(false);
        rv_out_record.setLoadingMoreEnabled(true);

        rv_out_record.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                page++;
                getKeyDoorOpenLogs();
            }
        });
        keyDoorModel = new KeyDoorModel(KeyDoorUserDetailsActivity.this);
        Intent intent = getIntent();
        accessName = intent.getStringExtra(KeySendKeyListActivity.KEY_CONTENT);
        contentBean = (KeyByAccessEntity.ContentBeanX.ContentBean) intent.getSerializableExtra(KEYUSERDETAILS);
        status = contentBean.getStatus();
        accessId = contentBean.getId();
        switch (status) {
            case "1":
                tv_frozen_key.setVisibility(View.VISIBLE);
                tv_delete_key.setVisibility(View.VISIBLE);
                tv_send_key.setVisibility(View.VISIBLE);
                tv_door_status.setText("正常");
                tv_door_status.setTextColor(Color.parseColor("#1DA1F4"));
                break;
            case "2":
                tv_frozen_key.setText("解冻钥匙");
                tv_frozen_key.setVisibility(View.VISIBLE);
                tv_delete_key.setVisibility(View.VISIBLE);
                tv_door_status.setText("被冻结钥匙");
                tv_door_status.setTextColor(Color.parseColor("#EF7E33"));
                break;
            case "3":
                tv_send_key.setVisibility(View.VISIBLE);
                tv_door_status.setText("被删除钥匙");
                tv_door_status.setTextColor(Color.parseColor("#F7667C"));
                break;
            default:
                tv_send_key.setVisibility(View.VISIBLE);
                tv_door_status.setText("失效");
                tv_door_status.setTextColor(Color.parseColor("#999FAA"));
                break;
        }
        GlideUtils.loadImageView(KeyDoorUserDetailsActivity.this, contentBean.getAvatar(), door_user_photo);
        tv_door_username.setText(contentBean.getScreenName());
        tv_door_identify.setText(contentBean.getIdentityName());
        tv_door_phone.setText(StringUtils.getHandlePhone(contentBean.getPhoneNumber()));
        StringBuffer sb = new StringBuffer();
        sb.append(contentBean.getToScreenName());
        sb.append(" ");
        sb.append(StringUtils.getHandlePhone(contentBean.getToPhone()));
        tv_username_phone.setText(sb.toString());
        tv_door_name.setText(accessName);
        String startTime = contentBean.getStartTime();
        if (!TextUtils.isEmpty(startTime)) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(startTime);
            stringBuffer.append("-");
            stringBuffer.append(contentBean.getEndTime());
            tv_door_date.setText(stringBuffer.toString());
        } else {
            tv_door_date.setText("");
        }
        keyDoorModel = new KeyDoorModel(KeyDoorUserDetailsActivity.this);
        mList = new ArrayList<>();
        getKeyDoorOpenLogs();
    }

    private void getKeyDoorOpenLogs() {
        keyDoorModel.getKeyOpenLog(0, accessId, page, "", KeyDoorUserDetailsActivity.this);
    }

    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.tv_frozen_key:  //要区分不同的状态
                DialogFactory.getInstance().showDoorDialog(KeyDoorUserDetailsActivity.this, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //调用解绑的接口 解绑成功 回调到门禁列表 进行刷新门禁
                                if ("1".equals(status)) {
                                    //冻结
                                    keyDoorModel.frozenKeyOperate(1, accessId, "2", KeyDoorUserDetailsActivity.this);
                                } else {
                                    //  解冻
                                    keyDoorModel.thawKeyOperate(3, accessId, KeyDoorUserDetailsActivity.this);
                                }
                            }
                        }, null, 0, "冻结钥匙后，用户将不能使用\n" +
                                "钥匙开门，是否确认冻结？",
                        null, null);

                break;
            case R.id.tv_delete_key:
                DialogFactory.getInstance().showDoorDialog(KeyDoorUserDetailsActivity.this, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //删除
                                keyDoorModel.frozenKeyOperate(3, accessId, "3", KeyDoorUserDetailsActivity.this);
                            }
                        }, null, 0, "删除钥匙后，用户将没有开门\n" +
                                "权限，是否确认删除？",
                        null, null);
                break;
            case R.id.tv_send_key:
                if ("3".equals(status)) {
                    //删除后的重新发送
                    keyDoorModel.thawKeyOperate(3, accessId, KeyDoorUserDetailsActivity.this);
                } else {
                    Intent intent = new Intent(KeyDoorUserDetailsActivity.this, KeySendKeyPhoneActivity.class);
                    intent.putExtra(DOOR_ID, contentBean.getAccessId());
                    intent.putExtra(KEY_CONTENT, accessName);
                    intent.putExtra(COMMUNITY_UUID, contentBean.getCommunityUuid());
                    intent.putExtra(DOOR_ROOM, contentBean.getHomeLoc());
                    intent.putExtra(DOOR_IDENTITY_ID, contentBean.getIdentityId());
                    intent.putExtra(DOOR_IDENTITY_NAME, contentBean.getIdentityName());
                    intent.putExtra(DOOR_USER_NAME, contentBean.getPhoneNumber());
                    intent.putExtra(FORM_SOURCE, 2);
                    startActivityForResult(intent, 1);
                }
                break;


        }
        return super.handClickEvent(v);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_key_door_userdetails, null);
    }

    @Override
    public String getHeadTitle() {
        return "用户详情";
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                try {
                    KeyDoorOpenLogsEntity keyDoorOpenLogsEntity = GsonUtils.gsonToBean(result, KeyDoorOpenLogsEntity.class);
                    int totalRecord = keyDoorOpenLogsEntity.getTotalRecord();
                    if (page == 1) {
                        mList.clear();
                    }
                    List<KeyDoorOpenLogsEntity.ContentBean> requestList = keyDoorOpenLogsEntity.getContent();
                    mList.addAll(requestList);
                    if (mList.size() == 0) {
                        rv_out_record.setVisibility(View.GONE);
                        tv_empty_record.setVisibility(View.VISIBLE);
                    } else {
                        rv_out_record.setVisibility(View.VISIBLE);
                        tv_empty_record.setVisibility(View.GONE);
                        if (null == keyDoorUserRecordAdapter) {
                            keyDoorUserRecordAdapter = new KeyDoorUserRecordAdapter(KeyDoorUserDetailsActivity.this, mList);
                            rv_out_record.setLayoutManager(new LinearLayoutManager(KeyDoorUserDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
                            rv_out_record.setAdapter(keyDoorUserRecordAdapter);
                        } else {
                            keyDoorUserRecordAdapter.notifyDataSetChanged();
                        }
                    }
                    if (mList.size() >= totalRecord) {
                        rv_out_record.setLoadingMoreEnabled(false);
                    }
                    rv_out_record.loadMoreComplete();
                } catch (Exception e) {

                }
                break;
            case 1:
                ToastUtil.showShortToastCenter(KeyDoorUserDetailsActivity.this, "钥匙冻结成功");
                setResult(Activity.RESULT_OK);
                finish();
                break;
            case 2:
                ToastUtil.showShortToastCenter(KeyDoorUserDetailsActivity.this, "钥匙解冻成功");
                setResult(Activity.RESULT_OK);
                finish();
                break;
            case 3:
                ToastUtil.showShortToastCenter(KeyDoorUserDetailsActivity.this, "钥匙删除成功");
                setResult(Activity.RESULT_OK);
                finish();
                break;
            case 4:
                ToastUtil.showShortToastCenter(KeyDoorUserDetailsActivity.this, "钥匙重新发送成功");
                setResult(Activity.RESULT_OK);
                finish();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}
