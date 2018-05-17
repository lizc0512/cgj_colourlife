package com.tg.coloursteward.module.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.tg.coloursteward.EmployeeDataActivity;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.entity.cn.SearchContactBean;

import java.util.List;

/**
 * Created by srsm on 2017/8/24.
 */
public class ContactsSearchFragment<T extends Parcelable> extends SearchFragment implements
        LoaderManager.LoaderCallbacks<List<T>>, GlobalSearchAdapter.GlobalSearchAdapterListener {

    public static final String TAG = ContactsSearchFragment.class.getSimpleName();

    private final int GLOBAL_SEARCH_LOADER_ID = 2;

    private RecyclerView mRecyclerView;
    private GlobalSearchAdapter mGlobalSearchAdapter;
    private ContactsSearchLoader mLoader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.global_search_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mGlobalSearchAdapter = new GlobalSearchAdapter(getActivity());
        mGlobalSearchAdapter.setGlobalSearchAdapterListener(this);
        mGlobalSearchAdapter.setHeadTitle(getString(R.string.hx_contacts_search_title));
        mGlobalSearchAdapter.setTailTitle(getString(R.string.hx_contacts_view_more_contacts));
        mRecyclerView.setAdapter(mGlobalSearchAdapter);

        getLoaderManager().initLoader(GLOBAL_SEARCH_LOADER_ID, null, this).forceLoad();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void reset() {
        mGlobalSearchAdapter.notifyDataSetChanged();
    }

    @Override
    protected void reloadData() {
        Log.d(TAG, "reloadData");
        if (mLoader != null) {
            mLoader.setQuery(getQueryString());
            mLoader.forceLoad();
        }
    }

    @NonNull
    @Override
    public Loader<List<T>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader");
        mLoader = new ContactsSearchLoader(getContext());
        mLoader.setQuery(getQueryString());

        return mLoader;
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        Log.d(TAG, "onLoaderReset");
        mGlobalSearchAdapter.setList(null);
    }


    @Override
    public void onLoadFinished(@NonNull Loader<List<T>> loader, List<T> data) {
        Log.d(TAG, "onLoadFinished");
        mGlobalSearchAdapter.setList(data);
    }


    @Override
    public void onItemClick(Object item) {
        String username = ((SearchContactBean) item).getUsername();
        Intent i = new Intent(getActivity(), EmployeeDataActivity.class);
        i.putExtra(EmployeeDataActivity.CONTACTS_ID, username);
        startActivity(i);
    }

}
