package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zconnect.zutto.zconnect.CommunitiesAround;

public class CommunitiesPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 2;
    Context context;

    public CommunitiesPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.context = context;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if(context instanceof CommunitiesAround){
                    return ((CommunitiesAround) context).f1;
                }
                return new Fragment();
            case 1: // Fragment # 0 - This will show FirstFragment different title
                if(context instanceof CommunitiesAround){
                    return ((CommunitiesAround) context).f2;
                }
                return new Fragment();
            default:
                return new Fragment();
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0){
            return "COMMUNITIES AROUND";
        }
        else {
            return "COMMUNITIES JOINED";
        }
    }

}