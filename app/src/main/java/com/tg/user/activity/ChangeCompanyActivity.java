package com.tg.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.entity.CropListEntity;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.user.adapter.ChangeCompanyAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @name lizc
 * @class name：com.tg.user.activity
 * @class describe
 * @anthor lizc QQ:510906433
 * @time 2020/5/23 9:57
 * @change
 * @chang time
 * @class describe
 */
public class ChangeCompanyActivity extends BaseActivity {

    private RecyclerView rv_change_company;
    private ChangeCompanyAdapter adapter;
    private List<CropListEntity.ContentBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        rv_change_company = findViewById(R.id.rv_change_company);
        rv_change_company.setLayoutManager(new LinearLayoutManager(this));

    }

    private void initData() {
        String result = spUtils.getStringData(SpConstants.storage.CORPDATA, "");
        CropListEntity bean = GsonUtils.gsonToBean(result, CropListEntity.class);
        list.addAll(bean.getContent());
        adapter = new ChangeCompanyAdapter(this, list);
        rv_change_company.setAdapter(adapter);
        adapter.setCallBack(position -> {
            setHandle(position);
        });
    }

    private void setHandle(int pos) {
        String name = list.get(pos).getName();
        String uuid = list.get(pos).getUuid();
        DialogFactory.getInstance().showDoorDialog(this, v -> {
                    spUtils.saveStringData(SpConstants.storage.CORPID, uuid);
                    spUtils.saveStringData(SpConstants.storage.CORPNAME, name);
                    Intent it = new Intent();
                    it.putExtra("companyName", name);
                    setResult(2001, it);
                    finish();
                }, null, 1, "切换到" + name + "该组织架构下，APP的所有配置将根据你所在组织权限重新进行配置",
                "切换", "取消");
    }

    @Override
    public View getContentView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_change_company, null);
    }

    @Override
    public String getHeadTitle() {
        return "切换组织";
    }
}
