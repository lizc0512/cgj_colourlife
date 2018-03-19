
package com.youmai.hxsdk.push.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.RemindMsg;
import com.youmai.hxsdk.db.dao.RemindMsgDao;
import com.youmai.hxsdk.db.manager.GreenDBIMManager;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.List;

public class RemindMsgAcceptFragment extends Fragment {
    private static final String TAG = RemindMsgAcceptFragment.class.getSimpleName();


    private RecyclerView mRecyclerView;
    private RemindContentAdapter mAdapter;

    private TextView tv_empty;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.hx_remind_accept_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }


    private void initView(View view) {
        tv_empty = (TextView) view.findViewById(R.id.tv_empty);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mAdapter = new RemindContentAdapter(getContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        /*mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                layoutManager.getOrientation()));*/
        mRecyclerView.setAdapter(mAdapter);

    }


    private void initData() {
        final RemindMsgDao remindMsgDao = GreenDBIMManager.instance(getContext()).getRemindMsgDao();
        List<RemindMsg> list = remindMsgDao.queryBuilder()
                .orderAsc(RemindMsgDao.Properties.RecTime)
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
