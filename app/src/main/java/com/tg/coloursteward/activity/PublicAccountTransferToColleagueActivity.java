package com.tg.coloursteward.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.model.BonusModel;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.serice.AppAuthService;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.RoundImageView;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.tg.point.activity.ChangePawdTwoStepActivity;
import com.tg.point.entity.CheckPwdEntiy;
import com.tg.point.entity.PointTransactionTokenEntity;
import com.tg.point.model.PointModel;
import com.tg.setting.activity.SettingActivity;
import com.tg.user.model.UserModel;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.pwddialog.PasswordDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * 对公账户兑换给同事，最新入口
 */
public class PublicAccountTransferToColleagueActivity extends BaseActivity implements HttpResponse {
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

    private ProgressDialog mProgressDialog;
    private UserModel userModel;
    private PointModel pointModel;
    private String state;//支付密码的状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        userModel = new UserModel(this);
        pointModel = new PointModel(this);
        userModel.getTs(0, this);
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
        initData();
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
                    pointModel.getTransactionToken(3, this);
                }
            } else {
                getAppAuthInfo();
            }
        }
        return super.handClickEvent(v);
    }

    private void initView() {
        accessToken_1 = Tools.getStringValue(PublicAccountTransferToColleagueActivity.this, Contants.storage.APPAUTH_1);
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
        BonusModel bonusModel = new BonusModel(this);
        String ts = HttpTools.getTime();
        long time = System.currentTimeMillis();//获取当前时间戳
        String orderno = MD5.getMd5Value(String.valueOf(time)).toLowerCase();
        bonusModel.postCollegeFasttransaction(4, accessToken_1, transferAmount, orderno, edtMessage.getText().toString(),
                payAtid, edtMessage.getText().toString(), payAno, atid, cano, ts, this);
    }

    public void initData() {
        String str = Contants.Html5.HEAD_ICON_URL + "/avatar?uid=" + OA;
        GlideUtils.loadImageDefaultDisplay(this, str, imgHead, R.drawable.placeholder2, R.drawable.placeholder2);
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
                            pointModel.getTransactionToken(3, PublicAccountTransferToColleagueActivity.this);
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
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    String difference = HttpTools.getContentString(result);
                    if (!TextUtils.isEmpty(difference)) {
                        spUtils.saveLongData(SpConstants.UserModel.DIFFERENCE, Long.valueOf(difference));
                    }
                }
                break;
            case 3:
                try {
                    PointTransactionTokenEntity pointTransactionTokenEntity = GsonUtils.gsonToBean(result, PointTransactionTokenEntity.class);
                    PointTransactionTokenEntity.ContentBean contentBean = pointTransactionTokenEntity.getContent();
                    state = contentBean.getState();
                    switch (state) {//1 已实名已设置支付密码2 已实名未设置支付密码3 未实名未设置支付密码4 未实名已设置支付密码
                        case "2"://已实名未设置支付密码
                            Intent intent = new Intent(PublicAccountTransferToColleagueActivity.this, ChangePawdTwoStepActivity.class);
                            startActivity(intent);
                            break;
                        case "3"://未实名未设置支付密码
                        case "4"://未实名已设置支付密码
                            DialogFactory.getInstance().showDoorDialog(this, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (AppUtils.isApkInstalled(PublicAccountTransferToColleagueActivity.this, "cn.net.cyberway")) {
                                        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("colourlifeauth://web?linkURL=colourlife://proto?type=Information"));
                                        startActivity(it);
                                    } else {
                                        AppUtils.launchAppDetail(PublicAccountTransferToColleagueActivity.this, "cn.net.cyberway", "");
                                    }
                                }
                            }, null, 1, "您的账号尚未实名，请前往彩之云APP进行实名认证", "去认证", null);
                            break;
                        default://1已实名已设置支付密码
                            payPssswordDialog();
                            break;
                    }
                } catch (Exception e) {

                }
                break;
            case 4:
                if (!TextUtils.isEmpty(result)) {
                    sendBroadcast(new Intent(PublicAccountActivity.ACTION_PUBLIC_ACCOUNT));
                    ToastFactory.showToast(PublicAccountTransferToColleagueActivity.this, "兑换成功");
                    finish();
                }
                break;
            case 7:
                if (!TextUtils.isEmpty(result)) {
                    CheckPwdEntiy entiy = new CheckPwdEntiy();
                    entiy = GsonUtils.gsonToBean(result, CheckPwdEntiy.class);
                    if (entiy.getContent().getRight_pwd().equals("1")) {
                        submit();
                    } else {
                        String remain = entiy.getContent().getRemain();
                        if (remain.equals("0")) {
                            ToastUtil.showShortToast(this, "您已输入5次错误密码，账户被锁定，请明日再进行操作");
                        } else {
                            ToastUtil.showShortToast(this, "支付密码不正确，您还可以输入" + remain + "次");
                        }
                    }
                }
                break;
        }
    }

    private void payPssswordDialog() {
        PasswordDialogListener dialogListener = new PasswordDialogListener(this, new PasswordDialogListener.pwdDialogListener() {
            @Override
            public void result(String pwd) {
                pointModel.postCheckPwd(7, pwd, 2, PublicAccountTransferToColleagueActivity.this);
            }

            @Override
            public void forgetPassWord() {
                Intent it = new Intent(PublicAccountTransferToColleagueActivity.this, SettingActivity.class);
                startActivity(it);
            }
        });
        dialogListener.show();
    }
}
