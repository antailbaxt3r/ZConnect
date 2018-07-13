package com.zconnect.zutto.zconnect;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.NewUserItemFormat;
import com.zconnect.zutto.zconnect.Utilities.VerificationUtilities;
import com.zconnect.zutto.zconnect.adapters.newUserRVAdapter;
import static com.zconnect.zutto.zconnect.BaseActivity.communityReference;

import java.util.Vector;

public class AdminHome extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin_home, menu);
        return true;
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


    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private RecyclerView newUsersRV;
        private LinearLayoutManager linearLayoutManager;
        private newUserRVAdapter adapter;
        private Vector<NewUserItemFormat> newUserItemFormats = new Vector<NewUserItemFormat>();
        private DatabaseReference newUsersDataReference;
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
            View rootView = inflater.inflate(R.layout.fragment_admin_home, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            newUsersDataReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("newUsers");
            newUsersRV = (RecyclerView) rootView.findViewById(R.id.new_users_recycler);
            linearLayoutManager = new LinearLayoutManager(getContext());
            newUsersRV.setLayoutManager(linearLayoutManager);

            String tabType = null;
            if (getArguments().getInt(ARG_SECTION_NUMBER)==1){
                tabType = VerificationUtilities.KEY_PENDING;
            }else if(getArguments().getInt(ARG_SECTION_NUMBER) ==2){
                tabType = VerificationUtilities.KEY_NOT_APPROVED;
            }else if(getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                tabType = VerificationUtilities.KEY_APPROVED;
            }


            final String finalTabType = tabType;

            newUsersDataReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    newUserItemFormats.clear();


                    for (DataSnapshot shot: dataSnapshot.getChildren()) {
                        NewUserItemFormat newUser = shot.getValue(NewUserItemFormat.class);

                        if(newUser.getStatusCode().equals(finalTabType)){
                            newUserItemFormats.add(newUser);
                        }
                    }
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            adapter = new newUserRVAdapter(rootView.getContext(),newUserItemFormats);
            newUsersRV.setAdapter(adapter);
            return rootView;


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
                    PlaceholderFragment frag1 = PlaceholderFragment.newInstance(1);
                    return frag1;
                case 1:
                    return PlaceholderFragment.newInstance(2);
                case 2:
                    return PlaceholderFragment.newInstance(3);
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Pending";
                case 1:
                    return "Rejected";
                case 2:
                    return "Approved";

            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}