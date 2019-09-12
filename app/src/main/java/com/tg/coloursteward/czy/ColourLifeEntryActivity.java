package com.tg.coloursteward.czy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.tg.coloursteward.R;
import com.tg.user.activity.LoginActivity;
import com.tg.user.activity.OauthLoginActivity;

public class ColourLifeEntryActivity extends Activity {

    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colour_life_entry);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        if (null != intent) {
            String data = intent.getStringExtra("code");
            String username = intent.getStringExtra("username");
            String portrait = intent.getStringExtra("portrait");
            if (!TextUtils.isEmpty(data)) {
                code = data.substring(5, data.length());
            }
            if (!TextUtils.isEmpty(code)) {
                Intent it = null;
                if (TextUtils.isEmpty(portrait)) {
                    it = new Intent(ColourLifeEntryActivity.this, LoginActivity.class);
                } else {
                    it = new Intent(ColourLifeEntryActivity.this, OauthLoginActivity.class);
                    it.putExtra("czy_name", username);
                    it.putExtra("czy_portrait", portrait);
                }
                it.putExtra("czy_code", code);
                startActivity(it);
                ColourLifeEntryActivity.this.finish();
            }
        }
    }
}
