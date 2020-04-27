package com.darshansfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikhil on 26-09-2017.
 */

public class FocusedPartsResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("Product")
    @Expose
    private List<FocusedParts> product = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<FocusedParts> getProduct() {
        return product;
    }

    public void setProduct(List<FocusedParts> product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "FocusedPartsResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", product=" + product +
                '}';
    }
}
