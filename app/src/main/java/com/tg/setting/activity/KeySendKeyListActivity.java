package com.tg.setting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;

/**
 * 乐开-发送钥匙列表
 *
 * @author hxg 2019.07.18
 */
public class KeySendKeyListActivity extends BaseActivity {
    public static String COMMUNITY_UUID = "communityUuid";
    public static String COMMUNITY_NAME = "community_name";
    public static final String DOOR_ID = "door_id";
    public static final String DEVICE_ID = "device_id";
    public static final String DOOR_QRCODE = "door_qrcode";
    public static final String KEY_CONTENT = "key_content";
    public static final String DOOR_ROOM= "door_room";
    public static final String DOOR_IDENTITY_ID= "door_identity_id";
    public static final String DOOR_IDENTITY_NAME= "door_identity_name";
    public static final String DOOR_USER_NAME= "door_user_name";
    public static final String FORM_SOURCE = "form_source";

    private RelativeLayout rl_phone;
    private RelativeLayout rl_code;
    private String doorId;
    private String communityUuid;
    private String communityName;
    private String keyName;
    private int formSource=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rl_phone = findViewById(R.id.rl_phone);
        rl_code = findViewById(R.id.rl_code);

        rl_phone.setOnClickListener(singleListener);
        rl_code.setOnClickListener(singleListener);
        Intent intent = getIntent();
        doorId = intent.getStringExtra(DOOR_ID);
        communityUuid = intent.getStringExtra(COMMUNITY_UUID);
        communityName = intent.getStringExtra(COMMUNITY_NAME);
        keyName = intent.getStringExtra(KEY_CONTENT);
        formSource = intent.getIntExtra(FORM_SOURCE,0);
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
                i.putExtra(KeySendKeyListActivity.DOOR_ID, doorId);
                i.putExtra(KeySendKeyListActivity.COMMUNITY_UUID, communityUuid);
                i.putExtra(KeySendKeyListActivity.COMMUNITY_NAME, communityName);
                i.putExtra(KeySendKeyListActivity.KEY_CONTENT, keyName);
                i.putExtra(KeySendKeyListActivity.FORM_SOURCE, formSource);
                startActivityForResult(i, 1);
                break;
            case R.id.rl_code:
                i = new Intent(this, KeySendKeyQrCodeActivity.class);
                i.putExtra(KeySendKeyListActivity.DOOR_ID, doorId);
                i.putExtra(KeySendKeyListActivity.COMMUNITY_UUID, communityUuid);
                i.putExtra(KeySendKeyListActivity.KEY_CONTENT, keyName);
                i.putExtra(KeySendKeyListActivity.COMMUNITY_NAME, communityName);
                i.putExtra(KeySendKeyListActivity.FORM_SOURCE, formSource);
                startActivityForResult(i, 1);
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
