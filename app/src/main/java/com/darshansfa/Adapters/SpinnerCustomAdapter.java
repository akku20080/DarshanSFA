package com.darshansfa.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import com.darshansfa.R;
import com.darshansfa.dbModel.Retailer;

/**
 * Created by Nikhil on 15-06-2017.
 */

public class SpinnerCustomAdapter extends ArrayAdapter<Retailer> {

    LayoutInflater flater;

    public SpinnerCustomAdapter(Activity context, List<Retailer> list) {

        super(context, android.R.layout.simple_spinner_dropdown_item, list);
//        flater = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return rowView(convertView, position);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return rowView(convertView, position);
    }

    private View rowView(View convertView, int position) {

        Retailer retailer = getItem(position);
        viewHolder holder;

        if (convertView == null) {
            holder = new viewHolder();
            flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = flater.inflate(R.layout.item_text_view, null, false);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvCode = (TextView) convertView.findViewById(R.id.tvCode);
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }
        holder.tvName.setText(retailer.getRetailerName());
        holder.tvCode.setText(retailer.getRetailerId());

        return convertView;
    }

    private class viewHolder {
        TextView tvName, tvCode;
    }
}
