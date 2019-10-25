package com.tg.setting.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.intelspace.library.ErrorConstants;
import com.intelspace.library.api.OnAddCardCallback;
import com.intelspace.library.module.Device;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.MyProgressDialog;
import com.tg.setting.entity.KeyIdentityEntity;
import com.tg.setting.model.SendCardModel;
import com.tg.setting.service.LekaiService;
import com.tg.setting.view.KeyChoiceRoomDialog;
import com.tg.setting.view.KeyStringPopWindowView;
import com.tg.user.model.UserModel;
import com.youmai.hxsdk.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

import static com.tg.setting.activity.KeySendKeyListActivity.COMMUNITY_UUID;

/**
 * 发卡器将人关联到具体的人
 */
public class CardSenderPhoneActivity extends BaseActivity implements HttpResponse, View.OnClickListener {

    public static final String HAIRPINID = "hairpinId";
    public static final String DEVICE = "device";
    public static final String CARDNUMBER = "cardnumber";
    public static final String DEVICEKEYS = "devicekeys";
    public static final String CARDWRITEKEYS = "cardwritekeys";

    private LinearLayout ll_identity;
    private TextView tv_identity;
    private EditText et_phone;
    private LinearLayout ll_room;
    private TextView tv_room;
    private TextView tv_send;
    private String communityUuid = "";
    private String hairpinId = "";
    private UserModel userModel;
    private List<KeyIdentityEntity.ContentBean> identityList = new ArrayList<>();
    private List<String> identityStringList = new ArrayList<>();
    private String identityId = "";
    private int cardNumber;
    private Device mDevice;
    private ArrayList<String> choiceKeysList;
    private ArrayList<String> writeKeysList;
    private int isPower = 1;
    private MyProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, LekaiService.class);
        startService(intent);
        bindService(intent, mConn, Context.BIND_AUTO_CREATE);
        userModel = new UserModel(this);
        initView();
        initData();
    }

    private LekaiService mLekaiService;
    private ServiceConnection mConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LekaiService.LocalBinder binder = (LekaiService.LocalBinder) service;
            mLekaiService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLekaiService = null;
        }
    };

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_card_send_key_phone, null);
    }

    @Override
    public String getHeadTitle() {
        return "发送钥匙";
    }

    private void initView() {
        ll_identity = findViewById(R.id.ll_identity);
        tv_identity = findViewById(R.id.tv_identity);
        et_phone = findViewById(R.id.et_phone);
        ll_room = findViewById(R.id.ll_room);
        tv_room = findViewById(R.id.tv_room);
        tv_send = findViewById(R.id.tv_send);
        ll_identity.setOnClickListener(this);
        ll_room.setOnClickListener(this);
        tv_send.setOnClickListener(this);
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sendKeyStatus();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        Intent intent = getIntent();
        communityUuid = intent.getStringExtra(COMMUNITY_UUID);
        choiceKeysList = intent.getStringArrayListExtra(DEVICEKEYS);
        writeKeysList = intent.getStringArrayListExtra(CARDWRITEKEYS);
        mDevice = intent.getParcelableExtra(DEVICE);
        hairpinId = intent.getStringExtra(HAIRPINID);
        cardNumber = intent.getIntExtra(CARDNUMBER, 0);
        userModel.getIdentity(1, communityUuid, this);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        KeyIdentityEntity entity = GsonUtils.gsonToBean(result, KeyIdentityEntity.class);
                        identityList.addAll(entity.getContent());
                        for (KeyIdentityEntity.ContentBean bean : identityList) {
                            identityStringList.add(bean.getIdentityName());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                if (TextUtils.isEmpty(result)) {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                } else {
                    if (writeKeysList.size() > 0) {
                        String[] deviceIds = new String[writeKeysList.size()];
                        writeKeysList.toArray(deviceIds);
                        mLekaiService.addCard(mDevice, deviceIds, new OnAddCardCallback() {
                            @Override
                            public void onAddCardCallback(final int status, final String message) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mProgressDialog != null) {
                                            mProgressDialog.dismiss();
                                        }
                                        if (status == ErrorConstants.SUCCESS) {
                                            ToastUtil.showLongToastCenter(CardSenderPhoneActivity.this, "发卡成功");
                                            setResult(200);
                                            finish();
                                        } else {
                                            ToastUtil.showLongToastCenter(CardSenderPhoneActivity.this, message+"["+status+"]");
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        ToastUtil.showLongToastCenter(CardSenderPhoneActivity.this, "发卡成功");
                        setResult(200);
                        finish();
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_identity:
                DisplayUtil.showInput(false, this);
                KeyStringPopWindowView areaPop = new KeyStringPopWindowView(this, identityStringList);
                areaPop.showPopupWindow(contentLayout);
                areaPop.setOnDismissListener(new PopupDismissListener());
                break;
            case R.id.ll_room:
                if (!TextUtils.isEmpty(communityUuid)) {
                    KeyChoiceRoomDialog keyChoiceRoomDialog = new KeyChoiceRoomDialog(CardSenderPhoneActivity.this, communityUuid);
                    keyChoiceRoomDialog.show();
                }
                break;
            case R.id.tv_send:
                String phone = et_phone.getText().toString().trim();
                String room = tv_room.getText().toString().trim();
                mProgressDialog = new MyProgressDialog(this, "正在发卡...");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                SendCardModel sendCardModel = new SendCardModel(CardSenderPhoneActivity.this);
                StringBuffer stringBuffer = new StringBuffer();
                for (int j = 0; j < choiceKeysList.size(); j++) {
                    stringBuffer.append(choiceKeysList.get(j));
                    stringBuffer.append(",");
                }
                String accStr = stringBuffer.toString();
                accStr = accStr.substring(0, accStr.length() - 1);
                sendCardModel.addCgjAccessCard(2, communityUuid, cardNumber, identityId, room, phone, accStr, hairpinId, CardSenderPhoneActivity.this);
                break;
        }
    }

    public void setIdentity(int position) {
        try {
            tv_identity.setText(identityList.get(position).getIdentityName());
            identityId = identityList.get(position).getId();
            isPower = identityList.get(position).getIsPower();
            if (0 == isPower) {//0为业主 1为管理
                ll_room.setVisibility(View.VISIBLE);
            } else {
                ll_room.setVisibility(View.GONE);
            }
            sendKeyStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setRoom(String roomName) {
        tv_room.setText(roomName);
        sendKeyStatus();
    }

    private void sendKeyStatus() {
        String phone = et_phone.getText().toString().trim();
        String room = tv_room.getText().toString().trim();
        String identifyName = tv_identity.getText().toString().trim();
        if (isPower == 0) {
            if (TextUtils.isEmpty(room) || TextUtils.isEmpty(identifyName) || TextUtils.isEmpty(phone) || 11 != phone.length()) {
                tv_send.setEnabled(false);
                tv_send.setBackgroundResource(R.drawable.shape_key_submit_text);
            } else {
                tv_send.setEnabled(true);
                tv_send.setBackgroundResource(R.drawable.shape_key_submit_blue_text);
            }
        } else {
            if (TextUtils.isEmpty(identifyName) || TextUtils.isEmpty(phone) || 11 != phone.length()) {
                tv_send.setEnabled(false);
                tv_send.setBackgroundResource(R.drawable.shape_key_submit_text);
            } else {
                tv_send.setEnabled(true);
                tv_send.setBackgroundResource(R.drawable.shape_key_submit_blue_text);
            }
        }
    }

    class PopupDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 1.0f;
            getWindow().setAttributes(lp);
        }
    }
}
