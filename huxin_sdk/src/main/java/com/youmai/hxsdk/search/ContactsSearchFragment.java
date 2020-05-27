package com.youmai.hxsdk.search;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.router.APath;

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

        mRecyclerView = view.findViewById(R.id.global_search_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mGlobalSearchAdapter = new GlobalSearchAdapter(getActivity());
        mGlobalSearchAdapter.setGlobalSearchAdapterListener(this);
        mGlobalSearchAdapter.setHeadTitle(getString(R.string.hx_contacts_search_title));
        mGlobalSearchAdapter.setTailTitle(getString(R.string.hx_contacts_view_more_contacts));
        mRecyclerView.setAdapter(mGlobalSearchAdapter);
        //scrollView滑动嵌套简单处理
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);

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
        mLoader = new ContactsSearchLoader(getContext());
        mLoader.setQuery(getQueryString());
        return mLoader;
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        mGlobalSearchAdapter.setList(null);
    }


    @Override
    public void onLoadFinished(@NonNull Loader<List<T>> loader, List<T> data) {
        mGlobalSearchAdapter.setList(data);
    }


    @Override
    public void onItemClick(Object item) {
        String username = ((SearchContactBean) item).getUsername();
        String user_uuid = ((SearchContactBean) item).getUuid();
        ARouter.getInstance().build(APath.EMPLOYEE_DATA_ACT)
                .withString("contacts_id", username)
                .withString("contacts_uuid", user_uuid)
                .navigation(getContext());
    }

}
