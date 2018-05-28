package com.tg.coloursteward.module.search;

import android.content.ContentValues;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.FamilyInfo;
import com.tg.coloursteward.info.FindContactInfo;
import com.tg.coloursteward.module.search.data.SearchData;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.ResponseData;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.http.OkHttpConnector;
import com.youmai.hxsdk.utils.ListUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srsm on 2017/8/7.
 */
public class ContactsSearchLoader extends AsyncTaskLoader {

    private final String TAG = ContactsSearchLoader.class.getSimpleName();

    private List<FamilyInfo> contactList;
    private String mQuery;
    private List<SearchContactBean> allList;

    public ContactsSearchLoader(Context context) {
        super(context);
    }

    public ContactsSearchLoader(Context context, List<FamilyInfo> list) {
        super(context);
        contactList = list;
    }


    public void setQuery(String str) {
        this.mQuery = str;
    }

    @Override
    public List<SearchContactBean> loadInBackground() {
        List<SearchContactBean> resList = new ArrayList<>();

        if (ListUtils.isEmpty(allList)) {
            if (ListUtils.isEmpty(contactList)) {
                allList = SearchData.instance().searchContactsList(getContext());
            } else {
                allList = SearchData.instance().searchContactsList(contactList);
            }
        }


        if (TextUtils.isEmpty(mQuery)) {
            return allList;
        } else {
            List<SearchContactBean> tempList = searchIce(mQuery);
            if (!ListUtils.isEmpty(tempList)) {
                allList.addAll(tempList);
            }
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


    private List<SearchContactBean> searchIce(String key) {
        String url = Contants.URl.URL_ICETEST + "/phonebook/search";
        ContentValues params = new ContentValues();
        params.put("keyword", key);
        ColorsConfig.commonParams(params);

        String response = OkHttpConnector.doGet(null, url, params);
        int code = HttpTools.getCode(response);
        if (code == 0) {
            List<FindContactInfo> list1 = new ArrayList<>();
            JSONArray jsonArray = HttpTools.getContentJsonArray(response);
            if (jsonArray != null) {
                ResponseData data = HttpTools.getResponseContent(jsonArray);
                if (data.length > 0) {
                    FindContactInfo info;
                    if (data.length >= 3) {
                        for (int i = 0; i < 3; i++) {
                            info = new FindContactInfo();
                            info.username = data.getString(i, "username");
                            info.realname = data.getString(i, "realname");
                            info.avatar = data.getString(i, "avatar");
                            info.org_name = data.getString(i, "org_name");
                            info.job_name = data.getString(i, "job_name");
                            list1.add(info);
                        }
                    } else {
                        for (int i = 0; i < data.length; i++) {
                            info = new FindContactInfo();
                            info.username = data.getString(i, "username");
                            info.realname = data.getString(i, "realname");
                            info.avatar = data.getString(i, "avatar");
                            info.org_name = data.getString(i, "org_name");
                            info.job_name = data.getString(i, "job_name");
                            list1.add(info);
                        }
                    }

                }
            }
            return SearchData.instance().searchIceList(list1);
        }

        return null;
    }


}