package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.fragments.InfoneFacultyFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InfoneActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    FirebaseUser mUser;
    FirebaseAuth mAuth;
    int TotalNumbers;
    DatabaseReference mUserStatsDbRef;
    DatabaseReference mFeaturesStatsDbRef;

    /**
     * References /Phonebook/
     */
    private DatabaseReference mPhoneBookDbRef;

    /**
     * Email of user.
     */
    private String userEmail;

    /**
     * Sets visibility of add contact fab according to whether user is registered in infone.
     */
    private ValueEventListener phoneBookListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            boolean userAddedToInfone = false;
            for (DataSnapshot child :
                    dataSnapshot.getChildren()) {
                if (userEmail.equals(child.child("email").getValue(String.class)))
                    userAddedToInfone = true;
            }
            if (!userAddedToInfone) addContactFab.setVisibility(View.VISIBLE);
            else addContactFab.setVisibility(View.GONE);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "onCancelled: ", databaseError.toException());
        }
    };

    @BindView(R.id.view_pager_app_bar_home)
    ViewPager viewPager;
    @BindView(R.id.tab_layout_app_bar_home)
    TabLayout tabLayout;
    @BindView(R.id.toolbar_app_bar_home)
    Toolbar toolbar;

    /**
     * Add Contact fab for users not registered to infone.
     */
    @BindView(R.id.fab_add_contact_act_infone)
    FloatingActionButton addContactFab;

    private Boolean guestMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_infone);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            if (getSupportActionBar() != null)
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

        mAuth = FirebaseAuth.getInstance();

        SharedPreferences guestModePref = getSharedPreferences("guestMode", MODE_PRIVATE);
        guestMode = guestModePref.getBoolean("mode", false);

        if (!guestMode) {
            mUser = mAuth.getCurrentUser();

            mUserStatsDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid()).child("Stats");
            mFeaturesStatsDbRef = FirebaseDatabase.getInstance().getReference().child("Stats");
            mPhoneBookDbRef = FirebaseDatabase.getInstance().getReference("Phonebook");

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
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        setSupportActionBar(toolbar);

        setupViewPager(viewPager);

        //Setup tabLayout with viewpager
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!guestMode) {
            userEmail = mUser.getEmail();
            if (!TextUtils.isEmpty(userEmail)) {
                mPhoneBookDbRef.addListenerForSingleValueEvent(phoneBookListener);
            }
        }
        if (addContactFab != null && addContactFab.getVisibility() == View.VISIBLE) addContactFab.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        if (addContactFab != null && addContactFab.getVisibility() == View.VISIBLE ) addContactFab.setOnClickListener(null); // removes onClickListener
        if (mPhoneBookDbRef != null) mPhoneBookDbRef.removeEventListener(phoneBookListener);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_phonebook, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search_menu_phonebook) {
            Intent phoneBookSearchIntent = new Intent(this, PhonebookSearch.class);
            startActivity(phoneBookSearchIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new InfoneFacultyFragment(), "Admin");
        if (!guestMode) adapter.addFragment(new PhonebookStudents(), "Students");
        adapter.addFragment(new PhonebookOthersCategories(), "others");
        viewPager.setAdapter(adapter);

        increaseCount(guestMode, viewPager.getCurrentItem());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                increaseCount(guestMode, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void increaseCount(boolean status, int position) {
        if (!status) {
            if (position == 0)
                CounterManager.infoneOpenTab("Admin");
            else if (position == 1)
                CounterManager.infoneOpenTab("Students");
            else if (position == 2)
                CounterManager.infoneOpenTab("others");
        } else {
            if (position == 0)
                CounterManager.infoneOpenTab("Admin");
            else if (position == 1)
                CounterManager.infoneOpenTab("others");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_contact_act_infone: {
                addContactFab.setVisibility(View.GONE); // we don't know what will happen in next intent, so revert visibility to default.
                Intent intent = new Intent(InfoneActivity.this, EditProfileActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
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
