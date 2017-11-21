package com.tg.coloursteward;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.RoundImageView;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 即时分成
 */
public class AccountActivity extends BaseActivity {
    private RoundImageView rivHead;
    private RelativeLayout rl_submit;
    private RelativeLayout rl_ticket_details;
    private TextView tvRealName;
    private TextView tv_balance;
    private String account;
    private DisplayImageOptions options;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPopup(true);
        initView();
        initOptions();
        initData();
    }

    private void initView() {
        rivHead = (RoundImageView) findViewById(R.id.riv_head);
        tvRealName = (TextView) findViewById(R.id.tv_real_Name);
        tv_balance = (TextView) findViewById(R.id.tv_balance);
        rl_submit = (RelativeLayout) findViewById(R.id.rl_submit);
        rl_ticket_details = (RelativeLayout) findViewById(R.id.rl_ticket_details);
        rl_submit.setEnabled(false);
        rl_ticket_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//明细
                Intent intent = new Intent(AccountActivity.this, AccountDetailActivity.class);
                intent.putExtra(AccountDetailActivity.TOTAL_ACCOUNT,account);
                startActivity(intent);

            }
        });
        tvRealName.setText(UserInfo.realname);

        String jsonStr = Tools.getStringValue(AccountActivity.this,Contants.storage.ACCOUNT);
        if(StringUtils.isNotEmpty(jsonStr)){
            JSONObject jsonObject = HttpTools.getContentJSONObject(jsonStr);
            try {
                account = jsonObject.getString("total_balance");
                if(StringUtils.isNotEmpty(account)){
                    tv_balance.setText(account);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            account = "0.00";
            tv_balance.setText(account);
        }
    }
    private void initOptions()
    {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder2)
                .showImageForEmptyUri(R.drawable.placeholder2)
                .showImageOnFail(R.drawable.placeholder2).cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(true)
                .build();
    }
    public void initData(){
        String str = Contants.URl.HEAD_ICON_URL +"avatar?uid=" + UserInfo.employeeAccount;
        ImageLoader.getInstance().clearMemoryCache();
        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().displayImage(str, rivHead, options);
    }

    private void getAccountInfo() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_HBUSER_MONEY);
        RequestParams params = new RequestParams();
        params.put("access_token", "1521ac83521b8063e7a9a49dc22e79b0");
        params.put("target_type", "2");
        params.put("target", UserInfo.employeeAccount);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/splitdivide/api/account",config, params);
    }
    @Override
    protected void onResume() {
        super.onResume();
        getAccountInfo();
    }


    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if(code == 0){
            Tools.saveStringValue(AccountActivity.this,Contants.storage.ACCOUNT,jsonString);
            JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
            try {
                account = jsonObject.getString("total_balance");
                if(StringUtils.isNotEmpty(account)){
                    tv_balance.setText(account);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            ToastFactory.showToast(AccountActivity.this,message);
            if(StringUtils.isNotEmpty(account)){
                tv_balance.setText(account);
            }else{
                tv_balance.setText("0.00");
            }
        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_account,null);
    }

    @Override
    public String getHeadTitle() {
        return "即时分成";
    }
}
