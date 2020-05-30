package com.tg.coloursteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.FamilyAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.info.FamilyInfo;
import com.tg.coloursteward.model.HomeModel;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.ResponseData;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * 组织架构和会员
 *
 * @author Administrator
 */
public class HomeContactOrgActivity extends BaseActivity implements OnClickListener {
    public static final String FAMILY_INFO = "familyInfo";
    public static final String DEPARTNAME = "departName";
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
    private String title;
    private ImageView iv_base_back;
    private TextView tv_base_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_contact_org);
        homeModel = new HomeModel(this);
        Intent intent = getIntent();
        if (intent != null) {
            info = (FamilyInfo) intent.getSerializableExtra(FAMILY_INFO);
            title = intent.getStringExtra(DEPARTNAME);
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
        iv_base_back = findViewById(R.id.iv_base_back);
        tv_base_title = findViewById(R.id.tv_base_title);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_home_contact.setLayoutManager(layoutManager);
        adapter = new FamilyAdapter(this, familyList);
        rv_home_contact.setAdapter(adapter);
        // 组织标题
        lin_contact_header = findViewById(R.id.lin_contact_header);
        iv_base_back.setOnClickListener(this);
        tv_base_title.setText(title);
    }

    private void initData(boolean isLoading, String org_uuid) {
        String corp_id = spUtils.getStringData(SpConstants.storage.CORPID, "");
        homeModel.getConatctSearch(0, corp_id, org_uuid, isLoading, this);
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
                            item.orgName = data.getString(i, "orgName");
                            item.jobName = data.getString(i, "jobName");
                            item.mobile = data.getString(i, "mobile");
                            item.email = data.getString(i, "email");
                            item.sex = data.getString(i, "sex");
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
                                    intent.putExtra(EmployeeDataActivity.USERDATA, info);
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
                initData(true, id);
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
        return null;
    }

    @Override
    public String getHeadTitle() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_base_back:
                lin_back();
                break;
        }
    }
}

