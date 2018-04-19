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

    public static final int TYPE_HOME = 0x01;
    public static final int TYPE_ADD_CONTACT = 0x02;

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
        setChanged();
        notifyObservers(null);
    }

    public List<Contact> contactList(Context context, ResponseData data, int type) {
        List<Contact> contactList = new ArrayList<>();
        Contact contact;

        if (type == TYPE_HOME) {
            String[] names = context.getResources().getStringArray(R.array.names_collect_contact); //获取
            for (int i = 0; i < names.length; i++) {
                contactList.add(addHeadItem(names[i]));
            }
        } else if (type == TYPE_ADD_CONTACT) {
            String[] names = context.getResources().getStringArray(R.array.names_add_contact); //获取
            for (int i = 0; i < names.length; i++) {
                contactList.add(addHeadItem(names[i]));
            }
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

    Contact addHeadItem(String hanzi) {
        Contact contact = new Contact();
        StringBuffer pinyin = new StringBuffer();
        StringBuffer ch = new StringBuffer();
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
        return contact;
    }

}
