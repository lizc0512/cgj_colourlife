package com.tg.coloursteward;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.serice.AppAuthService;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.RoundImageView;
import com.tg.coloursteward.view.dialog.PwdDialog2;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 对公账户账户兑换
 */
public class PublicAccountExchangeActivity extends BaseActivity {
    /**
     * 输入金额EditText
     */
    private EditText edtAmount;
    /**
     * 捎一句话EditText
     */
    private EditText edtMessage;
    private TextView tv_head;
    private TextView tv_receiver;
    private RoundImageView imgHead;
    private DisplayImageOptions options;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    /**
     * 转账金额
     */
    private String transferAmount;
    private String accessToken_1;
    private AppAuthService appAuthService;//1.0授权
    private String money;
    private String payAno;
    private int payAtid;
    private String acceptAno;
    private LinearLayout llSubmit;
    private TextView tvTicket;

    private PwdDialog2 aDialog;
    private PwdDialog2.ADialogCallback aDialogCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            money = intent.getStringExtra(Contants.PARAMETER.PUBLIC_ACCOUNT);
            payAtid = intent.getIntExtra(Contants.PARAMETER.PAY_ATID, -1);
            payAno = intent.getStringExtra(Contants.PARAMETER.PAY_ANO);
        }
        RequestParams params = new RequestParams();
        params.put("oa_username", UserInfo.employeeAccount);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/newczy/employee/getFinanceByOa",
                new RequestConfig(this, HttpTools.GET_USER_INFO), params);
        initView();
        if (StringUtils.isNotEmpty(money)) {
            tvTicket.setText("可兑换余额：" + money);
        }
        initData();
        initOptions();
    }

    @Override
    protected boolean handClickEvent(View v) {
        if (check()) {
            if (StringUtils.isEmpty(acceptAno)) {
                ToastFactory.showToast(PublicAccountExchangeActivity.this, "金融信息获取失败，请稍后再试");
                return false;
            }
            String expireTime = Tools.getStringValue(PublicAccountExchangeActivity.this, Contants.storage.APPAUTHTIME_1);
            Date dt = new Date();
            Long time = dt.getTime();
            /**
             * 获取对公账户数据
             */
            if (StringUtils.isNotEmpty(expireTime)) {
                if (Long.parseLong(expireTime) <= time) {//token过期
                    getAppAuthInfo();
                } else {
                    aDialogCallback = new PwdDialog2.ADialogCallback() {
                        @Override
                        public void callback() {
                            submit();
                        }
                    };
                    aDialog = new PwdDialog2(this, R.style.choice_dialog, "inputPwd", aDialogCallback);
                    aDialog.show();
                }
            } else {
                getAppAuthInfo();
            }
        }
        return super.handClickEvent(v);
    }

    private void initOptions() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder2)
                .showImageForEmptyUri(R.drawable.placeholder2)
                .showImageOnFail(R.drawable.placeholder2).cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(true)
                .build();
    }

    private void initView() {
        accessToken_1 = Tools.getStringValue(PublicAccountExchangeActivity.this, Contants.storage.APPAUTH_1);
        imgHead = (RoundImageView) findViewById(R.id.rig_head);
        imgHead.setCircleShape();
        tvTicket = (TextView) findViewById(R.id.tv_ticket);
        edtAmount = (EditText) findViewById(R.id.edit);
        edtMessage = (EditText) findViewById(R.id.edt_send_message);
        tv_head = (TextView) findViewById(R.id.tv_head);
        tv_receiver = (TextView) findViewById(R.id.tv_czy_account);
        llSubmit = (LinearLayout) findViewById(R.id.ll_submit);
        llSubmit.setOnClickListener(singleListener);
        tv_head.setText("正在兑换给" + UserInfo.realname);
        tv_receiver.setText(UserInfo.realname + "(" + UserInfo.mobile + ")");
        edtAmount.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // 限制小数点后最多两位小数
                if (!Tools.point2(s.toString())) {
                    String inputString = s.toString();
                    if (Integer.toString(inputString.indexOf(".")) != null) {
                        int pointIndex = inputString.indexOf(".");
                        if (pointIndex != -2) {
                            ToastFactory.showToast(PublicAccountExchangeActivity.this, "小数点后最多两位数");
                        }
                    }
                }
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        edtAmount.setText(s);
                        edtAmount.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    edtAmount.setText(s);
                    edtAmount.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        edtAmount.setText(s.subSequence(0, 1));
                        edtAmount.setSelection(1);
                        return;
                    }
                }
                Double double2;
                if (s.toString().length() > 0) {
                    double2 = Double.parseDouble(s.toString());
                    if (double2 > 5000) {
                        ToastFactory.showToast(PublicAccountExchangeActivity.this, "输入金额不能超过5000");
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 提交数据
     */
    private void submit() {
        RequestConfig config = new RequestConfig(this, HttpTools.POST_FASTTRANSACTION);
        RequestParams params = new RequestParams();
        try {
            String ts = HttpTools.getTime();
            long time = System.currentTimeMillis();//获取当前时间戳
            Calendar c = Calendar.getInstance();
            String startTime = Tools.getDateToString(c.getTimeInMillis());
            String orderno = MD5.getMd5Value(String.valueOf(time)).toLowerCase();
            params.put("access_token", accessToken_1);
            params.put("money", transferAmount);
            params.put("orderno", orderno);
            params.put("content", edtMessage.getEditableText().toString());
            params.put("orgtype", payAtid);
            params.put("detail", edtMessage.getEditableText().toString());
            params.put("orgaccountno", payAno);
            params.put("desttype", 6);
            params.put("destaccountno", acceptAno);
            params.put("starttime", ts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpTools.httpPost(Contants.URl.URL_ICETEST, "/jrpt/transaction/fasttransaction", config, params);
    }

    public void initData() {
        String str = Contants.URl.HEAD_ICON_URL + "avatar?uid=" + UserInfo.employeeAccount;
        imageLoader.clearMemoryCache();
        imageLoader.clearDiskCache();
        imageLoader.displayImage(str, imgHead, options);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.GET_USER_INFO) {
            if (code == 0) {
                JSONObject content = HttpTools.getContentJSONObject(jsonString);
                try {
                    if (content != null) {
                        acceptAno = content.getString("cano");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                ToastFactory.showToast(PublicAccountExchangeActivity.this, message);
            }
        } else {
            if (code == 0) {
                /**
                 * 发送已更改的广播
                 */
                sendBroadcast(new Intent(PublicAccountActivity.ACTION_PUBLIC_ACCOUNT));
                ToastFactory.showToast(PublicAccountExchangeActivity.this, "兑换成功");
                finish();
            } else {
                ToastFactory.showToast(PublicAccountExchangeActivity.this, "兑换失败，请稍后再试");
            }
        }
    }

    /**
     * 获取token（1.0）
     * sectet
     */
    private void getAppAuthInfo() {
        if (appAuthService == null) {
            appAuthService = new AppAuthService(PublicAccountExchangeActivity.this);
        }
        appAuthService.getAppAuth(new GetTwoRecordListener<String, String>() {
            @Override
            public void onFinish(String jsonString, String data2, String data3) {
                int code = HttpTools.getCode(jsonString);
                if (code == 0) {
                    JSONObject content = HttpTools.getContentJSONObject(jsonString);
                    if (content.length() > 0) {
                        try {
                            String accessToken = content.getString("access_token");
                            String expireTime = content.getString("expire");
                            Tools.saveStringValue(PublicAccountExchangeActivity.this, Contants.storage.APPAUTH_1, accessToken);
                            Tools.saveStringValue(PublicAccountExchangeActivity.this, Contants.storage.APPAUTHTIME_1, expireTime);

                            aDialogCallback = new PwdDialog2.ADialogCallback() {
                                @Override
                                public void callback() {
                                    submit();
                                }
                            };

                            aDialog = new PwdDialog2(PublicAccountExchangeActivity.this, R.style.choice_dialog, "inputPwd", aDialogCallback);
                            aDialog.show();
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

    /**
     * 判断
     *
     * @return
     */
    private boolean check() {
        transferAmount = edtAmount.getEditableText().toString().trim();
        if (!transferAmount.equals("")){
            transferAmount = String.valueOf(Double.parseDouble(String.valueOf(transferAmount)));
        }
        if (transferAmount.length() > 0 && Tools.point2(transferAmount)) {
            Double double2 = Double.parseDouble(transferAmount);
            if (double2 > Double.parseDouble(money)) {
                ToastFactory.showToast(PublicAccountExchangeActivity.this, "超出账户余额");
                return false;
            } else if (double2 > 5000) {
                ToastFactory.showToast(PublicAccountExchangeActivity.this, "输入金额不能超过5000");
                return false;
            } else if (Double.parseDouble(money) >= double2 && double2 > 0) {
                return true;
            } else {
                ToastFactory.showToast(PublicAccountExchangeActivity.this, "输入金额有误");
                return false;
            }
        } else {
            ToastFactory.showToast(PublicAccountExchangeActivity.this, "输入金额有误");
            return false;
        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_public_account_exchange, null);
    }

    @Override
    public String getHeadTitle() {
        return "兑换至饭票";
    }
}
