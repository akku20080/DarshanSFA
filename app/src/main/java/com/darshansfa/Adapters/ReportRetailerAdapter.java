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

public class ReportRetailerAdapter extends RecyclerView.Adapter<ReportRetailerAdapter.ViewHolder> {

    private ArrayList<Retailer> arrayList;
    private Activity activity;
    private boolean isTodayRetailer = true;

    private OnItemClickListener clickListener;

    public ReportRetailerAdapter(Activity activity, ArrayList<Retailer> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @Override
    public ReportRetailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_retailer_report, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ReportRetailerAdapter.ViewHolder holder, int position) {
        Retailer retailer = arrayList.get(position);

        holder.tvRetailerName.setText(retailer.getRetailerName().trim());
        holder.tvRetailerCode.setText("Code: " + retailer.getRetailerId() + ",  Locality: " + retailer.getLocality());


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
