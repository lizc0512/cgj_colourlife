package com.tg.coloursteward.module.search.data;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.util.SparseArray;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.module.contact.utils.PinYinUtils;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.youmai.hxsdk.entity.cn.DuoYinZi;
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.entity.cn.pinyin.Pinyin;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.smallvideorecord.utils.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2018.03.21 17:30
 * 描述：搜索数据
 */
public class SearchData implements MessageHandler.ResponseListener {

    private static final String TAG = "YW";

    private static SearchData instance;
    private Activity mActivity;

    public static SearchData init(Activity context) {
        synchronized (SearchData.class) {
            if (null == instance) {
                instance = new SearchData(context);
            }
            return instance;
        }
    }

    private SearchData(Activity context) {
        this.mActivity = context;
        msgHandler = new MessageHandler(mActivity);
        msgHandler.setResponseListener(this);
    }

    public static SearchData peekInstance() {
        return instance;
    }

    public void onDestroy() {
        instance = null;
        mActivity = null;
    }

    /**
     * 搜索联系人
     * @param context
     * @return
     */
    public List<SearchContactBean> searchContactsList(Context context) {
        String LinkManListCache = Tools.getLinkManList(context);
        ResponseData data = new ResponseData(new SparseArray<HashMap<String, Object>>());
        if (StringUtils.isNotEmpty(LinkManListCache)) {
            JSONArray jsonString = HttpTools.getContentJsonArray(LinkManListCache);
            if (jsonString != null) {
                data = HttpTools.getResponseContent(jsonString);
            }
        }

        List<SearchContactBean> contactList = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            String hanzi = data.getString(i,"realname");
            StringBuffer ch = new StringBuffer();
            StringBuffer pinyin = new StringBuffer();
            List<String> chStr = new ArrayList<>(); //每个汉字的 拼音集合
            for (int j = 0; j < hanzi.length(); j++) {
                System.out.println("yw-i: " + hanzi.charAt(j));
                pinyin.append(Pinyin.toPinyin(hanzi.charAt(j)).toUpperCase());
                ch.append(Pinyin.toPinyin(hanzi.charAt(j)).substring(0, 1));
                chStr.add(Pinyin.toPinyin(hanzi.charAt(j)));
            }

            SearchContactBean contact = new SearchContactBean();
            contact.setIconUrl(data.getString(i, "avatar"));
            contact.setUsername(data.getString(i, "username"));
            contact.setDisplayName(hanzi);
            contact.setWholePinyin(pinyin.toString());
            contact.setSimplepinyin(ch.toString());
            contact.setIndexPinyin(chStr);

            DuoYinZi duoYinZi = PinYinUtils.HanziToPinYin(hanzi);
            contact.setDuoYinzi(duoYinZi);

            Log.e("YW", contact.toString());

            contactList.add(contact);
        }
        return contactList;
    }

    /*****************************************************

    /**
     * 搜索应用
     * @return
     */
    public List<SearchContactBean> searchCacheAppsList(Context context) {
        List<SearchContactBean> appsList = new ArrayList<>();
        String cacheData = Tools.getCommonName(context);
        String json = HttpTools.getContentString(cacheData);
        ResponseData app_list = HttpTools.getResponseKey(json, "app_list");
        if (app_list.length > 0) {
            JSONArray commonArray = app_list.getJSONArray(0, "list");
            JSONArray otherArray = app_list.getJSONArray(1, "list");
            ResponseData commonData = HttpTools.getResponseKeyJSONArray(commonArray);
            ResponseData otherData = HttpTools.getResponseKeyJSONArray(otherArray);
            List<SearchContactBean> commonApps = searchAppsList(commonData);
            List<SearchContactBean> otherApps = searchAppsList(otherData);
            appsList.addAll(commonApps);
            appsList.addAll(otherApps);
        }
        return appsList;
    }



    private List<SearchContactBean> searchAppsList(ResponseData data) {
        List<SearchContactBean> mAppsList = new ArrayList<>();
        if (!ListUtils.isEmpty(mAppsList)) {
            mAppsList.clear();
        }
        SearchContactBean appsBean;
        for (int i = 0; i < data.length; i++) {
            appsBean = new SearchContactBean();
            String hanzi = data.getString(i, "name");
            try {
                appsBean.setDisplayName(hanzi);
                appsBean.setIconUrl(data.getString(i, "url"));
                JSONObject icon = data.getJSONObject(i, "icon");
                if (icon != null || icon.length() > 0) {
                    appsBean.setInfo(icon.getString("android"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            StringBuffer ch = new StringBuffer();
            StringBuffer pinyin = new StringBuffer();
            List<String> chStr = new ArrayList<>(); //每个汉字的 拼音集合
            for (int j = 0; j < hanzi.length(); j++) {
                System.out.println("yw-i: " + hanzi.charAt(j));
                pinyin.append(Pinyin.toPinyin(hanzi.charAt(j)).toUpperCase());
                ch.append(Pinyin.toPinyin(hanzi.charAt(j)).substring(0, 1));
                chStr.add(Pinyin.toPinyin(hanzi.charAt(j)));
            }

            appsBean.setWholePinyin(pinyin.toString());
            appsBean.setSimplepinyin(ch.toString());
            appsBean.setIndexPinyin(chStr);

            DuoYinZi duoYinZi = PinYinUtils.HanziToPinYin(hanzi);
            appsBean.setDuoYinzi(duoYinZi);

            Log.e("YW", appsBean.toString());

            mAppsList.add(appsBean);
        }
        return mAppsList;
    }

    MessageHandler msgHandler;

    public void initApps(int type) {
        switch (type) {
            case 1:
                String commonName = Tools.getCommonName(mActivity);
                cacheApps(commonName);
                break;
            case 2:
            default:
                String pwd = Tools.getPassWord(mActivity);
                RequestConfig config = new RequestConfig(mActivity, 0);
                config.handler = msgHandler.getHandler();
                RequestParams params = new RequestParams();
                params.put("user_name", UserInfo.employeeAccount);
                params.put("password", pwd);
                params.put("resource", "app");
                params.put("cate_id", 0);
                HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newoa/rights/list", config, params);
                break;
        }
    }

    void cacheApps(String cacheData) {
        if (StringUtils.isEmpty(cacheData)) {
            initApps(2);
        } else {
            searchForCacheApps(cacheData);
        }
    }

    void searchForNetAppsList(String jsonString) {
        String json = HttpTools.getContentString(jsonString);
        if (json != null) {
            ResponseData app_list = HttpTools.getResponseKey(json, "app_list");
            if (app_list.length > 0) {
                Tools.saveElseInfo(mActivity, json);
                Tools.saveCommonInfo(mActivity, json);
                JSONArray commonArray = app_list.getJSONArray(0, "list");
                JSONArray otherArray = app_list.getJSONArray(1, "list");
                ResponseData commonData = HttpTools.getResponseKeyJSONArray(commonArray);
                ResponseData otherData = HttpTools.getResponseKeyJSONArray(otherArray);

                searchAppsList(commonData);
                searchAppsList(otherData);
            }
        }
    }

    void searchForCacheApps(String cacheData) {
        String json = HttpTools.getContentString(cacheData);
        ResponseData app_list = HttpTools.getResponseKey(json, "app_list");
        if (app_list.length > 0) {
            JSONArray commonArray = app_list.getJSONArray(0, "list");
            JSONArray otherArray = app_list.getJSONArray(1, "list");
            ResponseData commonData = HttpTools.getResponseKeyJSONArray(commonArray);
            ResponseData otherData = HttpTools.getResponseKeyJSONArray(otherArray);
            searchAppsList(commonData);
            searchAppsList(otherData);
        }
    }

    @Override
    public void onRequestStart(Message msg, String hintString) {
        Log.e(TAG, "onRequestStart");
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        Log.e(TAG, "onSuccess");
        searchForNetAppsList(jsonString);
    }

    @Override
    public void onFail(Message msg, String hintString) {
        Log.e(TAG, "onFail");
    }
}
