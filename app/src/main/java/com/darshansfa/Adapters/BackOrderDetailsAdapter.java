package com.darshansfa.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.darshansfa.R;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.OrderPart;

/**
 * Created by Admin on 6/12/2018.
 */

public class BackOrderDetailsAdapter extends RecyclerView.Adapter<BackOrderDetailsAdapter.ViewHolder> {

    private ArrayList<OrderPart> arrayList;

    private OnItemClickListener clickListener;

    public BackOrderDetailsAdapter(ArrayList<OrderPart> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public BackOrderDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order_details, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BackOrderDetailsAdapter.ViewHolder holder, int position) {
        OrderPart cart = arrayList.get(position);
        holder.tvMRP.setText("Company Price : " + cart.getCompanyPrice());

        holder.tvOrderQty.setText("Backorder Qty : " + cart.getBack_order_quantity());

        holder.tvPartNumber.setText("" + cart.getPartId());
        holder.tvPartName.setText(cart.getPartName());
        holder.tvSubTotal.setText("Line Total : " + UIUtil.amountFormat(UIUtil.getText(cart.getLineTotal())));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvPartNumber, tvPartName, tvOrderQty, tvMRP, tvSubTotal;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvPartNumber = (TextView) itemView.findViewById(R.id.tvPartNumber);
            this.tvPartName = (TextView) itemView.findViewById(R.id.tvPartName);
            this.tvOrderQty = (TextView) itemView.findViewById(R.id.tvOrderQty);
            this.tvMRP = (TextView) itemView.findViewById(R.id.tvMRP);
            this.tvSubTotal = (TextView) itemView.findViewById(R.id.tvSubTotal);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                switch (v.getId()) {
                    case R.id.ivPhone:
                        clickListener.onCallClick(v, getAdapterPosition());
                        break;
                    default:
                        clickListener.onItemClick(v, getAdapterPosition());
                        break;
                }
            }

        }
    }

    public void SetOnItemClickListener(OnItemClickListener onItemClickListener) {
        clickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onCallClick(View view, int position);

    }
}
