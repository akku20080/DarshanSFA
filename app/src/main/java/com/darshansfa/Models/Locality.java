package com.darshansfa.Models;

/**
 * Created by Nikhil on 31-05-2017.
 */

public class Locality {
    private String localityId;
    private String localityName;
    private boolean isSelected;

    public String getLocalityId() {
        return localityId;
    }

    public void setLocalityId(String localityId) {
        this.localityId = localityId;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    @Override
    public String toString() {
        return "Locality{" +
                "localityId='" + localityId + '\'' +
                ", localityName='" + localityName + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
