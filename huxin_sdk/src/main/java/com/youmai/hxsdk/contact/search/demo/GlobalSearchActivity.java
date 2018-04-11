package com.youmai.hxsdk.contact.search.demo;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.contact.search.cn.CNPinyin;
import com.youmai.hxsdk.db.bean.Contact;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by srsm on 2017/8/21.
 */
@Route(path = "/app/HxGlobalSearchActivity")
public class GlobalSearchActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG_SEARCH_CONTACTS_FRAGMENT = "search_contacts_fragment";
    private static final String TAG_SEARCH_MESSAGE_FRAGMENT = "search_message_fragment";

    private ContactsSearchFragment mContactsSearchFragment;
    private SearchFragment.OnLoadFinishListener mLoadFinishListener;

    private View mSearchStatusContainer;
    private View mSearchEmptyButtonContainer;

    private SearchEditText mSearchEditText;
    private TextView mBtnBackMain;
    private TextView mBtnBack;
    private TextView mSearchStatus;

    private ArrayList<CNPinyin<Contact>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.global_activity_search);
        initView();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mContactsSearchFragment = new ContactsSearchFragment(contactList);
        mContactsSearchFragment.setOnLoadFinishListener(mLoadFinishListener);
        transaction.add(R.id.contacts_search_container, mContactsSearchFragment, TAG_SEARCH_CONTACTS_FRAGMENT);

        transaction.commitAllowingStateLoss();
    }

    private void initView() {

        contactList = (ArrayList<CNPinyin<Contact>>) getIntent().getSerializableExtra("contactList");
        Log.e("YW", "contactList: " + contactList);

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
                transaction.commit();
            }
        });

        mBtnBack = (TextView) findViewById(R.id.global_search_bar_cancel);
        mBtnBack.setOnClickListener(this);

        mSearchStatusContainer = findViewById(R.id.global_search_status_container);
        mSearchEmptyButtonContainer = findViewById(R.id.global_search_empty_button_container);

        mSearchStatus = (TextView) findViewById(R.id.global_search_status);
        mSearchStatus.setText(R.string.hx_common_global_can_input);

        mLoadFinishListener = new SearchFragment.OnLoadFinishListener() {
            @Override
            public void onFinishCallback(boolean finish, String query) {
                // 多个回调，有先后...
                synchronized (this) {
                    if (finish) {
                        mSearchStatusContainer.setVisibility(View.GONE);
                    } else {
                        mSearchStatusContainer.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onWhoShowMoreCallback(String fragmentTag) {
                mBtnBackMain.setVisibility(View.VISIBLE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.hide(mContactsSearchFragment);

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
            transaction.commit();
        }
    }

}
