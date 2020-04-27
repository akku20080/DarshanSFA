package com.darshansfa.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.darshansfa.Activities.BackOrderDetailsActivity;
import com.darshansfa.Adapters.BackOrderAdapter;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.Orders;
import com.darshansfa.dbModel.Retailer;


public class BackOrderFragment extends Fragment {

    @BindView(R.id.llSync)
    LinearLayout llSync;

    @BindView(R.id.recyclerBackOrder)
    RecyclerView recyclerView;
    private String retailerID;

    @BindView(R.id.llNoOrder)
    LinearLayout llNoOrder;

    private ArrayList<Orders> backOrderArrayList;
    private BackOrderAdapter adapter;


    public BackOrderFragment() {
        // Required empty public constructor
    }

    public static BackOrderFragment newInstance(String param1, String param2) {
        BackOrderFragment fragment = new BackOrderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_back_order, container, false);
        ButterKnife.bind(this, view);

        retailerID = PreferencesManger.getStringFields(getActivity(), Constants.Pref.RETAILER_ID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        backOrderArrayList = new ArrayList<>();
        adapter = new BackOrderAdapter(backOrderArrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        getBackOrders();

        adapter.SetOnItemClickListener(new BackOrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PreferencesManger.addStringFields(getActivity(), Constants.Pref.ORDER_NO, backOrderArrayList.get(position).getOrderId());
                Intent intent=new Intent(getActivity(), BackOrderDetailsActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCallClick(View view, int position) {

            }
        });

        return view;
    }

    private void getBackOrders() {

        backOrderArrayList.clear();
        backOrderArrayList.addAll(Orders.find(Orders.class, "retailer_id = ? and status = ?", new String[]{retailerID, Constants.ORDER_PARTIAL_SHIPPED}));

        adapter.notifyDataSetChanged();
        if (backOrderArrayList.size() > 0) {
            llNoOrder.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

        } else {
            llNoOrder.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            //getBackOrderList();
        }
    }

    private void getBackOrderList() {
        if (!UIUtil.isInternetAvailable(getContext())) {
            return;
        }

        llSync.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getBackOrders();
        List<Retailer> retailerList = Retailer.find(Retailer.class, "retailer_id = ? ", new String[]{retailerID});
        Log.e("retailerList", "retailerList - : " + retailerList.toString());
    }
}
