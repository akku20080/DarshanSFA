package com.darshansfa.Utility;


import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;

/**
 * Created by Nikhil on 30-11-2015.
 */

// Call this Object In Background thread
public class ImageUploadToS3 {

    private static String MY_ACCESS_KEY_ID = "", MY_SECRET_KEY = "";

    private AmazonS3Client s3Client;

    public final String BUCKET_NAME = "gladminds";
    public final String IMAGE_HOST_PATH = "https://" + BUCKET_NAME + ".s3.amazonaws.com/";
    public final String IMAGE_PATH = "demo/pony/";
    public final String IMAGE_PATH_USER = "demo/pony/userprofile/";
    private static String imagePath;


    public ImageUploadToS3() {
        s3Client = new AmazonS3Client(new BasicAWSCredentials(MY_ACCESS_KEY_ID, MY_SECRET_KEY));
        s3Client.createBucket(BUCKET_NAME);
    }

    public String UploadImage(String filePath, String fileUrl) {

        File file = new File(fileUrl);
        imagePath = IMAGE_PATH_USER + filePath + file.getName();
        imagePath = IMAGE_PATH + filePath + file.getName();
        Log.e("ImageUploadToS3", "------------NAME---------  " + file.getName());
        PutObjectRequest por = new PutObjectRequest(BUCKET_NAME, imagePath, new File(fileUrl));
        por.withCannedAcl(CannedAccessControlList.PublicRead);
        s3Client.putObject(por);
        Log.e("ImageUploadToS3", "------------URL---------  " + IMAGE_HOST_PATH + imagePath);
        return IMAGE_HOST_PATH + imagePath;
    }


}
