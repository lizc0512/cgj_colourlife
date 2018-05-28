package com.tg.coloursteward;

import org.json.JSONException;
import org.json.JSONObject;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 绑定彩之云账号
 *
 * @author Administrator
 */
public class RedpacketsBindCZYMainActivity extends BaseActivity {
    private static final String TAG = "RedpacketsBindCZYMainAc";
    /**
     * 彩之云账号EditText
     */
    private EditText edtCZYAccount;

    /**
     * 下一步
     */
    private RelativeLayout btnNext;

    /**
     * 获取到的对应的彩之云账号信息LinearLayout
     */
    private LinearLayout llUserInfo;

    /**
     * 彩之云姓名
     */
    private TextView tvName;
    private String name;

    /**
     * 彩之云小区名
     */
    private TextView tvCommunity;
    private String community;

    private String userId;

    /**
     * 红包余额
     */
    private Double balance;
    private String key;
    private String secret;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        balance = getIntent().getDoubleExtra(Contants.PARAMETER.BALANCE, 0.00);
        key = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.key);
        secret = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.secret);
        edtCZYAccount = (EditText) findViewById(R.id.edt_czy_account);
        llUserInfo = (LinearLayout) findViewById(R.id.ll_czy_info);
        llUserInfo.setVisibility(View.GONE);

        tvName = (TextView) findViewById(R.id.tv_name);
        tvCommunity = (TextView) findViewById(R.id.tv_community);

        edtCZYAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edtCZYAccount.getText().length() == 11) {
                    // 通过手机号查询用户信息
                    getCustomerInfo(edtCZYAccount.getText().toString());
                } else if (View.VISIBLE == llUserInfo.getVisibility()) {
                    llUserInfo.setVisibility(View.GONE);
                }
            }
        });

        btnNext = (RelativeLayout) findViewById(R.id.btn_next);
        next();
    }

    /**
     * 通过手机号查询用户信息
     */
    private void getCustomerInfo(String mobile) {
        RequestConfig config = new RequestConfig(this, HttpTools.POST_CUSTOMER_INFO);
        RequestParams params = new RequestParams();
        params.put("mobile", mobile);
        params.put("key", key);
        params.put("secret", secret);
        HttpTools.httpPost(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/getCustomerInfo", config, params);
    }

    private void next() {
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int user = llUserInfo.getVisibility();
                Log.e(TAG, "onClick: user" + user);
                int name = tvName.getVisibility();//0可见
                int community = tvCommunity.getVisibility();
                Log.e(TAG, "onClick: " + name + "    " + community);
                if (edtCZYAccount.getText().toString()
                        .length() == 11) {
                    if (user == 0) {
                        Intent intent = new Intent(RedpacketsBindCZYMainActivity.this,
                                RedpacketsBindCZYConfirmActivity.class);
                        String mobile = edtCZYAccount.getText().toString();
                        intent.putExtra(Contants.PARAMETER.MOBILE, mobile);
                        intent.putExtra(Contants.PARAMETER.BALANCE, balance);
                        intent.putExtra(Contants.PARAMETER.USERID, userId);
                        intent.putExtra(Contants.PARAMETER.COMMUNITY, community);
                        startActivity(intent);
                        finish();
                    } else {
                        ToastFactory.showToast(RedpacketsBindCZYMainActivity.this, "暂未获取到账号详情");
                    }

                } else {
                    ToastFactory.showToast(RedpacketsBindCZYMainActivity.this, "请输入正确的彩之云账号");
                }
            }
        });
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.POST_CUSTOMER_INFO) {
            if (code == 0) {
                JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
                try {
                    if (jsonObject.getInt("ok") == 1) {
                        jsonObject = jsonObject.getJSONObject("customerInfo");
                        name = jsonObject.getString("name");
                        community = jsonObject.getString("community");
                        userId = jsonObject.getString("id");
                        if (name.length() > 0 && community.length() > 0 && userId.length() > 0) {
                            llUserInfo.setVisibility(View.VISIBLE);
                            tvName.setText(name);
                            tvCommunity.setText(community);
                            next();

                        } else {
                            ToastFactory.showToast(RedpacketsBindCZYMainActivity.this, "请补全彩之云账号信息");
                        }
                    } else {
                        ToastFactory.showToast(RedpacketsBindCZYMainActivity.this, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                ToastFactory.showToast(RedpacketsBindCZYMainActivity.this, message);
            }
        }
    }

    @Override
    public View getContentView() {
        // TODO Auto-generated method stub
        return getLayoutInflater().inflate(R.layout.activity_redpackets_bind_czymain, null);
    }

    @Override
    public String getHeadTitle() {
        // TODO Auto-generated method stub
        return "绑定彩之云账号";
    }

}
