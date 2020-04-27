package com.darshansfa.Utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.darshansfa.Configuration.Configuration;
import com.darshansfa.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by kapil.kale on 7/7/2015.
 */
public class ImageUtil {


    /**
     * Creating file uri to store image/video
     */
    public static Uri getImagePathURI(Context context, String folderName) {
        try {

            String path = Environment.getExternalStorageDirectory().toString() + File.separator + Configuration.BRAND_NAME;
            File mediaStorageDir = new File(path, folderName);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.e(folderName, "Oops! Failed create " + folderName + " directory");
                    Toast.makeText(context, "Oops! Failed create " + folderName + " directory", Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File mediaFile;

            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

            if (Build.VERSION.SDK_INT >= 24) {
                return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", mediaFile);
            } else {
                return Uri.fromFile(mediaFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "" + e.toString(), Toast.LENGTH_SHORT).show();
            return null;
        }

    }


    public static void previewImage(Activity activity, String url) {

        try {
            Dialog settingsDialog = new Dialog(activity);
            settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
            View vi = inflater.inflate(R.layout.dialog_preview_image, null);
            ImageView iv = (ImageView) vi.findViewById(R.id.fullimage);
            iv.setImageBitmap(decodeSampledBitmapFromResource(url, dpToPx(activity, 200), dpToPx(activity, 200)));

            settingsDialog.setContentView(vi);
            settingsDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static int dpToPx(Context c, float dp) {
        DisplayMetrics displayMetrics = c.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public int pxToDp(Context c, int px) {
        DisplayMetrics displayMetrics = c.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

//         Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

}
