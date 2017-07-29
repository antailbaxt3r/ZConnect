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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TabStoreRoom extends BaseActivity {

    Toolbar mActionBarToolbar;
    FirebaseUser user;
    FirebaseAuth mAuth;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_store_room);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("StoreRoom");
        if (mActionBarToolbar != null) {
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
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


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPref = getSharedPreferences("guestMode", MODE_PRIVATE);
                Boolean status = sharedPref.getBoolean("mode", false);
                if (!status) {
                    CounterManager.StoreRoomFABclick();
                    Intent intent = new Intent(TabStoreRoom.this, AddProduct.class);
                    startActivity(intent);
                    finish();
                }else
                {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(TabStoreRoom.this);

                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder.setMessage("Please Log In to access this feature.")
                            .setTitle("Dear Guest!");

                    builder.setPositiveButton("Log In", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(TabStoreRoom.this, logIn.class);
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
        // Inflate the Menu; this adds items to the action bar if it is present.
        SharedPreferences sharedPref = getSharedPreferences("guestMode", MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);

        if (!status){
            getMenuInflater().inflate(R.menu.menu_storeroom, menu);
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_storeroom) {
            startActivity(new Intent(TabStoreRoom.this, MyProducts.class));
        }
        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    //NOT NEEDED..
//    public static class PlaceholderFragment extends Fragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        public PlaceholderFragment() {
//        }
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_tab_store_room, container, false);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
//            return rootView;
//        }
//    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent storeIntent = new Intent(TabStoreRoom.this, home.class);
        startActivity(storeIntent);
        finish();
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
            SharedPreferences sharedPref = getSharedPreferences("guestMode", MODE_PRIVATE);
            Boolean status = sharedPref.getBoolean("mode", false);
            if (!status){
            switch (position) {
                case 0:
                    ProductsTab productsTab = new ProductsTab();
                    return productsTab;
                case 1:
                    ReservedTab reservedTab = new ReservedTab();
                    return reservedTab;
                case 2:
                    CategoriesTab categoriesTab = new CategoriesTab();
                    return categoriesTab;
                default:
                    return null;
            }}else {
                switch (position) {
                    case 0:
                        ProductsTab productsTab = new ProductsTab();
                        return productsTab;
                    case 1:
                        CategoriesTab categoriesTab = new CategoriesTab();
                        return categoriesTab;
                    default:
                        return null;
                }

            }
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
        }


        @Override
        public int getCount() {

            SharedPreferences sharedPref = getSharedPreferences("guestMode", MODE_PRIVATE);
            Boolean status = sharedPref.getBoolean("mode", false);
            if(!status){
                return 3;
            }else {
                return 2;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            SharedPreferences sharedPref = getSharedPreferences("guestMode", MODE_PRIVATE);
            Boolean status = sharedPref.getBoolean("mode", false);
            if(!status) {
                switch (position) {
                    case 0:
                        return "Products";
                    case 1:
                        return "Shortlist";
                    case 2:
                        return "Categories";
                }
            }else {
                switch (position) {
                    case 0:
                        return "Products";
                    case 1:
                        return "Categories";
                }
            }
            return null;
        }
    }
}