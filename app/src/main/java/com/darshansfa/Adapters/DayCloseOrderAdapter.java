package com.darshansfa.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.dbModel.Orders;
import com.darshansfa.dbModel.Retailer;

/**
 * Created by Nikhil on 09-05-2017.
 */

public class DayCloseOrderAdapter extends RecyclerView.Adapter<DayCloseOrderAdapter.ViewHolder> {

    private ArrayList<Orders> arrayList;

    private OnItemClickListener clickListener;

    public DayCloseOrderAdapter(ArrayList<Orders> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public DayCloseOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_day_close_orders, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DayCloseOrderAdapter.ViewHolder holder, int position) {
        Orders orders = arrayList.get(position);
        holder.tvOrderId.setText(orders.getOrderId());
        holder.tvOrderAmount.setText("Order Amount : " + orders.getAmount());
        holder.tvOrderQty.setText("Order Qty :  " + orders.getTotalQuantity());
        boolean isPending = Constants.ORDER_PENDING.equalsIgnoreCase(orders.getStatus());
        holder.tvBtnSubmit.setVisibility(isPending ? View.VISIBLE : View.GONE);
        holder.tvDate.setVisibility(isPending ? View.GONE : View.VISIBLE);
        holder.tvDate.setText(orders.getOrderDate());

        holder.tvOrderStatus.setText(orders.getStatus());
        try {
            holder.tvRetailerName.setText("" + Retailer.find(Retailer.class, "retailer_id = ?",
                    new String[]{orders.getRetailerId()}).get(0).getRetailerName());
        } catch (Exception e) {
            e.printStackTrace();
            holder.tvRetailerName.setText("" + orders.getRetailerId());
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvOrderId, tvOrderAmount, tvOrderQty, tvBtnSubmit, tvRetailerName, tvOrderStatus, tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvOrderId = (TextView) itemView.findViewById(R.id.tvOrderId);
            this.tvOrderAmount = (TextView) itemView.findViewById(R.id.tvOrderAmount);
            this.tvOrderQty = (TextView) itemView.findViewById(R.id.tvOrderQty);
            this.tvBtnSubmit = (TextView) itemView.findViewById(R.id.tvBtnSubmit);
            this.tvRetailerName = (TextView) itemView.findViewById(R.id.tvRetailerName);
            this.tvOrderStatus = (TextView) itemView.findViewById(R.id.tvOrderStatus);
            this.tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            this.tvBtnSubmit.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                switch (v.getId()) {
                    case R.id.tvBtnSubmit:
                        clickListener.onSubmitClick(v, getAdapterPosition());
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

        public void onSubmitClick(View view, int position);

    }
}
