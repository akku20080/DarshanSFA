package com.darshansfa.Utility;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.darshansfa.Configuration.Configuration;
import com.darshansfa.Models.ImageUpload;

/**
 * Created by Nikhil on 21-01-2017.
 */

public class ImageUploadUtil {

    private static final String IDENTITY_POOL_ID = "us-east-1:5affb305-d44c-44a5-9dfc-34d69d8bdf29";
    private static final String MY_BUCKET = "gladminds-images";

    public static ImageUploadListener listener;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonS3Client s3Client;
    private TransferUtility transferUtility;
    private List<ImageUpload> imageUploadList;
    private boolean isUploadingInProgress;

    private String ImageHostUrl = "https://" + MY_BUCKET + ".s3.amazonaws.com/";


    public void init(Context context) {
        if (credentialsProvider == null) {
            credentialsProvider = new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(),
                    IDENTITY_POOL_ID,
                    Regions.US_EAST_1);

            s3Client = new AmazonS3Client(credentialsProvider);
            transferUtility = new TransferUtility(s3Client, context.getApplicationContext());
            imageUploadList = new ArrayList<>();
        }
        Log.e("init", "----------------------- imageUrl : " + ImageHostUrl);
    }

    public ImageUpload startImageUpload(ImageUpload imageUpload) {
        try {

            if (TextUtils.isEmpty(imageUpload.getUri())) {
                return null;
            }

            this.isUploadingInProgress = true;
            File image = UIUtil.getFileForUri(Uri.parse(imageUpload.getUri()));
            String imagePath = (Configuration.BRAND_NAME.toLowerCase() + "/" + Constants.IMAGE_PATH.toLowerCase() + "/" + imageUpload.getPath() + "/" + image.getName()).toLowerCase();
            Log.e("startImageUpload", "----------------------- image : " + image.getName());
            TransferObserver observer = transferUtility.upload(MY_BUCKET, imagePath, image, CannedAccessControlList.PublicRead);
            Log.e("startImageUpload", "----------------------- observer.getAbsoluteFilePath() : " + observer.getAbsoluteFilePath());


            imageUpload.setObserver(observer);
            imageUploadList.add(imageUpload);

            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {

                    if (listener != null) {
                        listener.onStateChanged(id, state);
                        if (!state.name().equalsIgnoreCase("IN_PROGRESS"))
                            updateImageUploadList(id);

                        if (state.name().equalsIgnoreCase("COMPLETED")) {
                            listener.onUploadComplete(getImageUploadFromId(id));
                        }

                        if (checkForAllImageUpload()) {
                            isUploadingInProgress = false;
                            listener.allImageUpload();
                        }
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                    try {
                        long percentage = (bytesCurrent / bytesTotal) * 100;

                        Log.e("onProgressChanged", "bytesCurrent :  " + bytesCurrent + "  bytesTotal : " + bytesTotal);

                        if (listener != null) {
//                        listener.onProgressChanged(getImageUploadFromId(id));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(int id, Exception ex) {
                    if (listener != null) {
                        listener.onError(id, ex);
                    }
                }
            });

            return imageUpload;

        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(2, e);
            }
            return null;
        }

    }

    private void updateImageUploadList(int id) {
        for (int i = 0; i < imageUploadList.size(); i++) {
            if (imageUploadList.get(i).getObserver().getId() == id) {
                imageUploadList.get(i).setUpload(true);
            }
        }
    }

    public interface ImageUploadListener {

        public void onStateChanged(int id, TransferState state);

        public void onProgressChanged(ImageUpload imageUpload);

        public void onError(int id, Exception ex);

        public void onUploadComplete(ImageUpload imageUpload);

        public void allImageUpload();

        public void onSingleImageUpload(String imgUrl);

    }

    public void SetImageUploadListener(ImageUploadListener uploadListener) {
        this.listener = uploadListener;
    }

    private boolean checkForAllImageUpload() {
        for (int i = 0; i < imageUploadList.size(); i++) {
            if (!imageUploadList.get(i).isUpload()) {
                return false;
            }
        }

        return true;
    }


    private ImageUpload getImageUploadFromId(int id) {
        for (int i = 0; i < imageUploadList.size(); i++) {
            if (imageUploadList.get(i).getObserver().getId() == id) {
                return imageUploadList.get(i);
            }
        }
        return null;
    }

    public boolean isUploadingInProgress() {
        return isUploadingInProgress;
    }

    public void setUploadingInProgress(boolean uploadingInProgress) {
        isUploadingInProgress = uploadingInProgress;
    }

    public String singleFileUpload(String imgUri, String imageUploadPath) {
        try {
            if (TextUtils.isEmpty(imgUri)) {
                return null;
            }
            this.isUploadingInProgress = true;
            File image = UIUtil.getFileForUri(Uri.parse(imgUri));
            String imagePath = (Configuration.BRAND_NAME.toLowerCase() + "/" + Constants.IMAGE_PATH.toLowerCase() + "/" + imageUploadPath + "/" + image.getName()).toLowerCase();
            Log.e("startImageUpload", "----------------------- image : " + image.getName());
            TransferObserver observer = transferUtility.upload(MY_BUCKET, imagePath, image, CannedAccessControlList.PublicRead);
            Log.e("startImageUpload", "----------------------- observer.getAbsoluteFilePath() : " + observer.getAbsoluteFilePath());
            return ImageHostUrl + imagePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
