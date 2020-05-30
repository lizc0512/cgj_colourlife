package com.youmai.hxsdk.search;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.loader.content.AsyncTaskLoader;

import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.entity.SearchResult;
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.entity.cn.pinyin.Pinyin;
import com.youmai.hxsdk.http.OkHttpConnector;
import com.youmai.hxsdk.utils.GsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by srsm on 2017/8/7.
 */
public class ContactsSearchLoader extends AsyncTaskLoader {

    private final String TAG = ContactsSearchLoader.class.getSimpleName();

    private String mQuery;
    private List<SearchContactBean> allList = new ArrayList<>();
    private Context mContext;
    private ProgressDialog mProgressDialog;

    public ContactsSearchLoader(Context context) {
        super(context);
        this.mContext = context;
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
        allList.clear();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("wisdomPark_map", 0);
        String corpId = sharedPreferences.getString("corp_id", "");
        searchIce(mContext, corpId, mQuery, allList);

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

            }


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


    private void searchIce(Context mContext, String corp_uuid, String key, List<SearchContactBean> allList) {

        String url = ColorsConfig.CONTACTS_PEOPLE_DATAS;
        ContentValues params = new ContentValues();
        params.put("corp_uuid", corp_uuid);
        params.put("keyword", key);
        ColorsConfig.commonParams(params);
        ContentValues header = new ContentValues();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("park_cache_map", 0);
        String color_token = sharedPreferences.getString("access_token2", "");
        header.put("color-token", color_token);
        updateDate.sendEmptyMessage(0);
        String response = OkHttpConnector.doGet(header, url, params);
        updateDate.sendEmptyMessage(1);
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

                String uuid = item.getId();
                String avatar = item.getAvatar();
                contact.setIconUrl(avatar);
                contact.setUsername(item.getUsername());
                contact.setUuid(uuid);
                contact.setDisplayName(hanzi);
                contact.setWholePinyin(pinyin.toString());
                contact.setSimplepinyin(ch.toString());
                contact.setIndexPinyin(chStr);
                contact.setPhoneNum(item.getOrgName());
                contact.setJobName(item.getJobName());
                contact.setMobile(item.getMobile());
                contact.setEmail(item.getEmail());
                contact.setSex(item.getSex());
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

    Handler updateDate = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    showProgressDialog();
                    break;
                case 1:
                    dismissProgressDialog();
                    break;
            }
        }
    };

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage("正在请求，请稍后...");
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(true);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


}