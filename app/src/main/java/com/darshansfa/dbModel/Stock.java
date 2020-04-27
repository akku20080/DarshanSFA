package com.darshansfa.dbModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * Created by Nikhil on 02-06-2017.
 */

public class Stock extends SugarRecord {
    @SerializedName("part_number")
    @Expose
    private String partNumber;
    @SerializedName("transit_stock")
    @Expose
    private String transitStock;
    @SerializedName("mrp")
    @Expose
    private String mrp;
    @SerializedName("part_available_quantity")
    @Expose
    private String partAvailableQuantity;
    @SerializedName("datetime")
    @Expose
    private String datetime;

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getTransitStock() {
        return transitStock;
    }

    public void setTransitStock(String transitStock) {
        this.transitStock = transitStock;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getPartAvailableQuantity() {
        return partAvailableQuantity;
    }

    public void setPartAvailableQuantity(String partAvailableQuantity) {
        this.partAvailableQuantity = partAvailableQuantity;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "partNumber='" + partNumber + '\'' +
                ", transitStock=" + transitStock +
                ", mrp='" + mrp + '\'' +
                ", partAvailableQuantity=" + partAvailableQuantity +
                ", datetime='" + datetime + '\'' +
                '}';
    }
}
