package com.youmai.hxsdk.module.groupchat;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;

/**
 * 作者：create by YW
 * 日期：2018.04.18 16:36
 * 描述：
 */
public class ChatDetailsActivity extends SdkBaseActivity {

    ImageView mSelfHeader;
    TextView mSelfName;
    ImageView mAddMore;
    RelativeLayout mClearRecords;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_im_chat_details);

        initView();
    }

    private void initView() {
        TextView mTvBack = findViewById(R.id.tv_back);
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView mTvTitle = findViewById(R.id.tv_title);
        mTvTitle.setText("聊天详情");

        mSelfHeader = findViewById(R.id.iv_self_header);
        mSelfName = findViewById(R.id.tv_self_name);
        mAddMore = findViewById(R.id.iv_add_more);
        mClearRecords = findViewById(R.id.rl_clear_chat_records);
    }
}
