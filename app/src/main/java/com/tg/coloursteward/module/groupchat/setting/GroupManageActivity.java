package com.tg.coloursteward.module.groupchat.setting;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.db.bean.Contact;

import java.util.List;

/**
 * 作者：create by YW
 * 日期：2018.04.26 17:05
 * 描述: 群名设置
 */
public class GroupManageActivity extends SdkBaseActivity {

    public static final String GROUP_ID = "groupId";
    public static final String GROUP_NAME = "groupName";
    public static final String GROUP_LIST = "GROUP_LIST";
    private static final String TAG_SEARCH_CONTACT_FRAGMENT = "groupManageFragment";

    private TextView tv_back, tv_title, tv_title_right;
    private RelativeLayout rl_trans_group;

    private String name;
    private int groupId;

    private GroupManageFragment groupManageFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_manage_setting);

        initView();
        initData();
        setClickListener();
    }

    private void initView() {
        List<Contact> list = getIntent().getParcelableArrayListExtra(GROUP_LIST);

        tv_back = findViewById(R.id.tv_left_cancel);
        tv_title = findViewById(R.id.tv_title);
        tv_title_right = findViewById(R.id.tv_right_sure);
        tv_title_right.setVisibility(View.GONE);
        rl_trans_group = findViewById(R.id.rl_trans_group);

        groupManageFragment = new GroupManageFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_select_container, groupManageFragment, TAG_SEARCH_CONTACT_FRAGMENT);
        transaction.hide(groupManageFragment);
        transaction.commitAllowingStateLoss();

        groupManageFragment.setList(list);
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
                if (groupManageFragment.isHidden()) {
                    finish();
                } else {
                    tv_title_right.setVisibility(View.GONE);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.hide(groupManageFragment);
                    transaction.commit();
                }
            }
        });

        tv_title_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        rl_trans_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_title_right.setVisibility(View.VISIBLE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.show(groupManageFragment);
                transaction.commit();
            }
        });
    }

}
