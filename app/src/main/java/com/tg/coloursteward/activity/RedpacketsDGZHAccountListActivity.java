package com.tg.coloursteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.RedpacketsInfo;
import com.tg.coloursteward.model.BonusModel;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.view.ChoiceView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 账号选择（根据手机号查询oa账号类别页面）
 */
public class RedpacketsDGZHAccountListActivity extends BaseActivity {
    public static final String REDPACKETS_LIST = "redpackets_list";
    private ListView mListView;
    private ListAdapter adapter;
    /**
     * 红包余额
     */
    private Double balance;
    private ArrayList<RedpacketsInfo> list = new ArrayList<RedpacketsInfo>();
    private int electPosition = -1;


    private String transferName;
    private String transferNameOA;
    private String transferMobile;

    private String money;
    private String pay_ano;
    private int pay_atid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            money = getIntent().getStringExtra(Contants.PARAMETER.PUBLIC_ACCOUNT);
            pay_ano = getIntent().getStringExtra(Contants.PARAMETER.PAY_ANO);
            pay_atid = getIntent().getIntExtra(Contants.PARAMETER.PAY_ATID, -1);
            balance = intent.getDoubleExtra(Contants.PARAMETER.BALANCE, 0.00);
            Bundle bundleObject = getIntent().getExtras();
            list = (ArrayList<RedpacketsInfo>) bundleObject.getSerializable(REDPACKETS_LIST);

        }
        initView();
    }

    @Override
    protected boolean handClickEvent(View v) {
        if (electPosition != -1) {
            RedpacketsInfo info = list.get(electPosition);
            transferMobile = info.receiverMobile;
            transferName = info.receiverName;
            transferNameOA = info.receiverOA;
            BonusModel bonusModel = new BonusModel(this);
            bonusModel.getEmployeeOa(0, info.receiverOA, this);
        } else {
            ToastUtil.showShortToast(RedpacketsDGZHAccountListActivity.this, "请先选择账号");
        }
        return super.handClickEvent(v);
    }

    private void initView() {
        mListView = findViewById(R.id.list_view);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        adapter = new ArrayAdapter<RedpacketsInfo>(this, R.layout.item_pay_choice, list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final ChoiceView view;
                if (convertView == null) {
                    view = new ChoiceView(RedpacketsDGZHAccountListActivity.this);
                } else {
                    view = (ChoiceView) convertView;
                }
                RedpacketsInfo info = list.get(position);
                TextView tvChoose = (TextView) view.findViewById(R.id.tv_choose);
                TextView tvRemarks = (TextView) view.findViewById(R.id.tv_remarks);
                tvChoose.setText(info.receiverName);
                tvRemarks.setText("OA：" + info.receiverOA);
                return view;
            }

            @Override
            public boolean isEnabled(int position) {
                return true;
            }
        };
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener((parent, view, position, id) -> electPosition = position);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_redpackets_dgzh_account_list, null);
    }

    @Override
    public String getHeadTitle() {
        headView.setRightText("下一步");
        headView.setRightTextColor(getResources().getColor(R.color.white));
        headView.setListenerRight(singleListener);
        return "账号选择";
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    JSONObject contentJSONObject = HttpTools.getContentJSONObject(result);
                    if (contentJSONObject != null) {
                        try {
                            JSONObject content = contentJSONObject.getJSONObject("content");
                            if (content != null) {
                                String cano = content.getString("cano");
                                String atid = content.getString("atid");
                                Intent intent = new Intent(this, PublicAccountTransferToColleagueActivity.class);
                                intent.putExtra("cano", cano);
                                intent.putExtra("atid", atid);
                                intent.putExtra(Contants.PARAMETER.PUBLIC_ACCOUNT, money);
                                intent.putExtra(Contants.PARAMETER.MOBILE, transferMobile);
                                intent.putExtra(Contants.PARAMETER.PAY_NAME, transferName);
                                intent.putExtra(Contants.PARAMETER.OA, transferNameOA);
                                intent.putExtra(Contants.PARAMETER.PAY_ANO, pay_ano);
                                intent.putExtra(Contants.PARAMETER.PAY_ATID, pay_atid);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
    }
}
