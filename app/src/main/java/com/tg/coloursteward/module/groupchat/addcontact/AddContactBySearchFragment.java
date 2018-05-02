package com.tg.coloursteward.module.groupchat.addcontact;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tg.coloursteward.R;
import com.tg.coloursteward.module.search.SearchFragment;
import com.tg.coloursteward.module.search.data.SearchData;
import com.youmai.hxsdk.db.bean.ContactBean;

import java.util.Map;

/**
 * 作者：create by YW
 * 日期：2018.04.19 18:24
 * 描述：搜索联系人列表
 */
public class AddContactBySearchFragment extends Fragment {

    private static final String TAG_SEARCH_CONTACTS_FRAGMENT = "search_contacts_fragment";

    private AddContactsSearchFragment mSearchFragment;
    private SearchFragment.OnLoadFinishListener mLoadFinishListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_search_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchData.init(getActivity()).initApps(2);
        initView();

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        mSearchFragment = new AddContactsSearchFragment();
        mSearchFragment.setOnLoadFinishListener(mLoadFinishListener);
        transaction.add(R.id.contacts_search_container, mSearchFragment, TAG_SEARCH_CONTACTS_FRAGMENT);

        transaction.commitAllowingStateLoss();
        
        mSearchFragment.setOnSelectItemListener(new AddContactsSearchFragment.SelectItemListener() {
            @Override
            public void onSelect(ContactBean contact) {
                //隐藏fragment
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.hide(mSearchFragment);
                transaction.commitAllowingStateLoss();
            }
        });

    }

    private void initView() {
        mLoadFinishListener = new SearchFragment.OnLoadFinishListener() {
            @Override
            public void onFinishCallback(boolean finish, String query) {
                // 多个回调，有先后...
                synchronized (this) {
                }
            }

            @Override
            public void onWhoShowMoreCallback(String fragmentTag) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.hide(mSearchFragment);
                transaction.commitAllowingStateLoss();
            }
        };
    }

    public void add(String queryStr) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        if (mSearchFragment != null) {
            if (mSearchFragment.isHidden()) {
                transaction.show(mSearchFragment);
            }
            mSearchFragment.setQueryString(queryStr);
            mSearchFragment.reset();
        }
        transaction.commit();
    }

    public void hide() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        if (mSearchFragment != null) {
            if (mSearchFragment.isHidden()) {
                transaction.show(mSearchFragment);
            }
            mSearchFragment.reset();
        }
        transaction.commit();
    }

    public void setMap(Map<String, ContactBean> totalMap, Map<String, ContactBean> groupMap) {
        mSearchFragment.setMap(totalMap, groupMap);
    }

}
