package com.tg.coloursteward;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.RedpacketsInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.updateapk.UpdateManager;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.ChoiceView;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据手机号查询oa账号类别页面
 */
public class RedpacketsAccountListActivity extends BaseActivity {
    public static final String REDPACKETS_LIST = "redpackets_list";
    private ListView mListView;
    private ListAdapter adapter;
    /**
     * 红包余额
     */
    private Double balance;
    private ArrayList<RedpacketsInfo> list = new ArrayList<RedpacketsInfo>();
    private  Intent intent;
    private  int electPosition = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null){
            balance = intent.getDoubleExtra(Contants.PARAMETER.BALANCE, 0.00);
            Bundle bundleObject = getIntent().getExtras();
            list = (ArrayList<RedpacketsInfo>) bundleObject.getSerializable(REDPACKETS_LIST);

        }
        if(list.size() == 0){
            ToastFactory.showToast(this,"参数错误");
            return;
        }
        initView();
    }

    @Override
    protected boolean handClickEvent(View v) {
        if(electPosition != -1){
            RedpacketsInfo info = list.get(electPosition);
            intent = new Intent(RedpacketsAccountListActivity.this,RedpacketsTransferMainActivity.class);
            intent.putExtra(Contants.PARAMETER.TRANSFERTO,"colleague");
            intent.putExtra(Contants.PARAMETER.BALANCE,balance);
            intent.putExtra("name",info.receiverName);
            intent.putExtra("username",info.receiverOA);
            intent.putExtra(Contants.PARAMETER.MOBILE, info.receiverMobile);
            intent.putExtra(Contants.PARAMETER.USERID,info.receiver_id);
            startActivity(intent);
            finish();
        }else {
            ToastFactory.showToast(RedpacketsAccountListActivity.this,"请先选择账号！");
        }
        return super.handClickEvent(v);
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        adapter = new ArrayAdapter<RedpacketsInfo>(this,R.layout.item_pay_choice, list){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final ChoiceView view;
                if(convertView == null) {
                    view = new ChoiceView(RedpacketsAccountListActivity.this);
                } else {
                    view = (ChoiceView)convertView;
                }
                RedpacketsInfo info = list.get(position);
                TextView tvChoose = (TextView) view.findViewById(R.id.tv_choose);
                TextView tvRemarks = (TextView) view.findViewById(R.id.tv_remarks);
                tvChoose.setText(info.receiverName);
                tvRemarks.setText("OA："+info.receiverOA);
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
        return getLayoutInflater().inflate(R.layout.activity_redpackets_account_list,null);
    }

    @Override
    public String getHeadTitle() {
        headView.setRightText("下一步");
        headView.setRightTextColor(getResources().getColor(R.color.white));
        headView.setListenerRight(singleListener);
        return "转账给同事";
    }
}
