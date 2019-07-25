package com.tg.coloursteward.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.model.MicroModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 数据看板
 *
 * @author Administrator
 */
public class DataShowActivity extends BaseActivity implements HttpResponse {
    private MicroModel microModel;
    private TextView tv_datashow_area;
    private TextView tv_datashow_community;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        microModel = new MicroModel(this);
        microModel.getDataShow(0, this);
    }

    private void initView() {
        tv_datashow_area = findViewById(R.id.tv_datashow_area);
        tv_datashow_community = findViewById(R.id.tv_datashow_community);
        String cacheData = spUtils.getStringData(SpConstants.MicroContant.DATASHOW, "");
        if (!TextUtils.isEmpty(cacheData)) {
            setData(cacheData);
        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_data_show, null);
    }

    @Override
    public String getHeadTitle() {
        return "数据看板";
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    spUtils.saveStringData(SpConstants.MicroContant.DATASHOW, result);
                    setData(result);
                }
        }
    }

    private void setData(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String content = jsonObject.getString("content");
            JSONObject jsonContent = new JSONObject(content);
            String area = jsonContent.getString("area");
            String community = jsonContent.getString("community");
            tv_datashow_area.setText(area);
            tv_datashow_community.setText(community);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
