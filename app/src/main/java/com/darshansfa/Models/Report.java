package com.darshansfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Nikhil on 16-08-2017.
 */

public class Report {

    @SerializedName("new_retailer_details")
    @Expose
    private String newRetailerDetails;
    @SerializedName("total_collection")
    @Expose
    private String totalCollection;
    @SerializedName("top_selling_part")
    @Expose
    private String topSellingPart;
    @SerializedName("billed_parts_value")
    @Expose
    private String billedPartsValue;
    @SerializedName("sales_value")
    @Expose
    private String salesValue;
    @SerializedName("zero_billed_retailers")
    @Expose
    private String zeroBilledRetailers;
    @SerializedName("top_selling_part_value")
    @Expose
    private String topSellingPartValue;
    @SerializedName("top_retailers_details")
    @Expose
    private String topRetailersDetails;
    @SerializedName("mtd")
    @Expose
    private String mtd;

    public String getNewRetailerDetails() {
        return newRetailerDetails;
    }

    public void setNewRetailerDetails(String newRetailerDetails) {
        this.newRetailerDetails = newRetailerDetails;
    }

    public String getTotalCollection() {
        return totalCollection;
    }

    public void setTotalCollection(String totalCollection) {
        this.totalCollection = totalCollection;
    }

    public String getTopSellingPart() {
        return topSellingPart;
    }

    public void setTopSellingPart(String topSellingPart) {
        this.topSellingPart = topSellingPart;
    }

    public String getBilledPartsValue() {
        return billedPartsValue;
    }

    public void setBilledPartsValue(String billedPartsValue) {
        this.billedPartsValue = billedPartsValue;
    }

    public String getSalesValue() {
        return salesValue;
    }

    public void setSalesValue(String salesValue) {
        this.salesValue = salesValue;
    }

    public String getZeroBilledRetailers() {
        return zeroBilledRetailers;
    }

    public void setZeroBilledRetailers(String zeroBilledRetailers) {
        this.zeroBilledRetailers = zeroBilledRetailers;
    }

    public String getTopSellingPartValue() {
        return topSellingPartValue;
    }

    public void setTopSellingPartValue(String topSellingPartValue) {
        this.topSellingPartValue = topSellingPartValue;
    }

    public String getTopRetailersDetails() {
        return topRetailersDetails;
    }

    public void setTopRetailersDetails(String topRetailersDetails) {
        this.topRetailersDetails = topRetailersDetails;
    }

    public String getMtd() {
        return mtd;
    }

    public void setMtd(String mtd) {
        this.mtd = mtd;
    }

    @Override
    public String toString() {
        return "Report{" +
                "newRetailerDetails='" + newRetailerDetails + '\'' +
                ", totalCollection='" + totalCollection + '\'' +
                ", topSellingPart='" + topSellingPart + '\'' +
                ", billedPartsValue='" + billedPartsValue + '\'' +
                ", salesValue='" + salesValue + '\'' +
                ", zeroBilledRetailers='" + zeroBilledRetailers + '\'' +
                ", topSellingPartValue='" + topSellingPartValue + '\'' +
                ", topRetailersDetails='" + topRetailersDetails + '\'' +
                ", mtd='" + mtd + '\'' +
                '}';
    }
}
