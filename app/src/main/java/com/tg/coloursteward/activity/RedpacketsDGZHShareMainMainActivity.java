package com.tg.coloursteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.RedpacketsInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 兑换给同事（对公账户进）
 *
 * @author Administrator
 */
public class RedpacketsDGZHShareMainMainActivity extends BaseActivity {
    private static final String TAG = "RedpacketsToColleagueMa";
    /**
     * 同事OA账号或手机号码
     */
    private EditText edtColleagueInfo;

    /**
     * 下一步
     */
    private RelativeLayout rlSubmit;

    /**
     * 获取到的员工oa信息
     */
    private LinearLayout lloaInfo;

    /**
     * 获取到的员工oa姓名
     */
    private TextView tvOaName;

    /**
     * 获取到的员工oa用户名
     */
    private TextView tvOaUsername;

    /**
     * 获取到的员工oa手机号
     */
    private TextView tvOaMobile;

    /**
     * oa账号或手机号
     */
    private String colleagueInfo;

    private String receiver_id;
    private String receiverName;
    private String receiverOA;
    private String receiverMobile;


    /**
     * 给同事发红包（colleague）/转账到彩之云（czy）/提现到银行卡（bank）
     */
    private String transferTo;

    /**
     * 红包余额
     */
    private Double balance;


    private ImageButton btnNext;

    /**
     * oa发红包记录的Adapter
     */
    private Intent intent;
    private int OA;
    final ArrayList<RedpacketsInfo> list = new ArrayList<RedpacketsInfo>();

    private String money;//对公账户余额
    private String transferNameOA;//转账对方OA账号
    private String transferName;//转账对方名字
    private String transferMobile;//转账对方电话
    private int pay_atid;//支付方类型
    private String pay_ano;//支付方账户

    private String transferNameOA_ONE;
    private String transferName_ONE;
    private String transferMobile_ONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPopup(false);
        transferTo = getIntent().getStringExtra("colleague");
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
        lloaInfo = (LinearLayout) findViewById(R.id.ll_oa_info);
        tvOaName = (TextView) findViewById(R.id.tv_oa_name);
        tvOaUsername = (TextView) findViewById(R.id.tv_oa_username);
        tvOaMobile = (TextView) findViewById(R.id.tv_oa_mobile);
        rlSubmit = (RelativeLayout) findViewById(R.id.rl_submit);
        btnNext = (ImageButton) findViewById(R.id.imgbtn_contact);
        rlSubmit.setOnClickListener(singleListener);
        btnNext.setOnClickListener(singleListener);
        lloaInfo.setVisibility(View.GONE);

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

    /**
     * 根据输入手机号获oa账号搜索
     *
     * @param username
     */
    private void getEmployeeInfo(String username) {
        RequestConfig confg = new RequestConfig(this, HttpTools.GET_EMPLOYEE_INFO, "查询");
        RequestParams param = new RequestParams();
        param.put("keyword", username);
        param.put("pagesize", "20");
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/txl2/contacts/search", confg, param);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.GET_EMPLOYEE_INFO) {
            if (code == 0) {
                JSONArray content = HttpTools.getContentJsonArray(jsonString);
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
                    if (list.size() == 1) {
                        /**
                         * 只有一个账户，直接跳转
                         */
                        transferMobile_ONE = list.get(0).receiverMobile;
                        transferName_ONE = list.get(0).receiverName;
                        transferNameOA_ONE = list.get(0).receiverOA;
                        RequestConfig config = new RequestConfig(this, HttpTools.GET_FINACE_BYOA_ONE, "查询");
                        RequestParams params = new RequestParams();
                        params.put("oa_username", list.get(0).receiverOA);
                        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/czyprovide/employee/getFinanceByOa", config, params);

                        list.clear();
//					finish();
                    } else {
                        /**
                         * 多个账户要进列表选择
                         */


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
//					finish();
                    }
                } else {
                    ToastFactory.showToast(RedpacketsDGZHShareMainMainActivity.this, "你输入的账号有误！");
                }
            } else {
                ToastFactory.showToast(RedpacketsDGZHShareMainMainActivity.this, message);
            }
        } else if (msg.arg1 == HttpTools.GET_FINACE_BYOA_ONE) {
            if (code == 0) {
                String error = "";
                JSONObject contentJSONObject = HttpTools.getContentJSONObject(jsonString);
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
                        ToastFactory.showToast(RedpacketsDGZHShareMainMainActivity.this, error);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 下一步
     */
    public void check() {
        if (edtColleagueInfo.getText().length() == 0) {
            ToastFactory.showToast(this, "请输入同事信息");
            return;
        }
        getEmployeeInfo(edtColleagueInfo.getText().toString());

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
        // TODO Auto-generated method stub
        return getLayoutInflater().inflate(R.layout.activity_redpackets_share_main, null);
    }

    @Override
    public String getHeadTitle() {
        // TODO Auto-generated method stub
        return "兑换给同事";
    }

}
