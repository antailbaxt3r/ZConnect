package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;

public class TabStoreRoom extends BaseActivity {

    FirebaseUser user;
    FirebaseAuth mAuth;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;


//    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_store_room);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
            int colorDarkPrimary = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            getWindow().setStatusBarColor(colorDarkPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout_app_bar_home);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                if (pos == 0)
                    CounterManager.StoreroomOpenTab("ProductsTab");
                else if (pos == 1)
                    CounterManager.StoreroomOpenTab("Shortlist");
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);

        if (!status){
            getMenuInflater().inflate(R.menu.menu_storeroom, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_storeroom) {
            startActivity(new Intent(getApplicationContext(), MyProducts.class));
        } else if (id == R.id.action_storeroom_shortlist){
            startActivity(new Intent(getApplicationContext(), Shortlist.class));
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
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
            Boolean status = sharedPref.getBoolean("mode", false);
            if (!status){
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
            }else {
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
        }


        @Override
        public int getCount() {

            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
            Boolean status = sharedPref.getBoolean("mode", false);
            if(!status){
                return 2;
            }else {
                return 2;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
            Boolean status = sharedPref.getBoolean("mode", false);
            if(!status) {
                switch (position) {
                    case 0:
                        return "Products";
                    case 1:
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