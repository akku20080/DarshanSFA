package com.darshansfa.API;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.darshansfa.Models.APIResponse;
import com.darshansfa.Models.FocusedParts;
import com.darshansfa.Models.FocusedPartsResponse;
import com.darshansfa.Models.InvoiceResponse;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.DBUtil;
import com.darshansfa.Utility.NotifyListener;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.dbModel.Distributor;
import com.darshansfa.dbModel.Invoice;
import com.darshansfa.dbModel.Orders;
import com.darshansfa.dbModel.PJPSchedule;
import com.darshansfa.dbModel.Product;
import com.darshansfa.dbModel.Retailer;
import com.darshansfa.dbModel.Stock;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nikhil on 15-05-2017.
 */

public class APIUtil {
    private NotifyListener listener;
    private Context context;

    public APIUtil(int api, Context context, NotifyListener listener) {
        this.listener = listener;
        this.context = context;
        switch (api) {
            case Constants.API.PRODUCT:
                startProductDownload();
                break;
            case Constants.API.FOCUS_PARTS_DSR:
                downloadFocusedPart();
                break;
            case Constants.API.RETAILERS:
                startRetailerDownload();
                break;
            case Constants.API.STOCK:
                downloadStock();
                break;
            case Constants.API.ORDERS:
                startOrdersDownload();
                break;
            case Constants.API.OUTSTANDING:
                startInvoiceDownload();
                break;
            case Constants.API.PJP:
                startPJPDownload();
                break;
            case Constants.API.COLLECTION:
                downloadCollection();
                break;
        }
    }

    private void downloadFocusedPart() {
        Call<FocusedPartsResponse> call = RetrofitAPI.getInstance().getApi().getFocusedParts(PreferencesManger.getStringFields(context, Constants.Pref.DEPOT_CODE)
                , PreferencesManger.getStringFields(context, Constants.Pref.DSR_ID));
        call.enqueue(new Callback<FocusedPartsResponse>() {
            @Override
            public void onResponse(Call<FocusedPartsResponse> call, Response<FocusedPartsResponse> response) {
                try {
                    Log.e("Product", "-------------------------------- P - " + response.body());
                    FocusedPartsResponse focusedPartsResponse = response.body();

                    List<FocusedParts> list = focusedPartsResponse.getProduct();
                    for (int i = 0; i < list.size(); i++) {
                        try {
                            Product product = Product.find(Product.class, "part_number = ?", new String[]{list.get(i).getPartNumber()}).get(0);
                            product.setFocusedPart(true);
                            product.setLocalityId("" + list.get(i).getLocalityId());
                            product.save();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    listener.onCompleted("");
                } catch (Exception e) {
                    Log.e("Product", "Error -------------------------------- P - " + response.body());
                    e.printStackTrace();
                    listener.onError(e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<FocusedPartsResponse> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });

    }

    private void startPJPDownload() {
        Call<List<PJPSchedule>> call = RetrofitAPI.getInstance().getApi().getSchedule(PreferencesManger.getStringFields(context, Constants.Pref.DEPOT_CODE),
                PreferencesManger.getStringFields(context, Constants.Pref.DSR_ID));
        call.enqueue(new Callback<List<PJPSchedule>>() {
            @Override
            public void onResponse(Call<List<PJPSchedule>> call, Response<List<PJPSchedule>> response) {
                Log.e("PJPSchedule", "-------------------------------- P - " + response.body());
                PJPSchedule.deleteAll(PJPSchedule.class, "distributor_Id = ? ", new String[]{PreferencesManger.getStringFields(context, Constants.Pref.DEPOT_CODE)});
                PJPSchedule.saveInTx(response.body());
                listener.onCompleted("");
            }

            @Override
            public void onFailure(Call<List<PJPSchedule>> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }


    private void downloadCollection() {
        listener.onCompleted("");
//        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
//        Call<List<Collection>> call = RetrofitAPI.getInstance().getApi().getCollection(
//                PreferencesManger.getStringFields(context, Constants.Pref.DISTRIBUTOR_ID),
//                PreferencesManger.getStringFields(context, Constants.Pref.DSR_ID), date);
//        call.enqueue(new Callback<List<Collection>>() {
//            @Override
//            public void onResponse(Call<List<Collection>> call, Response<List<Collection>> response) {
//                Log.e("Collection", "-------------------------------- P - " + response.body());
//                Collection.deleteAll(Collection.class);
//                Collection.saveInTx(response.body());
//                listener.onCompleted("");
//            }
//
//            @Override
//            public void onFailure(Call<List<Collection>> call, Throwable t) {
//                listener.onError(t.getMessage());
//            }
//        });
    }


    private void startProductDownload() {
        String lastSync = PreferencesManger.getStringFields(context, Constants.Pref.TIMESTAMP_PARTS);
        Call<APIResponse> call;
        if (TextUtils.isEmpty(lastSync)) {
            call = RetrofitAPI.getInstance().getApi().getProducts();
        } else {
            call = RetrofitAPI.getInstance().getApi().getProducts(lastSync);
        }

        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                try {
                    Log.e("Product", "-------------------------------- P - " + response.body());
                    String ls = "";

                    APIResponse apiResponse = response.body();
                    List<Product> productList = apiResponse.getProductList();
                    for (int i = 0; i < productList.size(); i++) {
                        Product product = productList.get(i);
                        ls = product.getDatetime();
                        String ids = "";
                        for (int j = 0; j < product.getAssociatedCategoriesStr().size(); j++) {
                            if (j == (product.getAssociatedCategoriesStr().size() - 1)) {
                                ids = ids + "'" + product.getAssociatedCategoriesStr().get(j) + "'";
                            } else {
                                ids = ids + "'" + product.getAssociatedCategoriesStr().get(j) + "',";
                            }
                            Log.e("Product", "Part setAssociatePartsIds  ---------- " + ids);
                        }
                        product.setAssociateProductsIds(ids);
                    }
                    PreferencesManger.addStringFields(context, Constants.Pref.TIMESTAMP_PARTS, ls);
                    Product.saveInTx(productList);
                    listener.onCompleted("");
                } catch (Exception e) {
                    Log.e("Product", "Error -------------------------------- P - " + response.body());
                    e.printStackTrace();
                    listener.onError(e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    private void startRetailerDownload() {
        String distributorId = PreferencesManger.getStringFields(context, Constants.Pref.DEPOT_CODE);
        String lastSync = "";
        Distributor distributor = null;
        try {
            distributor = Distributor.find(Distributor.class, " distributor_code = ? ", new String[]{distributorId}).get(0);
            lastSync = distributor.getTimeStampRetailer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Distributor d = distributor;

        Call<APIResponse> call = RetrofitAPI.getInstance().getApi().getRetailers(distributorId, lastSync);
        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                try {
                    Log.e("Retailer", "-------------------------------- P - " + response.body());
                    APIResponse apiResponse = response.body();
                    List<Retailer> retailers = apiResponse.getRetailerList();
                    Retailer.saveInTx(retailers);
                    DBUtil.addOrUpdateRetailer(retailers);
                    String ls = "";
                    if (retailers.size() > 0) {
                        ls = retailers.get(0).getDatetime();
                    }
                    if (d != null) {
                        d.setTimeStampRetailer(ls);
                        d.save();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                listener.onCompleted("");
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    private void startOrdersDownload() {
        Call<APIResponse> call = RetrofitAPI.getInstance().getApi().getOrders(PreferencesManger.getStringFields(context, Constants.Pref.DEPOT_CODE),
                PreferencesManger.getStringFields(context, Constants.Pref.DSR_ID));
        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                try {
                    Log.e("orderses", "-------------------------------- P - " + response.body());
                    APIResponse apiResponse = response.body();
                    List<Orders> orderses = new ArrayList<Orders>();
                    orderses.addAll(apiResponse.getOrdersList());
                    DBUtil.addOrUpdateOrder(orderses);

                    //Orders.saveInTx(orderses);
                    Log.e("Orders", "-------------------------------- P - " + orderses.size());

                    /*
                    for (int i = 0; i < orderses.size(); i++) {
                        Orders orders = orderses.get(i);
                        OrderPart.deleteAll(OrderPart.class, "order_id = ?", new String[]{orders.getOrderId()});
                        List<OrderPart> orderPartList = orderses.get(i).getOrderDetails();
                        orders.setOrderDetails(orderPartList);

                        for (int j = 0; j < orderPartList.size(); j++) {
                            OrderPart orderPart = orderPartList.get(j);
                            orderPart.setOrderId(orders.getOrderId());
                            orderPart.setPartId(orders.getOrderDetails().get(i).getPartId());
                            orderPart.setPartName(orders.getOrderDetails().get(i).getPartName());
                            orderPart.setQuantity(orders.getOrderDetails().get(i).getQuantity());
                            orderPart.setShipped_quantity(orders.getOrderDetails().get(i).getShipped_quantity());
                            orderPart.setBack_order_quantity(orders.getOrderDetails().get(i).getBack_order_quantity());
                            orderPart.setDeliveredQuantity(orders.getOrderDetails().get(i).getDeliveredQuantity());
                            orderPart.setCompanyPrice(orders.getOrderDetails().get(i).getCompanyPrice());
                            orderPart.setLineTotal(orders.getOrderDetails().get(i).getLineTotal());
                            orderPart.save();

                        }


                    }
                     */


                } catch (Exception e) {
                    e.printStackTrace();
                }
                listener.onCompleted("");
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    private void startInvoiceDownload() {

        String distributorId = PreferencesManger.getStringFields(context, Constants.Pref.DISTRIBUTOR_ID);


        String lastSync = "";
        Distributor distributor = null;


        try {
            distributor = Distributor.find(Distributor.class, " distributor_code = ? ", new String[]{distributorId}).get(0);
            lastSync = distributor.getTimeStampRetailer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Distributor d = distributor;
        if (!(Invoice.listAll(Invoice.class).size() > 0)) {
            lastSync = "";
        }

        Call<InvoiceResponse> call = RetrofitAPI.getInstance().getApi().getInvoices(PreferencesManger.getStringFields(context, Constants.Pref.DEPOT_CODE),
                PreferencesManger.getStringFields(context, Constants.Pref.DSR_ID), lastSync);
        call.enqueue(new Callback<InvoiceResponse>() {
            @Override
            public void onResponse(Call<InvoiceResponse> call, Response<InvoiceResponse> response) {
                try {
                    Log.e("Invoice", "-------------------------------- P - " + response.body());
                    InvoiceResponse invoiceResponse = response.body();
                    List<Invoice> invoices = invoiceResponse.getInvoiceList();
                    Invoice.saveInTx(invoices);
                    String ls = "";
                    if (invoices.size() > 0) {
                        ls = invoices.get(0).getDatetime();
                    }
                    if (d != null) {
                        d.setTimeStampRetailer(ls);
                        d.save();
                    }
                    DBUtil.updateRetailerOutstandingOnList();
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onError(e.getMessage());
                }
                listener.onCompleted("");
            }

            @Override
            public void onFailure(Call<InvoiceResponse> call, Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    private void downloadStock() {
        Call<List<Stock>> call = RetrofitAPI.getInstance().getApi().getStock(PreferencesManger.getStringFields(context, Constants.Pref.DEPOT_CODE));
        call.enqueue(new Callback<List<Stock>>() {
            @Override
            public void onResponse(Call<List<Stock>> call, Response<List<Stock>> response) {
                try {

                    List<Stock> stockList = response.body();
                    Log.e("Invoice", "-------------------------------- P - " + stockList);
                    Stock.saveInTx(stockList);
                    Product.executeQuery("VACUUM");
                    Log.e("Invoice", "----------before---------------------- P - " + stockList);
                    Product.executeQuery("UPDATE Product SET stock = '', transit_stock = '';");
                    Log.e("Invoice", "--------------after------------------ P - " + stockList);

                    for (int i = 0; i < stockList.size(); i++) {
                        Product.executeQuery("UPDATE Product SET stock = '" + stockList.get(i).getPartAvailableQuantity()
                                + "', transit_stock = '" + stockList.get(i).getTransitStock() + "' where part_number = '" + stockList.get(i).getPartNumber() + "';");
                        Log.e("Invoice", "-------------------------------- P - " + stockList.get(i));
                    }
                    Log.e("Invoice", "--------------end------------------ P - " + stockList);

                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onError(e.getMessage());
                }
                listener.onCompleted("");
            }

            @Override
            public void onFailure(Call<List<Stock>> call, Throwable t) {
                t.printStackTrace();
                listener.onError(t.getMessage());
            }
        });
    }


}