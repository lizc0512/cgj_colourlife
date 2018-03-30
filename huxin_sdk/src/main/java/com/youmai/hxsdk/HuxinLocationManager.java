package com.youmai.hxsdk;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.youmai.hxsdk.utils.LogUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/1/9.
 */

public class HuxinLocationManager {

    private static HuxinLocationManager instance;

    private Location mLocation;
    private LocationManager lm;

    private BetterLocationListener gpsLocationListener;
    private BetterLocationListener netLocationListener;

    public static HuxinLocationManager instance() {
        if (instance == null) {
            instance = new HuxinLocationManager();
        }
        return instance;
    }

    public void init(Context context) {
        lm = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }

    public interface BetterLocationListener {
        void result(Location location);
    }

    private LocationListener gpsListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LogUtils.d("", "告警：gps定位，onLocationChanged");
            if (isBetterLocation(location, mLocation)) {
                if (mLocation != null) {
                    mLocation = location;
                } else {
                    mLocation = location;
                }
            }
            gpsLocationListener.result(mLocation);
            lm.removeUpdates(gpsListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //LogUtils.d("", "告警：gps定位，onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            //LogUtils.d("", "告警：gps定位，onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            //LogUtils.d("", "告警：gps定位，onProviderDisabled");
        }
    };

    private LocationListener netListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LogUtils.d("", "告警：net定位，onLocationChanged");
            if (isBetterLocation(location, mLocation)) {
                if (mLocation != null) {
                    mLocation = location;
                } else {
                    mLocation = location;
                }
            }
            netLocationListener.result(mLocation);
            lm.removeUpdates(netListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //LogUtils.d("", "告警：net定位，onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            //LogUtils.d("", "告警：net定位，onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            //LogUtils.d("", "告警：net定位，onProviderDisabled");
        }
    };

    /**
     * GPS定位，精度高，耗时长
     *
     * @param listener
     */
    public void requestGPS(final BetterLocationListener listener) {
        gpsLocationListener = listener;
        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * wifi定位，精度低，耗时少
     *
     * @param listener
     */
    public void requestNetwork(final BetterLocationListener listener) {
        netLocationListener = listener;
        try {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, netListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从缓存中直接拿
     */
    public Location requestCache() {
        Location betterLocation = null;
        List<String> providers = lm.getProviders(true);
        if (providers != null) {
            for (String provider : providers) {
                Location l = null;
                try {
                    l = lm.getLastKnownLocation(provider);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }

                if (l == null) {
                    continue;
                }
                if (betterLocation == null || l.getAccuracy() < betterLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    betterLocation = l;
                }
            }
        }
        return betterLocation;
    }

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
