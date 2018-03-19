package com.youmai.thirdbiz.colorful;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.recyclerview.page.CallFunctionItem;
import com.youmai.hxsdk.recyclerview.page.CallFunctionViewUtils;
import com.youmai.hxsdk.recyclerview.page.FunctionItem;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.view.full.FullItemView;
import com.youmai.thirdbiz.BaseThirdBizEntry;
import com.youmai.thirdbiz.ThirdBizHelper;
import com.youmai.thirdbiz.ThirdBizMgr;

/**
 * Author:  Kevin Feng
 * Email:   597415099@qq.com
 * Date:    2017-02-12 17:34
 * Description:
 */
public class CzyUser extends BaseThirdBizEntry {

    public static final String PACKAGE_NAME = "cn.net.cyberway";

    public CzyUser(ThirdBizMgr thirdBizMgr, Context context, FrameLayout container, LinearLayout ll_parent) {
        super(thirdBizMgr, context, container, ll_parent);
    }

    @Override
    public void initView() {

    }

    @Override
    public void handleThirdBiz() {
        String activityClass = ThirdBizHelper.readThirdBizActivity(PACKAGE_NAME);
        if (activityClass != null) {
            if (!ListUtils.isEmpty(mCallFunctionList)) {
                mCallFunctionList.clear();
            }

            mCallFunctionList.add(new CallFunctionItem(mContext.getString(
                    CallFunctionViewUtils.ITEM_NAMES[6]), CallFunctionViewUtils.ITEM_100, "", "", PACKAGE_NAME));

            //容器回调
            if (mThirdBizMgr != null && mThirdBizMgr.getNotifyFullCallBack() != null) {
                mThirdBizMgr.getNotifyFullCallBack().notifyData(mCallFunctionList);
            }
        }
    }

    /**
     * 扫码开门
     */
    private void addScanDoor(int width, final String activityClass) {

        FullItemView itemEmo = new FullItemView(mContext);
        LinearLayout.LayoutParams lParamsEmo = new LinearLayout.LayoutParams(width, FrameLayout.LayoutParams.WRAP_CONTENT);

        itemEmo.setText(mContext.getString(R.string.open_door));
        itemEmo.setIvImg(R.drawable.cgj_btn_full_scandoor);

        mFunContainer.addView(itemEmo, 0, lParamsEmo);

        itemEmo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FloatViewUtil.instance().hideFloatView();
                //FloatLogoUtil.instance().showFloat(mContext, HuxinService.MODEL_TYPE_FULL, false);
                HuxinSdkManager.instance().hideFloatView();
                try {
                    Intent it = new Intent();
                    it.setClassName(PACKAGE_NAME, activityClass);
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    LogUtils.e("CzyUser", "pack=" + PACKAGE_NAME + " act=" + activityClass);
                    mContext.startActivity(it);
                } catch (Exception e) {
                    LogUtils.e("CzyUser", e.toString());
                }

            }
        });

    }

    @Override
    public void handlerAfterFloat(Activity context, RelativeLayout relativeLayout, View moreView) {
        if (context == null || relativeLayout == null) {
            return;
        }
        if (moreView != null) {
            moreView.setVisibility(View.GONE);
        }
        if (!ListUtils.isEmpty(mFunctionList)) {
            mFunctionList.clear();
        }

        final String activityClass = ThirdBizHelper.readThirdBizActivity(PACKAGE_NAME);
        if (activityClass != null) {
            mFunctionList.add(new FunctionItem(R.drawable.cgj_after_opendoor_selector, mContext.getString(R.string.open_door), PACKAGE_NAME));
        }

        if (mThirdBizMgr != null && mThirdBizMgr.getNotifyChanged() != null) {
            mThirdBizMgr.getNotifyChanged().notifyData(mFunctionList);
        }
    }
}
