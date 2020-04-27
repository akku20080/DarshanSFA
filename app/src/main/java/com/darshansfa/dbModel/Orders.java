package com.darshansfa.dbModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Unique;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikhil on 19-05-2017.
 */

public class Orders extends SugarRecord {

    public Orders(){

    }

    @Unique
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("retailer_id")
    @Expose
    private String retailerId;
    @SerializedName("distributor_Id")
    @Expose
    private String distributorId;
    @SerializedName("dsr_id")
    @Expose
    private String dsrId;
    @SerializedName("order_date")
    @Expose
    private String orderDate;
    @SerializedName("datetime")
    @Expose
    private String datetime;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("total_quantity")
    @Expose
    private Integer totalQuantity;
    @Ignore
    @SerializedName("order_details")
    @Expose
    private List<OrderPart> orderDetails = new ArrayList<>();

    private String latitude, longitude;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public List<OrderPart> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderPart> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    public String getDsrId() {
        return dsrId;
    }

    public void setDsrId(String dsrId) {
        this.dsrId = dsrId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "orderId='" + orderId + '\'' +
                ", retailerId='" + retailerId + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", datetime='" + datetime + '\'' +
                ", status='" + status + '\'' +
                ", amount=" + amount +
                ", totalQuantity=" + totalQuantity +
                ", orderDetails=" + orderDetails +
                '}';
    }
}
