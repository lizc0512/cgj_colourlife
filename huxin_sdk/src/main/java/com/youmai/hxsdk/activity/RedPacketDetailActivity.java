package com.youmai.hxsdk.activity;

import android.content.Context;
import android.content.Intent;
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
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.adapter.RedStatusAdapter;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.entity.red.GrabRedPacketResult;
import com.youmai.hxsdk.entity.red.OpenRedPacketResult;
import com.youmai.hxsdk.entity.red.RedPackageDetail;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.im.cache.CacheMsgRedPackage;
import com.youmai.hxsdk.service.SendMsgService;
import com.youmai.hxsdk.utils.GlideRoundTransform;
import com.youmai.hxsdk.utils.GsonUtil;

import java.util.List;

/**
 * 作者：create by YW
 * 日期：2017.06.07 11:42
 * 描述：Red packet
 */
public class RedPacketDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = RedPacketDetailActivity.class.getSimpleName();

    public static final String OPEN_TYPE = "openType";

    public static final String AVATAR = "avatar";
    public static final String NICKNAME = "nickName";
    public static final String VALUE = "value";
    public static final String REDTITLE = "redTitle";
    public static final String REDUUID = "redUuid";
    public static final String MSGBEAN = "msgBean";


    public static final int SINGLE_PACKET = 0;
    public static final int GROUP_PACKET = 1;

    private Context mContext;

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
    private RedStatusAdapter adapter;

    private String avatar;
    private String name;
    private String value;
    private String title;
    private String redUuid;
    private CacheMsgBean uiBean;

    private int openType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_red_packet_detail);
        mContext = this;
        openType = getIntent().getIntExtra(OPEN_TYPE, SINGLE_PACKET);


        avatar = getIntent().getStringExtra(AVATAR);
        name = getIntent().getStringExtra(NICKNAME);
        value = getIntent().getStringExtra(VALUE);
        title = getIntent().getStringExtra(REDTITLE);
        redUuid = getIntent().getStringExtra(REDUUID);
        uiBean = getIntent().getParcelableExtra(MSGBEAN);

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
        tv_title.setText("利是详情");

        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_right.setText("红包记录");
        tv_right.setOnClickListener(this);

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

        String format = getResources().getString(R.string.red_packet_unit2);

        tv_money.setText(String.format(format, value));

        tv_info = (TextView) findViewById(R.id.tv_info);
        //tv_info.setText(R.string.red_packet_back);
        //tv_info.setTextColor(ContextCompat.getColor(this, R.color.gray));

        tv_status = (TextView) findViewById(R.id.tv_status);

        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new RedStatusAdapter(this);

        recycler_view.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setAdapter(adapter);

        if (openType == SINGLE_PACKET) {
            tv_status.setVisibility(View.GONE);
            recycler_view.setVisibility(View.GONE);
        }

    }


    private void loadRedPacket() {
        HuxinSdkManager.instance().openRedPackage(redUuid, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                OpenRedPacketResult bean = GsonUtil.parse(response, OpenRedPacketResult.class);
                if (bean != null && bean.isSuccess()) {
                    int status = bean.getContent().getStatus();  //利是状态：-1已过期 ,0未拆开 ,1未领完 ,2已撤回 ,3已退款 ,4已领完
                    int canOpen = bean.getContent().getCanOpen();
                    int isGrabbed = bean.getContent().getIsGrabbed();

                    if (status == 0 || status == 1) {

                        if (canOpen == 1 && isGrabbed == 0) {
                            HuxinSdkManager.instance().grabRedPackage(redUuid, new IGetListener() {
                                @Override
                                public void httpReqResult(String response) {
                                    GrabRedPacketResult bean = GsonUtil.parse(response, GrabRedPacketResult.class);
                                    if (bean != null && bean.isSuccess()) {
                                        double moneyDraw = bean.getContent().getMoneyDraw();
                                        tv_money.setText(moneyDraw + "");


                                        final CacheMsgRedPackage redPackage = (CacheMsgRedPackage) uiBean.getJsonBodyObj();
                                        redPackage.setRedStatus(CacheMsgRedPackage.RED_PACKET_IS_OPEN_SINGLE);
                                        uiBean.setJsonBodyObj(redPackage);


                                        long id = uiBean.getId();
                                        uiBean.setMsgType(CacheMsgBean.OPEN_REDPACKET);

                                        Intent intent = new Intent(mContext, SendMsgService.class);
                                        intent.putExtra("id", id);
                                        intent.putExtra("data", uiBean);
                                        intent.putExtra("data_from", SendMsgService.FROM_IM);
                                        startService(intent);


                                    }
                                }
                            });
                        }
                    }
                }


                HuxinSdkManager.instance().redPackageDetail(redUuid, new IGetListener() {
                    @Override
                    public void httpReqResult(String response) {
                        RedPackageDetail bean = GsonUtil.parse(response, RedPackageDetail.class);
                        if (bean != null && bean.isSuccess()) {
                            List<RedPackageDetail.ContentBean.PacketListBean> list = bean.getContent().getPacketList();
                            adapter.setList(list);

                            int total = bean.getContent().getNumberTotal();
                            int draw = bean.getContent().getNumberDraw();

                            tv_status.setText("领取" + draw + "/" + total);
                        }
                    }
                });
            }
        });


    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_back) {
            onBackPressed();
        } else if (id == R.id.tv_right) {
            startActivity(new Intent(this, RedPacketHistoryActivity.class));
        }
    }
}
