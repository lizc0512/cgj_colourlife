package com.tg.coloursteward;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.entity.SplitBillDetailEntity;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * 分配详情
 */
public class AccountExchangeDetail_FreezingActivity extends BaseActivity {
    private ImageView iv_image_show;
    private TextView freezing_value;
    private TextView freezing_flowvalue;
    private TextView freezing_time;
    private TextView freezing_money;
    private AlertDialog ProgressBarDialog;
    private AlertDialog dialog;
    private AuthAppService authAppService;
    private String accessToken;
    private String general_uuid, split_type, split_target, id;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        Intent intent = getIntent();
        if (intent != null) {
            general_uuid = intent.getStringExtra("general_uuid");
            split_type = intent.getStringExtra("split_type");
            split_target = intent.getStringExtra("split_target");
            id = intent.getStringExtra("id");
        }
        String expireTime = Tools.getStringValue(AccountExchangeDetail_FreezingActivity.this, Contants.storage.APPAUTHTIME);
        Date dt = new Date();
        Long time = dt.getTime();
        if (StringUtils.isNotEmpty(expireTime)) {
            if (Long.parseLong(expireTime) * 1000 <= time) {//token过期
                getAuthAppInfo();
            } else {
                initData();
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
            authAppService = new AuthAppService(AccountExchangeDetail_FreezingActivity.this);
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
                            Tools.saveStringValue(AccountExchangeDetail_FreezingActivity.this, Contants.storage.APPAUTH, accessToken);
                            Tools.saveStringValue(AccountExchangeDetail_FreezingActivity.this, Contants.storage.APPAUTHTIME, expireTime);
                            initData();
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

    private void initData() {
        accessToken = Tools.getStringValue(AccountExchangeDetail_FreezingActivity.this, Contants.storage.APPAUTH);
        RequestConfig config = new RequestConfig(this, HttpTools.GET_SPLIT_BILL_DETAIL);
        RequestParams params = new RequestParams();
        params.put("general_uuid", general_uuid);
        params.put("split_type", split_type);
        params.put("split_target", split_target);
        params.put("id", id);
        params.put("access_token", accessToken);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/split/api/bill/detail", config, params);
    }

    private void initView() {
        iv_image_show = findViewById(R.id.iv_image_show);
        freezing_value = findViewById(R.id.freezing_value);
        freezing_flowvalue = findViewById(R.id.freezing_flowvalue);
        freezing_time = findViewById(R.id.freezing_time);
        freezing_money = findViewById(R.id.freezing_money);
        iv_image_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNoticeDialogMust();
            }
        });
    }

    /**
     * 提示语
     */
    private void showNoticeDialogMust() {
        if (dialog == null) {
            DisplayMetrics metrics = Tools.getDisplayMetrics(AccountExchangeDetail_FreezingActivity.this);
            dialog = new AlertDialog.Builder(AccountExchangeDetail_FreezingActivity.this).create();
            dialog.setCancelable(false);
            Window window = dialog.getWindow();
            dialog.show();
            LinearLayout layout = (LinearLayout) LayoutInflater.from(AccountExchangeDetail_FreezingActivity.this)
                    .inflate(R.layout.update_dialog_layout, null);
            TextView tvContent = (TextView) layout.findViewById(R.id.dialog_msg);
            TextView update_title = (TextView) layout.findViewById(R.id.update_title);
            update_title.setText("提示");
            Button btnOk = (Button) layout.findViewById(R.id.btn_ok);
            Button btnCancel = (Button) layout.findViewById(R.id.btn_cancel);
            ImageView ivLine = (ImageView) layout.findViewById(R.id.iv_line);
            btnCancel.setVisibility(View.GONE);
            ivLine.setVisibility(View.GONE);
            tvContent.setText(message);
            btnOk.setText("确定");
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//立即更新
                    dialog.dismiss();
                }
            });
            window.setContentView(layout);
            WindowManager.LayoutParams p = window.getAttributes();
            p.width = ((int) (metrics.widthPixels - 80 * metrics.density));
            window.setAttributes(p);
        }

        dialog.show();
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        int code = HttpTools.getCode(jsonString);
        if (msg.arg1 == HttpTools.GET_SPLIT_BILL_DETAIL) {
            if (code == 0) {
                if (null != jsonString) {
                    SplitBillDetailEntity splitBillDetailEntity = GsonUtils.gsonToBean(jsonString, SplitBillDetailEntity.class);
                    freezing_value.setText(splitBillDetailEntity.getContent().getOut_trade_no());
                    freezing_flowvalue.setText(splitBillDetailEntity.getContent().getFinance_tno());//冻结流水号
                    DecimalFormat df = new DecimalFormat("0.00");
                    freezing_money.setText("金额: +" + df.format(Double.valueOf(splitBillDetailEntity.getContent().getAmount())));
                    freezing_time.setText(splitBillDetailEntity.getContent().getTime());
                    message = splitBillDetailEntity.getContent().getFreezen_msg();
                }
            }

        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_account_exchange_detail_freezing, null);
    }

    @Override
    public String getHeadTitle() {
        return "冻结详情";
    }
}
