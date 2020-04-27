package com.darshansfa.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.darshansfa.API.RetrofitAPI;
import com.darshansfa.Activities.OrderDetailsActivity;
import com.darshansfa.Activities.PartSearchActivity;
import com.darshansfa.Activities.RetailerDetailTabActivity;
import com.darshansfa.Adapters.OrderAdapter;
import com.darshansfa.Models.OrdersResponse;
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


public class OrdersFragment extends Fragment {

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.recyclerOrder)
    RecyclerView recyclerView;

    @BindView(R.id.tvPlaced)
    TextView tvPlaced;

    @BindView(R.id.tvShipped)
    TextView tvShipped;

    @BindView(R.id.tvLocation)
    TextView tvLocation;

    @BindView(R.id.llNoOrder)
    LinearLayout llNoOrder;

    @BindView(R.id.llSync)
    LinearLayout llSync;


    private List<Orders> ordersArrayList;
    private List<Orders> fullList;
    private OrderAdapter adapter;
    private String retailerID = "";

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter(Constants.RETAILER_LOCATION_CHANGE));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onResume();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        ButterKnife.bind(this, view);

        retailerID = PreferencesManger.getStringFields(getActivity(), Constants.Pref.RETAILER_ID);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(linearLayoutManager);
        ordersArrayList = new ArrayList<>();
        fullList = new ArrayList<>();
        adapter = new OrderAdapter(ordersArrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        placedOrders();

        adapter.SetOnItemClickListener(new OrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PreferencesManger.addStringFields(getActivity(), Constants.Pref.ORDER_NO, ordersArrayList.get(position).getOrderId());
                Intent intent=new Intent(getActivity(), OrderDetailsActivity.class);
                intent.putExtra("from",ordersArrayList.get(position).getStatus());
                startActivity(intent);
            }

            @Override
            public void onCallClick(View view, int position) {

            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        placedOrders();
        List<Retailer> retailerList = Retailer.find(Retailer.class, "retailer_id = ? ", new String[]{retailerID});
        Log.e("retailerList", "retailerList - : " + retailerList.toString());
        if (retailerList.size() > 0) {
            Retailer retailer = retailerList.get(0);
            boolean loc = TextUtils.isEmpty(retailer.getLatitude()) || TextUtils.isEmpty(retailer.getLongitude());
            tvLocation.setVisibility(loc ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @OnClick(R.id.fab)
    public void NewOrder(View view) {
        startActivity(new Intent(getActivity(), PartSearchActivity.class));
        Snackbar.make(view, "Take new order.", Snackbar.LENGTH_LONG).setAction("Order", null).show();
    }

    @OnClick(R.id.tvPlaced)
    public void placedOrders() {

        tvPlaced.setTextColor(getResources().getColor(R.color.white));
        tvPlaced.setBackgroundColor(getResources().getColor(R.color.app_themes));

        tvShipped.setTextColor(getResources().getColor(R.color.app_themes));
        tvShipped.setBackgroundColor(getResources().getColor(R.color.white));

        ordersArrayList.clear();
        ordersArrayList.addAll(Orders.find(Orders.class, "retailer_id = ? and status = ?", new String[]{retailerID, Constants.ORDER_OPEN}));

        fullList.clear();
        fullList.addAll(Orders.find(Orders.class, "retailer_id = ? ", new String[]{retailerID}));
        Log.e("RESULT", String.valueOf(fullList.size()));


        //ordersArrayList.addAll(Orders.find(Orders.class, "retailer_id = ? ", new String[]{retailerID}));
        adapter.notifyDataSetChanged();
        if (ordersArrayList.size() > 0) {
            llNoOrder.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

        } else {
            llNoOrder.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            getOrderForRetailer();
        }

    }

    private void getOrderForRetailer() {

        if (!UIUtil.isInternetAvailable(getContext())) {
            return;
        }

        llSync.setVisibility(View.VISIBLE);
        Call<OrdersResponse> call = RetrofitAPI.getInstance().getApi().getRetailerOrder(retailerID);
        call.enqueue(new Callback<OrdersResponse>() {
            @Override
            public void onResponse(Call<OrdersResponse> call, Response<OrdersResponse> response) {
                llSync.setVisibility(View.GONE);
                try {
                    Log.e("Orders", "Orders : " + response.body().toString());

                    if(!(response.body().getStatus()==0)){
                        List<Orders> orders = response.body().getOrdersList();
//                    Orders.saveInTx(orders);
//                    for (Orders orders1 : orders) {
//                        OrderPart.saveInTx(orders1.getOrderDetails());
//                    }
                        DBUtil.addOrUpdateRetailerOrder(orders, retailerID);
                        placedOrders();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<OrdersResponse> call, Throwable t) {
                llSync.setVisibility(View.GONE);
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
            }
        });

    }

    @OnClick(R.id.tvShipped)
    public void shippedOrders() {

        tvPlaced.setTextColor(getResources().getColor(R.color.app_themes));
        tvPlaced.setBackgroundColor(getResources().getColor(R.color.white));

        tvShipped.setTextColor(getResources().getColor(R.color.white));
        tvShipped.setBackgroundColor(getResources().getColor(R.color.app_themes));
        ordersArrayList.clear();
        ordersArrayList.addAll(Orders.find(Orders.class, "retailer_id = ? and status = ? ", new String[]{retailerID, Constants.ORDER_SHIPPED}));
        ordersArrayList.addAll(Orders.find(Orders.class, "retailer_id = ? and status = ? ", new String[]{retailerID, Constants.ORDER_PARTIAL_SHIPPED}));


        /*
        fullList.clear();
        fullList.addAll(Orders.find(Orders.class, "retailer_id = ? and status = ? ", new String[]{retailerID, Constants.ORDER_PARTIAL_SHIPPED}));

        for (int i=0;i<fullList.size();i++){

            String json=String.valueOf((OrderPart.find(OrderPart.class, "order_id = ? ", new String[]{fullList.get(i).getOrderId()})));

            Log.e("RESULT", String.valueOf((OrderPart.find(OrderPart.class, "order_id = ? ", new String[]{fullList.get(i).getOrderId()}))));
            try {
                JSONArray jsonArray=new JSONArray(json);
                JSONObject jsonObject;
                for(int j=0;j<jsonArray.length();j++){
                    jsonObject=jsonArray.getJSONObject(j);
                    if(jsonObject.getDouble("shipped_quantity")==0.0){

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
         */


        Log.e("RESULT", String.valueOf(SugarRecord.count(OrderPart.class)));

        adapter.notifyDataSetChanged();
        if (ordersArrayList.size() > 0) {
            llNoOrder.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            llNoOrder.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btnNewOrder)
    public void newOrder() {
        startActivity(new Intent(getActivity(), PartSearchActivity.class));
    }

    @OnClick(R.id.tvLocation)
    public void retailerLocation() {
        try {
            ((RetailerDetailTabActivity) getActivity()).getRetailerLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
