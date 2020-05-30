package com.youmai.hxsdk.group.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.group.AddContactsCreateGroupActivity;
import com.youmai.hxsdk.group.adapter.AddContactsSearchAdapter;
import com.youmai.hxsdk.search.ContactsSearchLoader;
import com.youmai.hxsdk.search.SearchFragment;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by yw on 2018/4/20.
 */
public class AddContactsSearchFragment<T extends Parcelable> extends SearchFragment implements
        LoaderManager.LoaderCallbacks<ArrayList<T>>, AddContactsSearchAdapter.GlobalSearchAdapterListener {

    private static final String TAG = AddContactsSearchFragment.class.getSimpleName();

    private final int GLOBAL_SEARCH_LOADER_ID = 2;
    private RecyclerView mRecyclerView;

    private AddContactsSearchAdapter<T> mSearchAdapter;
    private SelectItemListener mListener;

    protected ArrayList<T> mContactList = new ArrayList<>();

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
        mSearchAdapter = new AddContactsSearchAdapter<>(getActivity());
        mSearchAdapter.setGlobalSearchAdapterListener(this);
        mSearchAdapter.setHeadTitle(getString(R.string.hx_contacts_search_title));
        mSearchAdapter.setTailTitle(getString(R.string.hx_contacts_view_more_contacts));
        mRecyclerView.setAdapter(mSearchAdapter);
        getLoaderManager().initLoader(GLOBAL_SEARCH_LOADER_ID, null, this).forceLoad();

    }

    public void reset() {
        mSearchAdapter.notifyDataSetChanged();
    }


    public void add(String queryStr) {
        setQueryString(queryStr);
        reset();
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
    public Loader<ArrayList<T>> onCreateLoader(int id, @Nullable Bundle args) {
        mLoader = new ContactsSearchLoader(getActivity());
        mLoader.setQuery(getQueryString());
        return mLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<T>> loader, ArrayList<T> data) {
        Log.d(TAG, "onLoadFinished");
        mSearchAdapter.setArrayList(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<T>> loader) {
        Log.d(TAG, "onLoaderReset");
        mSearchAdapter.setArrayList(null);
    }

    @Override
    public void onItemClick(Object item) {
        SearchContactBean bean = (SearchContactBean) item;
        ContactBean contact = new ContactBean();
        contact.setUsername(bean.getUsername());
        contact.setRealname(bean.getDisplayName());
        contact.setPinyin(bean.getWholePinyin());
        contact.setUuid(bean.getUuid());
        contact.setAvatar(bean.getIconUrl());

        Log.e("Yw", "onItemClick: " + contact.toString());

        if (null != mListener) {
            mListener.onSelect(contact);
        }

        Intent intent = new Intent(AddContactsCreateGroupActivity.BROADCAST_FILTER);
        intent.putExtra(AddContactsCreateGroupActivity.ACTION, AddContactsCreateGroupActivity.SEARCH_CONTACT);
        intent.putExtra("bean", contact);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

    }

    @Override
    public void onMoreItemClick() {

    }


    public interface SelectItemListener {
        void onSelect(ContactBean contact);
    }

    public void setOnSelectItemListener(SelectItemListener listener) {
        this.mListener = listener;
    }

    public void setMap(Map<String, ContactBean> totalMap, Map<String, ContactBean> groupMap) {
        mSearchAdapter.setCacheMap(totalMap);
        mSearchAdapter.setGroupMap(groupMap);
    }

}
