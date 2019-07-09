package com.tg.coloursteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.PublicAccountListAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.PublicAccountInfo;
import com.tg.coloursteward.view.dialog.ToastFactory;

import java.util.ArrayList;

/**
 * 收款方信息
 */
public class PublicAccountListActivity extends BaseActivity {
    public static final String PUBLICACCOUNT_LIST = "publicaccount_list";
    private ListView mListView;
    private PublicAccountListAdapter adapter;
    private ArrayList<PublicAccountInfo> list = new ArrayList<PublicAccountInfo>();
    private String money;
    private String  ano;
    private int atid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null){
            Bundle bundleObject = getIntent().getExtras();
            list = (ArrayList<PublicAccountInfo>) bundleObject.getSerializable(PUBLICACCOUNT_LIST);
            money = intent.getStringExtra(Contants.PARAMETER.PUBLIC_ACCOUNT);
            atid = intent.getIntExtra(Contants.PARAMETER.PAY_ATID,-1);
            ano = intent.getStringExtra(Contants.PARAMETER.PAY_ANO);
        }
        if(list.size() == 0){
            ToastFactory.showToast(this,"参数错误");
            return;
        }
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mListView = (ListView) findViewById(R.id.lv_public_account);
        adapter = new PublicAccountListAdapter(PublicAccountListActivity.this,list);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PublicAccountInfo info = list.get(position);
                Intent intent = new Intent(PublicAccountListActivity.this,PublicAccountTransferActivity.class);
                intent.putExtra(Contants.PARAMETER.PUBLIC_ACCOUNT,money);
                intent.putExtra(Contants.PARAMETER.PAY_ATID,atid);
                intent.putExtra(Contants.PARAMETER.PAY_ANO,ano);
                intent.putExtra(Contants.PARAMETER.ACCEPT_ATID,info.atid);
                intent.putExtra(Contants.PARAMETER.ACCEPT_ANO,info.ano);
                intent.putExtra(Contants.PARAMETER.ACCEPT_TYPE_NAME,info.typeName);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_public_account_list,null);
    }

    @Override
    public String getHeadTitle() {
        return "收款方信息";
    }
}
