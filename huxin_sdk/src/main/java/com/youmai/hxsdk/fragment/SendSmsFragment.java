package com.youmai.hxsdk.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.SMSUtils;


public class SendSmsFragment extends BaseFragment implements View.OnClickListener {
    public final static String TAG = SendSmsFragment.class.getSimpleName();


    private EditText et_phone, et_content;
    private Button btn_send;
    private TextView tv_content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hx_fragment_sms, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        et_phone = (EditText) view.findViewById(R.id.et_phone);
        tv_content = (TextView) view.findViewById(R.id.tv_content);
        et_content = (EditText) view.findViewById(R.id.et_content);
        btn_send = (Button) view.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_send) {
            String desPhone = et_phone.getText().toString();
            String textMsg = tv_content.getText().toString()
                    + et_content.getText().toString();

            if (AppUtils.isMobileNum(desPhone)) {
                Toast.makeText(mAct, textMsg, Toast.LENGTH_SHORT).show();
                SMSUtils.sendSMS(mAct, desPhone, textMsg);
            } else {
                Toast.makeText(mAct, getString(R.string.hx_toast_45), Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onPause() {
        super.onPause();

    }


}
