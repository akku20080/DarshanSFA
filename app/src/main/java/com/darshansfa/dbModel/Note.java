package com.darshansfa.dbModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import java.io.Serializable;

import com.darshansfa.Utility.Constants;

/**
 * Created by Nikhil on 19-06-2017.
 */

public class Note extends SugarRecord implements Serializable {

    @SerializedName("retailerId")
    @Expose
    private String retailerId;
    @SerializedName("note_id")
    @Expose
    private String noteId;
    @SerializedName("remarks")
    @Expose
    private String remarks;
    @SerializedName("dsrId")
    @Expose
    private String dsrId;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("noteType")
    @Expose
    private String noteType = Constants.DAILY_NOTE;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("distributorId")
    @Expose
    private String distributorId;

    @SerializedName("pjpId")
    @Expose
    private String pjpId;
    @SerializedName("pjpPlanned")
    @Expose
    private String pjpPlanned;

    /* {
        "remaks": "cff",
        "note_id": 26,
        "dsrId": "500001",
        "content": "ff",
        "noteType": "daily",
        "time": "06-10 AM",
        "date": "04-10-2017",
        "distributorId": "PONY9590"
    }*/

    private String status = Constants.STATUS_DONE;

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return remarks;
    }

    public void setTitle(String title) {
        this.remarks = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }

    public String getDsrId() {
        return dsrId;
    }

    public void setDsrId(String dsrId) {
        this.dsrId = dsrId;
    }

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPjpId() {
        return pjpId;
    }

    public void setPjpId(String pjpId) {
        this.pjpId = pjpId;
    }

    public String getPjpPlanned() {
        return pjpPlanned;
    }

    public void setPjpPlanned(String pjpPlanned) {
        this.pjpPlanned = pjpPlanned;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "Note{" +
                "retailerId='" + retailerId + '\'' +
                ", noteId='" + noteId + '\'' +
                ", remarks='" + remarks + '\'' +
                ", dsrId='" + dsrId + '\'' +
                ", content='" + content + '\'' +
                ", noteType='" + noteType + '\'' +
                ", time='" + time + '\'' +
                ", date='" + date + '\'' +
                ", distributorId='" + distributorId + '\'' +
                ", pjpId='" + pjpId + '\'' +
                ", pjpPlanned='" + pjpPlanned + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
