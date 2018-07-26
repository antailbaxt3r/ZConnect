package com.zconnect.zutto.zconnect;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.zconnect.zutto.zconnect.itemFormats.InfoneTabsItemFormat;
import com.zconnect.zutto.zconnect.fragments.ForumsFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

public class ForumsActivity extends Fragment{

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
    private Boolean guestMode;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(getContext());
        View v = inflater.inflate(R.layout.activity_infone, container, false);
        ButterKnife.bind(this, v);


        adapter = new ViewPagerAdapter(getChildFragmentManager());
        communitySP = getActivity().getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        tabDbRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabs");
      
        mAuth = FirebaseAuth.getInstance();
        SharedPreferences guestModePref = getContext().getSharedPreferences("guestMode", MODE_PRIVATE);
        guestMode = guestModePref.getBoolean("mode", false);

        tabDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                infoneTabItemFormats.clear();
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    infoneTabItemFormats.add(shot.getValue(InfoneTabsItemFormat.class));
                }
                adapter.notifyDataSetChanged();

                setupViewPager(viewPager);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error", databaseError.toString());
            }
        });

        if (!guestMode) {
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
        }
      
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Setup tabLayout with viewpager
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);

        return v;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_phonebook, menu);
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_search_menu_phonebook) {
//            Intent phoneBookSearchIntent = new Intent(getContext(), PhonebookSearch.class);
//            startActivity(phoneBookSearchIntent);
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public void setupViewPager(ViewPager viewPager) {

        for (int i = 0; i < infoneTabItemFormats.size(); i++) {
            Fragment fragment = new ForumsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("UID", infoneTabItemFormats.get(i).getUID());
            fragment.setArguments(bundle);
            adapter.addFragment(fragment, infoneTabItemFormats.get(i).getName());
        }

        viewPager.setAdapter(adapter);
        CounterManager.forumsOpenTab(infoneTabItemFormats.get(viewPager.getCurrentItem()).getUID());
//        increaseCount(guestMode, viewPager.getCurrentItem());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                CounterManager.forumsOpenTab(infoneTabItemFormats.get(position).getName());
//                increaseCount(guestMode, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

//    public void increaseCount(boolean status, int position) {
//        if (!status) {
//            if (position == 0)
//                CounterManager.infoneOpenTab("Admin");
//            else if (position == 1)
//                CounterManager.infoneOpenTab("Students");
//            else if (position == 2)
//                CounterManager.infoneOpenTab("others");
//            else if (position == 3) {
//                CounterManager.infoneOpenTab("AnonymousMessages");
//            }
//        } else {
//            if (position == 0)
//                CounterManager.infoneOpenTab("Admin");
//            else if (position == 1)
//                CounterManager.infoneOpenTab("others");
//        }
//    }


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
