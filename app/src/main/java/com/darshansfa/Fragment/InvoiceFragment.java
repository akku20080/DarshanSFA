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
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.darshansfa.Activities.CollectionActivity;
import com.darshansfa.Adapters.InvoiceAdapter;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.Invoice;
import com.darshansfa.dbModel.Retailer;


public class InvoiceFragment extends Fragment {


    @BindView(R.id.recyclerInvoice)
    RecyclerView recyclerView;

    @BindView(R.id.tvTotalOutstanding)
    TextView tvTotalOutstanding;

    @BindView(R.id.tvCreditLimit)
    TextView tvCreditLimit;

    @BindView(R.id.llNoCollection)
    LinearLayout llNoCollection;

    @BindView(R.id.llCollection)
    LinearLayout llCollection;

    private ArrayList<Invoice> invoiceArrayList;
    private InvoiceAdapter adapter;
    private String retailerID = "";
    float total = 0f;

    public InvoiceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invoice, container, false);
        ButterKnife.bind(this, view);

        retailerID = PreferencesManger.getStringFields(getActivity(), Constants.Pref.RETAILER_ID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        invoiceArrayList = new ArrayList<>();

        Log.e("invoice", "iiiiiiii ---- " + invoiceArrayList.size());
        adapter = new InvoiceAdapter(invoiceArrayList);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        adapter.SetOnItemClickListener(new InvoiceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Invoice invoice = invoiceArrayList.get(position);
                Intent intent = new Intent(getActivity(), CollectionActivity.class);
                intent.putExtra(Constants.INVOICE_ID, invoice.getInvoiceId());
                startActivity(intent);
            }

            @Override
            public void onCallClick(View view, int position) {

            }
        });

        try {

            Retailer retailer = Retailer.find(Retailer.class, "retailer_id = ?", new String[]{retailerID}).get(0);
            tvCreditLimit.setText("Credit : " + retailer.getCreditLimit());
        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        invoiceArrayList.clear();
        invoiceArrayList.addAll(Invoice.find(Invoice.class, "retailer_id = ? ", new String[]{retailerID}));

        if (invoiceArrayList.size() > 0) {
            llCollection.setVisibility(View.VISIBLE);
            llNoCollection.setVisibility(View.GONE);
        } else {
            llCollection.setVisibility(View.GONE);
            llNoCollection.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
        updateTotalOutstanding();

    }

//    @OnClick(R.id.tvTotalOutstanding)
//    protected void retailerCollection() {
//        Intent intent = new Intent(getActivity(), CollectionActivity.class);
//        intent.putExtra(Constants.RETAILER_COLLECTION, true);
//        intent.putExtra(Constants.COLLECTION_AMOUNT, total);
//        startActivity(intent);
//    }

    private void updateTotalOutstanding() {
        try {
            total = 0f;
            for (int i = 0; i < invoiceArrayList.size(); i++) {
                Invoice invoice = invoiceArrayList.get(i);
                total += Float.parseFloat(invoice.getTotalAmount()) - Float.parseFloat(invoice.getCollectedAmount());
            }
            tvTotalOutstanding.setText("Outstanding : " + UIUtil.amountFormat(String.valueOf(total)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
