package com.darshansfa.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Nikhil on 26-09-2017.
 */

public class FocusedParts {
    @SerializedName("part_number")
    @Expose
    private String partNumber;
    @SerializedName("locality_id")
    @Expose
    private Integer localityId;

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public Integer getLocalityId() {
        return localityId;
    }

    public void setLocalityId(Integer localityId) {
        this.localityId = localityId;
    }

    @Override
    public String toString() {
        return "FocusedParts{" +
                "partNumber='" + partNumber + '\'' +
                ", localityId=" + localityId +
                '}';
    }
}
