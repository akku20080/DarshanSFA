package com.darshansfa.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.darshansfa.R;
import com.darshansfa.dbModel.Distributor;

/**
 * Created by Nikhil on 09-05-2017.
 */

public class DistributorDialogAdapter extends RecyclerView.Adapter<DistributorDialogAdapter.ViewHolder> {

    private List<Distributor> arrayList;
    private OnItemClickListener clickListener;

    public DistributorDialogAdapter(List<Distributor> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public DistributorDialogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_distributor_dailog, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DistributorDialogAdapter.ViewHolder holder, int position) {
        Distributor distributor = arrayList.get(position);
        holder.tvName.setText(distributor.getName());
        holder.tvEmail.setText(distributor.getEmail());
        holder.tvId.setText("Id : " + distributor.getDistributorCode());
        holder.tvCode.setText("Code : " + distributor.getDepotCode());


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvId, tvCode;


        public ViewHolder(final View itemView) {
            super(itemView);
            this.tvName = (TextView) itemView.findViewById(R.id.tvName);
            this.tvEmail = (TextView) itemView.findViewById(R.id.tvEmail);
            this.tvId = (TextView) itemView.findViewById(R.id.tvId);
            this.tvCode = (TextView) itemView.findViewById(R.id.tvCode);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onItemClick(itemView, getAdapterPosition());
                    }
                }
            });

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
