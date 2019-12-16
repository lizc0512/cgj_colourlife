package com.tg.point.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tg.coloursteward.R;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.UserMessageConstant;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.point.adapter.MultiCollegeAdapter;
import com.tg.point.entity.UserIdInforEntity;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.tg.point.activity.GivenPointAmountActivity.GIVENMOBILE;
import static com.tg.point.activity.GivenPointAmountActivity.TYPE;
import static com.tg.point.activity.PointTransactionListActivity.POINTTPANO;

/**
 * @name ${lizc}
 * @class name：com.tg.point.activity;
 * @class describe
 * @anthor ${lizc} QQ:510906433
 * @time 2019/12/05 10:48
 * @change
 * @chang time
 * @class describe 转账给多个同事选择页面
 */
public class MultiCollegeActivity extends BaseActivity implements View.OnClickListener {
    private ImageView mIvBack;
    private TextView mTitle;
    private RecyclerView rv_mulitcollege;
    private MultiCollegeAdapter adapter;
    private String result;
    private int last_times;//剩余次数
    private float last_amount;//剩余金额
    private String pano;
    private String givePhone;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_college);
        initView();
        initData();
    }

    private void initView() {
        mIvBack = findViewById(R.id.iv_base_back);
        mTitle = findViewById(R.id.tv_base_title);
        rv_mulitcollege = findViewById(R.id.rv_mulitcollege);
        rv_mulitcollege.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mTitle.setText("转账给同事");
        mIvBack.setOnClickListener(this);
        if (null != getIntent()) {
            result = getIntent().getStringExtra("json");
            pano = getIntent().getStringExtra("pano");
            givePhone = getIntent().getStringExtra("givePhone");
            type = getIntent().getStringExtra("type");
            last_times = getIntent().getIntExtra("last_times", 0);
            last_amount = getIntent().getFloatExtra("last_amount", 0);
        }
    }

    private void initData() {
        UserIdInforEntity userIdInforEntity = GsonUtils.gsonToBean(result, UserIdInforEntity.class);
        List<UserIdInforEntity.ContentBean> mList = new ArrayList<>(userIdInforEntity.getContent());
        if (null == adapter) {
            adapter = new MultiCollegeAdapter(R.layout.item_multi_college, mList);
            rv_mulitcollege.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String real_name = mList.get(position).getName();
                String icon = mList.get(position).getPortrait();
                String uuid = mList.get(position).getAccount_uuid();
                Intent amount_Intent = new Intent(MultiCollegeActivity.this, GivenPointAmountActivity.class);
                amount_Intent.putExtra(POINTTPANO, pano);
                amount_Intent.putExtra(GIVENMOBILE, givePhone);
                amount_Intent.putExtra(TYPE, type);
                amount_Intent.putExtra(GivenPointAmountActivity.USERPORTRAIT, icon);
                amount_Intent.putExtra(GivenPointAmountActivity.USERID, uuid);
                amount_Intent.putExtra(GivenPointAmountActivity.USERNAME, real_name);
                amount_Intent.putExtra(GivenPointAmountActivity.LASTAMOUNT, last_amount);
                amount_Intent.putExtra(GivenPointAmountActivity.LASTTIME, last_times);
                startActivity(amount_Intent);
            }
        });
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
        }
    }

    @Subscribe
    public void onEvent(Object event) {
        final Message message = (Message) event;
        switch (message.what) {
            case UserMessageConstant.POINT_SUCCESS_RETURN:
            case UserMessageConstant.POINT_CONTINUE_GIVEN:
                finish();
                break;
        }
    }
}