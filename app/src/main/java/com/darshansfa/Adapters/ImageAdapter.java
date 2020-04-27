package com.darshansfa.Adapters;//package com.darshansfa.Adapters;
//
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//
//import com.darshansfa.R;
//import com.darshansfa.dbModel.Orders;
//
///**
// * Created by Nikhil on 09-05-2017.
// */
//
//public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
//
//    private ArrayList<String> arrayList;
//
//    private OnItemClickListener clickListener;
//
//    public ImageAdapter(ArrayList<String> arrayList) {
//        this.arrayList = arrayList;
//    }
//
//    @Override
//    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_orders, parent, false);
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(ImageAdapter.ViewHolder holder, int position) {
//
//
//        holder.tvOrderId.setText(orders.getOrderId());
//        holder.tvOrderAmount.setText("Order Amount : " + orders.getAmount());
//        holder.tvOrderQty.setText("Order Qty :  " + orders.getTotalQuantity());
//        holder.tvDate.setText(orders.getOrderDate());
//    }
//
//    @Override
//    public int getItemCount() {
//        return arrayList.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        TextView tvOrderId, tvOrderAmount, tvOrderQty, tvDate;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            this.tvOrderId = (TextView) itemView.findViewById(R.id.tvOrderId);
//            this.tvOrderAmount = (TextView) itemView.findViewById(R.id.tvOrderAmount);
//            this.tvOrderQty = (TextView) itemView.findViewById(R.id.tvOrderQty);
//            this.tvDate = (TextView) itemView.findViewById(R.id.tvDate);
//            itemView.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View v) {
//            if (clickListener != null) {
//                switch (v.getId()) {
//                    case R.id.ivPhone:
//                        clickListener.onCallClick(v, getAdapterPosition());
//                        break;
//                    default:
//                        clickListener.onItemClick(v, getAdapterPosition());
//                        break;
//                }
//            }
//
//        }
//    }
//
//    public void SetOnItemClickListener(OnItemClickListener onItemClickListener) {
//        clickListener = onItemClickListener;
//    }
//
//    public interface OnItemClickListener {
//        public void onItemClick(View view, int position);
//
//        public void onCallClick(View view, int position);
//
//    }
//}
