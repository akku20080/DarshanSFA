package com.darshansfa.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.darshansfa.dbModel.Product;

/**
 * Created by kapil.kale on 7/7/2015.
 */
public class UIUtil {

    private static ProgressDialog mProgressDialog;
    private static Object mObject = new Object();

    public static void startProgressDialog(Context context, String message) {
        try {
            synchronized (mObject) {
                if (mProgressDialog == null) {
                    mProgressDialog = ProgressDialog.show(context, "", message);
                    mProgressDialog.setIndeterminate(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopProgressDialog(Context context) {
        try {
            synchronized (mObject) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    public static boolean isGPSON(final Context context) {
       /* ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();*/

        LocationManager service = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Check if enabled and if not send user to the GPS settings
        if (!enabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

            // Setting Dialog Title
            alertDialog.setTitle("GPS settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. The app needs GPS, Please enable the GPS from the settings.");

            // On pressing Settings button
            alertDialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                }
            });

//            // on pressing cancel button
//            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
            alertDialog.setCancelable(false);
            // Showing Alert Message
            alertDialog.show();
        }


        return enabled;
    }

    public static String amountFormat(String value) {
        try {
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            float val = Float.parseFloat(value);
            return df.format(val);
        } catch (Exception e) {
            return value;
        }
    }

    public static String TotalClosingStock(Product product) {
        String str = "Total Closing Stock : ";
        try {
            if ("NA".equalsIgnoreCase(product.getStock())) {
                return str + product.getTransitStock();
            }
            if ("NA".equalsIgnoreCase(product.getTransitStock())) {
                return str + product.getStock();
            }

            return str + (Float.parseFloat(product.getStock()) + Float.parseFloat(product.getTransitStock()));

        } catch (Exception e) {
            return str + "NA";
        }
    }

    public static int compareDate(String date1, String date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        if (date1.equalsIgnoreCase(date2))
            return 0;
        try {
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);

            if (d2.after(d1)) {
                return 1;
            }
            return -1;

        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }

    }

    public static String getTodaysDate() {

        final Calendar c = Calendar.getInstance();
        int todaysDate = (c.get(Calendar.YEAR) * 10000) + ((c.get(Calendar.MONTH) + 1) * 100) + (c.get(Calendar.DAY_OF_MONTH));
        Log.w("DATE:", String.valueOf(todaysDate));
        return (String.valueOf(todaysDate));

    }

    public static String getCurrentTime() {

        final Calendar c = Calendar.getInstance();
        int currentTime = (c.get(Calendar.HOUR_OF_DAY) * 10000) + (c.get(Calendar.MINUTE) * 100) + (c.get(Calendar.SECOND));
        Log.w("TIME:", String.valueOf(currentTime));
        return (String.valueOf(currentTime));

    }

    public static File getFileForUri(@NonNull Uri uri) {
        String path = uri.getEncodedPath();
        final int splitIndex = path.indexOf('/', 1);
        final String tag = Uri.decode(path.substring(1, splitIndex));
        path = Uri.decode(path.substring(splitIndex + 1));

        if (!"root".equalsIgnoreCase(tag)) {
//            throw new IllegalArgumentException(
//                    String.format("Can't decode paths to '%s', only for 'root' paths.",
//                            tag));
            return new File(uri.getPath());
        }
        final File root = new File("/");

        File file = new File(root, path);
        try {
            file = file.getCanonicalFile();
        } catch (IOException e) {
            return new File(uri.getPath());
//            throw new IllegalArgumentException("Failed to resolve canonical path for " + file);
        }

        if (!file.getPath().startsWith(root.getPath())) {
            return new File(uri.getPath());
//            throw new SecurityException("Resolved path jumped beyond configured root");
        }

        return file;
    }

    public static void noInternet(final Activity activity, final boolean close) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);

//        AlertDialog.Builder builder =
//                new AlertDialog.Builder(this, R.style.AppTheme_AppBarOverlay);
        builder.setCancelable(false);
        builder.setTitle("No Internet");
        String message = "Device not connect to internet. Need sync some data, Please connect to internet";
        builder.setMessage(message);
        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (close)
                    activity.finish();
            }
        });

        builder.show();
    }

    public static void dialPhoneNumber(Activity activity, String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getText(String str) {
        try {

            if (TextUtils.isEmpty(str) || "null".equalsIgnoreCase(str)) {
                return "";
            }
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getTextNA(String str) {
        try {

            if (TextUtils.isEmpty(str) || "null".equalsIgnoreCase(str)) {
                return "NA";
            }
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return "NA";
        }
    }

    public static String getText(Double str) {
        try {
            if (str == null || str == 0) {
                return "";
            }
            return String.valueOf(str);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getText(Integer str) {
        try {
            if (str == null || str == 0) {
                return "";
            }
            return String.valueOf(str);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getText(TextView textView) {
        try {
            String str = textView.getText().toString();
            if (TextUtils.isEmpty(str) || "null".equalsIgnoreCase(str)) {
                return "";
            }
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getTextInt(TextView textView) {
        try {
            String str = textView.getText().toString();
            if (TextUtils.isEmpty(str))
                return 0;
            int i = Integer.parseInt(str);
            return i;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


}
