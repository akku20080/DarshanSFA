package com.darshansfa.dbModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by Nikhil on 07-06-2017.
 */

public class Distributor extends SugarRecord {

    @SerializedName("depot_code")
    @Expose
    private String depotCode;
    @Unique
    @SerializedName("distributor_code")
    @Expose
    private String distributorCode;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;

    private String timeStampPart;
    private String timeStampRetailer;
    private String timeStampOrder;
    private String timeStampInvoice;
    private String timeStampStock;
    private String timeStampPJPSchedule;
    private String timeStampProductAvg;


    public String getDepotCode() {
        return depotCode;
    }

    public void setDepotCode(String depotCode) {
        this.depotCode = depotCode;
    }

    public String getDistributorCode() {
        return distributorCode;
    }

    public void setDistributorCode(String distributorCode) {
        this.distributorCode = distributorCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimeStampPart() {
        return timeStampPart;
    }

    public void setTimeStampPart(String timeStampPart) {
        this.timeStampPart = timeStampPart;
    }

    public String getTimeStampRetailer() {
        return timeStampRetailer;
    }

    public void setTimeStampRetailer(String timeStampRetailer) {
        this.timeStampRetailer = timeStampRetailer;
    }

    public String getTimeStampOrder() {
        return timeStampOrder;
    }

    public void setTimeStampOrder(String timeStampOrder) {
        this.timeStampOrder = timeStampOrder;
    }

    public String getTimeStampInvoice() {
        return timeStampInvoice;
    }

    public void setTimeStampInvoice(String timeStampInvoice) {
        this.timeStampInvoice = timeStampInvoice;
    }

    public String getTimeStampStock() {
        return timeStampStock;
    }

    public void setTimeStampStock(String timeStampStock) {
        this.timeStampStock = timeStampStock;
    }

    public String getTimeStampPJPSchedule() {
        return timeStampPJPSchedule;
    }

    public void setTimeStampPJPSchedule(String timeStampPJPSchedule) {
        this.timeStampPJPSchedule = timeStampPJPSchedule;
    }

    public String getTimeStampProductAvg() {
        return timeStampProductAvg;
    }

    public void setTimeStampProductAvg(String timeStampProductAvg) {
        this.timeStampProductAvg = timeStampProductAvg;
    }

    @Override
    public String toString() {
        return "Distributor{" +
                "depotCode='" + depotCode + '\'' +
                ", distributorCode='" + distributorCode + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", timeStampPart='" + timeStampPart + '\'' +
                ", timeStampRetailer='" + timeStampRetailer + '\'' +
                ", timeStampOrder='" + timeStampOrder + '\'' +
                ", timeStampInvoice='" + timeStampInvoice + '\'' +
                ", timeStampStock='" + timeStampStock + '\'' +
                ", timeStampPJPSchedule='" + timeStampPJPSchedule + '\'' +
                ", timeStampProductAvg='" + timeStampProductAvg + '\'' +
                '}';
    }
}
