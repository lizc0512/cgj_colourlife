package com.youmai.hxsdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.dialog.MccDialog;
import com.youmai.hxsdk.entity.MccCode;
import com.youmai.hxsdk.entity.SmsCode;
import com.youmai.hxsdk.fragment.LoginFragment;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.LanguageUtil;
import com.youmai.hxsdk.utils.MccUtils;
import com.youmai.hxsdk.utils.PhoneImsi;
import com.youmai.hxsdk.utils.PhoneNumTypes;
import com.youmai.hxsdk.utils.ScreenUtils;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.utils.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.List;


/**
 * Created by Administrator on 2016/7/19.
 * SDK 登录页面
 */
public class LoginActivity extends SdkBaseActivity implements View.OnClickListener {

    private static final int HANDLER_COUNT_SECOND = 0;

    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    private static final String SEND_SMS_NUMBER = "106906969693226";  //发送短信验证码号码
    private static final int CODE_LEN = 4;   //短信验证码长度

    private static final int DATA_MAX_TIME = 60; //最大时间 1min

    private LoginHandler mHandler;

    private int curTime;
    private SmsReceiver mSmsReceiver;

    private FrameLayout relContent;
    private EditText phoneET;
    private EditText validET;
    private Button btnValid;
    private Button sureBtn;

    private ImageView btnPhoneClean;

    private RelativeLayout phoneLay;
    private TextView codeSpinner;
    private String mccCode;//区号

    private boolean isPhoneET = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setFinishOnTouchOutside(true);
        setContentView(R.layout.hx_activity_hx_login);
        //登录验证界面浏览次数统计
        mHandler = new LoginHandler(this);
        mSmsReceiver = new SmsReceiver();

        curTime = DATA_MAX_TIME;

        relContent = (FrameLayout) findViewById(R.id.rel_content);
        phoneET = (EditText) findViewById(R.id.phone_et);
        phoneET.addTextChangedListener(mTextWatcher);
        phoneLay = (RelativeLayout) findViewById(R.id.phone_lay);

        validET = (EditText) findViewById(R.id.valid_et);
        validET.addTextChangedListener(mValidWatcher);
        validET.setOnClickListener(this);

        btnPhoneClean = (ImageView) findViewById(R.id.phone_clean);
        btnPhoneClean.setOnClickListener(this);

        btnValid = (Button) findViewById(R.id.valid_btn);
        btnValid.setOnClickListener(this);

        codeSpinner = (TextView) findViewById(R.id.phone_code);

        final View phone_line = findViewById(R.id.phone_line);
        final View valid_line = findViewById(R.id.valid_line);

        sureBtn = (Button) findViewById(R.id.sure_btn);

        sureBtn.setOnClickListener(this);
        findViewById(R.id.close_btn).setOnClickListener(this);
        findViewById(R.id.rel_content).setOnClickListener(this);

        //手机验证事件
        mccSelect(); //设置国家前缀

        phoneET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    phone_line.setBackgroundColor(getResources().getColor(R.color.hx_main_color));
                } else {
                    phone_line.setBackgroundColor(getResources().getColor(R.color.hxs_color_gray17));
                }
            }
        });

        validET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    valid_line.setBackgroundColor(getResources().getColor(R.color.hx_main_color));
                } else {
                    valid_line.setBackgroundColor(getResources().getColor(R.color.hxs_color_gray17));
                }
            }
        });
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (StringUtils.isEmpty(s.toString())) {
                isPhoneET = false;
                btnPhoneClean.setVisibility(View.GONE);
                sureBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.hx_btn_login_on));
                btnValid.setBackgroundDrawable(getResources().getDrawable(R.drawable.hx_login_gray_shape));
                btnValid.setTextColor(getResources().getColor(R.color.hxs_color_gray18));
            } else {
                isPhoneET = true;
                btnPhoneClean.setVisibility(View.VISIBLE);
                sureBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.hx_btn_login_selector));
                btnValid.setBackgroundDrawable(getResources().getDrawable(R.drawable.hx_login_trans_shape));
                btnValid.setTextColor(getResources().getColor(R.color.hx_main_color));
            }
        }
    };

    TextWatcher mValidWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (StringUtils.isEmpty(s.toString()) && !isPhoneET) {
                sureBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.hx_btn_login_on));
            } else {
                isPhoneET = false;
                sureBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.hx_btn_login_selector));
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        monitorLay();

        IntentFilter filter = new IntentFilter();
        filter.addAction(LoginFragment.SMS_RECEIVED_ACTION);
        registerReceiver(mSmsReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mSmsReceiver != null) {
            unregisterReceiver(mSmsReceiver);
        }

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mHandler.hasMessages(HANDLER_COUNT_SECOND)) {
            mHandler.removeMessages(HANDLER_COUNT_SECOND);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.close_btn) {
            finish();
        } else if (id == R.id.valid_et) {

        } else if (id == R.id.valid_btn) {
            //获取验证码事件
            String phoneNum = phoneET.getText().toString();
            if (!AppUtils.isMobileNum(phoneNum)) {
                Toast.makeText(this, mContext.getString(R.string.hx_toast_09), Toast.LENGTH_SHORT).show();
                return;
            }

            if (AppUtils.isNetworkConnected(this)) {
                reqSmsCode(phoneNum);
            } else {
                Toast.makeText(this, mContext.getString(R.string.hx_toast_10), Toast.LENGTH_SHORT).show();
            }


        } else if (id == R.id.sure_btn) {
            //登录事件
            String phoneNum = phoneET.getText().toString();
            if (!AppUtils.isMobileNum(phoneNum)) {
                Toast.makeText(this, mContext.getString(R.string.hx_toast_11), Toast.LENGTH_SHORT).show();
                return;
            }

            String valid = validET.getText().toString();
            if (StringUtils.isEmpty(valid)) {
                Toast.makeText(this, mContext.getString(R.string.hx_toast_12), Toast.LENGTH_SHORT).show();
                return;
            }
            login(phoneNum, valid);
        } else if (id == R.id.phone_clean) {
            phoneET.setText("");
        }
    }

    /**
     * 设置国家前缀
     */
    private void mccSelect() {
        final List<MccCode> codeList = MccUtils.getMccCodeList(mContext);//获取国家前缀
        if (codeList.size() > 0) {
            final int index = PhoneNumTypes.getIndexForMcc(codeList, PhoneImsi.getMCC(mContext));
            mccCode = codeList.get(index).getCode();
            codeSpinner.setText(mccCode);
            codeSpinner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MccDialog.Builder builder = new MccDialog.Builder(mContext)
                            .setTitle(R.string.hx_mcc_select)
                            .setIndex(index)
                            .setMccCodes(codeList)
                            .setPositiveListener(new MccDialog.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, final String code) {
                                    codeSpinner.setText(code);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mccCode = code;
                                            dialog.dismiss();
                                        }
                                    }, 300);
                                }
                            });
                    builder.create().show();
                }
            });
        }
    }


    /**
     * 处理验证.
     */
    private void checkValid() {
        curTime--;
        if (curTime <= 0) {
            curTime = DATA_MAX_TIME;
            btnValid.setEnabled(true);
            btnValid.setText(R.string.hx_login_req_valid);


            phoneET.setEnabled(true);
            btnPhoneClean.setEnabled(true);

        } else {
            btnValid.setText(getString(R.string.hx_login_valid_second, curTime));
            mHandler.sendEmptyMessageDelayed(HANDLER_COUNT_SECOND, 1000);
        }
    }


    /**
     * 初始化验证
     */
    private void reqSmsCode(String phoneNum) {
        //验证
        btnValid.setEnabled(false);
        phoneET.setFocusable(false);
        btnPhoneClean.setEnabled(false);
        //字体变
        mHandler.sendEmptyMessageDelayed(HANDLER_COUNT_SECOND, 1000);


        IPostListener callback = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                SmsCode resp = GsonUtil.parse(response, SmsCode.class);
                if (resp != null) {
                    if (resp.isSucess()) {
                        String valid = resp.getE();
                        if (!StringUtils.isEmpty(valid) && !valid.equals("0")) {
                            validET.setText(valid);
                        }
                    }
                    ToastUtil.showToast(mContext, resp.getM());
                }
            }
        };

        if (LanguageUtil.isCN(mContext)) {
            HuxinSdkManager.instance().reqSmsCode(phoneNum, PhoneImsi.getMCC(mContext), callback);
        } else {
            //国际登录
            phoneNum = PhoneNumTypes.changePhone(phoneNum, mccCode);//转国际号码
            HuxinSdkManager.instance().reqSmsCode(phoneNum, MccUtils.getMcc(mccCode, mContext), callback);
        }
    }

    private void login(String phoneNum, String valid) {

        HuxinSdkManager.LoginListener listener = new HuxinSdkManager.LoginListener() {
            @Override
            public void success(String msg) {
                HuxinSdkManager.instance().initEmo();
                finish();
            }

            @Override
            public void fail(String msg) {
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            }
        };
        //国际登录,大陆不需要过滤
        if (!LanguageUtil.isCN(mContext)) {
            phoneNum = PhoneNumTypes.changePhone(phoneNum, mccCode);//过滤号码
        }
        HuxinSdkManager.instance().login(phoneNum, PhoneImsi.getMCC(mContext), valid, listener);
    }


    private class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String valid = intent.getStringExtra(LoginFragment.VALID);
            if (!StringUtils.isEmpty(valid)) {
                validET.setText(valid);
                abortBroadcast();
            }
        }
    }

    /**
     * 主线程处理handler
     *
     * @author Administrator
     */
    private static class LoginHandler extends Handler {
        private WeakReference<LoginActivity> mTarget;

        LoginHandler(LoginActivity target) {
            mTarget = new WeakReference<>(target);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity activity = mTarget.get();
            switch (msg.what) {
                case HANDLER_COUNT_SECOND:
                    activity.checkValid();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 监听 parent layout
     */
    private void monitorLay() {
        relContent.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        relContent.getWindowVisibleDisplayFrame(r);

                        int screenHeight = relContent.getRootView().getHeight();
                        int heightDifference = screenHeight - (r.bottom - r.top);
                        Log.e("YW", "Keyboard Size: " + heightDifference + "\t height: " +
                                (ScreenUtils.getHeightPixels(mContext) - heightDifference));

                        if (heightDifference > 450) {
                            ViewGroup.LayoutParams params = relContent.getLayoutParams();
                            params.height = ScreenUtils.getHeightPixels(mContext) - heightDifference + 105;
                            relContent.setLayoutParams(params);
                        } else {
                            relContent.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
                        }
                        relContent.requestLayout();
                    }
                });
    }
}
