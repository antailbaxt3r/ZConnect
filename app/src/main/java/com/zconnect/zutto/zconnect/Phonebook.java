package com.zconnect.zutto.zconnect;

import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Phonebook extends BaseActivity implements View.OnClickListener {
    FirebaseUser mUser;
    FirebaseAuth mAuth;
    int TotalNumbers;
    DatabaseReference mUserStatsDbRef;
    DatabaseReference mFeaturesStatsDbRef;
    @BindView(R.id.view_pager_app_bar_home)
    ViewPager viewPager;
    @BindView(R.id.tab_layout_app_bar_home)
    TabLayout tabLayout;
    @BindView(R.id.toolbar_app_bar_home)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private Boolean guestMode;
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_phonebook);
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

            mFeaturesStatsDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    TotalNumbers = dataSnapshot.child("TotalNumbers").getValue(Integer.class);
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
        fab.setOnClickListener(this);
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

        if (!guestMode) {
            adapter.addFragment(new PhonebookAdmin(), "Admin");
            adapter.addFragment(new PhonebookStudents(), "Students");
            adapter.addFragment(new PhonebookOthersCategories(), "others");
            viewPager.setAdapter(adapter);
        } else {
            adapter.addFragment(new PhonebookAdmin(), "Admin");
            adapter.addFragment(new PhonebookOthersCategories(), "others");
            viewPager.setAdapter(adapter);
        }

        increaseCount(guestMode, viewPager.getCurrentItem());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                increaseCount(guestMode, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
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
            case R.id.fab: {
                if ((!guestMode) && mUser != null) {
                    Intent intent = new Intent(Phonebook.this, EditProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Phonebook.this);

                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder.setMessage("Please Log In to access this feature.")
                            .setTitle("Dear Guest!");

                    builder.setPositiveButton("Log In", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Phonebook.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    });
                    builder.setNegativeButton("Lite :P", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    android.app.AlertDialog dialog = builder.create();
                    dialog.show();
                }
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
