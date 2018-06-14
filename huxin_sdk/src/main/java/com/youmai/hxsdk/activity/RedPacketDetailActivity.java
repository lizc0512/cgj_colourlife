package com.youmai.hxsdk.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.adapter.RedStatusAdapter;

/**
 * 作者：create by YW
 * 日期：2017.06.07 11:42
 * 描述：Red packet
 */
public class RedPacketDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = RedPacketDetailActivity.class.getSimpleName();

    private TextView tv_back;
    private TextView tv_title;
    private TextView tv_right;

    private ImageView img_head;
    private TextView tv_name;
    private TextView tv_red_title;
    private TextView tv_money;

    private TextView tv_info;
    private TextView tv_status;
    private RecyclerView recycler_view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_red_packet_detail);
        initView();
        loadRedPacket();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.tv_right);
        img_head = (ImageView) findViewById(R.id.img_head);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_red_title = (TextView) findViewById(R.id.tv_red_title);
        tv_money = (TextView) findViewById(R.id.tv_money);

        tv_info = (TextView) findViewById(R.id.tv_info);
        tv_status = (TextView) findViewById(R.id.tv_status);

        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        RedStatusAdapter adapter = new RedStatusAdapter(this);

        recycler_view.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setAdapter(adapter);

    }


    private void loadRedPacket() {

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
    }
}
