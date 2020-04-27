package com.darshansfa.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.darshansfa.R;
import com.darshansfa.Utility.UIUtil;
import com.darshansfa.dbModel.SalesReturn;

/**
 * Created by Nikhil on 09-05-2017.
 */

public class SalesReturnAdapter extends RecyclerView.Adapter<SalesReturnAdapter.ViewHolder> {

    private ArrayList<SalesReturn> arrayList;

    private OnItemClickListener clickListener;

    public SalesReturnAdapter(ArrayList<SalesReturn> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public SalesReturnAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_sales_return, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SalesReturnAdapter.ViewHolder holder, int position) {
        SalesReturn salesReturn = arrayList.get(position);

        holder.tvInvoiceId.setText("Product Id : " + salesReturn.getProductNumber());
        holder.tvDate.setText(salesReturn.getDate());
        try {
            holder.tvInvoiceId.setText("Invoice : " + UIUtil.getText(salesReturn.getInvoiceId()));
            holder.tvReason.setText("Reason : " + UIUtil.getText(salesReturn.getReason()));
            holder.tvQty.setText("Qty : " + UIUtil.getText(salesReturn.getProductQuantity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvProductNumber, tvInvoiceId, tvQty, tvReason, tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvInvoiceId = (TextView) itemView.findViewById(R.id.tvInvoiceId);
            this.tvProductNumber = (TextView) itemView.findViewById(R.id.tvProductNumber);
            this.tvQty = (TextView) itemView.findViewById(R.id.tvQty);
            this.tvReason = (TextView) itemView.findViewById(R.id.tvReason);
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
