package com.darshansfa.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.SugarContext;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.darshansfa.Adapters.ProductAdapter;
import com.darshansfa.Adapters.ProductTabPagerAdapter;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.EndlessRecyclerOnScrollListener;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.dbModel.Cart;
import com.darshansfa.dbModel.Product;

public class PartSearchActivity extends AppCompatActivity {

    @BindView(R.id.edSearchParts)
    EditText edSearchParts;
    @BindView(R.id.ivClear)
    ImageView ivClear;

    private ViewPager mViewPager;
    TabLayout tabLayout;
    private ProductTabPagerAdapter adapter;
    private ProductAdapter productAdapter;
    private TextView cartText;

    @BindView(R.id.recyclerPart)
    RecyclerView recyclerView;
    private ArrayList<Product> productArrayList;
    private LinearLayoutManager llm;
    private int max = 0;
    private String retailerId;
    private CharSequence searchText;
    public static int TAB = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part_search);
        SugarContext.init(PartSearchActivity.this);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        getSupportActionBar().setTitle("Add Product");

        retailerId = PreferencesManger.getStringFields(this, Constants.Pref.RETAILER_ID);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        productArrayList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productArrayList);
        recyclerView.setAdapter(productAdapter);

        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(llm) {
            @Override
            public void onLoadMore(int current_page) {
                Log.e("onLoadMore", "--- ");
                max = max + 50;
                productArrayList.addAll(Product.findWithQuery(Product.class, "Select * from Product where part_number like '%" + searchText + "%' " +
                        "or part_name like '%" + searchText + "%' ORDER BY part_number asc LIMIT 50 OFFSET " + String.valueOf(max) + ";"));

                productAdapter.notifyDataSetChanged();

            }
        });
        productAdapter.SetOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onAddToCart(View view, int position) {
                updateCartCount();
            }

            @Override
            public void onQuantityChangeListener(Product s, int position) {

            }
        });

        edSearchParts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString()) || s.length() < 2) {
                    tabLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    mViewPager.setVisibility(View.VISIBLE);
                    return;
                }
                tabLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                mViewPager.setVisibility(View.GONE);
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


        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_master));
                tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_category));
                tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_locality));
                tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_associate));
                tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_focused));

                adapter = new ProductTabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

                mViewPager.setAdapter(adapter);

                mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        TAB = position + 1;
                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                mViewPager.setOffscreenPageLimit(5);
                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        mViewPager.setCurrentItem(tab.getPosition());
                        TAB = tab.getPosition() + 1;
                        Log.e("TAB", "-----------------" + TAB);
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
            }
        });


    }

    private void searchProduct() {
        Log.e("Seart", "-----------------" + searchText);
        productArrayList.clear();
        max = 0;
        productArrayList.addAll(Product.findWithQuery(Product.class, "Select * from Product where part_number like '%" + searchText + "%' " +
                "or part_name like '%" + searchText + "%' ORDER BY part_number asc LIMIT 50 OFFSET " + String.valueOf(max) + ";"));

        productAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartCount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_part_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.cart) {
            Toast.makeText(this, "coming soon...!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.ivClear)
    public void clear() {
        edSearchParts.setText("");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.cart);
        RelativeLayout rootView = (RelativeLayout) item.getActionView();
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
            }
        });
        cartText = (TextView) rootView.findViewById(R.id.cartText);
        updateCartCount();
        return super.onPrepareOptionsMenu(menu);

    }


    public void updateCartCount() {
        try {
            int size = Cart.find(Cart.class, "retailer_id = ?", new String[]{retailerId}).size();
            Log.e("Size", "size ------- " + size);
            if (size == 0) {
                cartText.setVisibility(View.GONE);
                return;
            }
            cartText.setVisibility(View.VISIBLE);
            cartText.setText(String.valueOf(size));
        } catch (Exception e) {
//            cartText.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    private void sendBackPressMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent(Constants.ON_BACK_PRESS);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onBackPressed() {
        sendBackPressMessage();
    }
}
