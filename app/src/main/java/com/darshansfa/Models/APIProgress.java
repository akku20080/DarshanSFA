package com.darshansfa.Models;

import com.darshansfa.Utility.Constants;

/**
 * Created by Nikhil on 09-05-2017.
 */

public class APIProgress {

    private String title;
    private int progress;
    private int status = Constants.WAITING;
    private int API;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAPI() {
        return API;
    }

    public void setAPI(int API) {
        this.API = API;
    }

    @Override
    public String toString() {
        return "APIProgress{" +
                "title='" + title + '\'' +
                ", progress=" + progress +
                ", status=" + status +
                '}';
    }
}
