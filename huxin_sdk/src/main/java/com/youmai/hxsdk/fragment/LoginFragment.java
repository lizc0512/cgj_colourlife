package com.youmai.hxsdk.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.entity.SmsCode;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.PhoneImsi;
import com.youmai.hxsdk.utils.StringUtils;

import java.net.InetSocketAddress;


public class LoginFragment extends BaseFragment implements View.OnClickListener {
    public final static String TAG = LoginFragment.class.getSimpleName();

    public static final String SMS_RECEIVED_ACTION = "huxin.intent.action.SMS_RECEIVED";
    public static final String VALID = "valid";

    private AppCompatEditText et_phone, et_sms_code, et_ip, et_port;
    private Button btn_sms_code;

    private Button btn_http_login;
    private Button btn_socket_login;

    private SmsReceiver mSmsReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSmsReceiver = new SmsReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hx_fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        et_phone = (AppCompatEditText) view.findViewById(R.id.et_phone);
        String phone = HuxinSdkManager.instance().getPhoneNum();
        if (!phone.isEmpty()) {
            et_phone.setText(phone);
        }

        et_sms_code = (AppCompatEditText) view.findViewById(R.id.et_sms_code);

        et_ip = (AppCompatEditText) view.findViewById(R.id.et_ip);
        et_port = (AppCompatEditText) view.findViewById(R.id.et_port);

        btn_sms_code = (Button) view.findViewById(R.id.btn_sms_code);
        btn_sms_code.setOnClickListener(this);


        btn_http_login = (Button) view.findViewById(R.id.btn_http_login);
        btn_http_login.setOnClickListener(this);

        btn_socket_login = (Button) view.findViewById(R.id.btn_socket_login);
        btn_socket_login.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_sms_code) {
            String phoneNum = et_phone.getText().toString();
            if (AppUtils.isMobileNum(phoneNum)) {
                reqSmsCode(phoneNum);
            } else {
                Toast.makeText(mAct, getString(R.string.hx_toast_38), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.btn_http_login) {
            if (AppUtils.isMobileNum(et_phone.getText().toString())) {
                login();
            } else {
                Toast.makeText(mAct, getString(R.string.hx_toast_38), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.btn_socket_login) {
            int userId = HuxinSdkManager.instance().getUserId();
            String phone = HuxinSdkManager.instance().getPhoneNum();
            String session = HuxinSdkManager.instance().getSession();

            String ip = et_ip.getText().toString();
            String portStr = et_port.getText().toString();
            if (!AppUtils.isIpv4(ip) || (!AppUtils.isNumber(portStr))) {
                Toast.makeText(mAct, getString(R.string.hx_toast_39), Toast.LENGTH_SHORT).show();
                HuxinSdkManager.instance().tcpLogin(userId, phone, session);
                return;
            }
            if (Integer.parseInt(portStr) > 65535) {
                Toast.makeText(mAct, getString(R.string.hx_toast_39), Toast.LENGTH_SHORT).show();
                HuxinSdkManager.instance().tcpLogin(userId, phone, session);
                return;
            }
            int port = Integer.parseInt(portStr);
            if (!StringUtils.isEmpty(ip) && port != 0) {
                InetSocketAddress isa = new InetSocketAddress(ip, port);
                HuxinSdkManager.instance().connectTcp(userId, phone, session, isa);
            } else {
                HuxinSdkManager.instance().tcpLogin(userId, phone, session);
            }
        }


    }


    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(SMS_RECEIVED_ACTION);
        mAct.registerReceiver(mSmsReceiver, filter);
    }


    @Override
    public void onPause() {
        super.onPause();

        if (mSmsReceiver != null) {
            mAct.unregisterReceiver(mSmsReceiver);
        }

    }


    private void reqSmsCode(String phoneNum) {
        IPostListener callback = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                SmsCode resp = GsonUtil.parse(response,
                        SmsCode.class);
                if (resp == null) {
                    Toast.makeText(mAct, getString(R.string.hx_toast_40), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (resp.isSucess()) {
                    String valid = resp.getE();
                    if (!StringUtils.isEmpty(valid) && !valid.equals("0")) {
                        et_sms_code.setText(valid);
                    }
                }
                Toast.makeText(mAct, resp.getM(), Toast.LENGTH_SHORT).show();
            }
        };
        HuxinSdkManager.instance().reqSmsCode(phoneNum, PhoneImsi.getMCC(mAct), callback);
    }


    private void login() {
        final String phone = et_phone.getText().toString().trim();
        String validation = et_sms_code.getText().toString().trim();

        HuxinSdkManager.LoginListener callback = new HuxinSdkManager.LoginListener() {
            @Override
            public void success(String msg) {
                Toast.makeText(mAct, "login success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void fail(String msg) {
                Toast.makeText(mAct, "login fail", Toast.LENGTH_SHORT).show();
            }
        };

        HuxinSdkManager.instance().login(phone, PhoneImsi.getMCC(mAct), validation, callback);
    }


    private class SmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String valid = intent.getStringExtra(VALID);
            if (!StringUtils.isEmpty(valid)) {
                et_sms_code.setText(valid);
            }
        }
    }
}
