
package com.youmai.hxsdk.push.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.PushMsg;
import com.youmai.hxsdk.db.dao.PushMsgDao;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.List;

public class PushMsgContentActivity extends AppCompatActivity {
    private static final String TAG = PushMsgContentActivity.class.getSimpleName();


    private RecyclerView mRecyclerView;
    private MsgContentAdapter mAdapter;

    private TextView tv_empty;
    private TextView tv_title;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_msg);
        initTitle();
        initView();
        initData();
    }


    private void initTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        tv_title = (TextView) findViewById(R.id.tv_title);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initView() {
        tv_empty = (TextView) findViewById(R.id.tv_empty);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new MsgContentAdapter(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        /*mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                layoutManager.getOrientation()));*/
        mRecyclerView.setAdapter(mAdapter);

    }


    private void initData() {
        int type = getIntent().getIntExtra("type", 0);
        String title = null;
        if (type == 0) {
            title = "系统公告";
        } else if (type == 1) {
            title = "精选活动";
        } else if (type == 2) {
            title = "玩转呼信";
        } else if (type == 3) {
            title = "行业资讯";
        }
        tv_title.setText(title);

        PushMsgDao pushMsgDao = GreenDBIMManager.instance(this).getPushMsgDao();
        List<PushMsg> list = pushMsgDao.queryBuilder()
                .where(PushMsgDao.Properties.Msg_type.eq(type))
                .orderAsc(PushMsgDao.Properties.Rec_time)
                .list();

        mAdapter.setList(list);

        if (ListUtils.isEmpty(list)) {
            tv_empty.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //RecyclerView.getLayoutManager().smoothScrollToPosition(mRecyclerView, null, mAdapter.getItemCount() - 1);
                    mRecyclerView.getLayoutManager().scrollToPosition(mAdapter.getItemCount() - 1);
                }
            }, 100);

        }

    }


}
