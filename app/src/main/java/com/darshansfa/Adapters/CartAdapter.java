package com.darshansfa.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import com.darshansfa.Activities.CartActivity;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.Utility.PreferencesManger;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.Cart;

/**
 * Created by Nikhil on 09-05-2017.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private ArrayList<Cart> arrayList;

    private OnItemClickListener clickListener;
    private Cart cart;
    private String str, retailerId, depoId, dsrId;
    private Context context;

    public CartAdapter(Context context, ArrayList<Cart> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        retailerId = PreferencesManger.getStringFields(context, Constants.Pref.RETAILER_ID);
        depoId = PreferencesManger.getStringFields(context, Constants.Pref.DSR_ID);
        dsrId = PreferencesManger.getStringFields(context, Constants.Pref.DSR_ID);
    }

    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_cart, parent, false);
        return new ViewHolder(v, new CustomEditTextListener());
    }

    @Override
    public void onBindViewHolder(CartAdapter.ViewHolder holder, int position) {

        Cart cart = arrayList.get(position);
        holder.editTextListener.updatePosition(position, holder);
        holder.tvProductId.setText(cart.getPartNumber());
        holder.tvProductName.setText(cart.getPartName());
        holder.tvMRP.setText(cart.getPartMRP());
        holder.tvSubTotal.setText(UIUtil.amountFormat(UIUtil.getText(cart.getPartSubTotal())));
//        holder.edOrderQty.setText((cart.getPartOrderQty() != null) ? "" + cart.getPartOrderQty() : "");
        holder.edOrderQty.setText(UIUtil.amountFormat(UIUtil.getText(cart.getPartOrderQty())));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvProductId, tvProductName, tvMRP,
                tvSubTotal;
        EditText edOrderQty;

        ImageButton ivDelete;

        public CustomEditTextListener editTextListener;

        public ViewHolder(View itemView, CustomEditTextListener editTextListener) {
            super(itemView);
            this.editTextListener = editTextListener;
            this.tvProductId = (TextView) itemView.findViewById(R.id.tvProductId);
            this.tvProductName = (TextView) itemView.findViewById(R.id.tvProductName);
            this.tvMRP = (TextView) itemView.findViewById(R.id.tvMRP);
            this.tvSubTotal = (TextView) itemView.findViewById(R.id.tvSubTotal);
            this.edOrderQty = (EditText) itemView.findViewById(R.id.edOrderQty);

            this.ivDelete=(ImageButton)itemView.findViewById(R.id.ivDelete);
            this.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeAt(getPosition());
                }
            });
            itemView.setOnClickListener(this);
            edOrderQty.addTextChangedListener(this.editTextListener);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                switch (v.getId()) {
                    case R.id.btnAddPart:
                        clickListener.onAddToCart(v, getAdapterPosition());
                        break;
                    default:
                        clickListener.onItemClick(v, getAdapterPosition());
                        break;
                }
            }

        }
    }

    private void removeAt(int position) {
        Cart.deleteAll(Cart.class,"part_number = ?",new String[]{arrayList.get(position).getPartNumber()});
        arrayList.remove(position);
        notifyItemRemoved(position);
        ((CartActivity)context).updateTotalPrice();
        notifyItemRangeChanged(position, arrayList.size());

        if(arrayList.size()==0){
            //((Activity) context).finish();
            ((CartActivity)context).updateTotalPrice();
            ((CartActivity)context).clearCart();
        }
    }


    public void SetOnItemClickListener(OnItemClickListener onItemClickListener) {
        clickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onAddToCart(View view, int position);

        public void onQuantityChangeListener(Cart s, int position);

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
            Cart cart = arrayList.get(this.position);
            try {
                Double subTotal = Double.parseDouble(qty);
                cart.setPartSubTotal(Double.parseDouble(cart.getPartMRP()) * subTotal);
                cart.setPartOrderQty(subTotal);
                holder.tvSubTotal.setText(UIUtil.amountFormat(String.valueOf(cart.getPartSubTotal())));
            } catch (Exception e) {
                cart.setPartOrderQty(null);
                cart.setPartSubTotal(null);
                holder.tvSubTotal.setText("");
                e.printStackTrace();
            }
            cart.save();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }

}
