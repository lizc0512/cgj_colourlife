
package com.youmai.hxsdk.push.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;


import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.PushMsg;
import com.youmai.hxsdk.db.bean.RemindMsg;
import com.youmai.hxsdk.db.dao.PushMsgDao;
import com.youmai.hxsdk.db.dao.RemindMsgDao;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.FileUtils;
import com.youmai.hxsdk.utils.ListUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PushMsgTypeActivity extends AppCompatActivity {
    private static final String TAG = PushMsgTypeActivity.class.getSimpleName();

    public static final int DEL_REMIND = 100;

    private RecyclerView mRecyclerView;
    private MsgTypeAdapter mAdapter;

    private TextView tv_empty;
    private Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hx_activity_msg);
        mContext = this;

        initTitle();
        initView();

        /*if (!AppUtils.getBooleanSharedPreferences(this, "test_msg", false)) {
            testData();
            AppUtils.setBooleanSharedPreferences(this, "test_msg", true);
        }*/

        initData();
    }


    private void initTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("消息中心");

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
        mAdapter = new MsgTypeAdapter(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                layoutManager.getOrientation()));
        mRecyclerView.setAdapter(mAdapter);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DEL_REMIND && resultCode == Activity.RESULT_OK) {
            initData();
        }
    }

    private void initData() {
        PushMsgDao pushMsgDao = GreenDBIMManager.instance(this).getPushMsgDao();
        //List<PushMsg> list = pushMsgDao.queryBuilder().build().list();
        //Collections.reverse(list);  //List倒序

        List<PushMsg> list = pushMsgDao.queryBuilder().orderDesc(PushMsgDao.Properties.Rec_time).build().list();

        List<MsgType> typeList = new ArrayList<>();

        boolean isHave0 = false;
        boolean isHave1 = false;
        boolean isHave2 = false;
        boolean isHave3 = false;

        for (PushMsg item : list) {
            if (item.getMsg_type() == 0) {
                if (!isHave0) {

                    MsgType type = new MsgType();
                    type.setMsgType(item.getMsg_type());
                    type.setRecTime(item.getRec_time());
                    type.setTitle(item.getTitle());

                    typeList.add(type);
                    isHave0 = true;
                }
            } else if (item.getMsg_type() == 1) {
                if (!isHave1) {

                    MsgType type = new MsgType();
                    type.setMsgType(item.getMsg_type());
                    type.setRecTime(item.getRec_time());
                    type.setTitle(item.getTitle());


                    typeList.add(type);
                    isHave1 = true;
                }
            } else if (item.getMsg_type() == 2) {
                if (!isHave2) {

                    MsgType type = new MsgType();
                    type.setMsgType(item.getMsg_type());
                    type.setRecTime(item.getRec_time());
                    type.setTitle(item.getTitle());

                    typeList.add(type);
                    isHave2 = true;
                }
            } else if (item.getMsg_type() == 3) {
                if (!isHave3) {

                    MsgType type = new MsgType();
                    type.setMsgType(item.getMsg_type());
                    type.setRecTime(item.getRec_time());
                    type.setTitle(item.getTitle());

                    typeList.add(type);

                    isHave3 = true;
                }
            }
        }


        RemindMsgDao remindMsgDao = GreenDBIMManager.instance(this).getRemindMsgDao();
        List<RemindMsg> remindMsgList = remindMsgDao.queryBuilder().orderDesc(RemindMsgDao.Properties.RecTime).build().list();

        if (remindMsgList != null && remindMsgList.size() > 0) {
            RemindMsg item = remindMsgList.get(0);
            MsgType type = new MsgType();
            type.setMsgType(100);   //呼信提醒 固定 100
            type.setRecTime(item.getRecTime());
            String remark = item.getRemark();
            if (TextUtils.isEmpty(remark)) {
                remark = "暂无备注";
            }
            type.setTitle(remark);

            typeList.add(type);
        } else {
            //  String savedStr = timeFromWeekday + "@" + remind;
            String savedStr = AppUtils.getStringSharedPreferences(mContext, "last_set_remind", "");
            if (!TextUtils.isEmpty(savedStr)) {
                String strs[] = savedStr.split("@");
                if (strs.length == 3) {
                    MsgType type = new MsgType();
                    type.setMsgType(100);   //呼信提醒 固定 100
                    type.setRecTime(Long.parseLong(strs[0]));
                    String remark = strs[1];

                    if (TextUtils.isEmpty(remark)) {
                        remark = "暂无备注";
                    }

                    type.setTitle(remark);
                    typeList.add(type);
                }
            } else {
                MsgType type = new MsgType();
                type.setMsgType(100);   //呼信提醒 固定 100
                String remark = "暂无备注";
                type.setTitle(remark);

                typeList.add(type);
            }

        }


        mAdapter.setList(typeList);

        if (ListUtils.isEmpty(typeList)) {
            tv_empty.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

    }


}
