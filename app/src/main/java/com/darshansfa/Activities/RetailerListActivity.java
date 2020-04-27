package com.darshansfa.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.darshansfa.API.RetrofitAPI;
import com.darshansfa.Adapters.RetailerAdapter;
import com.darshansfa.Models.APIResponse;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.DBUtil;
import com.darshansfa.Utility.GPSTracker;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.PJPSchedule;
import com.darshansfa.dbModel.Retailer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonObject;
import com.orm.SugarContext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.provider.Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;

public class RetailerListActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        com.google.android.gms.location.LocationListener, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.recyclerRetailer)
    RecyclerView recyclerView;

    private LinearLayoutManager llm;
    private ArrayList<Retailer> retailerArrayList;
    private RetailerAdapter adapter;

    @BindView(R.id.tvToday)
    TextView tvToday;

    @BindView(R.id.tvAllRetailer)
    TextView tvAllRetailer;

    @BindView(R.id.edSearch)
    EditText searchRetailer;

    @BindView(R.id.llNoSchedule)
    LinearLayout llNoSchedule;

    @BindView(R.id.llLoading)
    LinearLayout llLoading;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private int retailerSyncCount = 0;

    private boolean todayRetailers = true, previousDate = false;
    private String searchText;
    private String today, distributorId, dsrId;
    private boolean isLocation;
    private String latitude = "null", longitude = "null", date, todayDate;
    private Retailer runningRetailer;
    String pjp_id;

    LocationRequest mLocationRequest;
    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    int locationMode = 0;
    FusedLocationProviderClient mFusedLocationClient;

    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_list);
        ButterKnife.bind(this);
        SugarContext.init(RetailerListActivity.this);

        pjp_id = "";
        distributorId = PreferencesManger.getStringFields(this, Constants.Pref.DEPOT_CODE);
        dsrId = PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID);
        today = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());

        date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        todayDate = date;

        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        retailerArrayList = new ArrayList<>();
        adapter = new RetailerAdapter(this, retailerArrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.SetOnItemClickListener(new RetailerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                PreferencesManger.addStringFields(getApplicationContext(), Constants.Pref.RETAILER_ID,
                        retailerArrayList.get(position).getRetailerId());
                PreferencesManger.addStringFields(getApplicationContext(), Constants.Pref.RETAILER_NAME,
                        retailerArrayList.get(position).getRetailerName());

                Toast.makeText(getApplicationContext(), retailerArrayList.get(position).getRetailerName(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), RetailerDetailTabActivity.class));
            }

            @Override
            public void onCallClick(View view, int position) {
                UIUtil.dialPhoneNumber(RetailerListActivity.this, retailerArrayList.get(position).getRetailerMobile());
                Toast.makeText(getApplicationContext(), retailerArrayList.get(position).getRetailerMobile(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartStopClick(View view, final int position) {

                final Retailer retailer = retailerArrayList.get(position);
                Log.e("Retailer", "retailer ; " + retailer);

                if (UIUtil.isInternetAvailable(RetailerListActivity.this)) {

                    if (retailer.isVisitStart()) {
                        getLocation(position);
                    } else {
                        if (checkForOtherVisit()) {
                            Toast.makeText(getApplicationContext(), "Please complete the Retailer Visit", Toast.LENGTH_SHORT).show();
                        } else {
                            getLocation(position);
                        }
                    }
                    /*
                    if(UIUtil.isGPSON(RetailerListActivity.this)){

                    }
                     */


                } else {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(RetailerListActivity.this);
                    alertDialog.setTitle("No Internet!");
                    alertDialog.setMessage("Device not connect to internet. Do you want to continue offline?");
                    alertDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (!(retailer.isVisitStart())) {

                                if (checkForOtherVisit()) {
                                    Toast.makeText(getApplicationContext(), "Please complete the Retailer Visit",
                                            Toast.LENGTH_SHORT).show();

                                } else {
                                    retailer.setVisitStart(true);
                                    retailer.setStartTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                                    retailer.setStartDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
                                    retailer.save();
                                    retailerArrayList.set(position, retailer);

                                    Collections.sort(retailerArrayList, new Comparator<Retailer>() {
                                        @Override
                                        public int compare(Retailer lhs, Retailer rhs) {
                                            return lhs.getRetailerName().compareTo(rhs.getRetailerName());
                                        }
                                    });

                                    adapter.notifyDataSetChanged();
                                    Log.e("Retailer", "retailer Onstart; " + retailer.getStartTime());
                                }

                            } else {
                                retailer.setVisitStart(false);
                                retailer.setStopTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
                                retailer.setStopDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
                                retailer.save();
                                retailerArrayList.set(position, retailer);

                                Collections.sort(retailerArrayList, new Comparator<Retailer>() {
                                    @Override
                                    public int compare(Retailer lhs, Retailer rhs) {
                                        return lhs.getRetailerName().compareTo(rhs.getRetailerName());
                                    }
                                });

                                adapter.notifyDataSetChanged();
                                Log.e("Retailer", "retailer Onstop; " + retailer.getStopTime());
                            }

                        }
                    });

                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    alertDialog.show();
                }

            }
        });

        todayRetailer();
        searchRetailer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (TextUtils.isEmpty(s.toString())) {
                    if (todayRetailers) {
                        todayRetailer();
                    } else {
                        allRetailer();
                    }
                    return;
                }

                if (s.length() < 2)
                    return;

                searchText = s.toString();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        searchRetailers();
                    }
                }, 1000);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        runningRetailer = checkForRunningVisitRetailer();

       /* if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            //getChildFragmentManager().popBackStack();
        } else {

            try {
                locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            if (locationMode == LOCATION_MODE_HIGH_ACCURACY) {
                buildGoogleApiClient();
                Log.d("onCreate", "Google Play Services available.");
            } else {
                Toast.makeText(RetailerListActivity.this, "Location Mode to High Accuracy is required", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }

        }*/

        opengps();

    }

    public void opengps() {
        gps = new GPSTracker(RetailerListActivity.this);

        // Check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = String.valueOf(gps.getLatitude());
            longitude = String.valueOf(gps.getLongitude());


        } else {
            gps.showSettingsAlert();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (todayRetailers)
            todayRetailer();
        checkForVisit();

       /* if (mGoogleApiClient != null && mFusedLocationClient != null) {
            requestLocationUpdates();
        } else {
            buildGoogleApiClient();
        }*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.sync) {
            syncAllRetailer();
        } else if (id == R.id.retailer) {
            openDatePicker();
        } else if (id == R.id.map) {
            startActivity(new Intent(RetailerListActivity.this, PJPMapsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    private void openDayDialog() {
        final String[] items = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        new AlertDialog.Builder(this)
                .setSingleChoiceItems(items, 0, null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        // Do something useful withe the position of the selected radio button
                        todayRetailers = true;
                        today = items[selectedPosition];
                        tvToday.setText("" + today);
                        todayRetailer();
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_retailer_list, menu);

        return true;
    }


    @OnClick(R.id.tvToday)
    public void todayRetailer() {

        int k = UIUtil.compareDate(todayDate, date);
        if (k == 1 || k == 0) {
            previousDate = false;
        } else {
            previousDate = true;
        }
        adapter.setTodayRetailer(previousDate ? false : true);
        todayRetailers = true;
        tvToday.setTextColor(getResources().getColor(R.color.white));
        tvToday.setBackgroundColor(getResources().getColor(R.color.app_themes));

        tvAllRetailer.setTextColor(getResources().getColor(R.color.app_themes));
        tvAllRetailer.setBackgroundColor(getResources().getColor(R.color.white));
//        List<PJPSchedule> pjpSchedules = PJPSchedule.listAll(PJPSchedule.class);
        List<PJPSchedule> pjpSchedules = PJPSchedule.find(PJPSchedule.class, "pjp_date = ? and distributor_id = ?", new String[]{date, distributorId});
        Log.e("Locality", "Locality -------------+" + pjpSchedules.toString());
        Log.e("Locality", "Locality -------------+" + today);

        if (!(pjpSchedules.size() > 0)) {
            llNoSchedule.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }
        try {
            PJPSchedule schedule = pjpSchedules.get(0);
//        String str = "";
//        for (int i = 0; i < pjpSchedules.size(); i++) {
//            Log.e("Locality", "Locality id -------------+" + pjpSchedules.get(i).getLocalityId());
//            if ((pjpSchedules.size() - 1) == i) {
//                str += "'" + pjpSchedules.get(i).getLocalityId() + "'";
//            } else {
//                str += "'" + pjpSchedules.get(i).getLocalityId() + "',";
//            }
//        }
//        Log.e("Locality", "Locality -------------+" + str);


            retailerArrayList.clear();
            Retailer.executeQuery("VACUUM");
            retailerArrayList.addAll(Retailer.findWithQuery(Retailer.class, "select * from Retailer where  locality_id IN (" + schedule.getLocalityId()
                    + ") and distributor_Id = '" + distributorId + "'  ;"));
            if (retailerArrayList.size() > 0) {
//            updateRetailerOutstandingOnList();
                llNoSchedule.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                llNoSchedule.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(retailerArrayList, new Comparator<Retailer>() {
            @Override
            public int compare(Retailer lhs, Retailer rhs) {
                return lhs.getRetailerName().compareTo(rhs.getRetailerName());
            }
        });

        adapter.notifyDataSetChanged();

    }

    @OnClick(R.id.tvAllRetailer)
    public void allRetailer() {
        adapter.setTodayRetailer(false);
        todayRetailers = false;
        llNoSchedule.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        tvToday.setTextColor(getResources().getColor(R.color.app_themes));
        tvToday.setBackgroundColor(getResources().getColor(R.color.white));

        tvAllRetailer.setTextColor(getResources().getColor(R.color.white));
        tvAllRetailer.setBackgroundColor(getResources().getColor(R.color.app_themes));
        new AsyncTaskLoadRetailer().execute();
//        updateRetailerOutstandingOnList();
    }


    private void searchRetailers() {

        /*
        retailerArrayList.clear();
        retailerArrayList.addAll(Retailer.findWithQuery(Retailer.class, "Select * from Retailer where retailer_name like '%" + searchText + "%' " +
                "or retailer_id like '%" + searchText + "%' and distributor_id = '" + distributorId + "' ;"));

        Log.e("retailers", "------- " + retailerArrayList);


        if (retailerArrayList.size() > 0) {
            llNoSchedule.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

 adapter.notifyDataSetChanged();

         */

        ArrayList<Retailer> temp = new ArrayList();
        for (Retailer d : retailerArrayList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if ((d.getRetailerName().toLowerCase().startsWith(searchRetailer.getText().toString().toLowerCase()))
                    || (d.getRetailerId().toLowerCase().startsWith(searchRetailer.getText().toString().toLowerCase()))
                    || (d.getDistributorId().toLowerCase().startsWith(searchRetailer.getText().toString().toLowerCase()))) {

                temp.add(d);
            }
        }

        for (Retailer d : retailerArrayList) {
            if (!(temp.contains(d))) {
                if ((d.getRetailerName().toLowerCase().contains(searchRetailer.getText().toString().toLowerCase()))
                        || (d.getRetailerId().toLowerCase().contains(searchRetailer.getText().toString().toLowerCase()))
                        || (d.getDistributorId().toLowerCase().contains(searchRetailer.getText().toString().toLowerCase()))) {

                    temp.add(d);
                }
            }

        }


        retailerArrayList.clear();
        retailerArrayList.addAll(temp);

        if (retailerArrayList.size() > 0) {
            llNoSchedule.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        /*
        Collections.sort(retailerArrayList, new Comparator<Retailer>() {
            @Override
            public int compare(Retailer lhs, Retailer rhs) {
                return lhs.getRetailerName().compareTo(rhs.getRetailerName());
            }
        });
         */


        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.btnCreatePJP)
    public void goToPJP() {
        startActivity(new Intent(this, CreatePJPDateWiseActivity.class));
    }


    private void updateStartStop(final int position) {
        //UIUtil.startProgressDialog(this, " Getting current location...");

        final Retailer retailer = retailerArrayList.get(position);

        UIUtil.startProgressDialog(this, "Update PJP, please wait...");
        JsonObject jsonObject = new JsonObject();

        if (!(retailer.isVisitStart())) {
            jsonObject.addProperty("distributor_id", distributorId);
            jsonObject.addProperty("dsr_id", dsrId);
            jsonObject.addProperty("latitude", latitude);
            jsonObject.addProperty("longitude", longitude);
            jsonObject.addProperty("flag", retailer.isVisitStart() ? 0 : 1);
            jsonObject.addProperty("retailer_id", retailer.getRetailerId());
            jsonObject.addProperty("date", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
            jsonObject.addProperty("time", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        } else {
            pjp_id = PreferencesManger.getStringFields(this, Constants.Pref.PJPID);
            jsonObject.addProperty("pjp_id", pjp_id);
            jsonObject.addProperty("latitude", latitude);
            jsonObject.addProperty("longitude", longitude);
            jsonObject.addProperty("flag", retailer.isVisitStart() ? 0 : 1);
            jsonObject.addProperty("date", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
            jsonObject.addProperty("time", new SimpleDateFormat("HH:mm:ss").format(new Date()));
        }

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
                        if (!(retailer.isVisitStart())) {
                            pjp_id = object.get("pjp_id").getAsString();
                            PreferencesManger.addStringFields(getApplicationContext(), Constants.Pref.PJPID, object.get("pjp_id").getAsString());
                        }

                        retailer.setVisitStart(!retailer.isVisitStart());
                        retailer.save();
                        retailerArrayList.set(position, retailer);

                        Collections.sort(retailerArrayList, new Comparator<Retailer>() {
                            @Override
                            public int compare(Retailer lhs, Retailer rhs) {
                                return lhs.getRetailerName().compareTo(rhs.getRetailerName());
                            }
                        });

                        adapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    UIUtil.stopProgressDialog(getApplicationContext());
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

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(RetailerListActivity.this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(RetailerListActivity.this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    private void getLocation(final int position) {

        if ((!latitude.matches("null")) && (!longitude.matches("null"))) {
            updateStartStop(position);
        } else {
            latitude="0.00";
            longitude="0.00";
            updateStartStop(position);
            //opengps();
        }


        /*
        isLocation = false;
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                isLocation = true;
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                Log.e("LatLong", "Lat -- " + location.getLatitude() + "  Long -- " + location.getLongitude());
                Log.e("LatLong After", "Lat -- " + location.getLatitude() + "  Long -- " + location.getLongitude());
                UIUtil.stopProgressDialog(getApplicationContext());
                locationManager.removeUpdates(this);
                updateStartStop(position);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        final Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Log.e("locationManager", " locationManager");

         new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isLocation && loc != null) {
                    latitude = String.valueOf(loc.getLatitude());
                    longitude = String.valueOf(loc.getLongitude());
                    UIUtil.stopProgressDialog(getApplicationContext());
                    locationManager.removeUpdates(locationListener);
                    updateStartStop(position);
                    Log.e("LatLong", "Lat -- " + loc.getLatitude() + "  Long -- " + loc.getLongitude());
                }
            }
        }, 5000);

         */


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(RetailerListActivity.this)
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

        if (mGoogleApiClient != null && (!latitude.matches("null") && (!longitude.matches("null")))) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }else{
            if(locationMode == LOCATION_MODE_HIGH_ACCURACY) {
                //buildGoogleApiClient();
                Log.d("onCreate", "Google Play Services available.");
            } else {
                Toast.makeText(RetailerListActivity.this,"Location Mode to High Accuracy is required",Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }

        }
        Log.d("onLocationChanged", "Exit");

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //requestLocationUpdates();

    }

    private void requestLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000);
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(RetailerListActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        /*if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }*/
    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {

            for (Location location : locationResult.getLocations()) {
                latitude= String.valueOf(location.getLatitude());
                longitude= String.valueOf(location.getLongitude());
                Log.i("MainActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());

            }

            if (mGoogleApiClient != null && (!latitude.matches("null") && (!longitude.matches("null")))) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                Log.d("onLocationChanged", "Removing Location Updates");
            }else{
                if(locationMode == LOCATION_MODE_HIGH_ACCURACY) {
                    //buildGoogleApiClient();
                    Log.d("onCreate", "Google Play Services available.");
                } else {
                    Toast.makeText(RetailerListActivity.this,"Location Mode to High Accuracy is required",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }

            }

        };

    };

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void syncAllRetailer() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        retailerSyncCount++;

        Call<APIResponse> call = RetrofitAPI.getInstance().getApi().getRetailers(PreferencesManger.getStringFields(this, Constants.Pref.DEPOT_CODE), "");
        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                Log.e("Product", "-------------------------------- P - " + response.body());
                APIResponse apiResponse = response.body();
                Retailer.saveInTx(apiResponse.getRetailerList());
                DBUtil.updateRetailerOutstandingOnList();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                if (todayRetailers) {
                    todayRetailer();
                } else {
                    allRetailer();
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

    }

    private void checkForVisit() {
        Call<JsonObject> call = RetrofitAPI.getInstance().getApi().getPJPVisitStatus(distributorId, dsrId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    Log.e("Product", "-------------------------------- P - " + response.body());
                    JsonObject object = response.body();
                    if (object.get("status").getAsInt() == 1) {
                        Retailer.executeQuery("UPDATE Retailer SET visit_start = '1' where retailer_Id = '"
                                + object.get("visit_status").getAsJsonObject().get("retailer").getAsString() + "';");
                        todayRetailer();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
            }
        });


    }

//    private void updateRunningRetailer() {
//        if (runningRetailer == null)
//            return;
//
//        List<Retailer> retailerList = Retailer.listAll(Retailer.class);
//        for (int i = 0; i < retailerList.size(); i++) {
//            if (retailerList.get(i).getRetailerId().equalsIgnoreCase(runningRetailer.getRetailerId())) {
//                Retailer retailer = retailerList.get(i);
//                retailer.setVisitStart(true);
//                retailer.save();
//                return;
//            }
//
//        }
//    }


    private boolean checkForOtherVisit() {
        for (int i = 0; i < retailerArrayList.size(); i++) {
            if (retailerArrayList.get(i).isVisitStart())
                return true;
        }
        return false;
    }

    private Retailer checkForRunningVisitRetailer() {
        for (int i = 0; i < retailerArrayList.size(); i++) {
            if (retailerArrayList.get(i).isVisitStart())
                return retailerArrayList.get(i);
        }
        return null;
    }

    private void openDatePicker() {

        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                monthOfYear = monthOfYear + 1;

                String month, day;
                //Calendar myCalendar = Calendar.getInstance();
                if (monthOfYear < 10) {
                    month = "0" + String.valueOf(monthOfYear);
                } else {
                    month = String.valueOf(monthOfYear);
                }

                if (dayOfMonth < 10) {
                    day = "0" + String.valueOf(dayOfMonth);
                } else {
                    day = String.valueOf(dayOfMonth);
                }

                date = "" + day + "-" + month + "-" + year;
                tvToday.setText(date);
                checkForBeforeDate();
                todayRetailer();

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void checkForBeforeDate() {
        int i = UIUtil.compareDate(todayDate, date);
        Log.e("Date", "O------------------- : " + i);
        if (i == 0) {
            tvToday.setText("Today");
            previousDate = false;
        } else if (i == -1) {
            previousDate = true;
        } else {
            previousDate = false;
        }

        Collections.sort(retailerArrayList, new Comparator<Retailer>() {
            @Override
            public int compare(Retailer lhs, Retailer rhs) {
                return lhs.getRetailerName().compareTo(rhs.getRetailerName());
            }
        });

        adapter.notifyDataSetChanged();
    }

//    private void updateRetailerOutstandingOnList() {
//
//        for (int i = 0; i < retailerArrayList.size(); i++) {
//            Retailer retailer = retailerArrayList.get(i);
//            List<Invoice> list = Invoice.find(Invoice.class, "retailer_id = ?", new String[]{retailer.getRetailerId()});
//            if (list != null && list.size() > 0) {
//                float oust = 0f;
//                for (int j = 0; j < list.size(); j++) {
//                    try {
//                        oust += Float.parseFloat(list.get(j).getTotalAmount()) - Float.parseFloat(list.get(j).getCollectedAmount());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                retailer.setOutstandingAmount(String.valueOf(oust));
//            } else {
//                retailer.setOutstandingAmount("0");
//            }
//            retailerArrayList.set(i, retailer);
//        }
//
//        adapter.notifyDataSetChanged();
//    }


    public class AsyncTaskLoadRetailer extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            llLoading.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            llNoSchedule.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                retailerArrayList.clear();
                retailerArrayList.addAll(Retailer.find(Retailer.class, "distributor_id = ?", new String[]{distributorId}));

                Collections.reverse(retailerArrayList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg) {
            if (retailerArrayList.size() == 0 && retailerSyncCount < 3) {
                syncAllRetailer();
            }
            llLoading.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            Collections.sort(retailerArrayList, new Comparator<Retailer>() {
                @Override
                public int compare(Retailer lhs, Retailer rhs) {
                    return lhs.getRetailerName().compareTo(rhs.getRetailerName());
                }
            });

            adapter.notifyDataSetChanged();
        }
    }


}
