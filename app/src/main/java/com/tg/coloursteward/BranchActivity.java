package com.tg.coloursteward;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.coloursteward.adapter.BranchAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.FamilyInfo;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.PullRefreshListView;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.tg.coloursteward.zxing.decoding.Intents;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 分支
 */
public class BranchActivity extends BaseActivity {
    public static final String FAMILY_INFO="familyInfo";
    private PullRefreshListView pullListView;
    private FamilyInfo info;
    private String id;
    // 组织标题
    private LinearLayout lin_contact_header;
    // 组织架构标题列表
    private List<FamilyInfo> orgTitleDatas = new ArrayList<FamilyInfo>();
    private ArrayList<FamilyInfo> familyList = new ArrayList<FamilyInfo>();
    private BranchAdapter adapter;
    private boolean clickable = true;
    private String corpUuid;
    private String orgType ="彩生活集团";
    private AuthAppService authAppService;
    private String accessToken;

    @Override
    protected boolean handClickEvent(View v) {
        switch (v.getId()) {
            case R.id.back_layout://左上角返回按钮点击处理事件
                lin_back();
                break;
        }
        return super.handClickEvent(v);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent  = getIntent();
        if(intent != null){
            info=(FamilyInfo) intent.getSerializableExtra(FAMILY_INFO);
        }
        id = info.id;
        initView();
        orgTitleDatas.add(info);
        addOrgTitle();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        // 组织标题
        lin_contact_header = (LinearLayout) findViewById(R.id.lin_contact_header);

        pullListView = (PullRefreshListView) findViewById(R.id.pull_listview1);
        pullListView.setEnableMoreButton(false);
        pullListView.setDividerHeight(0);
        pullListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                FamilyInfo info = familyList.get(position);
                    /*if (clickable) {
                        orgTitleDatas.add(info);
                        addOrgTitle();
                    }*/
                Intent intent = new Intent(BranchActivity.this, DataShowActivity.class);
                intent.putExtra("id", info.id);
                intent.putExtra("name", info.name);
                intent.putExtra("orgType", info.orgType);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {

            @Override
            public void refreshData(PullRefreshListView t,
                                    boolean isLoadMore, Message msg, String response) {
              // Log.d("printLog","response="+response);
                int code = HttpTools.getCode(response);
                String message = HttpTools.getMessageString(response);
                if(code == 0){
                    familyList.clear();
                    String content = HttpTools.getContentString(response);
                    try {
                        if(content.length() > 0){
                            Object 	json = new JSONTokener(content).nextValue();
                            if(json instanceof  JSONArray){
                                JSONArray jsonString = HttpTools.getContentJsonArray(response);
                                if (jsonString.length() > 0) {
                                    ResponseData data = HttpTools.getResponseContent(jsonString);
                                    FamilyInfo item;
                                    for (int i = 0; i < data.length; i++) {
                                        item = new FamilyInfo();
                                        item.id = data.getString(i, "orgUuid");
                                        item.name = data.getString(i, "name");
                                        item.orgType = orgType;
                                        familyList.add(item);
                                    }
                                    clickable = true;
                                    adapter.notifyDataSetChanged();
                                }
                            } else{
                                ToastFactory.showToast(BranchActivity.this,message);
                            }
                        }else {
                            ToastFactory.showToast(BranchActivity.this,message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onLoadingMore(PullRefreshListView t, Handler hand, int pageIndex) {
                RequestConfig config = new RequestConfig(BranchActivity.this,PullRefreshListView.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                try {
                    String org = URLEncoder.encode(orgType,"UTF-8");
                    params.put("token",accessToken);
                    params.put("orgType",org);
                    params.put("corpId",corpUuid);
                    params.put("status", 0);
                    params.put("familyTypeId", 0);
                    params.put("parentId", id);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/orgms/org/batchList",config, params);

            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                RequestConfig config = new RequestConfig(BranchActivity.this,PullRefreshListView.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                try {
                    //Log.d("printLog","id="+id);
                    //Log.d("printLog","orgType="+orgType);
                    //Log.d("printLog","accessToken="+accessToken);
                    //Log.d("printLog","corpUuid="+corpUuid);
                    String org = URLEncoder.encode(orgType,"UTF-8");
                    params.put("token",accessToken);
                    params.put("orgType",org);
                    params.put("corpId",corpUuid);
                    params.put("status", 0);
                    params.put("familyTypeId", 0);
                    params.put("parentId", id);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/orgms/org/batchList",config, params);
            }
        });
        adapter = new BranchAdapter(this, familyList);
        pullListView.setAdapter(adapter);
        adapter.setNetBranchRequestListener(new BranchAdapter.NetBranchRequestListener() {
            @Override
            public void onNext(FamilyInfo info) {
                if(info.orgType.equals("彩生活集团")){
                    orgType = "大区";
                }else if(info.orgType.equals("大区")){
                    orgType = "事业部";
                }else if(info.orgType.equals("事业部")){
                    orgType = "小区";
                }
                    if (clickable) {
                        orgTitleDatas.add(info);
                        addOrgTitle();
                    }
            }
        });
        //pullListView.performLoading();
        //getToken();
    }
    /**
     * 得到token值
     */
    private void getToken() {
        corpUuid = Tools.getStringValue(this, Contants.storage.CORPID);
        Date dt = new Date();
        Long time = dt.getTime();
        String expireTime = Tools.getStringValue(BranchActivity.this, Contants.storage.APPAUTHTIME);
        accessToken = Tools.getStringValue(BranchActivity.this, Contants.storage.APPAUTH);
        if (StringUtils.isNotEmpty(expireTime)) {
            if (Long.parseLong(expireTime) <= time) {//token过期
                getAuthAppInfo();
            } else {
                pullListView.performLoading();//刷新列表
            }
        } else {
            getAuthAppInfo();
        }
    }
    private void addOrgTitle() {
        clickable = false;
        int lenth = orgTitleDatas.size();
        lin_contact_header.removeAllViews();

        for (int i = 0; i < lenth; i++) {

            final int position = i;
            FamilyInfo info = orgTitleDatas.get(i);
            if (i > 0) {
                ImageView imageView = new ImageView(BranchActivity.this);
                imageView.setBackgroundResource(R.drawable.arrow1);
                lin_contact_header.addView(imageView);
            }

            TextView textView = new TextView(BranchActivity.this);
            textView.setText(info.name);
            textView.setTextSize(16);

            if (i == lenth - 1) {
                id = info.id;
                //pullListView.performLoading();
                getToken();
                textView.setTextColor(getResources()
                        .getColor(R.color.form_edit));
            } else {
                textView.setTextColor(getResources().getColor(
                        R.color.home_tab_txt_sel_color));
                textView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        switch (position) {
                            case 0:
                                orgType = "彩生活集团";
                                break;
                            case 1:
                                orgType = "大区";
                                break;
                            case 2:
                                orgType = "事业部";
                                break;
                        }
                        orgTitleDatas = orgTitleDatas.subList(0, position + 1);
                        addOrgTitle();

                    }
                });
            }

            lin_contact_header.addView(textView);
        }

    }

    private void lin_back() {
        if (orgTitleDatas.size() > 1) {
            orgTitleDatas = orgTitleDatas.subList(0, orgTitleDatas.size() - 1);
            addOrgTitle();
        } else if (orgTitleDatas.size() == 1) {
            onBackPressed();
        }
    }
    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_branch,null);
    }

    @Override
    public String getHeadTitle() {
        return "架构";
    }
    /**
     * 获取token
     * sectet
     */
    private void getAuthAppInfo() {
        if(authAppService == null){
            authAppService = new AuthAppService(BranchActivity.this);
        }
        authAppService.getAppAuth(new GetTwoRecordListener<String, String>() {
            @Override
            public void onFinish(String jsonString, String data2,String data3) {
                int code = HttpTools.getCode(jsonString);
                if(code == 0){
                    JSONObject content = HttpTools.getContentJSONObject(jsonString);
                    if(content.length() > 0){
                        try {
                            accessToken = content.getString("accessToken");
                            String expireTime = content.getString("expireTime");
                            Tools.saveStringValue(BranchActivity.this,Contants.storage.APPAUTH,accessToken);
                            Tools.saveStringValue(BranchActivity.this,Contants.storage.APPAUTHTIME,expireTime);
                            pullListView.performLoading();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }

            @Override
            public void onFailed(String Message) {

            }
        });
    }
}
