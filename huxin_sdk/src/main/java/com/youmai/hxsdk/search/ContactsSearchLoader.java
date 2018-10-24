package com.youmai.hxsdk.search;

import android.content.ContentValues;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.entity.SearchResult;
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.entity.cn.pinyin.Pinyin;
import com.youmai.hxsdk.http.OkHttpConnector;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srsm on 2017/8/7.
 */
public class ContactsSearchLoader extends AsyncTaskLoader {

    private final String TAG = ContactsSearchLoader.class.getSimpleName();

    private String mQuery;
    private List<SearchContactBean> allList = new ArrayList<>();

    public ContactsSearchLoader(Context context) {
        super(context);
    }


    public void setQuery(String str) {
        this.mQuery = str;
    }

    @Override
    public List<SearchContactBean> loadInBackground() {
        List<SearchContactBean> resList = new ArrayList<>();

        if (TextUtils.isEmpty(mQuery)) {
            return allList;
        }

        /*allList = SearchData.instance().searchContactsList(getContext());

        if (TextUtils.isEmpty(mQuery)) {
            return allList;
        } else {
            searchIce(mQuery, allList);
        }*/

        allList.clear();
        searchIce(mQuery, allList);

        String finalQuery = mQuery;
        String queryUpper = mQuery.toUpperCase();

        for (SearchContactBean bean : allList) {
            int searchType = SearchContactBean.SEARCH_TYPE_NONE;

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

            } /*else if (bean.getDuoYinzi().find(queryUpper, findResult)) {
                searchType = SearchContactBean.SEARCH_TYPE_WHOLE_SPECL;
                finalQuery = queryUpper;
                bean.setWholePinYinFindIndex(findResult);
            }*/


            if (searchType != SearchContactBean.SEARCH_TYPE_NONE) {
                bean.setSearchKey(finalQuery);
                bean.setSearchType(searchType);

                //通讯录显示时不拷贝下一级，节省数据
                SearchContactBean searchBean = new SearchContactBean(bean, false);
                resList.add(searchBean);
            }
        }
        allList.clear();
        return resList;
    }


    private void searchIce(String key, List<SearchContactBean> allList) {

        String url = ColorsConfig.CONTACTS_SEARCH;
        ContentValues params = new ContentValues();
        params.put("pagesize", "100");
        params.put("keyword", key);
        ColorsConfig.commonParams(params);

        String response = OkHttpConnector.doGet(null, url, params);
        SearchResult bean = GsonUtil.parse(response, SearchResult.class);
        if (bean != null && bean.isSuccess()) {
            List<SearchResult.ContentBean> list = bean.getContent();
            for (SearchResult.ContentBean item : list) {
                String hanzi = item.getName();
                StringBuilder ch = new StringBuilder();
                StringBuilder pinyin = new StringBuilder();
                List<String> chStr = new ArrayList<>(); //每个汉字的 拼音集合
                for (int j = 0; j < hanzi.length(); j++) {
                    pinyin.append(Pinyin.toPinyin(hanzi.charAt(j)).toUpperCase());
                    ch.append(Pinyin.toPinyin(hanzi.charAt(j)).substring(0, 1));
                    chStr.add(Pinyin.toPinyin(hanzi.charAt(j)));
                }

                SearchContactBean contact = new SearchContactBean();

                String uuid = item.getAccountUuid();
                String avatar = ColorsConfig.HEAD_ICON_URL + "avatar?uid=" + item.getUsername();
                contact.setIconUrl(avatar);
                contact.setUsername(item.getUsername());
                contact.setUuid(uuid);
                contact.setDisplayName(hanzi);
                contact.setWholePinyin(pinyin.toString());
                contact.setSimplepinyin(ch.toString());
                contact.setIndexPinyin(chStr);
                contact.setPhoneNum(item.getOrgName());

                //DuoYinZi duoYinZi = PinYinUtils.HanziToPinYin(hanzi);
                //contact.setDuoYinzi(duoYinZi);

                boolean isAdd = true;
                for (SearchContactBean local : allList) {
                    if (local.getUuid().equals(uuid)) {
                        isAdd = false;
                        break;
                    }
                }
                if (isAdd) {
                    allList.add(contact);
                }

            }
        }
    }


}