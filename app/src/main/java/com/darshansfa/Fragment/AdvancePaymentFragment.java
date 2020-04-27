package com.darshansfa.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.darshansfa.API.RetrofitAPI;
import com.darshansfa.Adapters.AdvancePaymentAdapter;
import com.darshansfa.Models.APIResponse;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.dbModel.AdvancePayment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AdvancePaymentFragment extends Fragment {


    @BindView(R.id.recyclerInvoice)
    RecyclerView recyclerView;


    @BindView(R.id.llNoCollection)
    LinearLayout llNoCollection;

    @BindView(R.id.llCollection)
    LinearLayout llCollection;


    @BindView(R.id.llSync)
    LinearLayout llSync;

    private ArrayList<AdvancePayment> arrayList;
    private AdvancePaymentAdapter adapter;
    private String retailerID = "";
    float total = 0f;
    private String distributorId, dsrId, date;
    private boolean isVisibleToUser;

    public AdvancePaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter(Constants.DATE_CHANGE));
        date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            date = intent.getStringExtra("date");
            Log.d("receiver", "Got message: " + date);
            if (isVisibleToUser) {
                onResume();
                getAdvancePaymentDetails();
            }
        }
    };


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;

        if (isVisibleToUser) {
            getAdvancePaymentDetails();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adv_payment, container, false);
        ButterKnife.bind(this, view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        arrayList = new ArrayList<>();
        distributorId = PreferencesManger.getStringFields(getActivity(), Constants.Pref.DISTRIBUTOR_ID);
        dsrId = PreferencesManger.getStringFields(getActivity(), Constants.Pref.DSR_ID);
        Log.e("invoice", "iiiiiiii ---- " + arrayList.size());
        adapter = new AdvancePaymentAdapter(arrayList);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        adapter.SetOnItemClickListener(new AdvancePaymentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onCallClick(View view, int position) {

            }
        });

//        getAdvancePaymentDetails();

        return view;
    }

    private void getAdvancePaymentDetails() {
        arrayList.clear();
        adapter.notifyDataSetChanged();

        llSync.setVisibility(View.VISIBLE);

        Call<APIResponse> call = RetrofitAPI.getInstance().getApi().advancePayment(distributorId, dsrId, date);
        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                llSync.setVisibility(View.GONE);
                try {

                    APIResponse apiResponse = response.body();
                    arrayList.clear();
                    arrayList.addAll(apiResponse.getAdvancePaymentList());
                    if (arrayList.size() > 0) {
                        llCollection.setVisibility(View.VISIBLE);
                        llNoCollection.setVisibility(View.GONE);
                    } else {
                        llCollection.setVisibility(View.GONE);
                        llNoCollection.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                llSync.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "No data", Toast.LENGTH_SHORT).show();
                llCollection.setVisibility(View.GONE);
                llNoCollection.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
                t.printStackTrace();
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

}
