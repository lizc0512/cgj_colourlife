package com.youmai.hxsdk.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by Gary on 17/4/10 17:30
 * Email aoguoyue@163.com
 */

public class LocationUtil {
    private  LocationManager manager;
    private  LocationListener swapLocationListener;
    private Context context;
    private OnLocationListener locationListener;
    public LocationUtil(Context context,OnLocationListener locationListener)
    {
        this.context = context;
        this.locationListener = locationListener;
        getAddress();
    }

    private  void  getAddress()
    {

        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        swapLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location1) {
                if (location1 !=null)
                {
                    locationListener.success(location1);
                }
                else
                {
                    LogUtils.e("LocationManager","Location 为空");
                    locationListener.failure();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                LogUtils.e("LocationManager","onStatusChanged ");
            }

            @Override
            public void onProviderEnabled(String provider) {
                LogUtils.e("LocationManager","onProviderEnabled"+provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                LogUtils.e("LocationManager","onProviderDisabled"+provider);
                locationListener.failure();
            }
        };
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,swapLocationListener);
    }

    public void removeLocation()
    {

        if (swapLocationListener != null)
        {
            manager.removeUpdates(swapLocationListener);
            swapLocationListener=null;
        }
    }

    public interface OnLocationListener {
        void success(Location location);
        void failure();
    }
}
