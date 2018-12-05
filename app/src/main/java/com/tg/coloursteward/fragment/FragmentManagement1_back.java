package com.tg.coloursteward.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tg.coloursteward.AccountActivity;
import com.tg.coloursteward.DataShowActivity;
import com.tg.coloursteward.HomeContactSearchActivity;
import com.tg.coloursteward.InviteRegisterActivity;
import com.tg.coloursteward.MyBrowserActivity;
import com.tg.coloursteward.PublicAccountActivity;
import com.tg.coloursteward.R;
import com.tg.coloursteward.RedpacketsBonusMainActivity;
import com.tg.coloursteward.adapter.ManagementAdapter1;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.entity.CaiHuiEntity;
import com.tg.coloursteward.entity.OaConfig;
import com.tg.coloursteward.info.GridViewInfo;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.inter.SingleClickListener;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MD5;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.util.AuthTimeUtils;
import com.tg.coloursteward.util.DateUtils;
import com.tg.coloursteward.util.GsonUtils;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.TokenUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.ManageMentLinearlayout;
import com.tg.coloursteward.view.ManageMentLinearlayout.NetworkRequestListener;
import com.tg.coloursteward.view.MyGridView1;
import com.tg.coloursteward.view.MyGridView1.NetGridViewRequestListener;
import com.tg.coloursteward.view.RotateProgress;
import com.tg.coloursteward.view.dialog.ToastFactory;
import com.youmai.hxsdk.adapter.PaddingItemDecoration;
import com.youmai.hxsdk.config.ColorsConfig;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.http.IPostListener;
import com.youmai.hxsdk.http.OkHttpConnector;
import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 微物管       工作（彩管家4.0）
 *
 * @author Administrator
 */
public class FragmentManagement1_back extends Fragment implements MessageHandler.ResponseListener {
    private static final String TAG = "FragmentManagement";
    private Activity mActivity;
    private View mView;
    private Intent intent;
    private MyGridView1 mGridView1, mGridView2;
    private ManagementAdapter1 adapter1, adapter2;
    private ManageMentLinearlayout magLinearLayoutMail;
    private ManageMentLinearlayout magLinearLayoutExamineNum;
    private ManageMentLinearlayout magLinearLayoutLeave;
    private ManageMentLinearlayout magLinearLayoutSign;
    private TextView tvMail, tvExamineNum;
    private String mail, examineNum;

    private String corpUuid;
    private AuthAppService authAppService;
    private String accessToken;
    private String accessToken_1;

    private ArrayList<GridViewInfo> gridlist1 = new ArrayList<>();
    private ArrayList<GridViewInfo> gridlist2 = new ArrayList<>();
    private String commonjsonStr, elsejsonStr;

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

    private String area, stock, ticket, community, performance, account;
    private MessageHandler msgHandler;
    private String AreaStr;
    private String StockStr;
    private String TicketStr;
    private String CommunityStr;
    private String PerformanceStr;
    private String AccountStr;
    private String key;
    private String secret;
    private int count = 0;
    private CaiHuiEntity caiHuiEntity;
    private LinearLayout ll_home_head;
    private int balanceNum = 0;
    private SingleClickListener titleListener = new SingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            switch (v.getId()) {
                case R.id.ll_area://在管面积(数据看板)
                    intent = new Intent(mActivity, DataShowActivity.class);
                    intent.putExtra(DataShowActivity.BRANCH, UserInfo.orgId);
                    startActivity(intent);
                    break;
                case R.id.ll_stock://集团股票
                    String addr = "http://image.sinajs.cn/newchart/hk_stock/min/01778.gif?"
                            + DateUtils.phpToString(DateUtils.getCurrentDate() + "",
                            DateUtils.DATE_FORMAT_DB);
                    intent = new Intent(mActivity, MyBrowserActivity.class);
                    intent.putExtra(MyBrowserActivity.KEY_URL, addr);
                    startActivity(intent);
                    break;
                case R.id.ll_ticket://我的饭票
                    startActivity(new Intent(mActivity, RedpacketsBonusMainActivity.class));
                    break;
                case R.id.ll_community://在管小区(数据看板)
                    intent = new Intent(mActivity, DataShowActivity.class);
                    intent.putExtra(DataShowActivity.BRANCH, UserInfo.orgId);
                    startActivity(intent);
                    break;
                case R.id.ll_performance://彩惠战况
                    Intent intent = new Intent(mActivity, MyBrowserActivity.class);
                    intent.putExtra(MyBrowserActivity.KEY_URL, "https://caihui-bishow.colourlife.com/orderData/orderData.html");
                    startActivity(intent);
                    break;
                case R.id.ll_account://即时分配
                    startActivity(new Intent(mActivity, AccountActivity.class));
                    break;
                case R.id.rl_saoyisao://扫一扫
                    startActivity(new Intent(mActivity, InviteRegisterActivity.class));
                    break;
                case R.id.rl_search://搜索框
                    startActivity(new Intent(mActivity, HomeContactSearchActivity.class));
                    break;
            }
        }
    };

    private SingleClickListener singleListener = new SingleClickListener() {

        @Override
        public void onSingleClick(View v) {
            AuthTimeUtils mAuthTimeUtils;
            switch (v.getId()) {
                case R.id.ll_mail://未读邮件
                    mAuthTimeUtils = new AuthTimeUtils();
                    mAuthTimeUtils.IsAuthTime(mActivity, Contants.Html5.YJ, "xyj", "1", "xyj", "");
                    break;
                case R.id.ll_examineNum://待我审批
                    /**
                     * 判断是中住还是通用
                     */
                    String skin_code = Tools.getStringValue(mActivity, Contants.storage.SKINCODE);
                    Log.e(TAG, "onSingleClick: skin_code" + skin_code);
                    if (skin_code.equals("102")) {//中住
                        mAuthTimeUtils = new AuthTimeUtils();
                        mAuthTimeUtils.IsAuthTime(mActivity, Contants.Html5.SP1, "sp", "0", "sp", "");
                    } else {
                        mAuthTimeUtils = new AuthTimeUtils();
                        mAuthTimeUtils.IsAuthTime(mActivity, Contants.Html5.SP + "?number=" + balanceNum, "tlmyapps", "1", "tlmyapps", "");

                    }
                    break;
                case R.id.ll_leave://请假
                    intent = new Intent(mActivity, MyBrowserActivity.class);
                    intent.putExtra(MyBrowserActivity.KEY_URL, Contants.Html5.URL_H5_LEAVE + "username=" + UserInfo.employeeAccount);
                    startActivity(intent);
                    break;
                case R.id.ll_sign://签到
                    mAuthTimeUtils = new AuthTimeUtils();
                    mAuthTimeUtils.IsAuthTime(mActivity, Contants.Html5.QIANDAO, "qiandao", "1", "qiandao", "");
                    break;
            }
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_management_layout1, container, false);
        msgHandler = new MessageHandler(mActivity);
        msgHandler.setResponseListener(this);
        addHead();
        initView();
        getEmployeeInfo();
        requestData();
        requestData2();
        initListener();
        initData();
        magLinearLayoutMail.loaddingData();
        mGridView2.loaddingData();
        mGridView1.loaddingData();
        magLinearLayoutExamineNum.loaddingData();
        return mView;
    }

    private void initData() {
        RequestConfig config = new RequestConfig(mActivity, HttpTools.GET_MINISERVER);
        config.handler = msgHandler.getHandler();
        Map<String, Object> map = new HashMap();
        Map<String, String> params = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(getActivity(), map));
        HttpTools.httpGet_Map(Contants.URl.URL_NEW, "app/home/utility/miscApp", config, (HashMap) params);
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

    private void addHead() {
        /**
         * 头部
         */
        magLinearLayoutArea = (ManageMentLinearlayout) mView.findViewById(R.id.ll_area);//在管面积
        magLinearLayoutStock = (ManageMentLinearlayout) mView.findViewById(R.id.ll_stock);//集团股票
        magLinearLayoutTicket = (ManageMentLinearlayout) mView.findViewById(R.id.ll_ticket);//我的饭票
        magLinearLayoutCommunity = (ManageMentLinearlayout) mView.findViewById(R.id.ll_community);//在管小区
        magLinearLayoutPerformance = (ManageMentLinearlayout) mView.findViewById(R.id.ll_performance);//彩惠战况
        magLinearLayoutAccount = (ManageMentLinearlayout) mView.findViewById(R.id.ll_account);//即时分账
        /**
         * 加载隐藏布局控件
         */
        rlArea = (RelativeLayout) mView.findViewById(R.id.rl_area);//在管面积
        rlStock = (RelativeLayout) mView.findViewById(R.id.rl_stock);//集团股票
        rlTicket = (RelativeLayout) mView.findViewById(R.id.rl_ticket);//我的饭票
        rlCommunity = (RelativeLayout) mView.findViewById(R.id.rl_community);//在管小区
        rlPerformance = (RelativeLayout) mView.findViewById(R.id.rl_performance);//彩惠状况
        rlAccount = (RelativeLayout) mView.findViewById(R.id.rl_account);//即时分账
        /**
         * 头部6个按钮加载旋转圈
         */
        progressBarArea = (RotateProgress) mView.findViewById(R.id.progressBar_area);//在管面积
        progressBarStock = (RotateProgress) mView.findViewById(R.id.progressBar_stock);//集团股票
        progressBarTicket = (RotateProgress) mView.findViewById(R.id.progressBar_ticket);//我的饭票
        progressBarCommunity = (RotateProgress) mView.findViewById(R.id.progressBar_community);//在管小区
        progressBarPerformance = (RotateProgress) mView.findViewById(R.id.progressBar_performance);//彩惠状况
        progressBarAccount = (RotateProgress) mView.findViewById(R.id.progressBar_account);//即时分账
        /**
         * 头部6个textView控件(展示数据)
         */
        tvArea = (TextView) mView.findViewById(R.id.tv_area);//在管面积
        tvStock = (TextView) mView.findViewById(R.id.tv_stock);//集团股票
        tvTicket = (TextView) mView.findViewById(R.id.tv_ticket);//我的饭票
        tvCommunity = (TextView) mView.findViewById(R.id.tv_community);//在管小区
        tvPerformance = (TextView) mView.findViewById(R.id.tv_performance);//彩惠状况
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
        //彩惠状况
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
        magLinearLayoutArea.setOnClickListener(titleListener);
        magLinearLayoutStock.setOnClickListener(titleListener);
        magLinearLayoutTicket.setOnClickListener(titleListener);
        magLinearLayoutCommunity.setOnClickListener(titleListener);
        magLinearLayoutPerformance.setOnClickListener(titleListener);
        magLinearLayoutAccount.setOnClickListener(titleListener);
    }


    /**
     * 初始化
     */
    private void initListener() {
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
                            tvTicket.setText("0.00");
                        }
                    }
                } else {
                    progressBarTicket.setVisibility(View.GONE);
                    rlTicket.setVisibility(View.VISIBLE);
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
                HttpTools.httpGet(Contants.URl.URL_CPMOBILE, "/1.0/caiRedPaket/getBalance", config, params);
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
         * 彩惠战况
         */
        magLinearLayoutPerformance.setNetworkRequestListener(new NetworkRequestListener() {

            @Override
            public void onSuccess(ManageMentLinearlayout magLearLayout, Message msg, String response) {
                int code = HttpTools.getCode(response);
                String message = HttpTools.getMessageString(response);
                progressBarPerformance.setVisibility(View.GONE);
                rlPerformance.setVisibility(View.VISIBLE);
                if (code == 0) {
                    caiHuiEntity = GsonUtils.gsonToBean(response, CaiHuiEntity.class);
                    performance = String.valueOf(caiHuiEntity.getContent().getCount());
                    Tools.saveStringValue(mActivity, Contants.storage.PERFORMANCEHOME, performance);
                    if (performance != null || performance.length() > 0) {
                        tvPerformance.setText(performance);
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
                RequestConfig config = new RequestConfig(mActivity, 0);
                config.handler = msgHand.getHandler();
                RequestParams params = new RequestParams();
                HttpTools.httpGet(Contants.URl.URL_CAIHUI, "/order/count", config, params);
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


    /**
     * 加载数据
     */
    private void requestData2() {
        magLinearLayoutStock.postDelayed(new Runnable() {//集团股票

            @Override
            public void run() {
                magLinearLayoutStock.loaddingData();
            }
        }, 1000);
        magLinearLayoutPerformance.postDelayed(new Runnable() {//彩惠状况

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
     * 初始化控件
     */
    private void initView() {
        magLinearLayoutMail = (ManageMentLinearlayout) mView.findViewById(R.id.ll_mail);
        magLinearLayoutExamineNum = (ManageMentLinearlayout) mView.findViewById(R.id.ll_examineNum);
        magLinearLayoutLeave = (ManageMentLinearlayout) mView.findViewById(R.id.ll_leave);
        magLinearLayoutSign = (ManageMentLinearlayout) mView.findViewById(R.id.ll_sign);//签到
        tvMail = (TextView) mView.findViewById(R.id.tv_mail);
        tvExamineNum = (TextView) mView.findViewById(R.id.tv_examineNum);

        GridLayoutManager manager1 = new GridLayoutManager(getContext(), 4);
        GridLayoutManager manager2 = new GridLayoutManager(getContext(), 4);

        mGridView1 = mView.findViewById(R.id.gridview1);
        adapter1 = new ManagementAdapter1(mActivity, gridlist1);
        mGridView1.setNestedScrollingEnabled(false);

        mGridView1.addItemDecoration(new PaddingItemDecoration(20));
        mGridView1.setLayoutManager(manager1);
        mGridView1.setAdapter(adapter1);

        mGridView2 = mView.findViewById(R.id.gridview2);
        mGridView2.setNestedScrollingEnabled(false);
        adapter2 = new ManagementAdapter1(mActivity, gridlist2);
        mGridView2.addItemDecoration(new PaddingItemDecoration(20));
        mGridView2.setLayoutManager(manager2);
        mGridView2.setAdapter(adapter2);


        outLocalData();


        magLinearLayoutLeave.setOnClickListener(singleListener);// 请假
        magLinearLayoutSign.setOnClickListener(singleListener);// 签到
        magLinearLayoutMail.setOnClickListener(singleListener);//未读邮件
        magLinearLayoutExamineNum.setOnClickListener(singleListener);//未读审批

        ll_home_head = mView.findViewById(R.id.ll_recycler_head);
        ll_home_head.setFocusableInTouchMode(true);
        ll_home_head.requestFocus();
        /**
         * 未读邮件
         */
        magLinearLayoutMail.setNetworkRequestListener(new NetworkRequestListener() {

            @Override
            public void onSuccess(ManageMentLinearlayout magLearLayout, Message msg, String response) {
                int code = HttpTools.getCode(response);
                if (code == 0) {
                    JSONObject jsonObject = HttpTools.getContentJSONObject(response);
                    try {
                        if (jsonObject != null) {
                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            if (jsonObject1 != null) {
                                mail = jsonObject1.getString("recipientsum");
                                if (mail != null) {
                                    tvMail.setText(mail);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    tvMail.setText("0");
                }
            }

            @Override
            public void onFail(ManageMentLinearlayout magLearLayout, Message msg, String hintString) {
                ToastFactory.showToast(mActivity, hintString);
            }

            @Override
            public void onRequest(MessageHandler msgHand) {
                RequestConfig config = new RequestConfig(mActivity, 0);
                config.handler = msgHand.getHandler();
                RequestParams params = new RequestParams();
                params.put("uid", UserInfo.employeeAccount);
                HttpTools.httpGet(Contants.URl.URL_ICETEST, "/newmail/mail/getmailsumbyuid", config, params);
            }
        });
        /**
         * 常用应用与其他应用中只要是 “扫码开门”  “对公账户” 都触发该点击事件
         */
        adapter1.setItemEventListener(new ManagementAdapter1.ItemEventListener() {
            @Override
            public void onItemClick(GridViewInfo info) {
                if (info.clientCode.equals("smkm")) {//扫码开门
                    RequestConfig config = new RequestConfig(mActivity, HttpTools.GET_CZY_ID);
                    RequestParams params = new RequestParams();
                    params.put("oa", UserInfo.employeeAccount);
                    HttpTools.httpGet(Contants.URl.URL_ICETEST, "/newczy/customer/infoByOa", config, params);
                } else if (info.clientCode.equals("dgzh")) {//对公账户
                    startActivity(new Intent(mActivity, PublicAccountActivity.class));
                } else {
                    /**
                     * 其他点击事件拼接HTML5
                     */
                    if (info.sso != "") {
                        AuthTimeUtils mAuthTimeUtils = new AuthTimeUtils();
                        mAuthTimeUtils.IsAuthTime(mActivity, info.sso, info.clientCode, info.oauthType, info.developerCode, "");
                    }
                }
            }
        });
        /**
         * 常用应用
         */
        mGridView1.setNetworkRequestListener(new NetGridViewRequestListener() {
            @Override
            public void onSuccess(MyGridView1 gridView, Message msg, String response) {
                String jsonString = HttpTools.getContentString(response);
                if (jsonString != null) {
                    ResponseData app_list = HttpTools.getResponseKey(jsonString, "app_list");
                    if (app_list.length > 0) {
//                        存储在本地
                        Tools.saveCommonInfo(mActivity, response);
                        JSONArray jsonArray = app_list.getJSONArray(0, "list");
                        ResponseData data = HttpTools.getResponseKeyJSONArray(jsonArray);
                        GridViewInfo item = null;
                        gridlist1.clear();
                        if (data.length % 4 == 0) {
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
                                    gridlist1.add(item);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            for (int i = 0; i < data.length + (4 - data.length % 4); i++) {
                                try {
                                    item = new GridViewInfo();
                                    item.name = data.getString(i, "name");
                                    item.oauthType = data.getString(i, "oauthType");
                                    item.developerCode = data.getString(i, "app_code");
                                    item.clientCode = data.getString(i, "app_code");
                                    item.sso = data.getString(i, "url");
                                    if (i == data.length || i == data.length + 1 || i == data.length + 2) {

                                    } else {
                                        JSONObject icon = data.getJSONObject(i, "icon");
                                        if (icon != null || icon.length() > 0) {
                                            item.icon = icon.getString("android");
                                        }
                                    }
                                    gridlist1.add(item);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                }

                adapter1.notifyDataSetChanged();
            }

            @Override
            public void onRequest(MessageHandler msgHand) {
                // TODO Auto-generated method stub
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
         * 常用应用与其他应用中只要是“扫码开门” “对公账户” 都触发该点击事件
         */

        adapter2.setItemEventListener(new ManagementAdapter1.ItemEventListener() {
            @Override
            public void onItemClick(GridViewInfo info) {
                if (info.clientCode.equals("smkm")) {//扫码开门
                } else if (info.clientCode.equals("dgzh")) {//对公账户
                    startActivity(new Intent(mActivity, PublicAccountActivity.class));
                } else {
                    /**
                     * 其他点击事件拼接HTML5
                     */
                    if (!info.sso.equals("")) {
                        if (info.clientCode.equals("erh")) {
                            AuthTimeUtils mAuthTimeUtils = new AuthTimeUtils();
                            mAuthTimeUtils.IsAuthTime(mActivity, info.sso, info.clientCode, "0", info.developerCode, "");
                        } else {
                            AuthTimeUtils mAuthTimeUtils = new AuthTimeUtils();
                            mAuthTimeUtils.IsAuthTime(mActivity, info.sso, info.clientCode, "1", info.developerCode, "");
                        }

                    }
                }
            }
        });

        /**
         * 其他应用
         */
        mGridView2.setNetworkRequestListener(new NetGridViewRequestListener() {
            @Override
            public void onSuccess(MyGridView1 gridView, Message msg, String response) {
                String jsonString = HttpTools.getContentString(response);
                if (jsonString != null) {
                    ResponseData app_list = HttpTools.getResponseKey(jsonString, "app_list");
                    if (app_list.length > 0) {
                        Tools.saveElseInfo(mActivity, response);
                        JSONArray jsonArray = app_list.getJSONArray(1, "list");
                        ResponseData data = HttpTools.getResponseKeyJSONArray(jsonArray);
                        GridViewInfo item = null;
                        gridlist2.clear();
                        if (data.length % 4 == 0) {
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
                                    gridlist2.add(item);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            for (int i = 0; i < data.length + 1; i++) {
                                try {
                                    if (i == data.length) {
                                        item = new GridViewInfo();
                                    } else {
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
                                        gridlist2.add(item);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                }
                adapter2.notifyDataSetChanged();
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
    }

    /**
     * 获取实时审批数量
     */
    private void getApproval() {
        String skin_code = Tools.getStringValue(mActivity, Contants.storage.SKINCODE);
        if (skin_code.equals("102")) {//中住
            magLinearLayoutExamineNum.setNetworkRequestListener(new NetworkRequestListener() {

                @Override
                public void onSuccess(ManageMentLinearlayout magLearLayout, Message msg, String response) {
                    int code = HttpTools.getCode(response);
                    if (code == 0) {
                        JSONObject jsonObject = HttpTools.getContentJSONObject(response);
                        try {
                            if (jsonObject != null) {
                                examineNum = jsonObject.getString("number");
                                Log.e(TAG, "onSuccess:examineNum " + examineNum);
                                if (examineNum != null) {
                                    tvExamineNum.setText(examineNum);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        tvExamineNum.setText("0");
                    }
                }

                @Override
                public void onFail(ManageMentLinearlayout magLearLayout, Message msg, String hintString) {
                    ToastFactory.showToast(mActivity, hintString);
                }

                @Override
                public void onRequest(MessageHandler msgHand) {
                    RequestConfig config = new RequestConfig(mActivity, 0);
                    config.handler = msgHand.getHandler();
                    RequestParams params = new RequestParams();
                    params.put("username", UserInfo.employeeAccount);
                    HttpTools.httpGet(Contants.URl.URL_ICETEST, "/oa/examineNum", config, params);
                }
            });
        } else {
            String url = Contants.URl.URL_ICETEST + "/BPM/runtime/users/flows/pending/amount";
            ContentValues params = new ContentValues();
            String access_token = Tools.getAccess_token(mActivity);
            params.put("applicationId", "11e7-8d55-9c8815e6-8d1c-edfa8b3b5e37");
            params.put("userId", UserInfo.uid);
            params.put("access_token", access_token);
            ColorsConfig.commonParams(params);
            OkHttpConnector.httpGet(mActivity, url, params, new IGetListener() {
                @Override
                public void httpReqResult(String response) {
                    int code = HttpTools.getCode(response);
                    if (code == 0) {
                        String contentString = HttpTools.getContentString(response);
                        if (!TextUtils.isEmpty(contentString)) {
                            tvExamineNum.setText(contentString);
                            try {
                                count = Integer.parseInt(contentString);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

            String oaUrl = Contants.URl.URL_ICETEST + "/oa/examineNum";
            ContentValues params2 = new ContentValues();
            params2.put("username", UserInfo.employeeAccount);
            ColorsConfig.commonParams(params2);
            OkHttpConnector.httpGet(mActivity, oaUrl, params2, new IGetListener() {
                @Override
                public void httpReqResult(String response) {
                    OaConfig config = GsonUtil.parse(response, OaConfig.class);
                    if (config != null && config.isSuccess() && isAdded()) {
                        OaConfig.ContentBean bean = config.getContent();
                        if (bean != null) {
                            count = count + bean.getNumber();
                            tvExamineNum.setText(String.valueOf(count));
                        }
                    }
                }
            });
            if (UserInfo.employeeAccount.contains("tangxb") || UserInfo.employeeAccount.contains("yuzhibin")) {
                if (TextUtils.isEmpty(accessToken)) {
                    accessToken = Tools.getStringValue(mActivity, Contants.storage.APPAUTH);
                }
                Map<String, Object> map = new HashMap<>();
                map.put("access_token", accessToken);
                ColorsConfig.commonParams(map);
                Map<String, Object> map1 = TokenUtils.getNewBalance(mActivity, map);
                ContentValues paramsbalance = new ContentValues();
                Set<String> keySet = map1.keySet();
                Iterator<String> it = keySet.iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    Object value = map1.get(key);
                    paramsbalance.put(key, String.valueOf(value));
                }
                OkHttpConnector.httpPost(mActivity, Contants.URl.URL_ICETEST + "/jrpt/balance/getWaitingBalanceCount",
                        paramsbalance, new IPostListener() {
                            @Override
                            public void httpReqResult(String response) {
                                try {
                                    OaConfig config = GsonUtil.parse(response, OaConfig.class);
                                    if (config != null && config.isSuccess() && isAdded()) {
                                        OaConfig.ContentBean bean = config.getContent();
                                        if (bean != null) {
                                            balanceNum = bean.getWaitingBalanceCount();
                                            count = count + bean.getWaitingBalanceCount();
                                            tvExamineNum.setText(String.valueOf(count));
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            }
                        });
            }
        }
    }


    /**
     * 从本地数据库取出数据
     */
    public void outLocalData() {
        //常用应用
        commonjsonStr = Tools.getCommonName(mActivity);
        String jsonString = HttpTools.getContentString(commonjsonStr);
        if (StringUtils.isNotEmpty(jsonString)) {
            ResponseData app_list = HttpTools.getResponseKey(jsonString, "app_list");
            if (app_list.length > 0) {
                JSONArray jsonArray = app_list.getJSONArray(0, "list");
                ResponseData data = HttpTools.getResponseKeyJSONArray(jsonArray);
                GridViewInfo item = null;
                if (data.length % 4 == 0) {
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
                            gridlist1.add(item);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    for (int i = 0; i < data.length + (4 - data.length % 4); i++) {
                        try {
                            item = new GridViewInfo();
                            item.name = data.getString(i, "name");
                            item.oauthType = data.getString(i, "oauthType");
                            item.developerCode = data.getString(i, "app_code");
                            item.clientCode = data.getString(i, "app_code");
                            item.sso = data.getString(i, "url");
                            if (i == data.length || i == data.length + 1 || i == data.length + 2) {

                            } else {
                                JSONObject icon = data.getJSONObject(i, "icon");
                                if (icon != null || icon.length() > 0) {
                                    item.icon = icon.getString("android");
                                }
                            }
                            gridlist1.add(item);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            adapter1 = new ManagementAdapter1(mActivity, gridlist1);
            mGridView1.setAdapter(adapter1);
        }
        elsejsonStr = Tools.getElseName(mActivity);
        //其他应用
        String jsonelse = HttpTools.getContentString(elsejsonStr);
        if (StringUtils.isNotEmpty(jsonelse)) {
            ResponseData app_list = HttpTools.getResponseKey(jsonelse, "app_list");
            if (app_list.length > 0) {
                JSONArray jsonArray = app_list.getJSONArray(1, "list");
                ResponseData data = HttpTools.getResponseKeyJSONArray(jsonArray);
                GridViewInfo item = null;
                if (data.length % 4 == 0) {
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
                            gridlist2.add(item);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    for (int i = 0; i < data.length + 1; i++) {
                        try {
                            if (i == data.length) {
                                item = new GridViewInfo();
                            } else {
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
                                gridlist2.add(item);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                adapter2 = new ManagementAdapter1(mActivity, gridlist2);
                mGridView2.setAdapter(adapter2);
            }
        }
    }

    public void requestData() {
        corpUuid = Tools.getStringValue(mActivity, Contants.storage.CORPID);
        key = Tools.getStringValue(mActivity, Contants.EMPLOYEE_LOGIN.key);
        secret = Tools.getStringValue(mActivity, Contants.EMPLOYEE_LOGIN.secret);

        long time = System.currentTimeMillis() / 1000;
        String expireTime = Tools.getStringValue(mActivity, Contants.storage.APPAUTHTIME);

        accessToken = Tools.getStringValue(mActivity, Contants.storage.APPAUTH);
        accessToken_1 = Tools.getStringValue(mActivity, Contants.storage.APPAUTH_1);
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
//            count = 0;
//            getApproval();
        }
    }

    private void getKeyAndSecret() {
        RequestConfig config = new RequestConfig(mActivity, HttpTools.GET_KEYSECERT, null);
        HttpTools.httpPost(Contants.URl.URL_CPMOBILE, "/1.0/auth", config, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        count = 0;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getApproval();
            }
        }, 2000);
        key = Tools.getStringValue(mActivity, Contants.EMPLOYEE_LOGIN.key);
        secret = Tools.getStringValue(mActivity, Contants.EMPLOYEE_LOGIN.secret);
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(secret)) {
            getKeyAndSecret();
        } else {
            magLinearLayoutTicket.postDelayed(new Runnable() {//我的饭票
                @Override
                public void run() {
                    magLinearLayoutTicket.loaddingData();
                }
            }, 1500);
        }

        magLinearLayoutPerformance.postDelayed(new Runnable() {//彩惠战况
            @Override
            public void run() {
                magLinearLayoutPerformance.loaddingData();
            }
        }, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onRequestStart(Message msg, String hintString) {
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        int code = HttpTools.getCode(jsonString);
        if (msg.arg1 == HttpTools.SET_EMPLOYEE_INFO) {
            if (code == 0) {
                JSONObject content = HttpTools.getContentJSONObject(jsonString);
                if (content != null) {
                    Tools.setBooleanValue(mActivity, Contants.storage.EMPLOYEE_LOGIN, true);
                }
            }
        } else if (msg.arg1 == HttpTools.GET_CZY_ID) {
            if (code == 1) {
                JSONArray jsonArray = HttpTools.getContentJsonArray(jsonString);
                try {
                    JSONObject object = (JSONObject) jsonArray.get(0);
                    String CZY_id = object.getString("id");
                    String community_id = object.getString("community_id");
                    Tools.saveCZYID(mActivity, CZY_id);
                    Tools.saveCZY_Community_ID(mActivity, community_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (msg.arg1 == HttpTools.GET_KEYSECERT) {
            if (code == 0) {
                try {
                    String contentString = HttpTools.getContentString(jsonString);
                    JSONObject sonJon = new JSONObject(contentString);
                    String key = sonJon.optString("key");
                    String secret = sonJon.optString("secret");
                    Tools.saveStringValue(mActivity, Contants.EMPLOYEE_LOGIN.key, key);
                    Tools.saveStringValue(mActivity, Contants.EMPLOYEE_LOGIN.secret, secret);
                    magLinearLayoutTicket.loaddingData();
                    getEmployeeInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (msg.arg1 == HttpTools.GET_MINISERVER) {
            if (code == 0) {

            } else {
                String message = HttpTools.getMessageString(jsonString);
                ToastFactory.showToast(mActivity, message);
            }

        }
    }

    @Override
    public void onFail(Message msg, String hintString) {
    }
}

