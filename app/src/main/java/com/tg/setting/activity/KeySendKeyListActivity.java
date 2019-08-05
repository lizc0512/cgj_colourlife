package com.tg.setting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.util.ToastUtil;

/**
 * 乐开-发送钥匙列表
 *
 * @author hxg 2019.07.18
 */
public class KeySendKeyListActivity extends BaseActivity {
    public static final String DOOR_ID = "door_id";
    public static final String COMMUNITY_UUID = "commnunity_uuid";
    public static final String KEY_NAME = "key_name";

    private RelativeLayout rl_phone;
    private RelativeLayout rl_code;
    private long doorId;
    private String communityUuid;
    private String keyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rl_phone = findViewById(R.id.rl_phone);
        rl_code = findViewById(R.id.rl_code);

        rl_phone.setOnClickListener(singleListener);
        rl_code.setOnClickListener(singleListener);
        doorId = getIntent().getLongExtra(DOOR_ID, 0);
        communityUuid = getIntent().getStringExtra(COMMUNITY_UUID);
        keyName = getIntent().getStringExtra(KEY_NAME);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_key_send_key_list, null);
    }

    @Override
    public String getHeadTitle() {
        return "发送钥匙";
    }

    @Override
    protected boolean handClickEvent(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.rl_phone:
                i = new Intent(this, KeySendKeyPhoneActivity.class);
                i.putExtra(KeySendKeyPhoneActivity.DOOR_ID, doorId);
                i.putExtra(KeySendKeyPhoneActivity.COMMUNITY_UUID, communityUuid);
                i.putExtra(KeySendKeyPhoneActivity.KEY_NAME, keyName);
                startActivityForResult(i, 1);
                break;
            case R.id.rl_code:
                ToastUtil.showShortToast(this, "开发中，敬请期待...");
//                i = new Intent(this, KeySendKeyCodeActivity.class);
//                i.putExtra(KeySendKeyCodeActivity.DOOR_ID, doorId);
//                i.putExtra(KeySendKeyCodeActivity.COMMUNITY_UUID, communityUuid);
//                startActivity(i);
                break;
        }
        return super.handClickEvent(v);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (1 == requestCode) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
