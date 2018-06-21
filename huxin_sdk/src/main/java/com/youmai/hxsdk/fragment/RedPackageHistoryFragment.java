
package com.youmai.hxsdk.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.adapter.RedPackageRecordAdapter;
import com.youmai.hxsdk.entity.red.SendRedPacketList;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;


public class RedPackageHistoryFragment extends Fragment {
    private static final String TAG = RedPackageHistoryFragment.class.getSimpleName();


    private XRecyclerView mRecyclerView;
    private RedPackageRecordAdapter mAdapter;

    private ArrayList<String> listData;
    private int page = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_red_package_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);
        reqDate();
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new RedPackageRecordAdapter(getContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.LineSpinFadeLoader);
        mRecyclerView.setPullRefreshEnabled(false);

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                reqDate();
            }
        });

        listData = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            listData.add("item" + i);
        }


        mRecyclerView.setAdapter(mAdapter);
    }


    private void reqDate() {
        HuxinSdkManager.instance().redSendPacketList("201806", page, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                SendRedPacketList bean = GsonUtil.parse(response, SendRedPacketList.class);
                if (bean != null && bean.isSuccess()) {
                    List<SendRedPacketList.ContentBean> list = bean.getContent();
                    if (ListUtils.isEmpty(list)) {
                        mRecyclerView.setLoadingMoreEnabled(false);
                    } else {
                        mAdapter.setList(list);
                        page++;
                    }
                    mRecyclerView.loadMoreComplete();
                }

            }
        });
    }


}
