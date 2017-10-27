package com.zconnect.zutto.zconnect;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.fragments.*;
import com.zconnect.zutto.zconnect.fragments.TrendingEvents;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class TabbedEvents extends Fragment {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mUserStats, mFeaturesStats;
    String TotalEvents;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_tabbed_events, container, false);
        super.onCreate(savedInstanceState);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) v.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

//        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        if (toolbar != null) {
//            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    onBackPressed();
//                }
//            });
//            if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
//            int colorDarkPrimary = ContextCompat.getColor(this, R.color.colorPrimaryDark);
//            getWindow().setStatusBarColor(colorDarkPrimary);
//            getWindow().setNavigationBarColor(colorPrimary);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        }
        mViewPager = (ViewPager) v.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        SharedPreferences sharedPref = getContext().getSharedPreferences("guestMode", MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);

        if (!status) {
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();

            mUserStats = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Stats");
            mFeaturesStats = FirebaseDatabase.getInstance().getReference().child("Stats");
            mFeaturesStats.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        TotalEvents = dataSnapshot.child("TotalEvents").getValue().toString();
                        DatabaseReference newPost = mUserStats;
                        Map<String, Object> taskMap = new HashMap<>();
                        taskMap.put("TotalEvents", TotalEvents);
                        newPost.updateChildren(taskMap);
                    }catch (Exception e) {
                        Log.d("Error Alert: ", e.getMessage());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        final FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getContext().getSharedPreferences("guestMode", MODE_PRIVATE);
                Boolean status = sharedPref.getBoolean("mode", false);

                if (!status) {

                    Intent intent = new Intent(getContext(), AddEvent.class);
                    startActivity(intent);
                }else {

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

            }
        });
        return v;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_tabbed_events, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    TrendingEvents trendingEvents = new TrendingEvents();
                    return trendingEvents;
                case 1:
                    TimelineEvents timelineEvents = new TimelineEvents();
                    return timelineEvents;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Trending";
                case 1:
                    return "Timeline";
            }
            return null;
        }
    }
    public void onStart() {
        super.onStart();

        String flag;
        flag = getActivity().getIntent().getStringExtra("snackbar");
        if (flag != null) {
            if (flag.equals("true")) {
                Snackbar snack = Snackbar.make(mViewPager, "Event sent for verification !!", Snackbar.LENGTH_LONG);
                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackBarText.setTextColor(Color.WHITE);
                Typeface customFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Regular.ttf");
                snackBarText.setTypeface(customFont);
                snack.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal800));
                snack.show();
            }
        }
    }

}
