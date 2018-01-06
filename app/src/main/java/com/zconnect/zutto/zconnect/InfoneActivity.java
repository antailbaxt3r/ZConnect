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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.fragments.AnonymMessages;
import com.zconnect.zutto.zconnect.fragments.InfoneFacultyFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InfoneActivity extends Fragment implements View.OnClickListener {

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
            if (!userAddedToInfone) fab.setVisibility(View.VISIBLE);
            else fab.setVisibility(View.GONE);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "onCancelled: ", databaseError.toException());
        }
    };
    private Boolean guestMode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(getContext());
        View v = inflater.inflate(R.layout.activity_infone, container, false);
        ButterKnife.bind(this, v);

        mAuth = FirebaseAuth.getInstance();
        SharedPreferences guestModePref = getContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
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

        setupViewPager(viewPager);

        //Setup tabLayout with viewpager
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
        fab.setOnClickListener(this);
        if (!guestMode) {
            userEmail = mUser.getEmail();
            if (!TextUtils.isEmpty(userEmail)) {
                mPhoneBookDbRef.addListenerForSingleValueEvent(phoneBookListener);
            }
        }
        if (fab != null && fab.getVisibility() == View.VISIBLE) fab.setOnClickListener(this);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_phonebook, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search_menu_phonebook) {
            Intent phoneBookSearchIntent = new Intent(getContext(), PhonebookSearch.class);
            startActivity(phoneBookSearchIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new InfoneFacultyFragment(), "Admin");
        if (!guestMode) adapter.addFragment(new PhonebookStudents(), "Students");
        adapter.addFragment(new PhonebookOthersCategories(), "others");
        adapter.addFragment(new AnonymMessages(), "Messages");
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
            else if (position ==3)
            {
                CounterManager.infoneOpenTab("AnonymousMessages");
            }
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
                    Intent intent = new Intent(getContext(), EditProfileActivity.class);
                    startActivity(intent);
                } else {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());

                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder.setMessage("Please Log In to access this feature.")
                            .setTitle("Dear Guest!");

                    builder.setPositiveButton("Log In", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
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
