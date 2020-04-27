package com.darshansfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.darshansfa.dbModel.NewRetailer;
import com.darshansfa.dbModel.Product;

/**
 * Created by Nikhil on 18-08-2017.
 */

public class RetailerResponse {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("report_details")
    @Expose
    private Report reportDetails;

    @SerializedName("retailer_dict")
    @Expose
    private NewRetailer retailer;

    @SerializedName("parts_list")
    @Expose
    private List<Product> productList;

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

    public NewRetailer getRetailer() {
        return retailer;
    }

    public void setRetailer(NewRetailer retailer) {
        this.retailer = retailer;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    @Override
    public String toString() {
        return "RetailerResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", reportDetails=" + reportDetails +
                ", retailer=" + retailer +
                '}';
    }
}
