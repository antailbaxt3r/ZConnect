package com.zconnect.zutto.zconnect.pools;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.adapters.PoolViewPagerAdapter;

import java.util.ArrayList;

public class PoolActivity extends AppCompatActivity {

    public static final String TAG = "PoolActivity";

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private PoolViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool);

        attachID();


    }

    private void attachID() {
        //TODO set proper title
        getSupportActionBar().setTitle("Pool");
        viewPager = findViewById(R.id.pool_view_pager);
        tabLayout = findViewById(R.id.pools_tab);

        //setting up fragments
        ArrayList<Fragment> fragmentsList = new ArrayList<>();
        fragmentsList.add(ActiveFragment.newInstance());
        fragmentsList.add(UpcomingFragment.newInstance());

        //setting view pager
        viewPagerAdapter = new PoolViewPagerAdapter(getSupportFragmentManager(), fragmentsList);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(fragmentsList.size());

    }


}
