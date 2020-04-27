package com.darshansfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.darshansfa.dbModel.Distributor;

/**
 * Created by Nikhil on 07-06-2017.
 */

public class DistributorResponse {
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("distributor")
    @Expose
    private List<Distributor> distributor = null;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Distributor> getDistributor() {
        return distributor;
    }

    public void setDistributor(List<Distributor> distributor) {
        this.distributor = distributor;
    }

    @Override
    public String toString() {
        return "DistributorResponse{" +
                "status=" + status +
                ", distributor=" + distributor +
                '}';
    }
}
