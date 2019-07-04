package com.tg.coloursteward;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.tg.coloursteward.adapter.BonusRecordDetailAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.entity.BonusRecordDetailEntity;
import com.tg.coloursteward.model.BonusModel;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.LinkParseUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 详情(奖金包)
 *
 * @author Administrator
 */
public class BonusRecordDetailNewActivity extends BaseActivity implements HttpResponse {
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
        bonusModel = new BonusModel(this);
        initView();
        initData();
    }

    /**
     * 个人奖金包明细
     */
    private void initData() {
        bonusModel.getBonusRecordDetail(0, calculid, rummagerid, this);
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
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    bonusRecordDetailEntity = GsonUtils.gsonToBean(result, BonusRecordDetailEntity.class);
                    if (bonusRecordDetailEntity.getContent() != null) {
                        list.add(bonusRecordDetailEntity.getContent());
                        mlist = bonusRecordDetailEntity.getContent().getList();
                        if (null == adapter) {
                            adapter = new BonusRecordDetailAdapter(this, mlist);
                            recyclerView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        adapter.setBonusCallBack(url -> {
                            if (url.equals("colourlife://proto?type=jtkkActivity")) {
                                Intent intent = new Intent(BonusRecordDetailNewActivity.this, EffectDetailActivity.class);
                                EffectDetailActivity.listinfo = list;
                                intent.putExtra("type", "group");
                                startActivity(intent);
                            } else if (url.equals("colourlife://proto?type=kkActivity")) {
                                Intent intent = new Intent(BonusRecordDetailNewActivity.this, EffectDetailActivity.class);
                                EffectDetailActivity.listinfo = list;
                                intent.putExtra("type", "persion");
                                startActivity(intent);
                            } else {
                                LinkParseUtil.parse(this, url, "");
                            }
                        });
                    }
                }
                break;
        }
    }
}
