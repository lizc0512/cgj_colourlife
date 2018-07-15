package com.tg.coloursteward.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.tg.coloursteward.AccountExchangeActivity;
import com.tg.coloursteward.MyBrowserActivity;
import com.tg.coloursteward.R;


/**
 * 所有link解析 解析文档
 */

public class LinkParseUtil {
    public static void parse(Activity context, String link, String title) {
        if (!TextUtils.isEmpty(link)) {
            if (link.startsWith("http://") || link.startsWith("https://")) {
                Intent intent = new Intent(context, MyBrowserActivity.class);
                intent.putExtra(MyBrowserActivity.KEY_URL, link);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            } else {
                if (link.length() > 18) {
                    String name = link.substring(18, link.length());
                    if (name.equals("Exchange")) {//即时分配兑换
                        Intent it = new Intent(context, AccountExchangeActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    }
                }
            }
        }
    }
}

