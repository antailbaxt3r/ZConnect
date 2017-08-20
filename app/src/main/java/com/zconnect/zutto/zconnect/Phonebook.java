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

public class Phonebook extends BaseActivity {
    FirebaseUser user;
    FirebaseAuth mAuth;
    String TotalNumbers;
    String userId;
    DatabaseReference mUserStats, mFeaturesStats;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_phonebook);

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

        mAuth = FirebaseAuth.getInstance();

        SharedPreferences sharedPref = getSharedPreferences("guestMode", MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);

        if (!status) {
            user = mAuth.getCurrentUser();

            mUserStats = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Stats");
            mFeaturesStats = FirebaseDatabase.getInstance().getReference().child("Stats");

            mFeaturesStats.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    TotalNumbers = dataSnapshot.child("TotalNumbers").getValue().toString();
                    DatabaseReference newPost = mUserStats;
                    Map<String, Object> taskMap = new HashMap<String, Object>();
                    taskMap.put("TotalNumbers", TotalNumbers);
                    newPost.updateChildren(taskMap);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        viewPager = (ViewPager) findViewById(R.id.view_pager_app_bar_home);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout_app_bar_home);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        setSupportActionBar(toolbar);

        setupViewPager(viewPager);

        assert tabLayout != null;
        //Setup tablayout with viewpager
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getSharedPreferences("guestMode", MODE_PRIVATE);
                Boolean status = sharedPref.getBoolean("mode", false);

                if (!status) {
                    Intent intent = new Intent(Phonebook.this, AddContact.class);
                    startActivity(intent);
                    finish();
                }else {

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

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_phonebook, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            Intent searchintent = new Intent(this, PhonebookSearch.class);
            startActivity(searchintent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        SharedPreferences sharedPref = getSharedPreferences("guestMode", MODE_PRIVATE);
        final Boolean status = sharedPref.getBoolean("mode", false);

        if (!status) {
            adapter.addFragment(new PhonebookAdmin(), "Admin");
            adapter.addFragment(new PhonebookStudents(), "Students");
            adapter.addFragment(new PhonebookOthersCategories(), "others");
            viewPager.setAdapter(adapter);
        }else {
            adapter.addFragment(new PhonebookAdmin(), "Admin");
            adapter.addFragment(new PhonebookOthersCategories(), "others");
            viewPager.setAdapter(adapter);
        }

        increaseCount(status, viewPager.getCurrentItem());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                increaseCount(status, position);
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
