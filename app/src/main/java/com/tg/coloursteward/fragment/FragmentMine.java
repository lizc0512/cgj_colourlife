package com.tg.coloursteward.fragment;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tg.coloursteward.DownloadManagerActivity;
import com.tg.coloursteward.InviteRegisterActivity;
import com.tg.coloursteward.MyBrowserActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.RedpacketsBonusMainActivity;
import com.tg.coloursteward.SettingActivity;
import com.tg.coloursteward.TemporaryPassWordActivity;
import com.tg.coloursteward.UserInfoActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.MessageHandler.ResponseListener;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.object.ViewConfig;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.MessageArrowView;
import com.tg.coloursteward.view.MessageArrowView.ItemClickListener;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.utils.GlideRoundTransform;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 个人中心
 *
 * @author Administrator
 */
public class FragmentMine extends Fragment implements ItemClickListener, ResponseListener {
    private View mView;
    private Activity mActivity;
    private HomeService homeService;
    private ImageView imgHead;
    private TextView tvRealName, tvJob;
    private RelativeLayout rlUserInfo;
    private DisplayImageOptions options;
    private AlertDialog dialog;
    private MessageHandler msgHandler;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private MessageArrowView mineInfoZone1, mineInfoZone2, mineInfoZone3, mineInfoZone4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        mView = inflater.inflate(R.layout.fragment_mine_layout, container, false);
        msgHandler = new MessageHandler(mActivity);
        msgHandler.setResponseListener(this);
        initView();
        initData();
        return mView;
    }

    /**
     * 初始化
     */
    private void initView() {
        imgHead = (ImageView) mView.findViewById(R.id.img_head);
        tvRealName = (TextView) mView.findViewById(R.id.tv_realname);
        tvJob = (TextView) mView.findViewById(R.id.tv_job);
        rlUserInfo = (RelativeLayout) mView.findViewById(R.id.rl_userinfo);
        rlUserInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, UserInfoActivity.class));
            }
        });
        tvRealName.setText(UserInfo.realname);
        tvJob.setText(UserInfo.jobName);
        mineInfoZone1 = (MessageArrowView) mView.findViewById(R.id.mine_info_zone1);
        //mineInfoZone2 = (MessageArrowView) mView.findViewById(R.id.mine_info_zone2);
        mineInfoZone3 = (MessageArrowView) mView.findViewById(R.id.mine_info_zone3);
        mineInfoZone4 = (MessageArrowView) mView.findViewById(R.id.mine_info_zone4);
        mineInfoZone1.setItemClickListener(this);
        //mineInfoZone2.setItemClickListener(this);
        mineInfoZone3.setItemClickListener(this);
        mineInfoZone4.setItemClickListener(this);

        ArrayList<ViewConfig> list1 = new ArrayList<ViewConfig>();
        ViewConfig viewConfig = new ViewConfig("我的饭票", "", true);
        viewConfig.leftDrawable = getResources().getDrawable(R.drawable.ticket);
        list1.add(viewConfig);

        viewConfig = new ViewConfig("找回支付密码", "", true);
        viewConfig.leftDrawable = getResources().getDrawable(R.drawable.findpwd);
        list1.add(viewConfig);

        mineInfoZone1.setData(list1);
        /*
		ArrayList<ViewConfig> list2 = new ArrayList<ViewConfig>();
		viewConfig = new ViewConfig("找回支付密码", "", true);
		viewConfig.leftDrawable = getResources().getDrawable(R.drawable.findpwd);
		list2.add(viewConfig);
		
		viewConfig = new ViewConfig("获取临时密码", "", true);
		viewConfig.leftDrawable = getResources().getDrawable(R.drawable.gainpwd);
		list2.add(viewConfig);
		mineInfoZone2.setData(list2);*/

        ArrayList<ViewConfig> list3 = new ArrayList<ViewConfig>();
        viewConfig = new ViewConfig("彩之云账号绑定", "", true);
        viewConfig.leftDrawable = getResources().getDrawable(R.drawable.bindczy);
        list3.add(viewConfig);

        viewConfig = new ViewConfig("邀请", "", true);
        viewConfig.leftDrawable = getResources().getDrawable(R.drawable.invite);
        list3.add(viewConfig);
        mineInfoZone3.setData(list3);

        ArrayList<ViewConfig> list4 = new ArrayList<ViewConfig>();
        viewConfig = new ViewConfig("我的下载", "", true);
        viewConfig.leftDrawable = getResources().getDrawable(R.drawable.down);
        list4.add(viewConfig);

        viewConfig = new ViewConfig("设置", "", true);
        viewConfig.leftDrawable = getResources().getDrawable(R.drawable.shezhi);
        list4.add(viewConfig);

        viewConfig = new ViewConfig("帮助", "", true);
        viewConfig.leftDrawable = getResources().getDrawable(R.drawable.help);
        list4.add(viewConfig);
        mineInfoZone4.setData(list4);
        initOptions();
    }

    private void initOptions() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder2)
                .showImageForEmptyUri(R.drawable.placeholder2)
                .showImageOnFail(R.drawable.placeholder2).cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(true)
                // .displayer(new RoundedBitmapDisplayer(20))
                .build();
    }

    /**
     * 更新UI
     */
    public void freshUI() {
        tvRealName.setText(UserInfo.realname);
        initData();
    }

    public void initData() {
        String str = Contants.URl.HEAD_ICON_URL + "avatar?uid=" + UserInfo.employeeAccount;
        //imageLoader.clearMemoryCache();
        //imageLoader.clearDiskCache();
        //imageLoader.displayImage(str, imgHead, options);

        Glide.with(this)
                .load(str)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .transform(new GlideRoundTransform()))
                .into(imgHead);
    }

    @Override
    public void onItemClick(MessageArrowView mv, View v, int position) {
        // TODO Auto-generated method stub
        if (mv == mineInfoZone1) {
            if (position == 0) {// 我的饭票
                startActivity(new Intent(mActivity, RedpacketsBonusMainActivity.class));
            } else if (position == 1) {// 找回支付密码
                find_pay_password();
            }
        } else if (mv == mineInfoZone2) {
            if (position == 0) {// 找回支付密码
                find_pay_password();
            } else {//获取临时密码
                startActivity(new Intent(mActivity, TemporaryPassWordActivity.class));
            }
        } else if (mv == mineInfoZone3) {
            if (position == 0) {// 彩之云账号绑定
                getAuth("绑定彩之云", Contants.URl.CZY_BINDCUSTOMER, "bdczy");
            } else {//邀请
                startActivity(new Intent(mActivity, InviteRegisterActivity.class));
            }
        } else if (mv == mineInfoZone4) {
            if (position == 0) {//我的下载
                startActivity(new Intent(mActivity, DownloadManagerActivity.class));
            } else if (position == 1) {// 设置
                startActivity(new Intent(mActivity, SettingActivity.class));
            } else {//帮助
                Intent help_intent = new Intent(getActivity(), MyBrowserActivity.class);
                help_intent.putExtra(MyBrowserActivity.KEY_URL, Contants.URl.HUXIN_H5_HELP);
                //help_intent.putExtra(MyBrowserActivity.KEY_URL,"http://192.168.2.175:40064/testlogin");
                startActivity(help_intent);
            }
        }
    }

    /**
     * 验证个人密码
     */
    private void find_pay_password() {
        dialog = null;
        if (dialog == null) {
            dialog = new AlertDialog.Builder(mActivity).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Window window = dialog.getWindow();
            window.setContentView(R.layout.custom_alert_dialog);
            final EditText etPaypassword = (EditText) window.findViewById(R.id.et_paypassword);
            window.findViewById(R.id.dialog_button_ok).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {//确定
                    String password = etPaypassword.getText().toString();
                    if (TextUtils.isEmpty(password)) {
                        ToastFactory.showToast(mActivity, "密码不能为空");
                        return;
                    }
                    try {
                        String passwordMD5 = MD5.getMd5Value(password).toLowerCase();
                        RequestConfig config = new RequestConfig(mActivity, HttpTools.GET_PASSWORD_INFO);
                        config.handler = msgHandler.getHandler();
                        RequestParams params = new RequestParams();
                        params.put("username", UserInfo.employeeAccount);
                        params.put("password", passwordMD5);
                        HttpTools.httpPost(Contants.URl.URL_ICETEST, "/account/login", config, params);
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            window.findViewById(R.id.dialog_button_cancel).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {//取消
                    dialog.dismiss();
                }
            });
            DisplayMetrics dm = Tools.getDisplayMetrics(mActivity);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = (int) (dm.widthPixels - 100 * dm.density);
            window.setAttributes(lp);
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    /**
     * 获取绑定彩之云权限
     *
     * @param name
     * @param url
     * @param clientCode
     */
    private void getAuth(final String name, final String url, String clientCode) {
        if (homeService == null) {
            homeService = new HomeService(getActivity());
        }
        homeService.getAuth(clientCode, new GetTwoRecordListener<String, String>() {

            @Override
            public void onFinish(String openID, String accessToken, String data3) {
                String str = "?";
                String URL;
                if (url.contains(str)) {//Url有问号
                    URL = url + "&openID=" + openID + "&accessToken=" + accessToken;
                } else {
                    URL = url + "?openID=" + openID + "&accessToken=" + accessToken;
                }
                Intent intent = new Intent(mActivity, MyBrowserActivity.class);
                intent.putExtra(MyBrowserActivity.KEY_URL, URL);
                startActivity(intent);
            }

            @Override
            public void onFailed(String Message) {
                ToastFactory.showToast(mActivity, Message);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    /**
     * 清空支付密码
     */
    private void clearPayPwd() {
        RequestConfig config = new RequestConfig(mActivity, HttpTools.POST_CLEAR_PAYPWD);
        config.handler = msgHandler.getHandler();
        RequestParams params = new RequestParams();
        params.put("oa", UserInfo.employeeAccount);
        HttpTools.httpPost(Contants.URl.URL_ICETEST, "/czywg/employee/clearPayPwd", config, params);
    }

    /**
     * 请求数据处理方法
     */
    @Override
    public void onRequestStart(Message msg, String hintString) {
        if (msg.arg1 == HttpTools.GET_PASSWORD_INFO) {
            ToastFactory.showToast(mActivity, "正在验证中...");
        }
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        int code = HttpTools.getCode(jsonString);
        if (msg.arg1 == HttpTools.GET_PASSWORD_INFO) {
            if (code == 0) {
                clearPayPwd();
            } else {
                ToastFactory.showToast(mActivity, hintString);
            }
        } else {
            JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
            if (code == 0) {
                if (jsonObject != null) {
                    try {
                        String message = jsonObject.getString("message");
                        ToastFactory.showToast(mActivity, message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                ToastFactory.showToast(mActivity, hintString);
            }
        }

    }

    @Override
    public void onFail(Message msg, String hintString) {

    }
}
