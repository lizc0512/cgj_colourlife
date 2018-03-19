package com.youmai.hxsdk.module.map;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.protobuf.InvalidProtocolBufferException;
import com.youmai.hxsdk.HuxinSdkManager;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.SendSmsActivity;
import com.youmai.hxsdk.activity.IMConnectionActivity;
import com.youmai.hxsdk.config.AppConfig;
import com.youmai.hxsdk.db.bean.CacheMsgBean;
import com.youmai.hxsdk.im.IMMsgManager;
import com.youmai.hxsdk.im.cache.CacheMsgHelper;
import com.youmai.hxsdk.im.cache.CacheMsgLShare;
import com.youmai.hxsdk.im.cache.JsonFormate;
import com.youmai.hxsdk.proto.YouMaiBasic;
import com.youmai.hxsdk.proto.YouMaiChat;
import com.youmai.hxsdk.proto.YouMaiLocation;
import com.youmai.hxsdk.socket.NotifyListener;
import com.youmai.hxsdk.socket.PduBase;
import com.youmai.hxsdk.socket.ReceiveListener;
import com.youmai.hxsdk.utils.LogFile;
import com.youmai.hxsdk.utils.StringUtils;

import java.lang.ref.WeakReference;

/**
 * 实时位置共享
 */
public class ActualLocationFragment extends Fragment implements LocationSource,
        AMapLocationListener, Runnable, SensorEventListener {
    private static final String tag = ActualLocationFragment.class.getName();

    public enum LShareStatus {
        RESEND, INVITE, ANSWER, QUIT
    }

    private String mSharePhone;
    private int mLShareStatus; //位置分享状态
    private CacheMsgBean mInviteMsgBean; //INVITE状态
    private CacheMsgBean mAnswerCacheMsgBean; //ANSWER状态
    private CacheMsgBean mResendCacheMsgBean; //resend状态
    private LatLng inviteLatLng, answerLatLng; // 开始定位坐标

    private Context mContext;
    private long mTalkMsgId = -1;

    // view
    private MapView mapView;
    private ImageView ib_turnOff;
    private ImageView ib_pullOff;
    private ImageView iv_my_header;
    private ImageView iv_his_header;
    private TextView map_tv_hisname;

    // sensor
    private SensorManager sm;

    // map和定位
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    // 路线规划
    private LatLng myPosition = null;// 自己的位置坐标
    private LatLng hisPosition = null;// 对方的位置坐标

    // 覆盖物
    private Marker myMarker;
    private Marker myMarkerBearing;
    private Marker hisMarker;
    private Marker hisMarkerBearing;
    private Bitmap myBitmap;
    private Bitmap hisBitmap;

    private float bearing;// 旋转的角度
    private float zoom = 16f;

    private HooXinAlertDialog mDialog; //退出提醒

    private LShareHandler mHandler = new LShareHandler(this);
    private static AbstractStartOrQuit mStartOrStopRefreshUIListener;

    // 发起邀请 -- 主动  && 接收邀请 -- 被动
    public static void setOnStartRefreshUIListener(AbstractStartOrQuit listener) {
        mStartOrStopRefreshUIListener = listener;
    }

    class LShareHandler extends Handler {
        WeakReference<ActualLocationFragment> weakReference;

        public LShareHandler(ActualLocationFragment activity) {
            weakReference = new WeakReference<>(activity);
        }

        public WeakReference<ActualLocationFragment> getWeakReference() {
            return weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            ActualLocationFragment activity = weakReference.get();
            if (activity == null
                    || activity.getActivity() == null
                    || (activity.getActivity()).isFinishing()) {
                return;
            }
            if (msg.what == 0) { //别人头像
                activity.refreshHisHeader(activity.getContext());
            } else if (msg.what == 1) { //刷新对方的位置marker
                LatLng latLng = (LatLng) msg.obj;
                activity.addHisMarkers(latLng);
            } else if (msg.what == 2) { // 退出位置共享
                activity.exit();
            }
        }
    }

    private void loadMyHeader() {
        int width = (int) getResources().getDimension(R.dimen.hx_location_share_header);
        map_tv_hisname.setText("1人在共享位置");
        Glide.with(mContext)
                .asBitmap()
                .load(AppConfig.getThumbHeaderUrl(mContext, width, width, HuxinSdkManager.instance().getPhoneNum()))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).circleCrop().error(R.drawable.hx_voip_header_normal))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        myBitmap = resource;
                        iv_my_header.setImageBitmap(resource);
                    }
                });
    }

    private void refreshHisHeader(Context context) {
        int width = (int) context.getResources().getDimension(R.dimen.hx_location_share_header);
        Glide.with(context)
                .asBitmap()
                .load(AppConfig.getThumbHeaderUrl(context, width, width, mSharePhone))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).circleCrop().error(R.drawable.hx_voip_header_normal))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        iv_his_header.setImageBitmap(resource);
                        hisBitmap = resource;
                    }
                });
    }

    private NotifyListener mNotifyContinueListener = new NotifyListener(
            YouMaiBasic.COMMANDID.LOCATIONSHARE_NOTIFY_VALUE) {
        @Override
        public void OnRec(byte[] data) {
            try {
                YouMaiLocation.LocationShare_Notify notify = YouMaiLocation.LocationShare_Notify.parseFrom(data);
                YouMaiLocation.LocationShare locationShare = notify.getLocationShare();

                String latitude = locationShare.getLatitude();
                String longitude = locationShare.getLongitude();
                String direction = locationShare.getAngle();

                LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                Message message = mHandler.obtainMessage();
                message.what = 1;
                message.obj = latLng;
                mHandler.sendMessage(message);

                if (hisMarkerBearing != null && !StringUtils.isEmpty(direction)) {
                    hisMarkerBearing.setRotateAngle(Float.parseFloat(direction));
                }

                //Toast.makeText(mContext, "持续定位: " + latitude + "\t" + longitude + "\t", Toast.LENGTH_SHORT).show();
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    };

    public static ActualLocationFragment newInstance() {
        ActualLocationFragment fragment = new ActualLocationFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hx_location_actual, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getContext();

        initData();
        initView(view, savedInstanceState);

        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }

        // 传感器管理器
        sm = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        setListener();

        HuxinSdkManager.instance().setNotifyListener(mNotifyContinueListener);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        if (mHandler != null)
            mHandler.removeCallbacks(this);
        super.onDestroy();

        Log.e(tag, "onDestoty-->-实时位置共享结束！" + mTalkMsgId);
        mapView.onDestroy();
        deactivate();
        if (aMap != null) {
            aMap = null;
        }
        releaseLatLng();
        releaseMarker();

        HuxinSdkManager.instance().clearNotifyListener(mNotifyContinueListener);
    }

    private void exit() {
        ActualLocation.onEnd(0);
        endActualLocationShared();
        //((IMConnectionActivity)getActivity()).removeFragment();
        //((IMConnectionActivity)getActivity()).hideLSView();
    }

    private void sendExitMessage() {
        Message message = mHandler.obtainMessage();
        message.what = 2;
        mHandler.sendMessage(message);
    }

    private void initData() {
        mLShareStatus = ActualLocation.getStatus();
        mSharePhone = ActualLocation.getLSharePhone();
        mInviteMsgBean = ActualLocation.getInviteCacheMsgBean();
        mAnswerCacheMsgBean = ActualLocation.getAnswerCacheMsgBean();
        mResendCacheMsgBean = ActualLocation.getResendCacheMsgBean();
        if (mAnswerCacheMsgBean != null) {
            ActualLocation.setTargetId(mAnswerCacheMsgBean.getSenderUserId());
        }
        if (mResendCacheMsgBean != null) {
            ActualLocation.setTargetId(mResendCacheMsgBean.getSenderUserId());
        }
    }

    private void initView(View view, Bundle savedInstanceState) {

        mapView = (MapView) view.findViewById(R.id.share_mapView);
        ib_turnOff = (ImageView) view.findViewById(R.id.ib_turnOff);
        ib_pullOff = (ImageView) view.findViewById(R.id.ib_pullOff);

        iv_my_header = (ImageView) view.findViewById(R.id.iv_my_header);
        iv_his_header = (ImageView) view.findViewById(R.id.iv_his_header);
        map_tv_hisname = (TextView) view.findViewById(R.id.share_count);

        loadMyHeader();
        mapView.onCreate(savedInstanceState);// 此方法必须重写
    }

    private void setListener() {

        // 注册传感器(Sensor.TYPE_ORIENTATION(方向传感器);SENSOR_DELAY_FASTEST(0毫秒延迟);
        // SENSOR_DELAY_GAME(20,000毫秒延迟)、SENSOR_DELAY_UI(60,000毫秒延迟))
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);

        if (aMap != null && myMarker != null && myMarker.getPosition() != null) {
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myMarker.getPosition(), zoom));
        }

        // 事件监听
        ib_turnOff.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initDialog(mContext);
            }
        });
        ib_pullOff.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IMConnectionActivity) getActivity()).hideFragment();
            }
        });
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {

        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        //myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));

        //MyLocationStyle myLocationStyle = new MyLocationStyle();
        //myLocationStyle.myLocationIcon(BitmapDescriptorFactory
        //.fromResource(R.drawable.icon_location_01));// 设置小蓝点的图标
        //myLocationStyle.strokeColor(Color.TRANSPARENT);// 设置圆形的边框颜色
        //myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));
        //设置圆形的填充颜色
        // myLocationStyle.anchor(0,0);//设置小蓝点的锚点,0,0为中心。
        //myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        //aMap.setMyLocationStyle(myLocationStyle);

        aMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
        aMap.setLocationSource(this);// 设置定位监听

        aMap.getUiSettings().setScaleControlsEnabled(true);
        aMap.getUiSettings().setZoomControlsEnabled(false); //缩放的图标
        aMap.getUiSettings().setZoomGesturesEnabled(true);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setAllGesturesEnabled(true);
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(mContext);
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mlocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            // 设置定位参数
            mLocationOption.setInterval(3000);// 3秒钟一次定位
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
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {

                if (inviteLatLng == null && mLShareStatus == LShareStatus.INVITE.ordinal()) {
                    inviteLatLng = new LatLng(amapLocation.getLatitude(),
                            amapLocation.getLongitude());
                    sendInviteLocation();
                }

                if (inviteLatLng == null && mLShareStatus == LShareStatus.RESEND.ordinal()) {
                    inviteLatLng = new LatLng(amapLocation.getLatitude(),
                            amapLocation.getLongitude());
                    resendInviteLocation();
                }

                if (answerLatLng == null && mLShareStatus == LShareStatus.ANSWER.ordinal()) {
                    answerLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                    sendAnswerLocation();
                }

                myPosition = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());

                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点

                addMyMarkers();

                if (myMarkerBearing == null) {
                    return;
                }

                LatLng newLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());// 新坐标

                if (myMarker != null) {
                    myMarker.setPosition(newLatLng);
                }

                if (myMarkerBearing != null) {
                    myMarkerBearing.setPosition(newLatLng);
                }

            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                //Log.e("AmapErr", errText);
                Toast.makeText(mContext, amapLocation.getErrorInfo(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addMyMarkers() {
        if (notNull() && !isAdded()) {
            return;
        }
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(new LatLng(myPosition.latitude, myPosition.longitude));
        markerOption.draggable(true);
        markerOption.zIndex(0);

        if (myMarkerBearing == null) {
            markerOption.icon(BitmapDescriptorFactory
                    .fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.hx_location_share_marker)));
            myMarkerBearing = aMap.addMarker(markerOption);
            myMarkerBearing.setAnchor(0.5f, 0.33f);
        }

        if (myMarker == null && myBitmap != null) {
            //myBitmap = AbImageUtil.getScaleBitmap(myBitmap, 70, 70);
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(myBitmap));// 设置头像，这里需要动态修改
            myMarker = aMap.addMarker(markerOption);
            myMarker.setAnchor(0.5f, 1.3f);
        }
    }

    private void addHisMarkers(LatLng latLng) {
        if (notNull() && !isAdded()) {
            return;
        }
        MarkerOptions markerOption2 = new MarkerOptions();
        markerOption2.position(latLng);
        markerOption2.draggable(true);
        markerOption2.zIndex(0);

        try {
            if (hisMarkerBearing == null) {
                markerOption2.icon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.hx_location_share_marker)));
                hisMarkerBearing = aMap.addMarker(markerOption2);
                hisMarkerBearing.setAnchor(0.5f, 0.33f);
            }

            if (hisMarker == null && hisBitmap != null) {
                //hisBitmap = AbImageUtil.getScaleBitmap(hisBitmap, 50, 50);
                markerOption2.icon(BitmapDescriptorFactory.fromBitmap(hisBitmap));// 设置头像，这里需要动态修改
                hisMarker = aMap.addMarker(markerOption2);
                hisMarker.setAnchor(0.5f, 1.3f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (!ActualLocation.isRunning()) {
            Log.e("ActualLocation", "run::endActuallLocationShared");
            // 清理队列中消息
            if (mHandler != null)
                mHandler.removeCallbacks(this);
            //endActualLocationShared();
            return;
        }

        if (myPosition != null) {
            //持续定位
            HuxinSdkManager.instance().continueLocation(ActualLocation.getTargetId(), mSharePhone,
                    myPosition.longitude + "",
                    myPosition.latitude + "",
                    bearing + "");
        }

        if (mHandler != null) {
            mHandler.postDelayed(this, 4000);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            bearing = -event.values[0] - 180;
            // currentDegree = -degree;
            if (myMarkerBearing != null) {
                myMarkerBearing.setRotateAngle(bearing);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //---------------------------------------------------------------------

    /**
     * 位置共享
     */
    public void sendInviteLocation() {
        double longitude = inviteLatLng.longitude;
        double latitude = inviteLatLng.latitude;
        final int userId = HuxinSdkManager.instance().getUserId();

        final CacheMsgBean cacheMsgBean = mInviteMsgBean;
        cacheMsgBean.setJsonBodyObj(new CacheMsgLShare()
                .setTargetId(System.currentTimeMillis())
                .setReceivePhone(mSharePhone)
                .setLongitude(longitude + "")
                .setLatitude(latitude + ""))
                .setRightUI(true);

        //add to db
        CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);

        IMMsgManager.getInstance().setOnAnswerOrRejectListener(new AnswerOrReject() {
            // 应答
            @Override
            public void onAnswerOrReject(boolean answerOrReject, String location, int targetUserId) {
                ActualLocation.setTargetId(targetUserId);
                cacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_LOCATION_SHARE)
                        .setSend_flag(0)
                        .setJsonBodyObj(new CacheMsgLShare()
                                .setAnswerOrReject(true)
                                .setReceiveLocation(location));
                ActualLocation.setInviteCacheMsgBean(cacheMsgBean);
                CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);

                String[] split = location.split(",");
                hisPosition = new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
                addHisMarkers(hisPosition);

                //UI
                map_tv_hisname.setText("2人在共享位置");
                iv_his_header.setVisibility(View.VISIBLE);
                mHandler.sendEmptyMessage(0);

                mHandler.post(ActualLocationFragment.this);

            }

            // 结束
            @Override
            public void onQuit() {
                map_tv_hisname.setText("1人在共享位置");
                iv_his_header.setVisibility(View.GONE);

                //exit();
                sendExitMessage();
            }
        });

        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                    long msgId = ack.getMsgId();
                    ActualLocation.onStart(msgId, mSharePhone);
                    final CacheMsgBean newMsgBean;
                    if (mSharePhone.equals(HuxinSdkManager.instance().getPhoneNum())) {
                        newMsgBean = HuxinSdkManager.instance().getCacheMsgFromDBById(cacheMsgBean.getId());
                    } else {
                        newMsgBean = cacheMsgBean;
                    }

                    newMsgBean.setMsgId(msgId);

                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        if (ack.getIsTargetOnline()) {
                            /*newMsgBean.setSend_flag(0);

                            CacheMsgHelper.instance(mContext).insertOrUpdate(newMsgBean);
                            if (mStartOrStopRefreshUIListener != null) {
                                mStartOrStopRefreshUIListener.onRefreshUi(newMsgBean);
                            }*/
                        } else {
                            //推送
                        }

                        newMsgBean.setSend_flag(0);
                        CacheMsgHelper.instance(mContext).insertOrUpdate(newMsgBean);
                        ActualLocation.setInviteCacheMsgBean(newMsgBean);
                        if (mStartOrStopRefreshUIListener != null) {
                            mStartOrStopRefreshUIListener.onRefreshUi(newMsgBean);
                        }
                    } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_NOT_HUXIN_USER) {
                        HuxinSdkManager.instance().showNotHuxinUser2(mSharePhone, SendSmsActivity.SEND_LOCATION, msgId, newMsgBean);
                    } else {
                        String log = "ErrerNo:" + ack.getErrerNo();
                        LogFile.inStance().toFile(log);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int errCode) {
                super.onError(errCode);
                cacheMsgBean.setSend_flag(4);
                ActualLocation.setInviteCacheMsgBean(cacheMsgBean);
                //add to db
                CacheMsgHelper.instance(mContext).insertOrUpdate(cacheMsgBean);
            }
        };

        HuxinSdkManager.instance().beginLocation(
                userId,
                mSharePhone,
                String.valueOf(inviteLatLng.longitude),
                String.valueOf(inviteLatLng.latitude),
                callback);
    }

    /**
     * 位置共享重发
     */
    private void resendInviteLocation() {
        double longitude = inviteLatLng.longitude;
        double latitude = inviteLatLng.latitude;

        IMMsgManager.getInstance().setOnAnswerOrRejectListener(new AnswerOrReject() {
            @Override
            public void onAnswerOrReject(boolean answerOrReject, String location, int targetUserId) {
                ActualLocation.setTargetId(targetUserId);
                mResendCacheMsgBean.setMsgType(CacheMsgBean.MSG_TYPE_LOCATION_SHARE)
                        .setJsonBodyObj(new CacheMsgLShare()
                                .setAnswerOrReject(true)
                                .setReceiveLocation(location));
                CacheMsgHelper.instance(mContext).insertOrUpdate(mResendCacheMsgBean);

                String[] split = location.split(",");
                hisPosition = new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
                addHisMarkers(hisPosition);

                //UI
                map_tv_hisname.setText("2人在共享位置");
                iv_his_header.setVisibility(View.VISIBLE);
                mHandler.sendEmptyMessage(0);

                mHandler.post(ActualLocationFragment.this);
            }

            @Override
            public void onQuit() {
                map_tv_hisname.setText("1人在共享位置");
                iv_his_header.setVisibility(View.GONE);

                //exit();
                sendExitMessage();
            }
        });

        ReceiveListener callback = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                    long msgId = ack.getMsgId();
                    ActualLocation.onStart(msgId, mSharePhone);
                    final CacheMsgBean newMsgBean;
                    if (mSharePhone.equals(HuxinSdkManager.instance().getPhoneNum())) {
                        newMsgBean = HuxinSdkManager.instance().getCacheMsgFromDBById(mResendCacheMsgBean.getId());
                    } else {
                        newMsgBean = mResendCacheMsgBean;
                    }

                    newMsgBean.setMsgId(msgId);

                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {

                        if (ack.getIsTargetOnline()) {
                            //newMsgBean.setSend_flag(0);
                            //add to db
                            //CacheMsgHelper.instance(mContext).insertOrUpdate(newMsgBean);
                        } else {
                            //推送
                        }

                        newMsgBean.setSend_flag(0);
                        CacheMsgHelper.instance(mContext).insertOrUpdate(newMsgBean);
                        ActualLocation.setInviteCacheMsgBean(newMsgBean);
                        if (mStartOrStopRefreshUIListener != null) {
                            mStartOrStopRefreshUIListener.onRefreshUi(newMsgBean);
                        }
                    } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_NOT_HUXIN_USER) {
                        HuxinSdkManager.instance().showNotHuxinUser2(mSharePhone, SendSmsActivity.SEND_LOCATION, msgId, newMsgBean);
                    } else {
                        String log = "ErrerNo:" + ack.getErrerNo();
                        LogFile.inStance().toFile(log);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int errCode) {
                super.onError(errCode);

                mResendCacheMsgBean.setSend_flag(4);
                //add to db
                CacheMsgHelper.instance(mContext).insertOrUpdate(mResendCacheMsgBean);
            }
        };

        HuxinSdkManager.instance().beginLocation(
                HuxinSdkManager.instance().getUserId(),
                mSharePhone,
                String.valueOf(longitude),
                String.valueOf(latitude),
                callback);
    }

    /**
     * 主动应答位置共享
     */
    private void sendAnswerLocation() {
        final double longitude = answerLatLng.longitude;
        final double latitude = answerLatLng.latitude;
        final int userId = HuxinSdkManager.instance().getUserId();

        map_tv_hisname.setText("2人在共享位置");
        iv_his_header.setVisibility(View.VISIBLE);
        mHandler.sendEmptyMessage(0);

        ActualLocation.onStart(ActualLocation.getMsgId(), mSharePhone);
        mHandler.post(ActualLocationFragment.this);

        IMMsgManager.getInstance().setOnAnswerOrRejectListener(new AnswerOrReject() {
            @Override
            public void onQuit() {
                map_tv_hisname.setText("1人在共享位置");
                iv_his_header.setVisibility(View.GONE);

                JsonFormate jsonBodyObj = mAnswerCacheMsgBean.getJsonBodyObj();
                mAnswerCacheMsgBean.setJsonBodyObj(((CacheMsgLShare) jsonBodyObj).setEndOver(true));

                ActualLocation.setAnswerCacheMsgBean(mAnswerCacheMsgBean);
                CacheMsgHelper.instance(mContext).insertOrUpdate(mAnswerCacheMsgBean);

                if (mStartOrStopRefreshUIListener != null) {
                    mStartOrStopRefreshUIListener.onQuit(mAnswerCacheMsgBean);
                }

                //exit();
                sendExitMessage();
                tipDialogDismiss();
            }
        });

        ReceiveListener receiveListener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                try {
                    YouMaiChat.IMChat_Personal_Ack ack = YouMaiChat.IMChat_Personal_Ack.parseFrom(pduBase.body);
                    long msgId = ack.getMsgId();
                    mAnswerCacheMsgBean.setMsgId(msgId);

                    if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_OK) {
                        if (ack.getIsTargetOnline()) {
                            //todo: 成功
                            mAnswerCacheMsgBean.setJsonBodyObj(new CacheMsgLShare()
                                    .setReceiveLocation(longitude + "," + latitude)
                                    .setAnswerOrReject(true));

                            ActualLocation.setAnswerCacheMsgBean(mAnswerCacheMsgBean);
                            CacheMsgHelper.instance(mContext).insertOrUpdate(mAnswerCacheMsgBean);
                            //Toast.makeText(mContext, "应答成功", Toast.LENGTH_SHORT).show();

                        } else {
                            //todo: 推送
                            //Toast.makeText(mContext, "推送", Toast.LENGTH_SHORT).show();
                        }
                    } else if (ack.getErrerNo() == YouMaiBasic.ERRNO_CODE.ERRNO_CODE_NOT_HUXIN_USER) {
                        //todo: 非呼信用户
                    } else {
                        //todo:其它状态
                    }
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
            }
        };

        HuxinSdkManager.instance().answerLocation(userId, mSharePhone, latitude + "," + longitude, true, receiveListener);

    }

    /**
     * 结束位置共享
     */
    public void endActualLocationShared() {

        int userId = HuxinSdkManager.instance().getUserId();
        ReceiveListener receiveListener = new ReceiveListener() {
            @Override
            public void OnRec(PduBase pduBase) {
                Toast.makeText(mContext, "位置共享结束", Toast.LENGTH_SHORT).show();
                endLShared();
            }

            @Override
            public void onError(int errCode) {
                endLShared();
            }
        };
        HuxinSdkManager.instance().endLocation(userId, mSharePhone, receiveListener);
    }

    /**
     * 结束位置共享 -- fragment destroy
     */
    private void endLShared() {
        mLShareStatus = ActualLocation.getStatus();
        if (mLShareStatus == LShareStatus.INVITE.ordinal()) {
            JsonFormate jsonBodyObj = mInviteMsgBean.getJsonBodyObj();
            mInviteMsgBean.setSend_flag(0)
                    .setJsonBodyObj(((CacheMsgLShare) jsonBodyObj).setEndOver(true));
            CacheMsgHelper.instance(mContext).insertOrUpdate(mInviteMsgBean);
            if (mStartOrStopRefreshUIListener != null) {
                mStartOrStopRefreshUIListener.onQuit(mInviteMsgBean);
            }
        } else if (mLShareStatus == LShareStatus.RESEND.ordinal()) {
            JsonFormate jsonBodyObj = mResendCacheMsgBean.getJsonBodyObj();
            mResendCacheMsgBean.setSend_flag(0)
                    .setJsonBodyObj(((CacheMsgLShare) jsonBodyObj).setEndOver(true));
            CacheMsgHelper.instance(mContext).insertOrUpdate(mResendCacheMsgBean);
            if (mStartOrStopRefreshUIListener != null) {
                mStartOrStopRefreshUIListener.onQuit(mResendCacheMsgBean);
            }
        } else if (mLShareStatus == LShareStatus.ANSWER.ordinal()) {
            JsonFormate jsonBodyObj = mAnswerCacheMsgBean.getJsonBodyObj();
            mAnswerCacheMsgBean.setSend_flag(0).setJsonBodyObj(((CacheMsgLShare) jsonBodyObj).setEndOver(true));
            CacheMsgHelper.instance(mContext).insertOrUpdate(mAnswerCacheMsgBean);
            if (mStartOrStopRefreshUIListener != null) {
                mStartOrStopRefreshUIListener.onQuit(mAnswerCacheMsgBean);
            }
        }
        tipDialogDismiss();
    }

    /**
     * dialog
     */
    public void initDialog(Context context) {
        mDialog = new HooXinAlertDialog(context);
        mDialog.setCancelableDialog(true).setTitle("共享提示")
                .setMessage("结束当前位置共享?")
                .setLeftButtonText("退出")
                .setRightButtonText("再想想")
                .setLeftButtonClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //exit();
                        sendExitMessage();
                        dialog.dismiss();
                    }
                })
                .setRightButtonClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    /**
     * 退出提示框
     */
    private void tipDialogDismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    /**
     * 判断Fragment 是否销毁
     *
     * @return
     */
    private boolean notNull() {
        return mHandler == null
                || mHandler.getWeakReference() == null
                || mHandler.getWeakReference().get() == null
                || mHandler.getWeakReference().get().isDetached();
    }

    /**
     * 释放坐标
     */
    public void releaseLatLng() {
        inviteLatLng = null;
        answerLatLng = null;
        //上次定位信息
        //ActualLocation.setInviteCacheMsgBean(null);
        //ActualLocation.setAnswerCacheMsgBean(null);
        //ActualLocation.setResendCacheMsgBean(null);
    }

    /**
     * 释放marker
     */
    private void releaseMarker() {
        myMarker = null;
        myMarkerBearing = null;
        hisMarker = null;
        hisMarkerBearing = null;
    }

}
