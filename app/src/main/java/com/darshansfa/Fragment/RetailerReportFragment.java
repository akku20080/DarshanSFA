package com.darshansfa.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.darshansfa.API.RetrofitAPI;
import com.darshansfa.Models.Report;
import com.darshansfa.Models.ReportResponse;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetailerReportFragment extends Fragment {

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

    @BindView(R.id.tvCurrentMonth)
    TextView tvCurrentMonth;

    @BindView(R.id.tv3Month)
    TextView tv3Month;

    @BindView(R.id.llSync)
    LinearLayout llSync;


    private String duration, strFromDate, strToDate;
    private String retailerId;
    private Report report;
    private boolean isLoad;


    public RetailerReportFragment() {
        // Required empty public constructor
    }


    public static RetailerReportFragment newInstance(String param1, String param2) {
        RetailerReportFragment fragment = new RetailerReportFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_retailer_report, container, false);
        ButterKnife.bind(this, view);
        retailerId = PreferencesManger.getStringFields(getActivity(), Constants.Pref.RETAILER_ID);
        duration = Constants.CURRENT_MONTH;
        isLoad = true;
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && report == null && isLoad) {
            getReport();
        }
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
        getReport();

    }

    private void getReport() {
        if (!UIUtil.isInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), "No Internet, Please connect to Internet", Toast.LENGTH_SHORT).show();
            return;
        }

        llSync.setVisibility(View.VISIBLE);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("duration", duration);
        jsonObject.addProperty("retailer_id", TextUtils.isEmpty(retailerId) ? "" : retailerId);
        jsonObject.addProperty("from_date", TextUtils.isEmpty(strFromDate) ? "" : strFromDate);
        jsonObject.addProperty("to_date", TextUtils.isEmpty(strToDate) ? "" : strToDate);

        Log.e("jsonObject", "jsonObject --------- " + jsonObject.toString());

        Call<ReportResponse> call = RetrofitAPI.getInstance().getApi().dsrReport(jsonObject, PreferencesManger.getStringFields(getActivity(),
                Constants.Pref.DEPOT_CODE),
                PreferencesManger.getStringFields(getActivity(), Constants.Pref.DSR_ID));

        call.enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> response) {
                llSync.setVisibility(View.GONE);
                try {
                    ReportResponse reportResponse = response.body();
                    Log.e("DistributorResponse", "-------------------------------- P - " + response.body());
                    bindReportData(reportResponse.getReportDetails());
                    Toast.makeText(getActivity(), "" + reportResponse.getMessage(), Toast.LENGTH_SHORT);

                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                    tvMtdPerformance.setText("NA");
                    tvSalesValue.setText("NA");
                    tvCollection.setText("NA");
                    tvBilledPC.setText("NA");
                    tvTopSPQ.setText("NA");
                    tvTopSellingPV.setText("NA");

                }
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                llSync.setVisibility(View.GONE);
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void bindReportData(Report report) {
        try {
            this.report = report;
            tvMtdPerformance.setText(report.getMtd());
            tvSalesValue.setText(UIUtil.amountFormat(report.getSalesValue()));
            tvCollection.setText(UIUtil.amountFormat(report.getTotalCollection()));
            tvBilledPC.setText(report.getBilledPartsValue());
            tvTopSPQ.setText(report.getTopSellingPart());
            tvTopSellingPV.setText(report.getTopSellingPartValue());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
