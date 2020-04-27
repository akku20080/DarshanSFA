package com.darshansfa.dbModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by Nikhil on 28-10-2017.
 */

public class AdvancePayment extends SugarRecord implements Serializable {

    @SerializedName("advance_payment_id")
    @Expose
    private String advancePaymentId;
    @SerializedName("cheque_image")
    @Expose
    private String chequeImage;
    @SerializedName("transaction_amount")
    @Expose
    private String transactionAmount;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;
    @SerializedName("net_bank_name")
    @Expose
    private String netBankName;
    @SerializedName("cheque_bank")
    @Expose
    private String chequeBank;
    @SerializedName("cash_amount")
    @Expose
    private String cashAmount;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("invoice_id")
    @Expose
    private String invoiceId;

    @SerializedName("retailer_name")
    @Expose
    private String retailerName;
    @SerializedName("outstanding_amount")
    @Expose
    private String outstandingAmount;
    @SerializedName("transaction_type")
    @Expose
    private String transactionType;
    @SerializedName("retailer_id")
    @Expose
    private String retailerId;
    @SerializedName("collected_amount")
    @Expose
    private String collectedAmount;
    @SerializedName("collection_type")
    @Expose
    private String collectionType;
    @SerializedName("cheque_amount")
    @Expose
    private String chequeAmount;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("cheque_number")
    @Expose
    private String chequeNumber;
    @SerializedName("distributor_id")
    @Expose
    private String distributorId;
    @SerializedName("transaction_id")
    @Expose
    private String transactionId;
    @SerializedName("dsr_id")
    @Expose
    private String dsrId;
    private int localStatus = 0;

    public String getAdvancePaymentId() {
        return advancePaymentId;
    }

    public void setAdvancePaymentId(String advancePaymentId) {
        this.advancePaymentId = advancePaymentId;
    }

    public String getChequeImage() {
        return chequeImage;
    }

    public void setChequeImage(String chequeImage) {
        this.chequeImage = chequeImage;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getNetBankName() {
        return netBankName;
    }

    public void setNetBankName(String netBankName) {
        this.netBankName = netBankName;
    }

    public String getChequeBank() {
        return chequeBank;
    }

    public void setChequeBank(String chequeBank) {
        this.chequeBank = chequeBank;
    }

    public String getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(String cashAmount) {
        this.cashAmount = cashAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRetailerName() {
        return retailerName;
    }

    public void setRetailerName(String retailerName) {
        this.retailerName = retailerName;
    }

    public String getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(String outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }

    public String getCollectedAmount() {
        return collectedAmount;
    }

    public void setCollectedAmount(String collectedAmount) {
        this.collectedAmount = collectedAmount;
    }

    public String getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }

    public String getChequeAmount() {
        return chequeAmount;
    }

    public void setChequeAmount(String chequeAmount) {
        this.chequeAmount = chequeAmount;
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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getDsrId() {
        return dsrId;
    }

    public void setDsrId(String dsrId) {
        this.dsrId = dsrId;
    }

    public int getLocalStatus() {
        return localStatus;
    }

    public void setLocalStatus(int localStatus) {
        this.localStatus = localStatus;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    @Override
    public String toString() {
        return "AdvancePayment{" +
                "advancePaymentId='" + advancePaymentId + '\'' +
                ", chequeImage='" + chequeImage + '\'' +
                ", transactionAmount='" + transactionAmount + '\'' +
                ", paymentMode='" + paymentMode + '\'' +
                ", netBankName='" + netBankName + '\'' +
                ", chequeBank='" + chequeBank + '\'' +
                ", cashAmount='" + cashAmount + '\'' +
                ", orderId='" + orderId + '\'' +
                ", retailerName='" + retailerName + '\'' +
                ", outstandingAmount='" + outstandingAmount + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", retailerId='" + retailerId + '\'' +
                ", collectedAmount='" + collectedAmount + '\'' +
                ", collectionType='" + collectionType + '\'' +
                ", chequeAmount='" + chequeAmount + '\'' +
                ", date='" + date + '\'' +
                ", chequeNumber='" + chequeNumber + '\'' +
                ", distributorId='" + distributorId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", dsrId='" + dsrId + '\'' +
                ", localStatus=" + localStatus +
                '}';
    }
}
