package com.youmai.hxsdk.recyclerview.page;

import android.content.Context;

import com.youmai.hxsdk.R;
import com.youmai.hxsdk.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：create by YW
 * 日期：2017.05.12 10:39
 * 描述：
 */
public class FunctionViewUtils {

    public static final int[] ITEM_DRAWABLES = {
            R.drawable.hx_call_after_recall, R.drawable.hx_call_after_call,
            R.drawable.hx_call_after_message, R.drawable.hx_call_afterl_card,
            R.drawable.cgj_after_opendoor_selector, R.drawable.cgj_after_feeback_selector,
            R.drawable.cgj_after_addowner_selector,

            R.drawable.hx_call_after_pic, R.drawable.hx_call_after_location,
            R.drawable.hx_call_after_file, R.drawable.hx_call_after_remark,
            R.drawable.hx_call_after_add, R.drawable.hx_call_after_camera};

    public static final int[] ITEM_STRS = {
            R.string.hx_sdk_hook_layout_11, R.string.hx_sdk_hook_layout_12,
            R.string.hx_sdk_hook_layout_13, R.string.hx_sdk_hook_layout_14,
            R.string.open_door, R.string.hx_feedbace, R.string.add_owner,

            R.string.hx_full_hook_view_pic, R.string.hx_full_hook_view_location,
            R.string.hx_full_hook_view_file, R.string.hx_full_hook_view_remark,
            R.string.hx_sdk_hook_layout_add, R.string.hx_full_hook_view_camera};

    private static FunctionViewUtils instance;
    private Context mContext;

    public static FunctionViewUtils instance(Context context) {
        if (instance == null) {
            instance = new FunctionViewUtils(context.getApplicationContext());
        }
        return instance;
    }

    private FunctionViewUtils(Context context) {
        mContext = context;
    }

    private List<FunctionItem> mItemList = new ArrayList<>();

    public void addItem(Integer itemValue, String itemName) {
        addItemList(new FunctionItem(itemValue, itemName, ""));
    }

    private void addItemList(FunctionItem item) {
        mItemList.add(item);
    }

    public List<FunctionItem> getItemList() {
        return mItemList;
    }

    public void addFuncList(boolean isPerson) {
        if (isPerson) {
            addDefaultItem();
        } else {
            addBizItem();
        }
    }

    public void clearItemList() {
        if (!ListUtils.isEmpty(mItemList)) {
            mItemList.clear();
        }
        instance = null;
    }

    private void addDefaultItem() {
        //重拨
        addItem(ITEM_DRAWABLES[0], mContext.getString(ITEM_STRS[0]));

        //拨号
        addItem(ITEM_DRAWABLES[1], mContext.getString(ITEM_STRS[1]));

        //图片
        addItem(ITEM_DRAWABLES[7], mContext.getString(ITEM_STRS[7]));

        //位置
        addItem(ITEM_DRAWABLES[8], mContext.getString(ITEM_STRS[8]));

        //拍摄
        addItem(ITEM_DRAWABLES[12], mContext.getString(ITEM_STRS[12]));

        //文件
        addItem(ITEM_DRAWABLES[9], mContext.getString(ITEM_STRS[9]));

        //备注
        addItem(ITEM_DRAWABLES[10], mContext.getString(ITEM_STRS[10]));
    }

    private void addBizItem() {
        //重拨
        addItem(ITEM_DRAWABLES[0], mContext.getString(ITEM_STRS[0]));

        //拨号
        addItem(ITEM_DRAWABLES[1], mContext.getString(ITEM_STRS[1]));

        //图片
        addItem(ITEM_DRAWABLES[7], mContext.getString(ITEM_STRS[7]));

        //位置
        addItem(ITEM_DRAWABLES[8], mContext.getString(ITEM_STRS[8]));

        //拍摄
        addItem(ITEM_DRAWABLES[12], mContext.getString(ITEM_STRS[12]));

        //文件
        addItem(ITEM_DRAWABLES[9], mContext.getString(ITEM_STRS[9]));

        //备注
        addItem(ITEM_DRAWABLES[10], mContext.getString(ITEM_STRS[10]));
    }

    //非本地通讯录联系人按钮
    public List<FunctionItem> addNotContactsItem() {
        List<FunctionItem> list = new ArrayList<>();
        //添加
        list.add(new FunctionItem(ITEM_DRAWABLES[11], mContext.getString(ITEM_STRS[11]), ""));
        //递名片
        list.add(new FunctionItem(ITEM_DRAWABLES[3], mContext.getString(ITEM_STRS[3]), ""));
        return list;
    }
}
