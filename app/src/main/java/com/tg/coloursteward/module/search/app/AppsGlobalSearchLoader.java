package com.tg.coloursteward.module.search.app;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import com.tg.coloursteward.info.FamilyInfo;
import com.tg.coloursteward.module.search.data.SearchData;
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srsm on 2018/4/17.
 */
public class AppsGlobalSearchLoader extends AsyncTaskLoader {
    private final String TAG = AppsGlobalSearchLoader.class.getSimpleName();

    private List<FamilyInfo> contactList;
    private String mQuery;


    public AppsGlobalSearchLoader(Context context) {
        super(context);
    }

    public AppsGlobalSearchLoader(Context context, List<FamilyInfo> list) {
        super(context);
        contactList = list;
    }


    public void setQuery(String str) {
        this.mQuery = str;
    }


    @Override
    public List<SearchContactBean> loadInBackground() {
        List<SearchContactBean> resList = new ArrayList<>();
        List<SearchContactBean> allList;
        if (ListUtils.isEmpty(contactList)) {
            allList = SearchData.instance().searchContactsList(contactList);
        } else {
            allList = SearchData.instance().searchContactsList(getContext());
        }

        if (TextUtils.isEmpty(mQuery)) {
            return allList;
        }

        String finalQuery = mQuery;
        String queryUpper = mQuery.toUpperCase();

        for (SearchContactBean bean : allList) {
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

                List<String> indexPinyin = bean.getIndexPinyin();
                for (String pinyin : indexPinyin) {
                    boolean b = pinyin.startsWith(queryUpper);//每个汉字拼音
                    boolean c = queryUpper.startsWith(pinyin);
                    Log.v(TAG, "pinyin: " + pinyin);
                    if (b || c) {
                        searchType = SearchContactBean.SEARCH_TYPE_WHOLE_SPECL;
                        break;
                    }
                }
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
                SearchContactBean searchBean = new SearchContactBean(bean, false);
                resList.add(searchBean);
            }
        }
        return resList;
    }


}
