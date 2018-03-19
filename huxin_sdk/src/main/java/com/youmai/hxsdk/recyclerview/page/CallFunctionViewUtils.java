package com.youmai.hxsdk.recyclerview.page;

import android.content.Context;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2017.10.27 17:51
 * 描述：数字对应着弹屏CallFullView的ITEM
 *      1 : 地图
        2 : 图片
        3 : 表情
        4 ：摇段子
        5 - 10：自定义H5
        11: 文件
        12：拍照
        13：背景音
 */
public class CallFunctionViewUtils {

    public static final String ITEM_ONE = "1";
    public static final String ITEM_TWO = "2";
    public static final String ITEM_THREE = "3";
    public static final String ITEM_FOUR = "4";
    public static final String ITEM_FIVE = "5";
    public static final String ITEM_SIX = "6";
    public static final String ITEM_SEVEN = "7";
    public static final String ITEM_EIGHT = "8";
    public static final String ITEM_NINE = "9";
    public static final String ITEM_TEN = "10";
    public static final String ITEM_ELEVEN = "11";
    public static final String ITEM_TWELVE = "12";
    public static final String ITEM_THIRTEEN = "13";
    public static final String ITEM_WHITE_BOARD = "14";
    
    // 第三方 APP
    public static final String ITEM_100 = "100";
    public static final String ITEM_101 = "101";
    public static final String ITEM_102 = "102";

    public static final int[] ITEM_NAMES = {
            R.string.hx_show_full_setting_info_send_location, R.string.hx_show_full_setting_info_send_pic,
            R.string.hx_show_full_setting_info_send_camera, R.string.hx_show_full_setting_info_send_emoji,
            R.string.hx_show_full_setting_info_send_file, R.string.hx_str_full_call_background_sound,
            R.string.hx_show_full_setting_info_shake,
            R.string.open_door, R.string.hx_feedbace,
            R.string.add_owner,R.string.hx_show_full_setting_info_whiteBoard};

    private static CallFunctionViewUtils instance;
    private Context mContext;

    public static CallFunctionViewUtils instance(Context context) {
        if (instance == null) {
            instance = new CallFunctionViewUtils(context.getApplicationContext());
        }
        return instance;
    }

    private CallFunctionViewUtils(Context context) {
        mContext = context;
    }

    private List<CallFunctionItem> mItemList = new ArrayList<>();

    public void addItem(String itemValue, String itemName) {
        addItemList(new CallFunctionItem(itemValue, itemName, "", "", ""));
    }

    private void addItemList(CallFunctionItem item) {
        mItemList.add(item);
    }

    public List<CallFunctionItem> getItemList() {
        return mItemList;
    }

    public void addInitFuncList(boolean isDefault) {
        if (isDefault) {
            addDefaultItem();
        } else {
            addMoreItem();
        }
    }

    public void clearItemList() {
        if (!ListUtils.isEmpty(mItemList)) {
            mItemList.clear();
        }
        instance = null;
    }

    private void addDefaultItem() {

        //位置
        addItem(mContext.getString(ITEM_NAMES[0]), ITEM_ONE);

        //图片
        addItem(mContext.getString(ITEM_NAMES[1]), ITEM_TWO);

        //拍摄
        addItem(mContext.getString(ITEM_NAMES[2]), ITEM_TWELVE);

        //表情
        addItem(mContext.getString(ITEM_NAMES[3]), ITEM_THREE);

        //文件
        addItem(mContext.getString(ITEM_NAMES[4]), ITEM_ELEVEN);

        //段子
        addItem(mContext.getString(ITEM_NAMES[6]), ITEM_FOUR);

        //白板
        addItem(mContext.getString(ITEM_NAMES[10]), ITEM_WHITE_BOARD);
    }

    private void addMoreItem() {

        //位置
        addItem(mContext.getString(ITEM_NAMES[0]), ITEM_ONE);

        //图片
        addItem(mContext.getString(ITEM_NAMES[1]), ITEM_TWO);

        //拍摄
        addItem(mContext.getString(ITEM_NAMES[2]), ITEM_TWELVE);

        //表情
        addItem(mContext.getString(ITEM_NAMES[3]), ITEM_THREE);

        //文件
        addItem(mContext.getString(ITEM_NAMES[4]), ITEM_ELEVEN);

        //段子
        addItem(mContext.getString(ITEM_NAMES[6]), ITEM_FOUR);
        
        //白板
        addItem(mContext.getString(ITEM_NAMES[10]), ITEM_WHITE_BOARD);
        
        //背景音
        addItem(mContext.getString(ITEM_NAMES[5]), ITEM_THIRTEEN);
    }

}
