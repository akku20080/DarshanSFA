package com.darshansfa.dbModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * Created by Nikhil on 30-05-2017.
 */

public class PJPSchedule extends SugarRecord {

    @SerializedName("locality_id")
    @Expose
    private String localityId;
    @SerializedName("pjp_day")
    @Expose
    private String pjpDay;
    @SerializedName("pjp_date")
    @Expose
    private String pjpDate;
    @SerializedName("pjpId")
    @Expose
    private String pjpId;

    private String distributorId;
    private String dsrId;

    public String getLocalityId() {
        return localityId;
    }

    public void setLocalityId(String localityId) {
        this.localityId = localityId;
    }

    public String getPjpDay() {
        return pjpDay;
    }

    public void setPjpDay(String pjpDay) {
        this.pjpDay = pjpDay;
    }

    public String getPjpDate() {
        return pjpDate;
    }

    public void setPjpDate(String pjpDate) {
        this.pjpDate = pjpDate;
    }

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    public String getDsrId() {
        return dsrId;
    }

    public void setDsrId(String dsrId) {
        this.dsrId = dsrId;
    }

    public String getPjpId() {
        return pjpId;
    }

    public void setPjpId(String pjpId) {
        this.pjpId = pjpId;
    }

    @Override
    public String toString() {
        return "PJPSchedule{" +
                "localityId=" + localityId +
                ", pjpDay='" + pjpDay + '\'' +
                ", pjpDate='" + pjpDate + '\'' +
                ", distributorId='" + distributorId + '\'' +
                ", dsrId='" + dsrId + '\'' +
                '}';
    }
}
