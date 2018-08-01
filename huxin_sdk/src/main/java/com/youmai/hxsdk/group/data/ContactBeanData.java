package com.youmai.hxsdk.group.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.entity.cn.pinyin.Pinyin;
import com.youmai.hxsdk.entity.ModifyContactsBean;
import com.youmai.hxsdk.group.adapter.SearchContactAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2018.03.21 17:30
 * 描述：联系人数据更新
 */
public class ContactBeanData {

    public static final int TYPE_HOME = 0x01;
    public static final int TYPE_GROUP_ADD = 0x02;


    public static List<ContactBean> contactList(Context context, int type) {
        List<ContactBean> contactList = new ArrayList<>();
        String[] names;
        if (type == TYPE_HOME) {
            names = context.getResources().getStringArray(R.array.names_home); //获取
        } else if (type == TYPE_GROUP_ADD) {
            names = context.getResources().getStringArray(R.array.names_group_add); //获取
        } else {
            names = context.getResources().getStringArray(R.array.names_home); //获取
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("park_cache_map", 0);
        String depart_name = sharedPreferences.getString("org_depart_name", "");
        String org_name = null;
        if (TextUtils.isEmpty(org_name)) {
            if (!TextUtils.isEmpty(org_name = sharedPreferences.getString("org_name", ""))) {
                org_name = sharedPreferences.getString("org_name", "");
            } else {
                org_name = "服务集团";
            }
        }
        for (int i = 0; i < names.length; i++) {
            String content = names[i];
            ContactBean bean = addHeadItem(content);
            if (content.contains("组织架构")) {
                bean.setUiType(SearchContactAdapter.TYPE.ORGANIZATION_TYPE.ordinal());
                bean.setOrg_depart_name(org_name);
            } else if (content.contains("我的部门")) {
                bean.setUiType(SearchContactAdapter.TYPE.DEPARTMENT_TYPE.ordinal());
                bean.setOrg_depart_name(depart_name);
            } else if (content.contains("收藏联系人")) {
                bean.setUiType(SearchContactAdapter.TYPE.COLLECT_TYPE.ordinal());
            } else {
                bean.setUiType(SearchContactAdapter.TYPE.CONTACT_TYPE.ordinal());
            }
            contactList.add(bean);
        }
        return contactList;
    }

    public static List<ContactBean> contactList(List<ModifyContactsBean.ContentBean.DataBean> data) {
        List<ContactBean> contactList = new ArrayList<>();

        ContactBean contact;
        for (int i = 0; i < data.size(); i++) {
            contact = new ContactBean();
            ModifyContactsBean.ContentBean.DataBean dataBean = data.get(i);

            String hanzi = dataBean.getName();
            contact.setRealname(hanzi);
            contact.setUsername(dataBean.getUsername());//联系人用户名
            contact.setAvatar(dataBean.getIcon());//头像
            contact.setEmail(dataBean.getEmail());//邮箱
            contact.setJobName(dataBean.getJobName());//岗位名称
            contact.setOrgID(String.valueOf(dataBean.getFamily()));//组织架构uuid
            contact.setFavoriteid(String.valueOf(dataBean.getFavoriteid()));
            String accountUuid = data.get(i).getAccountUuid();
            contact.setUuid(accountUuid);


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


    private static ContactBean addHeadItem(String hanzi) {
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
