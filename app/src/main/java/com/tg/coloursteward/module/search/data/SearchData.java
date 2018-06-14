package com.tg.coloursteward.module.search.data;

import android.content.Context;
import android.util.SparseArray;

import com.tg.coloursteward.info.FamilyInfo;
import com.tg.coloursteward.info.FindContactInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.youmai.hxsdk.entity.cn.DuoYinZi;
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.entity.cn.pinyin.Pinyin;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.PinYinUtils;
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
public class SearchData {

    private static final String TAG = "YW";

    private static SearchData instance;


    public static SearchData instance() {
        if (null == instance) {
            instance = new SearchData();
        }
        return instance;
    }


    /**
     * 搜索联系人
     *
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
            String hanzi = data.getString(i, "realname");
            StringBuilder ch = new StringBuilder();
            StringBuilder pinyin = new StringBuilder();
            List<String> chStr = new ArrayList<>(); //每个汉字的 拼音集合
            for (int j = 0; j < hanzi.length(); j++) {
                pinyin.append(Pinyin.toPinyin(hanzi.charAt(j)).toUpperCase());
                ch.append(Pinyin.toPinyin(hanzi.charAt(j)).substring(0, 1));
                chStr.add(Pinyin.toPinyin(hanzi.charAt(j)));
            }

            SearchContactBean contact = new SearchContactBean();
            contact.setIconUrl(data.getString(i, "avatar"));
            contact.setUsername(data.getString(i, "username"));
            contact.setUuid(data.getString(i, "contactsId"));
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
        ResponseData app_list = HttpTools.getResponseKey(cacheData, "app_list");
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

            StringBuilder ch = new StringBuilder();
            StringBuilder pinyin = new StringBuilder();
            List<String> chStr = new ArrayList<>(); //每个汉字的 拼音集合
            for (int j = 0; j < hanzi.length(); j++) {
                pinyin.append(Pinyin.toPinyin(hanzi.charAt(j)).toUpperCase());
                ch.append(Pinyin.toPinyin(hanzi.charAt(j)).substring(0, 1));
                chStr.add(Pinyin.toPinyin(hanzi.charAt(j)));
            }

            try {
                appsBean.setDisplayName(hanzi);
                appsBean.setInfo(data.getString(i, "url"));
                appsBean.setOauthType(data.getString(i, "oauthType"));
                appsBean.setDeveloperCode(data.getString(i, "app_code"));
                appsBean.setClientCode(data.getString(i, "app_code"));
                JSONObject icon = data.getJSONObject(i, "icon");
                if (icon != null || icon.length() > 0) {
                    appsBean.setIconUrl(icon.getString("android"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
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


    /**
     * 搜索联系人
     *
     * @param list
     * @return
     */
    public List<SearchContactBean> searchContactsList(List<FamilyInfo> list) {
        List<SearchContactBean> contactList = new ArrayList<>();
        for (FamilyInfo item : list) {
            String hanzi = item.name;
            StringBuilder ch = new StringBuilder();
            StringBuilder pinyin = new StringBuilder();
            List<String> chStr = new ArrayList<>(); //每个汉字的 拼音集合

            for (int j = 0; j < hanzi.length(); j++) {
                pinyin.append(Pinyin.toPinyin(hanzi.charAt(j)).toUpperCase());
                ch.append(Pinyin.toPinyin(hanzi.charAt(j)).substring(0, 1));
                chStr.add(Pinyin.toPinyin(hanzi.charAt(j)));
            }

            SearchContactBean contact = new SearchContactBean();
            contact.setIconUrl(item.avatar);
            contact.setUsername(item.username);
            contact.setUuid(item.id);
            contact.setDisplayName(hanzi);
            contact.setWholePinyin(pinyin.toString());
            contact.setSimplepinyin(ch.toString());
            contact.setIndexPinyin(chStr);

            DuoYinZi duoYinZi = PinYinUtils.HanziToPinYin(hanzi);
            contact.setDuoYinzi(duoYinZi);

            contactList.add(contact);
        }
        return contactList;
    }


    /**
     * 搜索联系人
     *
     * @param list
     * @return
     */
    public List<SearchContactBean> searchIceList(List<FindContactInfo> list) {
        List<SearchContactBean> contactList = new ArrayList<>();
        for (FindContactInfo item : list) {
            String hanzi = item.realname;
            StringBuilder ch = new StringBuilder();
            StringBuilder pinyin = new StringBuilder();
            List<String> chStr = new ArrayList<>(); //每个汉字的 拼音集合

            for (int j = 0; j < hanzi.length(); j++) {
                pinyin.append(Pinyin.toPinyin(hanzi.charAt(j)).toUpperCase());
                ch.append(Pinyin.toPinyin(hanzi.charAt(j)).substring(0, 1));
                chStr.add(Pinyin.toPinyin(hanzi.charAt(j)));
            }

            SearchContactBean contact = new SearchContactBean();
            contact.setIconUrl(item.avatar);
            contact.setUsername(item.username);
            contact.setDisplayName(hanzi);
            contact.setWholePinyin(pinyin.toString());
            contact.setSimplepinyin(ch.toString());
            contact.setIndexPinyin(chStr);

            DuoYinZi duoYinZi = PinYinUtils.HanziToPinYin(hanzi);
            contact.setDuoYinzi(duoYinZi);

            contactList.add(contact);
        }
        return contactList;
    }
}
