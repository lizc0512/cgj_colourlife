package com.tg.coloursteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.RedPacketBonusAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.entity.RedPacketEntity;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.TokenUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.RoundImageView;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的饭票
 *
 * @author Administrator
 */
public class RedpacketsBonusMainActivity extends BaseActivity {
    private RelativeLayout rl_submit;//转账提现
    private RelativeLayout rl_check_myfp;//饭票明细
    private RoundImageView rivHead;
    private TextView tvRealName;
    private TextView tv_balance;//饭票余额
    private double balance;
    private RecyclerView recyclerView;
    private RedPacketBonusAdapter redPacketBonusAdapter;
    private RedPacketEntity redPacketEntity;
    private List<RedPacketEntity> redPacketEntityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPopup(true);
        initView();
        initData();
        serachdata();
        setData();
    }

    /**
     * 根据用户获取该用户的红包发放列表情况
     *
     * @param secret
     * @param key
     */
    private void getHBUserList(String key, String secret) {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_BALANCE_INFO);
        RequestParams params = new RequestParams();
        params.put("key", key);
        params.put("secret", secret);
//        HttpTools.httpGet(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/getBalance", config, params);
    }

    /**
     * 请求集体奖金包数据
     */
    private void serachdata() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_USER_JJB);
        String corp_id = Tools.getStringValue(this, Contants.storage.CORPID);
        RequestParams params = new RequestParams();
        params.put("username", UserInfo.employeeAccount);
        params.put("corpid", corp_id);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/jxjjb/userjtjjb/list",
                config, params);
    }

    private void getEmployeeInfo() {
        String pwd = Tools.getPassWord(this);
        RequestConfig config = new RequestConfig(this, HttpTools.SET_EMPLOYEE_INFO, null);
        RequestParams params = new RequestParams();
        params.put("username", UserInfo.employeeAccount);
        try {
            params.put("password", MD5.getMd5Value(pwd).toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        HttpTools.httpPost(Contants.URl.URL_CPMOBILE, "/1.0/employee/login", config, params);
    }

    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.rl_check_myfp:// 饭票明细
                submit();
                break;
            case R.id.rl_submit:// 转账提现
                startActivity(new Intent(this, RedpacketsMainActivity.class));
                break;
        }
        return super.handClickEvent(v);
    }

    /**
     * 请求OA金融平台数据
     */
    private void submit() {
        RequestParams params = new RequestParams();
        params.put("oa_username", UserInfo.employeeAccount);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/newczy/employee/getFinanceByOa",
                new RequestConfig(this, HttpTools.GET_USER_INFO, "获取员工金融信息"), params);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        recyclerView = findViewById(R.id.rv_redpackets_bonus);
        rl_check_myfp = findViewById(R.id.rl_check_myfp);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        rivHead = (RoundImageView) findViewById(R.id.riv_head);
        tvRealName = (TextView) findViewById(R.id.tv_real_Name);
        tv_balance = (TextView) findViewById(R.id.tv_balance);
        rl_submit = (RelativeLayout) findViewById(R.id.rl_submit);
        rl_check_myfp.setOnClickListener(singleListener);
        rl_submit.setOnClickListener(singleListener);
        String jsonStr = Tools.getStringValue(RedpacketsBonusMainActivity.this, Contants.storage.TICKET);
        if (StringUtils.isNotEmpty(jsonStr)) {
            JSONObject jsonObject = HttpTools.getContentJSONObject(jsonStr);
            if (jsonObject != null) {
                try {
                    balance = jsonObject.getDouble("balance");
                    setData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void initData() {
        String str = Contants.Html5.HEAD_ICON_URL + "avatar?uid=" + UserInfo.employeeAccount;
        Glide.with(RedpacketsBonusMainActivity.this).load(str)
                .into(rivHead);
        String json = Tools.getStringValue(RedpacketsBonusMainActivity.this, Contants.storage.JTJJB);
        if (!TextUtils.isEmpty(json)) {
            setJJBData(json);
        }
        Map<String, Object> map = new HashMap<>();
        RequestConfig config = new RequestConfig(this, HttpTools.GET_MYPAGERULE, "");
        Map<String, String> stringMap = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(RedpacketsBonusMainActivity.this, map));
        HttpTools.httpGet_Map(Contants.URl.URL_NEW, "/app/home/utility/fpbutton", config, (HashMap) stringMap);
    }

    private void setJJBData(String jsonString) {
        redPacketEntity = GsonUtils.gsonToBean(jsonString, RedPacketEntity.class);
        if (redPacketEntity.getContent() != null) {
            try {
                redPacketEntityList.add(redPacketEntity);
                if (redPacketBonusAdapter == null) {
                    redPacketBonusAdapter = new RedPacketBonusAdapter(this, redPacketEntityList.get(0).getContent());
                    recyclerView.setAdapter(redPacketBonusAdapter);
                } else {
                    redPacketBonusAdapter.setData(redPacketEntityList.get(0).getContent());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        }
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.GET_USER_INFO) {//请求金融平台数据
            if (code == 0) {
                JSONObject content = HttpTools.getContentJSONObject(jsonString);
                try {
                    String cano = content.getString("cano");
                    String pano = content.getString("pano");
                    String cno = content.getString("cno");
                    Intent intent = new Intent(RedpacketsBonusMainActivity.this, RedpacketsDetailsActivity.class);
                    intent.putExtra(RedpacketsDetailsActivity.PUBLICACCOUNT_CANO, cano);
                    intent.putExtra(RedpacketsDetailsActivity.PUBLICACCOUNT_PANO, pano);
                    intent.putExtra(RedpacketsDetailsActivity.PUBLICACCOUNT_CNO, cno);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                ToastFactory.showToast(RedpacketsBonusMainActivity.this, message);
            }
        } else if (msg.arg1 == HttpTools.GET_USER_JJB) {
            if (code == 0) {
                Tools.saveStringValue(RedpacketsBonusMainActivity.this, Contants.storage.JTJJB, jsonString);
                setJJBData(jsonString);
            } else {
                ToastFactory.showToast(RedpacketsBonusMainActivity.this, message);
            }
        } else if (msg.arg1 == HttpTools.SET_EMPLOYEE_INFO) {
            if (code == 0) {
                JSONObject content = HttpTools.getContentJSONObject(jsonString);
                if (content != null) {
                    try {
                        String key = content.getString("key");
                        String secret = content.getString("secret");
                        //保存key  sectet
                        Tools.saveStringValue(RedpacketsBonusMainActivity.this, Contants.EMPLOYEE_LOGIN.key, key);
                        Tools.saveStringValue(RedpacketsBonusMainActivity.this, Contants.EMPLOYEE_LOGIN.secret, secret);
                        getHBUserList(key, secret);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                ToastFactory.showToast(RedpacketsBonusMainActivity.this, message);
            }
        } else if (msg.arg1 == HttpTools.GET_BALANCE_INFO) {
            if (code == 0) {
                Tools.saveStringValue(RedpacketsBonusMainActivity.this, Contants.storage.TICKET, jsonString);
                JSONObject content = HttpTools.getContentJSONObject(jsonString);
                try {
                    balance = content.getDouble("balance");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setData();
            } else {
                ToastFactory.showToast(RedpacketsBonusMainActivity.this, message);
            }
        } else if (msg.arg1 == HttpTools.GET_MYPAGERULE) {
            if (code == 0) {
                JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
                int show = 0;
                try {
                    show = jsonObject.getInt("show"); // 1：显示按钮，2：不显示按钮
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (show == 1) {
                    rl_submit.setVisibility(View.VISIBLE);
                } else {
                    rl_submit.setVisibility(View.GONE);
                }
            }

        }
    }

    @Override
    public void onFail(Message msg, String hintString) {
        super.onFail(msg, hintString);
    }

    private void setData() {
        DecimalFormat df = new DecimalFormat("0.00");
        if (TextUtils.isEmpty(UserInfo.familyName)) {
            tvRealName.setText(UserInfo.realname + UserInfo.jobName);
        } else {
            tvRealName.setText(UserInfo.realname + UserInfo.jobName + "(" + UserInfo.familyName + ")");
        }
        tv_balance.setText(df.format(balance));
    }

    @Override
    protected void onResume() {
        super.onResume();
        String key = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.key);
        String secret = Tools.getStringValue(this, Contants.EMPLOYEE_LOGIN.secret);
        if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(secret)) {
            getHBUserList(key, secret);
        } else {
            getEmployeeInfo();
        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_redpackets_bonus_main, null);
    }

    @Override
    public String getHeadTitle() {
        return "我的饭票";
    }

}
