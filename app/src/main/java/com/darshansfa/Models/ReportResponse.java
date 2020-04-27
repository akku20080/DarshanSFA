package com.darshansfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import com.darshansfa.dbModel.Retailer;

/**
 * Created by Nikhil on 16-08-2017.
 */

public class ReportResponse {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("report_details")
    @Expose
    private Report reportDetails;

    @SerializedName("billed_parts")
    @Expose
    private List<String> billedParts = new ArrayList<>();

    @SerializedName("retailer")
    @Expose
    private List<Retailer> retailers = new ArrayList<>();

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

    public Report getReportDetails() {
        return reportDetails;
    }

    public void setReportDetails(Report reportDetails) {
        this.reportDetails = reportDetails;
    }

    public List<String> getBilledParts() {
        return billedParts;
    }

    public void setBilledParts(List<String> billedParts) {
        this.billedParts = billedParts;
    }


    public List<Retailer> getRetailers() {
        return retailers;
    }

    public void setRetailers(List<Retailer> retailers) {
        this.retailers = retailers;
    }

    @Override
    public String toString() {
        return "ReportResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", reportDetails=" + reportDetails +
                ", billedParts=" + billedParts +
                ", retailers=" + retailers +
                '}';
    }
}
