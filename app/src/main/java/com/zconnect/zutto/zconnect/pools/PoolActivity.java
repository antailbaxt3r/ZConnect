package com.zconnect.zutto.zconnect.pools;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.adapters.PoolViewPagerAdapter;
import com.zconnect.zutto.zconnect.pools.models.Pool;

import java.util.ArrayList;

public class PoolActivity extends AppCompatActivity {

    public static final String TAG = "PoolActivity";

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private PoolViewPagerAdapter viewPagerAdapter;
    private ChildEventListener poolListener;
    private ActiveFragment activePoolFragment;
    private UpcomingFragment upcomingPoolFragment;

    private String communityID;
    private ArrayList<Fragment> fragmentsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool);
        // set proper commmunityID from preference
        communityID = "testCollege";

        attachID();
        loadPoolList();


    }

    private void loadPoolList() {
        Query query = FirebaseDatabase.getInstance().getReference(String.format(Pool.URL_POOL, communityID));
        query.addChildEventListener(poolListener);
    }

    private void attachID() {
        //TODO set proper title

        viewPager = findViewById(R.id.pool_view_pager);
        tabLayout = findViewById(R.id.pools_tab);

        //setting up fragments
        activePoolFragment = ActiveFragment.newInstance(communityID);
        upcomingPoolFragment = UpcomingFragment.newInstance(communityID);
        fragmentsList.add(activePoolFragment);
        fragmentsList.add(upcomingPoolFragment);

        //setting view pager
        viewPagerAdapter = new PoolViewPagerAdapter(getSupportFragmentManager(), fragmentsList);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(fragmentsList.size());

        defineListener();

    }

    private void defineListener() {
        poolListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Pool pool = dataSnapshot.getValue(Pool.class);
                pool.setID(dataSnapshot.getKey());
                if (pool.isActive()) {
                    activePoolFragment.addPool(pool);
                } else if (pool.isUpcoming()) {
                    upcomingPoolFragment.addPool(pool);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Pool pool = dataSnapshot.getValue(Pool.class);
                pool.setID(dataSnapshot.getKey());

                Pool oldPool = activePoolFragment.getPool(pool.getID());
                if (oldPool == null) {
                    // upcoming fragment has old version of this pool
                    if (pool.isActive()) {
                        upcomingPoolFragment.removePool(pool);
                        activePoolFragment.addPool(pool);
                    } else {
                        upcomingPoolFragment.updatePool(pool);
                    }


                } else {
                    // active fragment has old version of this pool
                    if (pool.isUpcoming()) {
                        activePoolFragment.removePool(pool);
                        upcomingPoolFragment.addPool(pool);
                    } else {
                        activePoolFragment.updatePool(pool);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Pool pool = dataSnapshot.getValue(Pool.class);
                pool.setID(dataSnapshot.getKey());
                if (pool.isActive()) {
                    activePoolFragment.removePool(pool);
                } else if (pool.isUpcoming()) {
                    upcomingPoolFragment.removePool(pool);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //TODO pool archive
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }


}
