package com.darshansfa.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.darshansfa.Fragment.AdvancePaymentFragment;
import com.darshansfa.Fragment.DayCloseCollectionFragment;
import com.darshansfa.Fragment.DayCloseOrderFragment;

/**
 * Created by Akshay on 15-May-16.
 */
public class DayCloseTabPagerAdapter extends FragmentPagerAdapter {
    int tabCount;

    public DayCloseTabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return DayCloseOrderFragment.newInstance(null, null);
            case 1:
                return DayCloseCollectionFragment.newInstance(null, null);
            case 2:
                return new AdvancePaymentFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
