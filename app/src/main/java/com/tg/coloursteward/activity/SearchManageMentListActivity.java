package com.tg.coloursteward.activity;

import android.os.Bundle;
import android.view.View;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;

public class SearchManageMentListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_search_manage_ment_list,null);
    }

    @Override
    public String getHeadTitle() {
        return "应用";
    }
}
