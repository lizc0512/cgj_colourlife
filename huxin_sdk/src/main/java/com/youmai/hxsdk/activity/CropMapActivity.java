package com.youmai.hxsdk.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.youmai.hxsdk.R;
import com.youmai.hxsdk.config.Constant;
import com.youmai.hxsdk.utils.LogUtils;
import com.youmai.hxsdk.utils.StringUtils;
import com.youmai.hxsdk.view.full.MapViewUtil;

/**
 * 作者：create by YW
 * 日期：2016.08.24 10:18
 * 描述：
 */
public class CropMapActivity extends SdkBaseActivity {

    public static CropMapActivity mapActivity;

    private MapViewUtil mMapViewUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hx_crop_map_view);

        mapActivity = this;

        String location = getIntent().getStringExtra("location");
        String mLabelAddress = getIntent().getStringExtra("labelAddress");

        MapView mMapView = (MapView) findViewById(R.id.fm_msg_map);
        TextView tv_location_address = (TextView) findViewById(R.id.tv_location_address);
        TextView tv_location_long_address = (TextView) findViewById(R.id.tv_location_long_address);
        ImageView iv_navigate = (ImageView) findViewById(R.id.iv_navigate);
        ImageView iv_half_back = (ImageView) findViewById(R.id.iv_half_back);


        mMapViewUtil = new MapViewUtil(this, mMapView);
        mMapViewUtil.onCreate(null);
        mMapViewUtil.setLocation(location);//标志物

        if (!StringUtils.isEmpty(location)) {
            final LatLng latLng1 = new LatLng(
                    Double.parseDouble(location.split(",")[1]),
                    Double.parseDouble(location.split(",")[0]));
            iv_navigate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//导航
                    setFloatView(false);
                    mMapViewUtil.toDaoHang(latLng1);
                }
            });
        }

        if (!StringUtils.isEmpty(mLabelAddress)) {
            try {
                if (mLabelAddress.contains(":")) {
                    tv_location_address.setVisibility(View.VISIBLE);
                    tv_location_address.setText(mLabelAddress.split(":")[0]);
                    tv_location_long_address.setVisibility(View.VISIBLE);
                    tv_location_long_address.setText(mLabelAddress.split(":")[1]);
                } else {
                    tv_location_address.setVisibility(View.VISIBLE);
                    tv_location_long_address.setVisibility(View.GONE);
                    tv_location_address.setText(mLabelAddress);
                }
            } catch (Exception e) {
                e.printStackTrace();
                tv_location_address.setVisibility(View.VISIBLE);
                tv_location_address.setText(mLabelAddress);
                LogUtils.e(Constant.SDK_UI_TAG, "地图位置拆分有异常");
            }
        }

        iv_half_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
