package com.youmai.thirdbiz.colorful.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.thirdbiz.colorful.net.ColorsConfig;
import com.youmai.thirdbiz.colorful.net.ColorsUtil;
import com.youmai.thirdbiz.colorful.view.wheelview.ProvinceBean;
import com.youmai.thirdbiz.colorful.view.wheelview.pickerview.OptionsPickerView;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2016.12.09 11:44
 * 描述：添加业主信息
 */
public class ColorAddOwnerActivity extends SdkBaseActivity {

    private TextView tv_back;
    private TextView tv_province;
    private EditText et_name;
    private EditText et_tel_number;
    private EditText et_id_card;
    private Button btn_submit;


    OptionsPickerView pvOptions;
    //  省份
    ArrayList<ProvinceBean> provinceBeanList = new ArrayList<>();
    //  城市
    ArrayList<String> cities;
    ArrayList<List<String>> cityList = new ArrayList<>();
    //  区/县
    ArrayList<String> district;
    ArrayList<List<String>> districts;
    ArrayList<List<List<String>>> districtList = new ArrayList<>();

    private ProgressDialog progressDialog;
    private RelativeLayout add_owner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cgj_activity_color_add_owner_info);

        //StatisticsMgr.instance().addEvent(StatsIDConst.FLOAT_BIZ_OWNER);
        //添加业主界面浏览次数

        //主题的长按事件
        add_owner = (RelativeLayout) findViewById(R.id.add_owner);
        add_owner.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(ColorAddOwnerActivity.this, ColorsConfig.COLOR_BUILD_VERSION, Toast.LENGTH_LONG).show();
                return true;
            }
        });

        TextView tv_back = (TextView) findViewById(R.id.tv_back);
        final EditText et_name = (EditText) findViewById(R.id.edittext_name);
        final EditText et_tel_number = (EditText) findViewById(R.id.edittext_phone);
        final EditText et_id_card = (EditText) findViewById(R.id.edittext_id_card);

        String dstphone = getIntent().getStringExtra("dstPhone");

//        LogUtils.e("xx",dstphone);
        et_tel_number.setText(dstphone);

        Button btn_submit = (Button) findViewById(R.id.btn_commit);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uuid = getIntent().getStringExtra("uuid");

                String name = et_name.getText().toString();
                String phone = et_tel_number.getText().toString();
                String id_card = et_id_card.getText().toString();
                if (uuid == null) {
                    uuid = "huoqushibai";
                }
                if (AppUtils.isMobileNum(phone) && name.length() > 0) {
                    ShowProgress();
                    ColorsUtil.addOwnerInfo(name, phone, id_card, uuid, new IPostListener() {

                        @Override
                        public void httpReqResult(String response) {
                            progressDialog.dismiss();


                            LogUtils.e("xx", response);


                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                int code = jsonObject.optInt("code");

                                if (code == 0) {
                                    Toast.makeText(ColorAddOwnerActivity.this.getApplicationContext(), "提交成功", Toast.LENGTH_LONG).show();
                                    setResult(1);
                                    finish();
                                } else {
                                    Toast.makeText(ColorAddOwnerActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                LogUtils.e("xx", e.getMessage());

                                e.printStackTrace();
                            }
                            if (response == null) {
                                Toast.makeText(ColorAddOwnerActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
                } else {

                    if (!AppUtils.isMobileNum(phone)) {

                        Toast.makeText(ColorAddOwnerActivity.this, "你输入的手机号码有误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(ColorAddOwnerActivity.this, "你输入的姓名有误", Toast.LENGTH_SHORT).show();


                }

            }
        });

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


    }


    /**
     * 显示进度条
     */
    public void ShowProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在上传数据");
        progressDialog.show();
    }


}
