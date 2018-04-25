package com.youmai.hxsdk.module.groupchat;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.db.bean.Contact;
import com.youmai.hxsdk.router.APath;
import com.youmai.hxsdk.utils.GlideRoundTransform;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2018.04.18 16:36
 * 描述：单聊详情
 */
public class ChatDetailsActivity extends SdkBaseActivity {

    public static final String GROUP_LIST = "GROUP_LIST";
    public static final String DS_NAME = "DS_NAME";
    public static final String DS_USER_AVATAR = "DS_USER_AVATAR";
    public static final String DS_UUID = "DS_UUID";

    private TextView mTvBack, mTvTitle;
    private ImageView mSelfHeader;
    private TextView mSelfName;
    private ImageView mAddMore;
    private RelativeLayout mClearRecords;

    private String uuid;
    private String avatar;
    private String realname;

    List<Contact> groupList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_im_chat_details);

        initView();
        setOnClickListener();

        HuxinSdkManager.instance().getStackAct().addActivity(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != groupList) {
            groupList.clear();
        }

        HuxinSdkManager.instance().getStackAct().removeActivity(this);
    }

    private void initView() {

        realname = getIntent().getStringExtra(DS_NAME);
        avatar = getIntent().getStringExtra(DS_USER_AVATAR);
        uuid = getIntent().getStringExtra(DS_UUID);

        mTvBack = findViewById(R.id.tv_back);
        mTvTitle = findViewById(R.id.tv_title);

        mSelfName = findViewById(R.id.tv_self_name);
        mSelfHeader = findViewById(R.id.iv_self_header);
        mAddMore = findViewById(R.id.iv_add_more);
        mClearRecords = findViewById(R.id.rl_clear_chat_records);

        mTvTitle.setText("聊天详情");
        mSelfName.setText(realname);

        Glide.with(mContext)
                .load(avatar)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .override(60, 60)
                        .transform(new GlideRoundTransform())
                        .error(R.drawable.color_default_header))
                .into(mSelfHeader);

        createGroupMap();

    }

    void createGroupMap() {
        Contact contact = new Contact();
        contact.setUuid(uuid);
        contact.setRealname(realname);
        contact.setAvatar(avatar);
        groupList.add(contact);

        Contact self = new Contact();
        self.setUuid(HuxinSdkManager.instance().getUuid());
        self.setAvatar(HuxinSdkManager.instance().getHeadUrl());
        self.setRealname(HuxinSdkManager.instance().getRealName());
        //self.setUsername(HuxinSdkManager.instance().getUserName());
        groupList.add(self);

    }

    void setOnClickListener() {
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(APath.GROUP_CREATE_ADD_CONTACT)
                        .withParcelableArrayList(GROUP_LIST, (ArrayList<? extends Parcelable>) groupList)
                        .withInt("DETAIL_TYPE", 1)
                        .navigation(ChatDetailsActivity.this);
            }
        });
    }

}
