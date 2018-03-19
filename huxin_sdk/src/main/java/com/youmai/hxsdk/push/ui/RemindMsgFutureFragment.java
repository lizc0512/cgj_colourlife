
package com.youmai.hxsdk.push.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.entity.RespBaseBean;
import com.youmai.hxsdk.http.HttpConnector;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.keep.ReqRemindBean;
import com.youmai.hxsdk.keep.RespRemindBean;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.view.refresh.OnNextPageListener;
import com.youmai.hxsdk.view.refresh.RefreshRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RemindMsgFutureFragment extends Fragment {
    private static final String TAG = RemindMsgFutureFragment.class.getSimpleName();

    public static final int REVIEW_REMIND = 100;

    private RefreshRecyclerView mRecyclerView;
    private RemindFutureAdapter mAdapter;

    private TextView tv_empty;

    private int mPageIndex = 1;
    private boolean isRefresh;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.hx_remind_future_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        onRemindRefresh();
        mRecyclerView.setRefreshing(true);  //刷新动画
    }

    private void initView(View view) {
        tv_empty = (TextView) view.findViewById(R.id.tv_empty);
        mRecyclerView = (RefreshRecyclerView) view.findViewById(R.id.recycler_view);
        mAdapter = new RemindFutureAdapter(this);
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
                    onRemindRefresh();
                }
            }
        });

        //设置下拉刷新回调
        mRecyclerView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isRefresh) {
                    onRemindRefresh();
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        /*mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                layoutManager.getOrientation()));*/

    }

    public void isDel() {
        if (getActivity() instanceof RemindMsgContentActivity) {
            RemindMsgContentActivity activity = (RemindMsgContentActivity) getActivity();
            activity.isDel = true;
        }
    }


    private void onRemindRefresh() {
        isRefresh = true;
        String url = AppConfig.REMIND_QUERY;
        ReqRemindBean req = new ReqRemindBean(getContext());
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

                            List<RespRemindBean> list = new ArrayList<>();

                            if (dataArray != null && dataArray.length() > 0) {
                                for (int i = 0; i < dataArray.length(); i++) {
                                    RespRemindBean dBean = new RespRemindBean();
                                    JSONObject json = dataArray.optJSONObject(i);
                                    dBean.setId(json.optString("id"));
                                    dBean.setMsgId(json.optLong("msgId"));
                                    dBean.setSendPhone(json.optString("sendPhone"));
                                    dBean.setReceivePhone(json.optString("receivePhone"));
                                    dBean.setMsgType(json.optInt("msgType"));
                                    dBean.setMsgTime(json.optLong("msgTime"));
                                    dBean.setCreateTime(json.optLong("createTime"));
                                    dBean.setMsgContent(json.optString("msgContent"));

                                    dBean.setRemindTime(json.optLong("remindTime"));
                                    dBean.setMsgIcon(json.optInt("msgIcon"));
                                    dBean.setRemark(json.optString("remark"));

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

    private void setData(List<RespRemindBean> list) {

        mAdapter.setList(list);
        if (ListUtils.isEmpty(list) && mPageIndex == 1) {
            tv_empty.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

    }


    public void refreshEmpty() {
        tv_empty.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REVIEW_REMIND && resultCode == Activity.RESULT_OK) {
            mPageIndex = 1;
            mAdapter.clearList();
            onRemindRefresh();
        }
    }
}
