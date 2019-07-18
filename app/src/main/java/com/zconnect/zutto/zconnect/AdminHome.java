package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.adapters.NewRequestRVAdapter;
import com.zconnect.zutto.zconnect.addActivities.AddForumTab;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.NewRequestItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.NewUserItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.PostedByDetails;
import com.zconnect.zutto.zconnect.itemFormats.RecentsItemFormat;
import com.zconnect.zutto.zconnect.utilities.VerificationUtilities;
import com.zconnect.zutto.zconnect.adapters.NewUserRVAdapter;

import static com.zconnect.zutto.zconnect.R.drawable.ic_arrow_back_black_24dp;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.ref;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class AdminHome extends BaseActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Admin Home");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(ic_arrow_back_black_24dp);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert_black_24dp));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onBackPressed();
                        }
                    });
        }
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
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
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        tabLayout.setupWithViewPager(mViewPager);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return false;
    }


    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        private RecyclerView newUsersRV;
        private RecyclerView newRequestsRV;

        private LinearLayoutManager linearLayoutManager;

        private NewUserRVAdapter adapter;
        private NewRequestRVAdapter requestsRVAdapter;

        private Vector<NewUserItemFormat> newUserItemFormats = new Vector<NewUserItemFormat>();
        private Vector<NewRequestItemFormat> newRequestItemFormats = new Vector<NewRequestItemFormat>();

        private DatabaseReference newUsersDataReference;
        private DatabaseReference newRequestsDataReference;

        private Boolean flag;

        private TextView noUserMessage;
        private TextView noRequestMessage;

        private ValueEventListener usersDatalistener;
        private ValueEventListener requestsDataListener;

        private ProgressBar progressBar;

        private int filterOption;
        //these values are according to index in the submenu of action_filter MenuItem
        private int FILTER_PENDING = 0;
        private int FILTER_REJECTED = 1;
        private int FILTER_APPROVED = 2;


        //for admin functionalities
        private TextView adminFuncTV0, adminFuncTV1,adminFuncTV2,adminFuncTV3;
        public PlaceholderFragment() {
        }


        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            filterOption = FILTER_PENDING;
            if(getArguments().getInt(ARG_SECTION_NUMBER)==0)
            {
                setHasOptionsMenu(true);
                return verifyUsersTab(inflater, container);
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==1)
            {
                return adminFunctionalityTab(inflater, container);
            }
            else
            {
                return requestsFromUsersTab(inflater,container);
            }
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            if(getArguments().getInt(ARG_SECTION_NUMBER)==0)
            {
                inflater.inflate(R.menu.menu_admin_fragment_users, menu);
            }
            super.onCreateOptionsMenu(menu, inflater);
        }

        private View adminFunctionalityTab(LayoutInflater inflater, ViewGroup container) {
            View rootView = inflater.inflate(R.layout.fragment_admin_functionalities, container, false);
            adminFuncTV0 = rootView.findViewById(R.id.admin_func_0);
            adminFuncTV1 = rootView.findViewById(R.id.admin_func_1);
            adminFuncTV2 = rootView.findViewById(R.id.admin_func_2);
            adminFuncTV3 = rootView.findViewById(R.id.admin_func_3);
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int option = v.getTag().toString().charAt(v.getTag().toString().length()-1) - '0';
                    switch (option)
                    {
                        case 0:
                            startActivity(new Intent(getContext(), CabPoolLocations.class));
                            break;
                        case 1:
                            startActivity(new Intent(getContext(), AddForumTab.class));
                            break;
                        case 2:
                            startActivity(new Intent(getContext(),MakeAdmin.class));
                            break;
                        case 3:
                            startActivity(new Intent(getContext(),NotificationImage.class));
                            break;
                        default:
                            break;
                    }
                }
            };

            adminFuncTV0.setOnClickListener(listener);
            adminFuncTV1.setOnClickListener(listener);
            adminFuncTV2.setOnClickListener(listener);
            adminFuncTV3.setOnClickListener(listener);

            return rootView;
        }

        private View requestsFromUsersTab(LayoutInflater inflater, ViewGroup container) {

            View rootView = inflater.inflate(R.layout.fragment_admin_requests, container, false);
            newRequestsDataReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("admin").child("requests");
            newRequestsRV = rootView.findViewById(R.id.new_requests_recycler);
            linearLayoutManager = new LinearLayoutManager(getContext());
            newRequestsRV.setLayoutManager(linearLayoutManager);
            progressBar = rootView.findViewById(R.id.progress_bar);
            noRequestMessage = rootView.findViewById(R.id.section_label);
            progressBar.setVisibility(View.VISIBLE);

            requestsDataListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    newRequestItemFormats.clear();

                    for (DataSnapshot shot: dataSnapshot.getChildren()){
                        try {
                            NewRequestItemFormat newRequest = shot.getValue(NewRequestItemFormat.class);
                            newRequestItemFormats.add(newRequest);
                        }
                        catch (Exception e) { }
                    }
                    if (newRequestItemFormats.isEmpty())
                        noRequestMessage.setVisibility(View.VISIBLE);

                    Collections.sort(newRequestItemFormats, new Comparator<NewRequestItemFormat>() {
                        @Override
                        public int compare(NewRequestItemFormat o1, NewRequestItemFormat o2) {
                            return o2.getPostTimeMillis().compareTo(o1.getPostTimeMillis());
                        }
                    });

                    requestsRVAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            newRequestsDataReference.addValueEventListener(requestsDataListener);

            requestsRVAdapter = new NewRequestRVAdapter(rootView.getContext(),newRequestItemFormats);
            newRequestsRV.setAdapter(requestsRVAdapter);
            return rootView;
        }


        private View verifyUsersTab(LayoutInflater inflater, ViewGroup container) {
            View rootView = inflater.inflate(R.layout.fragment_admin_home, container, false);
            newUsersDataReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("newUsers");
            newUsersRV = rootView.findViewById(R.id.new_users_recycler);
            linearLayoutManager = new LinearLayoutManager(getContext());
            newUsersRV.setLayoutManager(linearLayoutManager);
            noUserMessage = rootView.findViewById(R.id.section_label);
            progressBar = rootView.findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
            noUserMessage.setVisibility(View.INVISIBLE);
            usersDatalistener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    newUserItemFormats.clear();
                    flag = false;

                    for (DataSnapshot shot: dataSnapshot.getChildren()) {
                        try {
                            NewUserItemFormat newUser = shot.getValue(NewUserItemFormat.class);

                            PostedByDetails postedByDetails = new PostedByDetails();
                            postedByDetails.setUsername("none");
                            postedByDetails.setUID("none");
                            postedByDetails.setImageThumb("none");

                            if(!shot.hasChild("approvedRejectedBy")){
                                newUser.setApprovedRejectedBy(postedByDetails);
                            }
                            if(filterOption==FILTER_PENDING
                                    && newUser.getStatusCode().equals(VerificationUtilities.KEY_PENDING))
                            {
                                newUserItemFormats.add(newUser);
                            }
                            else if(filterOption==FILTER_REJECTED
                                    && newUser.getStatusCode().equals(VerificationUtilities.KEY_NOT_APPROVED))
                            {
                                newUserItemFormats.add(newUser);
                            }
                            else if(filterOption==FILTER_APPROVED
                                    && newUser.getStatusCode().equals(VerificationUtilities.KEY_APPROVED))
                            {
                                newUserItemFormats.add(newUser);
                            }
                            if(newUserItemFormats.isEmpty())
                            flag = true;
                        }catch (Exception e){}
                    }

                    new Handler().postDelayed(() -> {
                        progressBar.setVisibility(View.GONE);
                        if(flag) {
                            noUserMessage.setVisibility(View.VISIBLE);
                        }
                    }, 500);

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            newUsersDataReference.addValueEventListener(usersDatalistener);

            adapter = new NewUserRVAdapter(rootView.getContext(),newUserItemFormats);
            newUsersRV.setAdapter(adapter);
            return rootView;
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.action_filter:
                    View menuItemView = getView().findViewById(R.id.action_filter);
                    Log.i("6ftunder", "YES");
                    MenuItem menuItem = null;
                    switch (filterOption) {
                        case 0:
                            menuItem = item.getSubMenu().findItem(R.id.option_pending);
                            break;
                        case 1:
                            menuItem = item.getSubMenu().findItem(R.id.option_rejected_users);
                            break;
                        case 2:
                            menuItem = item.getSubMenu().findItem(R.id.option_approved_users);
                            break;
                    }
                    menuItem.setChecked(true);
                    return true;
                //submenu of filter
                case R.id.option_pending: {
                    filterOption = FILTER_PENDING;
                    newUsersDataReference.addValueEventListener(usersDatalistener);
                    adapter = new NewUserRVAdapter(getView().getContext(), newUserItemFormats);
                    newUsersRV.setAdapter(adapter);
                    return true;
                }
                case R.id.option_rejected_users: {
                    filterOption = FILTER_REJECTED;
                    newUsersDataReference.addValueEventListener(usersDatalistener);
                    adapter = new NewUserRVAdapter(getView().getContext(),newUserItemFormats);
                    newUsersRV.setAdapter(adapter);
                    return true;
                }
                case R.id.option_approved_users: {
                    filterOption = FILTER_APPROVED;
                    newUsersDataReference.addValueEventListener(usersDatalistener);
                    adapter = new NewUserRVAdapter(getView().getContext(),newUserItemFormats);
                    newUsersRV.setAdapter(adapter);
                    return true;
                }
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return PlaceholderFragment.newInstance(0);
                case 1:
                    return PlaceholderFragment.newInstance(1);
                case 2:
                    return PlaceholderFragment.newInstance(2);
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Verify users";
                case 1:
                    return "Settings";
                case 2:
                    return "Requests";
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 3;
        }
    }
}
