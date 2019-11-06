package com.tg.coloursteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dashuview.library.keep.Cqb_PayUtil;
import com.dashuview.library.keep.ListenerUtils;
import com.dashuview.library.keep.MyListener;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.entity.TinyFragmentTopEntity;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.model.MineModel;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.NumberUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.RoundImageView;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.tg.money.activity.InstantDistributionActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.tg.coloursteward.module.MainActivity.getEnvironment;
import static com.tg.coloursteward.module.MainActivity.getPublicParams;

/**
 * 即时分配
 */
public class AccountActivity extends BaseActivity implements MyListener, HttpResponse {
    private RoundImageView rivHead;
    private RelativeLayout rl_submit;
    private RelativeLayout rl_public;
    private TextView tvRealName;
    private TextView tv_balance;
    private TextView tv_dgzh;
    private String account;
    private String accessToken;
    private AuthAppService authAppService;//2.0授权
    private RelativeLayout rlNextBalance;
    private RelativeLayout rlNextdgzh;
    private List<TinyFragmentTopEntity.ContentBean> list_top = new ArrayList<>();
    private String jsfpNum;
    private MineModel mineModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListenerUtils.setCallBack(this);
        mineModel = new MineModel(this);
        initView();
        initData();
    }

    private void initView() {
        rlNextBalance = findViewById(R.id.rl_next_balance);
        rlNextdgzh = findViewById(R.id.rl_next_dgzh);
        accessToken = Tools.getStringValue(AccountActivity.this, Contants.storage.APPAUTH);
        rivHead = findViewById(R.id.riv_head);
        tvRealName = findViewById(R.id.tv_real_Name);
        tv_balance = findViewById(R.id.tv_balance);
        tv_dgzh = findViewById(R.id.tv_dgzh);
        rl_submit = findViewById(R.id.rl_submit);
        rl_public = findViewById(R.id.rl_public);
        rl_submit.setOnClickListener(v -> Cqb_PayUtil.getInstance(AccountActivity.this).ToJSFP(getPublicParams(), getEnvironment(), "jsfp"));
        /**
         * 及时分配详情
         */
        rlNextBalance.setOnClickListener(v ->
                startActivity(new Intent(this, InstantDistributionActivity.class)));
        rl_public.setOnClickListener(v -> startActivity(new Intent(AccountActivity.this, PublicAccountActivity.class)));
        rlNextdgzh.setOnClickListener(v -> startActivity(new Intent(AccountActivity.this, PublicAccountActivity.class)));
    }

    public void initData() {
        String str = Contants.Html5.HEAD_ICON_URL + "/avatar?uid=" + UserInfo.employeeAccount;
        GlideUtils.loadImageDefaultDisplay(this, str, rivHead, R.drawable.placeholder2, R.drawable.placeholder2);
        String jsfp = spUtils.getStringData(SpConstants.storage.JSFPNUM, "");
        if (!TextUtils.isEmpty(jsfp)) {
            tv_balance.setText(NumberUtils.format(jsfp));
        }
        String dgzhNum = spUtils.getStringData(SpConstants.storage.DGZH_ACCOUNT, "");
        if (!TextUtils.isEmpty(dgzhNum)) {
            tv_dgzh.setText(dgzhNum);
        }
        if (!TextUtils.isEmpty(UserInfo.jobName)) {
            tvRealName.setText(UserInfo.realname + "(" + UserInfo.jobName + ")");
        } else {
            tvRealName.setText(UserInfo.realname);
        }
    }

    /**
     * 获取对公账户金额
     */
    private void getDgzhInfo() {
        mineModel.postDgzhNumData(1, UserInfo.employeeAccount, accessToken, this);
    }

    /**
     * 获取即时分配金额
     */
    private void initDataTop() {
        mineModel.getJsfpNumData(0, accessToken, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String expireTime = Tools.getStringValue(AccountActivity.this, Contants.storage.APPAUTHTIME);
        Date dt = new Date();
        Long time = dt.getTime();
        /**
         * 获取数据
         */
        if (StringUtils.isNotEmpty(expireTime)) {
            if (Long.parseLong(expireTime) * 1000 <= time) {//token过期
                getAuthAppInfo();
            } else {
                getDgzhInfo();
                initDataTop();
            }
        } else {
            getAuthAppInfo();
        }

    }

    /**
     * 获取token(2.0)
     * sectet
     */
    private void getAuthAppInfo() {
        if (authAppService == null) {
            authAppService = new AuthAppService(AccountActivity.this);
        }
        authAppService.getAppAuth(new GetTwoRecordListener<String, String>() {
            @Override
            public void onFinish(String jsonString, String data2, String data3) {
                int code = HttpTools.getCode(jsonString);
                if (code == 0) {
                    JSONObject content = HttpTools.getContentJSONObject(jsonString);
                    if (content.length() > 0) {
                        try {
                            accessToken = content.getString("accessToken");
                            String expireTime = content.getString("expireTime");
                            Tools.saveStringValue(AccountActivity.this, Contants.storage.APPAUTH, accessToken);
                            Tools.saveStringValue(AccountActivity.this, Contants.storage.APPAUTHTIME, expireTime);
                            getDgzhInfo();
                            initDataTop();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }

            @Override
            public void onFailed(String Message) {

            }
        });
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_account, null);
    }

    @Override
    public String getHeadTitle() {
        return "即时分配";
    }

    @Override
    public void authenticationFeedback(String s, int i) {
        switch (i) {
            case 21:
                ToastFactory.showToast(AccountActivity.this, s);
                break;
        }
    }

    @Override
    public void toCFRS(String s) {

    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    list_top.clear();
                    try {
                        TinyFragmentTopEntity entity = GsonUtils.gsonToBean(result, TinyFragmentTopEntity.class);
                        list_top.addAll(entity.getContent());
                        if (null != list_top && list_top.size() > 0) {
                            for (int i = 0; i < list_top.size(); i++) {
                                if (list_top.get(i).getTitle().contains("即时分配")) {
                                    jsfpNum = list_top.get(i).getQuantity();
                                    if (!TextUtils.isEmpty(jsfpNum)) {
                                        tv_balance.setText(NumberUtils.format(jsfpNum));
                                        spUtils.saveStringData(SpConstants.storage.JSFPNUM, jsfpNum);
                                    } else {
                                        tv_balance.setText("0.00");
                                    }
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    JSONObject jsonObject = HttpTools.getContentJSONObject(result);
                    try {
                        if (jsonObject != null) {
                            account = jsonObject.getString("money");
                            if (!TextUtils.isEmpty(account)) {
                                String acc = NumberUtils.format(account);
                                tv_dgzh.setText(acc);
                                spUtils.saveStringData(SpConstants.storage.DGZH_ACCOUNT, acc);
                            } else {
                                tv_dgzh.setText("0.00");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}
