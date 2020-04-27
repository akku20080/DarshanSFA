package com.darshansfa.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.darshansfa.API.RetrofitAPI;
import com.darshansfa.Adapters.CartAdapter;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.ImageUploadUtil;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.Cart;
import com.darshansfa.dbModel.OrderPart;
import com.darshansfa.dbModel.Orders;
import com.darshansfa.dbModel.Retailer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends RuntimePermissionsActivity implements GoogleApiClient.ConnectionCallbacks,
        com.google.android.gms.location.LocationListener, GoogleApiClient.OnConnectionFailedListener{

    private static final int REQUEST_PERMISSIONS = 4;
    @BindView(R.id.recyclerCart)
    RecyclerView recyclerView;
    private CartAdapter adapter;
    private ArrayList<Cart> cartArrayList;
    private String retailerId, retailerName, distributorId, dsrId;

    @BindView(R.id.tvTotalOrderAmount)
    TextView tvTotalOrderAmount;

    @BindView(R.id.btnPlaceOrder)
    TextView btnPlaceOrder;

    @BindView(R.id.tvCollect)
    TextView tvCollect;

    @BindView(R.id.tvOrderCollection)
    TextView tvOrderCollection;

    @BindView(R.id.ivDelete)
    ImageView ivDelete;

    @BindView(R.id.llOrder)
    LinearLayout llOrder;

    private int REQUEST_CODE_ORDER_PAYMENT = 2;
    private String orderNumber;
    private Double total;
    //private Location location;
    private Orders order;
    private JsonObject collectionJsonObject;

    private String CHEQUE_IMG_PATH = "cheque/";
    private Retailer retailer;

    LocationRequest mLocationRequest;
    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    private String latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);

        retailerId = PreferencesManger.getStringFields(this, Constants.Pref.RETAILER_ID);
        distributorId = PreferencesManger.getStringFields(this, Constants.Pref.DEPOT_CODE);
        dsrId = PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID);
        retailerName = PreferencesManger.getStringFields(this, Constants.Pref.RETAILER_NAME);

        try {
            retailer = Retailer.find(Retailer.class, "retailer_id = ?", new String[]{retailerId}).get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartArrayList = new ArrayList<>();
        adapter = new CartAdapter(this, cartArrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        orderNumber = PreferencesManger.getStringFields(this, Constants.Pref.DEPO_CODE)
                + UIUtil.getTodaysDate() + UIUtil.getCurrentTime();

        CHEQUE_IMG_PATH = "cheque/" + orderNumber;

        order = new Orders();
        order.setDsrId(dsrId);
        order.setDistributorId(distributorId);
        order.setRetailerId(retailerId);
        order.setOrderId(orderNumber);

        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        order.setOrderDate(date);


        getSupportActionBar().setTitle(retailerName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        CartActivity.super.requestAppPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                , R.string.accept
                , REQUEST_PERMISSIONS);

        ///root/storage/emulated/0/pony/retailers/IMG_20170922_150707.jpg

        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            //getChildFragmentManager().popBackStack();
        }
        else {
            buildGoogleApiClient();
            Log.d("onCreate","Google Play Services available.");
        }


    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(CartActivity.this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(CartActivity.this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(CartActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
        Log.d("onLocationChanged", "Exit");

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(CartActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    @OnClick(R.id.tvCollect)
    public void openCollectionForOrder() {
        if (total <= 0) {
            Toast.makeText(this, "Please add some parts for order", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("Order Number ", "Oede : " + orderNumber + "   am : " + total);
        Intent intent = new Intent(this, OrderCollectionActivity.class);
        intent.putExtra("order_id", orderNumber);
        intent.putExtra("order_amount", "" + total);
        startActivityForResult(intent, REQUEST_CODE_ORDER_PAYMENT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.sync) {
            Toast.makeText(this, "Coming soon..", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartArrayList.clear();
        cartArrayList.addAll(Cart.find(Cart.class, "retailer_id = ? ", new String[]{retailerId}));
        adapter.notifyDataSetChanged();
        updateTotalPrice();
        UIUtil.isGPSON(this);
    }

    @OnClick(R.id.tvAddItem)
    protected void addMoreParts() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ORDER_PAYMENT) {
            try {
                String result = data.getStringExtra("collection");
//                advancePayment = (AdvancePayment) data.getSerializableExtra(Constants.ADV_COLLECTION);
//                Log.e("result ", "AdvancePayment : " + advancePayment.toString());
//                Toast.makeText(this, "Result : " + result, Toast.LENGTH_SHORT).show();
                collectionJsonObject = new Gson().fromJson(result, JsonObject.class);
                Log.e("result ", "Result : " + result.toString() + "   object : " + collectionJsonObject.toString());

                tvOrderCollection.setText("Collected amount : " + UIUtil.amountFormat(collectionJsonObject.get("total_collected_amount").getAsString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateTotalPrice() {
        total = 0d;
        for (int i = 0; i < cartArrayList.size(); i++) {
            try {
                total += cartArrayList.get(i).getPartSubTotal();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        tvTotalOrderAmount.setText("Total Amount : " + UIUtil.amountFormat(String.valueOf(total)));
    }


    @OnClick(R.id.btnPlaceOrder)
    public void submit() {

        if (total <= 0) {
            Toast.makeText(this, "Please add some parts for order", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!(retailer.isVisitStart())) {
            Toast.makeText(this, "Please start Retailer visit first!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!UIUtil.isInternetAvailable(this)) {


            if (retailer.isVisitStart()) {
                placeOrder();
//            Toast.makeText(this, "No internet, please connect to internet!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Toast.makeText(this, "Please start Retailer visit first!", Toast.LENGTH_SHORT).show();
            }

            return;

        }

        //TODO check all parts qty

        Log.e("place", "Ordre ------------------ place");
        UIUtil.startProgressDialog(this, "Getting location, Please wait..");

        order.setLatitude(latitude);
        order.setLongitude(longitude);
        Log.e("place", "Ordre --" + "Lat : " + latitude + ", Long : " + longitude);
        UIUtil.stopProgressDialog(CartActivity.this);
        UIUtil.startProgressDialog(CartActivity.this, "Submitting order please wait...");
        placeOrder();

        /*
         AppLocationProvider.requestForLocation(this, new AppLocationProvider.LocationCallback() {
            @Override
            public void onLocationAvailable(Location loc) {

                location = loc;
                order.setLatitude(loc.getLatitude() + "");
                order.setLongitude(loc.getLongitude() + "");
                Log.e("place", "Ordre --" + "Lat : " + location.getLatitude() + ", Long : " + location.getLongitude());
                UIUtil.stopProgressDialog(CartActivity.this);
                UIUtil.startProgressDialog(CartActivity.this, "Submitting order please wait...");
                placeOrder();


//                Toast.makeText(getApplicationContext(), "Lat : " + location.getLatitude() + ", Long : " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            }
        });
         */

    }

    @OnClick(R.id.ivDelete)
    public void removeOrderCollection() {
        tvOrderCollection.setText("Collected Amount : NA");
        collectionJsonObject = null;
    }

    public void placeOrder() {
        JsonArray jsonArray = new JsonArray();
        JsonObject object = new JsonObject();
        object.addProperty("order_id", orderNumber);
        object.addProperty("pjp_id", PreferencesManger.getStringFields(this, Constants.Pref.PJPID));
        object.addProperty("retailer_id", retailerId);
        object.addProperty("distributor_id", distributorId);
        object.addProperty("dsr_id", dsrId);

        try {
            object.addProperty("latitude", latitude);
            object.addProperty("longitude", longitude);
        } catch (Exception e) {
            e.printStackTrace();
        }

        object.addProperty("order_placed_by", 2);
        object.addProperty("has_advanced_payment", collectionJsonObject == null ? 0 : 1);


        JsonArray array = new JsonArray();
        for (int i = 0; i < cartArrayList.size(); i++) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("part_number", cartArrayList.get(i).getPartNumber());
            jsonObject.addProperty("qty", cartArrayList.get(i).getPartOrderQty());
            jsonObject.addProperty("line_total", cartArrayList.get(i).getPartSubTotal());
            array.add(jsonObject);
        }


        object.add("order_items", array);
        jsonArray.add(object);


        if (!UIUtil.isInternetAvailable(this)) {
            noInternetDialog();
            return;
        }

        Call<JsonObject> call = RetrofitAPI.getInstance().getApi().placeOrder(jsonArray, distributorId, dsrId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                try {
                    JsonObject object = response.body();
                    Toast.makeText(getApplicationContext(), "" + object.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    if (object.get("status").getAsInt() == 1) {
                        saveOrder(Constants.ORDER_OPEN);
                        updateStartStop();
                        PreferencesManger.addStringFields(getApplicationContext(), Constants.Pref.PJPID, "");
                        checkForPaymentUpload();
                    }
                } catch (Exception e) {
                    // Toast.makeText(getApplicationContext(), "Some error occured, please try again later", Toast.LENGTH_SHORT).show();
                    //finish();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                UIUtil.stopProgressDialog(getApplicationContext());
                Toast.makeText(getApplicationContext(), "Server not responding, Try after some time.. ", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                finish();
            }
        });


    }

    private void checkForPaymentUpload() {
        if (collectionJsonObject == null) {
            return;
        }


        if (collectionJsonObject.has("cheque_details")) {
            UIUtil.startProgressDialog(this, "Uploading cheque image..");
            ImageUploadUtil util = new ImageUploadUtil();
            util.init(getApplicationContext());
            JsonArray jsonArray = collectionJsonObject.get("cheque_details").getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject object = jsonArray.get(i).getAsJsonObject();
                object.addProperty("cheque_image", util.singleFileUpload(object.get("cheque_image").getAsString(), CHEQUE_IMG_PATH));
                jsonArray.set(i, object);
            }
        }
        updatePayment();
    }


    public void updatePayment() {

        UIUtil.stopProgressDialog(this);
        if (!UIUtil.isInternetAvailable(this)) {
            noInternetDialog();
            return;
        }
        UIUtil.startProgressDialog(this, "Uploading advance payment...");

        collectionJsonObject.addProperty("retailer_id", retailerId);
        collectionJsonObject.addProperty("order_id", orderNumber);
        collectionJsonObject.addProperty("dsr_id", dsrId);
//        collectionJsonObject.addProperty("payment_mode", "Cash/Cheque");

        Log.e("Payment", "xxxxxx : " + collectionJsonObject.toString());

        Call<JsonObject> call = RetrofitAPI.getInstance().getApi().updatePayment(collectionJsonObject, distributorId, dsrId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                try {
                    JsonObject object = response.body();
                    Toast.makeText(getApplicationContext(), "" + object.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    if (object.get("status").getAsInt() == 1) {
                        clearPayment();
                        return;
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Server not responding, Try after some time.. ", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                UIUtil.stopProgressDialog(getApplicationContext());
                Toast.makeText(getApplicationContext(), "Failed to update advance payment,  Server not responding, Try after some time.. ", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });


    }


    private void noInternetDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

//        AlertDialog.Builder builder =  new AlertDialog.Builder(this, R.style.AppTheme_AppBarOverlay);

        builder.setTitle("No Internet");
        String message = "No internet connection do you want to save this order offline";
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveOrder(Constants.ORDER_PENDING);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void saveOrder(String orderStatus) {
        order.setStatus(orderStatus);
        OrderPart.deleteAll(OrderPart.class, "order_id = ? ", new String[]{order.getOrderId()});

        for (int i = 0; i < cartArrayList.size(); i++) {
            OrderPart orderPart = new OrderPart();
            orderPart.setOrderId(order.getOrderId());
            orderPart.setPartId(cartArrayList.get(i).getPartNumber());
            orderPart.setPartName(cartArrayList.get(i).getPartName());
            orderPart.setQuantity(cartArrayList.get(i).getPartOrderQty());
            try {
                orderPart.setCompanyPrice(Double.parseDouble(cartArrayList.get(i).getPartMRP()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            orderPart.setLineTotal(cartArrayList.get(i).getPartSubTotal());
            orderPart.save();
        }
        try {
            order.setLongitude(latitude);
            order.setLatitude(longitude);
        } catch (Exception e) {
            e.printStackTrace();
            order.setLatitude("0.0");
            order.setLongitude("0.0");
        }
        order.setTotalQuantity(cartArrayList.size());
        order.setAmount(total);
        order.save();
        clearCart();
        clearPayment();

        if(orderStatus.matches(Constants.ORDER_PENDING)){
            retailer.setVisitStart(false);
            retailer.setStopTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
            retailer.setStopDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
            retailer.save();
            PreferencesManger.addStringFields(getApplicationContext(), Constants.Pref.PJPID, "");
            Toast.makeText(CartActivity.this,"Order saved successfully",Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    public void clearCart() {
        Cart.deleteAll(Cart.class, "retailer_id = ? ", new String[]{retailerId});
        cartArrayList.clear();
        adapter.notifyDataSetChanged();

    }

    public void clearPayment() {
        tvOrderCollection.setText("");
        tvTotalOrderAmount.setText("");
        collectionJsonObject = null;
    }


    private void updateStartStop() {

        UIUtil.startProgressDialog(this, "Update PJP, please wait...");
        JsonObject jsonObject = new JsonObject();
        /*
         if(!(retailer.isVisitStart())){
            jsonObject.addProperty("distributor_id", distributorId);
            jsonObject.addProperty("dsr_id", dsrId);
            jsonObject.addProperty("latitude", location.getLatitude());
            jsonObject.addProperty("longitude", location.getLongitude());
            jsonObject.addProperty("flag", 0);
            jsonObject.addProperty("retailer_id", retailerId);
            jsonObject.addProperty("date", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
            jsonObject.addProperty("time", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        }else{
            jsonObject.addProperty("pjp_id", PreferencesManger.getStringFields(this, Constants.Pref.PJPID));
            jsonObject.addProperty("latitude", location.getLatitude());
            jsonObject.addProperty("longitude", location.getLongitude());
            jsonObject.addProperty("flag", 0);
            jsonObject.addProperty("date", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
            jsonObject.addProperty("time", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        }
         */
        jsonObject.addProperty("pjp_id", PreferencesManger.getStringFields(this, Constants.Pref.PJPID));
        jsonObject.addProperty("latitude", latitude);
        jsonObject.addProperty("longitude", longitude);
        jsonObject.addProperty("flag", 0);
        jsonObject.addProperty("date", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        jsonObject.addProperty("time", new SimpleDateFormat("HH:mm:ss").format(new Date()));

        Log.e("jsonArray", "-------------------------------- P - " + jsonObject);


        Call<JsonObject> call = RetrofitAPI.getInstance().getApi().updatePJPStartStop(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                try {
                    JsonObject object = response.body();

                    Toast.makeText(getApplicationContext(), "" + object.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    if (object.get("status").getAsInt() == 1) {
                        Log.e("res", "----------" + response.body());
                        if (retailer != null) {
                            retailer.setVisitStart(false);
                            retailer.save();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                UIUtil.stopProgressDialog(getApplicationContext());
                t.printStackTrace();
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
            }
        });
    }


}
