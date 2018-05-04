package com.tg.coloursteward;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.tg.coloursteward.adapter.DataShowTenantAdapter;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.info.DataShowInfo;
import com.tg.coloursteward.info.MapDataResp;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.util.StringUtils;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.ToastFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 租户列表
 * Created by prince70 on 2018/4/10.
 */

public class TenantListActivity extends BaseActivity {
    private static final String TAG = "TenantListActivity";
    private ArrayList<DataShowInfo> infoArrayList = new ArrayList<>();
    private DataShowTenantAdapter adapter;
    private ListView lv_tenant;
    private String accessToken;
    private String totalArea;
    private String onlineArea;
    private String offlineArea;
    private String tobedelieverdArea;
    private String areaAccount;
    private String parkingAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        totalArea = getIntent().getStringExtra("totalArea");
        onlineArea = getIntent().getStringExtra("onlineArea");
        offlineArea = getIntent().getStringExtra("offlineArea");
        tobedelieverdArea = getIntent().getStringExtra("tobedelieverdArea");
        areaAccount = getIntent().getStringExtra("areaAccount");
        parkingAccount = getIntent().getStringExtra("parkingAccount");

//        Log.e(TAG, "onCreate: " + totalArea + "\n" + onlineArea + "\n" + offlineArea + "\n" + tobedelieverdArea + "\n" + areaAccount + "\n" + parkingAccount);
        initView();
    }

    private void initView() {
        lv_tenant = (ListView) findViewById(R.id.lv_tenant);
        accessToken = Tools.getStringValue(this, Contants.storage.APPAUTH);
        adapter = new DataShowTenantAdapter(this, infoArrayList);
        lv_tenant.setAdapter(adapter);
//        getTenantList();
        getTenantList2();

    }

    /**
     * 获取租户列表（测试）
     */
    private void getTenantList() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_TENANT_LIST);
        RequestParams params = new RequestParams();
        params.put("token", accessToken);
        params.put("isAll", 1);
        params.put("orgUuid", 0);
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/resourcems/community/statistics", config, params);
    }

    /**
     * 获取租户列表
     */
    private void getTenantList2() {
        RequestConfig config = new RequestConfig(this, HttpTools.GET_TENANT_LIST);
        RequestParams params = new RequestParams();
        HttpTools.httpGet(Contants.URl.URL_ICETEST, "/authms/corp/search", config, params);
    }


    @Override
    public View getContentView() {
        return getLayoutInflater().inflate(R.layout.activity_tenant_list, null);
    }

    @Override
    public String getHeadTitle() {
        return "租户列表";
    }

    /**
     * 添加数据
     *
     * @param info
     */
    private void getDataMagment(MapDataResp info) {
        DataShowInfo item = new DataShowInfo();
        item.mainTitle = "彩生活";
        item.title = "小区面积（㎡）";
        item.content = info.floorArea;

        item.title1 = "上线面积";
        item.title2 = "下线面积";
        item.title3 = "待交付面积";
        item.content1 = info.launchArea;
        item.content2 = info.offlineArea;
        item.content3 = info.to_be_deliveredArea;
        item.title4 = "小区数";
        item.content4 = info.communityCount;
        item.title5 = "车位数量";
        item.content5 = info.parkingCount;
        infoArrayList.add(item);

        item = new DataShowInfo();

        item.mainTitle = "开元国际";
        item.title = "小区面积（㎡）";
        item.content = info.floorArea;

        item.title1 = "上线面积";
        item.title2 = "下线面积";
        item.title3 = "待交付面积";
        item.content1 = info.launchArea;
        item.content2 = info.offlineArea;
        item.content3 = info.to_be_deliveredArea;
        item.title4 = "小区数";
        item.content4 = info.communityCount;
        item.title5 = "车位数量";
        item.content5 = info.parkingCount;

        infoArrayList.add(item);

    }

    @Override
    public void onSuccess(Message msg, String jsonString, String hintString) {
        super.onSuccess(msg, jsonString, hintString);
        int code = HttpTools.getCode(jsonString);
        String message = HttpTools.getMessageString(jsonString);
        if (msg.arg1 == HttpTools.GET_TENANT_LIST) {
            if (code == 0) {
                JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
                if (jsonObject != null) {
                    MapDataResp info = new MapDataResp();
                    DataShowInfo item;
                    try {
                        JSONArray list = jsonObject.getJSONArray("list");
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject jsonObject1 = list.getJSONObject(i);
                            String corpUuid = jsonObject1.getString("corpUuid");
                            String corpName = jsonObject1.getString("corpName");
                            if (corpUuid.equals("a8c58297436f433787725a94f780a3c9")) {//彩生活的才有数据
//                                    info.title=corpName;
//                                    info.floorArea=totalArea;
//                                    info.launchArea=onlineArea;
//                                    info.offlineArea=offlineArea;
//                                    info.to_be_deliveredArea=tobedelieverdArea;
//                                    info.communityCount=areaAccount;
//                                    info.parkingCount=parkingAccount;
                                item = new DataShowInfo();
                                item.mainTitle = corpName;
                                item.title = "小区面积（㎡）";
                                item.content = totalArea;

                                item.title1 = "上线面积";
                                item.title2 = "下线面积";
                                item.title3 = "待交付面积";
                                item.content1 = onlineArea;
                                item.content2 = offlineArea;
                                item.content3 = tobedelieverdArea;
                                item.title4 = "小区数";
                                item.content4 = areaAccount;
                                item.title5 = "车位数量";
                                item.content5 = parkingAccount;
                                infoArrayList.add(item);

                            } else {
                                item = new DataShowInfo();
                                item.mainTitle = corpName;
                                item.title = "小区面积（㎡）";
                                item.content = "";

                                item.title1 = "上线面积";
                                item.title2 = "下线面积";
                                item.title3 = "待交付面积";
                                item.content1 = "";
                                item.content2 = "";
                                item.content3 = "";
                                item.title4 = "小区数";
                                item.content4 = "";
                                item.title5 = "车位数量";
                                item.content5 = "";
                                infoArrayList.add(item);
                            }
//                            Log.e(TAG, "onSuccess: corpName" + corpName);
//                            getDataMagment(info);

                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                DataShowInfo item = new DataShowInfo();
                item.mainTitle = "";
                item.title = "小区面积（㎡）";
                item.content = "";

                item.title1 = "上线面积";
                item.title2 = "下线面积";
                item.title3 = "待交付面积";
                item.content1 = "";
                item.content2 = "";
                item.content3 = "";
                item.title4 = "小区数";
                item.content4 = "";
                item.title5 = "车位数量";
                item.content5 = "";
                infoArrayList.add(item);
                adapter.notifyDataSetChanged();
                ToastFactory.showToast(this, message);
            }
        }
    /*    if (msg.arg1 == HttpTools.GET_TENANT_LIST) {
            if (code == 0) {
                JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
                if (jsonObject != null) {
                    MapDataResp info = new MapDataResp();
                    try {
//                        info.title = "彩生活集团";
                        info.communityCount = jsonObject.getString("count");
                        int upParkingSpace = jsonObject.getInt("upParkingSpace");//地上车位数
                        int midParkingSpace = jsonObject.getInt("midParkingSpace");//架空车位数
                        int downParkingSpace = jsonObject.getInt("downParkingSpace");//地下车位数
                        info.parkingCount = String.valueOf(upParkingSpace + midParkingSpace + downParkingSpace);

                        String coveredArea = jsonObject.getString("coveredArea");//上线面积
                        double aa = Double.parseDouble(coveredArea);
                        BigDecimal a = new BigDecimal(aa);//上线面积数值
                        info.launchArea = a + "";
                        String contractArea = jsonObject.getString("contractArea");//合同面积
                        double bb = Double.parseDouble(contractArea);
                        BigDecimal b = new BigDecimal(bb);//合同面积数值

                        String delivered = jsonObject.getString("delivered");//已交付面积
                        double cc = Double.parseDouble(delivered);
                        BigDecimal c = new BigDecimal(cc);//已交付面积数值

                        BigDecimal bc = b.subtract(c);
                        Log.e(TAG, "onSuccess: " + b + "   " + c + "     " + bc + "    " + bc.toPlainString() + "   " + String.valueOf(bc));
                        info.to_be_deliveredArea = bc + "";//待交付面积

                        JSONObject js = jsonObject.getJSONObject("撤场数据");
                        String coveredArea1 = js.getString("coveredArea");//下线面积
                        double dd = Double.parseDouble(coveredArea1);
                        BigDecimal d = new BigDecimal(dd);//下线面积数值
                        info.offlineArea = d + "";

                        BigDecimal add = a.add(d);
                        BigDecimal total = add.add(bc);
                        Log.e(TAG, "onSuccess:total " + total);
                        info.floorArea = total + "";//小区面积
                        getDataMagment(info);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                MapDataResp info = new MapDataResp();
                info.title = "";
                info.floorArea = "";
                info.launchArea = "";
                info.communityCount = "";
                info.parkingCount = "";
                info.to_be_deliveredArea = "";
                info.offlineArea = "";
                getDataMagment(info);
                adapter.notifyDataSetChanged();
                ToastFactory.showToast(this, message);
            }
        }*/
    }
}
