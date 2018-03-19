package com.youmai.hxsdk.fragment;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.SettingShowActivity;
import com.youmai.hxsdk.activity.ShowPreviewActivity;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.db.bean.HxUsers;
import com.youmai.hxsdk.db.helper.HxUsersHelper;
import com.youmai.hxsdk.dialog.HxShowCancelDialog;
import com.youmai.hxsdk.dialog.HxTip2Dialog;
import com.youmai.hxsdk.dialog.ShowTipOneDialog;
import com.youmai.hxsdk.entity.ShowCancel;
import com.youmai.hxsdk.entity.UserInfo;
import com.youmai.hxsdk.entity.UserShow;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.sp.SPDataUtil;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.CommonUtils;
import com.youmai.hxsdk.utils.GlideCircleTransform;
import com.youmai.hxsdk.utils.GsonUtil;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.utils.ToastUtil;
import com.youmai.hxsdk.utils.VideoUtils;
import com.youmai.hxsdk.utils.VirtualKeyUtils;

/**
 * Created by colin on 2016/9/12.
 * 用户秀选择素材
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class UserShowFragment extends BaseFragment {

    public static final String TAG = UserShowFragment.class.getSimpleName();

    private ImageView imgShow;
    private VideoView videoView;
    private FrameLayout fl_show_parent;
    private TextView tvRight;
    private String mFid;

    private UserInfo.DBean mUserInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hx_setting_show_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initView(view);

        showDialog();
    }

    private void initView(View view) {

        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvTitle.setTextSize(18);
        tvTitle.setText(R.string.hx_cur_show_title);

        TextView tvBack = (TextView) view.findViewById(R.id.tv_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        TextView tvSetup = (TextView) view.findViewById(R.id.tv_setup);
        tvSetup.setText(R.string.hx_setting_show);
        //设置背景
        tvSetup.setBackgroundResource(R.drawable.hx_tip_dialog_green_selector);
        tvSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置通话秀事件
                if (mUserInfo != null && !mUserInfo.getShowType().equals("4")) {
                    if (CommonUtils.isNetworkAvailable(getActivity())) {
                        repTip(mUserInfo);
                    } else {
                        ToastUtil.showToast(getActivity(), getString(R.string.hx_toast_50));
                    }
                } else {
                    if (CommonUtils.isNetworkAvailable(getActivity())) {
                        Intent intent = new Intent(mAct, SettingShowActivity.class);
                        startActivity(intent);
                    } else {
                        ToastUtil.showToast(getActivity(), getString(R.string.hx_toast_50));
                    }
                }
            }
        });

        tvRight = (TextView) view.findViewById(R.id.tv_title_right);
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("取消通话秀");
        tvRight.setTextColor(getResources().getColor(R.color.hxs_color_gray20));
        tvRight.setClickable(false);
        initCancelClick();

        ImageView ivShowHeader = (ImageView) view.findViewById(R.id.iv_show_header);
        imgShow = (ImageView) view.findViewById(R.id.img_show);
        videoView = (VideoView) view.findViewById(R.id.vv_video_show);
        fl_show_parent = (FrameLayout) view.findViewById(R.id.fl_show_parent);

        try {
            String phone = HuxinSdkManager.instance().getPhoneNum();
            HxUsers user = HxUsersHelper.getHxUser(getActivity(), phone);
            if (user != null && user.getIconUrl() != null) {
                Glide.with(getActivity())
                        .load(user.getIconUrl())
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .placeholder(R.drawable.hx_show_preview_header_bg).circleCrop())
                        .into(ivShowHeader);
            }

            mFid = SPDataUtil.getFirstShowFid(mAct);
            if (!StringUtils.isEmpty(mFid)) {
                String url = AppConfig.getImageUrl(getActivity(), mFid);
                Glide.with(getActivity())
                        .load(url)
                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .placeholder(R.drawable.hx_show_default_full).centerCrop())
                        .into(imgShow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /***********************适配show***********************/
        boolean hasVirtualKey = VirtualKeyUtils.hasSoftKeys((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE));
        RelativeLayout rl_show_parent = (RelativeLayout) view.findViewById(R.id.rl_show_parent);
        if (hasVirtualKey) {
            rl_show_parent.setLayoutParams(new LinearLayout.LayoutParams(-2, (int) getResources().getDimension(R.dimen.hx_setting_show_height_virtual_key)));
        } else {
            rl_show_parent.setLayoutParams(new LinearLayout.LayoutParams(-2, (int) getResources().getDimension(R.dimen.hx_setting_show_height)));
        }
        /*****************************************************/

    }

    /**
     * 取消 - 是否可点击
     */
    private void initCancelClick() {
        if (tvRight.isClickable()) {
            tvRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HxShowCancelDialog dialog = new HxShowCancelDialog(getContext());
                    dialog.show();
                    dialog.setReplaceClickListener((new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //确认取消show
                            IGetListener listener = new IGetListener() {
                                @Override
                                public void httpReqResult(String response) {
                                    if (!isAdded()) {
                                        return;
                                    }
                                    ShowCancel cancelBean = GsonUtil.parse(response, ShowCancel.class);
                                    if (cancelBean != null && cancelBean.isSuccess()) {
                                        tvRight.setTextColor(getResources().getColor(R.color.hxs_color_gray20));
                                        tvRight.setClickable(false);
                                        imgShow.setVisibility(View.VISIBLE);
                                        videoView.setVisibility(View.GONE);
                                        SPDataUtil.setFirstShowFid(mAct, "");
                                        imgShow.setImageResource(R.drawable.hx_show_default_full);

                                        ToastUtil.showPublicToast(getContext(), "成功取消", R.layout.hx_public_toast_tip, R.id.public_toast_message);

                                        cancelClick("", "", "");
                                    }
                                }
                            };
                            HuxinSdkManager.instance().cancelShow(listener);
                        }
                    }));
                }
            });
        }
    }

    /**
     * 第一次show新手引导
     */
    private void showDialog() {
        if (!SPDataUtil.getIsFirstShowGuide(getActivity())) {
            ShowTipOneDialog dialog = new ShowTipOneDialog(getActivity());
            ShowTipOneDialog.HxCallback callback = new ShowTipOneDialog.HxCallback() {
                @Override
                public void onJumpToSetShow() {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), SettingShowActivity.class);
                    startActivity(intent);
                }
            };
            dialog.setHxSetShowListener(callback);
            dialog.show();
            SPDataUtil.setIsFirstShowGuide(getActivity(), true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkShowType();
    }


    private void checkShowType() {
        final String phone = HuxinSdkManager.instance().getPhoneNum();
        IPostListener listener = new IPostListener() {
            @Override
            public void httpReqResult(String response) {
                UserInfo userShow = GsonUtil.parse(response, UserInfo.class);
                if (userShow != null && userShow.isSuccess()) {
                    mUserInfo = userShow.getD();
                    String showType = mUserInfo.getShowType();

                    if (showType != null && !showType.equals("4")) {
                        reqShowData(phone, "0");
                    }
                }
            }
        };
        HuxinSdkManager.instance().userInfo(phone, listener);
    }


    /**
     * 向服务器请求用户&商家秀
     *
     * @param phone
     */
    private void reqShowData(final String phone, String version) {
        IGetListener listener = new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                if (!isAdded()) {
                    return;
                }
                UserShow userShow = GsonUtil.parse(response, UserShow.class);
                if (userShow != null && userShow.isSucess()) {
                    UserShow.DBean.ShowBean showBean = userShow.getD().getShow();
                    if (showBean.getType().equals("1")) {
                        tvRight.setVisibility(View.VISIBLE);
                        tvRight.setClickable(true);
                        tvRight.setTextColor(getResources().getColor(R.color.hx_toolbar_font));
                        initCancelClick();
                        final String fid = showBean.getFid();
                        final String fileType = showBean.getFile_type();
                        try {
                            if (fileType.equals("0")) {// 图片
                                imgShow.setVisibility(View.VISIBLE);
                                videoView.setVisibility(View.GONE);

                                if (!fid.equals(mFid)) {
                                    SPDataUtil.setFirstShowFid(mAct, fid);
                                    String url = AppConfig.getImageUrl(getActivity(), fid);
                                    Glide.with(getActivity())
                                            .load(url)
                                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                                    .placeholder(R.drawable.hx_show_default_full).centerCrop())
                                            .into(imgShow);
                                }

                            } else if (fileType.equals("1")) { // 视频
                                imgShow.setVisibility(View.GONE);
                                videoView.setVisibility(View.VISIBLE);

                                VideoUtils.instance(getActivity()).loadVideo(fid, videoView, true);
                            }

                            if (fileType.equals("0") || fileType.equals("1")) {
                                cancelClick(fid, phone, fileType);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    } else {
                        tvRight.setClickable(false);
                        tvRight.setTextColor(getResources().getColor(R.color.hxs_color_gray20));
                    }
                }
            }
        };
        HuxinSdkManager.instance().showData(version, listener);
    }


    /**
     * 点击设置通话秀时tip提示
     */
    private void repTip(UserInfo.DBean showBean) {
        if (showBean.getShowType().equals("2")) {
            if (!AppUtils.isGooglePlay(mAct)) {
                final HxTip2Dialog hxExitDialog = new HxTip2Dialog(getActivity());
                hxExitDialog.show();
                hxExitDialog.setSureBtnClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(mAct, SettingShowActivity.class);
                        startActivity(intent);
                    }
                }).setMessage(getResources().getString(R.string.hx_tip_represent_show_merchant));
                hxExitDialog.getSureBtn().setText(R.string.hx_call_text);
            } else {
                Intent intent = new Intent(mAct, SettingShowActivity.class);
                startActivity(intent);
            }
        } else if (showBean.getShowType().equals("3")) {
            if (!AppUtils.isGooglePlay(mAct)) {
                final HxTip2Dialog hxExitDialog = new HxTip2Dialog(getActivity());
                hxExitDialog.show();
                hxExitDialog.setSureBtnClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(mAct, SettingShowActivity.class);
                        startActivity(intent);
                    }
                }).setMessage(getResources().getString(R.string.hx_tip_represent_show_text));
                hxExitDialog.getSureBtn().setText(R.string.hx_call_text);
            } else {
                Intent intent = new Intent(mAct, SettingShowActivity.class);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(mAct, SettingShowActivity.class);
            startActivity(intent);
        }
    }

    private void cancelClick(final String fid, final String phone, final String fileType) {
        if (StringUtils.isEmpty(fid)) {
            fl_show_parent.setClickable(false);
        } else {
            fl_show_parent.setClickable(true);
        }

        if (fl_show_parent.isClickable()) {
            fl_show_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ShowPreviewActivity.class);
                    intent.putExtra(ShowPreviewActivity.FID, fid);
                    intent.putExtra(ShowPreviewActivity.PBL_PHONE, phone);
                    intent.putExtra(ShowPreviewActivity.FILE_TYPE, fileType);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(), fl_show_parent, "ishow").toBundle());
                    } else {
                        startActivity(intent);
                    }
                }
            });
        }
    }

}
