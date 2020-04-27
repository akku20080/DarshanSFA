package com.darshansfa.dbModel;

import com.orm.SugarRecord;

/**
 * Created by Nikhil on 24-05-2017.
 */

public class Cart extends SugarRecord {
    private String partNumber;
    private String partName;
    private Double partOrderQty;
    private Double partSubTotal;
    private String partMRP;
    private String retailerId;
    private String depoId;
    private String dsrId;

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public Double getPartOrderQty() {
        return partOrderQty;
    }

    public void setPartOrderQty(Double partOrderQty) {
        this.partOrderQty = partOrderQty;
    }

    public Double getPartSubTotal() {
        return partSubTotal;
    }

    public void setPartSubTotal(Double partSubTotal) {
        this.partSubTotal = partSubTotal;
    }

    public String getPartMRP() {
        return partMRP;
    }

    public void setPartMRP(String partMRP) {
        this.partMRP = partMRP;
    }

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }

    public String getDepoId() {
        return depoId;
    }

    public void setDepoId(String depoId) {
        this.depoId = depoId;
    }

    public String getDsrId() {
        return dsrId;
    }

    public void setDsrId(String dsrId) {
        this.dsrId = dsrId;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "partNumber='" + partNumber + '\'' +
                ", partName='" + partName + '\'' +
                ", partOrderQty='" + partOrderQty + '\'' +
                ", partSubTotal='" + partSubTotal + '\'' +
                ", partMRP='" + partMRP + '\'' +
                ", retailerId='" + retailerId + '\'' +
                ", depoId='" + depoId + '\'' +
                ", dsrId='" + dsrId + '\'' +
                '}';
    }
}
