package com.tg.coloursteward.module.contact.utils;

import android.content.Context;
import android.util.SparseArray;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.Contact;
import com.youmai.hxsdk.entity.cn.CN;
import com.youmai.hxsdk.entity.cn.DuoYinZi;
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.entity.cn.pinyin.Pinyin;
import com.youmai.smallvideorecord.utils.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
/**
 * 作者：create by YW
 * 日期：2018.03.21 17:30
 * 描述：联系人数据更新
 */
public class ContactsBindData extends Observable {

    private List<CN> mData = new ArrayList<>();

    private static ContactsBindData instance;

    public static ContactsBindData init() {
        synchronized (ContactsBindData.class) {
            if (null == instance) {
                instance = new ContactsBindData();
            }
            return instance;
        }
    }

    public static ContactsBindData peekInstance() {
        return instance;
    }

    public void initData(List<? extends CN> data) {
        mData.addAll(data);
        setChanged();
        notifyObservers(mData);
    }

    public List<Contact> contactList(Context context, ResponseData data) {
        List<Contact> contactList = new ArrayList<>();
        Contact contact;

        String[] names = context.getResources().getStringArray(R.array.names_1); //获取
        for (int i = 0; i < names.length; i++) {

            contact = new Contact();
            StringBuffer pinyin = new StringBuffer();
            StringBuffer ch = new StringBuffer();
            String hanzi = names[i];
            List<String> chStr = new ArrayList<>(); //每个汉字的 拼音集合
            for (int j = 0; j < hanzi.length(); j++) {
                System.out.println("yw-i: " + hanzi.charAt(j));
                pinyin.append(Pinyin.toPinyin(hanzi.charAt(j)).toUpperCase());
                ch.append(Pinyin.toPinyin(hanzi.charAt(j)).substring(0, 1));
                chStr.add(Pinyin.toPinyin(hanzi.charAt(j)));
            }

            contact.setIs_hx(true);
            contact.setRealname(hanzi);
            contact.setPinyin(pinyin.toString());
            contact.setSimplePinyin(ch.toString());
            contactList.add(contact);
        }
        for (int i = 0; i < data.length; i++) {

            contact = new Contact();
            String hanzi = data.getString(i, "realname");
            contact.setRealname(hanzi);
            contact.setUsername(data.getString(i, "username"));
            contact.setAvatar(data.getString(i, "avatar"));
            contact.setEmail(data.getString(i, "email"));
            contact.setJobName(data.getString(i, "jobName"));
            contact.setOrgName(data.getString(i, "orgName"));
            contact.setOrgID(data.getString(i, "orgID"));

            StringBuffer pinyin = new StringBuffer();
            StringBuffer ch = new StringBuffer();
            for (int j = 0; j < hanzi.length(); j++) {
                System.out.println("i: " + hanzi.charAt(j));
                pinyin.append(Pinyin.toPinyin(hanzi.charAt(j)).toUpperCase());
                ch.append(Pinyin.toPinyin(hanzi.charAt(j)).substring(0, 1));
            }

            contact.setIs_hx(true);
            contact.setPinyin(pinyin.toString());
            contact.setSimplePinyin(ch.toString());
            contactList.add(contact);
        }
        return contactList;
    }

}
