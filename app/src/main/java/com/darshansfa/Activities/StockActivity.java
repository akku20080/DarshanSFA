package com.darshansfa.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.darshansfa.API.RetrofitAPI;
import com.darshansfa.Adapters.ProductStockAdapter;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.EndlessRecyclerOnScrollListener;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.Product;
import com.darshansfa.dbModel.Stock;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockActivity extends AppCompatActivity {

    @BindView(R.id.recyclerStock)
    RecyclerView recyclerView;

    @BindView(R.id.edSearch)
    EditText searchProduct;

    @BindView(R.id.llLoading)
    LinearLayout llLoading;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private ArrayList<Product> productArrayList;
    private ProductStockAdapter adapter;
    private LinearLayoutManager llm;
    int max = 0;
    private String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        ButterKnife.bind(this);
        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        productArrayList = new ArrayList<>();
        adapter = new ProductStockAdapter(productArrayList);

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Product.executeQuery("VACUUM");

        /*
        Collections.sort(productArrayList, new Comparator<Product>() {
            @Override
            public int compare(Product lhs, Product rhs) {
                return Integer.valueOf(lhs.getCompanyPrice())-Integer.valueOf(rhs.getCompanyPrice());
            }
        });

         */
//        downloadStock(false);


        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(llm) {
            @Override
            public void onLoadMore(int current_page) {
                Log.e("onLoadMore", "--- ");
                max = max + 50;

                new AsyncTaskLoadMoreProduct().execute();

//                if (TextUtils.isEmpty(searchText)) {
//                    List<Product> notes = Product.findWithQuery(Product.class, "Select * from Product GROUP BY part_number LIMIT 50 OFFSET ?", String.valueOf(max));
//                    Log.e("Prod", "------------" + notes);
//                    productArrayList.addAll(notes);
//
//                    recyclerView.post(new Runnable() {
//                        public void run() {
//                            adapter.notifyItemInserted(productArrayList.size() - 1);
//                        }
//                    });
////                    adapter.notifyDataSetChanged();
//                } else {
//                    productArrayList.addAll(Product.findWithQuery(Product.class, "Select * from Product where part_number like '%" + searchText + "%' " +
//                            "or part_name like '%" + searchText + "%' LIMIT 50 OFFSET " + String.valueOf(max) + ";"));
//                    adapter.notifyDataSetChanged();
//                }

            }
        });

        listProduct();

        searchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    listProduct();
                    return;
                }
                if (s.length() < 2)
                    return;
                searchText = s.toString();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        searchProduct();
                    }
                }, 1000);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.sync) {
            downloadStock(true);
//            Toast.makeText(this, "Coming soon..", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dsr_stock, menu);

        return true;
    }


    private void listProduct() {
        new AsyncTaskLoadProduct().execute();
    }

    private void searchProduct() {
        productArrayList.clear();
        max = 0;
        productArrayList.addAll(Product.findWithQuery(Product.class, "Select * from Product where (stock IS NOT NULL AND stock != '' AND transit_stock IS NOT NULL AND transit_stock != '') AND (part_number like '%" + searchText + "%' " +
                "or part_name like '%" + searchText + "%') ORDER BY part_number asc LIMIT 50 OFFSET " + String.valueOf(max) + ";"));

        adapter.notifyDataSetChanged();

    }


    private void downloadStock(boolean pb) {
        if (!UIUtil.isInternetAvailable(this)) {
            UIUtil.noInternet(this, false);
            return;
        }
        if (pb) {
            UIUtil.startProgressDialog(this, "Updating stock please wait..");
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
        Call<List<Stock>> call = RetrofitAPI.getInstance().getApi().getStock(PreferencesManger.getStringFields(this, Constants.Pref.DEPOT_CODE));
        call.enqueue(new Callback<List<Stock>>() {
            @Override
            public void onResponse(Call<List<Stock>> call, Response<List<Stock>> response) {
                try {
                    UIUtil.stopProgressDialog(getApplicationContext());
                    progressBar.setVisibility(View.GONE);

                    List<Stock> stockList = response.body();
                    Log.e("Invoice", "-------------------------------- P - " + stockList);
                    Stock.saveInTx(stockList);
                    //Product.executeQuery("VACUUM");
                    Log.e("Product", "---------before----------------------- P - " + stockList);
                    //Product.executeQuery("UPDATE Product SET stock = '', transit_stock = '';");
                    //Log.e("Product", "---------------after----------------- P - " + stockList);

                    for (int i = 0; i < stockList.size(); i++) {
                        Log.e("Stock", "---------------stock----------------- P - " + stockList.get(i));
                        try {
                            Product.executeQuery("UPDATE Product SET stock = '" + stockList.get(i).getPartAvailableQuantity()
                                    + "', transit_stock = '" + stockList.get(i).getTransitStock() + "' where part_number = '" + stockList.get(i).getPartNumber() + "';");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.e("Invoice", "-----update--------------------------- P - " + stockList.get(i));
                    }
                    productArrayList.clear();
                    listProduct();

                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                    UIUtil.stopProgressDialog(getApplicationContext());
                }
            }

            @Override
            public void onFailure(Call<List<Stock>> call, Throwable t) {
                //Log.e("Error Toast",this.getClass().getSimpleName());
                //Toast.makeText(getApplicationContext(), "Error occured.Please contact support team.", Toast.LENGTH_LONG).show();
                UIUtil.stopProgressDialog(getApplicationContext());
            }
        });
    }

    public class AsyncTaskLoadProduct extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            llLoading.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                productArrayList.clear();
                List<Product> notes = Product.findWithQuery(Product.class, "Select * from Product where (stock IS NOT NULL AND stock != '' AND transit_stock IS NOT NULL AND transit_stock != '') LIMIT 50 OFFSET " +
                        String.valueOf(max) + ";");
                Log.e("Prod", "------------" + notes);
                productArrayList.addAll(notes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg) {
            llLoading.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            adapter.notifyDataSetChanged();
        }
    }


    public class AsyncTaskLoadMoreProduct extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
//            llLoading.setVisibility(View.VISIBLE);
//            recyclerView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                if (TextUtils.isEmpty(searchText)) {
                    List<Product> notes = Product.findWithQuery(Product.class, "Select * from Product where (stock IS NOT NULL AND stock != '' AND transit_stock IS NOT NULL AND transit_stock != '') GROUP BY part_number LIMIT 50 OFFSET ?",
                            String.valueOf(max));
                    Log.e("Prod", "------------" + notes);
                    productArrayList.addAll(notes);
                } else {
                    productArrayList.addAll(Product.findWithQuery(Product.class, "Select * from Product where (stock IS NOT NULL AND stock != '' AND transit_stock IS NOT NULL AND transit_stock != '') AND (part_number like '%"
                            + searchText + "%' " + "or part_name like '%" + searchText + "%') ORDER BY part_number asc LIMIT 50 OFFSET " + String.valueOf(max) + ";"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg) {
//            llLoading.setVisibility(View.GONE);
//            recyclerView.setVisibility(View.VISIBLE);

            adapter.notifyDataSetChanged();
//            recyclerView.post(new Runnable() {
//                public void run() {
//                    adapter.notifyItemInserted(productArrayList.size() - 1);
//                }
//            });
        }
    }


}
