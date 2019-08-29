package com.tg.setting.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.setting.model.KeyDoorModel;

import static com.tg.setting.activity.KeyAddDoorActivity.COMMUNITY_UUID;

/**
 * 门禁信息的详情和相关操作的入口
 *
 * @author hxg 2019.07.18
 */
public class KeyDoorInforDetailsActivity extends BaseActivity implements HttpResponse {

    public static final String DOOR_MAC = "door_mac";
    public static final String DOOR_INSTALL_TIME = "door_install_time";
    public static final String DOOR_MODEL = "door_model";
    public static final String DOOR_PERSON = "door_person";
    public static final String DOOR_PROTOCOL_VERSION = "door_protocol_version";
    public static final String DOOR_CIPHERID = "door_cipherid";

    private TextView tv_send;
    private TextView tv_door;
    private TextView tv_door_status;
    private TextView tv_door_person;
    private TextView tv_model;
    private TextView tv_mac;
    private TextView tv_install_time;
    private TextView tv_key_person;

    private RelativeLayout door_key_layout;
    private RelativeLayout enter_out_layout;
    private RelativeLayout change_door_layout;
    private RelativeLayout key_bindagain_layout;
    private RelativeLayout key_infor_layout;

    private String accessId;
    private String deviceId;
    private String accessName;
    private String communityUuid;
    private String communityName;
    private String doorMac;
    private String doorInstallTime;
    private String doorModel;
    private String doorPerson;
    private String doorProtocolVersion;
    private String doorCipherid;

    private KeyDoorModel keyDoorModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        tv_send = findViewById(R.id.tv_send);
        tv_door = findViewById(R.id.tv_door);
        tv_door_status = findViewById(R.id.tv_door_status);
        tv_door_person = findViewById(R.id.tv_door_person);
        tv_model = findViewById(R.id.tv_model);
        tv_mac = findViewById(R.id.tv_mac);
        tv_install_time = findViewById(R.id.tv_install_time);
        tv_key_person = findViewById(R.id.tv_key_person);


        door_key_layout = findViewById(R.id.door_key_layout);
        enter_out_layout = findViewById(R.id.enter_out_layout);
        change_door_layout = findViewById(R.id.change_door_layout);
        key_bindagain_layout = findViewById(R.id.key_bindagain_layout);
        key_infor_layout = findViewById(R.id.key_infor_layout);

        tv_send.setOnClickListener(singleListener);
        door_key_layout.setOnClickListener(singleListener);
        enter_out_layout.setOnClickListener(singleListener);
        change_door_layout.setOnClickListener(singleListener);
        key_bindagain_layout.setOnClickListener(singleListener);
        key_infor_layout.setOnClickListener(singleListener);

    }

    private void initData() {
        Intent intent = getIntent();
        accessId = intent.getStringExtra(KeySendKeyListActivity.DOOR_ID);
        deviceId = intent.getStringExtra(KeySendKeyListActivity.DEVICE_ID);
        accessName = intent.getStringExtra(KeySendKeyListActivity.KEY_CONTENT);
        communityUuid = intent.getStringExtra(KeySendKeyListActivity.COMMUNITY_UUID);
        communityName = intent.getStringExtra(KeySendKeyListActivity.COMMUNITY_NAME);
        doorMac = intent.getStringExtra(DOOR_MAC);
        doorInstallTime = intent.getStringExtra(DOOR_INSTALL_TIME);
        doorModel = intent.getStringExtra(DOOR_MODEL);
        doorPerson = intent.getStringExtra(DOOR_PERSON);
        doorProtocolVersion = intent.getStringExtra(DOOR_PROTOCOL_VERSION);
        doorCipherid = intent.getStringExtra(DOOR_CIPHERID);
        tv_door.setText(accessName);
        tv_model.setText(doorModel);
        tv_mac.setText(doorMac);
        tv_install_time.setText(doorInstallTime);
        tv_door_person.setText("钥匙数" + doorPerson);
        tv_key_person.setText(doorPerson);

        keyDoorModel = new KeyDoorModel(KeyDoorInforDetailsActivity.this);
    }


    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.tv_send:
                Intent i = new Intent(this, KeySendKeyListActivity.class);
                i.putExtra(KeySendKeyListActivity.COMMUNITY_UUID, communityUuid);
                i.putExtra(KeySendKeyListActivity.COMMUNITY_NAME, communityName);
                i.putExtra(KeySendKeyListActivity.FORM_SOURCE, 0);
                i.putExtra(KeySendKeyListActivity.DOOR_ID, accessId);
                i.putExtra(KeySendKeyListActivity.KEY_CONTENT, accessName);
                startActivityForResult(i, 1);
                break;
            case R.id.door_key_layout:
                Intent intent = new Intent(KeyDoorInforDetailsActivity.this, KeyDoorUserListActivity.class);
                intent.putExtra(KeySendKeyListActivity.DOOR_ID, accessId);
                intent.putExtra(KeySendKeyListActivity.DEVICE_ID, deviceId);
                intent.putExtra(KeySendKeyListActivity.KEY_CONTENT, accessName);
                intent.putExtra(KeySendKeyListActivity.FORM_SOURCE, 0);
                intent.putExtra(KeySendKeyListActivity.COMMUNITY_UUID, communityUuid);
                intent.putExtra(KeySendKeyListActivity.COMMUNITY_NAME, communityName);
                startActivity(intent);
                break;
            case R.id.enter_out_layout:
                Intent outIntent = new Intent(KeyDoorInforDetailsActivity.this, KeyDoorOutRecordActivity.class);
                outIntent.putExtra(KeySendKeyListActivity.DOOR_ID, accessId);
                outIntent.putExtra(KeySendKeyListActivity.DEVICE_ID, deviceId);
                outIntent.putExtra(KeySendKeyListActivity.KEY_CONTENT, accessName);
                startActivity(outIntent);
                break;
            case R.id.change_door_layout:
                DialogFactory.getInstance().showDoorDialog(KeyDoorInforDetailsActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        keyDoorModel.reflushDoorOperate(0, accessId, doorMac, doorCipherid, doorModel, doorProtocolVersion, KeyDoorInforDetailsActivity.this::OnHttpResponse);
                    }
                }, null, 1, "换锁前需将门禁设备进行提前处理，否则已发放钥匙将无法正常开锁。是否确认更换门禁？", null, null);

                break;
            case R.id.key_bindagain_layout:
                DialogFactory.getInstance().showDoorDialog(KeyDoorInforDetailsActivity.this, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //调用解绑的接口
                                keyDoorModel.delDoorOperate(1, accessId, KeyDoorInforDetailsActivity.this::OnHttpResponse);
                            }
                        }, null, 1, "当前门禁正常，解除绑定后才能重新绑定新的门禁，是否解绑当前门禁？（重置门禁会删除已发送的钥匙）",
                        null, null);
                break;
            case R.id.key_infor_layout:
                Intent inforIntent = new Intent(KeyDoorInforDetailsActivity.this, KeyAddDoorActivity.class);
                inforIntent.putExtra(COMMUNITY_UUID, communityUuid);
                startActivity(inforIntent);
                break;
        }
        return super.handClickEvent(v);

    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_key_door_details, null);
    }

    @Override
    public String getHeadTitle() {
        return "门禁详情";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (1 == requestCode) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        }
    }


    @Override
    public void OnHttpResponse(int what, String result) {

        switch (what) {
            case 0:
                Intent intent = new Intent(KeyDoorInforDetailsActivity.this, KeyDoorManagerActivity.class);
                startActivity(intent);
                break;
            case 1:
                setResult(Activity.RESULT_OK);
                finish();
                break;
        }

    }
}
