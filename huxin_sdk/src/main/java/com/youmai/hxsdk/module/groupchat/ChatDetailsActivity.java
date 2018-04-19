package com.youmai.hxsdk.module.groupchat;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.IMConnectionActivity;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.router.APath;
import com.youmai.hxsdk.utils.GlideRoundTransform;

/**
 * 作者：create by YW
 * 日期：2018.04.18 16:36
 * 描述：
 */
public class ChatDetailsActivity extends SdkBaseActivity {

    public static final String USER_AVATAR = "USER_AVATAR";

    TextView mTvBack, mTvTitle;
    private ImageView mSelfHeader;
    private TextView mSelfName;
    private ImageView mAddMore;
    private RelativeLayout mClearRecords;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_im_chat_details);

        initView();
        setOnClickListener();
    }

    private void initView() {

        String username = getIntent().getStringExtra(IMConnectionActivity.DST_NAME);
        String avatar = getIntent().getStringExtra(USER_AVATAR);
        mTvBack = findViewById(R.id.tv_back);
        mTvTitle = findViewById(R.id.tv_title);

        mSelfName = findViewById(R.id.tv_self_name);
        mSelfHeader = findViewById(R.id.iv_self_header);
        mAddMore = findViewById(R.id.iv_add_more);
        mClearRecords = findViewById(R.id.rl_clear_chat_records);

        mTvTitle.setText("聊天详情");
        mSelfName.setText(username);

        Glide.with(mContext)
                .load(avatar)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .override(120, 120)
                        .transform(new GlideRoundTransform(mContext))
                        .error(R.drawable.color_default_header))
                .into(mSelfHeader);

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
                        .navigation(ChatDetailsActivity.this);
//                Intent intent = new Intent();
//                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                intent.setClass(ChatDetailsActivity.this, AddContactsCreateGroupActivity.class);
//                startActivity(intent);
            }
        });
    }

}
