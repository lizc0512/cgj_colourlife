package com.youmai.hxsdk.module.callmsg;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.ChatMsg;
import com.youmai.hxsdk.view.refresh.OnNextPageListener;
import com.youmai.hxsdk.view.refresh.RefreshRecyclerView;

import java.util.ArrayList;

/**
 * 作者：create by YW
 * 日期：2017.02.06 10:32
 * 描述：通话接收到的聊天消息
 */
public class MsgFragment extends Fragment {

    public static final String MSG_LIST = "msgList";

    private RefreshRecyclerView mPullToRefreshView;
    private MsgRecyclerAdapter msgListAdapter;
    private String dstPhone;

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hx_fragment_call_msg, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        rootView = view;
        TextView tvTitle = (TextView) rootView.findViewById(R.id.tv_title);
        tvTitle.setTextSize(18);
        tvTitle.setText(getString(R.string.hx_fragment_msg_title));

        TextView tvBack = (TextView) rootView.findViewById(R.id.tv_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        mPullToRefreshView = (RefreshRecyclerView) rootView.findViewById(R.id.msg_recycler_refresh);

        mPullToRefreshView.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //设置上拉刷新回调
        mPullToRefreshView.setOnNextPageListener(new OnNextPageListener() {
            @Override
            public void onNextPage() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MsgFragment.this.onRefresh();
                    }
                }, 500);
            }
        });

        mPullToRefreshView.setRefreshEnable(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 初始化recycle view界面
     */
    private void initView() {
        dstPhone = HuxinSdkManager.instance().getPhoneNum();

        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mPullToRefreshView.setLayoutManager(manager);

        onRefresh();
    }

    private void onRefresh() {
        ArrayList<ChatMsg> msgList = getActivity().getIntent().getParcelableArrayListExtra(MSG_LIST);
        updateCallMsg(msgList);
    }

    public void updateCallMsg(ArrayList<ChatMsg> list) {

        if (list == null || list.size() == 0) {
            return;
        }
        if (null == msgListAdapter) {
            msgListAdapter = new MsgRecyclerAdapter(getActivity(), dstPhone);
            mPullToRefreshView.setAdapter(msgListAdapter);
        }
        msgListAdapter.updateDataList(list);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!(msgListAdapter != null)) {
            initView();
        }
    }

}
