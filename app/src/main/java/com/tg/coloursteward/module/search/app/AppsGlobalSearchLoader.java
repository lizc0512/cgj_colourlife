package com.tg.coloursteward.module.search.app;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.tg.coloursteward.module.search.GlobalSearchLoader;
import com.tg.coloursteward.module.search.data.SearchData;
import com.youmai.hxsdk.entity.cn.SearchContactBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srsm on 2018/4/17.
 */
public class AppsGlobalSearchLoader<T> extends GlobalSearchLoader<SearchContactBean> {
    private final String TAG = AppsGlobalSearchLoader.class.getSimpleName();
    private Activity mContext;

    public AppsGlobalSearchLoader(Activity context) {
        super(context);
        mContext = context;
    }

    @Override
    public ArrayList<SearchContactBean> fullLoadInBackground() {
        if (mQuery == null || mQuery.isEmpty()) {
            return null;
        }

        ArrayList<SearchContactBean> searchContactBeenList = new ArrayList<>();
        String finalQuery = mQuery;
        String queryUpper = mQuery.toUpperCase();

        List<SearchContactBean> appsList = SearchData.peekInstance().searchCacheAppsList(mContext);

        for (SearchContactBean bean : appsList) {
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
                    Log.e("YW", "pinyin: " + pinyin);
                    if (b || c) {
                        searchType = SearchContactBean.SEARCH_TYPE_WHOLE_SPECL;
                        break;
                    }
                }
                finalQuery = queryUpper;

                //去匹配中文名的
                Log.e("YW", "i: getWholePinyin");

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

    @Override
    public ArrayList<SearchContactBean> reloadInBackground() {
        return fullLoadInBackground();
    }

}
