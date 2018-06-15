package com.youmai.hxsdk.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.adapter.RedStatusAdapter;
import com.youmai.hxsdk.utils.GlideRoundTransform;

/**
 * 作者：create by YW
 * 日期：2017.06.07 11:42
 * 描述：Red packet
 */
public class RedPacketDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = RedPacketDetailActivity.class.getSimpleName();

    public static final String AVATAR = "avatar";
    public static final String NICKNAME = "nickName";
    public static final String VALUE = "value";
    public static final String REDTITLE = "redTitle";

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

    private String avatar;
    private String name;
    private String value;
    private String title;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_red_packet_detail);
        avatar = getIntent().getStringExtra(AVATAR);
        name = getIntent().getStringExtra(NICKNAME);
        value = getIntent().getStringExtra(VALUE);
        title = getIntent().getStringExtra(REDTITLE);

        initView();
        loadRedPacket();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("红包详情");

        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_right.setText("红包记录");

        img_head = (ImageView) findViewById(R.id.img_head);
        int size = getResources().getDimensionPixelOffset(R.dimen.red_head);
        Glide.with(this).load(avatar)
                .apply(new RequestOptions()
                        .transform(new GlideRoundTransform())
                        .override(size, size)
                        .placeholder(R.drawable.color_default_header)
                        .error(R.drawable.color_default_header)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(img_head);


        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_name.setText(name);

        tv_red_title = (TextView) findViewById(R.id.tv_red_title);
        tv_red_title.setText(title);


        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_money.setText(value);

        tv_info = (TextView) findViewById(R.id.tv_info);
        tv_info.setText(R.string.red_packet_back);
        tv_info.setTextColor(ContextCompat.getColor(this, R.color.gray));

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
        if (id == R.id.tv_back) {
            onBackPressed();
        }
    }
}
