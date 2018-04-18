package com.tg.coloursteward.module.search;

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
import android.widget.Toast;

import com.tg.coloursteward.EmployeeDataActivity;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.entity.cn.SearchContactBean;

import java.util.ArrayList;

/**
 * Created by srsm on 2017/8/24.
 */
public class ContactsSearchFragment<T extends Parcelable> extends SearchFragment implements
        LoaderManager.LoaderCallbacks<ArrayList<T>>, GlobalSearchAdapter.GlobalSearchAdapterListener {

    private static final String TAG = "YW";//ContactsSearchFragment.class.getSimpleName();

    private final int GLOBAL_SEARCH_LOADER_ID = 2;
    private Handler mHandler = new Handler();
    private ArrayList<T> mPreLoadList = new ArrayList<T>();

    private RecyclerView mRecyclerView;
    private GlobalSearchAdapter mGlobalSearchAdapter;
    private String mQueryString = "";

    public ContactsSearchFragment() {}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.global_search_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mGlobalSearchAdapter = new GlobalSearchAdapter(getActivity());
        mGlobalSearchAdapter.setGlobalSearchAdapterListener(this);
        mGlobalSearchAdapter.setAdapterType(GlobalSearchAdapter.ADAPTER_TYPE_MORE);
        mGlobalSearchAdapter.setHeadTitle(getString(R.string.hx_contacts_search_title));
        mGlobalSearchAdapter.setTailTitle(getString(R.string.hx_contacts_view_more_contacts));
        mRecyclerView.setAdapter(mGlobalSearchAdapter);

        getLoaderManager().initLoader(GLOBAL_SEARCH_LOADER_ID, null, this);
    }

    public void reset() {
        mGlobalSearchAdapter.setAdapterType(GlobalSearchAdapter.ADAPTER_TYPE_MORE);
        mGlobalSearchAdapter.notifyDataSetChanged();
    }

    @Override
    protected void reloadData() {
        Log.d(TAG, "reloadData");
        getLoaderManager().getLoader(GLOBAL_SEARCH_LOADER_ID).startLoading();
    }


    @NonNull
    @Override
    public Loader<ArrayList<T>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader");
        GlobalSearchLoader loader = new ContactsSearchLoader(getContext());
        loader.setConfigQueryParamListener(new GlobalSearchLoader.ConfigQueryParamListener() {
            @Override
            public String getConfigQueryParamString() {
                Log.d(TAG, "onCreateLoader getQueryString");
                return getQueryString();
            }
        });
        loader.setPreLoadCallback(new GlobalSearchLoader.PreLoadCallback() {
            @Override
            public void preLoad(final ArrayList preList) {
                Log.d(TAG, "111" + Thread.currentThread().getName());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "222" + Thread.currentThread().getName());
                        mPreLoadList.addAll(preList);
                        mGlobalSearchAdapter.setArrayList(mPreLoadList);
                    }
                });
            }
        });
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<T>> loader, ArrayList<T> data) {
        Log.d(TAG, "onLoadFinished");
        if (getOnLoadFinishListener() != null) {
            if (data != null && data.size() > 0) {
                getOnLoadFinishListener().onFinishCallback(true, getQueryString());
            } else {
                getOnLoadFinishListener().onFinishCallback(false, getQueryString());
            }
        }
        mGlobalSearchAdapter.setArrayList(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<T>> loader) {
        Log.d(TAG, "onLoaderReset");
    }

    @Override
    public void onItemClick(Object item) {
        Toast.makeText(getContext(), "onItemClick", Toast.LENGTH_SHORT).show();
        String username = ((SearchContactBean) item).getUsername();
        Intent i = new Intent(getActivity(), EmployeeDataActivity.class);
        i.putExtra(EmployeeDataActivity.CONTACTS_ID, username);
        startActivity(i);
    }

    @Override
    public void onMoreItemClick() {
        Toast.makeText(getContext(), "onMoreItemClick", Toast.LENGTH_SHORT).show();
        if (getOnLoadFinishListener() != null) {
            getOnLoadFinishListener().onWhoShowMoreCallback(getTag());
        }

        mGlobalSearchAdapter.setAdapterType(GlobalSearchAdapter.ADAPTER_TYPE_TITLE);
        mGlobalSearchAdapter.notifyDataSetChanged();
    }
}
