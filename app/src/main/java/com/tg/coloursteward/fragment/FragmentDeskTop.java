package com.tg.coloursteward.fragment;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tg.coloursteward.AccountActivity;
import com.tg.coloursteward.DataShowActivity;
import com.tg.coloursteward.DeskTopActivity;
import com.tg.coloursteward.DoorActivity;
import com.tg.coloursteward.HomeContactSearchActivity;
import com.tg.coloursteward.InviteRegisterActivity;
import com.tg.coloursteward.MainActivity;
import com.tg.coloursteward.MyBrowserActivity;
import com.tg.coloursteward.PublicAccountActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.RedpacketsBonusMainActivity;
import com.tg.coloursteward.adapter.HomeDeskTopAdapter;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.GridViewInfo;
import com.tg.coloursteward.info.HomeDeskTopInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.OnLoadingListener;
import com.tg.coloursteward.inter.SingleClickListener;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.net.image.VolleyUtils;
import com.tg.coloursteward.serice.AppAuthService;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.util.DateUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.HomeRelativeLayout;
import com.tg.coloursteward.view.ManageMentLinearlayout;
import com.tg.coloursteward.view.PopWindowView;
import com.tg.coloursteward.view.PullRefreshListViewFind;
import com.tg.coloursteward.view.PullRefreshListViewFind.NetOnItemLongClickListener;
import com.tg.coloursteward.view.PullRefreshListViewFind.NetPullRefreshOnScroll;
import com.tg.coloursteward.view.RotateProgress;
import com.tg.coloursteward.view.RoundImageView;
import com.tg.coloursteward.view.ManageMentLinearlayout.NetworkRequestListener;
import com.tg.coloursteward.view.HomeRelativeLayout.NetRelativeRequestListener;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

/**
 * 首页（定制版）彩生活
 *
 * @author Administrator
 */
public class FragmentDeskTop extends Fragment implements OnItemClickListener {
    private static final String TAG = "FragmentDeskTop";
    private View mView;
    private static final int ISTREAD = 1;
    private MainActivity mActivity;
    private PullRefreshListViewFind pullListView;
    private HomeDeskTopAdapter adapter;
    private ManageMentLinearlayout magLinearLayoutArea;
    private ManageMentLinearlayout magLinearLayoutStock;
    private ManageMentLinearlayout magLinearLayoutTicket;
    private ManageMentLinearlayout magLinearLayoutCommunity;
    private ManageMentLinearlayout magLinearLayoutPerformance;
    private ManageMentLinearlayout magLinearLayoutAccount;
    private RelativeLayout rlArea;
    private RelativeLayout rlStock;
    private RelativeLayout rlTicket;
    private RelativeLayout rlCommunity;
    private RelativeLayout rlPerformance;
    private RelativeLayout rlAccount;
    private RotateProgress progressBarArea;
    private RotateProgress progressBarStock;
    private RotateProgress progressBarTicket;
    private RotateProgress progressBarCommunity;
    private RotateProgress progressBarPerformance;
    private RotateProgress progressBarAccount;
    private TextView tvArea, tvStock, tvTicket, tvCommunity, tvPerformance, tvAccount, tvLogo;
    private RelativeLayout rlSearch;
    private String area, stock, ticket, community, performance, account;
    private ArrayList<GridViewInfo> gridlistAdd = new ArrayList<GridViewInfo>();
    private ArrayList<HomeDeskTopInfo> listType = new ArrayList<HomeDeskTopInfo>();
    private Intent intent;
    private int size;
    private RoundImageView ivHead;
    private LinearLayout llHomeHead;
    private RelativeLayout rlSaoyisao;
    private RelativeLayout rlLogo;
    private HomeRelativeLayout rlAdd;
    private PopWindowView popWindowView;
    private HomeService homeService;
    private int deletePosition;
    private String homeListCache;
    private String AreaStr;
    private String StockStr;
    private String TicketStr;
    private String CommunityStr;
    private String PerformanceStr;
    private String AccountStr;
    private String key;
    private String secret;
    private int readPosition = -1;
    private String corpUuid;
    private AuthAppService authAppService;
    private String accessToken;
    private String accessToken_1;
    private SingleClickListener singleListener = new SingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            switch (v.getId()) {
                case R.id.ll_area://在管面积(数据看板)
                    //startActivity(new Intent(mActivity, MapDetailActivity.class));
                    intent = new Intent(mActivity, DataShowActivity.class);
                    intent.putExtra(DataShowActivity.BRANCH, UserInfo.orgId);
                    startActivity(intent);
                    break;
                case R.id.ll_stock://集团股票
                    String addr = "http://image.sinajs.cn/newchart/hk_stock/min/01778.gif?"
                            + DateUtils.phpToString(DateUtils.getCurrentDate() + "",
                            DateUtils.DATE_FORMAT_DB);
                    //String addr = "http://192.168.2.175:40017/login?uid=61&username=18179042682&gardenid=XBBD&sign=0b8719526e95c3d2385a3b00e29d13f5";
                    intent = new Intent(mActivity, MyBrowserActivity.class);
                    intent.putExtra(MyBrowserActivity.KEY_URL, addr);
                    startActivity(intent);
                    break;
                case R.id.ll_ticket://我的饭票
                    startActivity(new Intent(mActivity, RedpacketsBonusMainActivity.class));
                    break;
                case R.id.ll_community://在管小区(数据看板)
                    //startActivity(new Intent(mActivity, MapDetailActivity.class));
                    intent = new Intent(mActivity, DataShowActivity.class);
                    intent.putExtra(DataShowActivity.BRANCH, UserInfo.orgId);
                    startActivity(intent);
                    break;
                case R.id.ll_performance://绩效评分
                    startActivity(new Intent(mActivity, RedpacketsBonusMainActivity.class));
                    break;
                case R.id.ll_account://即时分配
                    startActivity(new Intent(mActivity, AccountActivity.class));
                    break;
                case R.id.rl_saoyisao://扫一扫
                    startActivity(new Intent(mActivity, InviteRegisterActivity.class));
                    break;
                case R.id.rl_add://添加
                    /**
                     * 弹出框
                     */
                    popWindowView = new PopWindowView(mActivity, gridlistAdd);
                    popWindowView.setOnDismissListener(new poponDismissListener());
                    popWindowView.showPopupWindow(mView.findViewById(R.id.rl_add));
                    lightoff();
                    break;
                case R.id.rl_search://搜索框
                    startActivity(new Intent(mActivity, HomeContactSearchActivity.class));
                    break;
                case R.id.rl_logo://
                /*FamilyInfo info = new FamilyInfo();
                info.id = UserInfo.orgId;
				info.type = "org";
				info.name = UserInfo.familyName;
				intent = new Intent(mActivity,HomeContactOrgActivity.class);
				intent.putExtra(HomeContactOrgActivity.FAMILY_INFO, info);
				startActivity(intent);*/
                    break;
            }
        }
    };

    /**
     * popwindowview弹出时，全屏变灰色
     */
    private void lightoff() {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.3f;
        mActivity.getWindow().setAttributes(lp);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        mView = inflater.inflate(R.layout.fragment_desktop_layout, container, false);
        initHomeNewsView(mView);
        initListener();
        requestData();
        return mView;
    }

    /**
     * 加载数据
     */
    private void requestData() {
        Date dt = new Date();
        Long time = dt.getTime();
        String expireTime = Tools.getStringValue(mActivity, Contants.storage.APPAUTHTIME);
        String expireTime_1 = Tools.getStringValue(mActivity, Contants.storage.APPAUTHTIME_1);
        accessToken = Tools.getStringValue(mActivity, Contants.storage.APPAUTH);
        accessToken_1 = Tools.getStringValue(mActivity, Contants.storage.APPAUTH_1);
        Log.e(TAG, "requestData: accessToken" + accessToken);
        Log.e(TAG, "requestData: accessToken_1" + accessToken_1);
        if (StringUtils.isNotEmpty(expireTime)) {
            if (Long.parseLong(expireTime) <= time) {//token过期
                getAuthAppInfo();
            } else {
                freshData();//刷新在管面积与小区
                freshAccount();//刷新即时分配
            }
        } else {
            getAuthAppInfo();
        }
        //rlAdd.loaddingData();
        /*magLinearLayoutArea.postDelayed(new Runnable() {//在管面积

			@Override
			public void run() {
				magLinearLayoutArea.loaddingData();
			}
		}, 500);*/
        magLinearLayoutStock.postDelayed(new Runnable() {//集团股票

            @Override
            public void run() {
                magLinearLayoutStock.loaddingData();
            }
        }, 1000);
        magLinearLayoutTicket.postDelayed(new Runnable() {//我的饭票

            @Override
            public void run() {
                if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(secret)) {
                    magLinearLayoutTicket.loaddingData();
                } else {
                    if (mActivity != null) {
                        mActivity.getEmployeeInfo();
                    }
                }
            }
        }, 1500);
		/*magLinearLayoutCommunity.postDelayed(new Runnable() {//在管小区
			
			@Override
			public void run() {
				magLinearLayoutCommunity.loaddingData();
			}
		}, 2000);*/
        magLinearLayoutPerformance.postDelayed(new Runnable() {//绩效评分

            @Override
            public void run() {
                magLinearLayoutPerformance.loaddingData();
            }
        }, 2500);
        magLinearLayoutAccount.postDelayed(new Runnable() {//即时分账

            @Override
            public void run() {
                magLinearLayoutAccount.loaddingData();
            }
        }, 3000);
    }

    /**
     * 刷新饭票
     */
    public void freshUI() {
        key = Tools.getStringValue(mActivity, Contants.EMPLOYEE_LOGIN.key);
        secret = Tools.getStringValue(mActivity, Contants.EMPLOYEE_LOGIN.secret);
        magLinearLayoutTicket.loaddingData();
    }

    /**
     * 刷新即时分成
     */
    public void freshAccount() {
        magLinearLayoutAccount.loaddingData();
    }

    /**
     * 刷新在管面积与小区
     */
    public void freshData() {
        magLinearLayoutArea.postDelayed(new Runnable() {//在管面积

            @Override
            public void run() {
                magLinearLayoutArea.loaddingData();
            }
        }, 500);
        magLinearLayoutCommunity.postDelayed(new Runnable() {//在管小区

            @Override
            public void run() {
                magLinearLayoutCommunity.loaddingData();
            }
        }, 2000);
    }

    /**
     * 自定义消息盒子
     */
    private void setData() {
        HomeDeskTopInfo info;
        info = new HomeDeskTopInfo();
        info.id = 0;
        info.auth_type = 0;
        info.icon = "";
        info.owner_name = "暂无";
        info.owner_avatar = " ";
        info.modify_time = " ";
        info.title = "暂无新消息";
        info.source_id = " ";
        info.comefrom = "审批";
        info.url = "";
        info.client_code = "sp";
        info.notread = 0;
        listType.add(info);

        info = new HomeDeskTopInfo();
        info.id = 0;
        info.auth_type = 0;
        info.icon = "";
        info.owner_name = "暂无";
        info.owner_avatar = " ";
        info.modify_time = " ";
        info.title = "暂无新消息";
        info.source_id = " ";
        info.comefrom = "通知公告";
        info.url = "";
        info.client_code = "ggtz";
        info.notread = 0;
        listType.add(info);


        info = new HomeDeskTopInfo();
        info.id = 0;
        info.auth_type = 0;
        info.icon = "";
        info.owner_name = "暂无";
        info.owner_avatar = " ";
        info.modify_time = " ";
        info.title = "暂无新消息";
        info.source_id = " ";
        info.comefrom = "邮件";
        info.url = "";
        info.client_code = "yj";
        info.notread = 0;
        listType.add(info);

        info = new HomeDeskTopInfo();
        info.id = 0;
        info.auth_type = 0;
        info.icon = "";
        info.owner_name = "暂无";
        info.owner_avatar = " ";
        info.modify_time = " ";
        info.title = "暂无新消息";
        info.source_id = " ";
        info.comefrom = "蜜蜂协同";
        info.url = "";
        info.client_code = "case";
        info.notread = 0;
        listType.add(info);

    }

    private void initHomeNewsView(View view) {
        corpUuid = Tools.getStringValue(mActivity, Contants.storage.CORPID);
        key = Tools.getStringValue(mActivity, Contants.EMPLOYEE_LOGIN.key);
        secret = Tools.getStringValue(mActivity, Contants.EMPLOYEE_LOGIN.secret);
        llHomeHead = (LinearLayout) mView.findViewById(R.id.ll_home_head);
        rlSaoyisao = (RelativeLayout) mView.findViewById(R.id.rl_saoyisao);
        rlLogo = (RelativeLayout) mView.findViewById(R.id.rl_logo);
        rlAdd = (HomeRelativeLayout) mView.findViewById(R.id.rl_add);
        /**
         * 搜索框
         */
        rlSearch = (RelativeLayout) mView.findViewById(R.id.rl_search);
        tvLogo = (TextView) mView.findViewById(R.id.tv_logo);
        rlSaoyisao.setOnClickListener(singleListener);
        rlLogo.setOnClickListener(singleListener);
        rlAdd.setOnClickListener(singleListener);
        tvLogo.setText(UserInfo.familyName);
        //搜索框
        rlSearch.setOnClickListener(singleListener);

        pullListView = (PullRefreshListViewFind) mView.findViewById(R.id.pull_listview);
        //读取本地缓存列表
        getCacheList();
        pullListView.setOnItemClickListener(this);
        pullListView.setDividerHeight(0);

        pullListView.setFootTextView(15, getResources().getColor(R.color.text_color1)
                , 0, (int) getResources().getDimension(R.dimen.margin_10), 0, (int) getResources().getDimension(R.dimen.margin_10)
                , getResources().getColor(R.color.base_color));
        pullListView.setOnLoadingListener(new OnLoadingListener<PullRefreshListViewFind>() {
            @Override
            public void refreshData(PullRefreshListViewFind t, boolean isLoadMore,
                                    Message msg, String response) {
                // TODO Auto-generated method stub
                String jsonString = HttpTools.getContentString(response);
                if (StringUtils.isNotEmpty(jsonString)) {
                    Tools.saveHomeList(mActivity, response);
                    ResponseData data = HttpTools.getResponseData(jsonString);
                    HomeDeskTopInfo item;
                    for (int i = 0; i < data.length; i++) {
                        item = new HomeDeskTopInfo();
                        item.id = data.getInt(i, "id");
                        item.auth_type = data.getInt(i, "auth_type");
                        item.icon = data.getString(i, "ICON");
                        item.owner_name = data.getString(i, "owner_name");
                        item.owner_avatar = data.getString(i, "owner_avatar");
                        item.modify_time = data.getString(i, "homePushTime");
                        item.title = data.getString(i, "title");
                        item.source_id = data.getString(i, "source_id");
                        item.comefrom = data.getString(i, "comefrom");
                        item.url = data.getString(i, "url");
                        item.client_code = data.getString(i, "client_code");
                        item.notread = data.getInt(i, "notread");
                        listType.add(item);
                    }
                    setData();
                    for (int i = 0; i < listType.size(); i++) { //外循环是循环的次数
                        for (int j = listType.size() - 1; j > i; j--)  //内循环是 外循环一次比较的次数
                        {

                            if (listType.get(i).client_code.equals(listType.get(j).client_code)) {
                                listType.remove(j);
                            }
                        }
                    }
                } else {
                    setData();
                    for (int i = 0; i < listType.size(); i++) { //外循环是循环的次数
                        for (int j = listType.size() - 1; j > i; j--)  //内循环是 外循环一次比较的次数
                        {
                            if (listType.get(i).client_code.equals(listType.get(j).client_code)) {
                                listType.remove(j);
                            }
                        }
                    }
                }
                getNotReadNum();
            }

            @Override
            public void onLoadingMore(PullRefreshListViewFind t, Handler hand, int pageIndex) {
                // TODO Auto-generated method stub
                RequestConfig config = new RequestConfig(mActivity, PullRefreshListViewFind.HTTP_MORE_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("username", UserInfo.employeeAccount);
                params.put("corp_id", Tools.getStringValue(mActivity, Contants.storage.CORPID));
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/push2/homepush/gethomePushBybox", config, params);
            }

            @Override
            public void onLoading(PullRefreshListViewFind t, Handler hand) {
                // TODO Auto-generated method stub
                RequestConfig config = new RequestConfig(mActivity, PullRefreshListViewFind.HTTP_FRESH_CODE);
                config.handler = hand;
                RequestParams params = new RequestParams();
                params.put("username", UserInfo.employeeAccount);
                params.put("corp_id", Tools.getStringValue(mActivity, Contants.storage.CORPID));
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/push2/homepush/gethomePushBybox", config, params);
            }
        });
        /**
         * list添加头部
         */
        addHead();
        pullListView.setAdapter(adapter);
        pullListView.performLoading();
    }

    /**
     * 获取首页缓存列表
     */
    private void getCacheList() {
        homeListCache = Tools.getHomeList(mActivity);
        if (StringUtils.isNotEmpty(homeListCache)) {
            String jsonString = HttpTools.getContentString(homeListCache);
            if (StringUtils.isNotEmpty(jsonString)) {
                ResponseData data = HttpTools.getResponseData(jsonString);
                HomeDeskTopInfo item;
                for (int i = 0; i < data.length; i++) {
                    item = new HomeDeskTopInfo();
                    item.id = data.getInt(i, "id");
                    item.auth_type = data.getInt(i, "auth_type");
                    item.icon = data.getString(i, "app_icon_android");
                    item.owner_name = data.getString(i, "owner_name");
                    item.owner_avatar = data.getString(i, "owner_avatar");
                    item.modify_time = data.getString(i, "modify_time");
                    item.title = data.getString(i, "title");
                    item.source_id = data.getString(i, "source_id");
                    item.comefrom = data.getString(i, "comefrom");
                    item.url = data.getString(i, "url");
                    item.client_code = data.getString(i, "client_code");
                    item.notread = data.getInt(i, "notread");
                    listType.add(item);
                }
                setData();
                for (int i = 0; i < listType.size(); i++) { //外循环是循环的次数
                    for (int j = listType.size() - 1; j > i; j--)  //内循环是 外循环一次比较的次数
                    {
                        if (listType.get(i).client_code.equals(listType.get(j).client_code)) {
                            listType.remove(j);
                        }
                    }
                }
            } else {
                setData();
                for (int i = 0; i < listType.size(); i++) { //外循环是循环的次数
                    for (int j = listType.size() - 1; j > i; j--)  //内循环是 外循环一次比较的次数
                    {
                        if (listType.get(i).client_code.equals(listType.get(j).client_code)) {
                            listType.remove(j);
                        }
                    }
                }
            }
            pullListView.setAdapter(adapter);
            getNotReadNum();
        }
    }

    private void addHead() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View headView = inflater.inflate(R.layout.fragment_home_item_1, null);
        pullListView.addHeaderView(headView);
        /**
         * 头部
         */
        magLinearLayoutArea = (ManageMentLinearlayout) headView.findViewById(R.id.ll_area);//在管面积
        magLinearLayoutStock = (ManageMentLinearlayout) headView.findViewById(R.id.ll_stock);//集团股票
        magLinearLayoutTicket = (ManageMentLinearlayout) headView.findViewById(R.id.ll_ticket);//我的饭票
        magLinearLayoutCommunity = (ManageMentLinearlayout) headView.findViewById(R.id.ll_community);//在管小区
        magLinearLayoutPerformance = (ManageMentLinearlayout) headView.findViewById(R.id.ll_performance);//绩效评分
        magLinearLayoutAccount = (ManageMentLinearlayout) headView.findViewById(R.id.ll_account);//即时分账
        /**
         * 加载隐藏布局控件
         */
        rlArea = (RelativeLayout) mView.findViewById(R.id.rl_area);//在管面积
        rlStock = (RelativeLayout) mView.findViewById(R.id.rl_stock);//集团股票
        rlTicket = (RelativeLayout) mView.findViewById(R.id.rl_ticket);//我的饭票
        rlCommunity = (RelativeLayout) mView.findViewById(R.id.rl_community);//在管小区
        rlPerformance = (RelativeLayout) mView.findViewById(R.id.rl_performance);//绩效评分
        rlAccount = (RelativeLayout) mView.findViewById(R.id.rl_account);//即时分账
        /**
         * 头部6个按钮加载旋转圈
         */
        progressBarArea = (RotateProgress) mView.findViewById(R.id.progressBar_area);//在管面积
        progressBarStock = (RotateProgress) mView.findViewById(R.id.progressBar_stock);//集团股票
        progressBarTicket = (RotateProgress) mView.findViewById(R.id.progressBar_ticket);//我的饭票
        progressBarCommunity = (RotateProgress) mView.findViewById(R.id.progressBar_community);//在管小区
        progressBarPerformance = (RotateProgress) mView.findViewById(R.id.progressBar_performance);//绩效评分
        progressBarAccount = (RotateProgress) mView.findViewById(R.id.progressBar_account);//即时分账
        /**
         * 头部6个textView控件(展示数据)
         */
        tvArea = (TextView) mView.findViewById(R.id.tv_area);//在管面积
        tvStock = (TextView) mView.findViewById(R.id.tv_stock);//集团股票
        tvTicket = (TextView) mView.findViewById(R.id.tv_ticket);//我的饭票
        tvCommunity = (TextView) mView.findViewById(R.id.tv_community);//在管小区
        tvPerformance = (TextView) mView.findViewById(R.id.tv_performance);//绩效评分
        tvAccount = (TextView) mView.findViewById(R.id.tv_account);//即时分账
        AreaStr = Tools.getStringValue(mActivity, Contants.storage.AREAHOME);
        StockStr = Tools.getStringValue(mActivity, Contants.storage.STOCKHOME);
        TicketStr = Tools.getStringValue(mActivity, Contants.storage.TICKETHOME);
        CommunityStr = Tools.getStringValue(mActivity, Contants.storage.COMMUNITYHOME);
        PerformanceStr = Tools.getStringValue(mActivity, Contants.storage.PERFORMANCEHOME);
        AccountStr = Tools.getStringValue(mActivity, Contants.storage.ACCOUNTHOME);
        //在管面积
        if (StringUtils.isNotEmpty(AreaStr)) {
            progressBarArea.setVisibility(View.GONE);
            rlArea.setVisibility(View.VISIBLE);
            DecimalFormat decimalFomat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String p = decimalFomat.format(Float.valueOf(AreaStr) / 10000);
            tvArea.setText(p);
        } else {
            progressBarArea.setVisibility(View.VISIBLE);
            rlArea.setVisibility(View.GONE);
        }
        //集团股票
        if (StringUtils.isNotEmpty(StockStr)) {
            progressBarStock.setVisibility(View.GONE);
            rlStock.setVisibility(View.VISIBLE);
            tvStock.setText(StockStr);
        } else {
            progressBarStock.setVisibility(View.VISIBLE);
            rlStock.setVisibility(View.GONE);
        }
        //我的饭票
        if (StringUtils.isNotEmpty(TicketStr)) {
            progressBarTicket.setVisibility(View.GONE);
            rlTicket.setVisibility(View.VISIBLE);
            DecimalFormat df = new DecimalFormat("0.00");
            tvTicket.setText(df.format(Double.parseDouble(TicketStr)));
        } else {
            progressBarTicket.setVisibility(View.VISIBLE);
            rlTicket.setVisibility(View.GONE);
        }
        //在管小区
        if (StringUtils.isNotEmpty(CommunityStr)) {
            progressBarCommunity.setVisibility(View.GONE);
            rlCommunity.setVisibility(View.VISIBLE);
            tvCommunity.setText(CommunityStr);
        } else {
            progressBarCommunity.setVisibility(View.VISIBLE);
            rlCommunity.setVisibility(View.GONE);
        }
        //绩效评分
        if (StringUtils.isNotEmpty(PerformanceStr)) {
            progressBarPerformance.setVisibility(View.GONE);
            rlPerformance.setVisibility(View.VISIBLE);
            tvPerformance.setText(PerformanceStr);
        } else {
            progressBarPerformance.setVisibility(View.VISIBLE);
            rlPerformance.setVisibility(View.GONE);
        }
        //即时分账
        if (StringUtils.isNotEmpty(AccountStr)) {
            progressBarAccount.setVisibility(View.GONE);
            rlAccount.setVisibility(View.VISIBLE);
            DecimalFormat df = new DecimalFormat("0.00");
            tvAccount.setText(df.format(Double.parseDouble(AccountStr)));
        } else {
            progressBarAccount.setVisibility(View.VISIBLE);
            rlAccount.setVisibility(View.GONE);
        }
        /**
         * 按钮点击事件
         */
        magLinearLayoutArea.setOnClickListener(singleListener);
        magLinearLayoutStock.setOnClickListener(singleListener);
        magLinearLayoutTicket.setOnClickListener(singleListener);
        magLinearLayoutCommunity.setOnClickListener(singleListener);
        magLinearLayoutPerformance.setOnClickListener(singleListener);
        magLinearLayoutAccount.setOnClickListener(singleListener);
    }

    /**
     * 初始化
     */
    private void initListener() {
        /**
         * 未读消息数量更新（此处暂时放这，未读消息处理后再做更改）
         */
        MainActivity mainactivity = (MainActivity) getActivity();
        mainactivity.UpdataUnreadNum(0);
        /**
         * 右上角添加按钮
         */
        rlAdd.setNetworkRequestListener(new NetRelativeRequestListener() {

            @Override
            public void onSuccess(HomeRelativeLayout magLearLayout, Message msg,
                                  String response) {
                String jsonString = HttpTools.getContentString(response);
                if (jsonString != null) {
                    ResponseData app_list = HttpTools.getResponseKey(jsonString, "app_list");
                    if (app_list.length > 0) {
                        Tools.saveCommonInfo(mActivity, response);
                        JSONArray jsonArray = app_list.getJSONArray(0, "list");
                        ResponseData data = HttpTools.getResponseKeyJSONArray(jsonArray);
                        gridlistAdd = new ArrayList<GridViewInfo>();
                        GridViewInfo item = null;
                        for (int i = 0; i < data.length; i++) {
                            try {
                                item = new GridViewInfo();
                                item.name = data.getString(i, "name");
                                item.oauthType = data.getString(i, "oauthType");
                                item.developerCode = data.getString(i, "app_code");
                                item.clientCode = data.getString(i, "app_code");
                                item.sso = data.getString(i, "url");
                                JSONObject icon = data.getJSONObject(i, "icon");
                                if (icon != null || icon.length() > 0) {
                                    item.icon = icon.getString("android");
                                }
                                gridlistAdd.add(item);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onRequest(MessageHandler msgHand) {
                String pwd = Tools.getPassWord(mActivity);
                RequestConfig config = new RequestConfig(mActivity, 0);
                config.handler = msgHand.getHandler();
                RequestParams params = new RequestParams();
                params.put("user_name", UserInfo.employeeAccount);
                params.put("password", pwd);
                params.put("resource", "app");
                params.put("cate_id", 0);
                HttpTools.httpPost(Contants.URl.URL_ICETEST, "/newoa/rights/list", config, params);
            }
        });
        /**
         * 在管面积（上线面积）
         */
        magLinearLayoutArea.setNetworkRequestListener(new NetworkRequestListener() {

            @Override
            public void onSuccess(ManageMentLinearlayout magLearLayout, Message msg, String response) {
                int code = HttpTools.getCode(response);
                progressBarArea.setVisibility(View.GONE);
                rlArea.setVisibility(View.VISIBLE);
                if (code == 0) {
                    JSONObject jsonObject = HttpTools.getContentJSONObject(response);
                    if (jsonObject.length() > 0) {
                        try {
                            String coveredArea1 = jsonObject.getString("coveredArea");
                            String contractArea = jsonObject.getString("contractArea");
                            String delivered = jsonObject.getString("delivered");
                            JSONObject js = jsonObject.getJSONObject("撤场数据");
                            String coveredArea2 = js.getString("coveredArea");
                            double aa = Double.parseDouble(coveredArea1);//在管面积
                            double bb = Double.parseDouble(contractArea);//合同面积
                            double cc = Double.parseDouble(delivered);//已交付合同面积
                            double dd = Double.parseDouble(coveredArea2);//撤场面积
                            BigDecimal a = new BigDecimal(aa);
                            BigDecimal b = new BigDecimal(bb);
                            BigDecimal c = new BigDecimal(cc);
                            BigDecimal d = new BigDecimal(dd);
                            BigDecimal ad = a.add(d);
                            BigDecimal bc = b.subtract(c);
                            BigDecimal total = ad.add(bc);
                            area = String.valueOf(total);
                            if (StringUtils.isNotEmpty(area)) {
                                Tools.saveStringValue(mActivity, Contants.storage.AREAHOME, area);
                                DecimalFormat decimalFomat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                                String p = decimalFomat.format(Float.valueOf(area) / 10000);
                                tvArea.setText(p);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Tools.saveStringValue(mActivity, Contants.storage.AREAHOME, "0");
                    tvArea.setText("0");
                }

            }

            @Override
            public void onFail(ManageMentLinearlayout magLearLayout, Message msg, String hintString) {
                progressBarArea.setVisibility(View.GONE);
                rlArea.setVisibility(View.VISIBLE);
                if (StringUtils.isNotEmpty(AreaStr)) {
                    DecimalFormat decimalFomat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                    String p = decimalFomat.format(Float.valueOf(AreaStr) / 10000);
                    tvArea.setText(p);
                } else {
                    tvArea.setText("0");
                }
            }

            @Override
            public void onRequest(MessageHandler msgHand) {
                RequestConfig config = new RequestConfig(mActivity, 0);
                config.handler = msgHand.getHandler();
                RequestParams params = new RequestParams();
                params.put("token", accessToken);
                if (corpUuid.equals("a8c58297436f433787725a94f780a3c9")) {//彩生活的租户id
                    params.put("isAll", 1);
                    params.put("orgUuid", 0);
                } else {
                    params.put("corpId", corpUuid);
                    params.put("orgUuid", "9959f117-df60-4d1b-a354-776c20ffb8c7");
                }
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/resourcems/community/statistics", config, params);
            }
        });
        /**
         * 集团股票
         */
        magLinearLayoutStock.setNetworkRequestListener(new NetworkRequestListener() {

            @Override
            public void onSuccess(ManageMentLinearlayout magLearLayout, Message msg, String response) {
                int code = HttpTools.getCode(response);
                progressBarStock.setVisibility(View.GONE);
                rlStock.setVisibility(View.VISIBLE);
                if (code == 0) {
                    JSONObject jsonObject = HttpTools.getContentJSONObject(response);
                    try {
                        stock = jsonObject.getString("currentPrice");
                        Tools.saveStringValue(mActivity, Contants.storage.STOCKHOME, stock);
                        if (stock != null || stock.length() > 0) {
                            tvStock.setText(stock);
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    Tools.saveStringValue(mActivity, Contants.storage.STOCKHOME, "0");
                    if (StringUtils.isNotEmpty(StockStr)) {
                        tvStock.setText(StockStr);
                    } else {
                        tvStock.setText("0");
                    }
                }
            }

            @Override
            public void onFail(ManageMentLinearlayout magLearLayout, Message msg, String hintString) {
                progressBarStock.setVisibility(View.GONE);
                rlStock.setVisibility(View.VISIBLE);
                if (StringUtils.isNotEmpty(StockStr)) {
                    tvStock.setText(StockStr);
                } else {
                    tvStock.setText("0");
                }
            }

            @Override
            public void onRequest(MessageHandler msgHand) {
                try {
                    RequestConfig config = new RequestConfig(mActivity, 0);
                    config.handler = msgHand.getHandler();
                    RequestParams params = new RequestParams();
                    String stock = URLEncoder.encode("彩生活", "UTF-8");
                    params.put("stock", stock);
                    HttpTools.httpGet(Contants.URl.URL_ICETEST, "/stock", config, params);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        /**
         * 我的饭票
         */
        magLinearLayoutTicket.setNetworkRequestListener(new NetworkRequestListener() {

            @Override
            public void onSuccess(ManageMentLinearlayout magLearLayout, Message msg, String response) {
                int code = HttpTools.getCode(response);
                String message = HttpTools.getMessageString(response);
                if (code == 0) {
                    progressBarTicket.setVisibility(View.GONE);
                    rlTicket.setVisibility(View.VISIBLE);
                    JSONObject jsonObject = HttpTools.getContentJSONObject(response);
                    if (jsonObject != null) {
                        try {
                            String ticket = jsonObject.getString("balance");
                            Tools.saveStringValue(mActivity, Contants.storage.TICKETHOME, ticket);
                            if (ticket != null) {
                                DecimalFormat df = new DecimalFormat("0.00");
                                tvTicket.setText(df.format(Double.parseDouble(ticket)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Tools.saveStringValue(mActivity, Contants.storage.TICKETHOME, "0");
                    if (StringUtils.isNotEmpty(TicketStr)) {
                        DecimalFormat df = new DecimalFormat("0.00");
                        tvTicket.setText(df.format(Double.parseDouble(TicketStr)));
                    } else {
                        tvTicket.setText("0");
                    }
                }
            }

            @Override
            public void onFail(ManageMentLinearlayout magLearLayout, Message msg, String hintString) {
                progressBarTicket.setVisibility(View.GONE);
                rlTicket.setVisibility(View.VISIBLE);
                if (StringUtils.isNotEmpty(TicketStr)) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    tvTicket.setText(df.format(Double.parseDouble(TicketStr)));
                } else {
                    tvTicket.setText("0");
                }
            }

            @Override
            public void onRequest(MessageHandler msgHand) {
                RequestConfig config = new RequestConfig(mActivity, 0);
                config.handler = msgHand.getHandler();
                RequestParams params = new RequestParams();
                params.put("key", key);
                params.put("secret", secret);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/hongbao/getHBUserList", config, params);
            }
        });
        /**
         * 在管小区（上线小区）
         */
        magLinearLayoutCommunity.setNetworkRequestListener(new NetworkRequestListener() {

            @Override
            public void onSuccess(ManageMentLinearlayout magLearLayout, Message msg, String response) {
                int code = HttpTools.getCode(response);
                progressBarCommunity.setVisibility(View.GONE);
                rlCommunity.setVisibility(View.VISIBLE);
                if (code == 0) {
                    JSONObject jsonObject = HttpTools.getContentJSONObject(response);
                    if (jsonObject.length() > 0) {
                        try {
                            community = jsonObject.getString("count");
                            if (StringUtils.isNotEmpty(community)) {
                                Tools.saveStringValue(mActivity, Contants.storage.COMMUNITYHOME, community);
                                tvCommunity.setText((community));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Tools.saveStringValue(mActivity, Contants.storage.COMMUNITYHOME, "0");
                    if (StringUtils.isNotEmpty(CommunityStr)) {
                        tvCommunity.setText(CommunityStr);
                    } else {
                        tvCommunity.setText("0");
                    }
                }

            }

            @Override
            public void onFail(ManageMentLinearlayout magLearLayout, Message msg, String hintString) {
                progressBarCommunity.setVisibility(View.GONE);
                rlCommunity.setVisibility(View.VISIBLE);
                if (StringUtils.isNotEmpty(CommunityStr)) {
                    tvCommunity.setText(CommunityStr);
                } else {
                    tvCommunity.setText("0");
                }
            }

            @Override
            public void onRequest(MessageHandler msgHand) {
                RequestConfig config = new RequestConfig(mActivity, 0);
                config.handler = msgHand.getHandler();
                RequestParams params = new RequestParams();
                params.put("token", accessToken);
                if (corpUuid.equals("a8c58297436f433787725a94f780a3c9")) {
                    params.put("isAll", 1);
                    params.put("orgUuid", 0);
                } else {
                    params.put("corpId", corpUuid);
                    params.put("orgUuid", "9959f117-df60-4d1b-a354-776c20ffb8c7");
                }
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/resourcems/community/statistics", config, params);
            }
        });
        /**
         * 绩效评分
         */
        magLinearLayoutPerformance.setNetworkRequestListener(new NetworkRequestListener() {

            @Override
            public void onSuccess(ManageMentLinearlayout magLearLayout, Message msg, String response) {
                int code = HttpTools.getCode(response);
                String message = HttpTools.getMessageString(response);
                progressBarPerformance.setVisibility(View.GONE);
                rlPerformance.setVisibility(View.VISIBLE);
                if (code == 0) {
                    JSONObject jsonObject = HttpTools.getContentJSONObject(response);
                    try {
                        performance = jsonObject.getString("percent");
                        Tools.saveStringValue(mActivity, Contants.storage.PERFORMANCEHOME, performance);
                        if (performance != null || performance.length() > 0) {
                            tvPerformance.setText(performance);
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    Tools.saveStringValue(mActivity, Contants.storage.PERFORMANCEHOME, "无");
                    if (StringUtils.isNotEmpty(PerformanceStr)) {
                        tvPerformance.setText(PerformanceStr);
                    } else {
                        tvPerformance.setText("无");
                    }
                }

            }

            @Override
            public void onFail(ManageMentLinearlayout magLearLayout, Message msg, String hintString) {
                progressBarPerformance.setVisibility(View.GONE);
                rlPerformance.setVisibility(View.VISIBLE);
                if (StringUtils.isNotEmpty(PerformanceStr)) {
                    tvPerformance.setText(PerformanceStr);
                } else {
                    tvPerformance.setText("无");
                }
            }

            @Override
            public void onRequest(MessageHandler msgHand) {
                Time time = new Time();
                time.setToNow();
                int year = time.year;
                int month = time.month + 1;
                RequestConfig config = new RequestConfig(mActivity, 0);
                config.handler = msgHand.getHandler();
                RequestParams params = new RequestParams();
                params.put("oauser", UserInfo.employeeAccount);
                params.put("year", year);
                params.put("month", month);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/oa/jxfen", config, params);
            }
        });
        /**
         * 即时分配
         */
        magLinearLayoutAccount.setNetworkRequestListener(new NetworkRequestListener() {

            @Override
            public void onSuccess(ManageMentLinearlayout magLearLayout, Message msg, String response) {
                int code = HttpTools.getCode(response);
                String message = HttpTools.getMessageString(response);
                progressBarAccount.setVisibility(View.GONE);
                rlAccount.setVisibility(View.VISIBLE);
                if (code == 0) {
                    JSONObject jsonObject = HttpTools.getContentJSONObject(response);
                    try {
                        account = jsonObject.getString("total_balance");
                        Tools.saveStringValue(mActivity, Contants.storage.ACCOUNTHOME, account);
                        if (account != null || account.length() > 0) {
                            DecimalFormat df = new DecimalFormat("0.00");
                            tvAccount.setText(df.format(Double.parseDouble(account)));
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    Tools.saveStringValue(mActivity, Contants.storage.ACCOUNTHOME, "0.00");
                    if (StringUtils.isNotEmpty(AccountStr)) {
                        DecimalFormat df = new DecimalFormat("0.00");
                        tvAccount.setText(df.format(Double.parseDouble(AccountStr)));
                    } else {
                        tvAccount.setText("0.00");
                    }
                }

            }

            @Override
            public void onFail(ManageMentLinearlayout magLearLayout, Message msg, String hintString) {
                progressBarAccount.setVisibility(View.GONE);
                rlAccount.setVisibility(View.VISIBLE);
                Tools.saveStringValue(mActivity, Contants.storage.ACCOUNTHOME, "0");
                if (StringUtils.isNotEmpty(AccountStr)) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    tvAccount.setText(df.format(Double.parseDouble(AccountStr)));
                } else {
                    tvAccount.setText("0.00");
                }
            }

            @Override
            public void onRequest(MessageHandler msgHand) {
                RequestConfig config = new RequestConfig(mActivity, 0);
                config.handler = msgHand.getHandler();
                RequestParams params = new RequestParams();
                params.put("access_token", accessToken);
                params.put("split_type", "2");
                params.put("split_target", UserInfo.employeeAccount);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/split/api/account", config, params);
            }
        });
    }

    public void initData() {
        VolleyUtils.getImage(getActivity(), UserInfo.headUrl, ivHead, size, size, R.drawable.moren_geren);
    }

    /**
     * 删除item后更新listview适配器
     */
    public void freshUIDelete() {
        listType.remove(deletePosition);
        adapter.notifyDataSetChanged();
    }

    /**
     * 即时分配数据更新
     */
    public void freshUIAccount(String a) {
        tvAccount.setText(a);
    }

    /**
     * 首页消息盒子列表设置为已读，更新适配器
     */
    public void freshUIListType() {
        if (readPosition != -1) {
            listType.get(readPosition).notread = 0;
            adapter.notifyDataSetChanged();
            getNotReadNum();
        } else {
            pullListView.performLoading();
        }
    }

    private void getNotReadNum() {
        int notNum = 0;
        for (int i = 0; i < listType.size(); i++) { //外循环是循环的次数
            notNum += listType.get(i).notread;
        }
        /**
         * 未读消息数量更新
         */
        MainActivity mainactivity = (MainActivity) getActivity();
        mainactivity.UpdataUnreadNum(notNum);
    }

    /**
     * 接受到首页推送消息，更新适配器
     */
    public void freshPushUIListType(ArrayList<HomeDeskTopInfo> list) {
        listType.clear();
        listType.addAll(list);
        setData();
        for (int i = 0; i < listType.size(); i++) { //外循环是循环的次数
            for (int j = listType.size() - 1; j > i; j--)  //内循环是 外循环一次比较的次数
            {

                if (listType.get(i).client_code.equals(listType.get(j).client_code)) {
                    listType.remove(j);
                }
            }
        }
        adapter.notifyDataSetChanged();
        getNotReadNum();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            requestData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(MainActivity.ACTION_ACCOUNT_INFO);
        mActivity.sendBroadcast(intent);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        size = getResources().getDimensionPixelSize(R.dimen.margin_88);
        mActivity = (MainActivity) activity;
        adapter = new HomeDeskTopAdapter(mActivity, listType);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if ((int) parent.getAdapter().getItemId(position) != -1) {
            HomeDeskTopInfo info = listType.get((int) parent.getAdapter().getItemId(position));
            Intent intent = new Intent(mActivity, DeskTopActivity.class);
            intent.putExtra(DeskTopActivity.DESKTOP_WEIAPPCODE, info);
            intent.putExtra(DeskTopActivity.DESKTOP_POSTION, (int) parent.getAdapter().getItemId(position));
            startActivity(intent);
        } else {
            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ISTREAD) {
            pullListView.performLoading();
        }
    }

    /**
     * 设置为已读
     *
     * @param Position
     */
    private void readMsg(int Position) {
        readPosition = Position;
        Intent intent = new Intent(MainActivity.ACTION_READ_MESSAGEINFO);
        intent.putExtra("messageId", listType.get(Position).client_code);
        mActivity.sendBroadcast(intent);

    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */
    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
            lp.alpha = 1.0f;
            mActivity.getWindow().setAttributes(lp);
        }

    }

    @Subscribe
    public void onEvent(Object event) {
        final Message message = (Message) event;
        if (message.what == Contants.LOGO.CLEAR_HOMELIST) {
            if (listType != null) {
                listType.clear();
                pullListView.performLoading();
                adapter.notifyDataSetChanged();
            }
        } else if (message.what == Contants.LOGO.UPDATE_HOMELIST) {
            int position = message.arg1;
            if (listType.get(position).notread > 0) {
                readMsg(position);
            }
        }
    }

    /**
     * 获取token（2.0）
     * sectet
     */
    private void getAuthAppInfo() {
        if (authAppService == null) {
            authAppService = new AuthAppService(mActivity);
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
                            Tools.saveStringValue(mActivity, Contants.storage.APPAUTH, accessToken);
                            Tools.saveStringValue(mActivity, Contants.storage.APPAUTHTIME, expireTime);
                            freshData();//刷新在管面积与小区
                            freshAccount();//刷新即时分配
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
}
