package com.darshansfa.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.darshansfa.R;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.AdvancePayment;

/**
 * Created by Nikhil on 09-05-2017.
 */

public class AdvancePaymentAdapter extends RecyclerView.Adapter<AdvancePaymentAdapter.ViewHolder> {

    private ArrayList<AdvancePayment> arrayList;

    private OnItemClickListener clickListener;

    public AdvancePaymentAdapter(ArrayList<AdvancePayment> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public AdvancePaymentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_advance_payment, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdvancePaymentAdapter.ViewHolder holder, int position) {
        AdvancePayment advancePayment = arrayList.get(position);

        holder.tvInvoiceId.setText("Order Id : " + advancePayment.getOrderId());
        holder.tvRetailerName.setText("Customer : " + advancePayment.getRetailerName() + "," + advancePayment.getRetailerId());
        holder.tvDate.setText(advancePayment.getDate());
        try {
            holder.tvAmount.setText("Amount : " + UIUtil.amountFormat(UIUtil.getText(advancePayment.getCollectedAmount())));
        } catch (Exception e) {
            e.printStackTrace();
        }
//        holder.tvOrderId.setText(orders.getOrderId());
//        holder.tvOrderAmount.setText("Order Amount : " + orders.getAmount());
//        holder.tvOrderQty.setText("Order Qty :  " + orders.getTotalQuantity());
//        holder.tvDate.setText(orders.getOrderDate());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvInvoiceId, tvDate, tvAmount, tvPeriod, tvRetailerName;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvInvoiceId = (TextView) itemView.findViewById(R.id.tvInvoiceId);
            this.tvAmount = (TextView) itemView.findViewById(R.id.tvAmount);
            this.tvPeriod = (TextView) itemView.findViewById(R.id.tvPeriod);
            this.tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            this.tvRetailerName = (TextView) itemView.findViewById(R.id.tvRetailerName);
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
