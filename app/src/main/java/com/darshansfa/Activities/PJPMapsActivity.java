package com.darshansfa.Activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.PJPSchedule;
import com.darshansfa.dbModel.Retailer;

public class PJPMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    @BindView(R.id.tvDate)
    TextView tvDate;
    private GoogleMap mMap;
    private String todayDate, date, distributorId, dsrId;
    private List<Retailer> retailerArrayList;
    private boolean isLocation;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pjpmaps);

        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        todayDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        distributorId = PreferencesManger.getStringFields(this, Constants.Pref.DISTRIBUTOR_ID);
        dsrId = PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID);
        tvDate.setText(date);

        retailerArrayList = new ArrayList<>();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.tvDate)
    public void openDatePicker() {

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
                tvDate.setText(date);
                mMap.clear();

                bindRetailerOnMap(date);
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        UIUtil.isGPSON(this);
        //PermissionUtils.requestPermission(this, 2, Manifest.permission.ACCESS_FINE_LOCATION, false);
    }

    private void bindRetailerOnMap(String date) {

        List<PJPSchedule> pjpSchedules = PJPSchedule.find(PJPSchedule.class, "pjp_date = ? and distributor_id = ?", new String[]{date, distributorId});
        Log.e("Locality", "Locality -------------+" + pjpSchedules.toString());
        Log.e("Locality", "Locality -------------+" + date);

        if (!(pjpSchedules.size() > 0)) {
            myLocation();
            return;
        }

        try {
            PJPSchedule schedule = pjpSchedules.get(0);
            retailerArrayList.clear();
            mMap.clear();
            Retailer.executeQuery("VACUUM");
            retailerArrayList.addAll(Retailer.findWithQuery(Retailer.class, "select * from Retailer where  locality_id IN (" + schedule.getLocalityId()
                    + ") and distributor_Id = '" + distributorId + "'  ;"));
            if (retailerArrayList.size() > 0) {
                addOnMap();
            } else {
                loadToCurrentLocation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadToCurrentLocation() {
        Snackbar.make(null, "No Data", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void addOnMap() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < retailerArrayList.size(); i++) {
            try {
                LatLng latLng = new LatLng(Double.parseDouble(retailerArrayList.get(i).getLatitude()), Double.parseDouble(retailerArrayList.get(i).getLongitude()));
                builder.include(latLng);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .anchor(0.5f, 0.5f)
                        .title(retailerArrayList.get(i).getRetailerName())
                        .snippet(retailerArrayList.get(i).getRetailerAddress()));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Retailer", "Retailer : " + retailerArrayList.get(i));
            }
        }
        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mMap.setMyLocationEnabled(true);
        bindRetailerOnMap(date);
    }

    private void myLocation() {
        Location location = mMap.getMyLocation();

        LatLng myLocation;
        if (location != null) {
            myLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12));
        }

    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }
}
