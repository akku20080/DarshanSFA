package com.darshansfa.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.darshansfa.R;
import com.darshansfa.dbModel.Retailer;

/**
 * Created by Nikhil on 09-05-2017.
 */

public class RetailerAdapter extends RecyclerView.Adapter<RetailerAdapter.ViewHolder> {

    private ArrayList<Retailer> arrayList;
    private Activity activity;
    private boolean isTodayRetailer = true;

    private OnItemClickListener clickListener;

    public RetailerAdapter(Activity activity, ArrayList<Retailer> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @Override
    public RetailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_retailer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RetailerAdapter.ViewHolder holder, int position) {
        Retailer retailer = arrayList.get(position);

        holder.tvRetailerName.setText(retailer.getRetailerName().trim());
        holder.tvRetailerCode.setText("Code : " + retailer.getRetailerId());
        holder.tvLocality.setText("Locality : " + retailer.getLocality());
        holder.btnStartStop.setVisibility(isTodayRetailer ? View.VISIBLE : View.GONE);

        holder.btnStartStop.setText(retailer.isVisitStart() ? "Stop" : "Start");
        holder.btnStartStop.setBackgroundColor(retailer.isVisitStart() ? activity.getResources().getColor(R.color.red)
                : activity.getResources().getColor(R.color.green));
        holder.tvOutstanding.setText(retailer.getOutstandingAmount());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvRetailerName, tvRetailerCode, tvLocality, tvOutstanding, btnStartStop;
        ImageView ivPhone;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvRetailerName = (TextView) itemView.findViewById(R.id.tvRetailerName);
            this.tvRetailerCode = (TextView) itemView.findViewById(R.id.tvRetailerCode);
            this.tvLocality = (TextView) itemView.findViewById(R.id.tvLocality);
            this.tvOutstanding = (TextView) itemView.findViewById(R.id.tvOutstanding);
            this.btnStartStop = (TextView) itemView.findViewById(R.id.btnStartStop);
            this.ivPhone = (ImageView) itemView.findViewById(R.id.ivPhone);
            itemView.setOnClickListener(this);
            this.ivPhone.setOnClickListener(this);
            this.btnStartStop.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                switch (v.getId()) {
                    case R.id.ivPhone:
                        clickListener.onCallClick(v, getAdapterPosition());
                        break;
                    case R.id.btnStartStop:
                        clickListener.onStartStopClick(v, getAdapterPosition());
                        break;
                    default:
                        clickListener.onItemClick(v, getAdapterPosition());
                        break;
                }
            }

        }
    }

    public void setTodayRetailer(boolean isTodayRetailer) {
        this.isTodayRetailer = isTodayRetailer;
    }

    public void SetOnItemClickListener(OnItemClickListener onItemClickListener) {
        clickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onCallClick(View view, int position);

        public void onStartStopClick(View view, int position);

    }
}
