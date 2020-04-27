package com.darshansfa.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.darshansfa.Adapters.BackOrderDetailsAdapter;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.OrderPart;

public class BackOrderDetailsActivity extends AppCompatActivity {

    @BindView(R.id.recyclerOrderDetails)
    RecyclerView recyclerOrderDetails;

    @BindView(R.id.tvTotal)
    TextView tvTotal;

    ArrayList<OrderPart> orderPartArrayList;
    private BackOrderDetailsAdapter adapter;
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_order_details);
        ButterKnife.bind(this);

        recyclerOrderDetails.setHasFixedSize(true);
        recyclerOrderDetails.setLayoutManager(new LinearLayoutManager(this));
        orderPartArrayList = new ArrayList<>();
        orderPartArrayList.addAll(OrderPart.find(OrderPart.class, "order_id = ? ", new String[]{PreferencesManger.getStringFields(this, Constants.Pref.ORDER_NO)}));
        adapter = new BackOrderDetailsAdapter(orderPartArrayList);
        recyclerOrderDetails.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        setTitle(PreferencesManger.getStringFields(this, Constants.Pref.ORDER_NO));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTotal();
    }

    private void setTotal() {
        double total = 0d;
        for (int i = 0; i < orderPartArrayList.size(); i++) {
            try {
                total = total + orderPartArrayList.get(i).getLineTotal();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        tvTotal.setText("Total : " + UIUtil.amountFormat(String.valueOf(total)));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
