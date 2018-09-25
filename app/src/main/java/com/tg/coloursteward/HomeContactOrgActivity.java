package com.tg.coloursteward;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.coloursteward.adapter.FamilyAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.FamilyInfo;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.PullRefreshListView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * 组织架构和会员
 *
 * @author Administrator
 */
public class HomeContactOrgActivity extends BaseActivity {
    public static final String FAMILY_INFO = "familyInfo";
    private PullRefreshListView pullListView1;
    private FamilyInfo info;
    private String id;
    // 组织标题
    private LinearLayout lin_contact_header;
    // 组织架构标题列表
    private List<FamilyInfo> orgTitleDatas = new ArrayList<FamilyInfo>();
    private ArrayList<FamilyInfo> familyList = new ArrayList<FamilyInfo>();
    private FamilyAdapter adapter;
    private boolean clickable = true;

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
        Intent intent = getIntent();
        if (intent != null) {
            info = (FamilyInfo) intent.getSerializableExtra(FAMILY_INFO);
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

        pullListView1 = (PullRefreshListView) findViewById(R.id.pull_listview1);
        pullListView1.setEnableMoreButton(false);
        pullListView1.setDividerHeight(0);
        pullListView1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FamilyInfo info = familyList.get(position);
                String type = info.type;
                if ("org".equals(type)) {
                    if (clickable) {
                        orgTitleDatas.add(info);
                        addOrgTitle();
                    }
                } else if ("user".equals(type)) {
                    Intent intent = new Intent(HomeContactOrgActivity.this, EmployeeDataActivity.class);
                    intent.putExtra(EmployeeDataActivity.CONTACTS_ID, info.username);
                    startActivity(intent);
                }
            }
        });

        pullListView1.setOnLoadingListener(new OnLoadingListener<PullRefreshListView>() {

            @Override
            public void refreshData(PullRefreshListView t,
                                    boolean isLoadMore, Message msg, String response) {
                familyList.clear();
                JSONArray jsonString = HttpTools.getContentJsonArray(response);
                if (jsonString != null) {
                    ResponseData data = HttpTools.getResponseContent(jsonString);
                    FamilyInfo item;
                    for (int i = 0; i < data.length; i++) {
                        item = new FamilyInfo();
                        item.id = data.getString(i, "id");
                        item.type = data.getString(i, "type");
                        item.username = data.getString(i, "username");
                        item.avatar = data.getString(i, "avatar");
                        item.name = data.getString(i, "name");
                        if (item.type.equals("org")) {
                            item.sortLetters = "O";
                        } else {
                            item.sortLetters = "U";
                        }
                        familyList.add(item);
                    }
                    clickable = true;
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onLoadingMore(PullRefreshListView t, Handler hand, int pageIndex) {
                RequestConfig config = new RequestConfig(HomeContactOrgActivity.this, PullRefreshListView.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                if (!id.equals("-1")) {
                    params.put("orgID", id);
                }
                String corp_id = Tools.getStringValue(HomeContactOrgActivity.this, Contants.storage.CORPID);
                params.put("corpId", corp_id);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/txl2/contacts/childDatas", config, params);

            }

            @Override
            public void onLoading(PullRefreshListView t, Handler hand) {
                RequestConfig config = new RequestConfig(HomeContactOrgActivity.this, PullRefreshListView.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                if (!id.equals("-1")) {
                    params.put("orgID", id);
                }
                String corp_id = Tools.getStringValue(HomeContactOrgActivity.this, Contants.storage.CORPID);
                params.put("corpId", corp_id);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/txl2/contacts/childDatas", config, params);
            }
        });
        adapter = new FamilyAdapter(this, familyList);
        pullListView1.setAdapter(adapter);
        pullListView1.performLoading();


    }

    private void addOrgTitle() {
        clickable = false;
        int lenth = orgTitleDatas.size();
        lin_contact_header.removeAllViews();

        for (int i = 0; i < lenth; i++) {

            final int position = i;
            FamilyInfo info = orgTitleDatas.get(i);
            if (i > 0) {
                ImageView imageView = new ImageView(HomeContactOrgActivity.this);
                imageView.setBackgroundResource(R.drawable.arrow1);
                lin_contact_header.addView(imageView);
            }

            TextView textView = new TextView(HomeContactOrgActivity.this);
            textView.setText(info.name);
            textView.setTextSize(16);

            if (i == lenth - 1) {
                id = info.id;
                pullListView1.performLoading();
                textView.setTextColor(getResources()
                        .getColor(R.color.form_edit));
            } else {
                textView.setTextColor(getResources().getColor(
                        R.color.home_tab_txt_sel_color));
                textView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
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
        return getLayoutInflater().inflate(R.layout.activity_home_contact_org, null);
    }

    @Override
    public String getHeadTitle() {
        headView.setListenerBack(singleListener);
        return "通讯录";
    }

}

