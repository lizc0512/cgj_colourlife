package com.tg.setting.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;

/**
 * 乐开-消息通知
 *
 * @author hxg 2019.07.18
 */
public class KeyTodoNoticeActivity extends BaseActivity {
    private TextView tvVersionShort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        tvVersionShort = findViewById(R.id.tv_versionShort);
//        headView.setVisibility(View.GONE);
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_key_todo_notice, null);
    }

    @Override
    public String getHeadTitle() {
        return "消息通知";
    }

}
