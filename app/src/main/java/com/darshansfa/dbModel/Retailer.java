package com.darshansfa.dbModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by Nikhil on 16-05-2017.
 */

public class Retailer extends SugarRecord {
    @Unique
    @SerializedName("retailer_id")
    @Expose
    private String retailerId;

    @SerializedName("distributor_Id")
    @Expose
    private String distributorId;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("retailer_name")
    @Expose
    private String retailerName;
    @SerializedName("retailer_mobile")
    @Expose
    private String retailerMobile;
    @SerializedName("locality")
    @Expose
    private String locality;
    @SerializedName("retailer_email")
    @Expose
    private String retailerEmail;
    @SerializedName("locality_id")
    @Expose
    private String localityId;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("datetime")
    @Expose
    private String datetime;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("retailer_address")
    @Expose
    private String retailerAddress;
    @SerializedName("credit_limit")
    @Expose
    private String creditLimit;



    private String outstandingAmount;

    private boolean visitStart;

    private String startTime;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStopDate() {
        return stopDate;
    }

    public void setStopDate(String stopDate) {
        this.stopDate = stopDate;
    }

    private String startDate;
    private String stopDate;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    private String stopTime;


    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRetailerName() {
        return retailerName;
    }

    public void setRetailerName(String retailerName) {
        this.retailerName = retailerName;
    }

    public String getRetailerMobile() {
        return retailerMobile;
    }

    public void setRetailerMobile(String retailerMobile) {
        this.retailerMobile = retailerMobile;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getRetailerEmail() {
        return retailerEmail;
    }

    public void setRetailerEmail(String retailerEmail) {
        this.retailerEmail = retailerEmail;
    }

    public String getLocalityId() {
        return localityId;
    }

    public void setLocalityId(String localityId) {
        this.localityId = localityId;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getRetailerAddress() {
        return retailerAddress;
    }

    public void setRetailerAddress(String retailerAddress) {
        this.retailerAddress = retailerAddress;
    }

    public boolean isVisitStart() {
        return visitStart;
    }

    public void setVisitStart(boolean visitStart) {
        this.visitStart = visitStart;
    }

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    public String getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(String outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public String getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(String creditLimit) {
        this.creditLimit = creditLimit;
    }

    @Override
    public String toString() {
        return "Retailer{" +
                "retailerId='" + retailerId + '\'' +
                ", city='" + city + '\'' +
                ", retailerName='" + retailerName + '\'' +
                ", retailerMobile='" + retailerMobile + '\'' +
                ", locality='" + locality + '\'' +
                ", retailerEmail='" + retailerEmail + '\'' +
                ", localityId='" + localityId + '\'' +
                ", longitude='" + longitude + '\'' +
                ", datetime='" + datetime + '\'' +
                ", state='" + state + '\'' +
                ", latitude='" + latitude + '\'' +
                ", retailerAddress='" + retailerAddress + '\'' +
                '}';
    }
}
