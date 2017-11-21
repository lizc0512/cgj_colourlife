package com.tg.coloursteward;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;

import com.tg.coloursteward.adapter.HomeContactSearchAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.FindContactInfo;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.view.PullRefreshListView;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchContactListActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    public static final  String KEYWORD= "keyword";
    private String Keyword;
    private PullRefreshListView pullListView;
    private Intent intent;
    private HomeContactSearchAdapter adapter;
    private ArrayList<FindContactInfo> contactDatas = new ArrayList<FindContactInfo>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent  = getIntent();
        if(intent != null){
            Keyword= intent.getStringExtra(KEYWORD);
        }
        if(Keyword == null){
            ToastFactory.showToast(SearchContactListActivity.this, "参数错误");
            finish();
        }
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        pullListView = (PullRefreshListView) findViewById(R.id.pull_listview);
        adapter = new HomeContactSearchAdapter(SearchContactListActivity.this, contactDatas);
        pullListView.setAdapter(adapter);
        pullListView.setOnItemClickListener(this);
        pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
            @Override
            public void refreshData(PullRefreshListView t, boolean isLoadMore, Message msg, String response) {
                contactDatas.clear();
                int code = HttpTools.getCode(response);
                if(code == 0){
                    JSONArray jsonArray = HttpTools.getContentJsonArray(response);
                    if(jsonArray != null){
                        ResponseData  data = HttpTools.getResponseContent(jsonArray);
                        if(data.length > 0){
                            FindContactInfo info ;
                            for (int i = 0; i < data.length; i++) {
                                info = new FindContactInfo();
                                info.username = data.getString(i, "username");
                                info.realname = data.getString(i, "realname");
                                info.avatar = data.getString(i, "avatar");
                                info.org_name = data.getString(i, "org_name");
                                info.job_name = data.getString(i, "job_name");
                                contactDatas.add(info);
                            }
                        }else{
                        }
                    }
                }
            }
            @Override
            public void onLoadingMore(PullRefreshListView t, Handler hand, int pageIndex) {
                try {
                    String key = URLEncoder.encode(Keyword,"UTF-8");
                    RequestConfig config = new RequestConfig(SearchContactListActivity.this,PullRefreshListView.HTTP_MORE_CODE);
                    config.handler = hand;
                    RequestParams params = new RequestParams();
                    params.put("keyword",key);
                    HttpTools.httpGet(Contants.URl.URL_ICETEST,"/phonebook/search" ,config, params);
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                try {
                    String key = URLEncoder.encode(Keyword,"UTF-8");
                    RequestConfig config = new RequestConfig(SearchContactListActivity.this,PullRefreshListView.HTTP_FRESH_CODE);
                    config.handler = hand;
                    RequestParams params = new RequestParams();
                    params.put("keyword",key);
                    HttpTools.httpGet(Contants.URl.URL_ICETEST,"/phonebook/search" ,config, params);
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        pullListView.performLoading();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if((int) parent.getAdapter().getItemId(position) != -1){
            FindContactInfo item = contactDatas.get((int) parent.getAdapter().getItemId(position));
            Intent intent = new Intent(SearchContactListActivity.this,EmployeeDataActivity.class);
            intent.putExtra(EmployeeDataActivity.CONTACTS_ID,item.username);
            startActivity(intent);
        }else{
            return;
        }
    }
    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_search_contact_list,null);
    }

    @Override
    public String getHeadTitle() {
        return "联系人";
    }


}
