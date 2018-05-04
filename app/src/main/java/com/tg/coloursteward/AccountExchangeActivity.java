package com.tg.coloursteward;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.AccountDetailNewInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.PwdDialog2;
import com.tg.coloursteward.view.dialog.PwdDialog2.ADialogCallback;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * 即时分配兑换
 */
public class AccountExchangeActivity extends BaseActivity {
    public static  final String ACCOUNT_DETAIL_NEW_INFO = "account_detail_new_info";
    private AccountDetailNewInfo info ;
    private TextView tvAll;//全部兑换
    private TextView tvQuota;//金额
    private String split_money;
    private EditText edit;
    private String accessToken;
    private AuthAppService authAppService;//2.0授权
    private String money;
    private RelativeLayout rlSubmit;
    private PwdDialog2 aDialog;
	private ADialogCallback aDialogCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null){
            info = (AccountDetailNewInfo) intent.getSerializableExtra(ACCOUNT_DETAIL_NEW_INFO);
        }
        if(info == null){
            ToastFactory.showToast(this,"参数错误");
            return;
        }
        split_money = info.split_money;
        initView();
    }

    @Override
    protected boolean handClickEvent(View v) {
       /* money = edit.getText().toString();
        if(StringUtils.isEmpty(money)){
            ToastFactory.showToast(AccountExchangeActivity.this,"请先输入兑换金额");
            return false;
        }
        if(Float.parseFloat(money) == 0){
            ToastFactory.showToast(AccountExchangeActivity.this,"兑换金额不能为0");
            return false;
        }
        if(Float.parseFloat(money) > Float.parseFloat(split_money)){
            ToastFactory.showToast(AccountExchangeActivity.this,"兑换金额不能大于额度");
            return false;
        }
        isSetPwd(0);*/
        return super.handClickEvent(v);
    }
    /**
     * 点击事件判断有误密码以卡
     * @param position
     */
    private void isSetPwd(int position){
        String key = Tools.getStringValue(this,Contants.EMPLOYEE_LOGIN.key);
        String secret = Tools.getStringValue(this,Contants.EMPLOYEE_LOGIN.secret);
        RequestConfig config = new RequestConfig(this, HttpTools.POST_SETPWD_INFO);
        RequestParams params = new RequestParams();
        params.put("position", position);
        params.put("key", key);
        params.put("secret",secret);
        HttpTools.httpPost(Contants.URl.URL_ICETEST,"/hongbao/isSetPwd",config, params);
    }
    private void judgment(){
        String expireTime = Tools.getStringValue(AccountExchangeActivity.this,Contants.storage.APPAUTHTIME);
        Date dt = new Date();
        Long time = dt.getTime();
        /**
         * 获取数据
         */
        if(StringUtils.isNotEmpty(expireTime)){
            if(Long.parseLong(expireTime) * 1000 <= time) {//token过期
                getAuthAppInfo();
            }else{
                submit();
            }
        }else{
            getAuthAppInfo();
        }
    }
    private void submit() {
        RequestConfig config = new RequestConfig(AccountExchangeActivity.this, HttpTools.POST_WITHDRAWALS,"提交");
        RequestParams params = new RequestParams();
        params.put("general_uuid",info.general_uuid);
        params.put("business_uuid",info.business_uuid);
        params.put("finance_pano",info.pano);
        params.put("finance_atid",info.atid);
        params.put("finance_cano",info.finance_cano);
        params.put("amount",money);
        params.put("split_type",2);
        params.put("split_target", UserInfo.employeeAccount);
        params.put("access_token",accessToken);
        params.put("arrival_pano","1027510cc2d51ab847ae8f192e3ae566");
        params.put("arrival_atid","27");
        params.put("arrival_cano","1027c29d5b5ec87f4578bf0b9309f250");
        params.put("arrival_account","test");
        HttpTools.httpPost(Contants.URl.URL_ICETEST,"/split/api/withdrawals",config,params);
    }

    private void initView() {
        accessToken = Tools.getStringValue(AccountExchangeActivity.this,Contants.storage.APPAUTH);
        tvQuota = (TextView) findViewById(R.id.tv_quota);
        tvAll = (TextView) findViewById(R.id.tv_all);
        edit = (EditText) findViewById(R.id.edit);
        rlSubmit = (RelativeLayout) findViewById(R.id.rl_submit);
        if(StringUtils.isNotEmpty(split_money)){
            DecimalFormat df = new DecimalFormat("0.00");
            tvQuota.setText(df.format(Double.parseDouble(split_money)));
        }
        tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DecimalFormat df = new DecimalFormat("0.00");
                edit.setText(df.format(Double.parseDouble(split_money)));
            }
        });
        rlSubmit.setOnClickListener(singleListener);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if(msg.arg1 == HttpTools.POST_SETPWD_INFO){//判断是否设置支付密码
            if(code == 0){
                JSONObject content = HttpTools.getContentJSONObject(jsonString);
                if(content != null){
                    String state;
                    try {
                        state = content.getString("state");
                        aDialogCallback = new ADialogCallback() {
                            @Override
                            public void callback() {
                               judgment();
                            }
                        };
                        aDialog = new PwdDialog2(
                                AccountExchangeActivity.this,
                                R.style.choice_dialog, state,
                                aDialogCallback);
                        aDialog.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else{//兑换
            if(code == 0){
                ToastFactory.showToast(AccountExchangeActivity.this,"兑换成功");
                finish();
            }else{
                ToastFactory.showToast(AccountExchangeActivity.this,message);
            }
        }
    }

    /**
     * 获取token(2.0)
     * sectet
     */
    private void getAuthAppInfo() {
        if(authAppService == null){
            authAppService = new AuthAppService(AccountExchangeActivity.this);
        }
        authAppService.getAppAuth(new GetTwoRecordListener<String, String>() {
            @Override
            public void onFinish(String jsonString, String data2,String data3) {
                int code = HttpTools.getCode(jsonString);
                if(code == 0){
                    JSONObject content = HttpTools.getContentJSONObject(jsonString);
                    if(content.length() > 0){
                        try {
                            accessToken = content.getString("accessToken");
                            String expireTime = content.getString("expireTime");
                            Tools.saveStringValue(AccountExchangeActivity.this,Contants.storage.APPAUTH,accessToken);
                            Tools.saveStringValue(AccountExchangeActivity.this,Contants.storage.APPAUTHTIME,expireTime);
                            submit();
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
        return getLayoutInflater().inflate(R.layout.activity_account_exchange,null);
    }

    @Override
    public String getHeadTitle() {
        return "兑换";
    }
}
