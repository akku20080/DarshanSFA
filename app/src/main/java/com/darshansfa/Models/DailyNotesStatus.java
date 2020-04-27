package com.darshansfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Nikhil on 13-09-2017.
 */

public class DailyNotesStatus {

    @SerializedName("planned_date")
    @Expose
    private String plannedDate;
    @SerializedName("pjpId")
    @Expose
    private String id;

    public String getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(String plannedDate) {
        this.plannedDate = plannedDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "DailyNotesStatus{" +
                "plannedDate='" + plannedDate + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
