package com.tg.coloursteward.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.tg.coloursteward.R;
import com.tg.coloursteward.adapter.PublicAccountRvAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.baseModel.HttpResponse;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.AppsDetailInfo;
import com.tg.coloursteward.info.PublicAccountInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.model.BonusModel;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.ToastUtil;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.point.activity.ChangePawdTwoStepActivity;
import com.tg.point.entity.CheckPwdEntiy;
import com.tg.point.entity.PointTransactionTokenEntity;
import com.tg.point.model.PointModel;
import com.tg.setting.activity.SettingActivity;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.pwddialog.PasswordDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 对公账户
 */
public class PublicAccountActivity extends BaseActivity implements HttpResponse {
    public static final String ACTION_PUBLIC_ACCOUNT = "com.tg.coloursteward.ACTION_PUBLIC_ACCOUNT";
    private RelativeLayout rl_kong;
    private PublicAccountRvAdapter rvAdapter;
    private String accessToken;
    private ArrayList<PublicAccountInfo> list = new ArrayList<PublicAccountInfo>();
    private ArrayList<AppsDetailInfo> listAppsDetail = new ArrayList<AppsDetailInfo>();
    private AuthAppService authAppService;//2.0授权
    private int postion;
    private String isshow = "";
    private PointModel pointModel;
    private String state;//支付密码的状态
    private BonusModel bonusModel;
    private SmartRefreshLayout sr_public_account;
    private RecyclerView rv_public_account;
    private int mPage = 1;
    private int pageSize = 10;
    private int itemListNumber = 0;//单次list.size
    private BroadcastReceiver freshReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_PUBLIC_ACCOUNT)) {
                getdata();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pointModel = new PointModel(this);
        bonusModel = new BonusModel(this);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        accessToken = Tools.getStringValue(PublicAccountActivity.this, Contants.storage.APPAUTH);
        rl_kong = findViewById(R.id.rl_kong);
        sr_public_account = findViewById(R.id.sr_public_account);
        rv_public_account = findViewById(R.id.rv_public_account);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_public_account.setLayoutManager(layoutManager);
        rvAdapter = new PublicAccountRvAdapter(PublicAccountActivity.this, list);
        rv_public_account.setAdapter(rvAdapter);
        sr_public_account.setRefreshFooter(new ClassicsFooter(this));
        sr_public_account.setRefreshHeader(new MaterialHeader(this).setColorSchemeColors(getResources().getColor(R.color.color_27a2f0)));
        sr_public_account.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (itemListNumber < pageSize) {
                    sr_public_account.finishLoadMoreWithNoMoreData();
                } else {
                    mPage++;
                    initData(mPage, false);
                    sr_public_account.finishLoadMore();
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPage = 1;
                initData(mPage, true);
                sr_public_account.finishRefresh();
            }
        });
        rvAdapter.setTransferCallBack(position -> { //转账
            isshow = "transfer";
            postion = position;
            pointModel.getTransactionToken(3, PublicAccountActivity.this);
        });

        rvAdapter.setExchangeCallBack(position -> {//兑换
            isshow = "exchange";
            postion = position;
            pointModel.getTransactionToken(3, PublicAccountActivity.this);
        });
        getdata();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PUBLIC_ACCOUNT);
        registerReceiver(freshReceiver, filter);
    }

    /**
     * 获取数据
     */
    private void getdata() {
        list.clear();
        listAppsDetail.clear();
        getAuthAppInfo();
    }

    private void initData(int page, boolean loading) {
        bonusModel.postSearchDgzhListDetail(1, UserInfo.uid, accessToken, page, pageSize, loading, this);
    }

    @Override
    public void OnHttpResponse(int what, String result) {
        switch (what) {
            case 0:
                if (!TextUtils.isEmpty(result)) {
                    String content = HttpTools.getContentString(result);
                    if (!TextUtils.isEmpty(content)) {
                        ResponseData data = HttpTools.getResponseKey(content, "result");
                        if (data.length > 0) {
                            AppsDetailInfo info;
                            for (int i = 0; i < data.length; i++) {
                                info = new AppsDetailInfo();
                                info.name = data.getString(i, "general_name");
                                info.pano = data.getString(i, "pano");
                                listAppsDetail.add(info);
                            }
                        }
                    }
                    setData();
                }
                break;
            case 1:
                if (!TextUtils.isEmpty(result)) {
                    String contentString = HttpTools.getContentString(result);
                    if (contentString != null) {
                        ResponseData data = HttpTools.getResponseKey(contentString, "list");
                        if (data.length > 0) {
                            itemListNumber = data.length;
                            PublicAccountInfo info;
                            if (mPage == 1) {
                                list.clear();
                            }
                            for (int i = 0; i < data.length; i++) {
                                if (data.getInt(i, "adminLevel") == 0) {

                                } else {
                                    info = new PublicAccountInfo();
                                    info.title = data.getString(i, "name");
                                    info.typeName = data.getString(i, "typeName");
                                    info.ano = data.getString(i, "ano");
                                    info.bno = data.getString(i, "bno");
                                    info.pano = data.getString(i, "pano");
                                    info.money = data.getString(i, "money");
                                    info.pid = data.getString(i, "pid");
                                    info.adminLevel = data.getInt(i, "adminLevel");
                                    info.atid = data.getInt(i, "atid");
                                    list.add(info);
                                }
                            }
                        }
                        getAppsDetail();
                    }
                }
                break;
            case 3:
                try {
                    PointTransactionTokenEntity pointTransactionTokenEntity = GsonUtils.gsonToBean(result, PointTransactionTokenEntity.class);
                    PointTransactionTokenEntity.ContentBean contentBean = pointTransactionTokenEntity.getContent();
                    state = contentBean.getState();
                    switch (state) {//1 已实名已设置支付密码2 已实名未设置支付密码3 未实名未设置支付密码4 未实名已设置支付密码
                        case "2"://已实名未设置支付密码
                            Intent intent = new Intent(PublicAccountActivity.this, ChangePawdTwoStepActivity.class);
                            startActivity(intent);
                            break;
                        case "3"://未实名未设置支付密码
                        case "4"://未实名已设置支付密码
                            DialogFactory.getInstance().showDoorDialog(this, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (AppUtils.isApkInstalled(PublicAccountActivity.this, "cn.net.cyberway")) {
                                        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("colourlifeauth://web?linkURL=colourlife://proto?type=Information"));
                                        startActivity(it);
                                    } else {
                                        AppUtils.launchAppDetail(PublicAccountActivity.this, "cn.net.cyberway", "");
                                    }
                                }
                            }, null, 1, "您的账号尚未实名，请前往彩之云APP进行实名认证", "去认证", null);
                            break;
                        default://1已实名已设置支付密码
                            showPayDialog();
                            break;
                    }
                } catch (Exception e) {

                }
                break;
            case 7:
                if (!TextUtils.isEmpty(result)) {
                    CheckPwdEntiy entiy = new CheckPwdEntiy();
                    entiy = GsonUtils.gsonToBean(result, CheckPwdEntiy.class);
                    if (entiy.getContent().getRight_pwd().equals("1")) {
                        if (isshow.equals("transfer")) {//转账
                            PublicAccountInfo info = list.get(postion);
                            Intent intent = new Intent(PublicAccountActivity.this, PublicAccountSearchActivity.class);
                            intent.putExtra(Contants.PARAMETER.PUBLIC_ACCOUNT, info.money);
                            intent.putExtra(Contants.PARAMETER.PAY_ATID, info.atid);
                            intent.putExtra(Contants.PARAMETER.PAY_ANO, info.ano);
                            intent.putExtra(Contants.PARAMETER.PAY_TYPE_NAME, info.typeName);
                            intent.putExtra(Contants.PARAMETER.PAY_NAME, info.title);
                            startActivity(intent);
                        } else if (isshow.equals("exchange")) {//兑换
                            PublicAccountInfo info = list.get(postion);
                            Intent intent = new Intent(PublicAccountActivity.this, ExchangeMethodActivity.class);
                            intent.putExtra(Contants.PARAMETER.PUBLIC_ACCOUNT, info.money);
                            intent.putExtra(Contants.PARAMETER.PAY_ATID, info.atid);
                            intent.putExtra(Contants.PARAMETER.PAY_ANO, info.ano);
                            intent.putExtra(Contants.PARAMETER.PAY_TYPE_NAME, info.typeName);
                            intent.putExtra(Contants.PARAMETER.PAY_NAME, info.title);
                            startActivity(intent);
                        }
                    } else {
                        String remain = entiy.getContent().getRemain();
                        if (remain.equals("0")) {
                            ToastUtil.showShortToast(this, "您已输入5次错误密码，账户被锁定，请明日再进行操作");
                        } else {
                            ToastUtil.showShortToast(this, "支付密码不正确，您还可以输入" + remain + "次");
                        }
                    }
                }
                break;
        }
    }

    private void showPayDialog() {
        PasswordDialogListener dialogListener = new PasswordDialogListener(this, new PasswordDialogListener.pwdDialogListener() {
            @Override
            public void result(String pwd) {
                pointModel.postCheckPwd(7, pwd, 2, PublicAccountActivity.this);
            }

            @Override
            public void forgetPassWord() {
                Intent it = new Intent(PublicAccountActivity.this, SettingActivity.class);
                startActivity(it);
            }
        });
        dialogListener.show();
    }

    private void getAppsDetail() {
        listAppsDetail.clear();
        bonusModel.getAppdetail(0, accessToken, this);
    }

    /**
     * 数据整合
     */
    private void setData() {
        if (list.size() > 0 && listAppsDetail.size() > 0) {
            rl_kong.setVisibility(View.GONE);
            rv_public_account.setVisibility(View.VISIBLE);
            for (int i = 0; i < list.size(); i++) { //外循环是循环的次数
                for (int j = 0; j < listAppsDetail.size(); j++)  //内循环是 外循环一次比较的次数
                {
                    if (list.get(i).pano.equals(listAppsDetail.get(j).pano)) {
                        list.get(i).name = listAppsDetail.get(j).name;
                    }
                }
            }
            rvAdapter.notifyDataSetChanged();
        } else if (list.size() > 0 && listAppsDetail.size() == 0) {
            rl_kong.setVisibility(View.GONE);
            rv_public_account.setVisibility(View.GONE);
        } else if (list.size() == 0 && listAppsDetail.size() > 0) {
            rl_kong.setVisibility(View.VISIBLE);
            rv_public_account.setVisibility(View.GONE);
        } else {
            rl_kong.setVisibility(View.VISIBLE);
            rv_public_account.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 获取token(2.0)
     * sectet
     */
    private void getAuthAppInfo() {
        if (authAppService == null) {
            authAppService = new AuthAppService(PublicAccountActivity.this);
        }
        authAppService.getAppAuth(new GetTwoRecordListener<String, String>() {
            @Override
            public void onFinish(String jsonString, String data2, String data3) {
                int code = HttpTools.getCode(jsonString);
                if (code == 0) {
                    JSONObject content = HttpTools.getContentJSONObject(jsonString);
                    if (content.length() > 0) {
                        try {
                            accessToken = content.getString("accessToken");
                            String expireTime = content.getString("expireTime");
                            Tools.saveStringValue(PublicAccountActivity.this, Contants.storage.APPAUTH, accessToken);
                            Tools.saveStringValue(PublicAccountActivity.this, Contants.storage.APPAUTHTIME, expireTime);
                            initData(mPage, true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

            @Override
            public void onFailed(String Message) {

            }
        });
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_public_account, null);
    }

    @Override
    public String getHeadTitle() {
        return "对公账户";
    }
}
