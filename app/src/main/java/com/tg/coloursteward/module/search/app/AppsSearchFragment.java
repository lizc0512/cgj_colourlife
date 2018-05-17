package com.tg.coloursteward.module.search.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.tg.coloursteward.DoorActivity;
import com.tg.coloursteward.PublicAccountActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.module.search.GlobalSearchAdapter;
import com.tg.coloursteward.module.search.SearchFragment;
import com.tg.coloursteward.util.AuthTimeUtils;
import com.youmai.hxsdk.entity.cn.SearchContactBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srsm on 2017/8/24.
 */
public class AppsSearchFragment<T extends Parcelable> extends SearchFragment implements
        LoaderManager.LoaderCallbacks<List<T>>, GlobalSearchAdapter.GlobalSearchAdapterListener {

    private static final String TAG = AppsSearchFragment.class.getSimpleName();
    private final int GLOBAL_SEARCH_LOADER_ID = 1;
    private RecyclerView mRecyclerView;
    private GlobalSearchAdapter mGlobalSearchAdapter;
    private ArrayList<T> mMoreSearchContactsList = new ArrayList<T>();
    private ArrayList<T> mPreLoadList = new ArrayList<T>();
    private Handler mHandler = new Handler();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.global_search_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mGlobalSearchAdapter = new GlobalSearchAdapter(getActivity());
        mGlobalSearchAdapter.setGlobalSearchAdapterListener(this);
        mGlobalSearchAdapter.setHeadTitle(getString(R.string.hx_common_app_record));
        mGlobalSearchAdapter.setTailTitle(getString(R.string.hx_common_view_more_app_record));
        mRecyclerView.setAdapter(mGlobalSearchAdapter);

        startLoading();
    }

    public void reset() {
        mGlobalSearchAdapter.notifyDataSetChanged();
    }

    protected void startLoading() {
        Log.d(TAG, "startLoading");
        getLoaderManager().initLoader(GLOBAL_SEARCH_LOADER_ID, null, this);
    }

    @Override
    protected void reloadData() {
        Log.d(TAG, "reloadData");
        getLoaderManager().getLoader(GLOBAL_SEARCH_LOADER_ID).startLoading();
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader");
        AppsGlobalSearchLoader loader = new AppsGlobalSearchLoader(getActivity());
        loader.setQuery(getQueryString());
        return loader;
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.d(TAG, "onLoaderReset");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<T>> loader, List<T> data) {
        Log.d(TAG, "onLoadFinished");
        mGlobalSearchAdapter.setList(data);
    }


    @Override
    public void onItemClick(Object item) {
        SearchContactBean info = (SearchContactBean) item;
        if (info.getClientCode().equals("smkm")) {//扫码开门
            startActivity(new Intent(getActivity(), DoorActivity.class));
        } else if (info.getClientCode().equals("dgzh")) {//对公账户
            startActivity(new Intent(getActivity(), PublicAccountActivity.class));
        } else {
            if (info.getInfo() != "") {
                AuthTimeUtils mAuthTimeUtils = new AuthTimeUtils();
                mAuthTimeUtils.IsAuthTime(getActivity(), info.getInfo(),
                        info.getClientCode(), info.getOauthType(), info.getDeveloperCode(), "");
            }
        }
    }

}
