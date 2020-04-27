package com.darshansfa.dbModel;

/**
 * Created by Nikhil on 21-06-2017.
 */

public class Cheque {
    private String amount;
    private String bank;
    private String number;
    private String imageUrl;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Cheque{" +
                "amount='" + amount + '\'' +
                ", bank='" + bank + '\'' +
                ", number='" + number + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
