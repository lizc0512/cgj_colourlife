
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
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.db.bean.PushMsg;
import com.youmai.hxsdk.db.dao.PushMsgDao;
import com.youmai.hxsdk.db.manager.GreenDbManager;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.List;

public class PushMsgDetailActivity extends SdkBaseActivity {
    private static final String TAG = PushMsgDetailActivity.class.getSimpleName();

    public static final String TYPE = "type";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String TIME = "time";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_msg_detail);
        initTitle();
        initView();
    }


    private void initTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        int type = getIntent().getIntExtra(TYPE, 0);

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

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initView() {
        TextView tv_msg_title = (TextView) findViewById(R.id.tv_msg_title);
        TextView tv_content = (TextView) findViewById(R.id.tv_content);
        TextView tv_time = (TextView) findViewById(R.id.tv_time);

        String title = getIntent().getStringExtra(TITLE);
        String content = getIntent().getStringExtra(CONTENT);
        String time = getIntent().getStringExtra(TIME);

        tv_msg_title.setText(title);
        tv_content.setText(content);
        tv_time.setText(time);


    }


}
