package com.darshansfa.dbModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * Created by Nikhil on 28-10-2017.
 */

public class ChequeDetail extends SugarRecord {

    @SerializedName("advance_payment_id")
    @Expose
    private String advancePaymentId;
    @SerializedName("cheque_amount")
    @Expose
    private String chequeAmount;
    @SerializedName("cheque_bank")
    @Expose
    private String chequeBank;
    @SerializedName("cheque_number")
    @Expose
    private String chequeNumber;
    @SerializedName("cheque_image")
    @Expose
    private String chequeImage;

    public String getAdvancePaymentId() {
        return advancePaymentId;
    }

    public void setAdvancePaymentId(String advancePaymentId) {
        this.advancePaymentId = advancePaymentId;
    }

    public String getChequeAmount() {
        return chequeAmount;
    }

    public void setChequeAmount(String chequeAmount) {
        this.chequeAmount = chequeAmount;
    }

    public String getChequeBank() {
        return chequeBank;
    }

    public void setChequeBank(String chequeBank) {
        this.chequeBank = chequeBank;
    }

    public String getChequeNumber() {
        return chequeNumber;
    }

    public void setChequeNumber(String chequeNumber) {
        this.chequeNumber = chequeNumber;
    }

    public String getChequeImage() {
        return chequeImage;
    }

    public void setChequeImage(String chequeImage) {
        this.chequeImage = chequeImage;
    }

    @Override
    public String toString() {
        return "ChequeDetail{" +
                "advPaymentId='" + advancePaymentId + '\'' +
                ", chequeAmount='" + chequeAmount + '\'' +
                ", chequeBank='" + chequeBank + '\'' +
                ", chequeNumber='" + chequeNumber + '\'' +
                ", chequeImage='" + chequeImage + '\'' +
                '}';
    }
}
