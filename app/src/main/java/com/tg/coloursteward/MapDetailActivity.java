package com.tg.coloursteward;

import com.amap.api.maps2d.MapView;
import com.tg.coloursteward.application.CityPropertyApplication;
import com.tg.coloursteward.base.BaseActivity;
import com.tg.coloursteward.constant.Contants;
import com.tg.coloursteward.database.SharedPreferencesTools;
import com.tg.coloursteward.fragment.FragmentManagement;
import com.tg.coloursteward.info.MapDataResp;
import com.tg.coloursteward.info.MapListResp;
import com.tg.coloursteward.info.PoiCommunity;
import com.tg.coloursteward.info.UserInfo;
import com.tg.coloursteward.net.HttpTools;
import com.tg.coloursteward.net.MessageHandler;
import com.tg.coloursteward.net.MessageHandler.ResponseListener;
import com.tg.coloursteward.net.GetTwoRecordListener;
import com.tg.coloursteward.net.RequestConfig;
import com.tg.coloursteward.net.RequestParams;
import com.tg.coloursteward.net.ResponseData;
import com.tg.coloursteward.serice.HomeService;
import com.tg.coloursteward.util.Tools;
import com.tg.coloursteward.view.dialog.DialogFactory;
import com.tg.coloursteward.view.dialog.ToastFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.AMap.OnMapLoadedListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.AMap.OnMarkerDragListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.LocationSource.OnLocationChangedListener;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.LatLngBounds.Builder;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * 战略地图
 * @author Administrator
 *
 */
public class MapDetailActivity extends Activity implements  OnClickListener,
						OnMarkerClickListener, OnMarkerDragListener, OnMapLoadedListener,
						OnMapClickListener, AMapLocationListener, LocationSource, ResponseListener {
	private MessageHandler msgHandler;
	 // 地图
    private MapView mMapView = null;
    private AMap aMap;
    private UiSettings mUiSettings;
    // 地图类型：0代表默认第一次进入状态，1代表七大区域地图，2代表点进区域事业部地图，3代表点进事业部片区地图，4代表点进片区的小区地图，5代表点进小区小区详情地图
    private int mapType = 1;

    private List<LatLng> latLngs = new ArrayList<LatLng>();
    private List<Marker> markers = new ArrayList<Marker>();
    
    private HomeService homeService;
    private List<PoiCommunity> PoiCommunityList = new ArrayList<PoiCommunity>();

    private ArrayList<MapListResp> mapListResps = new ArrayList<MapListResp>();
    private MapDataResp mapDataResp;
    // 返回按钮
    private TextView backBtn;
    // 搜索布局
    // 标题
    private TextView tv_title;
    // 关闭
    private TextView btnClose;
    // 统计数据布局
    private LinearLayout linearLayout;
    // 物管面积
    private TextView tv_area;
    // 应收
    private TextView tv_receivable;
    // 实收
    private TextView tv_official_receipts;
    // 收缴率
    private TextView tv_collection_rate;
    // 滿意度
    private TextView tv_satisfaction;
    // APP安装数
    private TextView tv_appcount;
    // 业主投诉数
    private TextView tv_complaint;
    // 停车位
    // private TextView tv_parking_space;
    // 全国总部地图数据（一级地图）
    private MapListResp mapListResp1;
    // 区域地图数据（二级地图）
    private MapListResp mapListResp2;
    // 事业部地图数据（三级地图）
    private MapListResp mapListResp3;
    // 片区地图数据（三级地图）
    private MapListResp mapListResp4;
    //切换定位
    private ImageView ima_mapdetail_water;
    // 判断附近小区（全国地图）
    private boolean isWaterLocat = false;
    // 判断是否显示显示侧边数据栏
    private boolean isShowData = true;

    private TextView tv_community_num;

    private TextView tv_parkingCount;

    private TextView tv_qrcode2open;

    private TextView tv_parkingFee;

    private TextView tv_cameraStatus;

    private TextView tv_guardStatus;

    private TextView tv_barrierStatus;

    private TextView tv_community_appCount;

    private TextView tv_community_complainCount;

    private TextView tv_IllegalUp;

    private LinearLayout mLinearlayout_poicommunity;

    private Animation slideout;

    private Animation slidein;

    private boolean clickable_maplist = true;

    private boolean clickable_mapdata = true;

    private String orgUuid = "-1";

    private boolean isCommunity = false;


    private AMapLocationClient mlocationClient;
    private OnLocationChangedListener mListener;

    private boolean isPersonalAuth = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_detail);
		msgHandler = new MessageHandler(MapDetailActivity.this);
		msgHandler.setResponseListener(this);
		findViewById(R.id.ima_map_email).setOnClickListener(this);
		findViewById(R.id.ima_map_shenpi).setOnClickListener(this);
		findViewById(R.id.ima_map_showorhidden).setOnClickListener(this);
		findViewById(R.id.tv_seahealth_onlineinfo).setOnClickListener(this);

		ima_mapdetail_water = (ImageView) findViewById(R.id.ima_mapdetail_water);
		ima_mapdetail_water.setOnClickListener(this);

		prepareView();
		mMapView = (MapView) findViewById(R.id.bmapsView);
		mMapView.onCreate(savedInstanceState);// 此方法必须重写
		initOrg();
	}
	/**
	 * 初始化控件
	 */
	private void prepareView(){
        // 绑定按钮事件
        backBtn = (TextView) findViewById(R.id.btnBack);
        backBtn.setOnClickListener(this);
        backBtn.setVisibility(View.INVISIBLE);
        // 标题
        tv_title = (TextView) findViewById(R.id.tv_title);
        // 关闭
        btnClose = (TextView) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);
        // 统计数据布局
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout_mapdata);
        // 物管面积
        tv_area = (TextView) findViewById(R.id.tv_area);
        // 收缴率
        tv_collection_rate = (TextView) findViewById(R.id.tv_collection_rate);
        // 应收
        tv_receivable = (TextView) findViewById(R.id.tv_receivable);
        // 实收
        tv_official_receipts = (TextView) findViewById(R.id.tv_official_receipts);
        // 滿意度
        tv_satisfaction = (TextView) findViewById(R.id.tv_satisfaction);
        // APP安装数
        tv_appcount = (TextView) findViewById(R.id.tv_appcount);
        // 业主投诉数
        tv_complaint = (TextView) findViewById(R.id.tv_complaint);
        // 小区数
        tv_community_num = (TextView) findViewById(R.id.tv_community_num);
        // 停车位
        tv_parkingCount = (TextView) findViewById(R.id.tv_parkingCount);

        // 附近小区
        mLinearlayout_poicommunity = (LinearLayout) findViewById(R.id.linearlayout_poicommunity);
        // 二维码扫描次数
        tv_qrcode2open = (TextView) findViewById(R.id.tv_qrcode2open);
        // 停车场累计收费总额
        tv_parkingFee = (TextView) findViewById(R.id.tv_parkingFee);
        // 海康摄像头在线情况
        tv_cameraStatus = (TextView) findViewById(R.id.tv_cameraStatus);
        // 门禁在线情况
        tv_guardStatus = (TextView) findViewById(R.id.tv_guardStatus);
        // 格美特道闸系统
        tv_barrierStatus = (TextView) findViewById(R.id.tv_barrierStatus);
        // 小区APP安装数
        tv_community_appCount = (TextView) findViewById(R.id.tv_community_appCount);
        // 小区业主投诉数
        tv_community_complainCount = (TextView) findViewById(R.id.tv_community_complainCount);
        // 停车场非法起杆
        tv_IllegalUp = (TextView) findViewById(R.id.tv_IllegalUp);

    }
	
	/**
	 * 获取战略地图权限
	 */
	private void initOrg() {
		RequestConfig config = new RequestConfig(this, HttpTools.GET_USER_INITORG);
		config.handler = msgHandler.getHandler();
		RequestParams params = new RequestParams();
		params.put("username", UserInfo.employeeAccount);
		HttpTools.httpGet(Contants.URl.URL_ICETEST,"/bigdata/initOrg",config, params);
    }
	
	/**
	 * 获取附近小区
	 */
	private void getPoiCommunity(String lat, String lng) {
		RequestConfig config = new RequestConfig(this, HttpTools.GET_POI_COMMUNITY);
		config.handler = msgHandler.getHandler();
		RequestParams params = new RequestParams();
		params.put("uid", UserInfo.employeeAccount);
		params.put("location",lng + "%2C" + lat );
		params.put("radius",3000);
		HttpTools.httpGet(Contants.URl.URL_ICETEST,"/poi/community",config, params);
	}
	
	/**
	 * 获取kpi信息
	 */
	private void getkpi(String branch) {
		RequestConfig config = new RequestConfig(this, HttpTools.GET_KPI_INFO);
		config.handler = msgHandler.getHandler();
		RequestParams params = new RequestParams();
		params.put("uid", UserInfo.employeeAccount);
		params.put("branch",branch);
		params.put("year", "2016");
		HttpTools.httpGet(Contants.URl.URL_ICETEST, "/bigdata/kpi",config, params);
	}
	
	/**
	 * 获取kpi信息
	 */
	private void getkpiYear(String branch,int year) {
		RequestConfig config = new RequestConfig(this, HttpTools.GET_KPI_YEAR);
		config.handler = msgHandler.getHandler();
		RequestParams params = new RequestParams();
		params.put("uid", UserInfo.employeeAccount);
		params.put("branch",branch);
		params.put("year", year);
		HttpTools.httpGet(Contants.URl.URL_ICETEST, "/bigdata/kpi",config, params);
	}
	/**
	 * 获取kpi信息
	 */
	private void getkpiYearInfo(String branch,int year) {
		RequestConfig config = new RequestConfig(this, HttpTools.GET_KPI_YEAR_INFO);
		config.handler = msgHandler.getHandler();
		RequestParams params = new RequestParams();
		params.put("uid", UserInfo.employeeAccount);
		params.put("branch",branch);
		params.put("year", year);
		HttpTools.httpGet(Contants.URl.URL_ICETEST, "/bigdata/kpi",config, params);
	}
	
	/**
	 * 获取地图列表
	 */
	private void getMapListInfo(String branch_id) {
		RequestConfig config = new RequestConfig(this, HttpTools.GET_MAP_LIST);
		config.handler = msgHandler.getHandler();
		RequestParams params = new RequestParams();
		params.put("id", branch_id);
		HttpTools.httpGet(Contants.URl.URL_ICETEST, "/bigdata/branch",config, params);
	}
	
	private void getMapDatas()
    {
        String branch_id = "";

        if (mapType == 1)
        {
            branch_id = mapListResp1.uuid + "";
        } else if (mapType == 2)
        {
            branch_id = mapListResp2.uuid + "";
        } else if (mapType == 3)
        {
            branch_id = mapListResp3.uuid + "";
        } else if (mapType == 4)
        {
            branch_id = mapListResp4.uuid + "";
        }

        Calendar a = Calendar.getInstance();
        int year = a.get(Calendar.YEAR);
        getkpiYearInfo(branch_id, year);
    }

	/**
     * 初始化AMap对象
     */
    private void init(String name, String uuid, String lat, String lng){
    	if (aMap == null) {
            aMap = mMapView.getMap();
            mUiSettings = aMap.getUiSettings();

            mUiSettings.setZoomGesturesEnabled(true);
            mUiSettings.setScrollGesturesEnabled(true);
            mUiSettings.setZoomControlsEnabled(false);

            mUiSettings.setScaleControlsEnabled(true);
            aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

            mUiSettings.setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
            setUpMap();
            slidein = AnimationUtils
                    .loadAnimation(this, R.anim.mapdata_slidein);
            slideout = AnimationUtils.loadAnimation(this,
                    R.anim.mapdata_slideout);
        }
    	 mapListResp1 = new MapListResp();
         mapListResp1.name = name;
         mapListResp1.uuid = uuid;
         mapListResp1.lat = lat;
         mapListResp1.lng = lng;
         mapListResp2 = new MapListResp();
         mapListResp3 = new MapListResp();
         mapListResp4 = new MapListResp();
    }
    
    private void setUpMap()
    {
        aMap.setOnMapClickListener(this);// 对amap添加单击地图事件监听器
        aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
        aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
    }
    
    private void addMarkers(List<MapListResp> datas, float zoom) {
    	Gson gson = new Gson();
        latLngs.clear();
        markers.clear();
        if (aMap != null)
        {
            aMap.clear();
            aMap.invalidate();
        }

        // 中心点纬度
        String latCenter = "35.6972813";
        // 中心点经度
        String lonCenter = "104.238281";
        if (mapType == 1)
        {
            latCenter = mapListResp1.lat;
            lonCenter = mapListResp1.lng;
        } else if (mapType == 2)
        {
            latCenter = mapListResp2.lat;
            lonCenter = mapListResp2.lng;
        } else if (mapType == 3)
        {
            latCenter = mapListResp3.lat;
            lonCenter = mapListResp3.lng;
        } else if (mapType == 4)
        {
            latCenter = mapListResp4.lat;
            lonCenter = mapListResp4.lng;
        }

        setMapCenter(latCenter, lonCenter, zoom);
        for (MapListResp mapListResp : datas)
        {
            try
            {
                LatLng latLng = new LatLng(
                        Double.valueOf(mapListResp.lat),
                        Double.valueOf(mapListResp.lng));
                latLngs.add(latLng);
                if (mapListResp.orgType.equals("小区"))
                {
                    aMap.addMarker(new MarkerOptions()
                            .anchor(0.5f, 0.5f)
                            .position(latLng)
                            .title(mapListResp.name)
                            .snippet(gson.toJson(mapListResp))
                            .icon(BitmapDescriptorFactory
                                    .fromView(getMapPaoPaoView(mapListResp
                                            .name))).draggable(true));
                } else
                {
                   aMap.addMarker(new MarkerOptions()
                            .anchor(0.5f, 0.5f)
                            .position(latLng)
                            .title(mapListResp.name)
                            .snippet(gson.toJson(mapListResp))
                            .icon(BitmapDescriptorFactory
                                    .fromView(getMapPaoPaoView(mapListResp
                                            .name
                                            + "("
                                            + mapListResp.communityCount
                                            + ")"))).draggable(true));
                }
            } catch (Exception e)
            {
            }
        }

        mMapView.invalidate();
    }
    
    /**
     * 添加区域事业部数量
     *
     * @param content
     * @return
     */
    private View getMapPaoPaoView(String content)
    {

        View view = LayoutInflater.from(MapDetailActivity.this).inflate(
                R.layout.map_paopao2, null);
        TextView textView = (TextView) view.findViewById(R.id.popview_title);
        textView.setText(content);

        return view;
    }
    // 设置地图中心点
    private void setMapCenter(String lat, String lon, float zoom)
    {
        try
        {
            Double lat1 = Double.valueOf(lat);
            Double lon1 = Double.valueOf(lon);
            LatLng marker1 = new LatLng(lat1, lon1); // 设置中心点
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(marker1));
            aMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
        } catch (Exception e)
        {
        }
    }
   
    /**
    * 社区数据
    * @param data
    */
    private void setCommunityDataFromKpi(MapDataResp data)
    {
        // 二维码开门次数
        tv_qrcode2open.setText(data.qrcode2open);
        // 停车场累计收费总额
        String ParkingFee = changeFormat(data.parkingFee);
        tv_parkingFee.setText(ParkingFee);
        // 海康摄像头在线情况
        tv_cameraStatus.setText(data.cameraStatus);
        // 门禁在线情况
        tv_guardStatus.setText(data.guardStatus);
        // 格美特道闸系统在线情况
        tv_barrierStatus.setText(data.barrierStatus);
        // 小区APP安装数
        tv_community_appCount.setText(data.appCount);
        // 小区业主投诉数
        tv_community_complainCount.setText(data.complainCount);
        // 停车场非法起杆
        tv_IllegalUp.setText("");
    }
    
    private void getMapList() {
        String branch_id = "";

        if (mapType == 1)
        {
            branch_id = mapListResp1.uuid + "";
        } else if (mapType == 2)
        {
            branch_id = mapListResp2.uuid + "";
        } else if (mapType == 3)
        {
            branch_id = mapListResp3.uuid + "";
        } else if (mapType == 4)
        {
            branch_id = mapListResp4.uuid + "";
        }
        getMapListInfo(branch_id);
    }
    
    private void addMarkersToMap(List<PoiCommunity> poiCommunityList)
    {

        for (int i = 0; i < poiCommunityList.size(); i++)
        {
            PoiCommunity poiCommunity = poiCommunityList.get(i);
            String name = poiCommunity.name;
            String id = poiCommunity.uuid;
            Double lat = Double.valueOf(poiCommunity.lat);
            Double lng = Double.valueOf(poiCommunity.lng);
            LatLng latLng = new LatLng(lat, lng);

            aMap.addMarker(new MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .position(latLng)
                    .title(name)
                    .snippet(id)
                    .icon(BitmapDescriptorFactory.fromView(getCustomView(name,
                            id))).draggable(true));

            setCommunityData(-1);
        }
    }
    
    private void setCommunityData(int postion)
    {
        if (postion != -1)
        {
            PoiCommunity poiCommunity = PoiCommunityList.get(postion);
            tv_title.setText(poiCommunity.name);
            // 二维码开门次数
            tv_qrcode2open.setText("");
            // 停车场累计收费总额
            tv_parkingFee.setText("");
            // 海康摄像头在线情况
            tv_cameraStatus.setText("");
            // 门禁在线情况
            tv_guardStatus.setText("");
            // 格美特道闸系统在线情况
            tv_barrierStatus.setText("");
            // APP安装数
            tv_community_appCount.setText("");
            // 业主投诉数
            tv_community_complainCount.setText("");
            // 停车场非法起杆
            tv_IllegalUp.setText("");
        } else
        {
            tv_qrcode2open.setText("");
            tv_parkingFee.setText("");
            tv_cameraStatus.setText("");
            tv_guardStatus.setText("");
            tv_barrierStatus.setText("");
            tv_community_appCount.setText("");
            tv_community_complainCount.setText("");
            tv_IllegalUp.setText("");
        }
    }
    
    // 大地图上获取小区数据
    private void getCommunityData(MapListResp mapListResp)
    {
        linearLayout.setVisibility(View.GONE);
        if (isShowData)
        {
            mLinearlayout_poicommunity.setVisibility(View.VISIBLE);
        }
        if (mapType == 2)
        {
            tv_title.setText("全国大区分布-" + mapListResp2.name + "-"
                    + mapListResp3.name);
        } else if (mapType == 3)
        {
            tv_title.setText("全国大区分布-" + mapListResp2.name + "-"
                    + mapListResp3.name + "-" + mapListResp4.name);
        } else if (mapType == 4 || mapType == 5)
        {
            tv_title.setText("全国大区分布-" + mapListResp2.name + "-"
                    + mapListResp3.name + "-" + mapListResp4.name
                    + "-" + mapListResp.name);
        }
        for (int i = 0; i < mapListResps.size(); i++)
        {
            String uuid = mapListResp.uuid;
            if (mapListResps.get(i).uuid.equals(uuid))
            {
                // Log.e(TAG, "有进入到小区数据请求！");
                orgUuid = uuid;
                Calendar a = Calendar.getInstance();
                int year = a.get(Calendar.YEAR);
                getkpiYear(uuid, year);
            }
        }
    }
    private void setMapStatisticalData()
    {

        // 统计数据布局

        if (mapDataResp != null)
        {
            if (mapType == 1)
            {
                tv_title.setText("全国大区分布");
            } else if (mapType == 2)
            {
                tv_title.setText("全国大区分布-" + mapListResp2.name);
            } else if (mapType == 3)
            {
                tv_title.setText("全国大区分布-" + mapListResp2.name + "-"
                        + mapListResp3.name);
            } else if (mapType == 4)
            {
                tv_title.setText("全国大区分布-" + mapListResp2.name + "-"
                        + mapListResp3.name + "-" + mapListResp4.name);
            }

            // 上线面积
            String floorArea = changeFormat(mapDataResp.floorArea);
            tv_area.setText(floorArea);
            // 应收
            String normalFee = changeFormat(mapDataResp.normalFee);
            tv_receivable.setText(normalFee);
            // 实收
            String receivedFee = changeFormat(mapDataResp.receivedFee);
            tv_official_receipts.setText(receivedFee);
            // 收缴率
            tv_collection_rate.setText(mapDataResp.feeRate);
            // 滿意度
            tv_satisfaction.setText(mapDataResp.satisfaction);
            // APP安装数
            String AppCount = changeFormat(mapDataResp.appCount);
            tv_appcount.setText(AppCount);
            // 业主投诉数
            tv_complaint.setText(mapDataResp.complainCount);
            // 停车位
            String PakingCount = changeFormat(mapDataResp.parkingCount);
            tv_parkingCount.setText(PakingCount);
            //小区数
            String communityCount = mapDataResp.communityCount;
            tv_community_num.setText(communityCount);

        }
    }
    
    private void map_water()
    {
        isWaterLocat = !isWaterLocat;
        if (isWaterLocat)
        {
            isCommunity = true;
            tv_title.setText("附近小区");
            ima_mapdetail_water.setImageResource(R.drawable.poipeople_map);
            aMap.setLocationSource(MapDetailActivity.this);
            aMap.setMyLocationEnabled(true);
            aMap.getUiSettings().setMyLocationButtonEnabled(false);
            tv_community_num.setText("");
            if (isShowData)
            {
                linearLayout.setVisibility(View.GONE);
                mLinearlayout_poicommunity.setVisibility(View.VISIBLE);
            } else
            {
                linearLayout.setVisibility(View.GONE);
                mLinearlayout_poicommunity.setVisibility(View.GONE);
            }
        } else
        {
            isCommunity = false;
            tv_title.setText("全国地图");
            ima_mapdetail_water.setImageResource(R.drawable.map_poipeople);
            aMap.setMyLocationEnabled(false);
            mapType = 0;
            getMapList();
            getMapDatas();
            aMap.getUiSettings().setMyLocationButtonEnabled(false);
            if (isShowData)
            {
                linearLayout.setVisibility(View.VISIBLE);
                mLinearlayout_poicommunity.setVisibility(View.GONE);
            } else
            {
                linearLayout.setVisibility(View.GONE);
                mLinearlayout_poicommunity.setVisibility(View.GONE);
            }
        }
    }
    
    /**
     * 返回操作
     */
    private void btnback()
    {
        if (!clickable_maplist || !clickable_mapdata)
        {
            return;
        }
        clickable_maplist = false;
        clickable_mapdata = false;

        if (mapType == 1)
        {
            finish();
        } else if (mapType == 2)
        {
            mapType = 1;
            backBtn.setVisibility(View.INVISIBLE);
            tv_community_num.setText(mapListResp1.communityCount);
            ShowOrHideByBack();
            if (isPersonalAuth)
            {
                initOrg();
            } else
            {
                getMapList();
                getMapDatas();
            }
        } else if (mapType == 3)
        {
            mapType = 2;
            tv_community_num.setText(mapListResp2.communityCount);
            ShowOrHideByBack();
            getMapList();
            getMapDatas();
        } else if (mapType == 4)
        {
            mapType = 3;
            tv_community_num.setText(mapListResp3.communityCount);
            ShowOrHideByBack();
            getMapList();
            getMapDatas();
        } else if (mapType == 5)
        {
            mapType = 4;
            ShowOrHideByBack();
            getMapList();
            getMapDatas();
        }
    }
    
    private void ShowOrHideByBack()
    {
        isCommunity = false;
        if (isShowData)
        {
            mLinearlayout_poicommunity.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        } else
        {
            mLinearlayout_poicommunity.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
        }
    }
    
    private void ShowOrHidden()
    {
        isShowData = !isShowData;
        if (isShowData)
        {
            if (isWaterLocat || isCommunity)
            {
                mLinearlayout_poicommunity.startAnimation(slidein);
                mLinearlayout_poicommunity.setVisibility(View.VISIBLE);
            } else
            {
                linearLayout.startAnimation(slidein);
                linearLayout.setVisibility(View.VISIBLE);
            }
        } else
        {
            if (isWaterLocat || isCommunity)
            {
                mLinearlayout_poicommunity.startAnimation(slideout);
                mLinearlayout_poicommunity.setVisibility(View.GONE);
            } else
            {
                linearLayout.startAnimation(slideout);
                linearLayout.setVisibility(View.GONE);
            }
        }
    }
    
    private void JumpSeaHealth()
    {
        if (!"-1".equals(orgUuid))
        {
            // 跳转海康app
            if (FragmentManagement.isAvilible(MapDetailActivity.this,
                    Contants.APP.SeaHealthPackageName))
            {
                // 海康clientCode
                getSeaHealthAuth("hksxt", orgUuid);
            } else{
            	DialogFactory.getInstance().showDialog(MapDetailActivity.this, new OnClickListener() {
					@Override
					public void onClick(View v) {
//						String url = Contants.URl.SeaHealthApkUrl;
						String url = null;
						Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
					}
				}, null, "未安装海康客户端，是否立即下载？", null, null);
            }
        }
    }
	@Override
	public void onRequestStart(Message msg, String hintString) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSuccess(Message msg, String jsonString, String hintString) {
		int code = HttpTools.getCode(jsonString);
		String message = HttpTools.getMessageString(jsonString);
		if(msg.arg1 == HttpTools.GET_USER_INITORG){
			 mapListResps.clear();
			if(code == 0){
				JSONArray jsonArray = HttpTools.getContentJsonArray(jsonString);
				if(jsonArray != null){
					ResponseData data = HttpTools.getResponseContent(jsonArray);
					MapListResp info ;
					for (int i = 0; i < data.length; i++) {
						info= new MapListResp();
						info.country = data.getString("country");
						info.name = data.getString("name");
						info.orgType = data.getString("orgType");
						info.parentId = data.getString("parentId");
						info.status = data.getString("status");
						info.uuid = data.getString("uuid");
						info.lat = data.getString("lat");
						info.lng = data.getString("lng");
						info.communityCount = data.getString("communityCount");
						mapListResps.add(info);
					}
				}
				if(mapListResps.size() == 1){
					 MapListResp item = mapListResps.get(0);
                     init(item.name, item.uuid, item.lat, item.lng);
                     isPersonalAuth = true;
				}else if (mapListResps.size() > 1){
					init("全国", "0", "35.6972813", "104.238281");
                    isPersonalAuth = true;
				}
				clickable_maplist = true;
                
                float zoom = 4;
                if (mapType == 1)
                {
                    zoom = 4;
                } else if (mapType == 2)
                {
                    zoom = 6;
                } else if (mapType == 3)
                {
                    zoom = 8;
                } else if (mapType == 4)
                {
                    zoom = 10;
                }
                addMarkers(mapListResps, zoom);
			}else{
				init("全国", "0", "35.6972813", "104.238281");
                getMapList();
			}
			getMapDatas();
		}else if(msg.arg1 == HttpTools.GET_POI_COMMUNITY){//获取附近小区
			PoiCommunityList.clear();
			if(code == 0){
				JSONArray jsonArray = HttpTools.getContentJsonArray(jsonString);
				if(jsonArray != null){
					ResponseData data = HttpTools.getResponseContent(jsonArray);
					if(data.length > 0){
						PoiCommunity info ;
						for (int i = 0; i < data.length; i++) {
							info = new PoiCommunity();
							info.uuid = data.getString(i,"uuid");
							info.name = data.getString(i,"name");
							info.lat = data.getString(i,"lat");
							info.lng = data.getString(i,"lng");
							PoiCommunityList.add(info);
						}
					}
		            addMarkersToMap(PoiCommunityList);
				}
			}else{
				ToastFactory.showToast(MapDetailActivity.this,message);
			}
		}else if(msg.arg1 == HttpTools.GET_KPI_INFO){//获取kpi信息
			if(code == 0){
				JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
				MapDataResp info = new MapDataResp();
				try {
					info.communityCount = jsonObject.getString("communityCount");
					info.appCount = jsonObject.getString("appCount");
					info.complainCount = jsonObject.getString("complainCount");
					info.normalFee = jsonObject.getString("normalFee");
					info.receivedFee = jsonObject.getString("receivedFee");
					info.feeRate = jsonObject.getString("feeRate");
					info.satisfaction = jsonObject.getString("satisfaction");
					info.parkingCount = jsonObject.getString("parkingCount");
					info.floorArea = jsonObject.getString("floorArea");
					info.qrcode2open = jsonObject.getString("qrcode2open");
					info.parkingFee = jsonObject.getString("parkingFee");
					info.cameraStatus = jsonObject.getString("cameraStatus");
					info.guardStatus = jsonObject.getString("guardStatus");
					info.barrierStatus = jsonObject.getString("barrierStatus");
				} catch (Exception e) {
					e.printStackTrace();
				}
				setCommunityDataFromKpi(info);
			}else{
				ToastFactory.showToast(MapDetailActivity.this,message);
			}
		}else if(msg.arg1 == HttpTools.GET_MAP_LIST){//获取地图列表
			 mapListResps.clear();
			if(code == 0){
				JSONArray jsonArray = HttpTools.getContentJsonArray(jsonString);
				if(jsonArray != null){
					ResponseData data = HttpTools.getResponseContent(jsonArray);
					MapListResp info ;
					for (int i = 0; i < data.length; i++) {
						info = new MapListResp();
						info.country = data.getString(i, "country");
						info.name = data.getString(i, "name");
						info.orgType = data.getString(i, "orgType");
						info.parentId = data.getString(i, "parentId");
						info.status = data.getString(i, "status");
						info.uuid = data.getString(i, "uuid");
						info.lat = data.getString(i, "lat");
						info.lng = data.getString(i, "lng");
						info.communityCount = data.getString(i, "communityCount");
						mapListResps.add(info);
					}
				}
				
				clickable_maplist = true;
                    float zoom = 4;
                    if (mapType == 1)
                    {
                        zoom = 4;
                    } else if (mapType == 2)
                    {
                        zoom = 6;
                    } else if (mapType == 3)
                    {
                        zoom = 8;
                    } else if (mapType == 4)
                    {
                        zoom = 10;
                    }
                    addMarkers(mapListResps, zoom);
			}else{
				ToastFactory.showToast(MapDetailActivity.this,message);
			}
		}else if(msg.arg1 == HttpTools.GET_KPI_YEAR){
			if(code == 0){
				clickable_maplist = true;
                clickable_mapdata = true;
                JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
				MapDataResp info = new MapDataResp();
				try {
					info.communityCount = jsonObject.getString("communityCount");
					info.appCount = jsonObject.getString("appCount");
					info.complainCount = jsonObject.getString("complainCount");
					info.normalFee = jsonObject.getString("normalFee");
					info.receivedFee = jsonObject.getString("receivedFee");
					info.feeRate = jsonObject.getString("feeRate");
					info.satisfaction = jsonObject.getString("satisfaction");
					info.parkingCount = jsonObject.getString("parkingCount");
					info.floorArea = jsonObject.getString("floorArea");
					info.qrcode2open = jsonObject.getString("qrcode2open");
					info.parkingFee = jsonObject.getString("parkingFee");
					info.cameraStatus = jsonObject.getString("cameraStatus");
					info.guardStatus = jsonObject.getString("guardStatus");
					info.barrierStatus = jsonObject.getString("barrierStatus");
				} catch (Exception e) {
					e.printStackTrace();
				}
                setCommunityDataFromKpi(info);
			}else{
				ToastFactory.showToast(MapDetailActivity.this,message);
			}
		}else if(msg.arg1 == HttpTools.GET_KPI_YEAR_INFO){
			if(code == 0){
				clickable_mapdata = true;
                JSONObject jsonObject = HttpTools.getContentJSONObject(jsonString);
				MapDataResp info = new MapDataResp();
				try {
					info.communityCount = jsonObject.getString("communityCount");
					info.appCount = jsonObject.getString("appCount");
					info.complainCount = jsonObject.getString("complainCount");
					info.normalFee = jsonObject.getString("normalFee");
					info.receivedFee = jsonObject.getString("receivedFee");
					info.feeRate = jsonObject.getString("feeRate");
					info.satisfaction = jsonObject.getString("satisfaction");
					info.parkingCount = jsonObject.getString("parkingCount");
					info.floorArea = jsonObject.getString("floorArea");
					info.qrcode2open = jsonObject.getString("qrcode2open");
					info.parkingFee = jsonObject.getString("parkingFee");
					info.cameraStatus = jsonObject.getString("cameraStatus");
					info.guardStatus = jsonObject.getString("guardStatus");
					info.barrierStatus = jsonObject.getString("barrierStatus");
				} catch (Exception e) {
					e.printStackTrace();
				}
				mapDataResp = info;
			}else{
				ToastFactory.showToast(MapDetailActivity.this,message);
			}
              setMapStatisticalData();
		}else if(msg.arg1 == HttpTools.GET_SEA_HEALTH){//获取海康api
			if(code == 0){
				JSONObject content = HttpTools.getContentJSONObject(jsonString);
				if(content != null){
				try {
					String token = content.getString("token");
					PackageManager packageManager = MapDetailActivity.this.getPackageManager();
	                Intent intent = packageManager .getLaunchIntentForPackage(Contants.APP.SeaHealthPackageName);
	                ComponentName componetName = new ComponentName(Contants.APP.SeaHealthPackageName,
	                        "com.hikvi.ivms8700.resource.AddDeviceActivity");
	                Bundle bundle = new Bundle();
	                intent.putExtra("token", token);
	                intent.putExtras(bundle);
	                intent.setComponent(componetName);
	                startActivity(intent);
				} catch (JSONException e) {
					e.printStackTrace();
					}
				}
			}else{
				ToastFactory.showToast(MapDetailActivity.this,message);
			}
		}
	}
	
	@Override
	public void onFail(Message msg, String hintString) {
		// TODO Auto-generated method stub
		
	}
	
    @Override
	public void onClick(View v) {
    	switch (v.getId())
        {
            case R.id.btnBack:
               //btnback();
            	getSeaHealthAuth("hksxt", orgUuid);
                break;
            case R.id.btnClose:
                finish();
                break;
            case R.id.ima_map_email:
            	getAuth("邮件", Contants.URl.Yj_Url, "yj");
                break;
            case R.id.ima_map_shenpi:
            	 getAuth("审批", Contants.URl.Sp_Url, "sp");
                break;
            case R.id.ima_map_showorhidden://左侧状态栏隐藏操作
               ShowOrHidden();
                break;
            case R.id.ima_mapdetail_water://定位
                map_water();
                break;
            case R.id.tv_seahealth_onlineinfo:
                JumpSeaHealth();
                break;
        }
	}
    /**
     * 
     */
	@Override
	public void onMapClick(LatLng arg0) {
		 if (markers != null)
	        {
	            for (Marker marker : markers)
	            {
	                if (marker.isInfoWindowShown())
	                {
	                    marker.hideInfoWindow();
	                }
	            }
	        }
	}
	@Override
	public void onMarkerDrag(Marker arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMarkerDragEnd(Marker arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMarkerDragStart(Marker arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onMapLoaded() {
		if (latLngs.size() > 0)
        {
            LatLngBounds bounds = null;
            Builder builder = new LatLngBounds.Builder();

            for (LatLng latLng : latLngs)
            {

                builder.include(latLng).build();

            }
            bounds = builder.build();
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 12));

        }
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if (!clickable_maplist || !clickable_mapdata)
        {
            return true;
        }
        clickable_maplist = false;
        clickable_mapdata = false;
        marker.hideInfoWindow();
        if (!isWaterLocat)
        {
            Gson gson = new Gson();
            String snippet = marker.getSnippet();
            SpannableString snippetText = new SpannableString(snippet);
            Type entityType = new TypeToken<MapListResp>() {
            }.getType();
            MapListResp mapListResp = gson.fromJson(snippetText.toString(),
                    entityType);
            tv_community_num.setText(mapListResp.communityCount);
            if (mapType == 1)
            {
                backBtn.setVisibility(View.VISIBLE);
                mapListResp2.uuid = mapListResp.uuid;
                mapListResp2.name = mapListResp.name;
                mapListResp2.lat = mapListResp.lat;
                mapListResp2.lng = mapListResp.lng;
                mapListResp2.communityCount = mapListResp.communityCount;
                if ("小区".equals(mapListResp.orgType))
                {
                    isCommunity = true;
                    mapType = 1;
                    getCommunityData(mapListResp);
                } else
                {
                    isCommunity = false;
                    mapType = 2;
                    getMapList();
                    getMapDatas();
                }
            } else if (mapType == 2)
            {

                mapListResp3.uuid = mapListResp.uuid;
                mapListResp3.name = mapListResp.name;
                mapListResp3.lat = mapListResp.lat;
                mapListResp3.lng = mapListResp.lng;
                mapListResp3.communityCount = mapListResp.communityCount;
                if ("小区".equals(mapListResp.orgType))
                {
                    mapType = 2;
                    isCommunity = true;
                    getCommunityData(mapListResp);
                } else
                {
                    mapType = 3;
                    isCommunity = false;
                    getMapList();
                    getMapDatas();
                }
            } else if (mapType == 3)
            {
            	 mapListResp4.uuid = mapListResp.uuid;
            	 mapListResp4.name = mapListResp.name;
            	 mapListResp4.lat = mapListResp.lat;
            	 mapListResp4.lng = mapListResp.lng;
            	 mapListResp4.communityCount = mapListResp.communityCount;
                if ("小区".equals(mapListResp.orgType))
                {
                    mapType = 3;
                    isCommunity = true;
                    getCommunityData(mapListResp);
                } else
                {
                    mapType = 4;
                    isCommunity = false;
                    getMapList();
                    getMapDatas();
                }
            } else if (mapType == 4)
            {
                isCommunity = true;
                mapType = 5;
                getCommunityData(mapListResp);
            } else if (mapType == 5)
            {
                isCommunity = true;
                getCommunityData(mapListResp);
            }
        } else if (isWaterLocat)
        {
            for (int i = 0; i < PoiCommunityList.size(); i++)
            {
                PoiCommunity poiCommunity = PoiCommunityList.get(i);
                String id = poiCommunity.uuid;
                if (marker.getSnippet().equals(id))
                {
                    setCommunityData(i);
                    orgUuid = poiCommunity.uuid;
                    getkpi(orgUuid);
                }
            }
        }
		return true;
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (mMapView != null)
        {
            mMapView.onSaveInstanceState(outState);
        }
    }
	@Override
	protected void onPause() {
		if (mMapView != null)
        {
            mMapView.onPause();
        }
        deactivate();
        super.onPause();
	}
	@Override
	protected void onResume() {
		if (mMapView != null)
        {
            mMapView.onResume();
        }
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		if (mMapView != null)
        {
            mMapView.onDestroy();
        }
        deactivate();
        super.onDestroy();
	}
	
	 @Override
	 public void deactivate()
	    {
	        mListener = null;
	        if (mlocationClient != null)
	        {
	            mlocationClient.stopLocation();
	            mlocationClient.onDestroy();
	        }
	        mlocationClient = null;
	    }
	@Override
	public void activate(OnLocationChangedListener listener) {
		 mListener = listener;
	     initLocation();
	}
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null)
        {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0)
            {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                double latitude = amapLocation.getLatitude();
                double longitude = amapLocation.getLongitude();
                String Str_latitude = String.valueOf(latitude);
                String Str_longitude = String.valueOf(longitude);
                aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                getPoiCommunity(Str_latitude, Str_longitude);
            } else
            {
                Log.e("AmapErr", "Location ERR:"
                        + amapLocation.getErrorCode());
            }
        }
	}
	/**
	 * 初始化定位
	 */
	private void initLocation()
    {
        if (mlocationClient == null)
        {
            //初始化client
            mlocationClient = new AMapLocationClient(this.getApplicationContext());
            //设置定位参数
            mlocationClient.setLocationOption(getDefaultOption());
            // 设置定位监听
            mlocationClient.setLocationListener(this);

            mlocationClient.startLocation();

        }
    }
	
	/**
	 * 设置定位参数
	 * @return
	 */
	private AMapLocationClientOption getDefaultOption()
    {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是ture
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        return mOption;
    }
	 // 转化数据格式
    public String changeFormat(String str)
    {

        DecimalFormat myformat = new DecimalFormat("###,###");
        String format = "";
        try
        {
            Double DB_str = Double.valueOf(str);
            format = myformat.format(DB_str);
        } catch (Exception e)
        {
            return str;
        }
        return format;
    }
    public View getCustomView(String title, String id)
    {
        View view = getLayoutInflater().inflate(R.layout.custom_info_window,
                null);
        TextView tv_title = (TextView) view.findViewById(R.id.title);
        TextView tv_snippet = (TextView) view.findViewById(R.id.snippet);
        tv_title.setText(title);
        tv_snippet.setText(id);
        return view;
    }
    /**
     * 海康
     */
    private void getSeaHealthAuth(String clientCode, final String orgUuid){
    	if (homeService == null) {
			homeService = new HomeService(MapDetailActivity.this);
		}
		homeService.getAuth(clientCode,new GetTwoRecordListener<String, String>() {
			
			@Override
			public void onFinish(String openID, String accessToken,String data3) {//获取海康Api
				RequestConfig config = new RequestConfig(MapDetailActivity.this, HttpTools.GET_SEA_HEALTH);
				config.handler = msgHandler.getHandler();
				RequestParams params = new RequestParams();
				params.put("openID", openID);
				params.put("accessToken",accessToken);
				params.put("orgUuid", orgUuid);
				HttpTools.httpGet(Contants.URl.URL_ICETEST, "/bigdata/kpi",config, params);
			}
			
			@Override
			public void onFailed(String Message) {
				
			}
		});
    }
    /**
	 * 用户授权
	 * @param name
	 * @param url
	 * @param clientCode
	 */
	private void getAuth(final String name, final String url, String clientCode) {
		if (homeService == null) {
			homeService = new HomeService(MapDetailActivity.this);
		}
		homeService.getAuth(clientCode,new GetTwoRecordListener<String, String>() {
			
			@Override
			public void onFinish(String openID, String accessToken,String data3) {
				Intent intent = new Intent(MapDetailActivity.this,MyBrowserActivity.class);
				intent.putExtra(MyBrowserActivity.KEY_URL, url + "?openID=" + openID+ "&accessToken=" + accessToken);
				startActivity(intent);
				finish();
			}
			
			@Override
			public void onFailed(String Message) {
				
			}
		});
	}
}
