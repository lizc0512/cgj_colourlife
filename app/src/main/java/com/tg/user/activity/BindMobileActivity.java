package com.tg.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.util.MyTimeCount;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.user.model.UserModel;

/**
 * @name ${lizc}
 * @class name：com.tg.user.activity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/7/5 9:48
 * @change
 * @chang time
 * @class describe 绑定手机号功能
 */
public class BindMobileActivity extends BaseActivity implements View.OnClickListener, HttpResponse {
    public static final String ISFROMUSER = "isfromuser";
    public static final String PHONE = "phone";
    private TextView tv_base_title;
    private ImageView iv_base_back;
    private TextView tv_bindmobile_getmms;
    private ImageView iv_bindmobile_deloa;
    private EditText et_bindmobile_phone;
    private EditText et_bindmobile_mms;
    private Button btn_bindmobile_bind;
    private UserModel userModel;
    private MyTimeCount myTimeCount;
    private Boolean fromHome = false;
    private String phone;
    private TextView tv_user_changephone;
    private TextView tv_user_phone;
    private RelativeLayout rl_show;
    private ConstraintLayout cl_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_mobile);
        userModel = new UserModel(this);
        initView();
        initData();
    }

    private void initView() {
        tv_base_title = findViewById(R.id.tv_base_title);
        cl_data = findViewById(R.id.cl_data);
        rl_show = findViewById(R.id.rl_show);
        tv_user_phone = findViewById(R.id.tv_user_phone);
        tv_user_changephone = findViewById(R.id.tv_user_changephone);
        iv_base_back = findViewById(R.id.iv_base_back);
        tv_bindmobile_getmms = findViewById(R.id.tv_bindmobile_getmms);
        iv_bindmobile_deloa = findViewById(R.id.iv_bindmobile_deloa);
        et_bindmobile_phone = findViewById(R.id.et_bindmobile_phone);
        et_bindmobile_mms = findViewById(R.id.et_bindmobile_mms);
        btn_bindmobile_bind = findViewById(R.id.btn_bindmobile_bind);
        iv_base_back.setOnClickListener(this);
        btn_bindmobile_bind.setOnClickListener(this);
        tv_bindmobile_getmms.setOnClickListener(this);
        iv_bindmobile_deloa.setOnClickListener(this);
        tv_user_changephone.setOnClickListener(this);
        String phone = getIntent().getStringExtra(PHONE);
        tv_user_phone.setText(phone);

        tv_base_title.setText(getString(R.string.user_bindmobile_tv));
        et_bindmobile_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString())) {
                    iv_bindmobile_deloa.setVisibility(View.VISIBLE);
                } else {
                    iv_bindmobile_deloa.setVisibility(View.GONE);
                }
                canBtn();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_bindmobile_mms.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                canBtn();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        if (null != intent) {
            fromHome = intent.getBooleanExtra(ISFROMUSER, false);
        }
    }

    private void canBtn() {
        if (!TextUtils.isEmpty(et_bindmobile_phone.getText().toString().trim()) && !TextUtils.isEmpty(
                et_bindmobile_mms.getText().toString().trim())) {
            btn_bindmobile_bind.setBackgroundResource(R.drawable.bg_login_button_blue);
            btn_bindmobile_bind.setTextColor(getResources().getColor(R.color.white));
        } else {
            btn_bindmobile_bind.setBackgroundResource(R.drawable.bg_login_button_gray);
            btn_bindmobile_bind.setTextColor(getResources().getColor(R.color.line_login_button));
        }
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }

    @Override
    public void onClick(View v) {
        phone = et_bindmobile_phone.getText().toString().trim();
        String mms = et_bindmobile_mms.getText().toString().trim();
        switch (v.getId()) {
            case R.id.iv_base_back:
                if (fromHome) {
                    this.finish();
                } else {
                    DialogFactory.getInstance().showDialog(this, v12 -> {
                        this.finish();
                    }, v1 -> {
                    }, "取消后可在个人中心-手机号进行绑定,是否取消绑定?", "确定", "取消");
                }
                break;
            case R.id.iv_bindmobile_deloa:
                et_bindmobile_phone.getText().clear();
                break;
            case R.id.tv_bindmobile_getmms:
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShortToast(this, "手机号不能为空");
                    return;
                }
                userModel.postSendMms(0, phone, this);
                break;
            case R.id.btn_bindmobile_bind:
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.showShortToast(this, "手机号不能为空");
                    return;
                }
                if (TextUtils.isEmpty(mms)) {
                    ToastUtil.showShortToast(this, "验证码不能为空");
                    return;
                }
                userModel.postBindMobile(1, phone, mms, this);
                break;
            case R.id.tv_user_changephone:
                rl_show.setVisibility(View.GONE);
                cl_data.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    ToastUtil.showShortToast(this, "短信发送成功,请注意查收");
                    initTimeCount();
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    ToastUtil.showShortToast(this, "绑定成功");
                    UserInfo.mobile = phone;
                    startActivity(new Intent(this, UserInfoActivity.class));
                    this.finish();
                }
                break;
        }
    }

    private void initTimeCount() {
        cancelTimeCount();
        tv_bindmobile_getmms.setClickable(false);
        tv_bindmobile_getmms.setTextColor(getResources().getColor(R.color.color_999faa));
        myTimeCount = new MyTimeCount(this, tv_bindmobile_getmms, 60000, 1000);
        myTimeCount.start();
    }

    private void cancelTimeCount() {
        if (myTimeCount != null) {
            myTimeCount.cancel();
            myTimeCount = null;
        }
    }
}
