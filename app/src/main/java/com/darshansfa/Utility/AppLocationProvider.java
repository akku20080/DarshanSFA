package com.darshansfa.Utility;

import android.Manifest;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;

/**
 * Created by Nikhil on 19-06-2017.
 */

public class AppLocationProvider {
    static boolean b = true;

    public static interface LocationCallback {
        public void onLocationAvailable(Location location);
    }

    public static void requestForLocation(final Context context, final LocationCallback callback) {
        b = true;
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isNetworkEnabled) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(false);
            criteria.setAltitudeRequired(false);
            int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
            locationManager.requestLocationUpdates(1000, 0, criteria, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (b) {
                        callback.onLocationAvailable(location);
                        locationManager.removeUpdates(this);
                        b = false;
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            }, null);
        } else {
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSEnabled) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setBearingRequired(false);
                criteria.setSpeedRequired(false);
                criteria.setAltitudeRequired(false);
                locationManager.requestLocationUpdates(1000, 0, criteria, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (b) {
                            callback.onLocationAvailable(location);
                            locationManager.removeUpdates(this);
                            b = false;
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                }, null);
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (b) {
                    callback.onLocationAvailable(location);
                    b = false;
                }
            }
        }, 5000);


    }

//    public static void


}
