package com.darshansfa.dbModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikhil on 30-10-2017.
 */

public class SalesReturn extends SugarRecord {
    @SerializedName("retailer_id")
    @Expose
    private String retailerId;
    @SerializedName("retailer_name")
    @Expose
    private String retailerName;
    @SerializedName("dsr_id")
    @Expose
    private String dsrId;
    @SerializedName("distributor_id")
    @Expose
    private String distributorId;
    @SerializedName("invoice_id")
    @Expose
    private String invoiceId;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("product_number")
    @Expose
    private String productNumber;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("product_quantity")
    @Expose
    private String productQuantity;
    @SerializedName("excess_quantity")
    @Expose
    private String excessQuantity;
    @SerializedName("shortage_quantity")
    @Expose
    private String shortageQuantity;
    @SerializedName("reason_description")
    @Expose
    private String reasonDescription;
    @SerializedName("actual_product")
    @Expose
    private String actualProduct;
    @SerializedName("image_url")
    @Expose
    private List<String> imageUrl = new ArrayList<>();

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }

    public String getRetailerName() {
        return retailerName;
    }

    public void setRetailerName(String retailerName) {
        this.retailerName = retailerName;
    }

    public String getDsrId() {
        return dsrId;
    }

    public void setDsrId(String dsrId) {
        this.dsrId = dsrId;
    }

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getExcessQuantity() {
        return excessQuantity;
    }

    public void setExcessQuantity(String excessQuantity) {
        this.excessQuantity = excessQuantity;
    }

    public String getShortageQuantity() {
        return shortageQuantity;
    }

    public void setShortageQuantity(String shortageQuantity) {
        this.shortageQuantity = shortageQuantity;
    }

    public String getReasonDescription() {
        return reasonDescription;
    }

    public void setReasonDescription(String reasonDescription) {
        this.reasonDescription = reasonDescription;
    }

    public String getActualProduct() {
        return actualProduct;
    }

    public void setActualProduct(String actualProduct) {
        this.actualProduct = actualProduct;
    }

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "SalesReturn{" +
                "retailerId='" + retailerId + '\'' +
                ", retailerName='" + retailerName + '\'' +
                ", dsrId='" + dsrId + '\'' +
                ", distributorId='" + distributorId + '\'' +
                ", invoiceId='" + invoiceId + '\'' +
                ", date='" + date + '\'' +
                ", productNumber='" + productNumber + '\'' +
                ", reason='" + reason + '\'' +
                ", productQuantity='" + productQuantity + '\'' +
                ", excessQuantity='" + excessQuantity + '\'' +
                ", shortageQuantity='" + shortageQuantity + '\'' +
                ", reasonDescription='" + reasonDescription + '\'' +
                ", actualProduct='" + actualProduct + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
