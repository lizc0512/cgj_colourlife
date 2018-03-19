package com.youmai.hxsdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.entity.SoundModel;

import java.util.ArrayList;

/**
 * Created by youmai on 17/2/16.
 */

public class SoundsUtils {
    private static final String FILE_NAME = "HuXinSoundData";
    //背景应用的名字和对应的path
    private static final String FILE_ITEM_SOUND_NAME_1 ="sound_name_1";
    private static final String FILE_ITEM_SOUND_NAME_2 ="sound_name_2";
    private static final String FILE_ITEM_SOUND_NAME_3 ="sound_name_3";

    private static final String FILE_ITEM_SOUND_PATH_1 ="sound_path_1";
    private static final String FILE_ITEM_SOUND_PATH_2 ="sound_path_2";
    private static final String FILE_ITEM_SOUND_PATH_3 ="sound_path_3";

    public static void saveSoundFile(String name,String path,int flag,Context context) {
        SharedPreferences  mSharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor  editor= mSharedPreferences.edit();
        switch (flag) {
            case 1:
                editor.putString(FILE_ITEM_SOUND_NAME_1,name);
                editor.putString(FILE_ITEM_SOUND_PATH_1,path);
                break;
            case 2:
                editor.putString(FILE_ITEM_SOUND_NAME_2,name);
                editor.putString(FILE_ITEM_SOUND_PATH_2,path);
                break;
            case 3:
                editor.putString(FILE_ITEM_SOUND_NAME_3,name);
                editor.putString(FILE_ITEM_SOUND_PATH_3,path);
                break;
        }
        editor.commit();
    }

    public static ArrayList<SoundModel> getSoundData(Context context) {
        SharedPreferences  mSharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        ArrayList<SoundModel> list = new ArrayList<SoundModel>();
        SoundModel model1 = new SoundModel();
        SoundModel model2 = new SoundModel();
        SoundModel model3 = new SoundModel();
        model1.setName(mSharedPreferences.getString(FILE_ITEM_SOUND_NAME_1,""));
        model1.setUrl(mSharedPreferences.getString(FILE_ITEM_SOUND_PATH_1,""));

        model2.setName(mSharedPreferences.getString(FILE_ITEM_SOUND_NAME_2,""));
        model2.setUrl(mSharedPreferences.getString(FILE_ITEM_SOUND_PATH_2,""));

        model3.setName(mSharedPreferences.getString(FILE_ITEM_SOUND_NAME_3,""));
        model3.setUrl(mSharedPreferences.getString(FILE_ITEM_SOUND_PATH_3,""));
        list.add(model1);
        list.add(model2);
        list.add(model3);


        ArrayList<SoundModel> defaultlist = new ArrayList<SoundModel>(3);
        //添加默认背景音
        Uri uri1 = Uri.parse("android.resource://"+context.getPackageName()+"/"+ R.raw.call_bg_one);
        Uri uri2 = Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.call_bg_two);
        Uri uri3 = Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.call_bg_three);
        SoundModel defaultmodel1 = new SoundModel();
        SoundModel defaultmodel2 = new SoundModel();
        SoundModel defaultmodel3 = new SoundModel();
        defaultmodel1.setName(context.getString(R.string.sound_name_one));
        defaultmodel2.setName(context.getString(R.string.sound_name_two));
        defaultmodel3.setName(context.getString(R.string.sound_name_three));
        defaultmodel1.setUrl(uri1.toString());
        defaultmodel2.setUrl(uri2.toString());
        defaultmodel3.setUrl(uri3.toString());
        defaultlist.add(defaultmodel1);
        defaultlist.add(defaultmodel2);
        defaultlist.add(defaultmodel3);
        for (int i = 0;i < list.size();i ++) {
            SoundModel model = list.get(i);
            //如果数据为空，为数组设置默认值
            if (isNullOrIsEmpty(model.getName()) || isNullOrIsEmpty(model.getUrl())) {
                list.set(i,defaultlist.get(i));
            }
        }
        return list;
    }

    private static boolean isNullOrIsEmpty(String string) {
        if (string == null) {
            return true;
        }
        return string.isEmpty();
    }
}