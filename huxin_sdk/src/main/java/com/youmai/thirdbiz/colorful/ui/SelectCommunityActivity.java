package com.youmai.thirdbiz.colorful.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.youmai.thirdbiz.colorful.bean.AddrBean;
import com.youmai.thirdbiz.colorful.bean.BlockBean;
import com.youmai.thirdbiz.colorful.bean.CommunityBean;
import com.youmai.thirdbiz.colorful.bean.MyUnitBean;
import com.youmai.thirdbiz.colorful.bean.RoomNoBean;
import com.youmai.thirdbiz.colorful.net.ColorsConfig;
import com.youmai.thirdbiz.colorful.net.ColorsUtil;
import com.youmai.thirdbiz.colorful.view.wheelview.pickerview.OptionsPickerView;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.activity.SdkBaseActivity;
import com.youmai.hxsdk.http.IGetListener;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.GsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by Administrator on 2016/12/26 0026.
 */

public class SelectCommunityActivity extends SdkBaseActivity implements View.OnClickListener {

    private RelativeLayout selectCity, selectCommunity, selectFloor, selectHouseNumber;
    private Button btnNext;
    OptionsPickerView pvOptions;


    OptionsPickerView mAreaView;

    OptionsPickerView mbuildView;

    OptionsPickerView mUnitView;

    OptionsPickerView mOwnerView;

    // TODO:
    public AddrBean mAddrBean;

    public CommunityBean communityBean;


    public BlockBean blockBean;

    public MyUnitBean unitBean;

    public RoomNoBean roomNoBean;

    //ui 省市区
    public ArrayList<String> uiProvinceList = new ArrayList<>();
    public ArrayList<List<String>> uiCityList = new ArrayList<>();
    public ArrayList<List<List<String>>> uiDistrictList = new ArrayList<>();

    //ui  小区
    public ArrayList<String> uiCommunityList = new ArrayList<>();
    public ArrayList<String> uiBuildingList = new ArrayList<>();

    public ArrayList<String> uiUnitList = new ArrayList<>();
    public ArrayList<String> uiownerList = new ArrayList<>();

    private TextView tvProvince;
    private TextView tv_back, tv_building, tv_house_number, tv_unite;
    private ProgressDialog progressDialog;

    String province;
    String city;
    String district;
    String provincecode;
    String citycode;
    String regioncode;

    String areaUuid;
    String blockUuId;
    String unitUuId;
    String houseId;
    String ownerUuId;

    private TextView tvCommunity;
    private RelativeLayout selectUnite;
    private RelativeLayout title_se_community;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cgj_activity_color_select_area);
        initView();
        initListener();
    }

    /**
     * 获取省市区
     */
    private void initAddressData() {
        //  创建选项选择器
        pvOptions = new OptionsPickerView(this);

        uiCityList.clear();
        uiDistrictList.clear();
        uiProvinceList.clear();

        ArrayList<String> tempDist1;
        ArrayList<List<String>> tempDist2;
        ArrayList<String> tempCityList;

        for (AddrBean.province province : mAddrBean.content.provinces) {

            tempCityList = new ArrayList<>();
            tempDist2 = new ArrayList<>();
            for (AddrBean.city city : province.citys) {
                tempDist1 = new ArrayList<>();
                for (AddrBean.district district : city.districts) {
                    tempDist1.add(district.name);
                }
                tempDist2.add(tempDist1);
                tempCityList.add(city.name);
            }
            uiCityList.add(tempCityList);
            uiDistrictList.add(tempDist2);
            uiProvinceList.add(province.name);
        }

        //  设置三级联动效果
        pvOptions.setPicker(uiProvinceList, uiCityList, uiDistrictList, true);

        //  设置选择的三级单位
        //pvOptions.setLabels("省", "市", "区");
        pvOptions.setTitle("城市选择");

        //  设置是否循环滚动
        pvOptions.setCyclic(false, false, false);


        // 设置默认选中的三级项目
        pvOptions.setSelectOptions(0, 0, 0);
        //  监听确定选择按钮
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是三个级别的选中位置
                String address;
                address = uiProvinceList.get(options1)
                        + " " + uiCityList.get(options1).get(option2)
                        + " " + uiDistrictList.get(options1).get(option2).get(options3);


                district = mAddrBean.content.provinces.get(options1).getCitys().get(option2).getDistricts().get(options3).getName();
                regioncode = mAddrBean.content.provinces.get(options1).getCitys().get(option2).getDistricts().get(options3).getId();

                city = mAddrBean.content.provinces.get(options1).getCitys().get(option2).getName();
                citycode = mAddrBean.content.provinces.get(options1).getCitys().get(option2).getId();

                province = mAddrBean.content.provinces.get(options1).getName();
                provincecode = mAddrBean.content.provinces.get(options1).getId();
                if((tvProvince.getText().toString())!=address){
                    tvProvince.setText(address);
                    tvCommunity.setText("请选择小区");
                    tv_building.setText("请选择楼栋");
                    tv_unite.setText("请选择单元");
                    tv_house_number.setText("请选择门牌号");
                    areaUuid = null;
                    blockUuId = null;
                    unitUuId = null;
                    houseId = null;
                    ownerUuId = null;
                }


                //fixme:
                android.util.Log.e("xx", "p=" + province + " c=" + city + " d=" + district + " pc=" + provincecode + " cc=" + citycode + " rc=" + regioncode);
            }
        });
        pvOptions.show();

    }

    private void initView() {
        //主题的长按事件
        title_se_community = (RelativeLayout) findViewById(R.id.title_se_community);
        title_se_community.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(SelectCommunityActivity.this, ColorsConfig.COLOR_BUILD_VERSION, Toast.LENGTH_LONG).show();
                return true;
            }
        });

        tv_back = (TextView) findViewById(R.id.tv_back);
        selectCity = (RelativeLayout) findViewById(R.id.rl_select_city);
        tvProvince = (TextView) findViewById(R.id.tv_province);
        selectCommunity = (RelativeLayout) findViewById(R.id.rl_select_community);
        tvCommunity = (TextView) findViewById(R.id.tv_select_community);

        selectFloor = (RelativeLayout) findViewById(R.id.rl_select_floor);
        tv_building = (TextView) findViewById(R.id.tv_building);

        selectUnite = (RelativeLayout) findViewById(R.id.rl_select_unite);
        tv_unite = (TextView) findViewById(R.id.tv_unite);

        selectHouseNumber = (RelativeLayout) findViewById(R.id.rl_select_house_number);
        tv_house_number = (TextView) findViewById(R.id.tv_house_number);
        btnNext = (Button) findViewById(R.id.btn_next);

    }



    private void initListener() {
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        selectCity.setOnClickListener(this);
        selectCommunity.setOnClickListener(this);
        selectFloor.setOnClickListener(this);
        selectHouseNumber.setOnClickListener(this);
        selectUnite.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    /**
     * 初始化小区数据.fixme
     */
    public void initCommunityData() {
        mAreaView = new OptionsPickerView(this);

        mAreaView.setPicker(uiCommunityList);
//        mOptionSelecteView = new OptionSelecteCommunityView(this);

//        mOptionSelecteView.setPicker(uiCommunityList);
        //设置小区显示
        //  mOptionSelecteView.setPicker();
        //  设置是否循环滚动
//        mOptionSelecteView.setCyclic(false);
        mAreaView.setCyclic(false);

        // 设置默认选中的三级项目
        mAreaView.setSelectOptions(0);


        //  监听确定选择按钮
        mAreaView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options, int options1, int options2) {
                String tempCommunity = uiCommunityList.get(options);
                if(tvCommunity.getText().toString()!= tempCommunity){
                    tvCommunity.setText(tempCommunity);
                    tv_building.setText("请选择楼栋");
                    tv_unite.setText("请选择单元");
                    tv_house_number.setText("请选择门牌号");
                    areaUuid = null;
                    blockUuId = null;
                    unitUuId = null;
                    houseId = null;
                    ownerUuId = null;
                }

                // areaUuid= uiCommunityList.
                areaUuid = communityBean.getContent().get(options).getUuid();

                Log.e("xx", "areauuid=" + areaUuid);
            }
        });
        if(uiCommunityList.size()!= 0){
            mAreaView.show();
        }

    }

    /**
     * 初始化楼栋数据.fixme
     */
    public void initBuildingData() {
        mbuildView = new OptionsPickerView(this);
        if(uiBuildingList.size() != 0){
            mbuildView.setPicker(uiBuildingList);
        }
        //  设置是否循环滚动
        mbuildView.setCyclic(false);

        // 设置默认选中的三级项目
        mbuildView.setSelectOptions(0);

        //  监听确定选择按钮
        mbuildView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options, int options1, int options2) {
                String tempBuilding = uiBuildingList.get(options);
                if(tv_building.getText().toString() != tempBuilding){
                    tv_building.setText(tempBuilding);
                    tv_unite.setText("请选择单元");
                    tv_house_number.setText("请选择门牌号");
                    blockUuId = null;
                    unitUuId = null;
                    houseId = null;
                    ownerUuId = null;
                }
                String key = uiBuildingList.get(options);
                blockUuId = blockBean.content.housetype.get(key);
                Log.e("xx", blockUuId);

            }
        });
        if(uiBuildingList.size() != 0){
            mbuildView.show();
        }

    }

    /**
     * 初始化单元
     */
    public void initUnitData() {
        mUnitView = new OptionsPickerView(this);

        mUnitView.setPicker(uiUnitList);

        //  设置是否循环滚动
        mUnitView.setCyclic(false);

        // 设置默认选中的三级项目
        mUnitView.setSelectOptions(0);

        //  监听确定选择按钮
        mUnitView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options, int options1, int options2) {
                String temp = uiUnitList.get(options);

                if(tv_unite.getText().toString() != temp){
                    tv_unite.setText(temp);
                    tv_house_number.setText("请选择门牌号");
                    unitUuId = null;
                    houseId = null ;
                    ownerUuId = null;
                }
                //fixme
                unitUuId = unitBean.content.unit.get(temp);
            }
        });

        if(uiUnitList.size() != 0){
            mUnitView.show();
        }

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.rl_select_city) {
            if(AppUtils.isNetworkConnected(this)){
                getRegions();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(selectCity.getWindowToken(), 0);
                //选择城市
                return;
            }else{
                Toast.makeText(this,getString(R.string.hx_not_internet_connection), Toast.LENGTH_SHORT).show();
                return;
            }


        } else if (i == R.id.rl_select_community) {
            //选择小区
            if(AppUtils.isNetworkConnected(this)){
                if((province == null) || ( city == null) || (district== null) || (provincecode == null) || (citycode== null) || (district== null)){
                    Toast.makeText(this,"请选择省市区....", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    getCommunity();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(selectCommunity.getWindowToken(), 0);
                    return;
                }

            }else{
                Toast.makeText(this,getString(R.string.hx_not_internet_connection), Toast.LENGTH_SHORT).show();
                return;
            }

        } else if (i == R.id.rl_select_floor) {
            if(AppUtils.isNetworkConnected(this)){
                //选择楼栋
                if(areaUuid != null){
                    getBuilding();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(selectCommunity.getWindowToken(), 0);
                    return;
                } else {
                    Toast.makeText(this,"请选择小区....", Toast.LENGTH_SHORT).show();
                    return;
                }
            }else{
                Toast.makeText(this,getString(R.string.hx_not_internet_connection), Toast.LENGTH_SHORT).show();
                return;
            }



        } else if (i == R.id.rl_select_unite) {
            //选择单元
            if(AppUtils.isNetworkConnected(this)){
                if ( areaUuid != null && blockUuId !=null){
                    getUnite();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(selectCommunity.getWindowToken(), 0);

                    return;
                } else {
                    Toast.makeText(this,"请选择楼栋....", Toast.LENGTH_SHORT).show();
                    return;
                }
            }else{
                Toast.makeText(this,getString(R.string.hx_not_internet_connection), Toast.LENGTH_SHORT).show();
                return;
            }

        } else if (i == R.id.rl_select_house_number) {
            if(AppUtils.isNetworkConnected(this)){
                //选择门牌号
                if( areaUuid != null && blockUuId !=null && unitUuId != null ){
                    getHouse();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(selectCommunity.getWindowToken(), 0);
                    return;
                }else{
                    Toast.makeText(this,"请选择单元....", Toast.LENGTH_SHORT).show();
                    return;
                }
            }else{
                Toast.makeText(this,getString(R.string.hx_not_internet_connection), Toast.LENGTH_SHORT).show();
                return;
            }


        } else if (i == R.id.btn_next) {

            Log.e("areaUuid =="+areaUuid,"blockUuId====="+blockUuId+"unitUuId====="+unitUuId+"houseId====="+houseId);
                //下一步

            if(AppUtils.isNetworkConnected(this)){
                if(areaUuid != null && blockUuId !=null && unitUuId != null && houseId != null){
                    //下一步
//                Intent it = new Intent(SelectCommunityActivity.this, ColorAddOwnerActivity.class);
//                it.putExtra("uuid", ownerUuId);
//                it.putExtra("dstPhone",getIntent().getStringExtra("dstPhone"));
//                startActivityForResult(it, 1);
                    getOwnerUuid();
                    return;

                }else {
                    Toast.makeText(this,"请将以上资料填写完整....", Toast.LENGTH_SHORT).show();
                    return;
                }
            }else{
                Toast.makeText(this,getString(R.string.hx_not_internet_connection), Toast.LENGTH_SHORT).show();
                return;
            }

        }
    }

    /**
     * 获取业主接口
     * */
    private void getOwnerUuid() {
        ShowProgress();
        ColorsUtil.reUuid(houseId, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                Log.e("xx","resp=" + response);
                progressDialog.dismiss();
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int code = jsonObject.optInt("code");
                        String message = jsonObject.optString("message");
                        if (code == 0) {
                            JSONArray contentArray = jsonObject.optJSONArray("content");
                            if (contentArray != null && contentArray.length() > 0) {
                                JSONObject contentItemObj =  contentArray.optJSONObject(0);
                                ownerUuId = contentItemObj.optString("owner_uuid");
                                Log.e("xx"," ownerUuId=" + ownerUuId);
                                if(ownerUuId != null){
                                    Intent it = new Intent(SelectCommunityActivity.this, ColorAddOwnerActivity.class);
                                    it.putExtra("uuid", ownerUuId);
                                    it.putExtra("dstPhone", getIntent().getStringExtra("dstPhone"));
                                    startActivityForResult(it, 1);
                                    return;
                                }
                            }
                        } else {
                            Toast.makeText(SelectCommunityActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });


    }


    /**
     * 获取楼栋
     */
    private void getBuilding() {


        ShowProgress();
        blockBean = new BlockBean();
        uiBuildingList.clear();
        ColorsUtil.reBuilding(areaUuid, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                Log.e("areaUuid==", areaUuid);
                Log.e("response==", response);
                progressDialog.dismiss();
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int code = jsonObject.optInt("code");
                        String message = jsonObject.optString("message");
                        blockBean.code = code;
                        blockBean.message = message;
                        if (code == 0) {
                            JSONObject contentJObj = jsonObject.optJSONObject("content");
                            if (contentJObj != null) {
                                JSONObject houseType = contentJObj.optJSONObject("housetype");
                                if (houseType != null) {
                                    Iterator iterator = houseType.keys();
                                    String tempKey;
                                    String tempValue;
                                    while (iterator.hasNext()) {
                                        tempKey = (String) iterator.next();
                                        tempValue = houseType.optString(tempKey);
                                        blockBean.content.housetype.put(tempValue, tempKey);
                                        Log.e("xx", "key=" + tempKey + " value=" + tempValue);
                                        uiBuildingList.add(tempValue);
                                        initBuildingData();
                                        return;
                                    }
                                }
                                Toast.makeText(SelectCommunityActivity.this, "没有对应的楼栋信息,请选择正确的小区....;.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } else {
                            Toast.makeText(SelectCommunityActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        });

    }

    /**
     * 获取房号
     */
    private void getHouse() {
        ShowProgress();
        roomNoBean = new RoomNoBean();
        uiownerList.clear();
        ColorsUtil.reHouseNumber(unitUuId, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                Log.e("unitUuId==", unitUuId);
                Log.e("response==", response);
                progressDialog.dismiss();
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int code = jsonObject.optInt("code");
                        String message = jsonObject.optString("message");
                        roomNoBean.code = code;
                        roomNoBean.message = message;
                        if (code == 0) {
                            JSONObject contentJObj = jsonObject.optJSONObject("content");
                            if (contentJObj != null) {
                                Iterator iterator = contentJObj.keys();
                                String tempKey;
                                String tempValue;
                                while (iterator.hasNext()) {
                                    tempKey = (String) iterator.next();
                                    tempValue = contentJObj.optString(tempKey);
                                    roomNoBean.content.put(tempValue, tempKey);
                                    uiownerList.add(tempValue);
                                    initHouse();
                                    return;
                                }
                            }
                            Toast.makeText(SelectCommunityActivity.this, "没有对应的门牌号信息,请选择正确的单元....;.", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Toast.makeText(SelectCommunityActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
    }

    public void initHouse() {
        mOwnerView = new OptionsPickerView(this);

        mOwnerView.setPicker(uiownerList);

        //  设置是否循环滚动
        mOwnerView.setCyclic(false);

        // 设置默认选中的三级项目
        mOwnerView.setSelectOptions(0);

        //  监听确定选择按钮
        mOwnerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options, int options1, int options2) {
                String temp = uiownerList.get(options);
                tv_house_number.setText(temp);
                //fixme
                houseId = roomNoBean.content.get(temp);
            }
        });
        if(uiownerList!=null){
            mOwnerView.show();
        }

    }


    /**
     * 获取单元号
     */
    private void getUnite() {
        ShowProgress();
        unitBean = new MyUnitBean();
        uiUnitList.clear();
        ColorsUtil.reUnit(blockUuId, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                Log.e("areaUuid==", blockUuId);
                Log.e("response==", response);

                progressDialog.dismiss();
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int code = jsonObject.optInt("code");
                        String message = jsonObject.optString("message");
                        unitBean.code = code;
                        unitBean.message = message;
                        if (code == 0) {
                            JSONObject contentJObj = jsonObject.optJSONObject("content");
                            if (contentJObj != null) {
                                JSONObject unitObj = contentJObj.optJSONObject("unit");
                                if (unitObj != null) {
                                    Iterator iterator = unitObj.keys();
                                    String tempKey;
                                    String tempValue;
                                    while (iterator.hasNext()) {
                                        tempKey = (String) iterator.next();
                                        tempValue = unitObj.optString(tempKey);
                                        unitBean.content.unit.put(tempValue, tempKey);
                                        Log.e("xx", "key=" + tempKey + " value=" + tempValue);
                                        uiUnitList.add(tempValue);
                                        initUnitData();
                                        return;
                                    }
                                }
                                Toast.makeText(SelectCommunityActivity.this, "没有对应的单元信息,请选择正确的楼栋....;.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } else {
                            Toast.makeText(SelectCommunityActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
    }

    /**
     * 获取小区
     */

    private void getCommunity() {
        ShowProgress();
        //fixme
        android.util.Log.e("xx", "p=" + province + " c=" + city + " d=" + district + " pc=" + provincecode + " cc=" + citycode + " rc=" + regioncode);
        ColorsUtil.reqCommunity(province, city, district, provincecode, citycode, regioncode, new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                progressDialog.dismiss();
                communityBean = new CommunityBean();
                uiCommunityList.clear();
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int code = jsonObject.optInt("code");
                        String message = jsonObject.optString("message");
                        communityBean.setCode(code);
                        communityBean.setMessage(message);
                        if (code == 0) {
                            JSONArray contentArrayJObj = jsonObject.optJSONArray("content");
                            if (contentArrayJObj != null && contentArrayJObj.length() > 0) {
                                List<CommunityBean.ContentBean> contentBeenList = new ArrayList<CommunityBean.ContentBean>();
                                for (int i = 0; i < contentArrayJObj.length(); i++) {
                                    JSONObject itemJObj = contentArrayJObj.getJSONObject(i);
                                    if (itemJObj != null) {
                                        CommunityBean.ContentBean contentBean = new CommunityBean.ContentBean();
                                        contentBean.setName(itemJObj.optString("name"));
                                        contentBean.setUuid(itemJObj.optString("uuid"));
                                        contentBeenList.add(contentBean);
                                        uiCommunityList.add(contentBean.getName());
                                    }
                                }
                                communityBean.setContent(contentBeenList);
                                initCommunityData();
                                return;
                            }
                        } else {
                            Toast.makeText(SelectCommunityActivity.this, message, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(SelectCommunityActivity.this, "暂未获取到小区信息", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /***
     * 获取省市区
     */
    public void getRegions() {
        ShowProgress();
        ColorsUtil.reqRegions(new IGetListener() {
            @Override
            public void httpReqResult(String response) {
                progressDialog.dismiss();
                if (!TextUtils.isEmpty(response)) {
                    mAddrBean = GsonUtil.parse(response, AddrBean.class);
                    initAddressData();
                }
            }
        });
    }

    /**
     * 显示进度条
     */
    public void ShowProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在获取数据");
        progressDialog.show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(null, 0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1){
            finish();
        }
    }


}
