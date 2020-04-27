package com.darshansfa.dbModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * Created by Nikhil on 19-05-2017.
 */

public class OrderPart extends SugarRecord {

    public OrderPart(){

    }

    @SerializedName("order_id")
    @Expose
    private String orderId;

    @SerializedName("part_id")
    @Expose
    private String partId;

    @SerializedName("part_name")
    @Expose
    private String partName;

    @SerializedName("quantity")
    @Expose
    private Double quantity;

    @SerializedName("delivered_quantity")
    @Expose
    private Integer deliveredQuantity;

    @SerializedName("back_order_quantity")
    @Expose
    private Integer back_order_quantity;


    @SerializedName("shipped_quantity")
    @Expose
    private Integer shipped_quantity;

    @SerializedName("line_total")
    @Expose
    private Double lineTotal;

    @SerializedName("company_price")
    @Expose
    private Double companyPrice;


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPartId() {
        return partId;
    }

    public void setPartId(String partId) {
        this.partId = partId;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Integer getBack_order_quantity() {
        return back_order_quantity;
    }

    public void setBack_order_quantity(Integer back_order_quantity) {
        this.back_order_quantity = back_order_quantity;
    }

    public Integer getShipped_quantity() {
        return shipped_quantity;
    }

    public void setShipped_quantity(Integer shipped_quantity) {
        this.shipped_quantity = shipped_quantity;
    }

    public Integer getDeliveredQuantity() {
        return deliveredQuantity;
    }

    public void setDeliveredQuantity(Integer deliveredQuantity) {
        this.deliveredQuantity = deliveredQuantity;
    }


    public Double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(Double lineTotal) {
        this.lineTotal = lineTotal;
    }


    public Double getCompanyPrice() {
        return companyPrice;
    }

    public void setCompanyPrice(Double companyPrice) {
        this.companyPrice = companyPrice;
    }

    @Override
    public String toString() {
        return "OrderPart{" +
                "orderId='" + orderId + '\'' +
                ", partId='" + partId + '\'' +
                ", partName='" + partName + '\'' +
                ", quantity=" + quantity +
                ", shipped_quantity=" + shipped_quantity +
                ", back_order_quantity=" + back_order_quantity +
                ", deliveredQuantity=" + deliveredQuantity +
                ", lineTotal=" + lineTotal +
                ", companyPrice=" + companyPrice +
                '}';
    }
}
