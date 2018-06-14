
package com.youmai.hxsdk.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.adapter.RedPackageRecordAdapter;


public class RedPackageHistoryFragment extends Fragment {
    private static final String TAG = RedPackageHistoryFragment.class.getSimpleName();


    private RecyclerView mRecyclerView;
    private RedPackageRecordAdapter mAdapter;


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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new RedPackageRecordAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);


    }


}
