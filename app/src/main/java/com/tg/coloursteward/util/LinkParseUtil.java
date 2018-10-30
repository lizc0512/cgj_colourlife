package com.tg.coloursteward.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.tg.coloursteward.AccountExchangeRecordActivity;
import com.tg.coloursteward.ChangePhoneActivity;
import com.tg.coloursteward.DownloadManagerActivity;
import com.tg.coloursteward.InviteRegisterActivity;
import com.tg.coloursteward.MyBrowserActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.RedpacketsBonusMainActivity;
import com.tg.coloursteward.SettingActivity;


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
                if (link.startsWith("colourlife://proto") && link.length() > 24) {//colourlife://proto?type=XXX
                    String name = link.substring(24, link.length());
                    Intent it;
                    if (name.equals("redPacket")) {//我的饭票
                        it = new Intent(context, RedpacketsBonusMainActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else if (name.equals("invite")) {//邀请界面
                        it = new Intent(context, InviteRegisterActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else if (name.equals("setting")) {//设置页面
                        it = new Intent(context, SettingActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else if (name.equals("mydownload")) {//我的下载
                        it = new Intent(context, DownloadManagerActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else if (name.equals("dhRecord")) {//即时分配-兑换记录
                        it = new Intent(context, AccountExchangeRecordActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else if(name.equals("changephone")){//更换手机号
                        it=new Intent(context, ChangePhoneActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else {
                        return;
                    }
                }
            }
        }
    }
}

