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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
import com.darshansfa.Adapters.DayCloseOrderAdapter;
import com.darshansfa.Models.APIResponse;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.DBUtil;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.OrderPart;
import com.darshansfa.dbModel.Orders;
import com.darshansfa.dbModel.Retailer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DayCloseOrderFragment extends Fragment {


    @BindView(R.id.recyclerOrder)
    RecyclerView recyclerView;


    @BindView(R.id.llNoOrder)
    LinearLayout llNoOrder;


    @BindView(R.id.llSync)
    LinearLayout llSync;

    private ArrayList<Orders> ordersArrayList;
    private DayCloseOrderAdapter adapter;

    private String date;
    String pjp_id;
    private String latitude, longitude;
    private boolean isVisibleToUser, isLoaded;
    private Retailer retailer;

    int isStart=0;

    public DayCloseOrderFragment() {
        // Required empty public constructor
    }

    public static DayCloseOrderFragment newInstance(String param1, String param2) {
        DayCloseOrderFragment fragment = new DayCloseOrderFragment();

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter(Constants.DATE_CHANGE));
        date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        pjp_id = "";
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            date = intent.getStringExtra("date");
            Log.d("receiver", "Got message: " + date);
            if (isVisibleToUser)
                bindOrder(true);
        }
    };


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;

        if (isVisibleToUser && isLoaded) {
            bindOrder(true);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_close_order, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ordersArrayList = new ArrayList<>();
        adapter = new DayCloseOrderAdapter(ordersArrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

//        Orders.deleteAll(Orders.class);
//        OrderPart.deleteAll(OrderPart.class);

        adapter.SetOnItemClickListener(new DayCloseOrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //PreferencesManger.addStringFields(getActivity(), Constants.Pref.ORDER_NO, ordersArrayList.get(position).getOrderId());
                //startActivity(new Intent(getActivity(), OrderDetailsActivity.class));
            }

            @Override
            public void onSubmitClick(View view, int position) {
                /*
                 if (PreferencesManger.getStringFields(getActivity(), Constants.Pref.PJPID).isEmpty()) {
                    if (!UIUtil.isInternetAvailable(getActivity())) {
                        Toast.makeText(getActivity(), "No Internet!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    updateStartStop(position);
                    placeOrder(position);
                    PreferencesManger.addStringFields(getActivity(), Constants.Pref.PJPID, "");
                } else {

                    if (!UIUtil.isInternetAvailable(getActivity())) {
                        Toast.makeText(getActivity(), "No Internet!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    placeOrder(position);

                }
                 */

                if (!UIUtil.isInternetAvailable(getActivity())) {
                    Toast.makeText(getActivity(), "No Internet!", Toast.LENGTH_SHORT).show();
                    return;
                }
                isStart=0;
                updateStartStop(position);
                //placeOrder(position);
                //PreferencesManger.addStringFields(getActivity(), Constants.Pref.PJPID, "");

            }
        });
        isLoaded = true;
        bindOrder(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @OnClick(R.id.tvDate)
    protected void openDate() {
        openDatePicker();
    }

    private void getOrdersForDate() {
        if (!UIUtil.isInternetAvailable(getActivity())) {
            return;
        }

        llSync.setVisibility(View.VISIBLE);
        Call<APIResponse> call = RetrofitAPI.getInstance().getApi().getOrdersForDate(PreferencesManger.getStringFields(getActivity(), Constants.Pref.DEPOT_CODE),
                PreferencesManger.getStringFields(getActivity(), Constants.Pref.DSR_ID), date);
        call.enqueue(new Callback<APIResponse>() {
            @Override
            public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                llSync.setVisibility(View.GONE);
                try {
                    Log.e("orderses", "-------------------------------- P - " + response.body());
                    List<Orders> orderses = new ArrayList<Orders>();
                    APIResponse apiResponse = response.body();
                    orderses.addAll(apiResponse.getOrdersList());
                    DBUtil.addOrUpdateOrder(orderses);
                    bindOrder(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<APIResponse> call, Throwable t) {
                llSync.setVisibility(View.GONE);
                t.printStackTrace();
                Toast.makeText(getContext(), "No Orders", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private void bindOrder(boolean syncFromAPI) {
        if (ordersArrayList == null)
            ordersArrayList = new ArrayList<>();

        ordersArrayList.clear();
        try {
            date = ((DayCloseActivity) getActivity()).getDate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ordersArrayList.addAll(Orders.find(Orders.class, "order_date = ? ", new String[]{date}));
        if (ordersArrayList.size() == 0 && syncFromAPI) {
            getOrdersForDate();
        }

        llNoOrder.setVisibility(ordersArrayList.size() == 0 ? View.VISIBLE : View.GONE);


        if (adapter == null) {
            adapter = new DayCloseOrderAdapter(ordersArrayList);
            recyclerView.setAdapter(adapter);
        }

        adapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
        Log.e("Orders", "Order : " + Orders.listAll(Orders.class));
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

                bindOrder(true);
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateStartStop(final int position) {
        final Orders order = ordersArrayList.get(position);
        try {
            retailer = Retailer.find(Retailer.class, "retailer_id = ?", new String[]{order.getRetailerId()}).get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        UIUtil.startProgressDialog(getActivity(), "Update PJP, please wait...");
        JsonObject jsonObject = new JsonObject();

        String distributorId = PreferencesManger.getStringFields(getActivity(), Constants.Pref.DEPOT_CODE);
        String dsrId = PreferencesManger.getStringFields(getActivity(), Constants.Pref.DSR_ID);

        if (PreferencesManger.getStringFields(getActivity(), Constants.Pref.PJPID).isEmpty()) {
            jsonObject.addProperty("distributor_id", distributorId);
            jsonObject.addProperty("dsr_id", dsrId);
            jsonObject.addProperty("latitude", latitude);
            jsonObject.addProperty("longitude", longitude);
            jsonObject.addProperty("flag", retailer.isVisitStart() ? 0 : 1);
            jsonObject.addProperty("retailer_id", retailer.getRetailerId());
            jsonObject.addProperty("date", retailer.getStartDate());
            jsonObject.addProperty("time", retailer.getStartTime());
        } else {
            jsonObject.addProperty("pjp_id", PreferencesManger.getStringFields(getActivity(), Constants.Pref.PJPID));
            jsonObject.addProperty("latitude", latitude);
            jsonObject.addProperty("longitude", longitude);
            jsonObject.addProperty("flag", retailer.isVisitStart() ? 0 : 1);
            jsonObject.addProperty("date", retailer.getStopDate());
            jsonObject.addProperty("time", retailer.getStopTime());

        }

        Log.e("jsonArray", "-------------------------------- P - " + jsonObject);

        Call<JsonObject> call = RetrofitAPI.getInstance().getApi().updatePJPStartStop(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                UIUtil.stopProgressDialog(getActivity());
                try {
                    JsonObject object = response.body();

                    Toast.makeText(getActivity(), "" + object.get("message").getAsString(), Toast.LENGTH_SHORT).show();

                    if (object.get("status").getAsInt() == 1) {
                        Log.e("res", "----------" + response.body());
                        if (isStart==0) {
                            pjp_id = object.get("pjp_id").getAsString();
                            PreferencesManger.addStringFields(getActivity(), Constants.Pref.PJPID, object.get("pjp_id").getAsString());
                        }else{
                            PreferencesManger.addStringFields(getActivity(), Constants.Pref.PJPID, "");

                        }

                        retailer.setVisitStart(!retailer.isVisitStart());
                        retailer.save();

                        if (isStart==0) {
                            placeOrder(position);
                            return;
                        }

                        getOrdersForDate();
                        //adapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                UIUtil.stopProgressDialog(getActivity());
                t.printStackTrace();
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void placeOrder(final int position) {

        UIUtil.startProgressDialog(getActivity(), "Submitting order, please wait...");

        final Orders order = ordersArrayList.get(position);
        JsonArray jsonArray = new JsonArray();
        JsonObject object = new JsonObject();
        object.addProperty("order_id", order.getOrderId());
        object.addProperty("pjp_id", PreferencesManger.getStringFields(getActivity(), Constants.Pref.PJPID));
        object.addProperty("retailer_id", order.getRetailerId());
        object.addProperty("distributor_id", order.getDistributorId());
        object.addProperty("dsr_id", order.getDsrId());

        try {
            object.addProperty("latitude", order.getLatitude());
            object.addProperty("longitude", order.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }

        object.addProperty("order_placed_by", 2);
        object.addProperty("has_advanced_payment", 0);


        JsonArray array = new JsonArray();
        List<OrderPart> orderParts = OrderPart.find(OrderPart.class, "order_id = ?", new String[]{order.getOrderId()});

        for (int i = 0; i < orderParts.size(); i++) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("part_number", orderParts.get(i).getPartId());
            jsonObject.addProperty("qty", orderParts.get(i).getQuantity());
            jsonObject.addProperty("line_total", orderParts.get(i).getLineTotal());
            array.add(jsonObject);
        }


        object.add("order_items", array);
        jsonArray.add(object);


        Call<JsonObject> call = RetrofitAPI.getInstance().getApi().placeOrder(jsonArray, order.getDistributorId(), order.getDsrId());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                UIUtil.stopProgressDialog(getActivity());
                try {
                    JsonObject object = response.body();
                    Toast.makeText(getActivity(), "" + object.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    if (object.get("status").getAsInt() == 1) {
                        order.setStatus(Constants.ORDER_OPEN);
                        order.save();
                        isStart=1;
                        updateStartStop(position);
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Server not responding, Try after some time.. ", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                UIUtil.stopProgressDialog(getActivity());
                Toast.makeText(getActivity(), "Server not responding, Try after some time.. ", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });


    }

}
