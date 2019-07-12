package com.tg.coloursteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.serice.AppAuthService;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.RoundImageView;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * 给同事发红包
 */
public class RedpacketsTransferToColleagueH5Activity extends BaseActivity {
    private static final String TAG = "PublicAccountTransferTo";

    public static final String OA_USERNAME = "oa_username";
    public static final String Failed_MESSAGE = "failed_message";
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
    //    private String acceptAno;//收款账号
    private LinearLayout llSubmit;
    private TextView tvTicket;
    //    private String money;


    private String payAno;//支付方账户
    private int payAtid;//支付给类型

    private String OA;//收款方OA账号
    private String name;//收款方名字
    private String mobile;//收款方电话
    private String money;//可用余额
    private String cano;//收款方账户
    private int atid;//收款方类型

    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
//            money = intent.getStringExtra(Contants.PARAMETER.PUBLIC_ACCOUNT);
            payAtid = intent.getIntExtra(Contants.PARAMETER.PAY_ATID, -1);
            payAno = intent.getStringExtra(Contants.PARAMETER.PAY_ANO);
            Log.e(TAG, "onCreate:payAno " + payAno);
            OA = intent.getStringExtra(Contants.PARAMETER.OA);
            name = intent.getStringExtra(Contants.PARAMETER.PAY_NAME);
            mobile = intent.getStringExtra(Contants.PARAMETER.MOBILE);
            money = intent.getStringExtra(Contants.PARAMETER.PUBLIC_ACCOUNT);
            cano = intent.getStringExtra("cano");
            atid = Integer.valueOf(intent.getStringExtra("atid"));

        }

        initView();
        if (StringUtils.isNotEmpty(money)) {
            tvTicket.setText("可用余额：" + money);
        }
//       有获取头像的接口
        initData();
        initOptions();
    }

    @Override
    protected boolean handClickEvent(View v) {
        if (check()) {
            String expireTime = Tools.getStringValue(RedpacketsTransferToColleagueH5Activity.this, Contants.storage.APPAUTHTIME_1);
            Date dt = new Date();
            Long time = dt.getTime();
            /**
             * 获取对公账户数据
             */
            if (StringUtils.isNotEmpty(expireTime)) {
                if (Long.parseLong(expireTime) <= time) {//token过期
                    getAppAuthInfo();
                } else {
                    submit();
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
        accessToken_1 = Tools.getStringValue(RedpacketsTransferToColleagueH5Activity.this, Contants.storage.APPAUTH_1);
        imgHead = (RoundImageView) findViewById(R.id.rig_head);
        imgHead.setCircleShape();
        tvTicket = (TextView) findViewById(R.id.tv_ticket);
        edtAmount = (EditText) findViewById(R.id.edit);
        edtMessage = (EditText) findViewById(R.id.edt_send_message);
        tv_head = (TextView) findViewById(R.id.tv_head);
        tv_receiver = (TextView) findViewById(R.id.tv_czy_account);
        llSubmit = (LinearLayout) findViewById(R.id.ll_submit);
        llSubmit.setOnClickListener(singleListener);
//        tv_head.setText("正在兑换给" + UserInfo.realname);
        tv_head.setText("正在转账给");
        tv_receiver.setText(name + "(" + mobile + ")");
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
                            ToastFactory.showToast(RedpacketsTransferToColleagueH5Activity.this, "小数点后最多两位数");
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
                        ToastFactory.showToast(RedpacketsTransferToColleagueH5Activity.this, "输入金额不能超过5000");
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
            params.put("orgtype", payAtid);//支付账号类型
            params.put("detail", edtMessage.getEditableText().toString());
            params.put("orgaccountno", payAno);//支付账户
            params.put("desttype", atid);
            params.put("destaccountno", cano);
            params.put("starttime", ts);

            Log.e(TAG, "submit: " + accessToken_1 + "\n" +
                    transferAmount + "\n" + orderno +
                    "\n" + edtMessage.getEditableText().toString() + "\n"
                    + payAtid + "\n" +
                    edtMessage.getEditableText().toString() + "\n" +
                    payAno + "\n" +
                    atid + "\n" +
                    cano + "\n" +
                    ts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpTools.httpPost(Contants.URl.URL_ICETEST, "/jrpt/transaction/fasttransaction", config, params);
    }

    public void initData() {
        String str = Contants.Html5.HEAD_ICON_URL + "/avatar?uid=" + OA;
        imageLoader.clearMemoryCache();
        imageLoader.clearDiskCache();
        imageLoader.displayImage(str, imgHead, options);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (code == 0) {
            /**
             * 发送已更改的广播
             */
//            sendBroadcast(new Intent(PublicAccountActivity.ACTION_PUBLIC_ACCOUNT));
            intent = new Intent(MyBrowserActivity.ACTION_FRESH_PAYINFO);
            intent.putExtra(MyBrowserActivity.PAY_STATE, 1);
            this.sendBroadcast(intent);
            ToastFactory.showToast(RedpacketsTransferToColleagueH5Activity.this, "支付成功");
            finish();
        } else {
            intent = new Intent(MyBrowserActivity.ACTION_FRESH_PAYINFO);
            intent.putExtra(MyBrowserActivity.PAY_STATE, 0);
            this.sendBroadcast(intent);
            ToastFactory.showToast(RedpacketsTransferToColleagueH5Activity.this, "支付失败，请稍后再试");
            Log.e(TAG, "onFailed: " + message);
        }
    }

    /**
     * 获取token（1.0）
     * sectet
     */
    private void getAppAuthInfo() {
        if (appAuthService == null) {
            appAuthService = new AppAuthService(RedpacketsTransferToColleagueH5Activity.this);
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
                            Tools.saveStringValue(RedpacketsTransferToColleagueH5Activity.this, Contants.storage.APPAUTH_1, accessToken);
                            Tools.saveStringValue(RedpacketsTransferToColleagueH5Activity.this, Contants.storage.APPAUTHTIME_1, expireTime);
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

    /**
     * 判断
     *
     * @return
     */
    private boolean check() {
        transferAmount = edtAmount.getEditableText().toString();
        transferAmount = String.valueOf(Double.parseDouble(String.valueOf(transferAmount)));
        if (transferAmount.length() > 0 && Tools.point2(transferAmount)) {
            Double double2 = Double.parseDouble(transferAmount);

            if (double2 > Double.parseDouble(money)) {
                ToastFactory.showToast(RedpacketsTransferToColleagueH5Activity.this, "超出账户余额");
                return false;
            } else if (double2 > 5000) {
                ToastFactory.showToast(RedpacketsTransferToColleagueH5Activity.this, "输入金额不能超过5000");
                return false;
            } else if (Double.parseDouble(money) >= double2 && double2 > 0) {
                return true;
            } else {
                ToastFactory.showToast(RedpacketsTransferToColleagueH5Activity.this, "输入金额有误");
                return false;
            }
        } else {
            ToastFactory.showToast(RedpacketsTransferToColleagueH5Activity.this, "输入金额有误");
            return false;
        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_redpackets_transfer_to_colleague_h5, null);
    }

    @Override
    public String getHeadTitle() {
        headView.setListenerBack(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RedpacketsTransferToColleagueH5Activity.this, MyBrowserActivity.class);
                intent.putExtra(Failed_MESSAGE, "failed");
                startActivity(intent);
                finish();
            }
        });
        return "兑换给同事";
    }

    /**
     * 监听返回键
     */
    @Override
    public void onBackPressed() {
        intent = new Intent(MyBrowserActivity.ACTION_FRESH_PAYINFO);
//        失败
        intent.putExtra(MyBrowserActivity.PAY_STATE, 0);
        RedpacketsTransferToColleagueH5Activity.this.sendBroadcast(intent);
        finish();
        super.onBackPressed();
//        Intent intent = new Intent(RedpacketsTransferToColleagueH5Activity.this, MyBrowserActivity.class);
//        intent.putExtra(Failed_MESSAGE, "failed");
//        startActivity(intent);
//        finish();

    }

}