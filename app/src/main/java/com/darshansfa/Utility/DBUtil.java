package com.darshansfa.Utility;

import java.util.List;

import com.darshansfa.dbModel.Invoice;
import com.darshansfa.dbModel.OrderPart;
import com.darshansfa.dbModel.Orders;
import com.darshansfa.dbModel.Retailer;

/**
 * Created by Nikhil on 10-11-2017.
 */

public class DBUtil {
    public static void addOrUpdateRetailerOrder(List<Orders> ordersList, String retailerId) {
        long i = Orders.count(Orders.class, "retailer_id = ?", new String[]{retailerId});
        for (Orders orders : ordersList) {
            if (i == 0) {
                OrderPart.saveInTx(orders.getOrderDetails());
                orders.save();
            } else {
                long orderCount = Orders.count(Orders.class, "order_id = ?", new String[]{orders.getOrderId()});
                if (orderCount == 0) {
                    OrderPart.saveInTx(orders.getOrderDetails());
                    orders.save();
                }
            }
        }
    }

    public static void addOrUpdateOrder(List<Orders> ordersList) {

        OrderPart.deleteAll(OrderPart.class);
        Orders.deleteAll(Orders.class);

        for (Orders orders : ordersList) {
            long orderCount = Orders.count(Orders.class, "order_id = ?", new String[]{orders.getOrderId()});
            if (orderCount == 0) {
                OrderPart.saveInTx(orders.getOrderDetails());
                orders.save();
            }
        }
    }

    public static void addOrUpdateRetailer(List<Retailer> retailerList) {
        for (Retailer retailer : retailerList) {
            long orderCount = Orders.count(Orders.class, "retailer_id = ?", new String[]{retailer.getRetailerId()});
            if (orderCount == 0) {
                retailer.save();
            }
        }
    }

    public static void updateRetailerOutstandingOnList() {

        List<Retailer> retailerArrayList = Retailer.listAll(Retailer.class);
        for (int i = 0; i < retailerArrayList.size(); i++) {
            Retailer retailer = retailerArrayList.get(i);
            List<Invoice> list = Invoice.find(Invoice.class, "retailer_id = ?", new String[]{retailer.getRetailerId()});
            if (list != null && list.size() > 0) {
                float oust = 0f;
                for (int j = 0; j < list.size(); j++) {
                    try {
                        oust += Float.parseFloat(list.get(j).getTotalAmount()) - Float.parseFloat(list.get(j).getCollectedAmount());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                retailer.setOutstandingAmount(String.valueOf(oust));
            } else {
                retailer.setOutstandingAmount("0");
            }
            retailer.save();
        }

    }
}
