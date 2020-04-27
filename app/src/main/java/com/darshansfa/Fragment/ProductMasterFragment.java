package com.darshansfa.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.darshansfa.Activities.PartSearchActivity;
import com.darshansfa.Adapters.ProductAdapter;
import com.darshansfa.R;
import com.darshansfa.Utility.EndlessRecyclerOnScrollListener;
import com.darshansfa.dbModel.Product;

public class ProductMasterFragment extends Fragment {

    @BindView(R.id.recyclerPartMaster)
    RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private ArrayList<Product> productArrayList;
    private ProductAdapter adapter;
    private int max = 0;
    private CharSequence searchText;

    public ProductMasterFragment() {
        // Required empty public constructor
    }

    public static ProductMasterFragment newInstance() {
        ProductMasterFragment fragment = new ProductMasterFragment();
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


        Product.executeQuery("VACUUM");

        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(llm) {
            @Override
            public void onLoadMore(int current_page) {
                Log.e("onLoadMore", "--- ");
                max = max + 50;

                if (TextUtils.isEmpty(searchText)) {
                    List<Product> notes = Product.findWithQuery(Product.class, "Select * from Product GROUP BY part_number LIMIT 50 OFFSET ?", String.valueOf(max));
                    Log.e("Prod", "------------" + notes);
                    productArrayList.addAll(notes);

                   /* Collections.sort(productArrayList, new Comparator<Product>() {
                        @Override
                        public int compare(Product lhs, Product rhs) {
                            return Integer.valueOf(lhs.getPartNumber())-Integer.valueOf(rhs.getPartNumber());
                        }
                    });*/

                    Collections.sort(productArrayList, new Comparator<Product>() {
                        @Override
                        public int compare(Product lhs, Product rhs) {
                            return lhs.getPartName().compareTo(rhs.getPartName());
                        }
                    });

                    adapter.notifyDataSetChanged();
                } else {
                    productArrayList.addAll(Product.findWithQuery(Product.class, "Select * from Product where part_number like '%" + searchText + "%' " +
                            "or part_name like '%" + searchText + "%' LIMIT 50 OFFSET " + String.valueOf(max) + ";"));

                   /* Collections.sort(productArrayList, new Comparator<Product>() {
                        @Override
                        public int compare(Product lhs, Product rhs) {
                            return Integer.valueOf(lhs.getPartNumber())-Integer.valueOf(rhs.getPartNumber());
                        }
                    });*/

                    Collections.sort(productArrayList, new Comparator<Product>() {
                        @Override
                        public int compare(Product lhs, Product rhs) {
                            return lhs.getPartName().compareTo(rhs.getPartName());
                        }
                    });

                    adapter.notifyDataSetChanged();
                }

            }
        });


        new loadProduct().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        listProduct();

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


    class loadProduct extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Product.executeQuery("VACUUM");
        }

        @Override
        protected String doInBackground(String... params) {
            List<Product> notes = Product.findWithQuery(Product.class, "Select * from Product  LIMIT 50 OFFSET " + String.valueOf(max) + ";");
            Log.e("Prod", "------------" + notes);
            productArrayList.addAll(notes);

           /* Collections.sort(productArrayList, new Comparator<Product>() {
                @Override
                public int compare(Product lhs, Product rhs) {
                    return Integer.valueOf(lhs.getPartNumber())-Integer.valueOf(rhs.getPartNumber());
                }
            });*/

        Collections.sort(productArrayList, new Comparator<Product>() {
            @Override
            public int compare(Product lhs, Product rhs) {
                return lhs.getPartName().compareTo(rhs.getPartName());
            }
        });

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter.notifyDataSetChanged();
        }


    }
}
