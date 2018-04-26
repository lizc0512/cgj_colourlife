package com.tg.coloursteward.module.groupchat.setting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tg.coloursteward.R;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.CommonUtils;
import com.youmai.hxsdk.utils.StringUtils;

/**
 * 作者：create by YW
 * 日期：2018.04.26 17:05
 * 描述: 群名设置
 */
public class GroupManageActivity extends Activity {

    public static final String GROUP_ID = "groupId";
    public static final String GROUP_NAME = "groupName";

    private TextView tv_back, tv_title, tv_title_right;
    private RelativeLayout rl_trans_group;

    private String name;
    private int groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_manage_setting);

        initView();
        initData();
        setClickListener();
    }

    private void initView() {
        tv_back = findViewById(R.id.tv_left_cancel);
        tv_title = findViewById(R.id.tv_title);
        tv_title_right = findViewById(R.id.tv_right_sure);
        tv_title_right.setVisibility(View.GONE);
        rl_trans_group = findViewById(R.id.rl_trans_group);
    }

    private void initData() {
        groupId = getIntent().getIntExtra(GROUP_ID, -1);
        name = getIntent().getStringExtra(GROUP_NAME);
        tv_title.setText("群管理");
    }

    private void setClickListener() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_title_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }


}
