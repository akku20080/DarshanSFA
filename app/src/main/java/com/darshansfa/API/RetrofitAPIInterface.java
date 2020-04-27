package com.darshansfa.API;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import com.darshansfa.Configuration.Configuration;
import com.darshansfa.Models.APIResponse;
import com.darshansfa.Models.DailyNoteStatusResponse;
import com.darshansfa.Models.DistributorResponse;
import com.darshansfa.Models.FocusedPartsResponse;
import com.darshansfa.Models.InvoiceIdResponse;
import com.darshansfa.Models.InvoiceResponse;
import com.darshansfa.Models.OrdersResponse;
import com.darshansfa.Models.ReportResponse;
import com.darshansfa.Models.RetailerResponse;
import com.darshansfa.dbModel.Note;
import com.darshansfa.dbModel.PJPSchedule;
import com.darshansfa.dbModel.SalesReturn;
import com.darshansfa.dbModel.Stock;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Created by Nikhil on 18-05-2016.
 */
public interface RetrofitAPIInterface {

    @POST("/" + Configuration.BRAND_NAME + "/api-token-auth/")
    Call<JsonObject> signIn(@Body JsonObject loginDetails);

    @GET("/" + Configuration.BRAND_NAME + "/get-aws-info/")
    Call<JsonObject> getAWSInfo();

    @GET("/" + Configuration.BRAND_NAME + "/get_dsr_distributor/{dsr_code}/")
    Call<DistributorResponse> getDistributor(@Path("dsr_code") String dsrCode);

//    @GET("/cv/get_parts/?")
//    Call<List<Product>> getProducts();

    @GET("/" + Configuration.BRAND_NAME + "/get_parts/?")
    Call<APIResponse> getProducts();

    @GET("/" + Configuration.BRAND_NAME + "/get_parts/?")
    Call<APIResponse> getProducts(@Query("modified_since") String modifiedSince);

    @GET("/" + Configuration.BRAND_NAME + "/get_parts/?")
    Call<JsonArray> getProductsJ();

    @GET("/" + Configuration.BRAND_NAME + "/focused_parts/{distributor_id}/{dsr_id}/")
    Call<FocusedPartsResponse> getFocusedParts(@Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId);

    @GET("/" + Configuration.BRAND_NAME + "/get_retailers/{distributor_id}/")
    Call<APIResponse> getRetailers(@Path("distributor_id") String distributorId, @Query("modified_since") String modifiedSince);

    @GET("/" + Configuration.BRAND_NAME + "/get_orders/{distributor_id}/{dsr_id}/")
    Call<APIResponse> getOrders(@Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId);


    @GET("/" + Configuration.BRAND_NAME + "/get_orders/{distributor_id}/{dsr_id}/")
    Call<APIResponse> getOrdersForDate(@Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId, @Query("date") String date);


    @GET("/" + Configuration.BRAND_NAME + "/retailer-order/{retailer_id}/")
    Call<OrdersResponse> getRetailerOrder(@Path("retailer_id") String retailerId);


    @GET("/" + Configuration.BRAND_NAME + "/get_dsr_outstanding/{distributor_id}/{dsr_id}/")
    Call<InvoiceResponse> getInvoices(@Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId, @Query("modified_since") String modifiedSince);


    @GET("/" + Configuration.BRAND_NAME + "/get_pjp_locality/{distributor_id}/{dsr_id}/")
    Call<List<PJPSchedule>> getSchedule(@Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId);

    @GET("/" + Configuration.BRAND_NAME + "/get_pjp_date/{distributor_id}/{dsr_id}/{date}/")
    Call<List<PJPSchedule>> getScheduleForDate(@Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId, @Path("date") String date);

    //@GET("/get_stock/{dsr_code}/")
    //Call<List<Stock>> getStock(@Path("dsr_code") String dsrCode);

    @GET("/" + Configuration.BRAND_NAME + "/get_stock/{dsr_code}/")
    Call<List<Stock>> getStock(@Path("dsr_code") String dsrCode);

    @POST("/" + Configuration.BRAND_NAME + "/forgot-password-change/")
    Call<JsonObject> forgotPassword(@Body JsonObject username);

    @POST("/" + Configuration.BRAND_NAME + "/password-change/")
    Call<JsonObject> changePassord(@Body JsonObject username);

    @POST("/" + Configuration.BRAND_NAME + "/pjp_locality/")
    Call<JsonObject> updatePJPSchedule(@Body JsonArray jsonArray);

    @POST("/" + Configuration.BRAND_NAME + "/pjp_visit/")
    Call<JsonObject> updatePJPStartStop(@Body JsonObject jsonArray);

    @GET("/" + Configuration.BRAND_NAME + "/pjp_visit_status/{distributor_id}/{dsr_id}/")
    Call<JsonObject> getPJPVisitStatus(@Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId);

    @POST("/" + Configuration.BRAND_NAME + "/retailer_latlng/")
    Call<JsonObject> retailerUpdateLatLong(@Body JsonObject jsonArray);

    @POST("/" + Configuration.BRAND_NAME + "/place_order/{distributor_id}/{dsr_id}/")
    Call<JsonObject> placeOrder(@Body JsonArray jsonArray, @Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId);

    @POST("/" + Configuration.BRAND_NAME + "/advanced-payment/{distributor_id}/{dsr_id}/")
    Call<JsonObject> updatePayment(@Body JsonObject jsonArray, @Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId);

    @GET("/" + Configuration.BRAND_NAME + "/advanced-payment/{distributor_id}/{dsr_id}/")
    Call<APIResponse> advancePayment(@Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId, @Query("date") String date);

    @POST("/" + Configuration.BRAND_NAME + "/collections/{distributor_id}/{dsr_id}/")
    Call<JsonObject> uploadCollection(@Body JsonObject jsonObject, @Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId);


    @GET("/" + Configuration.BRAND_NAME + "/collections/{distributor_id}/{dsr_id}/")
    Call<JsonObject> getCollection(@Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId, @Query("date") String date);


    @POST("/" + Configuration.BRAND_NAME + "/{path}/{distributor_id}/{dsr_id}/")
    Call<JsonObject> uploadNote(@Body JsonArray jsonArray, @Path("path") String path, @Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId);

    @POST("/" + Configuration.BRAND_NAME + "/{path}/{distributor_id}/{dsr_id}/{note_id}/")
    Call<JsonObject> uploadNote(@Body JsonArray jsonArray, @Path("path") String path, @Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId, @Path("note_id") String noteId);

    @GET("/" + Configuration.BRAND_NAME + "/{path}/{distributor_id}/{dsr_id}/")
    Call<List<Note>> getNote(@Path("path") String path, @Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId);


    @POST("/" + Configuration.BRAND_NAME + "/report/dashboard_report/{distributor_id}/{dsr_id}/")
    Call<ReportResponse> dsrReport(@Body JsonObject jsonObject, @Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId);


    @POST("/" + Configuration.BRAND_NAME + "/report/top_retailers/{distributor_id}/{dsr_id}/")
    Call<ReportResponse> topRetailers(@Body JsonObject jsonObject, @Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId);

    @POST("/" + Configuration.BRAND_NAME + "/report/zero_billed_retailers/{distributor_id}/{dsr_id}/")
    Call<ReportResponse> zeroBilledRetailers(@Body JsonObject jsonObject, @Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId);

    @POST("/" + Configuration.BRAND_NAME + "/report/new_retailer/{distributor_id}/{dsr_id}/")
    Call<ReportResponse> newRetailer(@Body JsonObject jsonObject, @Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId);


    @POST("/" + Configuration.BRAND_NAME + "/report/billed_products/{distributor_id}/{dsr_id}/")
    Call<ReportResponse> billedProducts(@Body JsonObject jsonObject, @Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId);


    @GET("/" + Configuration.BRAND_NAME + "/address-from-pincode/{pincode}/{distributor_id}/{dsr_id}/")
    Call<JsonObject> localityFromPincode(@Path("pincode") String pincode, @Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId);


    @POST("/" + Configuration.BRAND_NAME + "/add-retailer/{distributor_id}/{dsr_id}/")
    Call<JsonObject> addRetailer(@Body JsonObject jsonObject, @Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId);


    @POST("/" + Configuration.BRAND_NAME + "/get-or-update-retailer/{distributor_id}/{dsr_id}/{retailer_id}/")
    Call<JsonObject> updateRetailer(@Body JsonObject jsonObject, @Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId, @Path("retailer_id") String retailerId);

    ///pony/add-retailer/dist_id/dsr_code/

//    /pony/address-from-pincode/<pincode>/<distributor_id>/<dsr_id>/

    @GET("/" + Configuration.BRAND_NAME + "/get-or-update-retailer/{distributor_id}/{dsr_id}/{retailer_id}/")
    Call<RetailerResponse> getRetailer(@Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId, @Path("retailer_id") String retailerId);


    @GET("/" + Configuration.BRAND_NAME + "/daily_notes_status/{distributor_id}/{dsr_id}/")
    Call<DailyNoteStatusResponse> checkDailyNote(@Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId);


    @POST("/" + Configuration.BRAND_NAME + "/dsr_tracking/{distributor_id}/{dsr_id}/")
    Call<JsonObject> updateDsrCurrentLocation(@Body JsonObject jsonObject, @Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId);
    ///pony/daily_notes_status/dist_id/dsr_code/


    @POST("/" + Configuration.BRAND_NAME + "/sales_return/{distributor_id}/{dsr_id}/")
    Call<JsonObject> postSalesReturn(@Body JsonObject jsonObject, @Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId);


    @GET("/" + Configuration.BRAND_NAME + "/sales_return/{distributor_id}/{dsr_id}/")
    Call<List<SalesReturn>> getSalesReturnForRetailer(@Path("distributor_id") String distributorId, @Path("dsr_id") String dsrId, @Query("retailer_id") String id);


    @GET("/" + Configuration.BRAND_NAME + "/get-invoice-from-sales-return/{retailer_id}/{part_number}/")
    Call<InvoiceIdResponse> getInvoiceIdsForSaleReturn(@Path("retailer_id") String retailerId, @Path("part_number") String partsNumber);

}
