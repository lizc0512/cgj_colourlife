package com.tg.coloursteward.module.search;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.tg.coloursteward.module.search.app.AppsSearchFragment;
import com.tg.coloursteward.module.search.data.SearchData;
import com.youmai.hxsdk.R;

/**
 * Created by srsm on 2017/8/21.
 */
public class GlobalSearchActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG_SEARCH_CONTACTS_FRAGMENT = "search_contacts_fragment";
    private static final String TAG_SEARCH_APPS_FRAGMENT = "search_apps_fragment";

    private ContactsSearchFragment mContactsSearchFragment;
    private AppsSearchFragment mAppsSearchFragment;
    private SearchFragment.OnLoadFinishListener mLoadFinishListener;

    private SearchEditText mSearchEditText;
    private TextView mBtnBackMain;
    private TextView mBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_activity_search);
        SearchData.init(this).initApps(2);
        initView();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mContactsSearchFragment = new ContactsSearchFragment();
        mContactsSearchFragment.setOnLoadFinishListener(mLoadFinishListener);
        transaction.add(R.id.contacts_search_container, mContactsSearchFragment, TAG_SEARCH_CONTACTS_FRAGMENT);

        mAppsSearchFragment = new AppsSearchFragment();
        mAppsSearchFragment.setOnLoadFinishListener(mLoadFinishListener);
        transaction.add(R.id.apps_search_container, mAppsSearchFragment, TAG_SEARCH_APPS_FRAGMENT);

        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SearchData.peekInstance().onDestroy();
    }

    private void initView() {

        mBtnBackMain = (TextView) findViewById(R.id.global_search_bar_back);
        mBtnBackMain.setOnClickListener(this);

        mSearchEditText = (SearchEditText) findViewById(R.id.global_search_bar);
        mSearchEditText.addTextChangedListener(new SearchEditText.MiddleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String queryStr = s.toString();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (mContactsSearchFragment != null) {
                    if (mContactsSearchFragment.isHidden()) {
                        transaction.show(mContactsSearchFragment);
                    }
                    mContactsSearchFragment.setQueryString(queryStr);
                    mContactsSearchFragment.reset();
                }
                if (mAppsSearchFragment != null) {
                    if (mAppsSearchFragment.isHidden()) {
                        transaction.show(mAppsSearchFragment);
                    }
                    mAppsSearchFragment.setQueryString(queryStr);
                    mAppsSearchFragment.reset();
                }
                transaction.commit();
            }
        });

        mBtnBack = (TextView) findViewById(R.id.global_search_bar_cancel);
        mBtnBack.setOnClickListener(this);

        mLoadFinishListener = new SearchFragment.OnLoadFinishListener() {
            @Override
            public void onFinishCallback(boolean finish, String query) {
                // 多个回调，有先后...
                synchronized (this) {

                }
            }

            @Override
            public void onWhoShowMoreCallback(String fragmentTag) {
                mBtnBackMain.setVisibility(View.VISIBLE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                if (fragmentTag.equals(TAG_SEARCH_CONTACTS_FRAGMENT)) {
                    transaction.hide(mAppsSearchFragment);
                } else {
                    transaction.hide(mContactsSearchFragment);
                }

                transaction.commitAllowingStateLoss();
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnBack) {
            onBackPressed();
        } else if (v == mBtnBackMain) {
            mBtnBackMain.setVisibility(View.GONE);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (mContactsSearchFragment != null) {
                if (mContactsSearchFragment.isHidden()) {
                    transaction.show(mContactsSearchFragment);
                }
                mContactsSearchFragment.reset();
            }
            if (mAppsSearchFragment != null) {
                if (mAppsSearchFragment.isHidden()) {
                    transaction.show(mAppsSearchFragment);
                }
                mAppsSearchFragment.reset();
            }
            transaction.commit();
        }
    }

}
