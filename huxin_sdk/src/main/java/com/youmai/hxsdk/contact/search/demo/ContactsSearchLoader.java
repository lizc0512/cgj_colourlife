package com.youmai.hxsdk.contact.search.demo;

import android.content.Context;

import com.youmai.hxsdk.contact.search.adapter.TestUtils;
import com.youmai.hxsdk.contact.search.cn.SearchContactBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srsm on 2017/8/7.
 */
public class ContactsSearchLoader extends GlobalSearchLoader<SearchContactBean> {
    private final String TAG = ContactsSearchLoader.class.getSimpleName();
    private final Context mContext;

    public ContactsSearchLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public ArrayList<SearchContactBean> fullLoadInBackground() {

        mContactBeanList.clear();

        List<SearchContactBean> contactList = TestUtils.searchContactsList(mContext);
        mContactBeanList.addAll(contactList);
        if (mQuery.isEmpty()) {
            return null;
        } else {
            return mContactBeanList;
        }
    }

    @Override
    public ArrayList<SearchContactBean> reloadInBackground() {
        ArrayList<SearchContactBean> searchContactBeenList = new ArrayList<>();
        String finalQuery = mQuery;
        String queryUpper = mQuery.toUpperCase();

        for (SearchContactBean bean : mContactBeanList) {
            int searchType = SearchContactBean.SEARCH_TYPE_NONE;
            //全拼搜索
            int[] findResult = new int[2];

            if (bean.getDisplayName().contains(mQuery)) {
                searchType = SearchContactBean.SEARCH_TYPE_NAME;
                finalQuery = mQuery;
            } else if (bean.getSimplepinyin().contains(queryUpper)) {
                searchType = SearchContactBean.SEARCH_TYPE_SIMPLE_SPELL;
                finalQuery = queryUpper;
            } else if (bean.getWholePinyin().contains(queryUpper)) {
                searchType = SearchContactBean.SEARCH_TYPE_WHOLE_SPECL;
                finalQuery = queryUpper;
            } else if (bean.getDuoYinzi().find(queryUpper, findResult)) {
                searchType = SearchContactBean.SEARCH_TYPE_WHOLE_SPECL;
                finalQuery = queryUpper;
                bean.setWholePinYinFindIndex(findResult);
            }

            if (searchType != SearchContactBean.SEARCH_TYPE_NONE) {
                bean.setSearchKey(finalQuery);
                bean.setSearchType(searchType);

                //通讯录显示时不拷贝下一级，节省数据
                SearchContactBean newbean = new SearchContactBean(bean, false);
                searchContactBeenList.add(newbean);
            }
        }

        postPreLoad(searchContactBeenList);
        return searchContactBeenList;
    }

}