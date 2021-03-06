package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.fragments.UpdateAppActivity;

import java.util.ArrayList;
import java.util.List;

public class Shop_detail extends BaseActivity {

    String name, imageurl;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private SimpleDraweeView simpleDraweeView;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);


        Toolbar toolbar = (Toolbar) findViewById(R.id.shop_details_toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
            int colorDarkPrimary = ContextCompat.getColor(this, R.color.colorPrimaryDark);
//            getWindow().setStatusBarColor(colorDarkPrimary);
//            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        setSupportActionBar(toolbar);
        ((NestedScrollView) findViewById(R.id.scroll)).setFillViewport(true);


        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.shop_details_collapsing_toolbar);
        simpleDraweeView = (SimpleDraweeView) findViewById(R.id.activity_shop_details_backdrop);
        name = getIntent().getStringExtra("Name");
        imageurl = getIntent().getStringExtra("Imageurl");
      //  Log.v("im",imageurl);
        if (name == null)
            finish();
        getSupportActionBar().setTitle(name);
        if (imageurl != null) {
            simpleDraweeView.setImageURI(Uri.parse(imageurl));
            collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        }

        viewPager = (ViewPager) findViewById(R.id.view_pager_app_bar_home);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout_app_bar_home);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        setupViewPager(viewPager);

        assert tabLayout != null;
        //Setup tablayout with viewpager
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);

    }


    private void setupViewPager(ViewPager viewPager) {
        Shop_detail.ViewPagerAdapter adapter = new Shop_detail.ViewPagerAdapter(getSupportFragmentManager());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1){}

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        adapter.addFragment(new ShopDetailFragment(), "Details");
        adapter.addFragment(new ShopOffersFragment(), "Offers");
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}

