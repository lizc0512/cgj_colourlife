package com.tg.coloursteward.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tg.coloursteward.R;
import com.tg.coloursteward.activity.MyBrowserActivity;
import com.tg.coloursteward.adapter.FragmentMineAdapter;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.constant.SpConstants;
import com.tg.coloursteward.entity.FragmentMineEntity;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.FragmentMineCallBack;
import com.tg.coloursteward.model.MineModel;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.util.GlideUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.LinkParseUtil;
import com.tg.coloursteward.util.SharedPreferencesUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.CircleImageView;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.tg.user.activity.UserInfoActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人中心
 *
 * @author Administrator
 */
public class FragmentMine extends Fragment implements OnClickListener, HttpResponse {
    private View mView;
    private Activity mActivity;
    private AlertDialog dialog;
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
    private MineModel mineModel;
    private String updateTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_mine_layout, container, false);
        updateTime = SharedPreferencesUtils.getInstance().getStringData(SpConstants.UserModel.UPDATETIME_IMG, "");
        if (TextUtils.isEmpty(updateTime)) {
            SharedPreferencesUtils.getInstance().saveStringData(SpConstants.UserModel.UPDATETIME_IMG, UserInfo.employeeAccount);
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mineModel = new MineModel(mActivity);
        initView();
        getHeadImg();
        initData();
        return mView;
    }

    private void initData() {
        String json = SharedPreferencesUtils.getInstance().getStringData(Contants.storage.FRAGMENTMINE, "");
        if (!TextUtils.isEmpty(json)) {//有网络数据缓存
            initDataAdapter(json);
        } else {
            String loaclCache = Contants.storage.fragmentminedata;
            initDataAdapter(loaclCache);
        }
        initGetData();
    }

    private void initGetData() {
        String corp_id = SharedPreferencesUtils.getInstance().getStringData(SpConstants.storage.CORPID, "");
        mineModel.getMineData(0, corp_id, this);
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
                    LinkParseUtil.parse(mActivity, "colourlife://proto?type=setting", "");
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
        recyclerview.setNestedScrollingEnabled(false);
        if (!TextUtils.isEmpty(UserInfo.realname)) {
            tv_mine_name.setText(UserInfo.realname);
        } else {
            tv_mine_name.setText(UserInfo.mobile);
        }
        initRefresh();
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
        homeService.getAuth2(new GetTwoRecordListener<String, String>() {

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
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getHeadImg();
    }

    public void getHeadImg() {
        String str = Contants.Html5.HEAD_ICON_URL + "/avatar?uid=" + UserInfo.employeeAccount;
        updateTime = SharedPreferencesUtils.getInstance().getStringData(SpConstants.UserModel.UPDATETIME_IMG, "");
        GlideUtils.loadSignatureImageView(mActivity, str, updateTime, iv_mine_head);

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
                    String passwordMD5 = null;
                    try {
                        passwordMD5 = MD5.getMd5Value(password).toLowerCase();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                    mineModel.postAccountLogin(1, UserInfo.employeeAccount, passwordMD5, FragmentMine.this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_mine_title:
                startActivity(new Intent(mActivity, UserInfoActivity.class));
                break;
        }

    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    SharedPreferencesUtils.getInstance().saveStringData(Contants.storage.FRAGMENTMINE, result);
                    initDataAdapter(result);
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    int code = HttpTools.getCode(result);
                    if (code == 0) {
                        if (openType == 1) {
                        } else if (openType == 2) {
                            Tools.saveStringValue(mActivity, Contants.storage.SALARY_TIME, String.valueOf(System.currentTimeMillis() / 1000));
                            Tools.setBooleanValue(mActivity, Contants.storage.SALARY_ISINPUT, true);
                            LinkParseUtil.parse(mActivity, salary, "");
                        }
                    }
                }
                break;
        }
    }

    @Subscribe
    public void onEvent(Object event) {
        final Message message = (Message) event;
        switch (message.what) {
            case Contants.EVENT.changeCorp:
                initRefresh();
                break;

        }
    }

    private void initRefresh() {
        initGetData();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
