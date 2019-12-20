package com.tg.point.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.constant.UserMessageConstant;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.ClearEditText;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.point.entity.CheckPwdEntiy;
import com.tg.point.entity.PointBalanceEntity;
import com.tg.point.entity.PointTransactionTokenEntity;
import com.tg.point.entity.RealNameTokenEntity;
import com.tg.point.model.NewUserModel;
import com.tg.point.model.PointModel;
import com.tg.point.view.CashierInputFilter;
import com.youmai.hxsdk.utils.AppUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import static com.tg.coloursteward.constant.UserMessageConstant.POINT_INPUT_PAYPAWD;
import static com.tg.coloursteward.constant.UserMessageConstant.POINT_SET_PAYPAWD;


/***
 * 赠送积分输入金额
 */
public class GivenPointAmountActivity extends BaseActivity implements View.OnClickListener, TextWatcher, HttpResponse {

    public static final String GIVENMOBILE = "givenmobile";
    public static final String GIVENAMOUNT = "givenamount";
    public static final String LASTTIME = "lasttime";
    public static final String LASTAMOUNT = "lastamount";
    public static final String USERPORTRAIT = "userportrait";
    public static final String USERNAME = "username";
    public static final String USERID = "userid";
    public static final String TYPE = "type";
    private ImageView mBack;
    private TextView mTitle;
    private ImageView iv_given_photo;
    private TextView tv_given_username;
    private ClearEditText ed_given_amount;
    private TextView tv_hint_notice;
    private ClearEditText ed_given_remark;
    private TextView tv_remain_amount;
    private TextView tv_remain_notice;
    private Button btn_given;
    private String giveAmount;//赠送的金额
    private String givenMobile;//赠送的手机号
    private PointModel pointModel;
    private NewUserModel newUserModel;
    private String keyword_sign;//是积分还是饭票的标识
    private String pano;//饭票类型
    private String state;//支付密码的状态
    private String dest_account;//目标用户的id
    private int last_time; //剩余次数
    private float last_amount;//剩余金额
    private float balanceAmount = 0.01f;//账户余额
    private String realName;//用户实名的
    private int giveBalance;//赠送的金额(单位分)
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setContentView(R.layout.activity_point_given_amount);
        mBack = findViewById(R.id.iv_base_back);
        mTitle = findViewById(R.id.tv_base_title);
        iv_given_photo = findViewById(R.id.iv_given_photo);
        tv_given_username = findViewById(R.id.tv_given_username);
        ed_given_amount = findViewById(R.id.ed_given_amount);
        tv_hint_notice = findViewById(R.id.tv_hint_notice);
        ed_given_remark = findViewById(R.id.ed_given_remark);
        tv_remain_amount = findViewById(R.id.tv_remain_amount);
        tv_remain_notice = findViewById(R.id.tv_remain_notice);
        btn_given = findViewById(R.id.btn_given);
        btn_given.setEnabled(false);
        mBack.setOnClickListener(this);
        btn_given.setOnClickListener(this);
        keyword_sign = spUtils.getStringData(SpConstants.storage.COLOUR_WALLET_KEYWORD_SIGN, "饭票");
        mTitle.setText(keyword_sign + "赠送");
        ed_given_amount.addTextChangedListener(this);
        Intent intent = getIntent();
        givenMobile = intent.getStringExtra(GIVENMOBILE);
        last_time = intent.getIntExtra(LASTTIME, 0);
        last_amount = intent.getFloatExtra(LASTAMOUNT, 0);
        dest_account = intent.getStringExtra(USERID);
        type = intent.getStringExtra(TYPE);
//        last_amount = last_amount * 1.0f / 100;
        pano = intent.getStringExtra(PointTransactionListActivity.POINTTPANO);
        pointModel = new PointModel(GivenPointAmountActivity.this);
        pointModel.getAccountBalance(1, pano, GivenPointAmountActivity.this);
        tv_remain_notice.setText("今日可赠送" + last_time + "次，剩余额度" + last_amount + keyword_sign);
        if (!EventBus.getDefault().isRegistered(GivenPointAmountActivity.this)) {
            EventBus.getDefault().register(GivenPointAmountActivity.this);
        }
        CashierInputFilter cashierInputFilter = new CashierInputFilter(GivenPointAmountActivity.this, 1, 5000);
        ed_given_amount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10), cashierInputFilter});
        String user_name = intent.getStringExtra(USERNAME);
        String user_portrait = intent.getStringExtra(USERPORTRAIT);
        tv_given_username.setText("正在向" + user_name + "\n" + "赠送" + keyword_sign);
        GlideUtils.loadImageDefaultDisplay(GivenPointAmountActivity.this, user_portrait, iv_given_photo, R.drawable.default_header, R.drawable.default_header);
    }

    @Override
    public View getContentView() {
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_base_back:
                finish();
                break;
            case R.id.btn_given:
                if (fastClick()) {
                    giveAmount = ed_given_amount.getText().toString().trim();
                    int length = giveAmount.length();
                    if (giveAmount.endsWith(".")) {
                        giveAmount = giveAmount.substring(0, length - 1);
                    } else if (giveAmount.startsWith("0") && !giveAmount.contains(".")) {
                        int pos = giveAmount.lastIndexOf('0');
                        giveAmount = giveAmount.substring(pos + 1, length);
                    }
                    float give_Amount = Float.valueOf(giveAmount);
                    if (give_Amount > balanceAmount) {
                        ToastUtil.showShortToast(GivenPointAmountActivity.this, "赠送金额不能超过可用余额");
                        return;
                    }
                    if (last_amount < 0) {
                        ToastUtil.showShortToast(GivenPointAmountActivity.this, "赠送金额不能超过剩余额度");
                        return;
                    }
                    giveBalance = (int) (give_Amount * 100);
                    pointModel.getTransactionToken(3, GivenPointAmountActivity.this);
                }
                break;
        }
    }

    @Subscribe
    public void onEvent(Object event) {
        final Message message = (Message) event;
        switch (message.what) {
            case UserMessageConstant.POINT_SUCCESS_RETURN:
            case UserMessageConstant.POINT_CONTINUE_GIVEN:
                finish();
                break;
            case POINT_INPUT_PAYPAWD://密码框输入密码
            case POINT_SET_PAYPAWD: //设置支付密码成功 直接拿密码进行支付
                String password = message.obj.toString();
                pointModel.postCheckPwd(7, password, 2, this);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(GivenPointAmountActivity.this)) {
            EventBus.getDefault().unregister(GivenPointAmountActivity.this);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        giveAmount = s.toString().trim();
        if (TextUtils.isEmpty(giveAmount)) {
            tv_hint_notice.setVisibility(View.VISIBLE);
        } else {
            tv_hint_notice.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(giveAmount) || giveAmount.equals("0") || giveAmount.equals("0.")
                || giveAmount.equals("0.0") || giveAmount.equals("0.00")) {
            btn_given.setEnabled(false);
            btn_given.setBackgroundResource(R.drawable.point_password_default_bg);
        } else {
            btn_given.setEnabled(true);
            btn_given.setBackgroundResource(R.drawable.point_password_click_bg);
        }
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 1:
                try {
                    PointBalanceEntity pointBalanceEntity = GsonUtils.gsonToBean(result, PointBalanceEntity.class);
                    PointBalanceEntity.ContentBean contentBean = pointBalanceEntity.getContent();
                    balanceAmount = contentBean.getBalance() * 1.0f / 100;
                    CashierInputFilter cashierInputFilter;
                    if (balanceAmount <= last_amount) {
                        cashierInputFilter = new CashierInputFilter(GivenPointAmountActivity.this, 0, balanceAmount);
                    } else {
                        cashierInputFilter = new CashierInputFilter(GivenPointAmountActivity.this, 1, last_amount);
                    }
                    ed_given_amount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10), cashierInputFilter});
                    tv_remain_amount.setText("可用余额:" + balanceAmount);
                } catch (Exception e) {

                }
                break;
            case 3:
                try {
                    PointTransactionTokenEntity pointTransactionTokenEntity = GsonUtils.gsonToBean(result, PointTransactionTokenEntity.class);
                    PointTransactionTokenEntity.ContentBean contentBean = pointTransactionTokenEntity.getContent();
                    state = contentBean.getState();
                    switch (state) {//1 已实名已设置支付密码2 已实名未设置支付密码3 未实名未设置支付密码4 未实名已设置支付密码
                        case "2"://已实名未设置支付密码
                            Intent intent = new Intent(GivenPointAmountActivity.this, ChangePawdTwoStepActivity.class);
                            startActivity(intent);
                            break;
                        case "3"://未实名未设置支付密码
                        case "4"://未实名已设置支付密码
                            DialogFactory.getInstance().showDoorDialog(this, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (AppUtils.isApkInstalled(GivenPointAmountActivity.this, "cn.net.cyberway")) {
                                        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("colourlifeauth://web?linkURL=colourlife://proto?type=Information"));
                                        startActivity(it);
                                    } else {
                                        AppUtils.launchAppDetail(GivenPointAmountActivity.this, "cn.net.cyberway", "");
                                    }
                                }
                            }, null, 1, "您的账号尚未实名，请前往彩之云APP进行实名认证", "去认证", null);
                            break;
                        default://1已实名已设置支付密码
                            showPayDialog();
                            break;
                    }
                } catch (Exception e) {

                }
                break;
            case 4://赠送成功  刷新首页的余额
                Message message = new Message();
                message.what = UserMessageConstant.SUREBTNCHECKET;
                EventBus.getDefault().post(message);
                Intent intent = new Intent(GivenPointAmountActivity.this, GivenPointResultActivity.class);
                intent.putExtra(GIVENMOBILE, givenMobile);
                intent.putExtra(GIVENAMOUNT, giveAmount);
                startActivityForResult(intent, 2000);
                break;
            case 5:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        RealNameTokenEntity entity = GsonUtils.gsonToBean(result, RealNameTokenEntity.class);
                        RealNameTokenEntity.ContentBean bean = entity.getContent();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 6:
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String code = jsonObject.getString("code");
                        if ("0".equals(code)) {
                            String content = jsonObject.getString("content");
                            if ("1".equals(content)) {
                                ToastUtil.showShortToast(this, "认证成功");
                                spUtils.saveStringData(SpConstants.storage.COLOUR_AUTH_REAL_NAME + UserInfo.uid, realName);
                                newUserModel.finishTask(10, "2", "task_web", this);//实名认证任务s
                                if ("3".equals(state)) {
                                    state = "2";
                                    Intent pawd_intent = new Intent(GivenPointAmountActivity.this, ChangePawdTwoStepActivity.class);
                                    startActivity(pawd_intent);
                                } else {
                                    showPayDialog();
                                }
                            }
                        } else {
                            String noticeMsg = jsonObject.getString("message");
                            ToastUtil.showShortToast(this, noticeMsg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 7:
                if (!TextUtils.isEmpty(result)) {
                    CheckPwdEntiy entiy = new CheckPwdEntiy();
                    entiy = GsonUtils.gsonToBean(result, CheckPwdEntiy.class);
                    if (entiy.getContent().getRight_pwd().equals("1")) {
                        pointModel.transferTransaction(4, giveBalance, entiy.getContent().getToken(), dest_account, pano,
                                ed_given_remark.getText().toString().trim(), type, GivenPointAmountActivity.this);
                    } else {
                        String remain = entiy.getContent().getRemain();
                        if (remain.equals("0")) {
                            ToastUtil.showShortToast(GivenPointAmountActivity.this, "您已输入5次错误密码，账户被锁定，请明日再进行操作");
                        } else {
                            ToastUtil.showShortToast(GivenPointAmountActivity.this, "支付密码不正确，您还可以输入" + remain + "次");
                        }
                    }
                }
                break;
        }
    }

    private void showPayDialog() {
        PointPasswordDialog pointPasswordDialog = new PointPasswordDialog(GivenPointAmountActivity.this);
        pointPasswordDialog.show();
    }
}
