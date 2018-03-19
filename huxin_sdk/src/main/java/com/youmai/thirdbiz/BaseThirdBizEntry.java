package com.youmai.thirdbiz;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.youmai.hxsdk.recyclerview.page.CallFunctionItem;
import com.youmai.hxsdk.recyclerview.page.FunctionItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2017-02-12 10:51
 * Description:
 */
public abstract class BaseThirdBizEntry {

    protected Context mContext;
    protected FrameLayout mUIContainer;
    protected LinearLayout mFunContainer;
    protected String dstPhone;
    protected ThirdBizMgr mThirdBizMgr;

    protected List<FunctionItem> mFunctionList = new ArrayList<>();
    protected List<CallFunctionItem> mCallFunctionList = new ArrayList<>();

    public BaseThirdBizEntry(ThirdBizMgr thirdBizMgr, Context context, FrameLayout container, LinearLayout ll_parent) {
        mThirdBizMgr = thirdBizMgr;
        mContext = context;
        mUIContainer = container;
        mFunContainer = ll_parent;
        initView();
    }


    public void setDstPhone(String phone) {
        dstPhone = phone;
    }

    public abstract void initView();

    public abstract void handleThirdBiz();

    public void handlerAfterFloat(Activity context, RelativeLayout relativeLayout, View moreView) {

    }
}
