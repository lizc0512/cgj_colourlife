package com.youmai.hxsdk.keep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.http.HttpConnector;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.view.refresh.OnNextPageListener;
import com.youmai.hxsdk.view.refresh.RefreshRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.tencent.open.utils.Global.getContext;

public class KeepActivity extends AppCompatActivity {

    private static final String TAG = KeepActivity.class.getSimpleName();

    public static final int DEL_CODE = 100;

    private RefreshRecyclerView mRecyclerView;
    private KeepAdapter mAdapter;

    private View tv_empty;

    private int mPageIndex = 1;
    private boolean isRefresh;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep);

        initTitle();
        initView();

        onKeepRefresh();
        mRecyclerView.setRefreshing(true);  //刷新动画

    }


    private void initTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("收藏");

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initView() {
        tv_empty = findViewById(R.id.tv_empty);
        mRecyclerView = (RefreshRecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new KeepAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        //设置上拉刷新回调
        mRecyclerView.setOnNextPageListener(new OnNextPageListener() {
            @Override
            public void onNextPage() {
                if (!isRefresh) {
                    onKeepRefresh();
                }
            }
        });

        //设置下拉刷新回调
        mRecyclerView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isRefresh) {
                    onKeepRefresh();
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.hx_keep_shape_rectangle));
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mRecyclerView.setRefreshEnable(false);//关闭下拉刷新
        mRecyclerView.setLoadMoreEnable(true);

        mAdapter.setOnEmptyCollectListener(new KeepAdapter.IEmptyCollect() {
            @Override
            public void onRefresh() {
                tv_empty.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
        });

    }

    public void startForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DEL_CODE && resultCode == Activity.RESULT_OK) {
            String ids = data.getStringExtra(KeepMapActivity.KEEP_ID);
            mAdapter.removeItem(ids);
        }
    }

    private void onKeepRefresh() {

        isRefresh = true;

        String url = AppConfig.COLLECT_QUERY;
        ReqKeepBean req = new ReqKeepBean(this);
        req.setPage(mPageIndex);

        HttpConnector.httpPost(url, req.getParams(), new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String m = jsonObject.optString("m");
                    String s = jsonObject.optString("s");

                    RespBaseBean bean = new RespBaseBean();
                    bean.setM(m);
                    bean.setS(s);

                    if (bean.isSuccess()) {
                        JSONObject dataJson = jsonObject.optJSONObject("d");
                        if (dataJson != null) {
                            JSONArray dataArray = dataJson.optJSONArray("datas");

                            JSONObject pageResult = dataJson.optJSONObject("queryResult");
                            int curPage = pageResult.optInt("currentPage");
                            int totalPage = pageResult.optInt("pageCount");

                            List<RespKeepBean> list = new ArrayList<>();


                            if (dataArray != null && dataArray.length() > 0) {
                                for (int i = 0; i < dataArray.length(); i++) {
                                    RespKeepBean dBean = new RespKeepBean();
                                    JSONObject json = dataArray.optJSONObject(i);
                                    dBean.setId(json.optString("id"));
                                    dBean.setMsgId(json.optString("msgId"));
                                    dBean.setSendPhone(json.optString("sendPhone"));
                                    dBean.setReceivePhone(json.optString("receivePhone"));
                                    dBean.setMsgType(json.optInt("msgType"));
                                    dBean.setMsgTime(json.optLong("msgTime"));
                                    dBean.setCreateTime(json.optString("createTime"));
                                    dBean.setMsgContent(json.optString("msgContent"));
                                    list.add(dBean);
                                }
                            }

                            if (curPage == totalPage) {
                                setRefreshEnable(false);
                            } else {
                                setRefreshEnable(true);
                                if (mPageIndex < totalPage) {
                                    mPageIndex++;
                                }
                            }

                            setData(list);
                        }
                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                refreshComplete();
                isRefresh = false;
            }
        });

    }


    /**
     * 是否刷新是否可用
     *
     * @param b false:关闭 ,ture：打开
     */
    private void setRefreshEnable(boolean b) {
        mRecyclerView.setRefreshEnable(b); //是否关闭下拉刷新
        mRecyclerView.setLoadMoreEnable(b);  //是否关闭上拉刷新
    }

    /**
     * 关闭刷新动画
     */
    private void refreshComplete() {
        mRecyclerView.setRefreshing(false);  //关闭下拉刷新动画
        mRecyclerView.refreshComplete();//关闭上拉刷新动画
    }

    private void setData(List<RespKeepBean> list) {

        mAdapter.setList(list);
        if (ListUtils.isEmpty(list) && mPageIndex == 1) {
            tv_empty.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

    }


}
