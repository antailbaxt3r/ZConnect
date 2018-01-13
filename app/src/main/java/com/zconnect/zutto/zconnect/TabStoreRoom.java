package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TabStoreRoom extends Fragment {

    Toolbar mActionBarToolbar;
    FirebaseUser user;
    FirebaseAuth mAuth;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_tab_store_room, container, false);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) v.findViewById(R.id.view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tab_layout_app_bar_home);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                if (pos == 0)
                    CounterManager.StoreroomOpenTab("ProductsTab");
                else if (pos == 1)
                    CounterManager.StoreroomOpenTab("ReservedTab");
                else
                    CounterManager.StoreroomOpenTab("CategoriesTab");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the Menu; this adds items to the action bar if it is present.
        SharedPreferences sharedPref = getContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);

        if (!status){
            inflater.inflate(R.menu.menu_storeroom, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_storeroom) {
            startActivity(new Intent(getContext(), MyProducts.class));
        }
        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }



    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            SharedPreferences sharedPref = getContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
            Boolean status = sharedPref.getBoolean("mode", false);
            if (!status){
            switch (position) {
                case 0:
                    ProductsTab productsTab = new ProductsTab();
                    return productsTab;
                case 1:
                    ReservedTab reservedTab = new ReservedTab();
                    return reservedTab;
                case 2:
                    CategoriesTab categoriesTab = new CategoriesTab();
                    return categoriesTab;
                default:
                    return null;
            }}else {
                switch (position) {
                    case 0:
                        ProductsTab productsTab = new ProductsTab();
                        return productsTab;
                    case 1:
                        CategoriesTab categoriesTab = new CategoriesTab();
                        return categoriesTab;
                    default:
                        return null;
                }

            }
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
        }


        @Override
        public int getCount() {

            SharedPreferences sharedPref = getContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
            Boolean status = sharedPref.getBoolean("mode", false);
            if(!status){
                return 3;
            }else {
                return 2;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            SharedPreferences sharedPref = getContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
            Boolean status = sharedPref.getBoolean("mode", false);
            if(!status) {
                switch (position) {
                    case 0:
                        return "Products";
                    case 1:
                        return "Shortlist";
                    case 2:
                        return "Categories";
                }
            }else {
                switch (position) {
                    case 0:
                        return "Products";
                    case 1:
                        return "Categories";
                }
            }
            return null;
        }
    }
}