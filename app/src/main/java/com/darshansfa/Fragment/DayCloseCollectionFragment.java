package com.darshansfa.Fragment;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.darshansfa.API.RetrofitAPI;
import com.darshansfa.Activities.DayCloseActivity;
import com.darshansfa.Adapters.DayCloseCollectionAdapter;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.Collection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DayCloseCollectionFragment extends Fragment {

    @BindView(R.id.recyclerCollection)
    RecyclerView recyclerView;


    @BindView(R.id.llSync)
    LinearLayout llSync;

    @BindView(R.id.llNoCollection)
    LinearLayout llNoCollection;

    private List<Collection> collectionArrayList;
    private DayCloseCollectionAdapter adapter;

    private String date;
    private boolean isVisibleToUser = false;

    public DayCloseCollectionFragment() {
        // Required empty public constructor
    }

    public static DayCloseCollectionFragment newInstance(String param1, String param2) {
        DayCloseCollectionFragment fragment = new DayCloseCollectionFragment();

        return fragment;
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
                getCollection();
            }

        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;

        if (isVisibleToUser) {
            getCollection();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_day_close_collection, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        collectionArrayList = new ArrayList<>();
//        collectionArrayList.addAll(Collection.listAll(Collection.class));
        adapter = new DayCloseCollectionAdapter(collectionArrayList);
//        Log.e("collectionArrayList", "collectionArrayList --- " + collectionArrayList.toString());

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
//        getCollection();
        return view;
    }


    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @OnClick(R.id.btnChangeDate)
    public void changeDate() {
        openDatePicker();
    }


    private void openDatePicker() {
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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
                try {
                    ((DayCloseActivity) getActivity()).setDate(date);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                getCollection();
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void getCollection() {
        if (!UIUtil.isInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
            return;
        }

//        UIUtil.startProgressDialog(getActivity(), "Getting collection.. ");
        llSync.setVisibility(View.VISIBLE);
        Call<JsonObject> call = RetrofitAPI.getInstance().getApi().getCollection(
                PreferencesManger.getStringFields(getActivity(), Constants.Pref.DEPOT_CODE),
                PreferencesManger.getStringFields(getActivity(), Constants.Pref.DSR_ID), date);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                UIUtil.stopProgressDialog(getActivity());
                llSync.setVisibility(View.GONE);
                Log.e("Collection", "-------------------------------- P - " + response.body());
                try {

                    JsonObject obj = response.body();

                    if (1 == obj.get("status").getAsInt()) {
                        Type listType = new TypeToken<List<Collection>>() {
                        }.getType();
                        List<Collection> yourClassList = new Gson().fromJson(obj.getAsJsonArray("collection_details_list"), listType);

                        collectionArrayList.clear();
                        collectionArrayList.addAll(yourClassList);
                        if (collectionArrayList.size() > 0) {
                            llNoCollection.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            llNoCollection.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), obj.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                llSync.setVisibility(View.GONE);
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                try {
                    collectionArrayList.clear();
                    if (collectionArrayList.size() > 0) {
                        llNoCollection.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        llNoCollection.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
