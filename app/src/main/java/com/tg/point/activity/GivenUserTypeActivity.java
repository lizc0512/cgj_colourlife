package com.tg.point.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;

import static com.tg.point.activity.PointTransactionListActivity.POINTTPANO;

/**
 * @name ${lizc}
 * @class name：com.tg.point.activity
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/12/03 10:48
 * @change
 * @chang time
 * @class describe 赠送饭票用户类型页面
 */
public class GivenUserTypeActivity extends BaseActivity implements View.OnClickListener {
    private ImageView mBack;
    private TextView mTitle;
    private RelativeLayout rl_usertype_college;
    private RelativeLayout rl_usertype_czyuser;
    private String mobilePhone;
    private String mPano;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_given_user_type);
        mBack = findViewById(R.id.iv_base_back);
        mTitle = findViewById(R.id.tv_base_title);
        rl_usertype_college = findViewById(R.id.rl_usertype_college);
        rl_usertype_czyuser = findViewById(R.id.rl_usertype_czyuser);
        mBack.setOnClickListener(this);
        rl_usertype_college.setOnClickListener(this);
        rl_usertype_czyuser.setOnClickListener(this);
        mTitle.setText("赠送");
        if (null != getIntent()) {
            mobilePhone = getIntent().getStringExtra(GivenPointAmountActivity.GIVENMOBILE);
            mPano = getIntent().getStringExtra(POINTTPANO);
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
                finish();
                break;
            case R.id.rl_usertype_college:
                Intent intent = new Intent(this, GivenPointMobileActivity.class);
                intent.putExtra(GivenPointAmountActivity.GIVENMOBILE, mobilePhone);
                intent.putExtra(POINTTPANO, mPano);
                intent.putExtra(GivenPointAmountActivity.TYPE, "cgj-cgj");
                startActivity(intent);
                break;
            case R.id.rl_usertype_czyuser:
                Intent it = new Intent(this, GivenPointMobileActivity.class);
                it.putExtra(GivenPointAmountActivity.GIVENMOBILE, mobilePhone);
                it.putExtra(POINTTPANO, mPano);
                it.putExtra(GivenPointAmountActivity.TYPE, "cgj-czy");
                startActivity(it);
                break;
        }
    }
}
