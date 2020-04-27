package com.darshansfa.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.darshansfa.dbModel.Product;

public class ProductAssociateFragment extends Fragment implements HandleBackPressInterface {

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
    public String category;

    public ProductAssociateFragment() {
        // Required empty public constructor
    }

    public static ProductAssociateFragment newInstance(int tab) {
        ProductAssociateFragment fragment = new ProductAssociateFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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

        bindCategory();

        listViewCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), arrayListCategory.get(position), Toast.LENGTH_SHORT).show();
//                listProduct(arrayListCategory.get(position));
                category = arrayListCategory.get(position);
                new listProduct().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        });
        listViewSubCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }

    class FetchData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapterCategory.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(String... params) {
            List<Product> products = Product.findWithQuery(Product.class, "Select * from Product WHERE associate_products_ids IS NOT NULL AND associate_products_ids !='' ;");
            arrayListCategory.clear();
            for (int i = 0; i < products.size(); i++) {
                arrayListCategory.add(products.get(i).getPartNumber() + ",\n" + products.get(i).getPartName());
            }
            return null;
        }
    }

    private void bindCategory() {

        new FetchData().execute();
    }

    private void bindSubCategory(String category) {
        String[] str = category.split(",");
        if (str.length > 0) {
            List<Product> products = Product.findWithQuery(Product.class, "Select * from Product WHERE part_number ='" + str[0] + "';");
            arrayListSubCategory.clear();
            for (int i = 0; i < products.size(); i++) {
                arrayListSubCategory.add(products.get(i).getPartNumber() + ",\n" + products.get(i).getPartName());
            }
            adapterSubCategory.notifyDataSetChanged();
        }
    }


    private void listProduct(String category) {

        recyclerView.setVisibility(View.VISIBLE);
        llCategory.setVisibility(View.GONE);
        Product.executeQuery("VACUUM");
        String[] str = category.split(",");
        productArrayList.clear();
        if (str.length > 0) {
            List<Product> products = Product.findWithQuery(Product.class, "Select * from Product WHERE part_number ='" + str[0] + "';");
            productArrayList.addAll(products);
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onBackPress() {
        return false;
    }


    class listProduct extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            recyclerView.setVisibility(View.VISIBLE);
            llCategory.setVisibility(View.GONE);
            Product.executeQuery("VACUUM");
        }

        @Override
        protected String doInBackground(String... params) {

            Product.executeQuery("VACUUM");
            String[] str = category.split(",");
            productArrayList.clear();
            if (str.length > 0) {
                List<Product> products = Product.findWithQuery(Product.class, "Select * from Product WHERE part_number ='" + str[0] + "';");
                productArrayList.addAll(products);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter.notifyDataSetChanged();
        }

    }
}
