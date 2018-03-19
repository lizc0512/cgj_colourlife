package com.youmai.thirdbiz;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.youmai.hxsdk.recyclerview.page.CallFunctionItem;
import com.youmai.hxsdk.recyclerview.page.FunctionItem;
import com.youmai.thirdbiz.colorful.CgjUser;
import com.youmai.thirdbiz.colorful.CzyUser;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2017-02-12 10:52
 * Description:
 */
public class ThirdBizMgr {

    private static ThirdBizMgr instance = null;

    private ThirdBizMgr() {

    }

    public synchronized static ThirdBizMgr getInstance() {
        if (instance == null) {
            instance = new ThirdBizMgr();
        }
        return instance;
    }

    private OnThirdCallback onThirdCallback;

    public CgjUser cgjUser;  //彩管家

    public CzyUser czyUser; //彩之云


    public OnThirdCallback getOnThirdCallback() {
        return onThirdCallback;
    }

    public void setOnThirdCallback(OnThirdCallback onThirdCallback) {
        this.onThirdCallback = onThirdCallback;
    }

    private INotifyDataChanged mNotifyDataCallBack, mNotifyFullCallBack;

    public INotifyDataChanged getNotifyChanged() {
        return mNotifyDataCallBack;
    }

    public void setNotifyChanged(INotifyDataChanged<FunctionItem> callBack) {
        mNotifyDataCallBack = callBack;
    }

    public INotifyDataChanged getNotifyFullCallBack() {
        return mNotifyFullCallBack;
    }

    public void setNotifyFullCallBack(INotifyDataChanged<CallFunctionItem> mNotifyFullCallBack) {
        this.mNotifyFullCallBack = mNotifyFullCallBack;
    }

    /**
     * 第三方初始化.
     * @param context
     * @param container
     * @param ll_parent
     */
    public void initThirdBiz(Context context, FrameLayout container, LinearLayout ll_parent) {
        //彩管家
        cgjUser = new CgjUser(this, context, container, ll_parent);

        //彩之云
        czyUser = new CzyUser(this, context, container, ll_parent);
    }

    /**
     * 设置电话号码
     * @param phone
     */
    public void setDstPhone(String phone) {
        if (cgjUser != null) {
            cgjUser.setDstPhone(phone);
        }
        if (czyUser != null) {
            czyUser.setDstPhone(phone);
        }

    }

    /**
     * 执行第三方逻辑
     */
    public void handleThirdBiz(Context context) {
        if (ThirdBizHelper.isAppInstall(context, CgjUser.PACKAGE_NAME)) { //彩管家已安装
            if (cgjUser != null) {
                cgjUser.handleThirdBiz();
            }
        } else if (ThirdBizHelper.isAppInstall(context, CzyUser.PACKAGE_NAME)) { //彩之云已安装
            if (czyUser != null) {
                czyUser.handleThirdBiz();
            }
        }
    }

    public void handleAferFloat(Activity context, RelativeLayout linearLayout, View moreView) {
        if (ThirdBizHelper.isAppInstall(context, CgjUser.PACKAGE_NAME)) { //彩管家已安装
            if (cgjUser != null) {
                cgjUser.handlerAfterFloat(context, linearLayout, moreView);
            }
        } else if (ThirdBizHelper.isAppInstall(context, CzyUser.PACKAGE_NAME)) { //彩之云已安装
            if (czyUser != null) {
                czyUser.handlerAfterFloat(context, linearLayout, moreView);
            }
        }
    }
}
