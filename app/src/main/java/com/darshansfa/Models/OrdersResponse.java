package com.darshansfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.darshansfa.dbModel.Orders;

/**
 * Created by Nikhil on 10-11-2017.
 */

public class OrdersResponse {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("orders_list")
    @Expose
    private List<Orders> ordersList = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Orders> getOrdersList() {
        return ordersList;
    }

    public void setOrdersList(List<Orders> ordersList) {
        this.ordersList = ordersList;
    }

    @Override
    public String toString() {
        return "OrdersResponse{" +
                "status=" + status +
                ", ordersList=" + ordersList +
                '}';
    }
}
