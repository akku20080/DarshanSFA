package com.darshansfa.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.orm.SugarContext;
import com.splunk.mint.Mint;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.darshansfa.API.RetrofitAPI;
import com.darshansfa.Models.DailyNoteStatusResponse;
import com.darshansfa.Models.DailyNotesStatus;
import com.darshansfa.Models.DistributorResponse;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.DistributorDialog;
import com.darshansfa.Utility.LocationTrackService;
import com.darshansfa.Utility.NotifyListener;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.SyncDialog;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.Distributor;
import com.darshansfa.dbModel.Note;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DSRDashboardActivity extends RuntimePermissionsActivity implements View.OnClickListener {

    private static final int REQUEST_PERMISSIONS = 21;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvDepoName)
    TextView tvDepoName;

    private String todayDate;

    final String[] items = {"Sync ALL", "Product", "Order", "Retailer", "Outstanding", "PJPSchedule",
            "Product Avg", "Stock", "Focus Product", "Back Order", "Collection"};

    final ArrayList<Integer> itemsSelected = new ArrayList();
    private List<DailyNotesStatus> dailyNotesStatusList;
    private List<Note> pendingDailyNote;
    private Dialog dialog;

    String currentVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsrdashboard);

        Mint.initAndStartSession(this.getApplication(), Constants.SPLUNK_KEY);
        ButterKnife.bind(this);
        SugarContext.init(DSRDashboardActivity.this);

//        if (UIUtil.isGPSON(this))
        startService(new Intent(this, LocationTrackService.class));

        pendingDailyNote = new ArrayList<>();

        ((LinearLayout) findViewById(R.id.llRetailer)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.llAddRetailer)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.llStockUpdate)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.llDayClose)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.llJourneyMap)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.llDeshBoard)).setOnClickListener(this);

        ((ImageView) findViewById(R.id.ivRetailer)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.ivStockUpdate)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.ivAddRetailer)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.ivDayClose)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.ivMAP)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.ivDeshBoard)).setOnClickListener(this);

        ((TextView) findViewById(R.id.tvRetailer)).setOnClickListener(this);
        ((TextView) findViewById(R.id.tvAddRetailer)).setOnClickListener(this);
        ((TextView) findViewById(R.id.tvStockUpdate)).setOnClickListener(this);
        ((TextView) findViewById(R.id.tvDayClose)).setOnClickListener(this);
        ((TextView) findViewById(R.id.tvMap)).setOnClickListener(this);
        ((TextView) findViewById(R.id.tvDeshBoard)).setOnClickListener(this);

        tvName.setText(PreferencesManger.getStringFields(this, Constants.Pref.USERNAME));

        todayDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        tvDepoName.setText(PreferencesManger.getStringFields(this, Constants.Pref.DEPO_NAME));


        if (TextUtils.isEmpty(PreferencesManger.getStringFields(this, Constants.Pref.TODAY))) {
            syncData(true);
        } else if (!todayDate.equalsIgnoreCase(PreferencesManger.getStringFields(this, Constants.Pref.TODAY))) {
            checkDailyNote();
            if (UIUtil.isInternetAvailable(this)) {
                syncData(false);
            } else {
                noInternet();
            }
        }

        //permission handle for app
        DSRDashboardActivity.super.requestAppPermissions(new String[]{
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, R.string.accept, REQUEST_PERMISSIONS);

//        Log.e("Demo", " ---------------- : " + "2017-11-22 05:08:42 p.m.".toUpperCase().replaceAll("\\.", ""));

        /*
        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

         */

        //new GetVersionCode().execute();
    }

    class GetVersionCode extends AsyncTask<Void, String, String> {

        @Override

        protected String doInBackground(Void... voids) {

            String newVersion = null;

            try {
                Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + DSRDashboardActivity.this.getPackageName()  + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();
                if (document != null) {
                    Elements element = document.getElementsContainingOwnText("Current Version");
                    for (org.jsoup.nodes.Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElemets = ele.siblingElements();
                            for (org.jsoup.nodes.Element sibElemet : sibElemets) {
                                newVersion = sibElemet.text();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newVersion;

        }


        @Override

        protected void onPostExecute(String onlineVersion) {

            super.onPostExecute(onlineVersion);

            if (onlineVersion != null && !onlineVersion.isEmpty()) {

                //if (Float.valueOf(currentVersion) < Float.valueOf(onlineVersion))  }

                if(!(currentVersion.trim().matches(onlineVersion))){
                    Toast.makeText(DSRDashboardActivity.this,"New version available",Toast.LENGTH_LONG).show();

                }

            }

            Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);

        }
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private void syncData(boolean progress) {
        itemsSelected.clear();
        for (int i = 1; i < items.length; i++) {
            itemsSelected.add(i);
        }


        SyncDialog syncDialog = new SyncDialog(DSRDashboardActivity.this);
        if (!progress) {
            syncDialog.syncDataWithoutProgress(itemsSelected, new NotifyListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onCompleted(String s) {
                    PreferencesManger.addStringFields(getApplicationContext(), Constants.Pref.TODAY, todayDate);
                    checkDailyNote();
                }

                @Override
                public void onError(String error) {
                }
            });
        } else {
            syncDialog.show(itemsSelected, new NotifyListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onCompleted(String id) {
                    PreferencesManger.addStringFields(getApplicationContext(), Constants.Pref.TODAY, todayDate);
                    checkDailyNote();
                }

                @Override
                public void onError(String error) {

                }
            });
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        UIUtil.isGPSON(this);

        try {
            tvName.setText(PreferencesManger.getStringFields(this, Constants.Pref.USERNAME));
            tvDepoName.setText(PreferencesManger.getStringFields(this, Constants.Pref.DEPO_NAME));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sync) {
            ShowSyncConfirmationMultiple();
            return true;
        } else if (id == R.id.logout) {
            showLogoutConfirmation();
//            startProductDownload();
        }else if(id==R.id.change_pass){
            changePass();
        }

        return super.onOptionsItemSelected(item);
    }

    private void changePass() {
        startActivity(new Intent(getApplicationContext(), ChangePassword.class));
        //finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dsr_dashboard, menu);

        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.llRetailer:
            case R.id.ivRetailer:
            case R.id.tvRetailer:
                startActivity(new Intent(this, RetailerListActivity.class));
                break;

            case R.id.llStockUpdate:
            case R.id.ivStockUpdate:
            case R.id.tvStockUpdate:
                startActivity(new Intent(this, StockActivity.class));
                break;

            case R.id.llAddRetailer:
            case R.id.tvAddRetailer:
            case R.id.ivAddRetailer:
                startActivity(new Intent(this, AddRetailerActivity.class));
                break;

            case R.id.llDayClose:
            case R.id.ivDayClose:
            case R.id.tvDayClose:
                startActivity(new Intent(this, DayCloseActivity.class));
                break;

            case R.id.llJourneyMap:
            case R.id.tvMap:
            case R.id.ivMAP:
//                startActivity(new Intent(this, CreatePJPDateWiseActivity.class));
                getDistributorList();
                break;

            case R.id.llDeshBoard:
            case R.id.ivDeshBoard:
            case R.id.tvDeshBoard:
                startActivity(new Intent(this, DSRReportActivity.class));
                break;

            default:
                break;

        }
    }

    private void showLogoutConfirmation() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

//        AlertDialog.Builder builder =
//                new AlertDialog.Builder(this, R.style.AppTheme_AppBarOverlay);

        builder.setTitle("Confirmation");
        String message = "Do you want to logout?";
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
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

    private void noInternet() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

//        AlertDialog.Builder builder =
//                new AlertDialog.Builder(this, R.style.AppTheme_AppBarOverlay);
        builder.setCancelable(false);
        builder.setTitle("No Internet");
        String message = "Device not connect to internet. Do you want to continue offline?";
        builder.setMessage(message);
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void logout() {
        PreferencesManger.clearPreferences(this);
        deleteDatabase("app.db");
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void ShowSyncConfirmationMultiple() {

        Dialog dialog;
        itemsSelected.clear();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select to Sync ");
        builder.setMultiChoiceItems(items, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedItemId, boolean isSelected) {
                        if (isSelected) {
                            itemsSelected.add(selectedItemId);
                        } else if (itemsSelected.contains(selectedItemId)) {
                            itemsSelected.remove(Integer.valueOf(selectedItemId));
                        }
                    }
                }).setPositiveButton("Sync Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (!UIUtil.isInternetAvailable(getApplicationContext())) {
                            Toast.makeText(getApplicationContext(), "Network Not Available . Please connect it now", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!(itemsSelected.size() > 0))
                            return;

                        if (itemsSelected.contains(0)) {
                            itemsSelected.clear();
                            for (int i = 1; i < items.length; i++) {
                                itemsSelected.add(i);
                            }
                        }

                        SyncDialog syncDialog = new SyncDialog(DSRDashboardActivity.this);
                        syncDialog.show(itemsSelected, new NotifyListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onCompleted(String s) {

                            }

                            @Override
                            public void onError(String error) {

                            }
                        });

                    }
                }

        ).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }
        );
        dialog = builder.create();
        dialog.show();

    }


    private void getDistributorList() {

        UIUtil.startProgressDialog(this, "Getting depo list, Please wait....");
        Call<DistributorResponse> call = RetrofitAPI.getInstance().getApi().getDistributor(PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID));

        call.enqueue(new Callback<DistributorResponse>() {
            @Override
            public void onResponse(Call<DistributorResponse> call, Response<DistributorResponse> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                //JsonObject object = response.body();
                try {
                    Log.e("DistributorResponse", "-------------------------------- P - " + response.body());
                    DistributorResponse distributorResponse = response.body();
                    if (distributorResponse.getStatus() == 1) {
                        Distributor.deleteAll(Distributor.class);
                        Distributor.saveInTx(distributorResponse.getDistributor());
                        openDistributorList();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    openDistributorList();
                }
            }

            @Override
            public void onFailure(Call<DistributorResponse> call, Throwable t) {
                UIUtil.stopProgressDialog(getApplicationContext());
                openDistributorList();
            }
        });
    }

    private void openDistributorList() {
        DistributorDialog.show(this, Distributor.listAll(Distributor.class), new NotifyListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onCompleted(String s) {
                Log.e("DSR", "ID : ---------------" + s);
                if (s.equalsIgnoreCase(PreferencesManger.getStringFields(getApplicationContext(), Constants.Pref.DISTRIBUTOR_ID))) {
                    startActivity(new Intent(DSRDashboardActivity.this, CreatePJPDateWiseActivity.class));
                    PreferencesManger.addStringFields(DSRDashboardActivity.this, Constants.Pref.DISTRIBUTOR_ID, s);
                } else {
                    PreferencesManger.addStringFields(DSRDashboardActivity.this, Constants.Pref.DISTRIBUTOR_ID, s);
                    SyncDialog syncDialog = new SyncDialog(DSRDashboardActivity.this);
                    ArrayList<Integer> itemsSelected = new ArrayList<Integer>();
                    for (int i = 1; i < 10; i++) {
                        itemsSelected.add(i);
                    }
                    syncDialog.show(itemsSelected, new NotifyListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onCompleted(String s) {
                            startActivity(new Intent(DSRDashboardActivity.this, CreatePJPDateWiseActivity.class));
                        }

                        @Override
                        public void onError(String error) {

                        }
                    });

                }


            }

            @Override
            public void onError(String error) {

            }
        });

    }


    public void getRetailerLocation() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), 9);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }


    private void checkDailyNote() {
        Call<DailyNoteStatusResponse> call = RetrofitAPI.getInstance().getApi().checkDailyNote(PreferencesManger.getStringFields(this, Constants.Pref.DEPOT_CODE),
                PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID));
        call.enqueue(new Callback<DailyNoteStatusResponse>() {
            @Override
            public void onResponse(Call<DailyNoteStatusResponse> call, Response<DailyNoteStatusResponse> response) {
                try {
                    Log.e("checkDailyNote", "-------------------------------- P - " + response.body());
                    dailyNotesStatusList = response.body().getDailyNotesStatus();
                    if (dailyNotesStatusList != null && dailyNotesStatusList.size() > 0) {
                        updateDailyNote();
                    }

                } catch (Exception e) {
                    Log.e("checkDailyNote", "Error -------------------------------- P - " + response.body());
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<DailyNoteStatusResponse> call, Throwable t) {
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateDailyNoteDialog(final DailyNotesStatus dailyNotesStatus) {
        dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_daily_note);
        dialog.setTitle("Daily Note");
        ButterKnife.bind(dialog);
        TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
        tvMessage.setText("You have pending daily Note For Date : " + dailyNotesStatus.getPlannedDate() + " Please update to continue.\n\n\n");
        TextView tvSubmit = (TextView) dialog.findViewById(R.id.tvSubmit);
        final EditText edTitle = (EditText) dialog.findViewById(R.id.edTitle);
        final EditText edContent = (EditText) dialog.findViewById(R.id.edContent);
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edTitle.getText().toString())) {
                    edTitle.setError("Enter title");
                    return;
                }

                if (TextUtils.isEmpty(edContent.getText().toString())) {
                    edContent.setError("Enter content");
                    return;
                }

                Note note = new Note();
                note.setDistributorId(PreferencesManger.getStringFields(DSRDashboardActivity.this, Constants.Pref.DISTRIBUTOR_ID));
                note.setDsrId(PreferencesManger.getStringFields(DSRDashboardActivity.this, Constants.Pref.DSR_ID));
                note.setTitle(edTitle.getText().toString());
                note.setContent(edContent.getText().toString());
                note.setNoteType(Constants.DAILY_NOTE);
                note.setPjpPlanned(dailyNotesStatus.getPlannedDate());
                note.setPjpId(dailyNotesStatus.getId());
                note.setDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
                note.setTime(new SimpleDateFormat("hh:mm a").format(new Date()));
                pendingDailyNote.add(note);
                try {
                    dailyNotesStatusList.remove(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                updateDailyNote();

            }
        });

        dialog.show();
    }

    private void updateDailyNote() {
        try {
            if (dailyNotesStatusList.size() > 0) {
                if (!todayDate.equalsIgnoreCase(dailyNotesStatusList.get(0).getPlannedDate())) {
                    updateDailyNoteDialog(dailyNotesStatusList.get(0));
                } else {
                    dailyNotesStatusList.remove(0);
                    updateDailyNote();
                }
            } else {
                if (pendingDailyNote.size() > 0) {
                    updateDailyNoteToServer();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateDailyNoteToServer() {

        Gson gson = new Gson();
        String s = gson.toJson(pendingDailyNote);
        JsonArray jsonArray = gson.fromJson(s, JsonArray.class);
        for (int i = 0; i < jsonArray.size(); i++) {
            jsonArray.get(i).getAsJsonObject().addProperty("remarks", pendingDailyNote.get(i).getRemarks());
        }
        UIUtil.startProgressDialog(this, "Updating note, please wait...");

        Call<JsonObject> call = RetrofitAPI.getInstance().getApi().uploadNote(jsonArray,
                Constants.PATH_DAILY_NOTE,
                PreferencesManger.getStringFields(this, Constants.Pref.DEPOT_CODE),
                PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID));

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                try {
                    JsonObject object = response.body();
                    Toast.makeText(getApplicationContext(), "" + object.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    if (object.get("status").getAsInt() == 1) {
                        if (dialog != null && dialog.isShowing())
                            dialog.dismiss();

                        Toast.makeText(getApplicationContext(), "Daily notes update successfully", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Server not responding, Try after some time.. ", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                UIUtil.stopProgressDialog(getApplicationContext());
                Toast.makeText(getApplicationContext(), "Server not responding, Try after some time.. ", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                dialog.dismiss();
            }
        });

    }

}

