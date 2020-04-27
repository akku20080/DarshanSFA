package com.darshansfa.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.darshansfa.Models.Locality;
import com.darshansfa.R;

/**
 * Created by Ranjan on 26-12-2015.
 */
public class LocalityRecycleAdapter extends RecyclerView.Adapter<LocalityRecycleAdapter.ViewHolder> {

    private ArrayList<Locality> localityList;
    private Activity activity;
    private OnItemClickListener clickListener;


    public class CustomComparator implements Comparator<Locality> {
        @Override
        public int compare(Locality o1, Locality o2) {
            return o1.getLocalityName().compareTo(o2.getLocalityName());
        }
    }

    public LocalityRecycleAdapter(Activity activity, ArrayList<Locality> localityList) {
        this.localityList = localityList;
        this.activity = activity;
        Collections.sort(this.localityList, new CustomComparator());


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_locality, parent, false);
        // pass MyCustomEditTextListener to viewholder in onCreateViewHolder
        // so that we don't have to do this expensive allocation in onBindViewHolder
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Locality locality = localityList.get(position);
        try {
            holder.ckbLocality.setChecked(false);
            if (!TextUtils.isEmpty(locality.getLocalityName())) {
                holder.ckbLocality.setChecked(false);
                holder.tvLocality.setText(locality.getLocalityName());
//                Log.e("Get Location ID", "---" + locality);
                holder.ckbLocality.setTag(position);
                holder.ckbLocality.setChecked(locality.isSelected());
                holder.ckbLocality.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        locality.setSelected(holder.ckbLocality.isChecked());
                        int pos = (int) holder.ckbLocality.getTag();
                        Log.e("Position ", " Pos --" + pos);
                        localityList.set(pos, locality);
                        if (clickListener != null) {
                            clickListener.updatePJP(locality, position);
                        }
                    }
                });
//
//                holder.ckbLocality.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        locality.setSelected(isChecked);
//                        int pos = (int) holder.ckbLocality.getTag();
//                        Log.e("Position ", " Pos --" + pos);
//                        localityList.set(pos, locality);
//                        if (clickListener != null) {
//                            clickListener.updatePJP(locality);
//                        }
//                    }
//                });

            } else {
                //Toast.makeText(activity, "Demo else", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return localityList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvLocality;
        public CheckBox ckbLocality;


        public ViewHolder(View v) {
            super(v);
            this.tvLocality = (TextView) v.findViewById(R.id.tv_areaName);
            this.ckbLocality = (CheckBox) v.findViewById(R.id.checkboxArea);

        }
    }


    public ArrayList<Locality> getLocalityList() {
        return localityList;
    }
    // we make TextWatcher to be aware of the position it currently works with
    // this way, once a new item is attached in onBindViewHolder, it will
    // update current position MyCustomEditTextListener, reference to which is kept by ViewHolder

    public void SetOnItemClickListener(OnItemClickListener onItemClickListener) {
        clickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void updatePJP(Locality localityId, int position);
    }
}
