package com.darshansfa.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.darshansfa.R;
import com.darshansfa.dbModel.Cheque;


/**
 * Created by Nikhil on 27-11-2015.
 */
public class CollectionRecyclerAdapter extends RecyclerView.Adapter<CollectionRecyclerAdapter.ViewHolder> {

    private ArrayList<Cheque> chequeArrayList;
    private Activity activity;
    OnItemClickListener clickListener;

    public CollectionRecyclerAdapter(Activity activity, ArrayList<Cheque> chequeArrayList) {
        this.chequeArrayList = chequeArrayList;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_cheque, parent, false);
        // pass MyCustomEditTextListener to viewholder in onCreateViewHolder
        // so that we don't have to do this expensive allocation in onBindViewHolder
        ViewHolder vh = new ViewHolder(v, new AmountEditTextListener(), new NumberEditTextListener(), new NameEditTextListener());

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // update MyCustomEditTextListener every time we bind a new item
        // so that it knows what item in mDataset to update
        holder.amountEditTextListener.updatePosition(position, holder);
        holder.numberEditTextListener.updatePosition(position, holder);
        holder.nameEditTextListener.updatePosition(position, holder);
        Cheque cheque = chequeArrayList.get(position);
        Log.e("cheque", "cheque  : " + cheque.toString());

        holder.etAmount.setText(TextUtils.isEmpty(cheque.getAmount()) ? "" : cheque.getAmount());
        holder.etBankName.setText(TextUtils.isEmpty(cheque.getBank()) ? "" : cheque.getBank());
        holder.etCheckNumber.setText(TextUtils.isEmpty(cheque.getNumber()) ? "" : cheque.getNumber());

        Glide.with(activity).load(cheque.getImageUrl()).thumbnail(0.5f).placeholder(R.drawable.progress_animation).into(holder.ivPreview);

//
//        if (!TextUtils.isEmpty(cheque.getImageUrl())) {
//            // bimatp factory
//            holder.ivPreview.setVisibility(View.VISIBLE);
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            // downsizing image as it throws OutOfMemory Exception for larger
//            // images
//            options.inSampleSize = 8;
//            final Bitmap bitmap = BitmapFactory.decodeFile(cheque.getImageUrl(), options);
//            holder.ivPreview.setImageBitmap(bitmap);
//
//        } else {
//            holder.ivPreview.setImageDrawable(activity.getResources().getDrawable(R.drawable.image_preview));
//        }

    }

    @Override
    public int getItemCount() {
        return chequeArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public EditText etAmount, etCheckNumber, etBankName;
        public AmountEditTextListener amountEditTextListener;
        public NumberEditTextListener numberEditTextListener;
        public NameEditTextListener nameEditTextListener;

        public ImageView ivCapture, ivPreview, ivDelete;


        public ViewHolder(View v, AmountEditTextListener amountEditTextListener, NumberEditTextListener numberEditTextListener, NameEditTextListener nameEditTextListener) {
            super(v);
            this.etAmount = (EditText) v.findViewById(R.id.chequeAmount);
            this.etCheckNumber = (EditText) v.findViewById(R.id.chequeNo);
            this.etBankName = (EditText) v.findViewById(R.id.chequeBankName);
            this.ivCapture = (ImageView) v.findViewById(R.id.ivCapture);
            this.ivPreview = (ImageView) v.findViewById(R.id.ivPreview);
            this.ivDelete = (ImageView) v.findViewById(R.id.ivDelete);

            this.amountEditTextListener = amountEditTextListener;
            this.numberEditTextListener = numberEditTextListener;
            this.nameEditTextListener = nameEditTextListener;

            this.etAmount.addTextChangedListener(amountEditTextListener);
            this.etCheckNumber.addTextChangedListener(numberEditTextListener);
            this.etBankName.addTextChangedListener(nameEditTextListener);
//

            this.ivPreview.setOnClickListener(this);
            this.ivCapture.setOnClickListener(this);
            this.ivDelete.setOnClickListener(this);
            v.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                switch (v.getId()) {
                    case R.id.ivPreview:
                        clickListener.onImagePreview(v, getAdapterPosition());
                        break;
                    case R.id.ivCapture:
                        clickListener.onCamera(v, getAdapterPosition());
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


    private void deleteConfirm(final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

        // Setting Dialog Title
        alertDialog.setTitle("Do You Want Delete");

        // Setting Dialog Message
        //alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                chequeArrayList.remove(position);
                notifyDataSetChanged();
//                setListViewHeightBasedOnItems(chequeRecycleView, chequeArrayList.size(), chequeAdapter);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    private class AmountEditTextListener implements TextWatcher {
        private int position;
        private ViewHolder holder;

        public void updatePosition(int position, ViewHolder holder) {
            this.position = position;
            this.holder = holder;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            String qty = charSequence.toString();
            Cheque cheque = chequeArrayList.get(this.position);
            Log.e("AmountEditTextListener", "Position =======" + this.position);

            if (!TextUtils.isEmpty(charSequence)) {
                cheque.setAmount(charSequence.toString());
            } else {
                cheque.setAmount("");
            }
            if (clickListener != null)
                clickListener.updateCheque(this.position, cheque);
            chequeArrayList.set(this.position, cheque);
        }


        @Override
        public void afterTextChanged(Editable editable) {
        }
    }


    private class NumberEditTextListener implements TextWatcher {
        private int position;
        private ViewHolder holder;

        public void updatePosition(int position, ViewHolder holder) {
            this.position = position;
            this.holder = holder;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            String qty = charSequence.toString();
            Cheque cheque = chequeArrayList.get(this.position);
            Log.e("NumberEditTextListener", "Position =======" + this.position);

            if (!TextUtils.isEmpty(charSequence)) {
                cheque.setNumber(charSequence.toString());

            } else {
                cheque.setNumber("");
            }
            if (clickListener != null)
                clickListener.updateCheque(this.position, cheque);
            chequeArrayList.set(this.position, cheque);
        }


        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    private class NameEditTextListener implements TextWatcher {
        private int position;
        private ViewHolder holder;

        public void updatePosition(int position, ViewHolder holder) {
            this.position = position;
            this.holder = holder;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            String qty = charSequence.toString();
            Cheque cheque = chequeArrayList.get(this.position);
            Log.e("NameEditTextListener", "Position =======" + this.position);

            if (!TextUtils.isEmpty(charSequence)) {
                cheque.setBank(charSequence.toString());
            } else {
                cheque.setBank("");
            }
            if (clickListener != null)
                clickListener.updateCheque(this.position, cheque);
            chequeArrayList.set(this.position, cheque);
        }


        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }

    public ArrayList<Cheque> getChequeArrayList() {
        return this.chequeArrayList;
    }


    public void SetOnItemClickListener(OnItemClickListener onItemClickListener) {
        clickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onCamera(View view, int position);

        public void onImagePreview(View view, int position);

        public void onDelete(View view, int position);

        public void updateCheque(int position, Cheque cheque);

    }
}
