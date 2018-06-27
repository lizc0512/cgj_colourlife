package com.youmai.hxsdk.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.ColorsConfig;
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

import java.text.DecimalFormat;
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
    private TextView tv_name;

    private AppCompatEditText et_money;
    private TextView tv_money;
    private AppCompatEditText et_msg;

    private Button btn_commit;

    private AppCompatEditText et_count;
    private TextView tv_person;
    private TextView tv_type;
    private TextView tv_type_title;
    private int type = 2;

    private int groupId;

    private double moneyMax;
    private double money;

    private int numberTotal;

    private String pano;
    private String moneyStr;

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

            String displayName = groupInfo.getGroup_name();
            boolean contains = displayName.contains(ColorsConfig.GROUP_DEFAULT_NAME);
            if (contains) {
                String format1 = getResources().getString(R.string.group_default_name);
                tv_name.setText(String.format(format1, groupInfo.getGroup_member_count()));
            }


        } else {
            queryGroupInfo(groupId);
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void initView() {
        tv_name = (TextView) findViewById(R.id.tv_name);

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
                    money = Double.parseDouble(s.toString());
                    //String format = getResources().getString(R.string.red_packet_unit1);
                    //tv_money.setText(String.format(format, String.valueOf(money * numberTotal)));

                    if (type == 2) {
                        moneyStr = String.valueOf(money);
                        tv_money.setText(moneyStr);

                        if (money > moneyMax) {
                            tv_error.setVisibility(View.VISIBLE);
                            btn_commit.setEnabled(false);
                            return;
                        } else {
                            btn_commit.setEnabled(true);
                            tv_error.setVisibility(View.INVISIBLE);
                        }

                    } else {
                        double num = money * numberTotal;
                        DecimalFormat format = new DecimalFormat("0.00");
                        moneyStr = format.format(num);

                        if (num > moneyMax) {
                            tv_error.setVisibility(View.VISIBLE);
                            btn_commit.setEnabled(false);
                            return;
                        } else {
                            btn_commit.setEnabled(true);
                            tv_error.setVisibility(View.INVISIBLE);
                        }

                        tv_money.setText(moneyStr);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                try {
                    numberTotal = Integer.parseInt(s.toString());
                    //String format = getResources().getString(R.string.red_packet_unit1);
                    //tv_money.setText(String.format(format, String.valueOf(money * numberTotal)));

                    tv_money.setText(String.valueOf(money * numberTotal));
                    if (type == 2) {
                        moneyStr = String.valueOf(money);
                        tv_money.setText(moneyStr);

                        if (money > moneyMax) {
                            tv_error.setVisibility(View.VISIBLE);
                            return;
                        } else {
                            tv_error.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        double num = money * numberTotal;
                        DecimalFormat format = new DecimalFormat("0.00");
                        moneyStr = format.format(num);


                        if (num > moneyMax) {
                            tv_error.setVisibility(View.VISIBLE);
                            return;
                        } else {
                            tv_error.setVisibility(View.INVISIBLE);
                        }
                        tv_money.setText(moneyStr);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tv_person = (TextView) findViewById(R.id.tv_person);
        tv_type = (TextView) findViewById(R.id.tv_type);
        if (type == 2) {
            tv_type.setText(R.string.type_fix);
        } else {
            tv_type.setText(R.string.type_pin);
        }


        tv_type_title = (TextView) findViewById(R.id.tv_type_title);

        btn_commit = (Button) findViewById(R.id.btn_commit);
        btn_commit.setOnClickListener(this);


        tv_type.setOnClickListener(this);


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

                            //String format = getResources().getString(R.string.red_packet_unit2);
                            //tv_value.setText(String.format(format, balance));
                            tv_value.setText(balance);

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

                            String displayName = groupInfo.getGroupName();
                            boolean contains = displayName.contains(ColorsConfig.GROUP_DEFAULT_NAME);
                            if (contains) {
                                String format1 = getResources().getString(R.string.group_default_name);
                                tv_name.setText(String.format(format1, groupInfo.getGroupMemberCount()));
                            }


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

                    String remark = et_msg.getText().toString().trim();

                    if (TextUtils.isEmpty(remark)) {
                        remark = et_msg.getHint().toString().trim();
                    }

                    String password = dialog.getStrPassword();

                    final String title = remark;

                    if (randomConfig == null) {
                        Toast.makeText(mContext, "利是配置接口失败，暂不支持发利是，请退出重试", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (money > randomConfig.getMoneyMax()) {
                        Toast.makeText(mContext, "超过利是最大金额限制，请重新设置", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (money < randomConfig.getMoneyMin()) {
                        Toast.makeText(mContext, "小于利是最小金额限制，请重新设置", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (money > moneyMax) {
                        Toast.makeText(mContext, "超过了您的利是余额，请重新设置", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (numberTotal > randomConfig.getNumberMax()) {
                        Toast.makeText(mContext, "超过利是最大数目限制，请重新设置", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (numberTotal < randomConfig.getNumberMin()) {
                        Toast.makeText(mContext, "小于利是最小数目限制，请重新设置", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (pano == null) {
                        Toast.makeText(mContext, "查询饭票数据发生错误", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    IGetListener listener = new IGetListener() {
                        @Override
                        public void httpReqResult(String response) {
                            SendRedPacketResult bean = GsonUtil.parse(response, SendRedPacketResult.class);
                            if (bean != null) {
                                if (bean.isSuccess()) {
                                    dialog.dismiss();
                                    String redUuid = bean.getContent().getLishiUuid();
                                    Intent intent = new Intent();
                                    intent.putExtra("value", String.valueOf(money));
                                    intent.putExtra("redTitle", title);
                                    intent.putExtra("redUuid", redUuid);
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                } else if (bean.getCode() == 1002) {
                                    dialog.pwError();
                                } else {
                                    Toast.makeText(mContext, bean.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    };
                    if (type == 2) {
                        HuxinSdkManager.instance().reqSendGroupRedPackageRandom(money, numberTotal,
                                title, pano, password, listener);
                    } else {
                        HuxinSdkManager.instance().reqSendGroupRedPackageFix(money, numberTotal,
                                title, pano, password, listener);
                    }


                }
            });
            dialog.show();
            dialog.setMoney(moneyStr);
        } else if (id == R.id.tv_back) {
            onBackPressed();
        } else if (id == R.id.tv_right) {
            startActivity(new Intent(this, RedPacketHistoryActivity.class));
        } else if (id == R.id.tv_type) {
            et_money.setText("");
            if (type == 2) {
                type = 1;
                tv_type.setText(R.string.type_pin);
                tv_type_title.setText(R.string.title_fix);
                tv_type_title.setCompoundDrawables(null, null, null, null);
            } else {
                type = 2;
                tv_type.setText(R.string.type_fix);
                tv_type_title.setText(R.string.title_pin);
                Drawable left = ContextCompat.getDrawable(this, R.drawable.ic_pin);
                tv_type_title.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
            }
        }
    }
}
