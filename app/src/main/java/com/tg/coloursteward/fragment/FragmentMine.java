package com.tg.coloursteward.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.tg.coloursteward.MyBrowserActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.UserInfoActivity;
import com.tg.coloursteward.adapter.FragmentMineAdapter;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.entity.FragmentMineEntity;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.FragmentMineCallBack;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.MessageHandler.ResponseListener;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.LinkParseUtil;
import com.tg.coloursteward.util.TokenUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.CircleImageView;
import com.tg.coloursteward.view.dialog.PwdDialog2;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.utils.GlideRoundTransform;
import com.youmai.hxsdk.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人中心
 *
 * @author Administrator
 */
public class FragmentMine extends Fragment implements ResponseListener, OnClickListener {
    private View mView;
    private Activity mActivity;
    private AlertDialog dialog;
    private MessageHandler msgHandler;
    private PwdDialog2.ADialogCallback aDialogCallback;
    private PwdDialog2 aDialog;
    private String state = "noPwd";
    private RecyclerView recyclerview;
    private List<FragmentMineEntity.ContentBean> list = new ArrayList<>();
    private List<FragmentMineEntity.ContentBean.DataBean> list_item = new ArrayList<>();
    private FragmentMineAdapter mineAdapter;
    private int openType;
    private String salary;
    private HomeService homeService;
    private RelativeLayout rl_mine_title;
    private TextView tv_mine_name;
    private TextView tv_mine_job;
    private CircleImageView iv_mine_head;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_mine_layout, container, false);
        msgHandler = new MessageHandler(mActivity);
        msgHandler.setResponseListener(this);
        initView();
        Tools.saveStringValue(mActivity, "updatetime_img", UserInfo.userinfoImg);
        getHeadImg();
        initData();
        getEmployeeInfo();
        return mView;
    }

    private void initData() {
        String json = Tools.getStringValue(mActivity, Contants.storage.FRAGMENTMINE);
        if (!TextUtils.isEmpty(json)) {//有网络数据缓存
            initDataAdapter(json);
        } else {
            String loaclCache = Contants.storage.fragmentminedata;
            initDataAdapter(loaclCache);
        }
        initGetData();
    }

    private void initGetData() {
        RequestConfig config = new RequestConfig(mActivity, HttpTools.GET_FRAGMENTMINE);
        config.handler = msgHandler.getHandler();
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, String> stringMap = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(getActivity(), map));
        HttpTools.httpGet_Map(Contants.URl.URL_NEW, "app/home/mypage", config, (HashMap) stringMap);
    }

    private void initDataAdapter(String json) {
        list.clear();
        list_item.clear();
        FragmentMineEntity fragmentMineEntity = new FragmentMineEntity();
        fragmentMineEntity = GsonUtils.gsonToBean(json, FragmentMineEntity.class);
        list.addAll(fragmentMineEntity.getContent());
        for (int i = 0; i < list.size(); i++) {
            list_item.addAll(list.get(i).getData());
        }
        if (null == mineAdapter) {
            mineAdapter = new FragmentMineAdapter(mActivity, list_item);
            recyclerview.setAdapter(mineAdapter);
        } else {
            mineAdapter.setData(list_item);
        }
        mineAdapter.setFragmentMineCallBack(new FragmentMineCallBack() {
            @Override
            public void getData(String result, int positon) {
                String url = list_item.get(positon).getUrl();
                String name = list_item.get(positon).getName();
                if (url.contains("findPwd")) {
                    openType = 1;
                    find_pay_password();
                } else if (name.contains("工资")) {
                    openType = 2;
                    salary = url;
                    long salary_time;
                    if (TextUtils.isEmpty(Tools.getStringValue(mActivity, Contants.storage.SALARY_TIME))) {
                        salary_time = 0;
                    } else {
                        salary_time = Long.parseLong(Tools.getStringValue(mActivity, Contants.storage.SALARY_TIME));
                    }
                    long now_time = System.currentTimeMillis() / 1000;
                    boolean isinput = Tools.getBooleanValue(mActivity, Contants.storage.SALARY_ISINPUT);
                    if (isinput == true && now_time - salary_time >= 300) {//超过300秒
                        find_pay_password();
                    } else if (isinput == true) {
                        LinkParseUtil.parse(mActivity, salary, "");
                    } else {
                        find_pay_password();
                    }
                } else if (name.contains("账号绑定")) {
                    getAuth("绑定彩之云", url, "bdczy");
                } else {
                    LinkParseUtil.parse(mActivity, url, "");
                }
            }
        });
    }

    /**
     * 初始化
     */
    private void initView() {
        tv_mine_name = mView.findViewById(R.id.tv_mine_name);
        tv_mine_job = mView.findViewById(R.id.tv_mine_job);
        iv_mine_head = mView.findViewById(R.id.iv_mine_head);

        rl_mine_title = mView.findViewById(R.id.rl_mine_title);
        rl_mine_title.setOnClickListener(this);
        recyclerview = mView.findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerview.setLayoutManager(linearLayoutManager);
        tv_mine_name.setText(UserInfo.realname);
        if (!TextUtils.isEmpty(UserInfo.familyName)) {
            if (UserInfo.jobName.contains(UserInfo.familyName)) {
                tv_mine_job.setText(UserInfo.jobName);
            } else {
                tv_mine_job.setText(UserInfo.jobName + "(" + UserInfo.familyName + ")");
            }
        } else {
            tv_mine_job.setText(UserInfo.jobName + UserInfo.familyName);
        }
    }

    /**
     * employee/login接口调用
     */
    public void getEmployeeInfo() {
        String pwd = Tools.getPassWord(mActivity);
        RequestConfig config = new RequestConfig(mActivity, HttpTools.SET_EMPLOYEE_INFO, null);
        RequestParams params = new RequestParams();
        params.put("username", UserInfo.employeeAccount);
        try {
            params.put("password", MD5.getMd5Value(pwd).toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String key = Tools.getStringValue(mActivity, Contants.EMPLOYEE_LOGIN.key);
        String secret = Tools.getStringValue(mActivity, Contants.EMPLOYEE_LOGIN.secret);
        params.put("key", key);
        params.put("secret", secret);
        HttpTools.httpPost(Contants.URl.URL_CPMOBILE, "/1.0/employee/login", config, params);
    }

    /**
     * 更新UI
     */
    public void freshUI() {
        tv_mine_name.setText(UserInfo.realname);
        getHeadImg();
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
        homeService.getAuth2(clientCode, new GetTwoRecordListener<String, String>() {

            @Override
            public void onFinish(String openID, String accessToken, String data3) {
                String str = "?";
                String URL;
                if (url.contains(str)) {//Url有问号
                    URL = url + "&username=" + openID + "&access_token=" + accessToken;
                } else {
                    URL = url + "?username=" + openID + "&access_token=" + accessToken;
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
    public void onResume() {
        super.onResume();
        getHeadImg();
    }

    public void getHeadImg() {
        String str = Contants.Html5.HEAD_ICON_URL + "avatar?uid=" + UserInfo.employeeAccount;
        Glide.with(this)
                .load(str)
                .apply(new RequestOptions()
                        .signature(new ObjectKey(Tools.getStringValue(mActivity, "updatetime_img")))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .transform(new GlideRoundTransform()))
                .into(iv_mine_head);

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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
//            initGetData();
        }
    }

    /**
     * 清空支付密码
     */
    private void clearPayPwd() {
        RequestConfig config = new RequestConfig(mActivity, HttpTools.POST_CLEAR_PAYPWD);
        config.handler = msgHandler.getHandler();
        RequestParams params = new RequestParams();
        params.put("oa", UserInfo.employeeAccount);
        HttpTools.httpPost(Contants.URl.URL_CPMOBILE, "/1.0/employee/clearPayPwd", config, params);
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
                if (openType == 1) {
                    clearPayPwd();
                } else if (openType == 2) {
                    Tools.saveStringValue(mActivity, Contants.storage.SALARY_TIME, String.valueOf(System.currentTimeMillis() / 1000));
                    Tools.setBooleanValue(mActivity, Contants.storage.SALARY_ISINPUT, true);
                    LinkParseUtil.parse(mActivity, salary, "");
                }
            } else {
                ToastFactory.showToast(mActivity, hintString);
            }
        } else if (msg.arg1 == HttpTools.POST_CLEAR_PAYPWD) {
            JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
            if (code == 0) {
                if (jsonObject != null) {
                    try {
                        String message = jsonObject.getString("message");//密码清空成功  支付密码已经清空过了！
                        ToastUtil.showMidToast(mActivity, message);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SetPwd();
                            }
                        }, 1000);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                ToastFactory.showToast(mActivity, hintString);
            }
        } else if (msg.arg1 == HttpTools.SET_EMPLOYEE_INFO) {
            if (code == 0) {
                JSONObject content = HttpTools.getContentJSONObject(jsonString);
                if (content != null) {
                    Tools.setBooleanValue(mActivity, Contants.storage.EMPLOYEE_LOGIN, true);
                }
            }
        } else if (msg.arg1 == HttpTools.GET_FRAGMENTMINE) {
            if (code == 0) {
                Tools.saveStringValue(mActivity, Contants.storage.FRAGMENTMINE, jsonString);
                initDataAdapter(jsonString);
            }
        }

    }

    private void SetPwd() {
        aDialogCallback = new PwdDialog2.ADialogCallback() {
            @Override
            public void callback() {
                ToastUtil.showToast(getActivity(), "设置成功");
            }
        };
        aDialog = new PwdDialog2(
                getActivity(),
                R.style.choice_dialog, state,
                aDialogCallback);
        aDialog.show();
        aDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                aDialog.dismiss();
            }
        });
    }

    @Override
    public void onFail(Message msg, String hintString) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_mine_title:
                startActivity(new Intent(mActivity, UserInfoActivity.class));
                break;
        }

    }
}
