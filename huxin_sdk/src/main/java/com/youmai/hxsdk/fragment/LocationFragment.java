package com.youmai.hxsdk.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.adapter.DividerItemDecoration;
import com.youmai.hxsdk.map.SearchAdapter;
import com.youmai.hxsdk.push.http.HttpPushManager;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiChat;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.AppUtils;
import com.youmai.hxsdk.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;


public class LocationFragment extends BaseFragment implements
        LocationSource, AMapLocationListener, OnCameraChangeListener,
        OnMarkerClickListener, OnGeocodeSearchListener {
    public final static String TAG = LocationFragment.class.getSimpleName();
    // UI
    private AMap aMap;
    private MapView mapView;

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private SearchAdapter adapter;

    private TextView btn_cancel, btn_send;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private GeocodeSearch geocoderSearch;// 坐标转物理地址
    private AMapLocation amapLocation;

    // Logic
    private Marker marker;
    private EditText ed_phone;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hx_fragment_location, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        ed_phone = (EditText) view.findViewById(R.id.ed_phone);
        ed_phone.setText(HuxinSdkManager.instance().getPhoneNum());

        btn_send = (TextView) view.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phoneNum = ed_phone.getText().toString();
                if (!(AppUtils.isMobileNum(phoneNum) || phoneNum.equals("4000"))) {
                    Toast.makeText(mAct, R.string.hx_phone_illegitmacy, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (adapter != null) {
                    PoiItem poiItem = adapter.getSelectPoiItem();
                    sendMarkerLocation(poiItem);
                }
            }
        });

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        adapter = new SearchAdapter(mAct);

        LinearLayoutManager layout = new LinearLayoutManager(mAct);
        recyclerView.setLayoutManager(layout);
        recyclerView.addItemDecoration(new DividerItemDecoration(mAct, layout.getOrientation()));


        recyclerView.setAdapter(adapter);

        mapView = (MapView) view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();

        setUpMap();
    }


    /**
     * 设置amap属性
     */
    private void setUpMap() {
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16f));
        aMap.setLocationSource(this);// 设置定位监听

        aMap.getUiSettings().setScaleControlsEnabled(true);
        aMap.getUiSettings().setZoomControlsEnabled(true);
        aMap.getUiSettings().setZoomGesturesEnabled(true);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setAllGesturesEnabled(true);
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

        // 事件监听
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器

        geocoderSearch = new GeocodeSearch(mAct);
        geocoderSearch.setOnGeocodeSearchListener(this);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        activate(mListener);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        this.amapLocation = amapLocation;

        if (marker != null) {
            return;
        }

        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(new LatLng(amapLocation.getLongitude(),
                amapLocation.getLatitude()));

        String title = amapLocation.getPoiName() + ":" + amapLocation.getAddress();
        markerOption.title(title);
        markerOption.draggable(true);
        markerOption.icon(BitmapDescriptorFactory
                .fromResource(R.drawable.hx_icon_location));
        marker = aMap.addMarker(markerOption);
        marker.showInfoWindow();

        aMap.setOnCameraChangeListener(this);// 对amap添加移动地图事件监听器


        if (mListener != null) {
            mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
        }
    }


    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(mAct);
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mlocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            // 设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }


    /**
     * 对marker标注点点击响应事件
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

        if (marker != null) {
            marker.setPosition(cameraPosition.target);
        }
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        LatLonPoint latLonPoint = new LatLonPoint(
                cameraPosition.target.latitude, cameraPosition.target.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
        //search(latLonPoint);
    }

    @Override
    public void onGeocodeSearched(GeocodeResult arg0, int arg1) {

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {

                if (marker != null) {
                    marker.setTitle(result.getRegeocodeAddress()
                            .getFormatAddress());
                    marker.showInfoWindow();
                }

                List<PoiItem> list = result.getRegeocodeAddress().getPois();
                adapter.setList(list);

                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

            }
        }
    }


    /**
     * 发送marker的位置信息
     */
    public void sendMarkerLocation(PoiItem poiItem) {
        double longitude = 0;
        double latitude = 0;
        String address = "";

        if (poiItem != null) {
            LatLonPoint point = poiItem.getLatLonPoint();
            longitude = point.getLongitude();
            latitude = point.getLatitude();
            address = poiItem.getTitle() + ":" + poiItem.getSnippet();
        } else if (marker != null) {
            longitude = marker.getPosition().longitude;// 经度
            latitude = marker.getPosition().latitude;// 纬度
            address = marker.getTitle();// 地址
        }


        final int zoomLevel = (int) aMap.getCameraPosition().zoom;

        //http://restapi.amap.com/v3/staticmap?location=113.481485,39.990464&zoom=10&size=750*300&markers=mid,,A:116.481485,39.990464&key=ee95e52bf08006f63fd29bcfbcf21df0
//        String url = "http://restapi.amap.com/v3/staticmap?location="
//                + longitude + "," + latitude + "&zoom=" + zoomLevel
//                + "&size=600*400&traffic=1&markers=mid,0xff0000,A:" + longitude
//                + "," + latitude + "&key=" + AppConfig.staticMapKey;

        final int userId = HuxinSdkManager.instance().getUserId();

        final String targetPhone = ed_phone.getText().toString();

        final double finalLongitude = longitude;
        final double finalLatitude = latitude;
        final String finalAddress = address;
        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        if (ack.getIsTargetOnline()) {
                            Toast.makeText(mAct, R.string.hx_hook_strategy_log_message_success, Toast.LENGTH_SHORT).show();
                        } else {
                            HttpPushManager.pushMsgForLocation(userId, targetPhone,
                                    finalLongitude, finalLatitude,
                                    zoomLevel, finalAddress,
                                    new HttpPushManager.PushListener() {
                                        @Override
                                        public void success(String msg) {
                                            LogUtils.w(TAG, msg);
                                            Toast.makeText(mAct, R.string.hx_hook_strategy_log_message_success, Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void fail(String msg) {
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(mAct, "ErrerNo:" + ack.getErrerNo(), Toast.LENGTH_SHORT).show();
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        };

        HuxinSdkManager.instance().sendLocation(userId, targetPhone, longitude, latitude,
                zoomLevel, address, callback);
    }

    /**
     * 搜索附近
     *
     * @param latLonPoint
     */
    private void search(LatLonPoint latLonPoint) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        String code = amapLocation.getCityCode();
        PoiSearch.Query query = new PoiSearch.Query("", getString(R.string.hx_hook_strategy_log_infomation), code);

        // keyWord表示搜索字符串，第二个参数表示POI搜索类型，默认为：生活服务、餐饮服务、商务住宅
        //共分为以下20种：汽车服务|汽车销售|
        //汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|
        //住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|
        //金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施
        //cityCode表示POI搜索区域，（这里可以传空字符串，空字符串代表全国在全国范围内进行搜索）

        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);//设置查第一页
        PoiSearch poiSearch = new PoiSearch(mAct, query);
        poiSearch.setBound(new PoiSearch.SearchBound(latLonPoint, 200));//设置周边搜索的中心点以及区域

        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int i) {
                final ArrayList<PoiItem> list = poiResult.getPois();
                adapter.setList(list);

                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        });//设置数据返回的监听器

        poiSearch.searchPOIAsyn();//开始搜索

    }

}
