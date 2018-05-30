package com.tg.coloursteward.module.contact.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.tg.coloursteward.module.groupchat.SearchContactAdapter;
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
    public static final int TYPE_GROUP_ADD = 0x02;

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
        String[] names;
        if (type == TYPE_HOME) {
            names = context.getResources().getStringArray(R.array.names_home); //获取
        } else if (type == TYPE_GROUP_ADD) {
            names = context.getResources().getStringArray(R.array.names_group_add); //获取
        } else {
            names = context.getResources().getStringArray(R.array.names_home); //获取
        }

        for (int i = 0; i < names.length; i++) {
            String content = names[i];
            ContactBean bean = addHeadItem(content);
            if (content.contains("组织架构")) {
                bean.setUiType(SearchContactAdapter.TYPE.ORGANIZATION_TYPE.ordinal());
            } else if (content.contains("我的部门")) {
                bean.setUiType(SearchContactAdapter.TYPE.DEPARTMENT_TYPE.ordinal());
            } else if (content.contains("收藏联系人")) {
                bean.setUiType(SearchContactAdapter.TYPE.COLLECT_TYPE.ordinal());
            } else {
                bean.setUiType(SearchContactAdapter.TYPE.CONTACT_TYPE.ordinal());
            }
            contactList.add(bean);
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

            String uuid = data.getString(i, "contactsId");
            if (TextUtils.isEmpty(uuid)) {
                Toast.makeText(context, "收藏联系人接口错误" + hanzi + "的uuid为空", Toast.LENGTH_SHORT).show();
            }

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
            contact.setUiType(SearchContactAdapter.TYPE.CONTACT_TYPE.ordinal());
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
