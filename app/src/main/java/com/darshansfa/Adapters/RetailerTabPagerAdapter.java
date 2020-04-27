package com.darshansfa.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.darshansfa.Fragment.BackOrderFragment;
import com.darshansfa.Fragment.InvoiceFragment;
import com.darshansfa.Fragment.OrdersFragment;
import com.darshansfa.Fragment.RetailerReportFragment;
import com.darshansfa.Fragment.SalesReturnFragment;

/**
 * Created by Akshay on 15-May-16.
 */
public class RetailerTabPagerAdapter extends FragmentPagerAdapter {
    int tabCount;

    public RetailerTabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OrdersFragment();
            case 1:
                return new InvoiceFragment();
            case 2:
                return new SalesReturnFragment();
            case 3:
                return new RetailerReportFragment();
            case 4:
                return new BackOrderFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
