package com.zconnect.zutto.zconnect.pools;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.BuildConfig;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.Shop_detail;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.fragments.UpdateAppActivity;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.pools.adapters.PoolViewPagerAdapter;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;

import java.util.ArrayList;
import java.util.HashMap;

public class PoolActivity extends BaseActivity {

    public static final String TAG = "PoolActivity";

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private PoolViewPagerAdapter viewPagerAdapter;
    private ActiveFragment activePoolFragment;
    private UpcomingFragment upcomingPoolFragment;

    private ArrayList<Fragment> fragmentsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseDatabase.getInstance().getReference().child("communities").
                child(communityReference).child("features").child("shops").
                child("minimumClientVersion").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("VERSIONN",dataSnapshot.getValue(Integer.class)+"");
                        if (dataSnapshot.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                            Intent intent = new Intent(PoolActivity.this, UpdateAppActivity.class);
                            intent.putExtra("feature", "shops");
                            startActivity(intent);
                            finish();

                        } else {
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool);
        //TODO  set proper commmunityID from preference

        setToolbar();
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        attachID();


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
//            getWindow().setStatusBarColor(colorDarkPrimary);
//            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pool_shop, menu);
        return true;
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_orders) {

            CounterItemFormat counterItemFormat = new CounterItemFormat();
            HashMap<String, String> meta= new HashMap<>();
            meta.put("type","fromFeature");
            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
            counterItemFormat.setUniqueID(CounterUtilities.KEY_SHOPS_MY_ORDERS_OPEN);
            counterItemFormat.setTimestamp(System.currentTimeMillis());
            counterItemFormat.setMeta(meta);
            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
            counterPush.pushValues();

            Intent i = new Intent(this, MyOrdersActivity.class);
            startActivity(i);
        }
        return true;
    }


    private void attachID() {
        //TODO set proper title

        viewPager = findViewById(R.id.pool_view_pager);
        tabLayout = findViewById(R.id.pools_tab);

        //setting up fragments
        activePoolFragment = ActiveFragment.newInstance();
        upcomingPoolFragment = UpcomingFragment.newInstance();
        fragmentsList.add(activePoolFragment);
        fragmentsList.add(upcomingPoolFragment);

        //setting view pager
        viewPagerAdapter = new PoolViewPagerAdapter(getSupportFragmentManager(), fragmentsList);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(fragmentsList.size());

    }


}
