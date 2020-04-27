package com.darshansfa.dbModel;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Unique;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikhil on 15-05-2017.
 */

public class Product extends SugarRecord {


    @SerializedName("mup")
    @Expose
    private String mup;
    @SerializedName("company_price")
    @Expose
    private String companyPrice;
    @SerializedName("datetime")
    @Expose
    private String datetime;
    @Ignore
    @SerializedName("associated_categories_str")
    @Expose
    private List<String> associatedCategoriesStr = new ArrayList<>();
    @Unique
    @SerializedName("part_number")
    @Expose
    private String partNumber;
    @SerializedName("part_name")
    @Expose
    private String partName;
    @SerializedName("units")
    @Expose
    private String units;
    @SerializedName("part_available_quantity")
    @Expose
    private String partAvailableQuantity;
    @SerializedName("part_products")
    @Expose
    private String partProducts;
    @SerializedName("mrp")
    @Expose
    private String mrp;
    @SerializedName("part_category")
    @Expose
    private String partCategory;
    @SerializedName("hsn_code")
    @Expose
    private String hsnCode;


    private String associateProductsIds;
    private String stock = "NA";
    private String transitStock = "NA";
    private String areaAvg;
    private String retailerAvg;
    private Double orderQty;
    private Double subTotal;

    @SerializedName("focused_part")
    @Expose
    private boolean isFocusedPart;
    @SerializedName("locality_id")
    @Expose
    private String localityId;


    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public List<String> getAssociatedCategoriesStr() {
        return associatedCategoriesStr;
    }

    public void setAssociatedCategoriesStr(List<String> associatedCategoriesStr) {
        this.associatedCategoriesStr = associatedCategoriesStr;
        if (associatedCategoriesStr == null) {
            associateProductsIds = null;
            return;
        }
        String ids = "";
        for (int j = 0; j < associatedCategoriesStr.size(); j++) {

            if (j == (associatedCategoriesStr.size() - 1)) {
                ids = ids + "'" + associatedCategoriesStr.get(j) + "'";
            } else {
                ids = ids + "'" + associatedCategoriesStr.get(j) + "',";
            }
            Log.e("Part Object ", "Part setAssociatePartsIds  ---------- " + ids);
        }
        associateProductsIds = ids;
    }

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

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getPartAvailableQuantity() {
        return partAvailableQuantity;
    }

    public void setPartAvailableQuantity(String partAvailableQuantity) {
        this.partAvailableQuantity = partAvailableQuantity;
    }

    public String getPartProducts() {
        return partProducts;
    }

    public void setPartProducts(String partProducts) {
        this.partProducts = partProducts;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getPartCategory() {
        return partCategory;
    }

    public void setPartCategory(String partCategory) {
        this.partCategory = partCategory;
    }

    public String getAssociateProductsIds() {
        return associateProductsIds;
    }

    public void setAssociateProductsIds(String associateProductsIds) {
        this.associateProductsIds = associateProductsIds;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getTransitStock() {
        return transitStock;
    }

    public void setTransitStock(String transitStock) {
        this.transitStock = transitStock;
    }


    public String getAreaAvg() {
        return areaAvg;
    }

    public void setAreaAvg(String areaAvg) {
        this.areaAvg = areaAvg;
    }

    public String getRetailerAvg() {
        return retailerAvg;
    }

    public void setRetailerAvg(String retailerAvg) {
        this.retailerAvg = retailerAvg;
    }

    public Double getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(Double orderQty) {
        this.orderQty = orderQty;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public boolean isFocusedPart() {
        return isFocusedPart;
    }

    public void setFocusedPart(boolean focusedPart) {
        isFocusedPart = focusedPart;
    }

    public String getLocalityId() {
        return localityId;
    }

    public void setLocalityId(String localityId) {
        this.localityId = localityId;
    }

    public String getHsnCode() {
        return hsnCode;
    }

    public void setHsnCode(String hsnCode) {
        this.hsnCode = hsnCode;
    }

    public String getMup() {
        return mup;
    }

    public void setMup(String mup) {
        this.mup = mup;
    }

    public String getCompanyPrice() {
        return companyPrice;
    }

    public void setCompanyPrice(String companyPrice) {
        this.companyPrice = companyPrice;
    }

    @Override
    public String toString() {
        return "Product{" +
                "mup='" + mup + '\'' +
                ", companyPrice='" + companyPrice + '\'' +
                ", datetime='" + datetime + '\'' +
                ", associatedCategoriesStr=" + associatedCategoriesStr +
                ", partNumber='" + partNumber + '\'' +
                ", partName='" + partName + '\'' +
                ", units='" + units + '\'' +
                ", partAvailableQuantity='" + partAvailableQuantity + '\'' +
                ", partProducts='" + partProducts + '\'' +
                ", mrp='" + mrp + '\'' +
                ", partCategory='" + partCategory + '\'' +
                ", hsnCode='" + hsnCode + '\'' +
                ", associateProductsIds='" + associateProductsIds + '\'' +
                ", stock='" + stock + '\'' +
                ", transitStock='" + transitStock + '\'' +
                ", areaAvg='" + areaAvg + '\'' +
                ", retailerAvg='" + retailerAvg + '\'' +
                ", orderQty=" + orderQty +
                ", subTotal=" + subTotal +
                ", isFocusedPart=" + isFocusedPart +
                ", localityId='" + localityId + '\'' +
                '}';
    }

    public Cart getCartItem() {
        Cart cart = new Cart();
        cart.setPartNumber(this.partNumber);
        cart.setPartName(this.partName);
        cart.setPartMRP(this.companyPrice);
        cart.setPartSubTotal(this.subTotal);
        cart.setPartOrderQty(this.orderQty);
        return cart;
    }
}
