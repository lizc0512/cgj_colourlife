package com.tg.coloursteward;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.tg.coloursteward.adapter.MyPunishmentAdapter;
import com.tg.coloursteward.adapter.PersionPunishmentAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.entity.BonusRecordDetailEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 详情(绩效)
 *
 * @author Administrator
 */
public class EffectDetailActivity extends BaseActivity {
    private TextView tv_effect_date;//时间
    private TextView tv_punishment;//惩罚列表
    private ListView mListView2;
    private MyPunishmentAdapter myPunishmentAdapter;
    private PersionPunishmentAdapter persionPunishmentAdapter;
    private Intent intent;
    private String type;
    public static List<BonusRecordDetailEntity.ContentBean> listinfo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        if (intent != null) {
            type = intent.getStringExtra("type");
        }
        initView();
    }

    private void initView() {
        tv_effect_date = (TextView) findViewById(R.id.tv_effect_date);
        tv_punishment = (TextView) findViewById(R.id.tv_punishment);
        mListView2 = (ListView) findViewById(R.id.listView2);
        mListView2.setAdapter(myPunishmentAdapter);
        if (type.equals("group")) {
            tv_punishment.setText("集体奖罚列表：");
            if (listinfo != null && listinfo.size() > 0) {
                String date = listinfo.get(0).getYear() + "年" + listinfo.get(0).getMonth() + "月";
                tv_effect_date.setText(date);
                if (myPunishmentAdapter == null) {
                    myPunishmentAdapter = new MyPunishmentAdapter(this, listinfo.get(0).getJtkk());
                    mListView2.setAdapter(myPunishmentAdapter);
                } else {
                    myPunishmentAdapter.setData(listinfo.get(0).getJtkk());
                }
            }
        } else if (type.equals("persion")) {
            tv_punishment.setText("个人奖罚列表：");
            if (listinfo != null) {
                String date = listinfo.get(0).getYear() + "年" + listinfo.get(0).getMonth() + "月";
                tv_effect_date.setText(date);
                if (persionPunishmentAdapter == null) {
                    persionPunishmentAdapter = new PersionPunishmentAdapter(this, listinfo.get(0).getKk());
                    mListView2.setAdapter(persionPunishmentAdapter);
                } else {
                    persionPunishmentAdapter.setData(listinfo.get(0).getKk());
                }
            }
        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_effect_detail, null);
    }

    @Override
    public String getHeadTitle() {
        return "处罚详情";
    }


}
