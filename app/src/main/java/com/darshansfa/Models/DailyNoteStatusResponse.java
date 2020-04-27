package com.darshansfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Nikhil on 13-09-2017.
 */

public class DailyNoteStatusResponse {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("daily_notes_status")
    @Expose
    private List<DailyNotesStatus> dailyNotesStatus = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DailyNotesStatus> getDailyNotesStatus() {
        return dailyNotesStatus;
    }

    public void setDailyNotesStatus(List<DailyNotesStatus> dailyNotesStatus) {
        this.dailyNotesStatus = dailyNotesStatus;
    }

    @Override
    public String toString() {
        return "DailyNoteStatusResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", dailyNotesStatus=" + dailyNotesStatus +
                '}';
    }
}
