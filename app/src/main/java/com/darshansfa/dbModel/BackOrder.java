package com.darshansfa.dbModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Created by Nikhil on 10-07-2017.
 */

public class BackOrder extends SugarRecord {


    @Unique
    @SerializedName("part_number")
    @Expose
    private String partNumber;

    @SerializedName("part_description")
    @Expose
    private String partDescription;

    @SerializedName("part_quantity")
    @Expose
    private String partQty;

    @SerializedName("order_date")
    @Expose
    private String orderDate;

    @SerializedName("retailer_id")
    @Expose
    private String retailerId;

    @SerializedName("distributor_id")
    @Expose
    private String distributorId;


    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartDescription() {
        return partDescription;
    }

    public void setPartDescription(String partDescription) {
        this.partDescription = partDescription;
    }

    public String getPartQty() {
        return partQty;
    }

    public void setPartQty(String partQty) {
        this.partQty = partQty;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    @Override
    public String toString() {
        return "BackOrder{" +
                "partNumber='" + partNumber + '\'' +
                ", partDescription='" + partDescription + '\'' +
                ", partQty='" + partQty + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", retailerId='" + retailerId + '\'' +
                ", distributorId='" + distributorId + '\'' +
                '}';
    }
}
