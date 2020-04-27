package com.darshansfa.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.darshansfa.R;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.Invoice;

/**
 * Created by Nikhil on 09-05-2017.
 */

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder> {

    private ArrayList<Invoice> arrayList;

    private OnItemClickListener clickListener;

    public InvoiceAdapter(ArrayList<Invoice> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public InvoiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_invoice, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(InvoiceAdapter.ViewHolder holder, int position) {
        Invoice invoice = arrayList.get(position);

        holder.tvInvoiceId.setText("Invoice No : " + invoice.getInvoiceId());
        //holder.tvOthers.setText("Others:....");
        holder.tvDate.setText(invoice.getInvoiceDate());
        holder.tvPeriod.setText(invoice.getPeriod());
        try {
            holder.tvAmount.setText("Outstanding : " + UIUtil.amountFormat(Float.parseFloat(invoice.getTotalAmount()) - Float.parseFloat(invoice.getCollectedAmount()) + ""));
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
        TextView tvInvoiceId, tvDate, tvAmount, tvPeriod;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvInvoiceId = (TextView) itemView.findViewById(R.id.tvInvoiceId);
            this.tvAmount = (TextView) itemView.findViewById(R.id.tvAmount);
            this.tvPeriod = (TextView) itemView.findViewById(R.id.tvPeriod);
            this.tvDate = (TextView) itemView.findViewById(R.id.tvDate);
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
