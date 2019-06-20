package com.tg.coloursteward.czy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.tg.user.activity.LoginActivity;
import com.tg.coloursteward.R;

public class ColourLifeEntryActivity extends Activity {

    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colour_life_entry);
        initView();
        initData();
    }

    private void initView() {
        Intent intent = getIntent();
        if (null != intent) {
            String data = intent.getStringExtra("code");
            if (!TextUtils.isEmpty(data)) {
                code = data.substring(5, data.length());
            }
            if (!TextUtils.isEmpty(code)) {
                Intent it = new Intent(ColourLifeEntryActivity.this, LoginActivity.class);
                it.putExtra("czy_code", code);
                startActivity(it);
            }

        }
    }

    private void initData() {

    }

}
