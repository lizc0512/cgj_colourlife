package com.youmai.hxsdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.dialog.HxPayErrorDialog;
import com.youmai.hxsdk.dialog.HxPayPasswordDialog;
import com.youmai.hxsdk.dialog.HxRedPacketDialog;
import com.youmai.hxsdk.entity.red.RedPackageList;
import com.youmai.hxsdk.entity.red.ShareRedPackage;
import com.youmai.hxsdk.entity.red.StandardRedPackage;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.utils.GlideRoundTransform;
import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 作者：create by YW
 * 日期：2017.06.07 11:42
 * 描述：Red packet
 */
public class RedPacketActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = RedPacketActivity.class.getSimpleName();

    public static final String FROM_GROUP = "from_group";
    public static final String TARGET_ID = "target_id";
    public static final String TARGET_NAME = "target_name";
    public static final String TARGET_AVATAR = "target_avatar";


    private TextView tv_error;
    private TextView tv_back;
    private TextView tv_title;
    private TextView tv_right;

    private TextView tv_value;

    private ImageView img_head;
    private TextView tv_name;
    private AppCompatEditText et_money;
    private TextView tv_money;
    private AppCompatEditText et_msg;

    private String uuid;
    private String name;
    private String avatar;

    private int moneyMax;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_red_packet);

        uuid = getIntent().getStringExtra(TARGET_ID);
        name = getIntent().getStringExtra(TARGET_NAME);
        avatar = getIntent().getStringExtra(TARGET_AVATAR);

        initView();
        initClick();
        loadRedPacket();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void initView() {
        img_head = (ImageView) findViewById(R.id.img_head);
        int size = getResources().getDimensionPixelOffset(R.dimen.red_head);
        Glide.with(this).load(avatar)
                .apply(new RequestOptions()
                        .transform(new GlideRoundTransform())
                        .override(size, size)
                        .placeholder(R.drawable.color_default_header)
                        .error(R.drawable.color_default_header)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(img_head);

        tv_error = (TextView) findViewById(R.id.tv_error);
        tv_value = (TextView) findViewById(R.id.tv_value);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_name.setText(name);

        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("彩利是");

        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_right.setText("利是记录");
        tv_right.setOnClickListener(this);

        tv_money = (TextView) findViewById(R.id.tv_money);
        et_msg = (AppCompatEditText) findViewById(R.id.et_msg);

        et_money = (AppCompatEditText) findViewById(R.id.et_money);
        et_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = "f  " + s.toString();
                tv_money.setText(content);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.btn_commit).setOnClickListener(this);


    }


    private void initClick() {

    }


    private void loadRedPacket() {
        HuxinSdkManager.instance().reqRedPackageStandardConfig(new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                StandardRedPackage bean = GsonUtil.parse(response, StandardRedPackage.class);

                if (bean != null && bean.isSuccess()) {
                    moneyMax = bean.getContent().getFixedConfig().getMoneyMax();
                }

            }
        });

        HuxinSdkManager.instance().reqRedPackageList(new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RedPackageList bean = GsonUtil.parse(response, RedPackageList.class);

                if (bean != null && bean.isSuccess()) {
                    String balance = bean.getContent().get(0).getBalance();
                    tv_value.setText(balance);
                }
            }
        });


    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_commit) {

            final HxPayPasswordDialog dialog = new HxPayPasswordDialog(this);
            dialog.setOnFinishInput(new HxPayPasswordDialog.OnPasswordInputFinish() {
                @Override
                public void inputFinish() {
                    dialog.dismiss();
                    Intent intent = new Intent();
                    String remark = et_msg.getText().toString().trim();
                    if (TextUtils.isEmpty(remark)) {
                        remark = et_msg.getHint().toString().trim();
                    }
                    intent.putExtra("value", et_money.getText().toString().trim());
                    intent.putExtra("redTitle", remark);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            });
            dialog.show();
        } else if (id == R.id.tv_back) {
            onBackPressed();
        } else if (id == R.id.tv_right) {
            /*HuxinSdkManager.instance().checkPayPwd("666666", new IGetListener() {
                @Override
                public void httpReqResult(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String result = jsonObject.optString("state");
                        if (result.equals("ok")) {

                        }
                    } catch (JSONException e) {

                    }
                }
            });*/
            startActivity(new Intent(this, RedPacketHistoryActivity.class));
        }
    }


    private void test() {
        HuxinSdkManager.instance().reqRedPackageShareConfig(new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                ShareRedPackage bean = GsonUtil.parse(response, ShareRedPackage.class);

                if (bean != null && bean.isSuccess()) {
                    Intent intent = new Intent();
                    String remark = et_msg.getText().toString().trim();
                    if (TextUtils.isEmpty(remark)) {
                        remark = et_msg.getHint().toString().trim();
                    }

                    /*intent.putExtra("value", et_money.getText().toString().trim());
                    intent.putExtra("redTitle", remark);
                    setResult(Activity.RESULT_OK, intent);
                    finish();*/
                }

            }
        });


        HuxinSdkManager.instance().reqSendSingleRedPackage(20, "大吉大利", "", "123456", new IGetListener() {
            @Override
            public void httpReqResult(String response) {

            }
        });

        HuxinSdkManager.instance().checkPayPwd("666666", new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                String test = response;
            }
        });
    }
}
