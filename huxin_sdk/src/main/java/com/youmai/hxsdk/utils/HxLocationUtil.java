package com.youmai.hxsdk.utils;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.youmai.hxsdk.config.AppConfig;

/**
 * Created by Gary on 17/4/18 11:57
 * Email aoguoyue@163.com
 */

public class HxLocationUtil {

    private boolean isGoogle;
    private Context context;
    private OnLocationListener listener;
     public HxLocationUtil(Context context,OnLocationListener listener)
    {
        this.context = context;
        this.listener = listener;
        GetAddress();


    }
    private AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mapLocationClient = null;
    private AMapLocationListener mapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation !=null)
            {
                LogUtils.e("LocationManager", "地址：:--:" + aMapLocation.toString());
                if(aMapLocation.getErrorCode()==AMapLocation.LOCATION_SUCCESS)
                {
                    String  city =aMapLocation.getProvince()+" "+aMapLocation.getCity();
                    LocationInfo locationInfo  = new LocationInfo();
                    String url = "http://restapi.amap.com/v3/staticmap?location="
                            + aMapLocation.getLongitude() + "," + aMapLocation.getLatitude() + "&zoom=" + 16
                            + "&size=720*550&traffic=1&markers=mid,0xff0000,A:" + aMapLocation.getLongitude()
                            + "," + aMapLocation.getLatitude() + "&key=" + AppConfig.staticMapKey;
                    locationInfo.setAddress(aMapLocation.getAoiName()).setCity(city).setLatitude(aMapLocation.getLatitude()).setLongitude(aMapLocation.getLongitude()).setMapPicUrl(url);

                    listener.success(locationInfo);
                }
                else
                {
                    listener.failure();
                }
            }
            else
            {
                listener.failure();
            }


        }
    };
    private void  GetAddress()
    {
        mapLocationClient = new AMapLocationClient(context);
        mapLocationClient.setLocationListener(mapLocationListener);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        mLocationOption.setOnceLocation(true);
        mapLocationClient.setLocationOption(mLocationOption);
        mapLocationClient.startLocation();
    }
    public void removeLocation()
    {

            if (mapLocationClient !=null)
            {
                if (mapLocationClient.isStarted())
                {
                    mapLocationClient.unRegisterLocationListener(mapLocationListener);
                    mapLocationClient.stopLocation();
                    mapLocationClient.onDestroy();
                }
            }

    }
    public interface OnLocationListener {
        void success(LocationInfo locationInfo);
        void failure();
    }

    public class LocationInfo
    {
        private double latitude;
        private double longitude;
        private String city;
        private String address;
        private String mapPicUrl;

        public LocationInfo() {
        }
        public double getLatitude() {
            return latitude;
        }

        public LocationInfo setLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public double getLongitude() {
            return longitude;
        }

        public LocationInfo setLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public String getCity() {
            return city;
        }

        public LocationInfo setCity(String city) {
            this.city = city;
            return this;
        }

        public String getAddress() {
            return address;
        }

        public LocationInfo setAddress(String address) {
            this.address = address;
            return this;
        }

        public String getMapPicUrl() {
            return mapPicUrl;
        }

        public LocationInfo setMapPicUrl(String mapPicUrl) {
            this.mapPicUrl = mapPicUrl;
            return this;
        }

        @Override
        public String toString() {
            return "LocationInfo{" +
                    "latitude=" + latitude +
                    ", longitude=" + longitude +
                    ", city='" + city + '\'' +
                    ", address='" + address + '\'' +
                    ", mapPicUrl='" + mapPicUrl + '\'' +
                    '}';
        }
    }
}
