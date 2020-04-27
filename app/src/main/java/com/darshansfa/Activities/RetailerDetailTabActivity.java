package com.darshansfa.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.splunk.mint.Mint;

import java.util.List;

import com.darshansfa.API.RetrofitAPI;
import com.darshansfa.Adapters.RetailerTabPagerAdapter;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.Retailer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetailerDetailTabActivity extends AppCompatActivity {


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private RetailerTabPagerAdapter adapter;
    int PLACE_PICKER_REQUEST = 1;
    private MenuItem locationMenu;
    private String retailerId;
    private Retailer retailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_detail_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mViewPager = (ViewPager) findViewById(R.id.container);

        Mint.initAndStartSession(this.getApplication(), Constants.SPLUNK_KEY);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        getSupportActionBar().setTitle(PreferencesManger.getStringFields(this, Constants.Pref.RETAILER_NAME));


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_order));
                tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_collection));
                tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_sale_return));
                tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_report));
                tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_back_order));

                adapter = new RetailerTabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
                mViewPager.setAdapter(adapter);
                mViewPager.setOffscreenPageLimit(5);

                mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        mViewPager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
            }
        });

        retailerId = PreferencesManger.getStringFields(this, Constants.Pref.RETAILER_ID);

        List<Retailer> retailerList = Retailer.find(Retailer.class, "retailer_id = ? ", new String[]{retailerId});
        if (retailerList.size() > 0) {
            retailer = retailerList.get(0);
//            locationMenu.setVisible(TextUtils.isEmpty(retailer.getLatitude()) || TextUtils.isEmpty(retailer.getLongitude()));
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_retailer_detail_tab, menu);
//        locationMenu = menu.findItem(R.id.location);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        if (id == R.id.note) {
            openNotePage();
        }
        if (id == R.id.location) {
            getRetailerLocation();
        }
        if (id == R.id.profile) {
            Intent intent = new Intent(this, AddRetailerActivity.class);
            intent.putExtra(Constants.UPDATE, true);
            intent.putExtra(Constants.RETAILER_LOGIN, retailerId);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void openNotePage() {
        PreferencesManger.addStringFields(this, Constants.Pref.NOTE_TYPE, Constants.RETAILER_NOTE);
        startActivity(new Intent(this, NoteActivity.class));
    }


    public void getRetailerLocation() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Mint.initAndStartSession(this.getApplication(), Constants.SPLUNK_KEY);
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            Mint.initAndStartSession(this.getApplication(), Constants.SPLUNK_KEY);
            e.printStackTrace();
        }catch (Exception e){
            Log.e("ERRRR",e.toString());
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                updateRetailerLatLong(place.getLatLng());
            }
        }
    }

    private void updateRetailerLatLong(final LatLng latLng) {

        JsonObject object = new JsonObject();
        object.addProperty("lat", latLng.latitude);
        object.addProperty("lng", latLng.longitude);
        object.addProperty("retailer", retailerId);

        Call<JsonObject> call = RetrofitAPI.getInstance().getApi().retailerUpdateLatLong(object);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                try {
                    JsonObject object = response.body();

                    Toast.makeText(getApplicationContext(), "" + object.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    if (object.get("status").getAsInt() == 1) {
                        retailer.setLatitude(latLng.latitude + "");
                        retailer.setLongitude(latLng.longitude + "");
                        retailer.save();
                        updateLocation();
                        locationMenu.setVisible(false);
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
               // Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
            }
        });

    }


    private void updateLocation() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent(Constants.RETAILER_LOCATION_CHANGE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
