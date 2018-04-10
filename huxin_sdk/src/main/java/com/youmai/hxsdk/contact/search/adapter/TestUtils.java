package com.youmai.hxsdk.contact.search.adapter;

import android.content.Context;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.contact.pinyin.Pinyin;
import com.youmai.hxsdk.contact.search.cn.Contact;
import com.youmai.hxsdk.contact.search.cn.DuoYinZi;
import com.youmai.hxsdk.contact.search.utils.PinYinUtils;
import com.youmai.hxsdk.contact.search.cn.SearchContactBean;
import com.youmai.smallvideorecord.utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by you on 2017/9/11.
 */

public class TestUtils {

    static int[] URLS = {R.drawable.header0, R.drawable.header1, R.drawable.header2, R.drawable.header3};

    public static List<Contact> contactList(Context context) {
        List<Contact> contactList = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());
        String[] names = context.getResources().getStringArray(R.array.names); //获取
        for (int i = 0; i < names.length; i++) {
            int urlIndex = random.nextInt(URLS.length - 1);
            int url = URLS[urlIndex];

            StringBuffer pinyin = new StringBuffer();
            StringBuffer ch = new StringBuffer();
            String hanzi = names[i];
            for (int j = 0; j < hanzi.length(); j++) {
                System.out.println("i: " + hanzi.charAt(j));
                pinyin.append(Pinyin.toPinyin(hanzi.charAt(j)).toUpperCase());
                ch.append(Pinyin.toPinyin(hanzi.charAt(j)).substring(0, 1));
            }

            Contact contact = new Contact();
            contact.setIconUrl(url);
            contact.setDisplayName(hanzi);
            contact.setPinyin(pinyin.toString());
            contact.setSimplePinyin(ch.toString());
            contactList.add(contact);
        }
        return contactList;
    }

    public static List<SearchContactBean> searchContactsList(Context context) {
        List<SearchContactBean> contactList = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());
        String[] names = context.getResources().getStringArray(R.array.names); //获取


        for (int i = 0; i < names.length; i++) {
            int urlIndex = random.nextInt(URLS.length - 1);
            int url = URLS[urlIndex];

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

            SearchContactBean contact = new SearchContactBean();
            contact.setIconUrl(url);
            contact.setDisplayName(hanzi);
            contact.setWholePinyin(pinyin.toString());
            contact.setSimplepinyin(ch.toString());
            contact.setIndexPinyin(chStr);

            DuoYinZi duoYinZi = PinYinUtils.HanziToPinYin(hanzi);
            contact.setDuoYinzi(duoYinZi);

            Log.e("YW", contact.toString());

            contactList.add(contact);
        }


        //先把第一个汉字拿出来


        return contactList;
    }

}
