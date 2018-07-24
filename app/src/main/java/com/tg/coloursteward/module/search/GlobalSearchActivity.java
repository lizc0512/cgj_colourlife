package com.tg.coloursteward.module.search;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.widget.SearchEditText;

/**
 * Created by srsm on 2017/8/21.
 */
public class GlobalSearchActivity extends AppCompatActivity implements View.OnClickListener {

    private ContactsSearchFragment mContactsSearchFragment;
    //private AppsSearchFragment mAppsSearchFragment;

    private SearchEditText mSearchEditText;
    private TextView mBtnBackMain;
    private TextView mBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_activity_search);
        initView();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mContactsSearchFragment = new ContactsSearchFragment();
        transaction.replace(R.id.contacts_search_container, mContactsSearchFragment, ContactsSearchFragment.TAG);
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                /*if (mAppsSearchFragment != null) {
                    if (mAppsSearchFragment.isHidden()) {
                        transaction.show(mAppsSearchFragment);
                    }
                    mAppsSearchFragment.setQueryString(queryStr);
                    mAppsSearchFragment.reset();
                }*/
                transaction.commit();
            }
        });
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });

        mBtnBack = (TextView) findViewById(R.id.global_search_bar_cancel);
        mBtnBack.setOnClickListener(this);
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
            transaction.commit();
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
    }

}
