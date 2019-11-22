package com.tg.money.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;

/**
 * @name ${lizc}
 * @class name：com.tg.money.activity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/11/11 10:48
 * @change
 * @chang time
 * @class describe 提现绑定银行卡页面
 */
public class BindCardActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_base_title;
    private ImageView iv_base_back;
    private TextView tv_bindcard_look;
    private TextView tv_bindcard_agree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_card);
        initView();
        initData();
    }

    private void initView() {
        tv_base_title = findViewById(R.id.tv_base_title);
        iv_base_back = findViewById(R.id.iv_base_back);
        tv_bindcard_agree = findViewById(R.id.tv_bindcard_agree);
        tv_bindcard_look = findViewById(R.id.tv_bindcard_look);
        tv_base_title.setText("绑定银行卡");
        iv_base_back.setOnClickListener(this);
        tv_bindcard_look.setOnClickListener(this);
        SpannableString spannableString = new SpannableString(tv_bindcard_agree.getText());
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.color_1ca1f4));
        spannableString.setSpan(colorSpan, 8, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_bindcard_agree.setText(spannableString);
    }

    private void initData() {

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
                finish();
                break;
            case R.id.tv_bindcard_look:
                startActivity(new Intent(this, SupportCardActivity.class));
                break;
        }
    }
}
