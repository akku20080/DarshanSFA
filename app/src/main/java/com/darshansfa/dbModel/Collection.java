package com.darshansfa.dbModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * Created by Nikhil on 27-06-2017.
 */

public class Collection extends SugarRecord {

    @SerializedName("transaction_amount")
    @Expose
    private Object transactionAmount;
    @SerializedName("cash_amount")
    @Expose
    private Double cashAmount;
    @SerializedName("cheque_bank")
    @Expose
    private String chequeBank;
    @SerializedName("cheque_image")
    @Expose
    private String chequeImage;
    @SerializedName("net_bank_name")
    @Expose
    private Object netBankName;
    @SerializedName("invoice_id")
    @Expose
    private String invoiceId;
    @SerializedName("retailer_name")
    @Expose
    private String retailerName;
    @SerializedName("outstanding_amount")
    @Expose
    private Double outstandingAmount;
    @SerializedName("total_collected_amount")
    @Expose
    private Double totalCollectedAmount;
    @SerializedName("retailer_id")
    @Expose
    private String retailerId;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;
    @SerializedName("cheque_amount")
    @Expose
    private Double chequeAmount;
    @SerializedName("invoice_amount")
    @Expose
    private Double invoiceAmount;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("cheque_number")
    @Expose
    private String chequeNumber;
    @SerializedName("distributor_id")
    @Expose
    private String distributorId;
    @SerializedName("transaction_type")
    @Expose
    private String transactionType;
    @SerializedName("transaction_id")
    @Expose
    private Object transactionId;
    @SerializedName("dsr_id")
    @Expose
    private String dsrId;

    private String status;

    public Object getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Object transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public Double getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(Double cashAmount) {
        this.cashAmount = cashAmount;
    }

    public String getChequeBank() {
        return chequeBank;
    }

    public void setChequeBank(String chequeBank) {
        this.chequeBank = chequeBank;
    }

    public String getChequeImage() {
        return chequeImage;
    }

    public void setChequeImage(String chequeImage) {
        this.chequeImage = chequeImage;
    }

    public Object getNetBankName() {
        return netBankName;
    }

    public void setNetBankName(Object netBankName) {
        this.netBankName = netBankName;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getRetailerName() {
        return retailerName;
    }

    public void setRetailerName(String retailerName) {
        this.retailerName = retailerName;
    }

    public Double getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(Double outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public Double getTotalCollectedAmount() {
        return totalCollectedAmount;
    }

    public void setTotalCollectedAmount(Double totalCollectedAmount) {
        this.totalCollectedAmount = totalCollectedAmount;
    }

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Double getChequeAmount() {
        return chequeAmount;
    }

    public void setChequeAmount(Double chequeAmount) {
        this.chequeAmount = chequeAmount;
    }

    public Double getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(Double invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getChequeNumber() {
        return chequeNumber;
    }

    public void setChequeNumber(String chequeNumber) {
        this.chequeNumber = chequeNumber;
    }

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Object getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Object transactionId) {
        this.transactionId = transactionId;
    }

    public String getDsrId() {
        return dsrId;
    }

    public void setDsrId(String dsrId) {
        this.dsrId = dsrId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Collection{" +
                "transactionAmount=" + transactionAmount +
                ", cashAmount=" + cashAmount +
                ", chequeBank='" + chequeBank + '\'' +
                ", chequeImage='" + chequeImage + '\'' +
                ", netBankName=" + netBankName +
                ", invoiceId='" + invoiceId + '\'' +
                ", retailerName='" + retailerName + '\'' +
                ", outstandingAmount=" + outstandingAmount +
                ", totalCollectedAmount=" + totalCollectedAmount +
                ", retailerId='" + retailerId + '\'' +
                ", paymentMode='" + paymentMode + '\'' +
                ", chequeAmount=" + chequeAmount +
                ", invoiceAmount=" + invoiceAmount +
                ", date='" + date + '\'' +
                ", chequeNumber='" + chequeNumber + '\'' +
                ", distributorId='" + distributorId + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", transactionId=" + transactionId +
                ", dsrId='" + dsrId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
