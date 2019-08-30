package com.tg.setting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.setting.model.KeyDoorModel;
import com.tg.setting.view.KeyUnitPopWindowView;
import com.tg.user.model.UserModel;
import com.youmai.hxsdk.utils.DisplayUtil;

/**
 * 乐开-新建门禁
 *
 * @author hxg 2019.07.22
 */
public class KeyAddDoorActivity extends BaseActivity implements HttpResponse {
    public static String COMMUNITY_UUID = "community_uuid";
    private EditText et_door;
    private ImageView iv_big_door;
    private ImageView iv_unit_door;
    private EditText et_address;
    private LinearLayout ll_address;
    private EditText et_des;
    private TextView tv_submit;

    private int bigDoor = 1;
    private int unitDoor = 0;
    private String communityUuid = "";
    private int fromSource;
    private String accessId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        et_door = findViewById(R.id.et_door);
        iv_big_door = findViewById(R.id.iv_big_door);
        iv_unit_door = findViewById(R.id.iv_unit_door);
        et_address = findViewById(R.id.et_address);
        ll_address = findViewById(R.id.ll_address);
        et_des = findViewById(R.id.et_des);
        tv_submit = findViewById(R.id.tv_submit);

        iv_big_door.setOnClickListener(singleListener);
        iv_unit_door.setOnClickListener(singleListener);
        et_address.setOnClickListener(singleListener);

        et_address.setFocusableInTouchMode(false);
    }

    private void initData() {
        Intent intent = getIntent();
        communityUuid = intent.getStringExtra(COMMUNITY_UUID);

        fromSource = intent.getIntExtra(KeySendKeyListActivity.FORM_SOURCE, 0);
        if (fromSource == 1) {
            accessId = intent.getStringExtra(KeySendKeyListActivity.DOOR_ID);
            headView.setTitle("修改门禁");
        }
    }

    private void initListener() {
        et_door.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setSubmitBg();
            }
        });
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_key_add_door, null);
    }

    @Override
    public String getHeadTitle() {
        return "新建门禁";
    }

    @Override
    protected boolean handClickEvent(View v) {

        switch (v.getId()) {
            case R.id.iv_big_door:
                DisplayUtil.showInput(false, this);
                ll_address.setVisibility(View.GONE);
                bigDoor = 1;
                iv_big_door.setImageResource(R.drawable.ic_key_check_box_select);
                if (1 == unitDoor) {
                    unitDoor = 0;
                    iv_unit_door.setImageResource(R.drawable.ic_key_check_box);
                }
                setSubmitBg();
                break;
            case R.id.iv_unit_door:
                DisplayUtil.showInput(false, this);
                ll_address.setVisibility(View.VISIBLE);
                unitDoor = 1;
                iv_unit_door.setImageResource(R.drawable.ic_key_check_box_select);
                if (1 == bigDoor) {
                    bigDoor = 0;
                    iv_big_door.setImageResource(R.drawable.ic_key_check_box);
                }
                setSubmitBg();
                break;
            case R.id.et_address:
                DisplayUtil.showInput(false, this);

                KeyUnitPopWindowView popWindowView = new KeyUnitPopWindowView(this, communityUuid, true);
                popWindowView.setOnDismissListener(new PopupDismissListener());
                popWindowView.showPopupWindow(contentLayout);
                break;
            case R.id.tv_submit:
                DisplayUtil.showInput(false, this);
                if (setSubmitBg()) {
                    if (fromSource == 0) {
                        UserModel userModel = new UserModel(this);
                        userModel.addDoor(1, communityUuid, et_door.getText().toString().trim(),
                                0 == unitDoor ? communityUuid : unitUuid, unitDoor + "", unitName, buildUuid, buildName, et_address.getText().toString().trim(), et_des.getText().toString().trim(), this);
                    } else {
                        KeyDoorModel keyDoorModel = new KeyDoorModel(this);
                        keyDoorModel.upadteDoorInfor(1, accessId, communityUuid, et_door.getText().toString().trim(),
                                0 == unitDoor ? communityUuid : unitUuid, unitDoor + "", unitName, buildUuid, buildName, et_address.getText().toString().trim(), et_des.getText().toString().trim(), this);
                    }

                }
                break;
        }
        return super.handClickEvent(v);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    if (fromSource == 1) {
                        ToastUtil.showShortToast(this, "门禁修改成功");
                    } else {
                        ToastUtil.showShortToast(this, "新建门禁成功");
                    }
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
    }

    private String buildUuid = "";
    private String buildName = "";
    private String unitName = "";
    private String unitUuid = "";

    public void setUnit(String buildUuid, String buildName, String unitName, String unitUuid) {
        et_address.setText(buildName + unitName);
        this.buildUuid = buildUuid;
        this.buildName = buildName;
        this.unitName = unitName;
        this.unitUuid = unitUuid;
    }

    private boolean setSubmitBg() {
        if ((0 == unitDoor && !TextUtils.isEmpty(et_door.getText().toString().trim()))
                || (1 == unitDoor && !TextUtils.isEmpty(et_door.getText().toString().trim())
                && !TextUtils.isEmpty(et_address.getText().toString().trim()))) {
            tv_submit.setBackgroundResource(R.drawable.shape_key_submit_blue_text);
            tv_submit.setOnClickListener(singleListener);
            return true;
        }
        tv_submit.setBackgroundResource(R.drawable.shape_key_submit_text);
        tv_submit.setOnClickListener(null);
        return false;
    }

    class PopupDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 1.0f;
            getWindow().setAttributes(lp);

            setSubmitBg();
        }

    }

}