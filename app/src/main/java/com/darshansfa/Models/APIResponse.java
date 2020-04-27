package com.darshansfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import com.darshansfa.dbModel.AdvancePayment;
import com.darshansfa.dbModel.Orders;
import com.darshansfa.dbModel.Product;
import com.darshansfa.dbModel.Retailer;

/**
 * Created by Nikhil on 20-11-2017.
 */

public class APIResponse {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("parts_list")
    @Expose
    private List<Product> productList = new ArrayList<>();

    @SerializedName("retailer_list")
    @Expose
    private List<Retailer> retailerList = new ArrayList<>();

    @SerializedName("orders_list")
    @Expose
    private List<Orders> ordersList = new ArrayList<>();

    @SerializedName("advance_payment_details_list")
    @Expose
    private List<AdvancePayment> advancePaymentList = new ArrayList<>();




//

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

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public List<Retailer> getRetailerList() {
        return retailerList;
    }

    public void setRetailerList(List<Retailer> retailerList) {
        this.retailerList = retailerList;
    }

    public List<Orders> getOrdersList() {
        return ordersList;
    }

    public void setOrdersList(List<Orders> ordersList) {
        this.ordersList = ordersList;
    }


    public List<AdvancePayment> getAdvancePaymentList() {
        return advancePaymentList;
    }

    public void setAdvancePaymentList(List<AdvancePayment> advancePaymentList) {
        this.advancePaymentList = advancePaymentList;
    }

    @Override
    public String toString() {
        return "APIResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", productList=" + productList +
                '}';
    }
}
