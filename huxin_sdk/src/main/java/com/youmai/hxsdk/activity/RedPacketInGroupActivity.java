package com.youmai.hxsdk.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.GroupInfoBean;
import com.youmai.hxsdk.dialog.HxPayPasswordDialog;
import com.youmai.hxsdk.entity.red.RedPackageList;
import com.youmai.hxsdk.entity.red.SendRedPacketResult;
import com.youmai.hxsdk.entity.red.StandardRedPackage;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiGroup;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.List;

/**
 * 作者：create by YW
 * 日期：2017.06.07 11:42
 * 描述：Red packet
 */
public class RedPacketInGroupActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = RedPacketInGroupActivity.class.getSimpleName();

    public static final String TARGET_ID = "target_id";
    public static final String GROUP_COUNT = "group_count";
    public static final String TARGET_NAME = "target_name";
    public static final String TARGET_AVATAR = "target_avatar";


    private Context mContext;
    private TextView tv_error;
    private TextView tv_back;
    private TextView tv_title;
    private TextView tv_right;

    private TextView tv_value;

    private AppCompatEditText et_money;
    private TextView tv_money;
    private AppCompatEditText et_msg;

    private AppCompatEditText et_count;
    private TextView tv_person;

    private int groupId;

    private double moneyMax;
    private double money;

    private int numberTotal;

    private String pano;

    //private StandardRedPackage.ContentBean.FixedConfigBean fixedConfig;
    private StandardRedPackage.ContentBean.RandomConfigBean randomConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_red_packet_in_group);
        mContext = this;

        groupId = getIntent().getIntExtra(TARGET_ID, 0);
        GroupInfoBean groupInfo = getIntent().getParcelableExtra(IMGroupActivity.GROUP_INFO);

        initView();

        loadRedPacket();

        if (groupInfo != null) {
            String format = getResources().getString(R.string.group_count);
            tv_person.setText(String.format(format, String.valueOf(groupInfo.getGroup_member_count())));
        } else {
            queryGroupInfo(groupId);
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void initView() {

        tv_error = (TextView) findViewById(R.id.tv_error);
        tv_value = (TextView) findViewById(R.id.tv_value);

        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("彩利是");

        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_right.setText("利是记录");
        tv_right.setOnClickListener(this);

        tv_money = (TextView) findViewById(R.id.tv_money);
        et_msg = (AppCompatEditText) findViewById(R.id.et_msg);

        et_money = (AppCompatEditText) findViewById(R.id.et_money);
        et_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {

                    double value = Double.parseDouble(s.toString());

                    if (value > randomConfig.getMoneyMax()) {
                        Toast.makeText(mContext, "超过利是最大金额限制，请重新设置", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value < randomConfig.getMoneyMin()) {
                        Toast.makeText(mContext, "小于利是最小金额限制，请重新设置", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value > moneyMax) {
                        Toast.makeText(mContext, "超过了您的利是余额，请重新设置", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    money = value;


                } catch (Exception e) {
                    e.printStackTrace();
                }


                String format = getResources().getString(R.string.red_packet_unit1);
                tv_money.setText(String.format(format, s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        et_count = (AppCompatEditText) findViewById(R.id.et_count);
        et_count.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int value = Integer.parseInt(s.toString());
                try {
                    if (value > randomConfig.getNumberMax()) {
                        Toast.makeText(mContext, "超过利是最大数目限制，请重新设置", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value < randomConfig.getNumberMin()) {
                        Toast.makeText(mContext, "小于利是最小数目限制，请重新设置", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    numberTotal = value;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tv_person = (TextView) findViewById(R.id.tv_person);

        findViewById(R.id.btn_commit).setOnClickListener(this);


    }


    private void loadRedPacket() {
        HuxinSdkManager.instance().reqRedPackageStandardConfig(new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                StandardRedPackage bean = GsonUtil.parse(response, StandardRedPackage.class);
                if (bean != null) {
                    if (bean.isSuccess()) {
                        //fixedConfig = bean.getContent().getFixedConfig();
                        randomConfig = bean.getContent().getRandomConfig();
                    } else {
                        Toast.makeText(mContext, bean.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        HuxinSdkManager.instance().reqRedPackageList(new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                RedPackageList bean = GsonUtil.parse(response, RedPackageList.class);
                if (bean != null) {
                    if (bean.isSuccess()) {
                        List<RedPackageList.ContentBean> list = bean.getContent();

                        RedPackageList.ContentBean contentBean = null;

                        if (!ListUtils.isEmpty(list)) {
                            for (RedPackageList.ContentBean item : list) {
                                if (item.getName().equals("彩管家全国饭票")) {
                                    contentBean = item;
                                    break;
                                }
                            }
                        }

                        if (contentBean != null) {
                            pano = contentBean.getPano();
                            String balance = contentBean.getBalance();

                            String format = getResources().getString(R.string.red_packet_unit2);
                            tv_value.setText(String.format(format, balance));


                            try {
                                moneyMax = Double.parseDouble(balance);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }


                    } else {
                        Toast.makeText(mContext, bean.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }


    private void queryGroupInfo(int groupId) {

        long updateTime = 0;
        HuxinSdkManager.instance().reqGroupInfo(groupId, updateTime, new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiGroup.GroupInfoRsp rsp = YouMaiGroup.GroupInfoRsp.parseFrom(pduBase.body);
                    if (rsp.getResult() == YouMaiBasic.ResultCode.RESULT_CODE_SUCCESS) {
                        if (rsp.getUpdate()) {
                            YouMaiGroup.GroupInfo groupInfo = rsp.getGroupInfo();
                            String format = getResources().getString(R.string.group_count);
                            tv_person.setText(String.format(format, String.valueOf(groupInfo.getGroupMemberCount())));

                        }
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_commit) {

            final HxPayPasswordDialog dialog = new HxPayPasswordDialog(this);
            dialog.setOnFinishInput(new HxPayPasswordDialog.OnPasswordInputFinish() {
                @Override
                public void inputFinish() {
                    dialog.dismiss();
                    String remark = et_msg.getText().toString().trim();

                    if (TextUtils.isEmpty(remark)) {
                        remark = et_msg.getHint().toString().trim();
                    }

                    String password = dialog.getStrPassword();

                    final String title = remark;

                    if (pano == null) {
                        Toast.makeText(mContext, "查询饭票数据发生错误", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (money == 0) {
                        Toast.makeText(mContext, "请正确添加利是金额", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (numberTotal == 0) {
                        Toast.makeText(mContext, "请正确添加利是个数", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    HuxinSdkManager.instance().reqSendGroupRedPackage(money, numberTotal,
                            title, pano, password,
                            new IGetListener() {
                                @Override
                                public void httpReqResult(String response) {
                                    SendRedPacketResult bean = GsonUtil.parse(response, SendRedPacketResult.class);
                                    if (bean != null) {
                                        if (bean.isSuccess()) {
                                            String redUuid = bean.getContent().getLishiUuid();
                                            Intent intent = new Intent();
                                            intent.putExtra("value", String.valueOf(money));
                                            intent.putExtra("redTitle", title);
                                            intent.putExtra("redUuid", redUuid);
                                            setResult(Activity.RESULT_OK, intent);
                                            finish();
                                        } else {
                                            Toast.makeText(mContext, bean.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                            });


                }
            });
            dialog.show();
        } else if (id == R.id.tv_back) {
            onBackPressed();
        } else if (id == R.id.tv_right) {
            startActivity(new Intent(this, RedPacketHistoryActivity.class));
        }
    }
}
