package com.darshansfa.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.darshansfa.R;
import com.darshansfa.dbModel.Note;


/**
 * Created by Nikhil on 27-11-2015.
 */
public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {

    private ArrayList<Note> noteArrayList;
    static OnItemClickListener clickListener;

    public NoteRecyclerAdapter(ArrayList<Note> noteArrayList) {
        this.noteArrayList = noteArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_note, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // update MyCustomEditTextListener every time we bind a new item
        // so that it knows what item in mDataset to update
        Note note = noteArrayList.get(position);

//        if (Constants.RETAILER_NOTE.equalsIgnoreCase(note.getNoteType())) {
//            holder.tvDate.setText("" + note.getDate());
//        } else {
//            holder.tvDate.setText("" + note.getPjpPlanned());
//        }

        holder.tvDate.setText("" + note.getDate());
        holder.tvTime.setText("" + note.getTime());

        holder.tvTitle.setText("" + note.getRemarks());
        holder.tvDsc.setText("" + note.getContent());

    }

    @Override
    public int getItemCount() {
        return noteArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView tvDate, tvTime, tvTitle, tvDsc;
        public ImageView ivDelete, ivEdit;
        public LinearLayout llRowItem;

        public ViewHolder(View v) {
            super(v);
            this.tvDate = (TextView) v.findViewById(R.id.tvDate);
            this.tvTime = (TextView) v.findViewById(R.id.tvTime);
            this.tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            this.tvDsc = (TextView) v.findViewById(R.id.tvDsc);
            this.ivDelete = (ImageView) v.findViewById(R.id.ivDelete);
            this.ivEdit = (ImageView) v.findViewById(R.id.ivEdit);
            this.llRowItem = (LinearLayout) v.findViewById(R.id.llRowItem);
            this.ivDelete.setOnClickListener(this);
            this.ivEdit.setOnClickListener(this);
            this.llRowItem.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                switch (v.getId()) {
                    case R.id.ivEdit:
                        clickListener.onEdit(v, getAdapterPosition());
                        break;
                    case R.id.ivDelete:
                        clickListener.onDelete(v, getAdapterPosition());
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

        public void onDelete(View view, int position);

        public void onEdit(View view, int position);

    }
}
