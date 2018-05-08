package com.tg.coloursteward.module.contact.utils;

import android.content.Context;

import com.tg.coloursteward.net.ResponseData;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.entity.cn.CN;
import com.youmai.hxsdk.entity.cn.pinyin.Pinyin;

import java.util.ArrayList;
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
    public static final int TYPE_ADD_CONTACT_NO_HEADER = 0x03;

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

    public void onDestroy() {
        instance = null;
    }

    public void initData(List<? extends CN> data) {
        setChanged();
        notifyObservers(null);
    }

    public List<ContactBean> contactList(Context context, ResponseData data, int type) {
        List<ContactBean> contactList = new ArrayList<>();
        ContactBean contact;

        if (type == TYPE_HOME) {
            String[] names = context.getResources().getStringArray(R.array.names_collect_contact); //获取
            for (int i = 0; i < names.length; i++) {
                contactList.add(addHeadItem(names[i]));
            }
        } if (type == TYPE_ADD_CONTACT_NO_HEADER) {
            String[] names = context.getResources().getStringArray(R.array.names_add_contact2); //获取
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

            contact = new ContactBean();
            String hanzi = data.getString(i, "realname");
            contact.setRealname(hanzi);
            contact.setUsername(data.getString(i, "username"));
            contact.setAvatar(data.getString(i, "avatar"));
            contact.setEmail(data.getString(i, "email"));
            contact.setJobName(data.getString(i, "jobName"));
            contact.setOrgName(data.getString(i, "orgName"));
            contact.setOrgID(data.getString(i, "orgID"));
            contact.setUuid(data.getString(i, "contactsId"));

            StringBuilder pinyin = new StringBuilder();
            StringBuilder ch = new StringBuilder();
            for (int j = 0; j < hanzi.length(); j++) {
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

    ContactBean addHeadItem(String hanzi) {
        ContactBean contact = new ContactBean();
        StringBuffer pinyin = new StringBuffer();
        StringBuffer ch = new StringBuffer();
        List<String> chStr = new ArrayList<>(); //每个汉字的 拼音集合
        for (int j = 0; j < hanzi.length(); j++) {
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
