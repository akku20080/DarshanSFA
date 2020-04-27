package com.darshansfa.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.darshansfa.Fragment.ProductAssociateFragment;
import com.darshansfa.Fragment.ProductCategoryFragment;
import com.darshansfa.Fragment.ProductFocusedFragment;
import com.darshansfa.Fragment.ProductLocalityFragment;
import com.darshansfa.Fragment.ProductMasterFragment;

/**
 * Created by Akshay on 15-May-16.
 */
public class ProductTabPagerAdapter extends FragmentPagerAdapter {
    int tabCount;

    public ProductTabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ProductMasterFragment();
            case 1:
                return new ProductCategoryFragment();
            case 2:
                return new ProductLocalityFragment();
            case 3:
                return new ProductAssociateFragment();
            case 4:
                return new ProductFocusedFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
