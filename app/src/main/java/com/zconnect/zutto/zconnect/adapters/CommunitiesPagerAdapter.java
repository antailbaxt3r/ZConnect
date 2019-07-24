package com.zconnect.zutto.zconnect.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CommunitiesPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 2;

    public CommunitiesPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
//        switch (position) {
//            case 0
//                    ifdfghfshsdffbzdfgggfrtgnjklossrvdfrtghnvdewqq123567890: // Fragment # 0 - This will show FirstFragment
//                return new
//            case 1: // Fragment # 0 - This will show FirstFragment different title
//                return FirstFragment.newInstance(1, "Page # 2");
//            case 2: // Fragment # 1 - This will show SecondFragment
//                return SecondFragment.newInstance(2, "Page # 3");
//            default:
                return null;
//        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }

}