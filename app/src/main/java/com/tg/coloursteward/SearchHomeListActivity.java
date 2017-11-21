package com.tg.coloursteward;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;

import com.tg.coloursteward.adapter.HomeHomeListAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.FindHomeListInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.util.AuthTimeUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.PullRefreshListView;
import com.tg.coloursteward.view.PullRefreshListViewFind;
import com.tg.coloursteward.view.dialog.ToastFactory;


import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;

public class SearchHomeListActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    public static final  String KEYWORD= "keyword";
    private String Keyword;
    private PullRefreshListView pullListView;
    private Intent intent;
    private HomeHomeListAdapter adapter;
    private HomeService homeService;
    private ArrayList<FindHomeListInfo> homelistDatas = new ArrayList<FindHomeListInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent  = getIntent();
        if(intent != null){
            Keyword= intent.getStringExtra(KEYWORD);
        }
        if(Keyword == null){
            ToastFactory.showToast(SearchHomeListActivity.this, "参数错误");
            finish();
        }
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        pullListView = (PullRefreshListView) findViewById(R.id.pull_listview);
        adapter = new HomeHomeListAdapter(SearchHomeListActivity.this, homelistDatas);
        pullListView.setAdapter(adapter);
        pullListView.setOnItemClickListener(this);
        pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {
            @Override
            public void refreshData(PullRefreshListView t, boolean isLoadMore, Message msg, String response) {
                homelistDatas.clear();
                String jsonString = HttpTools.getContentString(response);
                if (StringUtils.isNotEmpty(jsonString)) {
                    ResponseData data = HttpTools.getResponseData(jsonString);
                    FindHomeListInfo item;
                    for (int i = 0; i < data.length; i++) {
                        item = new FindHomeListInfo();
                        item.id = data.getInt(i, "id");
                        item.auth_type = data.getInt(i, "auth_type");
                        item.icon = data.getString(i, "ICON");
                        item.owner_name = data.getString(i, "owner_name");
                        item.owner_avatar = data.getString(i, "owner_avatar");
                        item.modify_time = data.getString(i, "homePushTime");
                        item.title = data.getString(i, "title");
                        item.source_id = data.getString(i, "source_id");
                        item.comefrom = data.getString(i, "comefrom");
                        item.url = data.getString(i, "url");
                        item.client_code = data.getString(i, "client_code");
                        homelistDatas.add(item);
                    }
                }else{

                }

            }
            @Override
            public void onLoadingMore(PullRefreshListView t, Handler hand, int pageIndex) {
                try {
                    String key = URLEncoder.encode(Keyword,"UTF-8");
                    RequestConfig config = new RequestConfig(SearchHomeListActivity.this,PullRefreshListView.HTTP_MORE_CODE);
                    config.handler = hand;
                    RequestParams params = new RequestParams();
                    params.put("username", UserInfo.employeeAccount);
                    params.put("keyword",key);
                    params.put("page", pageIndex);
                    params.put("pagesize", PullRefreshListViewFind.PAGER_SIZE);
                    HttpTools.httpGet(Contants.URl.URL_ICETEST,"/push2/homepush" ,config, params);
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                try {
                    String key = URLEncoder.encode(Keyword,"UTF-8");
                    RequestConfig config = new RequestConfig(SearchHomeListActivity.this,PullRefreshListView.HTTP_FRESH_CODE);
                    config.handler = hand;
                    RequestParams params = new RequestParams();
                    params.put("username", UserInfo.employeeAccount);
                    params.put("keyword",key);
                    params.put("page", 1);
                    params.put("pagesize", PullRefreshListViewFind.PAGER_SIZE);
                    HttpTools.httpGet(Contants.URl.URL_ICETEST,"/push2/homepush" ,config, params);
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
            FindHomeListInfo info = homelistDatas.get((int) parent.getAdapter().getItemId(position));
            //getAuth(info.url, info.client_code, String.valueOf(info.auth_type), info.client_code,"");
            AuthTimeUtils mAuthTimeUtils  = new AuthTimeUtils();
            mAuthTimeUtils.IsAuthTime(SearchHomeListActivity.this,info.url, info.client_code, String.valueOf(info.auth_type), info.client_code,"");
        }else{
            return;
        }
    }
    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_search_home_list,null);
    }

    @Override
    public String getHeadTitle() {
        return "消息";
    }
}
