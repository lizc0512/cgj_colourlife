package com.tg.coloursteward.activity;

import android.app.ProgressDialog;
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

import com.dashuview.library.keep.Cqb_PayUtil;
import com.dashuview.library.keep.ListenerUtils;
import com.dashuview.library.keep.MyListener;
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
import com.tg.coloursteward.view.dialog.PwdDialog2;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import static com.tg.coloursteward.module.MainActivity.getEnvironment;
import static com.tg.coloursteward.module.MainActivity.getPublicParams;

/**
 * 兑换给同事（对公账户账户兑换）
 */
public class PublicAccountTransferToColleagueActivity extends BaseActivity implements MyListener {
    private static final String TAG = "PublicAccountTransferTo";
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

    private PwdDialog2 aDialog;
    private PwdDialog2.ADialogCallback aDialogCallback;
    private ProgressDialog mProgressDialog;

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
        ListenerUtils.setCallBack(this);
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
            String expireTime = Tools.getStringValue(PublicAccountTransferToColleagueActivity.this, Contants.storage.APPAUTHTIME_1);
            Date dt = new Date();
            Long time = dt.getTime();
            /**
             * 获取对公账户数据
             */
            if (StringUtils.isNotEmpty(expireTime)) {
                if (Long.parseLong(expireTime) <= time) {//token过期
                    getAppAuthInfo();
                } else {
                    Cqb_PayUtil.getInstance(this).PayPasswordDialog(getPublicParams(),getEnvironment(),"payDialog");

                }
            } else {
                getAppAuthInfo();
            }
        }
        return super.handClickEvent(v);
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(PublicAccountTransferToColleagueActivity.this);
            mProgressDialog.setMessage("正在兑换");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }

        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }

    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
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
        accessToken_1 = Tools.getStringValue(PublicAccountTransferToColleagueActivity.this, Contants.storage.APPAUTH_1);
        Log.e(TAG, "initView:鉴权1.0 " + accessToken_1);
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
                            ToastFactory.showToast(PublicAccountTransferToColleagueActivity.this, "小数点后最多两位数");
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
                        ToastFactory.showToast(PublicAccountTransferToColleagueActivity.this, "输入金额不能超过5000");
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

        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpTools.httpPost(Contants.URl.URL_ICETEST, "/jrpt/transaction/fasttransaction", config, params);
        showProgressDialog();
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
        dismissProgressDialog();
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (code == 0) {
            /**
             * 发送已更改的广播
             */
            sendBroadcast(new Intent(PublicAccountActivity.ACTION_PUBLIC_ACCOUNT));
            ToastFactory.showToast(PublicAccountTransferToColleagueActivity.this, "兑换成功");
            finish();
        } else {
            ToastFactory.showToast(PublicAccountTransferToColleagueActivity.this, message);
            Log.e(TAG, "onFailed: " + message);
        }
    }

    @Override
    public void onFail(Message msg, String hintString) {
        super.onFail(msg, hintString);
        dismissProgressDialog();
    }

    /**
     * 获取token（1.0）
     * sectet
     */
    private void getAppAuthInfo() {
        if (appAuthService == null) {
            appAuthService = new AppAuthService(PublicAccountTransferToColleagueActivity.this);
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
                            Tools.saveStringValue(PublicAccountTransferToColleagueActivity.this, Contants.storage.APPAUTH_1, accessToken);
                            Tools.saveStringValue(PublicAccountTransferToColleagueActivity.this, Contants.storage.APPAUTHTIME_1, expireTime);

                            Cqb_PayUtil.getInstance(PublicAccountTransferToColleagueActivity.this).PayPasswordDialog(getPublicParams(),getEnvironment(),"payDialog");
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
        if (!transferAmount.equals("")) {
            transferAmount = String.valueOf(Double.parseDouble(String.valueOf(transferAmount)));
        }
        if (transferAmount.length() > 0 && Tools.point2(transferAmount)) {
            Double double2 = Double.parseDouble(transferAmount);

            if (double2 > Double.parseDouble(money)) {
                ToastFactory.showToast(PublicAccountTransferToColleagueActivity.this, "超出账户余额");
                return false;
            } else if (double2 > 5000) {
                ToastFactory.showToast(PublicAccountTransferToColleagueActivity.this, "输入金额不能超过5000");
                return false;
            } else if (Double.parseDouble(money) >= double2 && double2 > 0) {
                return true;
            } else {
                ToastFactory.showToast(PublicAccountTransferToColleagueActivity.this, "输入金额有误");
                return false;
            }
        } else {
            ToastFactory.showToast(PublicAccountTransferToColleagueActivity.this, "输入金额有误");
            return false;
        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_public_account_transfer_to_colleague, null);
    }

    @Override
    public String getHeadTitle() {
        return "兑换给同事";
    }

    @Override
    public void authenticationFeedback(String s, int i) {
        switch (i) {
            case 16://密码校验成功
                submit();
                break;
            case 17://密码检验时主动中途退出
                ToastFactory.showToast(PublicAccountTransferToColleagueActivity.this,"已取消");
                break;
            case 18://没有设置支付密码
                ToastFactory.showToast(PublicAccountTransferToColleagueActivity.this,"未设置支付密码，即将跳转到彩钱包页面");
                Cqb_PayUtil.getInstance(this).createPay(getPublicParams(),getEnvironment());
                break;
            case 19://绑定银行卡并设置密码成功
                break;
            case 20://名片赠送成功
//                ToastFactory.showToast(EmployeeDataActivity.this,"转账成功");
                break;
        }
    }

    @Override
    public void toCFRS(String s) {

    }
}
