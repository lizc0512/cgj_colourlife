package com.youmai.hxsdk.search;

import android.content.Context;
import android.text.TextUtils;

import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.entity.cn.pinyin.Pinyin;
import com.youmai.hxsdk.entity.ModifyContactsBean;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.smallvideorecord.utils.Log;

import java.util.ArrayList;
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
        List<SearchContactBean> contactList = new ArrayList<>();
        String json = AppUtils.getStringSharedPreferences(context, "contents", "");
        if (!TextUtils.isEmpty(json)) {
            ModifyContactsBean bean = GsonUtil.parse(json, ModifyContactsBean.class);
            if (bean != null && bean.isSuccess()) {
                List<ModifyContactsBean.ContentBean.DataBean> data = bean.getContent().getData();
                for (ModifyContactsBean.ContentBean.DataBean item : data) {
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
                    contact.setIconUrl(item.getIcon());
                    contact.setUsername(item.getUsername());
                    contact.setUuid(item.getAccountUuid());
                    contact.setDisplayName(hanzi);
                    contact.setWholePinyin(pinyin.toString());
                    contact.setSimplepinyin(ch.toString());
                    contact.setIndexPinyin(chStr);

                    //DuoYinZi duoYinZi = PinYinUtils.HanziToPinYin(hanzi);
                    //contact.setDuoYinzi(duoYinZi);

                    Log.e("YW", contact.toString());

                    contactList.add(contact);
                }

            }
        }
        return contactList;

    }

}
