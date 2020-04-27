package com.darshansfa.Utility;

import android.content.Context;
import android.location.LocationManager;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Nikhil on 06-02-2017.
 */

public class LatLngUtil {

    public static final int UPDATE_TIME_INTERVAL = 5; // Time interval in min

    public static final int LAT_LNG_ALARM_ID = 2333;//

//    public static void startRepeatAlarm(Context context) {
//
//        Intent intent = new Intent(context, LatLongUpdateService.class);
//
//        PendingIntent pendingIntent = PendingIntent.getService(context, LAT_LNG_ALARM_ID, intent,
//                PendingIntent.FLAG_CANCEL_CURRENT);
//
//        AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
//
//        am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
//                UPDATE_TIME_INTERVAL * 60 * 1000, pendingIntent);
//
//    }
//
//    public static void stopRepeatAlarm(Context context) {
//        Intent intent = new Intent(context, LatLongUpdateService.class);
//        PendingIntent pendingIntent = PendingIntent.getService(context,
//                LAT_LNG_ALARM_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
//        am.cancel(pendingIntent);
//
//    }

    public static boolean checkForTime() {
        try {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aaa");
            Date date1 = simpleDateFormat.parse("08:00 AM");
            Date date2 = simpleDateFormat.parse("06:00 PM");

            String delegate = "hh:mm aaa";
            String now = (String) DateFormat.format(delegate, Calendar.getInstance().getTime());

            Log.e("LatLngUtil", "time v-------------- : " + now);
            Date date = simpleDateFormat.parse(now);
            if (date.before(date2) && date.after(date1)) {
                Log.e("LatLngUtil", "time v-------------- : true");
                return true;
            } else {
                Log.e("LatLngUtil", "time v-------------- : false");
                return false;
//                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


    public static boolean checkForLocationEnable(final Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return gps_enabled || network_enabled ? true : false;
    }
}
