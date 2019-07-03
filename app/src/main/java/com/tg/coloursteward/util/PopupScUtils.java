package com.tg.coloursteward.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tg.coloursteward.module.PopupActivity;

import java.util.ArrayList;

/**
 * 作者: zhudewei
 * 时间： 2016/12/23 10:51
 * God bless the program never bug
 */

public class PopupScUtils {

    private static PopupScUtils instance = new PopupScUtils();

    public static PopupScUtils getInstance() {
        return instance;
    }

    public void jump(Context context, ArrayList<String> urlData, ArrayList<String> imageData, ArrayList<String> nameData) {
        if (urlData.size() > 0) {
            Intent intent = new Intent(context, PopupActivity.class);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("urldata", urlData);
            bundle.putStringArrayList("imagedata", imageData);
            bundle.putStringArrayList("namedata", nameData);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }
}
