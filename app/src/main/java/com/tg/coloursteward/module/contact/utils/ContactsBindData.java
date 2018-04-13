package com.tg.coloursteward.module.contact.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tg.coloursteward.module.contact.pinyin.Pinyin;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.db.bean.Contact;
import com.youmai.hxsdk.entity.cn.CN;
import com.youmai.hxsdk.entity.cn.DuoYinZi;
import com.youmai.hxsdk.entity.cn.SearchContactBean;
import com.youmai.hxsdk.proto.YouMaiBuddy;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.smallvideorecord.utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;

/**
 * 作者：create by YW
 * 日期：2018.03.21 17:30
 * 描述：联系人数据更新
 */
public class ContactsBindData extends Observable {

    private List<CN> mData = new ArrayList<>();

    private static ContactsBindData instance;

    public static ContactsBindData init() {
        synchronized (InputMethodManager.class) {
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

    public void loadOrgInfo() {

        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiBuddy.IMGetOrgRsp rsp = YouMaiBuddy.IMGetOrgRsp.parseFrom(pduBase.body);
                    List<YouMaiBuddy.OrgInfo> list = rsp.getOrgListList();

                    for (YouMaiBuddy.OrgInfo item : list) {
                        String test1 = item.getAvator();
                        String test2 = item.getName();
                        String test3 = item.getOrgId();
                        String test4 = item.getUsername();
                        int test5 = item.getType();

                    }

                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int errCode) {
                super.onError(errCode);
            }
        };

        HuxinSdkManager.instance().sendOrgInfo(ColorsConfig.ColorLifeAppId_test, callback);
    }

    static int[] URLS = {R.drawable.header0, R.drawable.header1, R.drawable.header2, R.drawable.header3};

    public List<Contact> contactList(Context context) {
        List<Contact> contactList = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());
        String[] names = context.getResources().getStringArray(R.array.names_1); //获取
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
            contact.setAvatar(url + "");
            contact.setNick_name(hanzi);
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
        return contactList;
    }

}
