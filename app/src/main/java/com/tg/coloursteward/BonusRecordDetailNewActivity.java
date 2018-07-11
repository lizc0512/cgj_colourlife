package com.tg.coloursteward;


import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.entity.BonusRecordDetailEntity;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.view.dialog.ToastFactory;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 详情(奖金包)
 *
 * @author Administrator
 */
public class BonusRecordDetailNewActivity extends BaseActivity {
    public final static String BONUS_RECORD_INFO = "BonusRecordInfo";
    private TextView tv_coefficient_group;//集体系数之和
    private TextView tv_coefficient_personal;//个人系数
    private TextView tv_Bonus_personal;//个人奖金包
    private TextView tv_real_receive_month;//本月实获奖金
    private TextView tv_coefficient_groupname;
    private TextView tv_bonus_groupbao;
    private TextView tv_bonus_groupnum;
    private TextView tv_bonus_persona_num;
    private RelativeLayout rl_persion, rl_group;
    private Intent intent;
    private String calculid, rummagerid, name;
    private BonusRecordDetailEntity bonusRecordDetailEntity;
    private List<BonusRecordDetailEntity.ContentBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        if (intent != null) {
            calculid = intent.getStringExtra("calculid");
            rummagerid = intent.getStringExtra("rummagerid");
            name = intent.getStringExtra("name");
        }
        initView();
        initData();
    }

    /**
     * 个人奖金包明细
     */
    private void initData() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_USER_JJB_DETAIL);
        RequestParams params = new RequestParams();
        params.put("calculid", calculid);
        params.put("rummagerid", rummagerid);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/jxjjb/userjjb/detail",
                config, params);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        tv_coefficient_groupname = findViewById(R.id.tv_coefficient_groupname);
        tv_bonus_groupbao = findViewById(R.id.tv_bonus_groupbao);
        tv_bonus_groupnum = findViewById(R.id.tv_bonus_groupnum);
        tv_bonus_persona_num = findViewById(R.id.tv_bonus_persona_num);
        tv_coefficient_group = (TextView) findViewById(R.id.tv_coefficient_group);
        tv_coefficient_personal = (TextView) findViewById(R.id.tv_coefficient_personal);
        tv_Bonus_personal = (TextView) findViewById(R.id.tv_Bonus_personal);
        tv_real_receive_month = (TextView) findViewById(R.id.tv_real_receive_month);
        rl_group = findViewById(R.id.rl_group);
        rl_persion = findViewById(R.id.rl_persion);
        rl_group.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(BonusRecordDetailNewActivity.this, EffectDetailActivity.class);
//                intent.putExtra(EffectDetailActivity.BONUS_RECORD_INFO,(Serializable) list);
                EffectDetailActivity.listinfo = list;
                intent.putExtra("type", "group");
                startActivity(intent);
            }
        });
        rl_persion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(BonusRecordDetailNewActivity.this, EffectDetailActivity.class);
//                intent.putExtra(EffectDetailActivity.BONUS_RECORD_INFO, (Serializable) list);
                EffectDetailActivity.listinfo = list;
                intent.putExtra("type", "persion");
                startActivity(intent);
            }
        });
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_bonus_record_detail_new, null);
    }

    @Override
    public String getHeadTitle() {
        return "奖金包详情";
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.GET_USER_JJB_DETAIL) {
            if (code == 0) {
                bonusRecordDetailEntity = GsonUtils.gsonToBean(jsonString, BonusRecordDetailEntity.class);
                if (bonusRecordDetailEntity.getContent() != null) {
                    list.add(bonusRecordDetailEntity.getContent());
                    tv_coefficient_groupname.setText(name);
                    DecimalFormat df = new DecimalFormat("0.00");
                    tv_coefficient_group.setText(list.get(0).getTotaljjbbase() + "");
                    tv_coefficient_personal.setText(list.get(0).getJjbbase() + "");
                    BigDecimal a1 = new BigDecimal(Double.toString(list.get(0).getBaoFee()));
                    BigDecimal b1 = new BigDecimal(Double.toString(list.get(0).getJjbbase()));
                    BigDecimal result1 = a1.multiply(b1);
                    tv_Bonus_personal.setText(df.format(result1) + "");
                    tv_bonus_groupbao.setText(df.format(list.get(0).getJtbaoFee()));
                    tv_bonus_groupnum.setText(df.format(list.get(0).getJtkkfee()));
                    double a = Double.parseDouble(df.format(list.get(0).getNormalFee()));
                    double b = Double.parseDouble(df.format(list.get(0).getActualFee()));
                    tv_bonus_persona_num.setText(df.format(a - b));
                    tv_real_receive_month.setText(df.format(list.get(0).getActualFee()));

                }
            } else {
                ToastFactory.showToast(BonusRecordDetailNewActivity.this, message);
            }

        }
    }
}
