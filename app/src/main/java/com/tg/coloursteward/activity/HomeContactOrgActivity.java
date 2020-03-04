package com.tg.coloursteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.FamilyAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.FamilyInfo;
import com.tg.coloursteward.model.HomeModel;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.Tools;

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
    private FamilyInfo info;
    private String id;
    // 组织标题
    private LinearLayout lin_contact_header;
    // 组织架构标题列表
    private List<FamilyInfo> orgTitleDatas = new ArrayList<>();
    private List<FamilyInfo> familyList = new ArrayList<>();
    private FamilyAdapter adapter;
    private boolean clickable = true;
    private HomeModel homeModel;
    private RecyclerView rv_home_contact;

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
        homeModel = new HomeModel(this);
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
        rv_home_contact = findViewById(R.id.rv_home_contact);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_home_contact.setLayoutManager(layoutManager);
        adapter = new FamilyAdapter(this, familyList);
        rv_home_contact.setAdapter(adapter);
        // 组织标题
        lin_contact_header = findViewById(R.id.lin_contact_header);
    }

    private void initData(boolean isLoading) {
        String corp_id = Tools.getStringValue(HomeContactOrgActivity.this, Contants.storage.CORPID);
        homeModel.getConatctSearch(0, id, corp_id, isLoading, this);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    familyList.clear();
                    adapter.notifyDataSetChanged();
                    JSONArray jsonString = HttpTools.getContentJsonArray(result);
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
                        adapter.list = familyList;
                        adapter.notifyDataSetChanged();
                        if (null != adapter) {
                            adapter.setOnClickCallBack(position -> {
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
                            });
                        }
                    }
                }
                break;
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
                ImageView imageView = new ImageView(HomeContactOrgActivity.this);
                imageView.setBackgroundResource(R.drawable.arrow1);
                lin_contact_header.addView(imageView);
            }

            TextView textView = new TextView(HomeContactOrgActivity.this);
            textView.setText(info.name);
            textView.setTextSize(16);

            if (i == lenth - 1) {
                id = info.id;
                initData(true);
                textView.setTextColor(getResources()
                        .getColor(R.color.form_edit));
            } else {
                textView.setTextColor(getResources().getColor(
                        R.color.home_tab_txt_sel_color));
                textView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
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

