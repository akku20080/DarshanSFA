package com.darshansfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Nikhil on 10-11-2017.
 */

public class InvoiceIdResponse {
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("invoice_id_list")
    @Expose
    private List<String> invoiceIdList = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<String> getInvoiceIdList() {
        return invoiceIdList;
    }

    public void setInvoiceIdList(List<String> invoiceIdList) {
        this.invoiceIdList = invoiceIdList;
    }

    @Override
    public String toString() {
        return "InvoiceIdResponse{" +
                "status=" + status +
                ", invoiceIdList=" + invoiceIdList +
                '}';
    }
}
