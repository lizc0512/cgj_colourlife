package com.tg.coloursteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.RedpacketsInfo;
import com.tg.coloursteward.model.BonusModel;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 兑换给同事（对公账户进）
 *
 * @author Administrator
 */
public class RedpacketsDGZHShareMainMainActivity extends BaseActivity implements HttpResponse {
    private EditText edtColleagueInfo;
    private RelativeLayout rlSubmit;
    private String colleagueInfo;
    private Double balance;
    private ImageButton btnNext;

    private Intent intent;
    final ArrayList<RedpacketsInfo> list = new ArrayList<RedpacketsInfo>();

    private String money;//对公账户余额
    private int pay_atid;//支付方类型
    private String pay_ano;//支付方账户

    private String transferNameOA_ONE;
    private String transferName_ONE;
    private String transferMobile_ONE;
    private BonusModel bonusModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bonusModel = new BonusModel(this);
        colleagueInfo = getIntent().getStringExtra(Contants.PARAMETER.OA);
        balance = getIntent().getDoubleExtra(Contants.PARAMETER.BALANCE, 0.00);
        money = getIntent().getStringExtra(Contants.PARAMETER.PUBLIC_ACCOUNT);

        pay_atid = getIntent().getIntExtra(Contants.PARAMETER.PAY_ATID, -1);
        pay_ano = getIntent().getStringExtra(Contants.PARAMETER.PAY_ANO);
        initView();
        if (StringUtils.isNotEmpty(colleagueInfo)) {
            edtColleagueInfo.setText(colleagueInfo);
            check();
        }
    }

    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.imgbtn_contact://通讯录
                intent = new Intent(RedpacketsDGZHShareMainMainActivity.this, RedpacketsContactsActivity.class);
                startActivityForResult(intent, 1001);
                break;
            case R.id.rl_submit://下一步
                check();
                break;
        }
        return super.handClickEvent(v);
    }

    private void initView() {
        edtColleagueInfo = (EditText) findViewById(R.id.edt_colleague_info);
        rlSubmit = (RelativeLayout) findViewById(R.id.rl_submit);
        btnNext = (ImageButton) findViewById(R.id.imgbtn_contact);
        rlSubmit.setOnClickListener(singleListener);
        btnNext.setOnClickListener(singleListener);

        edtColleagueInfo.addTextChangedListener(new TextWatcher() {

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
                if (!TextUtils.isEmpty(s.toString())) {
                    edtColleagueInfo.setSelection(s.length());
                }
            }
        });
    }

    private void getEmployeeInfo(String username) {
        bonusModel.getContactSearch(0, username, 20, this);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    JSONArray content = HttpTools.getContentJsonArray(result);
                    if (content != null) {
                        ResponseData data = HttpTools.getResponseContent(content);
                        if (data.length > 0) {
                            RedpacketsInfo info;
                            for (int i = 0; i < data.length; i++) {
                                info = new RedpacketsInfo();
                                String job = data.getString(i, "jobType");
                                String part = data.getString(i, "orgName");
                                String name = data.getString(i, "name");
                                String buff = name + "--" + job + "(" + part + ")";
                                info.receiver_id = data.getString(i, "czyId");
                                info.receiverName = buff;
                                info.receiverOA = data.getString(i, "username");
                                info.receiverMobile = data.getString(i, "mobile");
                                list.add(info);
                            }
                        }
                    }
                    if (list.size() > 0) {
                        if (list.size() == 1) {//只有一个账户，直接跳转
                            transferMobile_ONE = list.get(0).receiverMobile;
                            transferName_ONE = list.get(0).receiverName;
                            transferNameOA_ONE = list.get(0).receiverOA;
                            bonusModel.getEmployeeOa(1, list.get(0).receiverOA, this);
                            list.clear();
                        } else {//多个账户要进列表选择
                            intent = new Intent(RedpacketsDGZHShareMainMainActivity.this, RedpacketsDGZHAccountListActivity.class);
                            intent.putExtra(Contants.PARAMETER.BALANCE, balance);
                            intent.putExtra("redpackets_list", list);
                            Bundle bundleObject = new Bundle();
                            bundleObject.putSerializable("redpackets_list", list);
                            bundleObject.putString(Contants.PARAMETER.PUBLIC_ACCOUNT, money);
                            bundleObject.putString(Contants.PARAMETER.PAY_ANO, pay_ano);
                            bundleObject.putInt(Contants.PARAMETER.PAY_ATID, pay_atid);
                            intent.putExtras(bundleObject);
                            startActivity(intent);
                            list.clear();
                        }
                    } else {
                        ToastUtil.showShortToast(RedpacketsDGZHShareMainMainActivity.this, "你输入的账号有误！");
                    }
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    String error = "";
                    JSONObject contentJSONObject = HttpTools.getContentJSONObject(result);
                    if (contentJSONObject != null) {
                        try {
                            error = contentJSONObject.getString("message");
                            JSONObject content = contentJSONObject.getJSONObject("content");
                            if (content != null) {
                                String cano = content.getString("cano");
                                String atid = content.getString("atid");
                                Intent intent = new Intent(RedpacketsDGZHShareMainMainActivity.this, PublicAccountTransferToColleagueActivity.class);
                                intent.putExtra("cano", cano);
                                intent.putExtra("atid", atid);
                                intent.putExtra(Contants.PARAMETER.PUBLIC_ACCOUNT, money);
                                intent.putExtra(Contants.PARAMETER.MOBILE, transferMobile_ONE);
                                intent.putExtra(Contants.PARAMETER.PAY_NAME, transferName_ONE);
                                intent.putExtra(Contants.PARAMETER.OA, transferNameOA_ONE);
                                intent.putExtra(Contants.PARAMETER.PAY_ANO, pay_ano);
                                intent.putExtra(Contants.PARAMETER.PAY_ATID, pay_atid);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            ToastUtil.showShortToast(RedpacketsDGZHShareMainMainActivity.this, error);
                        }
                    }
                }
        }
    }

    public void check() {
        String name = edtColleagueInfo.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.showShortToast(this, "请输入同事信息");
        } else {
            getEmployeeInfo(name);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 1002:
                colleagueInfo = data.getStringExtra("phoneNum");
                edtColleagueInfo.setText(colleagueInfo);
                break;
            default:
                break;
        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_redpackets_share_main, null);
    }

    @Override
    public String getHeadTitle() {
        return "兑换给同事";
    }
}
