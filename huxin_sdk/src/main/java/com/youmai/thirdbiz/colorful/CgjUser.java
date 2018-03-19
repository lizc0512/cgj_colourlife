package com.youmai.thirdbiz.colorful;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.db.dao.OwnerDataDao;
import com.youmai.hxsdk.db.manager.GreenDbManager;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.recyclerview.page.CallFunctionItem;
import com.youmai.hxsdk.recyclerview.page.CallFunctionViewUtils;
import com.youmai.hxsdk.recyclerview.page.FunctionItem;
import com.youmai.hxsdk.service.HuxinService;
import com.youmai.hxsdk.utils.FloatLogoUtil;
import com.youmai.hxsdk.utils.ListUtils;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.view.full.FloatViewUtil;
import com.youmai.hxsdk.view.full.FullItemView;
import com.youmai.thirdbiz.BaseThirdBizEntry;
import com.youmai.thirdbiz.ThirdBizHelper;
import com.youmai.thirdbiz.ThirdBizMgr;
import com.youmai.hxsdk.db.bean.OwnerData;
import com.youmai.thirdbiz.colorful.net.ColorsUtil;
import com.youmai.thirdbiz.colorful.ui.FeedBackActivity;
import com.youmai.thirdbiz.colorful.ui.SelectCommunityActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by colin on 2017/2/9.
 */

public class CgjUser extends BaseThirdBizEntry {

    public static final String PACKAGE_NAME = "com.ewit.colourlifepmnew.activity";

    private View mView;

    private boolean hasDataInDB = false;

    public boolean isOwner = false;
    public ImageView iv_owner;
    private TextView tv_owner_name, tv_house, tv_bulid, tv_room;
    private LinearLayout linear_owner;

    private FullItemView openDoorView = null;
    private FullItemView feebackView = null;
    private FullItemView addOwnerView = null;

    public CgjUser(ThirdBizMgr thirdBizMgr, Context context, FrameLayout container, LinearLayout ll_parent) {
        super(thirdBizMgr, context, container, ll_parent);
    }


    @Override
    public void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.hx_colorlife_header, null);
        //todo_k: cgj start
        tv_owner_name = (TextView) mView.findViewById(R.id.tv_owner_name);//业主姓名
        tv_house = (TextView) mView.findViewById(R.id.tv_house);//小区
        tv_bulid = (TextView) mView.findViewById(R.id.tv_storied_building);//楼栋
        tv_room = (TextView) mView.findViewById(R.id.tv_room_number);//房号
        iv_owner = (ImageView) mView.findViewById(R.id.iv_is_owner);//是否业主
        linear_owner = (LinearLayout) mView.findViewById(R.id.linear_owner);
    }


    /**
     * 处理彩管家业务(cgj)
     */
    @Override
    public void handleThirdBiz() {
        //get db
        /*ColorOwerDataDao colorOwerDataDao = new ColorOwerDataDao(mContext);
        colorOwerDataDao.startReadableDatabase();
        List<OwnerData> ownerDataList = colorOwerDataDao.queryList("msisdn=?", new String[]{dstPhone});
        colorOwerDataDao.closeDatabase();*/

        OwnerDataDao ownerDataDao = GreenDbManager.instance(mContext).getOwnerData();
        List<OwnerData> ownerDataList = ownerDataDao.queryBuilder().where(OwnerDataDao.Properties.Msisdn.eq(dstPhone)).list();
        Long id = 0L;
        if (ownerDataList != null && ownerDataList.size() > 0) {
            hasDataInDB = true;
            Log.e("xx", "has data");
            OwnerData ownerData = ownerDataList.get(0);
            id = ownerData.getId();

            mUIContainer.removeAllViews();
            mUIContainer.addView(mView);

            String name = ownerData.getVname();
            String house = ownerData.getSmallarea_name();
            String bulid = ownerData.getHousetype_name();
            String room = ownerData.getUnit_name() + ownerData.getRoomno();

            //显示业主信息
            setOwnerName(name);
            setHouseInfo(house);
            setBulidInfo(bulid);
            setRoomInfo(room);
            updateInfo();
            iv_owner.setImageResource(R.drawable.cgj_image_owner);

        } else {
            Log.e("xx", "has no data");
            hasDataInDB = false;
        }

        final Long fid = id;
        ColorsUtil.reqOwnerInfo(dstPhone, new IGetListener() {

            @Override
            public void httpReqResult(String response) {
                LogUtils.e("xx", "info coming = " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("message");
                    JSONArray jsonArray = jsonObject.optJSONArray("content");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        JSONObject ownerInfoJObj = jsonArray.optJSONObject(0);
                        if (ownerInfoJObj != null) {

                            String name = ownerInfoJObj.optString("vname");
                            String house = ownerInfoJObj.optString("smallarea_name");
                            String bulid = ownerInfoJObj.optString("housetype_name");
                            String room = ownerInfoJObj.optString("unit_name") + ownerInfoJObj.optString("roomno");

                            OwnerData owerData = new OwnerData();
                            owerData.setBool_ower(1);
                            owerData.setVname(name);
                            owerData.setMsisdn(dstPhone);
                            owerData.setHousetype_name(bulid);
                            owerData.setSmallarea_name(house);
                            owerData.setRoomno(ownerInfoJObj.optString("roomno"));
                            owerData.setUnit_name(ownerInfoJObj.optString("unit_name"));
                            if (fid != 0) {
                                owerData.setId(fid);
                            }

                            OwnerDataDao ownerDataDao = GreenDbManager.instance(mContext).getOwnerData();
                            ownerDataDao.insertOrReplace(owerData);

                            if (!hasDataInDB) {
                                mUIContainer.removeAllViews();
                                mUIContainer.addView(mView);

                                //显示业主信息
                                setOwnerName(name);
                                setHouseInfo(house);
                                setBulidInfo(bulid);
                                setRoomInfo(room);
                                updateInfo();
                                iv_owner.setImageResource(R.drawable.cgj_image_owner);
                            }
                        }
                    } else {
                        //显示非业主信息
                        Log.e("xx", "非业主");
                        iv_owner.setImageResource(R.drawable.cgj_image_noowner);

                        setListener();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "请求失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void setOwnerName(String name) {
        if (linear_owner.getVisibility() == View.GONE) {
            linear_owner.setVisibility(View.VISIBLE);
        }
        tv_owner_name.setText(name);
    }

    private void setHouseInfo(String name) {
        if (linear_owner.getVisibility() == View.GONE) {
            linear_owner.setVisibility(View.VISIBLE);
        }
        tv_house.setText(name);
    }

    private void setBulidInfo(String name) {
        if (linear_owner.getVisibility() == View.GONE) {
            linear_owner.setVisibility(View.VISIBLE);
        }
        tv_bulid.setText(name);
    }

    private void setRoomInfo(String name) {
        if (linear_owner.getVisibility() == View.GONE) {
            linear_owner.setVisibility(View.VISIBLE);
        }
        tv_room.setText(name);
    }

    /**
     * 扫码开门
     */
    private void addScanDoor(final String activityClass) {
        if (openDoorView == null) {
            openDoorView = new FullItemView(mContext);
        } else {
            int index = mFunContainer.indexOfChild(openDoorView);
            if (index != -1) {
                mFunContainer.removeViewAt(index);
            }
        }

        LinearLayout.LayoutParams lParamsEmo = new LinearLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        openDoorView.setText(mContext.getString(R.string.open_door));
        openDoorView.setIvImg(R.drawable.cgj_btn_full_scandoor);


        if (openDoorView.getParent() == null) {
            mFunContainer.addView(openDoorView, 0, lParamsEmo);
        }
        openDoorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // StatisticsMgr.instance().addEvent(StatsIDConst.ADD_OWNER_FINISH);
                //弹屏中扫码开门统计
                FloatViewUtil.instance().hideFloatView();
                FloatLogoUtil.instance().showFloat(mContext, HuxinService.MODEL_TYPE_FULL, false);
                try {
                    Intent it = new Intent();
                    it.setClassName(PACKAGE_NAME, activityClass);
                    it.putExtra("extra_float", 1);
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.e("xx", "pack=" + PACKAGE_NAME + " act=" + activityClass);
                    mContext.startActivity(it);
                } catch (Exception e) {
                    LogUtils.e("CzyUser", e.toString());
                }

            }
        });

    }

    /**
     * 意见反馈
     */
    private void addFeeback() {
        if (feebackView == null) {
            feebackView = new FullItemView(mContext);
        } else {
            int index = mFunContainer.indexOfChild(feebackView);
            if (index != -1) {
                mFunContainer.removeViewAt(index);
            }
        }

        LinearLayout.LayoutParams lParamsJokes = new LinearLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        feebackView.setText(mContext.getString(R.string.hx_feedbace));
        feebackView.setIvImg(R.drawable.cgj_btn_full_complaints);

        mFunContainer.addView(feebackView, 0, lParamsJokes);

        feebackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setClass(mContext, FeedBackActivity.class);
                mContext.startActivity(intent);
            }
        });

    }

    /**
     * 设为业主
     */
    private void addOwner() {
        if (addOwnerView == null) {
            addOwnerView = new FullItemView(mContext);
        } else {
            int index = mFunContainer.indexOfChild(addOwnerView);
            if (index != -1) {
                mFunContainer.removeViewAt(index);
            }
        }

        LinearLayout.LayoutParams lParamsJokes = new LinearLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        addOwnerView.setText(mContext.getString(R.string.add_owner));
        addOwnerView.setIvImg(R.drawable.cgj_btn_full_owner);

        mFunContainer.addView(addOwnerView, 0, lParamsJokes);

        addOwnerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通话中添加业主的统计添加按钮的弹屏次数

                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setClass(mContext, SelectCommunityActivity.class);

                LogUtils.e("xx", dstPhone);
                intent.putExtra("dstPhone", dstPhone);

                mContext.startActivity(intent);

            }
        });
    }


    private void updateInfo() {
        isOwner = true;
        setListener();
    }


    /**
     * 动态监听定制数据
     */
    private void setListener() {

        if (!ListUtils.isEmpty(mCallFunctionList)) {
            mCallFunctionList.clear();
        }

        String activityClass = ThirdBizHelper.readThirdBizActivity(PACKAGE_NAME);
        if (activityClass != null) {
            //addScanDoor(activityClass);
            mCallFunctionList.add(new CallFunctionItem(
                    mContext.getString(CallFunctionViewUtils.ITEM_NAMES[6]), CallFunctionViewUtils.ITEM_100, "", "", PACKAGE_NAME));
        }

        if (isOwner) {
            /*if (addOwnerView != null) {
                mFunContainer.removeView(addOwnerView);
                addOwnerView = null;
            }
            addFeeback();*/
            mCallFunctionList.add(new CallFunctionItem(
                    mContext.getString(CallFunctionViewUtils.ITEM_NAMES[7]), CallFunctionViewUtils.ITEM_101, "", "", PACKAGE_NAME));
        } else {
            /*if (feebackView != null) {
                mFunContainer.removeView(feebackView);
                feebackView = null;
            }
            addOwner();*/
            mCallFunctionList.add(new CallFunctionItem(
                    mContext.getString(CallFunctionViewUtils.ITEM_NAMES[8]), CallFunctionViewUtils.ITEM_102, "", "", PACKAGE_NAME));
        }

        //容器回调
        /*if (mThirdBizMgr != null && mThirdBizMgr.getOnThirdCallback() != null) {
            mThirdBizMgr.getOnThirdCallback().onFunctionCallback(mFunContainer);
        }*/

        //容器回调
        if (mThirdBizMgr != null && mThirdBizMgr.getNotifyFullCallBack() != null) {
            mThirdBizMgr.getNotifyFullCallBack().notifyData(mCallFunctionList);
        }
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
        if (isOwner) {
            mFunctionList.add(new FunctionItem(R.drawable.cgj_after_feeback_selector, mContext.getString(R.string.hx_feedbace), PACKAGE_NAME));
        } else {
            mFunctionList.add(new FunctionItem(R.drawable.cgj_after_addowner_selector, mContext.getString(R.string.add_owner), PACKAGE_NAME));
        }

        if (mThirdBizMgr != null && mThirdBizMgr.getNotifyChanged() != null) {
            mThirdBizMgr.getNotifyChanged().notifyData(mFunctionList);
        }
    }

}
