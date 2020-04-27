package com.darshansfa.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.dbModel.OrderPart;
import com.darshansfa.dbModel.Orders;

/**
 * Created by Nikhil on 09-05-2017.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<Orders> arrayList;

    private OnItemClickListener clickListener;

    public OrderAdapter(List<Orders> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_orders, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrderAdapter.ViewHolder holder, int position) {
        Orders orders = arrayList.get(position);

        List<OrderPart> orderssss=new ArrayList<>();
        int shipped=0;

        if(orders.getStatus().matches(Constants.ORDER_PARTIAL_SHIPPED)){
            orderssss.addAll(OrderPart.find(OrderPart.class, "order_id = ? ", new String[]{orders.getOrderId()}));

            for(int i=0;i<orderssss.size();i++){
                if(orderssss.get(i).getShipped_quantity()!=0){

                    shipped=shipped+ orderssss.get(i).getShipped_quantity();

                    holder.tvOrderId.setVisibility(View.VISIBLE);
                    holder.tvOrderAmount.setVisibility(View.VISIBLE);
                    holder.tvOrderQty.setVisibility(View.VISIBLE);
                    holder.tvShippedQty.setVisibility(View.VISIBLE);
                    holder.tvDate.setVisibility(View.VISIBLE);

                    holder.tvOrderId.setText(orders.getOrderId());
                    holder.tvOrderAmount.setText("Order Amount : " + orders.getAmount());
                    holder.tvOrderQty.setText("Order Qty :  " + orders.getTotalQuantity());
                    holder.tvShippedQty.setText("Shipped Qty :  " + shipped);
                    holder.tvDate.setText(orders.getOrderDate());
                }else{
                    holder.tvOrderId.setVisibility(View.GONE);
                    holder.tvOrderAmount.setVisibility(View.GONE);
                    holder.tvOrderQty.setVisibility(View.GONE);
                    holder.tvShippedQty.setVisibility(View.GONE);
                    holder.tvDate.setVisibility(View.GONE);
                }
            }


        }else{
            holder.tvShippedQty.setVisibility(View.GONE);
            holder.tvOrderId.setText(orders.getOrderId());
            holder.tvOrderAmount.setText("Order Amount : " + orders.getAmount());
            holder.tvOrderQty.setText("Order Qty :  " + orders.getTotalQuantity());
            holder.tvDate.setText(orders.getOrderDate());
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvOrderId, tvOrderAmount, tvOrderQty,tvShippedQty, tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvOrderId = (TextView) itemView.findViewById(R.id.tvOrderId);
            this.tvOrderAmount = (TextView) itemView.findViewById(R.id.tvOrderAmount);
            this.tvOrderQty = (TextView) itemView.findViewById(R.id.tvOrderQty);
            this.tvShippedQty = (TextView) itemView.findViewById(R.id.tvShippedQty);
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
