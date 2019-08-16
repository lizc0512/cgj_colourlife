package com.tg.setting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.ImageUtil;

import static com.tg.setting.activity.KeySendKeyListActivity.COMMUNITY_NAME;
import static com.tg.setting.activity.KeySendKeyListActivity.DOOR_QRCODE;
import static com.tg.setting.activity.KeySendKeyListActivity.KEY_CONTENT;

/**
 * 乐开-二维码发送钥匙
 *
 * @author hxg 2019.07.18
 */
public class KeySendKeyCodeActivity extends BaseActivity {

    private LinearLayout save_pic_layout;
    private TextView tv_community;
    private TextView tv_door_name;
    private ImageView iv_qrcode;
    private Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        String community_name = intent.getStringExtra(COMMUNITY_NAME);
        String door_name = intent.getStringExtra(KEY_CONTENT);
        String qrcode = intent.getStringExtra(DOOR_QRCODE);
        tv_community.setText(community_name);
        tv_door_name.setText(door_name);
        GlideUtils.loadImageView(KeySendKeyCodeActivity.this, qrcode, iv_qrcode);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btn_save.setVisibility(View.VISIBLE);
            }
        }, 500);
    }

    private void initView() {
        save_pic_layout = findViewById(R.id.save_pic_layout);
        tv_community = findViewById(R.id.tv_community);
        tv_door_name = findViewById(R.id.tv_door_name);
        iv_qrcode = findViewById(R.id.iv_qrcode);
        btn_save = findViewById(R.id.btn_save);
        btn_save.setVisibility(View.GONE);
        btn_save.setOnClickListener(singleListener);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_key_send_key_qrcode, null);
    }

    @Override
    public String getHeadTitle() {
        return "发送钥匙";
    }

    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                ImageUtil.viewSaveToImage(KeySendKeyCodeActivity.this, save_pic_layout);
                setResult(200);
                finish();
                break;
        }
        return super.handClickEvent(v);
    }

}
