package com.tg.coloursteward.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.dashuview.library.keep.Cqb_PayUtil;
import com.tg.coloursteward.R;
import com.tg.coloursteward.activity.AccountActivity;
import com.tg.coloursteward.activity.AccountExchangeRecordActivity;
import com.tg.coloursteward.activity.BonusRecordPersonalActivity;
import com.tg.coloursteward.activity.DataShowActivity;
import com.tg.coloursteward.activity.DownloadManagerActivity;
import com.tg.coloursteward.activity.GroupAccountDetailsActivity;
import com.tg.coloursteward.activity.MyBrowserActivity;
import com.tg.coloursteward.activity.PublicAccountActivity;
import com.tg.setting.activity.InviteRegisterActivity;
import com.tg.setting.activity.KeyDoorManagerActivity;
import com.tg.setting.activity.SettingActivity;
import com.tg.user.activity.BindMobileActivity;
import com.tg.user.activity.UserInfoActivity;

import static com.tg.coloursteward.module.MainActivity.getEnvironment;
import static com.tg.coloursteward.module.MainActivity.getPublicParams;


/**
 * 所有link解析 解析文档
 */

public class LinkParseUtil {
    public static void parse(Activity context, String link, String title) {
        if (!TextUtils.isEmpty(link)) {
            if (link.startsWith("http://") || link.startsWith("https://")) {
                Intent intent = new Intent(context, MyBrowserActivity.class);
                intent.putExtra(MyBrowserActivity.KEY_URL, link);
                intent.putExtra(MyBrowserActivity.KEY_TITLE, title);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            } else {
                if (link.startsWith("colourlife://proto") && link.length() > 24) {//colourlife://proto?type=XXX
                    String name = link.substring(24, link.length());
                    Intent it;
                    if (name.equals("redPacket")) {//我的饭票
                        //Environment：true 正式环境 ||  false 测试环境
                        Cqb_PayUtil.getInstance(context).createPay(getPublicParams(), getEnvironment());
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
                    } else if (name.equals("dgzh")) {//对公账户
                        it = new Intent(context, PublicAccountActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else if (name.equals("onlinEarea")) {//数据看板
                        it = new Intent(context, DataShowActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else if (name.equals("jsfp")) {//即时分配
                        it = new Intent(context, AccountActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else if (name.equals("groupAccountDetails")) {//集体奖金包明细
                        it = new Intent(context, GroupAccountDetailsActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else if (name.equals("personalAccountDetails")) {//个人奖金包明细
                        it = new Intent(context, BonusRecordPersonalActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else if (name.equals("personInfo")) {//个人信息
                        it = new Intent(context, UserInfoActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else if (name.equals("bindMobile")) {//绑定手机号
                        it = new Intent(context, BindMobileActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else if (name.equals("entranceGuard")) {//乐开管家
                        it = new Intent(context, KeyDoorManagerActivity.class);
                        context.startActivity(it);
                        context.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else {
                        return;
                    }
                }
            }
        }
    }

    public static void parse(Activity context, String oauth_type, String link, String title) {
        if (!TextUtils.isEmpty(link)) {
            if (link.startsWith("http://") || link.startsWith("https://")) {
                Intent intent = new Intent(context, MyBrowserActivity.class);
                intent.putExtra(MyBrowserActivity.KEY_URL, link);
                intent.putExtra(MyBrowserActivity.KEY_TITLE, title);
                intent.putExtra(MyBrowserActivity.OAUTH2_0, oauth_type);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            } else {
                if (link.startsWith("colourlife://proto") && link.length() > 24) {//colourlife://proto?type=XXX
                    String name = link.substring(24, link.length());
                    Intent it;
                    if (name.equals("redPacket")) {//我的饭票
                        //Environment：true 正式环境 ||  false 测试环境
                        Cqb_PayUtil.getInstance(context).createPay(getPublicParams(), getEnvironment());
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
                    } else if (name.equals("dgzh")) {//对公账户
                        it = new Intent(context, PublicAccountActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else if (name.equals("onlinEarea")) {//数据看板
                        it = new Intent(context, DataShowActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else if (name.equals("jsfp")) {//即时分配
                        it = new Intent(context, AccountActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else if (name.equals("groupAccountDetails")) {//集体奖金包明细
                        it = new Intent(context, GroupAccountDetailsActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else if (name.equals("personalAccountDetails")) {//个人奖金包明细
                        it = new Intent(context, BonusRecordPersonalActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else if (name.equals("personInfo")) {//个人信息
                        it = new Intent(context, UserInfoActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else if (name.equals("bindMobile")) {//绑定手机号
                        it = new Intent(context, BindMobileActivity.class);
                        context.startActivity(it);
                        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else if (name.equals("entranceGuard")) {//乐开管家
                        it = new Intent(context, KeyDoorManagerActivity.class);
                        context.startActivity(it);
                        context.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else {
                        return;
                    }
                }
            }
        }
    }

    /***双乾光彩支付用到**/
    public static void jumpHtmlPay(Context context, String link, String domainName) {
        Intent intent = new Intent(context, MyBrowserActivity.class);
        intent.putExtra(MyBrowserActivity.KEY_URL, link);
        intent.putExtra(MyBrowserActivity.WEBDOMAIN, domainName);
        intent.putExtra(MyBrowserActivity.THRIDSOURCE, false);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    /***双乾光彩支付用到**/
    public static void jumpFromThrid(Context context, String link, String title) {
        Intent intent = new Intent(context, MyBrowserActivity.class);
        intent.putExtra(MyBrowserActivity.KEY_URL, link);
        intent.putExtra(MyBrowserActivity.KEY_TITLE, title);
        intent.putExtra(MyBrowserActivity.THRIDSOURCE, true);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}

