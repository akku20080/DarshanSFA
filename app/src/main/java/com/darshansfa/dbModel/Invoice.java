package com.darshansfa.dbModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by Nikhil on 15-05-2017.
 */

public class Invoice extends SugarRecord {

    @Unique
    @SerializedName("invoice_id")
    @Expose
    private String invoiceId;
    @SerializedName("total_amount")
    @Expose
    private String totalAmount;
    @SerializedName("collected_amount")
    @Expose
    private String collectedAmount;
    @SerializedName("invoice_date")
    @Expose
    private String invoiceDate;
    @SerializedName("retailer_id")
    @Expose
    private String retailerId;
    @SerializedName("period")
    @Expose
    private String period;
    @SerializedName("datetime")
    @Expose
    private String datetime;

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCollectedAmount() {
        return collectedAmount;
    }

    public void setCollectedAmount(String collectedAmount) {
        this.collectedAmount = collectedAmount;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceId='" + invoiceId + '\'' +
                ", totalAmount='" + totalAmount + '\'' +
                ", collectedAmount='" + collectedAmount + '\'' +
                ", invoiceDate='" + invoiceDate + '\'' +
                ", retailerId='" + retailerId + '\'' +
                ", period='" + period + '\'' +
                ", datetime='" + datetime + '\'' +
                '}';
    }
}
