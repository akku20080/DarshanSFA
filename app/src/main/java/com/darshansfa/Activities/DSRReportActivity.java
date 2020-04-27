package com.darshansfa.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
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
import com.darshansfa.Adapters.ReportRetailerAdapter;
import com.darshansfa.Adapters.SpinnerCustomAdapter;
import com.darshansfa.Models.Report;
import com.darshansfa.Models.ReportResponse;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.Retailer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DSRReportActivity extends AppCompatActivity {

    @BindView(R.id.tvCurrentMonth)
    TextView tvCurrentMonth;

    @BindView(R.id.tv3Month)
    TextView tv3Month;

    @BindView(R.id.tvTo)
    TextView tvTo;

    @BindView(R.id.tvFrom)
    TextView tvFrom;

    @BindView(R.id.btnSubmit)
    TextView btnSubmit;

    @BindView(R.id.tvMtdPerformance)
    TextView tvMtdPerformance;

    @BindView(R.id.tvSalesValue)
    TextView tvSalesValue;

    @BindView(R.id.tvCollection)
    TextView tvCollection;

    @BindView(R.id.tvBilledPC)
    TextView tvBilledPC;

    @BindView(R.id.tvTopSPQ)
    TextView tvTopSPQ;

    @BindView(R.id.tvTopSellingPV)
    TextView tvTopSellingPV;

    @BindView(R.id.tvZeroBilledRetailer)
    TextView tvZeroBilledRetailer;

    @BindView(R.id.tvNewRetailer)
    TextView tvNewRetailer;

    @BindView(R.id.tvTopRetailer)
    TextView tvTopRetailer;

    @BindView(R.id.spinnerRetailer)
    Spinner spinnerRetailer;

    @BindView(R.id.llReport)
    LinearLayout llReport;

    @BindView(R.id.llDsrExtraDetails)
    LinearLayout llDsrExtraDetails;


//    @BindView(R.id.llBilledPC)
//    LinearLayout llBilledPC;
//
//
//    @BindView(R.id.llzeroBilledRetailer)
//    LinearLayout llzeroBilledRetailer;
//
//    @BindView(R.id.llDsrExtraDetails)
//    LinearLayout llDsrExtraDetails;
//
//    @BindView(R.id.llDsrExtraDetails)
//    LinearLayout llDsrExtraDetails;


    private SpinnerCustomAdapter adapter;
    private List<String> retailers;
    List<Retailer> retailerList;
    private long fromDate;
    private String strToDate;
    private String strFromDate;
    private String retailerId;
    private String duration;

    LinearLayout llMtrPerformance;
    View view1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsr_report);
        ButterKnife.bind(this);
        SugarContext.init(DSRReportActivity.this);

        getSupportActionBar().setTitle("REPORT");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        llMtrPerformance=(LinearLayout)findViewById(R.id.llMtrPerformance);
        view1=(View)findViewById(R.id.view1);

        retailers = new ArrayList<>();
        retailers.add("All Retailer");
        retailerList = new ArrayList<>();
        final Retailer retailer = new Retailer();
        retailer.setRetailerName("All Customers");
        retailer.setLocalityId("");
        retailerList.add(retailer);

        retailerList.addAll(Retailer.listAll(Retailer.class));


        adapter = new SpinnerCustomAdapter(this, retailerList);

        spinnerRetailer.setAdapter(adapter);

        spinnerRetailer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    retailerId = "";
                    if(duration.matches(Constants.CURRENT_MONTH)){
                        llMtrPerformance.setVisibility(View.VISIBLE);
                        view1.setVisibility(View.VISIBLE);
                    }else{
                        llMtrPerformance.setVisibility(View.GONE);
                        view1.setVisibility(View.GONE);
                    }


                } else {
                    retailerId = retailerList.get(position).getRetailerId();
                    llMtrPerformance.setVisibility(View.GONE);
                    view1.setVisibility(View.GONE);
                }
                getReport();
                Toast.makeText(getApplicationContext(), retailerList.get(position).getRetailerName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        llReport.setVisibility(View.GONE);
        duration = Constants.CURRENT_MONTH;
        getReport();

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.tvCurrentMonth)
    public void currentMonthClick() {

        tvCurrentMonth.setTextColor(getResources().getColor(R.color.white));
        tvCurrentMonth.setBackgroundColor(getResources().getColor(R.color.app_themes));

        tv3Month.setTextColor(getResources().getColor(R.color.app_themes));
        tv3Month.setBackgroundColor(getResources().getColor(R.color.white));
        duration = Constants.CURRENT_MONTH;
        strFromDate = "";
        strToDate = "";
        tvTo.setText("");
        tvFrom.setText("");

        if(retailerId.matches("")){
            llMtrPerformance.setVisibility(View.VISIBLE);
            view1.setVisibility(View.VISIBLE);
        }else{
            llMtrPerformance.setVisibility(View.GONE);
            view1.setVisibility(View.GONE);
        }


        getReport();

    }

    @OnClick(R.id.tv3Month)
    public void threeMonthClick() {
        tv3Month.setTextColor(getResources().getColor(R.color.white));
        tv3Month.setBackgroundColor(getResources().getColor(R.color.app_themes));

        tvCurrentMonth.setTextColor(getResources().getColor(R.color.app_themes));
        tvCurrentMonth.setBackgroundColor(getResources().getColor(R.color.white));
        duration = Constants.THREE_MONTH;
        strFromDate = "";
        strToDate = "";
        tvTo.setText("");
        tvFrom.setText("");

        llMtrPerformance.setVisibility(View.GONE);
        view1.setVisibility(View.GONE);

        getReport();


    }


    @OnClick(R.id.btnSubmit)
    public void submit() {
        if (TextUtils.isEmpty(strFromDate)) {
            Toast.makeText(this, "Select the From Date!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(strToDate)) {
            Toast.makeText(this, "Select the To Date!", Toast.LENGTH_SHORT).show();
            return;
        }
        getReport();

    }


    @OnClick(R.id.tvFrom)
    public void fromDate() {

        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                monthOfYear = monthOfYear + 1;
                String month, day;

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

                try {
                    String str_date = day + "-" + month + "-" + String.valueOf(year);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = (Date) formatter.parse(str_date);
                    fromDate = date.getTime();
                    System.out.println("Today is " + date.getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                tvFrom.setText(day + "/" + month + "/" + String.valueOf(year));
                strFromDate = day + "-" + month + "-" + String.valueOf(year);
//                tvDob.setText(newDate.toString());

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    @OnClick(R.id.tvTo)
    public void toDate() {

        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                String month, day;
                monthOfYear = monthOfYear + 1;
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

                Log.e("Date", " Text --" + day + "/" + month + "/" + String.valueOf(year));
                tvTo.setText(day + "/" + month + "/" + String.valueOf(year));
                strToDate = day + "-" + month + "-" + String.valueOf(year);

//                edDOB.setText(String.valueOf(year) + "-" + month + "-" + day);
//                tvDob.setText(newDate.toString());

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.getDatePicker().setMinDate(fromDate);

        datePickerDialog.show();

    }


    private void bindReportData(Report report) {
        try {

            tvMtdPerformance.setText(report.getMtd());
            tvSalesValue.setText(report.getSalesValue());
            tvCollection.setText(report.getTotalCollection());
            tvBilledPC.setText(report.getBilledPartsValue());
            tvTopSPQ.setText(report.getTopSellingPart());
            tvTopSellingPV.setText(report.getTopSellingPartValue());
            tvZeroBilledRetailer.setText(report.getZeroBilledRetailers());
            tvNewRetailer.setText(report.getNewRetailerDetails());
            tvTopRetailer.setText(report.getTopRetailersDetails());

            llDsrExtraDetails.setVisibility(TextUtils.isEmpty(retailerId) ? View.VISIBLE : View.GONE);

            llReport.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            llReport.setVisibility(View.GONE);
        }


    }

    private void getReport() {
        if (!UIUtil.isInternetAvailable(this)) {
            Toast.makeText(this, "No Internet, Please connect to Internet", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!(TextUtils.isEmpty(strFromDate)) && (!(TextUtils.isEmpty(strToDate)))){
            duration="";
        }

        UIUtil.startProgressDialog(this, "Loading Report, Please wait");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("duration", duration);
        jsonObject.addProperty("retailer_id", TextUtils.isEmpty(retailerId) ? "" : retailerId);
        jsonObject.addProperty("from_date", TextUtils.isEmpty(strFromDate) ? "" : strFromDate);
        jsonObject.addProperty("to_date", TextUtils.isEmpty(strToDate) ? "" : strToDate);

        Log.e("jsonObject", "jsonObject --------- " + jsonObject.toString());

        Call<ReportResponse> call = RetrofitAPI.getInstance().getApi().dsrReport(jsonObject, PreferencesManger.getStringFields(this, Constants.Pref.DEPOT_CODE),
                PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID));

        call.enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                try {
                    Log.e("jsonObject", "jsonObject --------- " + response.body());

                    ReportResponse reportResponse = response.body();
                    Log.e("DistributorResponse", "-------------------------------- P - " + response.body());
                    bindReportData(reportResponse.getReportDetails());
                    Toast.makeText(getApplicationContext(), "" + reportResponse.getMessage(), Toast.LENGTH_SHORT);

                } catch (Exception e) {
                    Log.e("jsonObject", "jsonObject --------- " + e);
                    //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                UIUtil.stopProgressDialog(getApplicationContext());
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
            }
        });

    }


    @OnClick(R.id.llBilledPC)
    public void billedParts() {
        if (!UIUtil.isInternetAvailable(this)) {
            Toast.makeText(this, "No Internet, Please connect to Internet", Toast.LENGTH_SHORT).show();
            return;
        }

        UIUtil.startProgressDialog(this, "Loading Report, Please wait");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("duration", duration);
        jsonObject.addProperty("retailer_id", TextUtils.isEmpty(retailerId) ? "" : retailerId);
        jsonObject.addProperty("from_date", TextUtils.isEmpty(strFromDate) ? "" : strFromDate);
        jsonObject.addProperty("to_date", TextUtils.isEmpty(strToDate) ? "" : strToDate);

        Log.e("jsonObject", "jsonObject --------- " + jsonObject.toString());

        Call<ReportResponse> call = RetrofitAPI.getInstance().getApi().billedProducts(jsonObject, PreferencesManger.getStringFields(this, Constants.Pref.DEPOT_CODE),
                PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID));

        call.enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                try {
                    ReportResponse reportResponse = response.body();

                    showBillPart(reportResponse.getBilledParts());

                    Log.e("DistributorResponse", "-------------------------------- P - " + response.body());
                    Toast.makeText(getApplicationContext(), "" + reportResponse.getMessage(), Toast.LENGTH_SHORT);
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                UIUtil.stopProgressDialog(getApplicationContext());
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
            }
        });


    }

    @OnClick(R.id.llTopRetailer)
    public void topRetailer() {
        if (!UIUtil.isInternetAvailable(this)) {
            Toast.makeText(this, "No Internet, Please connect to Internet", Toast.LENGTH_SHORT).show();
            return;
        }

        UIUtil.startProgressDialog(this, "Loading Report, Please wait");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("duration", duration);
        jsonObject.addProperty("retailer_id", TextUtils.isEmpty(retailerId) ? "" : retailerId);
        jsonObject.addProperty("from_date", TextUtils.isEmpty(strFromDate) ? "" : strFromDate);
        jsonObject.addProperty("to_date", TextUtils.isEmpty(strToDate) ? "" : strToDate);

        Log.e("jsonObject", "jsonObject --------- " + jsonObject.toString());

        Call<ReportResponse> call = RetrofitAPI.getInstance().getApi().topRetailers(jsonObject, PreferencesManger.getStringFields(this, Constants.Pref.DEPOT_CODE),
                PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID));

        call.enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                try {
                    ReportResponse reportResponse = response.body();
                    showZeroBillRetailer(reportResponse.getRetailers());
                    Toast.makeText(getApplicationContext(), "" + reportResponse.getMessage(), Toast.LENGTH_SHORT);
                } catch (Exception e) {
                    e.printStackTrace();
                   // Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                UIUtil.stopProgressDialog(getApplicationContext());
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
            }
        });


    }

    @OnClick(R.id.llzeroBilledRetailer)
    public void ZeroBillRetailer() {
        if (!UIUtil.isInternetAvailable(this)) {
            Toast.makeText(this, "No Internet, Please connect to Internet", Toast.LENGTH_SHORT).show();
            return;
        }

        UIUtil.startProgressDialog(this, "Loading Report, Please wait");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("duration", duration);
        jsonObject.addProperty("retailer_id", TextUtils.isEmpty(retailerId) ? "" : retailerId);
        jsonObject.addProperty("from_date", TextUtils.isEmpty(strFromDate) ? "" : strFromDate);
        jsonObject.addProperty("to_date", TextUtils.isEmpty(strToDate) ? "" : strToDate);

        Log.e("jsonObject", "jsonObject --------- " + jsonObject.toString());

        Call<ReportResponse> call = RetrofitAPI.getInstance().getApi().zeroBilledRetailers(jsonObject, PreferencesManger.getStringFields(this, Constants.Pref.DEPOT_CODE),
                PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID));

        call.enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                try {
                    ReportResponse reportResponse = response.body();
                    showZeroBillRetailer(reportResponse.getRetailers());
                    Log.e("DistributorResponse", "-------------------------------- P - " + response.body());
                    Toast.makeText(getApplicationContext(), "" + reportResponse.getMessage(), Toast.LENGTH_SHORT);
                } catch (Exception e) {
                    e.printStackTrace();
                   // Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                UIUtil.stopProgressDialog(getApplicationContext());
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
            }
        });


    }


    @OnClick(R.id.llNewRetailer)
    public void newRetailer() {
        if (!UIUtil.isInternetAvailable(this)) {
            Toast.makeText(this, "No Internet, Please connect to Internet", Toast.LENGTH_SHORT).show();
            return;
        }

        UIUtil.startProgressDialog(this, "Loading Report, Please wait");
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("duration", duration);
        jsonObject.addProperty("retailer_id", TextUtils.isEmpty(retailerId) ? "" : retailerId);
        jsonObject.addProperty("from_date", TextUtils.isEmpty(strFromDate) ? "" : strFromDate);
        jsonObject.addProperty("to_date", TextUtils.isEmpty(strToDate) ? "" : strToDate);

        Log.e("jsonObject", "jsonObject --------- " + jsonObject.toString());

        Call<ReportResponse> call = RetrofitAPI.getInstance().getApi().newRetailer(jsonObject, PreferencesManger.getStringFields(this, Constants.Pref.DEPOT_CODE),
                PreferencesManger.getStringFields(this, Constants.Pref.DSR_ID));

        call.enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                UIUtil.stopProgressDialog(getApplicationContext());
                try {
                    ReportResponse reportResponse = response.body();
                    showZeroBillRetailer(reportResponse.getRetailers());
                    Log.e("DistributorResponse", "-------------------------------- P - " + response.body());
                    Toast.makeText(getApplicationContext(), "" + reportResponse.getMessage(), Toast.LENGTH_SHORT);
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                UIUtil.stopProgressDialog(getApplicationContext());
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
            }
        });


    }


    private void showBillPart(List<String> list) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_bill_part);

        ListView lv = (ListView) dialog.findViewById(R.id.listView);
        ArrayList<String> arrayList = new ArrayList<>();
        for(int i=0;i<list.size();i++){
           if(!(arrayList.contains(list.get(i)))){
               arrayList.add(list.get(i));
           }
        }
        //arrayList.addAll(list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        dialog.setCancelable(true);
        lv.setAdapter(adapter);
        dialog.show();

    }

    private void showZeroBillRetailer(List<Retailer> list) {
        Log.e("Retailer", "Retailer ------ : " + list.toString());
        if (!(list.size() > 0)) {
            Toast.makeText(this, "Data not available", Toast.LENGTH_SHORT);
            return;
        }

        Dialog dialog = new Dialog(this, android.R.style.Theme_NoTitleBar_Fullscreen);
//        Dialog dialog = new Dialog(this);

        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog.setContentView(R.layout.dailog_retailer);

        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.listView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<Retailer> arrayList = new ArrayList<>();
        arrayList.addAll(list);
        ReportRetailerAdapter adapter = new ReportRetailerAdapter(this, arrayList);
        dialog.setCancelable(true);
        recyclerView.setAdapter(adapter);
        dialog.show();

    }


}
