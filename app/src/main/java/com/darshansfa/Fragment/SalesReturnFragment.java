package com.darshansfa.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.darshansfa.API.RetrofitAPI;
import com.darshansfa.Activities.SalesReturnActivity;
import com.darshansfa.Adapters.SalesReturnAdapter;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.SalesReturn;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SalesReturnFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final int SALE_RETURN = 3;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.llSync)
    LinearLayout llSync;

    private String retailerID;
    private ArrayList<SalesReturn> arrayList;
    private SalesReturnAdapter adapter;
    private String distributorId;
    private String dsrId;

    public SalesReturnFragment() {
        // Required empty public constructor
    }


    public static SalesReturnFragment newInstance(String param1, String param2) {
        SalesReturnFragment fragment = new SalesReturnFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sales_return, container, false);
        ButterKnife.bind(this, view);

        retailerID = PreferencesManger.getStringFields(getActivity(), Constants.Pref.RETAILER_ID);
        distributorId = PreferencesManger.getStringFields(getActivity(), Constants.Pref.DEPOT_CODE);
        dsrId = PreferencesManger.getStringFields(getActivity(), Constants.Pref.DSR_ID);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        arrayList = new ArrayList<>();
        adapter = new SalesReturnAdapter(arrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        swipeRefreshLayout.setOnRefreshListener(this);
//        swipeRefreshLayout.setColorSchemeColors(getActivity().getColor(R.color.colorAccent));

        getSalesReturn();

        Gson gson = new GsonBuilder().serializeNulls().create();
        String jso = gson.toJson(new SalesReturn());
        Log.e("Json", "sales : ----------------------------------------- " + jso.toString());
        return view;
    }

    private void getSalesReturn() {
        arrayList.clear();
        /*
         SalesReturn salesReturn = new SalesReturn();
        salesReturn.setProductNumber("Dummy Number");
        salesReturn.setInvoiceId("Test - 288882");
        salesReturn.setProductQuantity("10");
        salesReturn.setDate("10-10-2013");
        salesReturn.setReason("other");
        arrayList.add(salesReturn);
         */

        adapter.notifyDataSetChanged();

        if (!UIUtil.isInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), "No Internet!", Toast.LENGTH_SHORT).show();
            return;
        }

        llSync.setVisibility(View.VISIBLE);
        Call<List<SalesReturn>> call = RetrofitAPI.getInstance().getApi().getSalesReturnForRetailer(distributorId, dsrId, retailerID);
        call.enqueue(new Callback<List<SalesReturn>>() {
            @Override
            public void onResponse(Call<List<SalesReturn>> call, Response<List<SalesReturn>> response) {
                llSync.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                try {
                    arrayList.clear();
                    arrayList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<SalesReturn>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                llSync.setVisibility(View.GONE);
                t.printStackTrace();
                //Log.e("Error Toast",this.getClass().getSimpleName());

            }
        });


    }

    @OnClick(R.id.fab)
    protected void addSalesReturn() {
        startActivityForResult(new Intent(getActivity(), SalesReturnActivity.class), SALE_RETURN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRefresh() {
        getSalesReturn();
    }
}
