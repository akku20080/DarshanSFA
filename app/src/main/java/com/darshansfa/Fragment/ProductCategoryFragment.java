package com.darshansfa.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.darshansfa.Activities.PartSearchActivity;
import com.darshansfa.Adapters.ProductAdapter;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.EndlessRecyclerOnScrollListener;
import com.darshansfa.dbModel.Product;

public class ProductCategoryFragment extends Fragment {

    @BindView(R.id.recyclerPart)
    RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private ArrayList<Product> productArrayList;
    private ProductAdapter adapter;
    private ArrayAdapter<String> adapterCategory, adapterSubCategory;
    private ArrayList<String> arrayListCategory, arrayListSubCategory;
    private int max = 0;
    private CharSequence searchText;

    @BindView(R.id.llCategory)
    LinearLayout llCategory;

    @BindView(R.id.listViewCategory)
    ListView listViewCategory;

    @BindView(R.id.listViewSubCategory)
    ListView listViewSubCategory;

    public ProductCategoryFragment() {
        // Required empty public constructor
    }

    public static ProductCategoryFragment newInstance(int tab) {
        ProductCategoryFragment fragment = new ProductCategoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter(Constants.ON_BACK_PRESS));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (llCategory.getVisibility() == View.VISIBLE) {
                getActivity().finish();
            } else {
                llCategory.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_category, container, false);
        ButterKnife.bind(this, view);

        //Bind  List View
        bindListView();


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
                    adapter.notifyDataSetChanged();
                } else {
                    productArrayList.addAll(Product.findWithQuery(Product.class, "Select * from Product where part_number like '%" + searchText + "%' " +
                            "or part_name like '%" + searchText + "%' LIMIT 50 OFFSET " + String.valueOf(max) + ";"));
                    adapter.notifyDataSetChanged();
                }

            }
        });


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

    private void bindListView() {
        arrayListCategory = new ArrayList<>();
        arrayListSubCategory = new ArrayList<>();

        adapterCategory = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayListCategory);
        adapterSubCategory = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arrayListSubCategory);

        listViewCategory.setAdapter(adapterCategory);
        listViewSubCategory.setAdapter(adapterSubCategory);

        adapterCategory.notifyDataSetChanged();
        adapterSubCategory.notifyDataSetChanged();

        new bindCategory().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        listViewCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), arrayListCategory.get(position), Toast.LENGTH_SHORT).show();
                bindSubCategory(arrayListCategory.get(position));
            }
        });
        listViewSubCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listProductFromSubCategory(arrayListSubCategory.get(position));
            }
        });

    }

    private void bindCategory() {
        List<Product> products = Product.findWithQuery(Product.class, "Select * from Product WHERE part_category IS NOT NULL GROUP BY part_category;");
        arrayListCategory.clear();
        for (int i = 0; i < products.size(); i++) {
            arrayListCategory.add(products.get(i).getPartCategory());
        }
        adapterCategory.notifyDataSetChanged();
    }

    private void bindSubCategory(String category) {
        List<Product> products = Product.findWithQuery(Product.class, "Select * from Product WHERE part_category ='" + category + "' GROUP BY part_number;");
        arrayListSubCategory.clear();
        for (int i = 0; i < products.size(); i++) {
            arrayListSubCategory.add(products.get(i).getPartNumber() + ",\n" + products.get(i).getPartName());
        }
        adapterSubCategory.notifyDataSetChanged();
    }

    private void listProductFromSubCategory(String product) {

        try {
            String[] strings = product.split(",");
            if (strings.length > 0) {
                llCategory.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                productArrayList.clear();
                productArrayList.addAll(Product.find(Product.class, "part_number = ?", new String[]{strings[0]}));
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Product not found", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void listProduct() {
        Product.executeQuery("VACUUM");
        List<Product> notes = Product.findWithQuery(Product.class, "Select * from Product  LIMIT 50 OFFSET " + String.valueOf(max) + ";");
        Log.e("Prod", "------------" + notes);
//        Log.e("Prod", "------------ all " + Product.listAll(Product.class));
        productArrayList.addAll(notes);
        adapter.notifyDataSetChanged();
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
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter.notifyDataSetChanged();
        }

    }

    class bindCategory extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Product.executeQuery("VACUUM");
        }

        @Override
        protected String doInBackground(String... params) {
            List<Product> products = Product.findWithQuery(Product.class, "Select * from Product WHERE part_category IS NOT NULL GROUP BY part_category;");
            arrayListCategory.clear();
            for (int i = 0; i < products.size(); i++) {
                arrayListCategory.add(products.get(i).getPartCategory());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter.notifyDataSetChanged();
        }

    }


    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}
