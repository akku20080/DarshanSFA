package com.darshansfa.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.darshansfa.Activities.PartSearchActivity;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.Cart;
import com.darshansfa.dbModel.Product;

/**
 * Created by Nikhil on 09-05-2017.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private ArrayList<Product> arrayList;

    private OnItemClickListener clickListener;
    private Product product;
    private String str, retailerId, depoId, dsrId;
    private Activity context;

    public ProductAdapter(Activity context, ArrayList<Product> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        retailerId = PreferencesManger.getStringFields(context, Constants.Pref.RETAILER_ID);
        depoId = PreferencesManger.getStringFields(context, Constants.Pref.DSR_ID);
        dsrId = PreferencesManger.getStringFields(context, Constants.Pref.DSR_ID);
    }

    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product, parent, false);
        return new ViewHolder(v, new CustomEditTextListener());
    }

    @Override
    public void onBindViewHolder(ProductAdapter.ViewHolder holder, int position) {
        Product product = arrayList.get(position);
        holder.editTextListener.updatePosition(position, holder);
        holder.tvProductId.setText(product.getPartNumber());
        holder.tvUnit.setText("Unit : " + product.getUnits());
        holder.tvProductName.setText(product.getPartName());
        holder.tvAreaAvg.setText("Area Avg : NA");
        holder.tvRetailerAvg.setText("Retailer Avg : NA");
        holder.tvMRP.setText("MRP :  " + product.getMrp());
        holder.tvMUP.setText("MUP :  " + product.getMup());
        holder.tvCompanyPrice.setText("Company Price :  " + product.getCompanyPrice());
        holder.tvTransitStock.setText("Transit Stock :  " + (TextUtils.isEmpty(product.getTransitStock()) ? "NA" : product.getTransitStock()));
        holder.tvStock.setText("Stock :  " + (TextUtils.isEmpty(product.getStock()) ? "NA" : product.getStock()));
        holder.tvSubTotal.setText((product.getSubTotal() != null) ? "" + product.getSubTotal() : "");
        holder.edOrderQty.setText((product.getOrderQty() != null) ? "" + product.getOrderQty() : "");
        holder.tvTotalClosingStock.setText(UIUtil.TotalClosingStock(product));
        holder.tvHSNCode.setText("HSN : " + UIUtil.getText(product.getHsnCode()));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvProductId, tvUnit, tvProductName, tvAreaAvg, tvRetailerAvg, tvStock, tvTransitStock, tvMRP,
                tvTotalClosingStock, tvSubTotal, btnAddPart, tvHSNCode, tvCompanyPrice, tvMUP;
        EditText edOrderQty;
        public CustomEditTextListener editTextListener;

        public ViewHolder(View itemView, CustomEditTextListener editTextListener) {
            super(itemView);
            this.editTextListener = editTextListener;
            this.tvProductId = (TextView) itemView.findViewById(R.id.tvProductId);
            this.tvUnit = (TextView) itemView.findViewById(R.id.tvUnit);
            this.tvProductName = (TextView) itemView.findViewById(R.id.tvProductName);
            this.tvAreaAvg = (TextView) itemView.findViewById(R.id.tvAreaAvg);
            this.tvRetailerAvg = (TextView) itemView.findViewById(R.id.tvRetailerAvg);
            this.tvStock = (TextView) itemView.findViewById(R.id.tvStock);
            this.tvTransitStock = (TextView) itemView.findViewById(R.id.tvTransitStock);
            this.tvMRP = (TextView) itemView.findViewById(R.id.tvMRP);
            this.tvMUP = (TextView) itemView.findViewById(R.id.tvMUP);
            this.tvCompanyPrice = (TextView) itemView.findViewById(R.id.tvCompanyPrice);
            this.tvTotalClosingStock = (TextView) itemView.findViewById(R.id.tvTotalClosingStock);
            this.tvSubTotal = (TextView) itemView.findViewById(R.id.tvSubTotal);
            this.edOrderQty = (EditText) itemView.findViewById(R.id.edOrderQty);
            this.tvHSNCode = (TextView) itemView.findViewById(R.id.tvHSNCode);

            this.btnAddPart = (TextView) itemView.findViewById(R.id.btnAddPart);
            itemView.setOnClickListener(this);
            this.btnAddPart.setOnClickListener(this);
            edOrderQty.addTextChangedListener(this.editTextListener);
//            edOrderQty.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    try {
//                        if (TextUtils.isEmpty(s))
//                            return;
//                        Log.e("p", "=--------------------------" + getAdapterPosition());
//                        product = arrayList.get(getAdapterPosition());
//                        Log.e("p", "=--------------------------" + product.toString());
//                        Double subTotal = Double.parseDouble(s.toString());
//                        product.setSubTotal(Double.parseDouble(product.getMrp()) * subTotal);
//                        product.setOrderQty(subTotal);
//                        ViewHolder.this.tvSubTotal.setText(UIUtil.amountFormat(String.valueOf(product.getSubTotal())));
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    if (clickListener != null) {
//                        clickListener.onQuantityChangeListener(product, getAdapterPosition());
//                    }
//
//                }
//            });
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                switch (v.getId()) {
                    case R.id.btnAddPart:
                        addToCart(getAdapterPosition());

                        break;
                    default:
                        clickListener.onItemClick(v, getAdapterPosition());
                        break;
                }
            }

        }
    }

    private void addToCart(int adapterPosition) {
        Product product = arrayList.get(adapterPosition);
        if (product.getOrderQty() == null || product.getOrderQty() <= 0) {
            Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Cart.find(Cart.class, "part_number = ? and retailer_id = ? ", new String[]{product.getPartNumber(), retailerId}).size() > 0) {
            Toast.makeText(context, "Already added", Toast.LENGTH_SHORT).show();
            return;
        }

        Cart cart = product.getCartItem();
        cart.setRetailerId(retailerId);
        cart.setDepoId(depoId);
        cart.setDsrId(dsrId);
        long l = cart.save();
        if (l > 0) {
            arrayList.remove(adapterPosition);
            Toast.makeText(context, "Added successfully", Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
        }

        if (clickListener != null)
            clickListener.onAddToCart(null, 0);


        ((PartSearchActivity) context).updateCartCount();


    }

    public void SetOnItemClickListener(OnItemClickListener onItemClickListener) {
        clickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onAddToCart(View view, int position);

        public void onQuantityChangeListener(Product s, int position);

    }


    private class CustomEditTextListener implements TextWatcher {
        private int position;
        private ViewHolder holder;

        public void updatePosition(int position, ViewHolder holder) {
            this.position = position;
            this.holder = holder;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            String qty = charSequence.toString();
            Product product = arrayList.get(this.position);
            try {
                if (!TextUtils.isEmpty(qty)) {
                    Double subTotal = Double.parseDouble(qty);
                    product.setSubTotal(Double.parseDouble(product.getCompanyPrice()) * subTotal);
                    product.setOrderQty(subTotal);
                    holder.tvSubTotal.setText(UIUtil.amountFormat(String.valueOf(product.getSubTotal())));
                }

            } catch (Exception e) {
                product.setOrderQty(null);
                product.setSubTotal(null);
                holder.tvSubTotal.setText("");
                e.printStackTrace();
            }
            if (clickListener != null) {
                clickListener.onQuantityChangeListener(product, this.position);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }

}
