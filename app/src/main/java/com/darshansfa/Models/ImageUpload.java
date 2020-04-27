package com.darshansfa.Models;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;

/**
 * Created by Nikhil on 30-08-2017.
 */

public class ImageUpload {
    private int imageType;
    private String uri;
    private String url;
    private String path;
    private TransferObserver observer;
    private boolean isUpload;

    public ImageUpload() {
    }

    public ImageUpload(int imageType, String uri, String path) {
        this.imageType = imageType;
        this.uri = uri;
        this.path = path;
    }

    public int getImageType() {
        return imageType;
    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public TransferObserver getObserver() {
        return observer;
    }

    public void setObserver(TransferObserver observer) {
        this.observer = observer;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    @Override
    public String toString() {
        return "ImageUpload{" +
                ", uri='" + uri + '\'' +
                ", url='" + url + '\'' +
                ", path='" + path + '\'' +
                ", observer=" + observer.toString() +
                '}';
    }
}
