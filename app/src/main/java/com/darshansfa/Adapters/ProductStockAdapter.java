package com.darshansfa.Adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.darshansfa.R;
import com.darshansfa.dbModel.Product;

/**
 * Created by Nikhil on 09-05-2017.
 */

public class ProductStockAdapter extends RecyclerView.Adapter<ProductStockAdapter.ViewHolder> {

    private ArrayList<Product> arrayList;

    public ProductStockAdapter(ArrayList<Product> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public ProductStockAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_stock, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProductStockAdapter.ViewHolder holder, int position) {
        Product product = arrayList.get(position);

        holder.tvProductId.setText(product.getPartNumber());
        holder.tvProductName.setText(product.getPartName());
        Log.e("Stock", "Stock : -------------------: " + product.getStock());
        holder.tvStock.setText((TextUtils.isEmpty(product.getStock()) ? "NA" : product.getStock()));
        holder.tvTransitStock.setText((TextUtils.isEmpty(product.getTransitStock()) ? "NA" : product.getTransitStock()));
        holder.tvCompanyPrice.setText(product.getCompanyPrice());
        holder.tvProductUnit.setText(product.getUnits());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductId, tvProductName, tvStock, tvTransitStock, tvProductUnit, tvCompanyPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvProductId = (TextView) itemView.findViewById(R.id.tvProductId);
            this.tvProductName = (TextView) itemView.findViewById(R.id.tvProductName);
            this.tvProductUnit = (TextView) itemView.findViewById(R.id.tvProductUnit);
            this.tvCompanyPrice = (TextView) itemView.findViewById(R.id.tvCompanyPrice);
            this.tvStock = (TextView) itemView.findViewById(R.id.tvStock);
            this.tvTransitStock = (TextView) itemView.findViewById(R.id.tvTransitStock);

        }
    }
}
