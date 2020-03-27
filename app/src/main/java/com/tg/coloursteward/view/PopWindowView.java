package com.tg.coloursteward.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.tg.coloursteward.R;
import com.tg.coloursteward.activity.CaptureActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.util.MicroAuthTimeUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.db.bean.ContactBean;
import com.youmai.hxsdk.group.AddContactsCreateGroupActivity;

import java.util.ArrayList;
import java.util.List;

public class PopWindowView extends PopupWindow {

    private static PopWindowView instance;
    private View conentView;
    private MicroAuthTimeUtils microAuthTimeUtils;
    private Activity mActivity;

    public static PopWindowView getInstance(Activity activity, View parent) {
        if (null == instance) {
            synchronized (PopWindowView.class) {
                if (null == instance) {
                    instance = new PopWindowView(activity, parent);
                }
            }
        }
        return instance;
    }

    public PopWindowView(final Activity context, View parent) {
        this.mActivity = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popup_window, null);
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽  
        this.setWidth(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高  
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击  
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态  
        this.update();
        // 实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作  
        this.setBackgroundDrawable(dw);
        // 设置SelectPicPopupWindow弹出窗体动画效果  
        this.setAnimationStyle(R.style.AnimationPreview);
        this.setOnDismissListener(() -> {
            WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
            lp.alpha = 1.0f;
            mActivity.getWindow().setAttributes(lp);
        });
        microAuthTimeUtils = new MicroAuthTimeUtils();
        conentView.findViewById(R.id.rl_add_group).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                List<ContactBean> groupList = new ArrayList<>();
                ContactBean self = new ContactBean();
                String selfUid = HuxinSdkManager.instance().getUuid();
                self.setUuid(selfUid);
                self.setAvatar(HuxinSdkManager.instance().getHeadUrl());
                self.setRealname(HuxinSdkManager.instance().getRealName());
                self.setUsername(HuxinSdkManager.instance().getUserName());
                groupList.add(self);

                Intent intent = new Intent(context, AddContactsCreateGroupActivity.class);
                intent.putExtra(AddContactsCreateGroupActivity.DETAIL_TYPE, 1);
                intent.putParcelableArrayListExtra(AddContactsCreateGroupActivity.GROUP_LIST, (ArrayList<? extends Parcelable>) groupList);
                context.startActivity(intent);

                PopWindowView.this.dismiss();
            }
        });

        conentView.findViewById(R.id.rl_mail).setOnClickListener(new OnClickListener() { //邮件 

            @Override
            public void onClick(View arg0) {
                microAuthTimeUtils.IsAuthTime(context, Contants.Html5.YJ, "2", "");
                PopWindowView.this.dismiss();
            }
        });
        conentView.findViewById(R.id.rl_examination).setOnClickListener(new OnClickListener() {  //审批

            @Override
            public void onClick(View arg0) {
                microAuthTimeUtils.IsAuthTime(context, Contants.Html5.SP, "2", "");
                PopWindowView.this.dismiss();
            }
        });
        conentView.findViewById(R.id.rl_sign).setOnClickListener(new OnClickListener() { // 签到

            @Override
            public void onClick(View arg0) {
                microAuthTimeUtils.IsAuthTime(context, Contants.Html5.QIANDAO, "2", "");
                PopWindowView.this.dismiss();
            }
        });
        conentView.findViewById(R.id.rl_saoyisao).setOnClickListener(new OnClickListener() { // 扫一扫

            @Override
            public void onClick(View arg0) {
                XXPermissions.with(context)
                        .constantRequest()
                        .permission(Manifest.permission.CAMERA)
                        .request(new OnPermission() {
                            @Override
                            public void hasPermission(List<String> granted, boolean isAll) {
                                context.startActivity(new Intent(context, CaptureActivity.class));
                                PopWindowView.this.dismiss();
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean quick) {
                                ToastUtil.showShortToast(context, "拍照权限被拒绝，请到设置中打开");
                            }
                        });


            }
        });
    }

    private void lightoff() {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.3f;
        mActivity.getWindow().setAttributes(lp);
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow  
            this.showAsDropDown(parent, 0, 0);
            lightoff();
        } else {
            this.dismiss();
        }
    }
}  