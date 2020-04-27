package com.darshansfa.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.dbModel.Collection;

/**
 * Created by Nikhil on 09-05-2017.
 */

public class DayCloseCollectionAdapter extends RecyclerView.Adapter<DayCloseCollectionAdapter.ViewHolder> {

    private List<Collection> arrayList;

    private OnItemClickListener clickListener;

    public DayCloseCollectionAdapter(List<Collection> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public DayCloseCollectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_day_close_collection, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DayCloseCollectionAdapter.ViewHolder holder, int position) {
        Collection collection = arrayList.get(position);
        holder.tvInvoiceId.setText("Invoice :" + collection.getInvoiceId());
        holder.tvRetailerName.setText("" + collection.getRetailerName());
        holder.tvCollectedAmount.setText("Collected Amount : " + collection.getTotalCollectedAmount());
        holder.tvCollectionType.setText("Collection Type :  " + collection.getPaymentMode());
        boolean isPending = Constants.ORDER_PENDING.equalsIgnoreCase(collection.getStatus());
        holder.llPending.setVisibility(isPending ? View.VISIBLE : View.GONE);
        holder.tvDate.setText(collection.getDate());

        holder.tvTotal.setText("Invoice Amount : " + collection.getInvoiceAmount());
        holder.tvOutstandingAmount.setText("Outstanding : " + collection.getOutstandingAmount());

        holder.tvCollectionStatus.setText(collection.getStatus());


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvInvoiceId, tvRetailerName, tvCollectedAmount, tvCollectionType, tvTotal, tvOutstandingAmount, tvDate,
                tvCollectionStatus, tvBtnSubmit;
        LinearLayout llPending;


        public ViewHolder(View itemView) {
            super(itemView);
            this.tvInvoiceId = (TextView) itemView.findViewById(R.id.tvInvoiceId);
            this.tvCollectedAmount = (TextView) itemView.findViewById(R.id.tvCollectedAmount);
            this.tvCollectionType = (TextView) itemView.findViewById(R.id.tvCollectionType);
            this.tvTotal = (TextView) itemView.findViewById(R.id.tvTotal);
            this.tvRetailerName = (TextView) itemView.findViewById(R.id.tvRetailerName);
            this.tvOutstandingAmount = (TextView) itemView.findViewById(R.id.tvOutstandingAmount);
            this.tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            this.tvCollectionStatus = (TextView) itemView.findViewById(R.id.tvCollectionStatus);
            this.tvBtnSubmit = (TextView) itemView.findViewById(R.id.tvBtnSubmit);
            this.llPending = (LinearLayout) itemView.findViewById(R.id.llPending);
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
