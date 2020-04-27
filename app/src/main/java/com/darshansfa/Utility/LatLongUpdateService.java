package com.darshansfa.Utility;//package com.darshansfa.Utility;
//
//import android.Manifest;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.support.v4.content.ContextCompat;
//import android.util.Log;
//
//import com.google.gson.JsonObject;
//
//import java.util.List;
//
//
//public class LatLongUpdateService extends Service {
//    private static final int TRIP_HISTORY_INTERVAL = 1000 * 20;
//    private static final int REQ_CODE = 100;
//    private final static int TRIP_LOCATION_INTERVAL = 1000 * 2;//1 second
//    private String currentStopLatitude, currentStopLongitude;
//
//    public LatLongUpdateService() {
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.e("LatLongUpdateService", "onStartCommand-------------- onStartCommand : ");
//        if (LatLngUtil.checkForTime()) {
//            checkForPermission();
//        } else {
//            stopSelf();
//        }
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    public void onCreate() {
//        Log.e("LatLongUpdateService", "onCreate-------------- onCreate : ");
//        super.onCreate();
//    }
//
//
//    private void checkForPermission() {
//        Log.e("LatLongUpdateService", "checkForPermission-------------- onStartCommand : ");
//
//        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
//
//        if (permissionCheck != PackageManager.PERMISSION_GRANTED || !LatLngUtil.checkForLocationEnable(getApplicationContext())) {
////            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//            Log.e("LatLongUpdateService", "checkForPermission-------------- PermissionEverywhere : ");
//            PermissionEverywhere.getPermission(getApplicationContext(),
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                    REQ_CODE,
//                    getString(R.string.app_name),
//                    "This app needs a GPS permission",
//                    R.mipmap.ic_launcher)
//                    .enqueue(new PermissionResultCallback() {
//                        @Override
//                        public void onComplete(PermissionResponse permissionResponse) {
//                            Log.e("LatLongUpdateService", "checkForPermission-------------- onComplete : ");
//                            if (permissionResponse.isGranted()) {
//                                requestCurrentLocation();
//                            } else {
//                                stopSelf();
//                            }
//                        }
//                    });
//        } else {
//            Log.e("LatLongUpdateService", "checkForPermission-------------- PERMISSION_GRANTED : ");
//            requestCurrentLocation();
//        }
//    }
//
//
//    private void updateLotLong(Location location) {
//
//        Log.e("LatLongUpdateService", "onLocationChanged-------------- location : " + location.toString());
//
//        StorageManager.addPreferencesFields(getApplicationContext(), AppConstants.Pref.LAT,
//                String.valueOf(location.getLatitude()));
//        StorageManager.addPreferencesFields(getApplicationContext(), AppConstants.Pref.LONG,
//                String.valueOf(location.getLongitude()));
//
//        if (!UIUtil.isInternetAvailable(this)) {
//            stopSelf();
//            return;
//        }
//
//
//        JsonObject object = new JsonObject();
//
//        object.addProperty("latitude", location.getLatitude());
//        object.addProperty("longitude", location.getLongitude());
//
//
//        Log.e("update Lat", "JSON -- " + object.toString());
//
//        RetrofitAPI.getInstance().getApi().updateLatlng(StorageManager.getDsrID(getApplicationContext()), object, new Callback<JsonObject>() {
//            @Override
//            public void success(JsonObject jsonObject, retrofit.client.Response response) {
//                Log.e("update Lat", "Succc -- " + jsonObject.toString());
//                stopSelf();
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//
//            }
//        });
////
//    }
//
//
//    private void requestCurrentLocation() {
//        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
//        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        final LocationListener locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                if (location != null) {
//                    Log.e("onLocationChanged", " not  onLocationChanged :  lat : " + "" + "  Long : " + "");
//                    updateLotLong(location);
//                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
//                    locationManager.removeUpdates(this);
//                } else {
//                    Log.e("onLocationChanged", " Location is null................ ");
//                }
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//
//            }
//        };
//
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//
////        new Handler().postDelayed(new Runnable() {
////            @Override
////            public void run() {
////                Log.e("onLocationChanged", " befor onLocationChanged :  lat : " + "" + "  Long : " + "");
////                Location location = getLastKnownLocation(locationManager);
////                if (location != null) {
////                    Log.e("onLocationChanged", " not  LastKnow location :  lat : " + "" + "  Long : " + "");
////                    updateLotLong(location);
////                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
////                    locationManager.removeUpdates(locationListener);
////                } else {
////                    Log.e("onLocationChanged", " Loaction  not null");
////                }
////
////            }
////        }, 15000);
//
//    }
//
//    private Location getLastKnownLocation(LocationManager mLocationManager) {
//        List<String> providers = mLocationManager.getProviders(true);
//        Location bestLocation = null;
//        ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
//        for (String provider : providers) {
//            Location l = mLocationManager.getLastKnownLocation(provider);
//            Log.d("demo", "last known location, provider: %s, location: %s " + provider);
//
//            if (l == null) {
//                continue;
//            }
//            if (bestLocation == null
//                    || l.getAccuracy() < bestLocation.getAccuracy()) {
//                Log.d("loc", "found best last known location: %s");
//                bestLocation = l;
//            }
//        }
//        if (bestLocation == null) {
//            return null;
//        }
//        return bestLocation;
//    }
//}
