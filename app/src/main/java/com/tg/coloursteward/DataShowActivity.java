package com.tg.coloursteward;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.tg.coloursteward.serice.AuthAppService;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.TokenUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.MyViewPager;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据看板
 *
 * @author Administrator
 */
public class DataShowActivity extends BaseActivity implements OnCheckedChangeListener, OnPageChangeListener {
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
        getToken();
        initView();
        radioGroup.setOnCheckedChangeListener(this);
        viewPager.setOnPageChangeListener(this);
    }

    private void initView() {
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
        tvOrgId = (TextView) findViewById(R.id.tv_orgId);
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
        //管理类
        RequestConfig config = new RequestConfig(DataShowActivity.this, HttpTools.GET_STATISTICS_INFO);
        Map<String, Object> params = new HashMap<>();
        Map<String, String> stringMap = TokenUtils.getStringMap(TokenUtils.getNewSaftyMap(this, params));
        HttpTools.httpGet_Map(Contants.URl.URL_NEW, "app/home/utility/managerMsg", config, (HashMap) stringMap);
        //经营类
        config = new RequestConfig(DataShowActivity.this, HttpTools.GET_KPI_INFO);
        RequestParams requestParams = new RequestParams();
        requestParams.put("groupUuid", "9959f117-df60-4d1b-a354-776c20ffb8c7");
        String level = "0";
        requestParams.put("level", level);
        if (orgType.equals("彩生活集团")) {
            requestParams.put("level", 1);
        } else if (orgType.equals("大区")) {
            requestParams.put("level", 2);
            requestParams.put("regiongroupUuid", branch);
        } else if (orgType.equals("事业部")) {
            requestParams.put("level", 3);
            requestParams.put("districtUuid", branch);
        } else if (orgType.equals("小区")) {
            requestParams.put("level", 4);
            requestParams.put("regionUuid", branch);
        }
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/xsfxt/report/charge_receipt", config, requestParams);
    }


    private void getDataMagment2(MapDataResp info) {

        DataShowInfo item = new DataShowInfo();
        item.title = "上线面积（万㎡）";
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
        if (msg.arg1 == HttpTools.GET_STATISTICS_INFO) {//管理类
            if (code == 0) {
                JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
                if (jsonObject.length() > 0) {
                    MapDataResp info = new MapDataResp();
                    try {
                        info.floorArea = jsonObject.getString("area");
                        info.communityCount = jsonObject.getString("community");
                        info.parkingCount = jsonObject.getInt("park") + "";
                        info.appCount = jsonObject.getString("app_num");
                        info.join_smallarea_num = jsonObject.getString("comunity_online");

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

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

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
            }
        } else {
            if (viewPager.getCurrentItem() != 1) {
                viewPager.setCurrentItem(1);
            }
        }
    }

    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_data_show, null);
    }

    @Override
    public String getHeadTitle() {
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
