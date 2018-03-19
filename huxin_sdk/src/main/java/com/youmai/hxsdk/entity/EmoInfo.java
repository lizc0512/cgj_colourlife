package com.youmai.hxsdk.entity;


import android.content.Context;

import com.youmai.hxsdk.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by colin on 2016/8/8.
 */
public class EmoInfo {

    private static final int[] ITEM_DRAWABLES = {
            R.drawable.hx_cheers, R.drawable.hx_handclap, R.drawable.hx_flower, R.drawable.hx_laud,
            R.drawable.hx_hi, R.drawable.hx_bye, R.drawable.hx_icome,
            R.drawable.hx_kb, R.drawable.hx_kiss, R.drawable.hx_laugh};

    private static final int[] ITEM_DRAWABLES_T = {
            R.drawable.hx_cheers_t, R.drawable.hx_handclap_t, R.drawable.hx_flower_t, R.drawable.hx_laud_t,
            R.drawable.hx_hi_t, R.drawable.hx_bye_t, R.drawable.hx_icome_t,
            R.drawable.hx_kb_t, R.drawable.hx_kiss_t, R.drawable.hx_laugh_t};

    private static final String[] ITEM_STRS = {"/hx_cheers", "/hx_handclap", "/hx_flower", "/hx_laud",
            "/hx_hi", "/hx_bye", "/hx_icome", "/hx_kb", "/hx_kiss", "/hx_laugh"};

    private static final int[] ITEM_SOUND = {R.raw.cheers, R.raw.handclap, R.raw.flower, R.raw.laud,  //新增表情未提供声音
            R.raw.hi, R.raw.bye, R.raw.icome, R.raw.kb, R.raw.kiss, R.raw.laugh};

    private List<EmoItem> emoList;

    public EmoInfo(Context context) {
        String[] ITEM_NAMES = context.getResources().getStringArray(R.array.emo_item_names);
        emoList = new ArrayList<>();
        if (ITEM_DRAWABLES.length == ITEM_STRS.length) {
            for (int i = 0; i < ITEM_DRAWABLES.length; i++) {
                EmoItem item = new EmoItem();
                item.emoRes = ITEM_DRAWABLES[i];
                item.emoStr = ITEM_STRS[i];
                item.emoName = ITEM_NAMES[i];
                item.soundId = ITEM_SOUND[i];
                item.emoTRes = ITEM_DRAWABLES_T[i];
                emoList.add(item);
            }
        }
    }

    public boolean isEmotion(String str) {
        for (String item : ITEM_STRS) {
            if (item.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public int getEmoRes(String str) {
        for (int i = 0; i < ITEM_STRS.length; i++) {
            if (ITEM_STRS[i].equals(str)) {
                return ITEM_DRAWABLES[i];
            }
        }
        return -1;
    }

    public List<EmoItem> getEmoList() {
        return emoList;
    }

    public static class EmoItem {
        String emoStr;
        int emoRes;
        String emoName;
        int soundId;
        int emoTRes;

        public String getEmoStr() {
            return emoStr;
        }

        public int getEmoRes() {
            return emoRes;
        }

        public String getEmoName() {
            return emoName;
        }

        public int getSoundId() {
            return soundId;
        }

        public int getEmoTRes() {
            return emoTRes;
        }
    }
}
