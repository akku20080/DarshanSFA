package com.darshansfa.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.darshansfa.Activities.PartSearchActivity;
import com.darshansfa.Adapters.ProductAdapter;
import com.darshansfa.R;
import com.darshansfa.dbModel.Product;

public class ProductFocusedFragment extends Fragment {

    @BindView(R.id.recyclerPartMaster)
    RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private ArrayList<Product> productArrayList;
    private ProductAdapter adapter;
    private int max = 0;

    public ProductFocusedFragment() {
    }

    public static ProductFocusedFragment newInstance() {
        ProductFocusedFragment fragment = new ProductFocusedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_master, container, false);
        ButterKnife.bind(this, view);

        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        productArrayList = new ArrayList<>();
        adapter = new ProductAdapter(getActivity(), productArrayList);
        recyclerView.setAdapter(adapter);

        new loadProduct().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        adapter.SetOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onAddToCart(View view, int position) {

            }

            @Override
            public void onQuantityChangeListener(Product s, int position) {
                productArrayList.set(position, s);

            }
        });

        ((PartSearchActivity) getActivity()).updateCartCount();
        return view;
    }


    private void listProduct() {
        List<Product> notes = Product.find(Product.class, "is_focused_part = ? ", new String[]{"1"});
        Log.e("Prod", "------------" + notes);
        productArrayList.addAll(notes);
    }

    class loadProduct extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            listProduct();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter.notifyDataSetChanged();
        }

    }
}
