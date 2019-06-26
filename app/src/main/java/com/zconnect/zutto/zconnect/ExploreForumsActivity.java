package com.zconnect.zutto.zconnect;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.addActivities.AddForumTab;
import com.zconnect.zutto.zconnect.addActivities.RequestForumTab;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.InfoneTabsItemFormat;
import com.zconnect.zutto.zconnect.fragments.ForumsFragment;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.UserUtilities;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class ExploreForumsActivity extends BaseActivity{

    public static Vector<InfoneTabsItemFormat> infoneTabItemFormats = new Vector<>();
    private final String TAG = getClass().getSimpleName();
    FirebaseUser mUser;
    FirebaseAuth mAuth;
    int TotalNumbers;
    DatabaseReference mUserStatsDbRef;
    DatabaseReference mFeaturesStatsDbRef;
    @BindView(R.id.view_pager_app_bar_home)
    ViewPager viewPager;
    @BindView(R.id.tab_layout_app_bar_home)
    TabLayout tabLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private DatabaseReference mPhoneBookDbRef, tabDbRef;
    private String userEmail;

    private SharedPreferences communitySP;
    public String communityReference;

    ViewPagerAdapter adapter;
    private Boolean newUser = false;

    int flag;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fresco.initialize(this);
        setContentView(R.layout.activity_infone);

        ButterKnife.bind(this);
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
//            getWindow().setStatusBarColor(colorDarkPrimary);
//            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        showBackButton();

        communitySP = getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        mAuth = FirebaseAuth.getInstance();

        tabDbRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabs");

        mAuth = FirebaseAuth.getInstance();

        tabDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                infoneTabItemFormats.clear();
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    infoneTabItemFormats.add(shot.getValue(InfoneTabsItemFormat.class));
                }
                adapter = new ViewPagerAdapter(getSupportFragmentManager());
                setupViewPager(viewPager);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", databaseError.toString());
            }
        });

        mUser = mAuth.getCurrentUser();

        mUserStatsDbRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(mUser.getUid()).child("Stats");
        mFeaturesStatsDbRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Stats");
        mPhoneBookDbRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Phonebook");

        mFeaturesStatsDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    TotalNumbers = dataSnapshot.child("TotalNumbers").getValue(Integer.class);
                } catch (Exception e) {
                    TotalNumbers = 0;
                }

                Map<String, Object> taskMap = new HashMap<>();
                taskMap.put("TotalNumbers", TotalNumbers);
                mUserStatsDbRef.updateChildren(taskMap);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        });

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Setup tabLayout with viewpager
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_explore_forums, menu);

        if (getIntent().hasExtra("newUser")) {
            newUser = getIntent().getBooleanExtra("newUser",false);

            MenuItem item = menu.findItem(R.id.action_done);
            if(newUser) {
                item.setVisible(true);
            }else {
                item.setVisible(false);
            }
        }else {
            MenuItem item = menu.findItem(R.id.action_done);
            item.setVisible(false);
        }
        MenuItem item_addTab = menu.findItem(R.id.action_add_tab);
        if (getIntent().hasExtra("userType"))
        {
            if(getIntent().getStringExtra("userType").equals(UsersTypeUtilities.KEY_ADMIN))
            {
               item_addTab.setVisible(true);
               flag = 1;
            }
            else if(getIntent().getStringExtra("userType").equals(UsersTypeUtilities.KEY_VERIFIED))
            {
                item_addTab.setVisible(true);
                flag = 2;
            }
            else
            {
                item_addTab.setVisible(false);
            }
        }
        else
        {
            item_addTab.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            finish();
        }
        if(id == R.id.action_add_tab) {
            if (flag==1)
            startActivity(new Intent(getApplicationContext(), AddForumTab.class));
            else
            {
                startActivity(new Intent(getApplicationContext(), RequestForumTab.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void setupViewPager(final ViewPager viewPager) {

        for (int i = 0; i < infoneTabItemFormats.size(); i++) {
            Fragment fragment = new ForumsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("UID", infoneTabItemFormats.get(i).getUID());
            bundle.putBoolean("newUser",newUser);
            fragment.setArguments(bundle);
            adapter.addFragment(fragment, infoneTabItemFormats.get(i).getName());
        }

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta= new HashMap<>();

                meta.put("catID",infoneTabItemFormats.get(viewPager.getCurrentItem()).getUID());

                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_CATEGORY_OPEN);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);
                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();

//                increaseCount(guestMode, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }



    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
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

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
