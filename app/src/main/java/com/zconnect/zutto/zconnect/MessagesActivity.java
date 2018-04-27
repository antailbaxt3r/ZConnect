package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.zconnect.zutto.zconnect.fragments.ChatTabFragment;
import com.zconnect.zutto.zconnect.fragments.MessageTabFragment;

/* messages activity has 2 tabs and each one has a rv with its adapter and viewholder
                   in the packages(holders and adapters) namely MessageTabRVAdapter,ChatTabRVAdapter
                   along with the item class ChatTabRVItem,MessageTabRVItem(item format package).
                */
public class MessagesActivity extends Fragment {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_messages, container, false);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) v.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

//        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        // Set up the ViewPager with the sections adapter.

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);
//        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            SharedPreferences sharedPref = getActivity().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
            Boolean status = sharedPref.getBoolean("mode", false);
            if (!status) {
                switch (position) {
                    case 0:
                        MessageTabFragment messageTabFragment = new MessageTabFragment();

                        return messageTabFragment;
                    case 1:
                        ChatTabFragment chatTabFragment = new ChatTabFragment();
                        return chatTabFragment;
                    default:
                        return null;
                }
            } else
                return null;
            //return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            SharedPreferences sharedPref = getContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
            Boolean status = sharedPref.getBoolean("mode", false);
            if(!status) {
                switch (position) {
                    case 0:
                        return "Messages";
                    case 1:
                        return "Chats";
                }
            }
            return null;
        }
    }
    
}
