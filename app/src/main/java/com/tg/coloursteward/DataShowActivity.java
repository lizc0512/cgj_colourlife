package com.tg.coloursteward;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.tg.coloursteward.adapter.DataShowAdapter;
import com.tg.coloursteward.adapter.ViewPagerAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.DataShowInfo;
import com.tg.coloursteward.info.FamilyInfo;
import com.tg.coloursteward.info.MapDataResp;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.MyViewPager;
import com.tg.coloursteward.view.PullRefreshListViewFind;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.os.Bundle;
import android.content.Intent;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 数据看板
 *
 * @author Administrator
 */
public class DataShowActivity extends BaseActivity implements OnCheckedChangeListener, OnPageChangeListener {
    private static final String TAG = "DataShowActivity";
    private DataShowAdapter mAdapter1;
    private DataShowAdapter mAdapter2;
    public final static String BRANCH = "branch";
    private String branch;
    private TextView tvOrgId;
    private Intent intent;
    private MyViewPager viewPager;
    private RadioGroup radioGroup;
    private ViewPagerAdapter pagerAdapter;
    private ArrayList<View> pagerList = new ArrayList<View>();
    private ListView mListView1;
    private ListView mListView2;
    private RelativeLayout rlOrgId;
    private ArrayList<DataShowInfo> list1 = new ArrayList<DataShowInfo>();//管理类
    private ArrayList<DataShowInfo> list2 = new ArrayList<DataShowInfo>();//经营类
    private String corpUuid;
    private String orgType;
    private AuthAppService authAppService;
    private String accessToken;

    private String registerNum = "";
    private boolean isSuccess = false;


    private String TotalArea;//小区面积
    private String OnlineArea;//上线面积
    private String OfflineArea;//下线面积
    private String ToBeDelieveredArea;//待交付面积
    private String AreaAccount;//小区数量
    private String ParkingAccount;//车位数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        if (intent != null) {
            branch = intent.getStringExtra(BRANCH);
        }
        if (branch == null) {
            ToastFactory.showToast(this, "参数错误");
            finish();
            return;
        }
        orgType = Tools.getStringValue(this, Contants.storage.ORGTYPE);
//        token如果已经过期了就要重新获取数据
        getToken();
        initView();
        radioGroup.setOnCheckedChangeListener(this);
        viewPager.setOnPageChangeListener(this);
    }

    private void initView() {
        /**
         * 平台数据
         */
        rlOrgId = (RelativeLayout) findViewById(R.id.rl_orgId);
        rlOrgId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FamilyInfo info = new FamilyInfo();
                info.id = "9959f117-df60-4d1b-a354-776c20ffb8c7";
                info.type = "org";
                info.name = UserInfo.familyName;
                intent = new Intent(DataShowActivity.this, BranchActivity.class);
                intent.putExtra(BranchActivity.FAMILY_INFO, info);
                startActivityForResult(intent, 1);
            }
        });

        /**
         * 发布新版本前的改动
         */
//        rlOrgId.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DataShowActivity.this, TenantListActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("totalArea",TotalArea);
//                bundle.putString("onlineArea",OnlineArea);
//                bundle.putString("offlineArea",OfflineArea);
//                bundle.putString("tobedelieverdArea",ToBeDelieveredArea);
//                bundle.putString("areaAccount",AreaAccount);
//                bundle.putString("parkingAccount",ParkingAccount);
//                intent.putExtras(bundle);
//                startActivity(intent);
//            }
//        });
        tvOrgId = (TextView) findViewById(R.id.tv_orgId);
        tvOrgId.setText(UserInfo.familyName);
        /**
         * 发布新版本前的改动
         */
//        tvOrgId.setText("平台数据");


        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        viewPager = (MyViewPager) findViewById(R.id.viewPager);
        RadioButton btn1 = (RadioButton) findViewById(R.id.rb_noticBtn);
        RadioButton btn2 = (RadioButton) findViewById(R.id.rb_notifiicationBtn);
        btn1.setText("管理类");
        btn2.setText("经营类");
        mListView1 = new ListView(this);
        mAdapter1 = new DataShowAdapter(this, list1);
        mListView1.setAdapter(mAdapter1);
        pagerList.add(mListView1);

        mListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    DataShowInfo item = new DataShowInfo();
                }
            }
        });


        mListView2 = new ListView(this);
        mAdapter2 = new DataShowAdapter(this, list2);
        mListView2.setAdapter(mAdapter2);
        pagerList.add(mListView2);
        pagerAdapter = new ViewPagerAdapter(pagerList, this);
        viewPager.setAdapter(pagerAdapter);
    }

    /**
     * 得到token值
     */
    private void getToken() {
        corpUuid = Tools.getStringValue(this, Contants.storage.CORPID);
        Date dt = new Date();
        Long time = dt.getTime();
        String expireTime = Tools.getStringValue(DataShowActivity.this, Contants.storage.APPAUTHTIME);
        accessToken = Tools.getStringValue(DataShowActivity.this, Contants.storage.APPAUTH);
        if (StringUtils.isNotEmpty(expireTime)) {
            if (Long.parseLong(expireTime) <= time) {//token过期
                getAuthAppInfo();
            } else {
                getData();//获取数据
            }
        } else {
            getAuthAppInfo();
        }
    }

    /**
     * 获取数据
     */
    private void getData() {

//        /**
//         * 获取APP注册数量
//         */
//        RequestConfig config = new RequestConfig(DataShowActivity.this, HttpTools.GET_REGISTER_ACCOUNT);
//        RequestParams params = new RequestParams();
//        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/czyprovide/customer/getRegisterQuantity", config, params);

        /**
         * 管理类
         * 获取全部数据
         */
        //Log.d("printLog","orgUuid="+branch);
        //Log.d("printLog","orgType="+orgType);
        RequestConfig config = new RequestConfig(DataShowActivity.this, HttpTools.GET_STATISTICS_INFO);
        RequestParams params = new RequestParams();
        params.put("token", accessToken);
        params.put("corpId", corpUuid);
        params.put("orgUuid", branch);

        /**
         * 发布新版本前的改动
         */
//        params.put("isAll", 1);
//        params.put("orgUuid", 0);


//        Log.e(TAG, "getData: " + accessToken + "   " + corpUuid + "  " + branch);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/resourcems/community/statistics", config, params);
        /**
         * 经营类
         */
        config = new RequestConfig(DataShowActivity.this, HttpTools.GET_KPI_INFO);
        params = new RequestParams();
        params.put("groupUuid", "9959f117-df60-4d1b-a354-776c20ffb8c7");
        params.put("level", 1);
        if (orgType.equals("彩生活集团")) {
            params.put("level", 1);
        } else if (orgType.equals("大区")) {
            params.put("level", 2);
            params.put("regiongroupUuid", branch);
        } else if (orgType.equals("事业部")) {
            params.put("level", 3);
            params.put("districtUuid", branch);
        } else {
            params.put("level", 4);
            params.put("regionUuid", branch);
        }
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/xsfxt/report/charge_receipt", config, params);


    }


    private void getDataMagment2(MapDataResp info){

        DataShowInfo item = new DataShowInfo();
        item.title = "小区面积（㎡）";
        item.content = info.floorArea;
        list1.add(item);


        item = new DataShowInfo();
        item.title = "小区数";
        item.content = info.communityCount;
        list1.add(item);

        item = new DataShowInfo();
        item.title = "车位数量";
        item.content = info.parkingCount;
        list1.add(item);

        item = new DataShowInfo();
        item.title = "APP安装数量";
        item.content = info.appCount;
        list1.add(item);

        item = new DataShowInfo();
        item.title = "上线小区";
        item.content = info.join_smallarea_num;
        list1.add(item);
    }


    /**
     * 管理类添加数据
     */
    private void getDataMagment(MapDataResp info) {
        DataShowInfo item = new DataShowInfo();
        item.title = "小区面积（㎡）";
//        String area = info.floorArea;
//        if (StringUtils.isNotEmpty(area)) {
//            item.content = Tools.formatTosepara(Float.valueOf(area));
//        }

        item.title1 = "上线面积";
        item.title2 = "下线面积";
        item.title3 = "待交付面积";
//        String launchArea = info.launchArea;
//        if (StringUtils.isNotEmpty(launchArea)) {
//            item.content1 = Tools.formatTosepara(Float.valueOf(launchArea));
//        }
//        String offlineArea = info.offlineArea;
//        if (StringUtils.isNotEmpty(offlineArea)) {
//            item.content2 = Tools.formatTosepara(Float.valueOf(offlineArea));
//        }
//        String to_be_deliveredArea = info.to_be_deliveredArea;
//        if (StringUtils.isNotEmpty(to_be_deliveredArea)) {
//            item.content3 = Tools.formatTosepara(Float.valueOf(to_be_deliveredArea));
//        }
        item.content = info.floorArea;
        item.content1 = info.launchArea;
        item.content2 = info.offlineArea;
        item.content3 = info.to_be_deliveredArea;


        list1.add(item);


        item = new DataShowInfo();
        item.title = "小区数";
        item.content = info.communityCount;
        list1.add(item);

        item = new DataShowInfo();
        item.title = "车位数量";
        item.content = info.parkingCount;
        list1.add(item);

        item = new DataShowInfo();
//        item.title = "APP安装数量";
        item.title = "APP注册数量";
        item.content = info.appCount;
        list1.add(item);

//        item = new DataShowInfo();
//        item.title = "上线小区";
//        item.content = info.join_smallarea_num;
//        list1.add(item);
    }


    /**
     * 经营类添加数据
     */
    private void getDataBusiness(MapDataResp info) {
        DataShowInfo item = new DataShowInfo();
        item.title = "当前查询费用日期";
        item.content = info.dateTime;
        list2.add(item);

        item = new DataShowInfo();
        item.title = "应收";
        item.content = info.normalFee;
        list2.add(item);

        item = new DataShowInfo();
        item.title = "实收";
        item.content = info.receivedFee;
        list2.add(item);

        item = new DataShowInfo();
        item.title = "收费率";
        item.content = info.feeRate;
        list2.add(item);

        item = new DataShowInfo();
        item.title = "业主投诉数";
        item.content = info.complainCount;
        list2.add(item);
    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
//        if (msg.arg1 == HttpTools.GET_REGISTER_ACCOUNT) {
//            if (code == 0) {
//                isSuccess = true;
//                JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
//                if (jsonObject.length() > 0) {
//                    MapDataResp info = new MapDataResp();
//                    try {
//                        String quantity = jsonObject.getString("quantity");
//                        Log.e(TAG, "onSuccess: quantity" + quantity);
//                        if (!quantity.equals("")) {
//                            registerNum = quantity;
//                            Log.e(TAG, "onSuccess: registerNum" + registerNum);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    registerNum = 0 + "";
//                }
//            }
//        } else
        if (msg.arg1 == HttpTools.GET_STATISTICS_INFO) {//管理类
            Log.e(TAG, "onSuccess: +++++++++++" + registerNum);
            if (code == 0) {
                JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
                if (jsonObject.length() > 0) {
                    MapDataResp info = new MapDataResp();
                    DecimalFormat df = new DecimalFormat("#,###");
                    try {
                        info.communityCount = jsonObject.getString("count");
                        /**
                         * 发布新版本之前的改动
                         */
//                        请求成功在这里显示APP注册数量
//                        String account = Tools.getStringValue(this, "APP_REGISTER_ACCOUNT");
//                        info.appCount = account;



                        info.appCount = "0";
                        int upParkingSpace = jsonObject.getInt("upParkingSpace");//地上车位数
                        int midParkingSpace = jsonObject.getInt("midParkingSpace");//架空车位数
                        int downParkingSpace = jsonObject.getInt("downParkingSpace");//地下车位数
                        info.parkingCount = String.valueOf(upParkingSpace + midParkingSpace + downParkingSpace);
//                        info.floorArea = jsonObject.getString("area");
                        info.join_smallarea_num = jsonObject.getString("count");

                        String coveredArea = jsonObject.getString("coveredArea");//上线面积
                        double aa = Double.parseDouble(coveredArea);
                        BigDecimal a = new BigDecimal(aa);//上线面积数值
                        info.launchArea = df.format(a);
                        String contractArea = jsonObject.getString("contractArea");//合同面积
                        double bb = Double.parseDouble(contractArea);
                        BigDecimal b = new BigDecimal(bb);//合同面积数值

                        String delivered = jsonObject.getString("delivered");//已交付面积
                        double cc = Double.parseDouble(delivered);
                        BigDecimal c = new BigDecimal(cc);//已交付面积数值

                        BigDecimal bc = b.subtract(c);
                        Log.e(TAG, "onSuccess: " + b + "   " + c + "     " + bc + "    " + bc.toPlainString() + "   " + String.valueOf(bc));
                        info.to_be_deliveredArea = df.format(bc);//待交付面积

                        JSONObject js = jsonObject.getJSONObject("撤场数据");
                        String coveredArea1 = js.getString("coveredArea");//下线面积
                        double dd = Double.parseDouble(coveredArea1);
                        BigDecimal d = new BigDecimal(dd);//下线面积数值
                        info.offlineArea = df.format(d);

                        BigDecimal add = a.add(d);
                        BigDecimal total = add.add(bc);
                        Log.e(TAG, "onSuccess:total " + total);
                        info.floorArea = df.format(total) + "";//小区面积


                        TotalArea = df.format(total);
                        OnlineArea = df.format(a);
                        OfflineArea = df.format(d);
                        ToBeDelieveredArea = df.format(bc);
                        AreaAccount = jsonObject.getString("count");
                        ParkingAccount = String.valueOf(upParkingSpace + midParkingSpace + downParkingSpace);

//                        getDataMagment(info);
                        /**
                         * 发布新版本前的改动
                         */
                        getDataMagment2(info);
                        mAdapter1.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                MapDataResp info = new MapDataResp();
                info.communityCount = "0";
                info.appCount = "0";
                info.parkingCount = "0";
                info.floorArea = "0";
                info.join_smallarea_num = "0";
                info.launchArea = "0";
                info.offlineArea = "0";
                info.to_be_deliveredArea = "0";

                TotalArea = "";
                OnlineArea ="";
                OfflineArea = "";
                ToBeDelieveredArea = "";
                AreaAccount = "";
                ParkingAccount = "";

//                getDataMagment(info);
                /**
                 * 发布新版本前的改动
                 */
                getDataMagment2(info);


                mAdapter1.notifyDataSetChanged();
                ToastFactory.showToast(DataShowActivity.this, message);
            }
        } else if (msg.arg1 == HttpTools.GET_KPI_INFO) {//经营类
            if (code == 0) {
                JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
                if (jsonObject.length() > 0) {
                    MapDataResp info = new MapDataResp();
                    try {
                        info.normalFee = jsonObject.getString("receivableValue");
                        info.receivedFee = jsonObject.getString("chargeValue");
                        info.dateTime = jsonObject.getString("dateTime");
                        info.feeRate = "0.00";
                        info.complainCount = "0";
                        getDataBusiness(info);
                        mAdapter2.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                MapDataResp info = new MapDataResp();
                info.normalFee = "0";
                info.receivedFee = "0";
                info.dateTime = "0";
                info.feeRate = "0.00";
                info.complainCount = "0";
                getDataBusiness(info);
                mAdapter2.notifyDataSetChanged();
                ToastFactory.showToast(DataShowActivity.this, message);
            }
        }
    }

    @Override
    public void onFail(Message msg, String hintString) {
        super.onFail(msg, hintString);
        ToastFactory.showToast(DataShowActivity.this, hintString);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            list1.clear();
            list2.clear();
            branch = data.getStringExtra("id");
            orgType = data.getStringExtra("orgType");
            String name = data.getStringExtra("name");
            tvOrgId.setText(name);//分支的名字
            getToken();
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            radioGroup.check(R.id.rb_noticBtn);
        } else {
            radioGroup.check(R.id.rb_notifiicationBtn);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rb_noticBtn) {
            if (viewPager.getCurrentItem() != 0) {
                viewPager.setCurrentItem(0);
                //listViewReceive.performLoading();
            }
        } else {
            if (viewPager.getCurrentItem() != 1) {
                viewPager.setCurrentItem(1);
                //listViewExpend.performLoading();
            }
        }
    }

    @Override
    public View getContentView() {
        // TODO Auto-generated method stub
        return getLayoutInflater().inflate(R.layout.activity_data_show, null);
    }

    @Override
    public String getHeadTitle() {
        // TODO Auto-generated method stub
        return "数据看板";
    }

    /**
     * 获取token
     * sectet
     */
    private void getAuthAppInfo() {
        if (authAppService == null) {
            authAppService = new AuthAppService(DataShowActivity.this);
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
                            Tools.saveStringValue(DataShowActivity.this, Contants.storage.APPAUTH, accessToken);
                            Tools.saveStringValue(DataShowActivity.this, Contants.storage.APPAUTHTIME, expireTime);
                            getData();
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
