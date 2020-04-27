package com.darshansfa.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.orm.SugarContext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.darshansfa.API.RetrofitAPI;
import com.darshansfa.Adapters.LocalityRecycleAdapter;
import com.darshansfa.Models.APIResponse;
import com.darshansfa.Models.Locality;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.PJPSchedule;
import com.darshansfa.dbModel.Retailer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePJPDateWiseActivity extends AppCompatActivity {


    @BindView(R.id.recyclerLocality)
    RecyclerView recyclerView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.tvDate)
    TextView tvDate;

    @BindView(R.id.llNoData)
    LinearLayout llNoData;

    @BindView(R.id.edSearch)
    EditText searchLocality;

    private ArrayList<Locality> localityArrayList;
    private LocalityRecycleAdapter adapter;
    private List<PJPSchedule> pjpSchedules;
    private String date, distributorId, dsrId, todayDate;
    private boolean updateWarring = true;

    private String searchText;
    int running=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pjpday_wise);

        getSupportActionBar().setTitle(PreferencesManger.getStringFields(this, Constants.Pref.DEPO_NAME));

        ButterKnife.bind(this);
        SugarContext.init(CreatePJPDateWiseActivity.this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        todayDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        distributorId = PreferencesManger.getStringFields(this, Constants.Pref.DEPOT_CODE);
        dsrId = PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID);
        tvDate.setText(date);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0){
                    fab.setVisibility(View.GONE);
                    //fab.hide();
                } else{
                    fab.setVisibility(View.VISIBLE);
                    //fab.show();
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        localityArrayList = new ArrayList<>();
        pjpSchedules = PJPSchedule.find(PJPSchedule.class, "distributor_Id = ? ", new String[]{distributorId});

        List<Retailer> retailers = Retailer.findWithQuery(Retailer.class, "Select * from Retailer where distributor_Id='" + distributorId + "' " +
                " Group by locality_id ;");

        Log.e("retailers", "------- " + retailers);

        if (retailers.size() == 0) {
            llNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            llNoData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < retailers.size(); i++) {
            Locality locality = new Locality();
            locality.setLocalityId(retailers.get(i).getLocalityId());
            locality.setLocalityName(retailers.get(i).getLocality());
            localityArrayList.add(locality);
        }

        adapter = new LocalityRecycleAdapter(this, localityArrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        searchLocality.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (TextUtils.isEmpty(s.toString())) {
                    localityArrayList.clear();

                    List<Retailer> retailers = Retailer.findWithQuery(Retailer.class, "Select * from Retailer where distributor_Id='" + distributorId + "' " +
                            " Group by locality_id ;");

                    Log.e("retailers", "------- " + retailers);

                    if (retailers.size() == 0) {
                        llNoData.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        llNoData.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                    for (int i = 0; i < retailers.size(); i++) {
                        Locality locality = new Locality();
                        locality.setLocalityId(retailers.get(i).getLocalityId());
                        locality.setLocalityName(retailers.get(i).getLocality());
                        localityArrayList.add(locality);
                    }
                    adapter.notifyDataSetChanged();
                    updateForSelection();

                    return;
                }

                if (s.length() < 2)
                    return;

                searchText = s.toString();
                searchLocations();
               /*
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //searchLocations();
                    }
                }, 1000);
                 */

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });




        adapter.SetOnItemClickListener(new LocalityRecycleAdapter.OnItemClickListener() {
            @Override
            public void updatePJP(Locality localityId, int position) {
                Log.e("Locality", "" + localityId);
                int i = UIUtil.compareDate(todayDate, date);
                if (i == 1 || i == 0) {
                    if (updateWarring && checkForUpdate()) {
                        updateWarringPopup(localityId);
                        updateWarring = false;
                    } else {
                        updateSchedule(localityId);
                        updateWarring = false;
                    }
                } else {
                    localityId.setSelected(!localityId.isSelected());
                    localityArrayList.set(position, localityId);
                    Toast.makeText(getApplicationContext(), "You cant update Previous PJP", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }
        });
        getScheduleForDate();

        updateForSelection();

    }

    private void searchLocations() {
        localityArrayList.clear();

        List<Retailer> retailers = Retailer.findWithQuery(Retailer.class,
                "Select * from Retailer where locality like '%" +searchText+ "%' " +
                        "and distributor_Id='" + distributorId + "' " + " Group by locality_id ;");

        Log.e("retailers", "------- " + retailers);

        if (retailers.size() == 0) {
            llNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            llNoData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < retailers.size(); i++) {
            Locality locality = new Locality();
            locality.setLocalityId(retailers.get(i).getLocalityId());
            locality.setLocalityName(retailers.get(i).getLocality());
            localityArrayList.add(locality);
        }

        //localityArrayList.addAll(Retailer.findWithQuery(Retailer.class,
       // "Select * from Retailer where locality like '%" + searchText + "%' " + "or retailer_Id like '%" + searchText + "%' and distributor_Id = '" + distributorId + "' ;"));
        updateForSelection();
        adapter.notifyDataSetChanged();
    }

    private void updateWarringPopup(final Locality localityId) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Confirmation");
        alertDialog.setMessage("Do you want update your Planned journey.?");

        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                updateSchedule(localityId);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                updateWarring = false;
            }
        });

        alertDialog.show();

    }

    private boolean checkForUpdate() {
        for (int i = 0; i < pjpSchedules.size(); i++) {
            if (todayDate.equalsIgnoreCase(pjpSchedules.get(i).getPjpDate())) {
                return true;
            }
        }
        return false;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.sync) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_pjp, menu);

        return true;
    }


    @OnClick(R.id.sync)
    public void syncRetailer() {
        if (!UIUtil.isInternetAvailable(this)) {
            Toast.makeText(this, "No internet, Please connect to internet", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<APIResponse> call = RetrofitAPI.getInstance().getApi().getRetailers(PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID), "");
        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                Log.e("Product", "-------------------------------- P - " + response.body());
                APIResponse apiResponse = response.body();
                Retailer.saveInTx(apiResponse.getRetailerList());
                llNoData.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                llNoData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.fab)
    public void save() {

        updatePJP();
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
                updateForSelection();

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


    private void updateForSelection() {
        int pos = getPJPObjectPosition();
        if (pos != -1) {
            String locality = pjpSchedules.get(pos).getLocalityId();
            for (int i = 0; i < localityArrayList.size(); i++) {
                Locality l = localityArrayList.get(i);
                l.setSelected(locality.contains(localityArrayList.get(i).getLocalityId()));
                localityArrayList.set(i, l);
            }
        } else {
            for (int i = 0; i < localityArrayList.size(); i++) {
                Locality l = localityArrayList.get(i);
                l.setSelected(false);
                localityArrayList.set(i, l);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void updateSchedule(Locality locality) {
        PJPSchedule schedule = null;

        fab.setVisibility(View.VISIBLE);
        int pos = getPJPObjectPosition();
        if (pos != -1)
            schedule = pjpSchedules.get(pos);

        if (schedule == null && locality.isSelected()) {
            schedule = new PJPSchedule();
            schedule.setLocalityId(locality.getLocalityId());
            schedule.setDistributorId(distributorId);
            schedule.setDsrId(dsrId);
            schedule.setPjpDate(date);
            pjpSchedules.add(schedule);
            return;
        }

        if (schedule.getLocalityId().contains(locality.getLocalityId())) {
            if (!locality.isSelected()) {
                Log.e("locality", "locality : --- : " + locality.getLocalityId());
                String s = locality.getLocalityId() + ",";
                Log.e("locality", "s : --- : " + s);
                String loc = schedule.getLocalityId().replace(s, "");
                Log.e("locality", "locality  coma : --- : " + loc);

                loc = loc.replace(locality.getLocalityId(), "");
                Log.e("locality", "locality : --- : " + loc);

                if (loc.startsWith(","))
                    loc = loc.substring(1, (loc.length() - 1));

                schedule.setLocalityId(loc);
                Log.e("locality", "locality : --- : " + loc);

                if (TextUtils.isEmpty(loc)) {
                    pjpSchedules.remove(pos);
                } else {
                    pjpSchedules.set(pos, schedule);
                }
            }
        } else {

            if (locality.isSelected()) {
                if (schedule.getLocalityId().endsWith(",")) {
                    schedule.setLocalityId(schedule.getLocalityId() + "" + locality.getLocalityId());
                } else {
                    schedule.setLocalityId(schedule.getLocalityId() + "," + locality.getLocalityId());
                }
                pjpSchedules.set(pos, schedule);
            }
        }

        Log.e("Schedule", "Count : " + pjpSchedules.size() + "  ddd : " + schedule);

    }

    private int getPJPObjectPosition() {
        for (int i = 0; i < pjpSchedules.size(); i++) {
            if (date.equalsIgnoreCase(pjpSchedules.get(i).getPjpDate())) {
                return i;
            }
        }
        return -1;
    }

    private void updatePJP() {

        UIUtil.startProgressDialog(this, "Update PJP, please wait...");
        String gson = new Gson().toJson(pjpSchedules);
        Log.e("Jso", "" + gson);

        final JsonArray jsonArray = new JsonParser().parse(gson).getAsJsonArray();

        Log.e("jsonArray", "-------------------------------- P - " + jsonArray);


        Call<JsonObject> call = RetrofitAPI.getInstance().getApi().updatePJPSchedule(jsonArray);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                try {
                    Log.e("res", "----------" + response.body());

                    JsonObject object = response.body();

                    if (object.get("status").getAsInt() == 1) {
                        int dele = PJPSchedule.deleteAll(PJPSchedule.class, "distributor_Id = ? ", new String[]{distributorId});
                        Log.e("Delete", "Deo --------- + " + dele);
                        Log.e("Delete", "pjpSchedules --------- + " + pjpSchedules.size());
                        Snackbar.make(tvDate, "PJP Update successfully..", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        try {
                            JsonArray jsonArray1 = object.getAsJsonArray("pjp_data");
                            for (int i = 0; i < jsonArray1.size(); i++) {
                                JsonObject jsonObject = jsonArray1.get(i).getAsJsonObject();
                                updatePJPId(jsonObject.get("pjp_date").getAsString(), jsonObject.get("pjpId").getAsString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        PJPSchedule.saveInTx(pjpSchedules);
                        fab.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "" + object.get("message").getAsString(), Toast.LENGTH_SHORT).show();
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

    private void updatePJPId(String date, String pjpId) {
        for (int i = 0; i < pjpSchedules.size(); i++) {
            if (pjpSchedules.get(i).getPjpDate().equalsIgnoreCase(date)) {
                pjpSchedules.get(i).setPjpId(pjpId);
            }
        }
    }

    private void getScheduleForDate() {
        Call<List<PJPSchedule>> call = RetrofitAPI.getInstance().getApi().getScheduleForDate(distributorId, dsrId, date);
        call.enqueue(new Callback<List<PJPSchedule>>() {
            @Override
            public void onResponse(Call<List<PJPSchedule>> call, Response<List<PJPSchedule>> response) {
                Log.e("Product", "-------------------------------- P - " + response.body());
//                PJPSchedule.deleteAll(PJPSchedule.class, "distributor_Id = ? ", new String[]{PreferencesManger.getStringFields(context, Constants.Pref.DISTRIBUTOR_ID)});
//                PJPSchedule.saveInTx(response.body());
//                listener.onCompleted();
            }

            @Override
            public void onFailure(Call<List<PJPSchedule>> call, Throwable t) {
//                listener.onError(t.getMessage());
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
            }
        });
    }

}
