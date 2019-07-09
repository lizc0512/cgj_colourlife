package com.tg.coloursteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.RedpacketsInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.view.ChoiceView;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 账号选择（根据手机号查询oa账号类别页面）
 */
public class RedpacketsDGZHAccountListActivity extends BaseActivity {
    private static final String TAG = "RedpacketsDGZHAccountLi";
    public static final String REDPACKETS_LIST = "redpackets_list";
    private ListView mListView;
    private ListAdapter adapter;
    /**
     * 红包余额
     */
    private Double balance;
    private ArrayList<RedpacketsInfo> list = new ArrayList<RedpacketsInfo>();
    private Intent intent;
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
        if (list.size() == 0) {
            ToastFactory.showToast(this, "参数错误");
            return;
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
            RequestConfig config = new RequestConfig(this, HttpTools.GET_FINACE_BYOA_MULTI, "查询");
            RequestParams params = new RequestParams();
            params.put("oa_username", info.receiverOA);
            HttpTools.httpGet(Contants.URl.URL_ICETEST, "/czyprovide/employee/getFinanceByOa", config, params);



        } else {
            ToastFactory.showToast(RedpacketsDGZHAccountListActivity.this, "请先选择账号！");
        }
        return super.handClickEvent(v);
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.list_view);
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
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                electPosition = position;
            }
        });
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
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.GET_FINACE_BYOA_MULTI) {
            if (code == 0) {

                JSONObject contentJSONObject = HttpTools.getContentJSONObject(jsonString);
                if (contentJSONObject != null) {
                    try {
                        JSONObject content = contentJSONObject.getJSONObject("content");
                        if (content != null) {
                            String cano = content.getString("cano");
                            String atid = content.getString("atid");
                            Log.e(TAG, "onSuccess:对公账户详情多个之一 " + cano + "\n" + atid);
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
        }
    }
}
