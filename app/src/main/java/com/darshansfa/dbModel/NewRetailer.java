package com.darshansfa.dbModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * Created by Nikhil on 03-12-2015.
 */
public class NewRetailer extends SugarRecord {

    @SerializedName("dsr_id")
    @Expose
    private String dsrId = "";
    @SerializedName("shop_name")
    @Expose
    private String shopName = "";
    @SerializedName("first_name")
    @Expose
    private String firstName = "";
    @SerializedName("last_name")
    @Expose
    private String lastName = "";
    @SerializedName("email")
    @Expose
    private String email = "";
    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth = "";
    @SerializedName("shop_size")
    @Expose
    private String shopSize = "";
    @SerializedName("shop_number")
    @Expose
    private String shopNumber = "";
    @SerializedName("street")
    @Expose
    private String street = "";
    @SerializedName("locality")
    @Expose
    private String locality = "";
    @SerializedName("district")
    @Expose
    private String district = "";
    @SerializedName("state")
    @Expose
    private String state = "";
    @SerializedName("pincode")
    @Expose
    private String pincode = "";
    @SerializedName("near_dealer_name")
    @Expose
    private String nearDealerName = "";
    @SerializedName("mobile")
    @Expose
    private String mobile = "";
    @SerializedName("mobile_2")
    @Expose
    private String mobile2 = "";
    @SerializedName("total_sale")
    @Expose
    private String totalSale = "";
    @SerializedName("identification_no")
    @Expose
    private String identificationNo = "";
    @SerializedName("total_counter_sale")
    @Expose
    private String totalCounterSale = "";
    @SerializedName("latitude")
    @Expose
    private String latitude = "";
    @SerializedName("longitude")
    @Expose
    private String longitude = "";
    @SerializedName("shop_photo")
    @Expose
    private String shopPhoto = "";
    @SerializedName("image_url")
    @Expose
    private String imageUrl = "";
    @SerializedName("identity_url")
    @Expose
    private String identityUrl = "";
    @SerializedName("signature_url")
    @Expose
    private String signatureUrl = "";
    @SerializedName("distributor_id")
    @Expose
    private String distributorId = "";
    @SerializedName("gst_state_code")
    @Expose
    private String gst = "";
    @SerializedName("gst_image_url")
    @Expose
    private String gstImageUrl = "";
    @SerializedName("aadhar_card_number")
    @Expose
    private String aadharCardNumber = "";
    @SerializedName("aadhar_card_image")
    @Expose
    private String aadharCardImage = "";
    @SerializedName("pan_card_number")
    @Expose
    private String panCardNumber = "";
    @SerializedName("pan_card_image")
    @Expose
    private String panCardImage = "";
    @SerializedName("voter_id_number")
    @Expose
    private String voterIdNumber = "";
    @SerializedName("voter_id_image")
    @Expose
    private String voterIdImage = "";
    @SerializedName("driving_licence_number")
    @Expose
    private String drivingLicenceNumber = "";
    @SerializedName("driving_licence_image")
    @Expose
    private String drivingLicenceImage = "";
    @SerializedName("cst")
    @Expose
    private String cst = "";
    @SerializedName("tin")
    @Expose
    private String TINNumber = "";
    @SerializedName("landline")
    @Expose
    private String landline = "";
    @SerializedName("city")
    @Expose
    private String city = "";
    @SerializedName("retailer_sign_image_url")
    @Expose
    private String retailerSignImageUrl = "";
    @SerializedName("gst_status")
    @Expose
    private String gstStatus = "";


    public String getDsrId() {
        return dsrId;
    }

    public void setDsrId(String dsrId) {
        this.dsrId = dsrId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getShopSize() {
        return shopSize;
    }

    public void setShopSize(String shopSize) {
        this.shopSize = shopSize;
    }

    public String getShopNumber() {
        return shopNumber;
    }

    public void setShopNumber(String shopNumber) {
        this.shopNumber = shopNumber;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getNearDealerName() {
        return nearDealerName;
    }

    public void setNearDealerName(String nearDealerName) {
        this.nearDealerName = nearDealerName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile2() {
        return mobile2;
    }

    public void setMobile2(String mobile2) {
        this.mobile2 = mobile2;
    }

    public String getTotalSale() {
        return totalSale;
    }

    public void setTotalSale(String totalSale) {
        this.totalSale = totalSale;
    }

    public String getIdentificationNo() {
        return identificationNo;
    }

    public void setIdentificationNo(String identificationNo) {
        this.identificationNo = identificationNo;
    }

    public String getTotalCounterSale() {
        return totalCounterSale;
    }

    public void setTotalCounterSale(String totalCounterSale) {
        this.totalCounterSale = totalCounterSale;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getShopPhoto() {
        return shopPhoto;
    }

    public void setShopPhoto(String shopPhoto) {
        this.shopPhoto = shopPhoto;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIdentityUrl() {
        return identityUrl;
    }

    public void setIdentityUrl(String identityUrl) {
        this.identityUrl = identityUrl;
    }

    public String getSignatureUrl() {
        return signatureUrl;
    }

    public void setSignatureUrl(String signatureUrl) {
        this.signatureUrl = signatureUrl;
    }

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getGstImageUrl() {
        return gstImageUrl;
    }

    public void setGstImageUrl(String gstImageUrl) {
        this.gstImageUrl = gstImageUrl;
    }

    public String getAadharCardNumber() {
        return aadharCardNumber;
    }

    public void setAadharCardNumber(String aadharCardNumber) {
        this.aadharCardNumber = aadharCardNumber;
    }

    public String getAadharCardImage() {
        return aadharCardImage;
    }

    public void setAadharCardImage(String aadharCardImage) {
        this.aadharCardImage = aadharCardImage;
    }

    public String getPanCardNumber() {
        return panCardNumber;
    }

    public void setPanCardNumber(String panCardNumber) {
        this.panCardNumber = panCardNumber;
    }

    public String getPanCardImage() {
        return panCardImage;
    }

    public void setPanCardImage(String panCardImage) {
        this.panCardImage = panCardImage;
    }

    public String getVoterIdNumber() {
        return voterIdNumber;
    }

    public void setVoterIdNumber(String voterIdNumber) {
        this.voterIdNumber = voterIdNumber;
    }

    public String getVoterIdImage() {
        return voterIdImage;
    }

    public void setVoterIdImage(String voterIdImage) {
        this.voterIdImage = voterIdImage;
    }

    public String getDrivingLicenceNumber() {
        return drivingLicenceNumber;
    }

    public void setDrivingLicenceNumber(String drivingLicenceNumber) {
        this.drivingLicenceNumber = drivingLicenceNumber;
    }

    public String getDrivingLicenceImage() {
        return drivingLicenceImage;
    }

    public void setDrivingLicenceImage(String drivingLicenceImage) {
        this.drivingLicenceImage = drivingLicenceImage;
    }

    public String getCst() {
        return cst;
    }

    public void setCst(String cst) {
        this.cst = cst;
    }


    public String getTINNumber() {
        return TINNumber;
    }

    public void setTINNumber(String TINNumber) {
        this.TINNumber = TINNumber;
    }

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRetailerSignImageUrl() {
        return retailerSignImageUrl;
    }

    public void setRetailerSignImageUrl(String retailerSignImageUrl) {
        this.retailerSignImageUrl = retailerSignImageUrl;
    }

    public String getGstStatus() {
        return gstStatus;
    }

    public void setGstStatus(String gstStatus) {
        this.gstStatus = gstStatus;
    }

    @Override
    public String toString() {
        return "NewRetailer{" +
                "dsrId='" + dsrId + '\'' +
                ", shopName='" + shopName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", shopSize='" + shopSize + '\'' +
                ", shopNumber='" + shopNumber + '\'' +
                ", street='" + street + '\'' +
                ", locality='" + locality + '\'' +
                ", district='" + district + '\'' +
                ", state='" + state + '\'' +
                ", pincode='" + pincode + '\'' +
                ", nearDealerName='" + nearDealerName + '\'' +
                ", mobile='" + mobile + '\'' +
                ", mobile2='" + mobile2 + '\'' +
                ", totalSale='" + totalSale + '\'' +
                ", identificationNo='" + identificationNo + '\'' +
                ", totalCounterSale='" + totalCounterSale + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", shopPhoto='" + shopPhoto + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", identityUrl='" + identityUrl + '\'' +
                ", signatureUrl='" + signatureUrl + '\'' +
                ", distributorId='" + distributorId + '\'' +
                ", gst='" + gst + '\'' +
                ", gstImageUrl='" + gstImageUrl + '\'' +
                ", aadharCardNumber='" + aadharCardNumber + '\'' +
                ", aadharCardImage='" + aadharCardImage + '\'' +
                ", panCardNumber='" + panCardNumber + '\'' +
                ", panCardImage='" + panCardImage + '\'' +
                ", voterIdNumber='" + voterIdNumber + '\'' +
                ", voterIdImage='" + voterIdImage + '\'' +
                ", drivingLicenceNumber='" + drivingLicenceNumber + '\'' +
                ", drivingLicenceImage='" + drivingLicenceImage + '\'' +
                ", cst='" + cst + '\'' +
                ", TINNumber='" + TINNumber + '\'' +
                ", landline='" + landline + '\'' +
                ", city='" + city + '\'' +
                ", gst_status='" + gstStatus + '\'' +
                ", retailerSignImageUrl='" + retailerSignImageUrl + '\'' +
                '}';
    }
}
