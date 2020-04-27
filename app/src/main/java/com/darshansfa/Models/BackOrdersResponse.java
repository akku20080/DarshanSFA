package com.darshansfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.darshansfa.dbModel.BackOrder;

/**
 * Created by Admin on 6/4/2018.
 */

public class BackOrdersResponse {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("orders_list")
    @Expose
    private List<BackOrder> ordersList = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<BackOrder> getOrdersList() {
        return ordersList;
    }

    public void setOrdersList(List<BackOrder> ordersList) {
        this.ordersList = ordersList;
    }

    @Override
    public String toString() {
        return "BackOrdersResponse{" +
                "status=" + status +
                ", ordersList=" + ordersList +
                '}';
    }
}
