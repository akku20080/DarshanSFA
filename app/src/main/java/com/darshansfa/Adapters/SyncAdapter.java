package com.darshansfa.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import com.darshansfa.Models.APIProgress;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;

/**
 * Created by Nikhil on 09-05-2017.
 */

public class SyncAdapter extends RecyclerView.Adapter<SyncAdapter.ViewHolder> {

    private ArrayList<APIProgress> arrayList;

    public SyncAdapter(ArrayList<APIProgress> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public SyncAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_sync, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SyncAdapter.ViewHolder holder, int position) {
        APIProgress progress = arrayList.get(position);
        holder.title.setText(progress.getTitle());
        holder.progressBar.setProgress(progress.getProgress());
        if (progress.getStatus() == Constants.DONE) {
            holder.ivProgressImage.setImageResource(R.drawable.done);
            holder.subTitle.setText("Done");
        } else if (progress.getStatus() == Constants.DOWNLOADING) {
            holder.ivProgressImage.setImageResource(R.drawable.progress_animation);
            holder.subTitle.setText("Downloading");
        } else if (progress.getStatus() == Constants.WAITING) {
            holder.ivProgressImage.setImageResource(R.drawable.waiting);
            holder.subTitle.setText("Waiting");
        } else if (progress.getStatus() == Constants.ERROR) {
            holder.ivProgressImage.setImageResource(R.drawable.error);
            holder.subTitle.setText("Failed");
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProgressImage;
        ProgressBar progressBar;
        TextView title, subTitle;


        public ViewHolder(View itemView) {
            super(itemView);
            this.progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            this.ivProgressImage = (ImageView) itemView.findViewById(R.id.ivProgressImage);
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.subTitle = (TextView) itemView.findViewById(R.id.subTitle);

        }
    }
}
