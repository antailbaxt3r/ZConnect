package com.zconnect.zutto.zconnect;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookDisplayItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, FirebaseAuth.AuthStateListener {
    private Homescreen homescreen;
    private ActionBarDrawerToggle toggle;
    private final String TAG = getClass().getSimpleName();
    boolean doubleBackToExitPressedOnce = false;
    String number;
    private FirebaseAuth mAuth;
    private DatabaseReference usersDbRef;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference phoneBookDbRef;
    private DatabaseReference mDatabasePopUps;
    private ValueEventListener phoneBookValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                PhonebookDisplayItem phonebookDisplayItem = child.getValue(PhonebookDisplayItem.class);
                if (userEmail != null
                        && phonebookDisplayItem != null
                        && phonebookDisplayItem.getEmail() != null
                        && phonebookDisplayItem.getEmail().equals(userEmail)) {
                    username = phonebookDisplayItem.getName();
                    number = phonebookDisplayItem.getNumber();
                    Log.v(TAG, number);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "onCancelled: ", databaseError.toException());
        }
    };
    TextView usernameTv;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.view_pager_app_bar_home)
    ViewPager viewPager;
    @BindView(R.id.tab_layout_app_bar_home)
    TabLayout tabLayout;
    @BindView(R.id.toolbar_app_bar_home)
    Toolbar toolbar;
    View navHeader;
    TextView emailTv;
    private Homescreen homescreen;
    private boolean checkUser = true;
    private ActionBarDrawerToggle toggle;
    private String userEmail;
    private String username;
    private FirebaseAuth mAuth;
    private DatabaseReference usersDbRef;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference phoneBookDbRef;
    private DatabaseReference mDatabasePopUps;
    private FirebaseUser mUser;
    private boolean guestMode;

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        SharedPreferences defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        //This code will run for first time.
        if (!defaultPrefs.getBoolean("isReturningUser", false)) {
            Intent tutIntent = new Intent(HomeActivity.this, FullscreenActivity.class);
            tutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(tutIntent);

            //mark first time has run.
            SharedPreferences.Editor editor = defaultPrefs.edit();
            editor.putBoolean("isReturningUser", true);
            editor.commit();
        }

        guestMode = getSharedPreferences("guestMode", MODE_PRIVATE).getBoolean("mode", false);

        usersDbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        phoneBookDbRef = FirebaseDatabase.getInstance().getReference().child("Phonebook");
        //TODO: put sync for all DbRef in Application Class.
        usersDbRef.keepSynced(true);
        phoneBookDbRef.keepSynced(true);

        navHeader = navigationView.getHeaderView(0);
        usernameTv = (TextView) navHeader.findViewById(R.id.tv_name_nav_header);
        emailTv = (TextView) navHeader.findViewById(R.id.tv_email_nav_header);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            username = mUser.getDisplayName();
            userEmail = mUser.getEmail();
        } else if (!guestMode) {
            Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
        usernameTv.setText(username != null ? username : "ZConnect");
        emailTv.setText(userEmail != null ? userEmail : "The way to connect!");

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        addImageDialog();

        homescreen = new Homescreen();

        toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setSupportActionBar(toolbar);

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

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        if (guestMode) {
            MenuItem editProfileItem = navigationView.getMenu().findItem(R.id.edit_profile);
            editProfileItem.setVisible(false);
            editProfileItem.setEnabled(false);
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        setSupportActionBar(toolbar);

        setupViewPager(viewPager);

        //Setup tabLayout with viewpager
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);

        FirebaseMessaging.getInstance().subscribeToTopic("ZCM");

        //put try catch
            mDatabasePopUps = FirebaseDatabase.getInstance().getReference().child("PopUps");
            mDatabasePopUps.keepSynced(true);

            mDatabasePopUps.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    ArrayList<String> popUpUrl1 = new ArrayList<>();
                    ArrayList<String> importance = new ArrayList<>();

                    boolean dataComplete=true;

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                    String first = preferences.getString("popup", "");

                    boolean firstTimePopUp=Boolean.parseBoolean(first);

                    for (DataSnapshot shot : dataSnapshot.getChildren()) {

                        if(shot.child("imp").getValue(String.class)!=null && shot.child("imageUrl").getValue(String.class)!=null) {
                            popUpUrl1.add(shot.child("imageUrl").getValue(String.class));
                            importance.add(shot.child("imp").getValue(String.class));
                            dataComplete=true;
                        }
                        else {

                            dataComplete=false;

                        }

                    }
                    for (int i = 0; i < popUpUrl1.size() && dataComplete && firstTimePopUp ; i++) {

                        double random1 = Math.random();

                        int random = (int) (random1 * 10);

                        int importanceDigit = Integer.parseInt(importance.get(i));

                        boolean show = false;

                        if (importanceDigit == 3) {
                            if (random % 2 == 0)
                                show = true;
                        } else if (importanceDigit == 2) {

                            if (random % 3 == 0)
                                show = true;

                        } else if (importanceDigit == 1) {

                            if (random % 4 == 0)
                                show = true;
                        } else if (importanceDigit == 4) {

                            show = true;
                        } else {
                            show = false;
                        }

                        if (!importance.get(i).equals("0") && show) {
                            CustomDialogClass cdd = new CustomDialogClass(HomeActivity.this, popUpUrl1.get(i));
                            cdd.show();
                            cdd.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //style id
                            Window window = cdd.getWindow();
                            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                            //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(home.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("popup", "false");
                            editor.apply();

                            break;
                        }
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {


                }
            });

        //}
        /*catch (Exception e)
        {
            Log.e("popups",e.toString());
        }*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile: {
                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.infone: {
                CounterManager.InfoneOpen();
                Intent intent = new Intent(this, InfoneActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.shop: {
                CounterManager.ShopOpen();
                Intent intent = new Intent(this, Shop.class);
                startActivity(intent);
                break;
            }
            case R.id.storeRoom: {
                CounterManager.StoreRoomOpen();
                startActivity(new Intent(HomeActivity.this, TabStoreRoom.class));
                break;
            }
            case R.id.events: {
                CounterManager.EventOpen();
                startActivity(new Intent(HomeActivity.this, AllEvents.class));
                break;
            }
            case R.id.cabpool: {
                startActivity(new Intent(HomeActivity.this, CabPooling.class));
                break;
            }
            case R.id.signOut: {
                if (!isNetworkAvailable(getApplicationContext())) {
                    Snackbar snack = Snackbar.make(usernameTv, "No Internet. Can't Log Out.", Snackbar.LENGTH_LONG);
                    TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.WHITE);
                    snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                    snack.show();
                } else {
                    logoutAndSendToLogin();
                }
                break;
            }
            case R.id.ad: {
                CounterManager.AdvertisementOpen();
                startActivity(new Intent(HomeActivity.this, Advertisement.class));
                break;
            }
            case R.id.about: {
                startActivity(new Intent(HomeActivity.this, AboutUs.class));
                break;
            }
            case R.id.mapActivity: {
                CounterManager.MapOpen();
                startActivity(new Intent(this, Campus_Map.class));
                break;
            }
            case R.id.bugReport: {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(HomeActivity.this);
                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("Send a Bug Report or a Feedback to \nzconnectinc@gmail.com")
                        .setTitle("Alert");

                builder.setPositiveButton("Bug Report", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "zconnectinc@gmail.com", null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bug Report");
                        // emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    }
                });
                builder.setNegativeButton("Feedback", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "zconnectinc@gmail.com", null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                        // emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    }
                });
                android.app.AlertDialog dialog = builder.create();
                dialog.show();
                break;
            }
            default: {
                return false;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        phoneBookDbRef.addValueEventListener(phoneBookValueEventListener);
    }

    @Override
    protected void onDestroy() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("popup", "true");
        editor.apply();

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("popup", "true");
        editor.apply();
        phoneBookDbRef.removeEventListener(phoneBookValueEventListener);
        super.onStop();
    }

    @Override
    protected void onPause() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("popup", "true");
        editor.apply();

        super.onPause();
    }

    private void logoutAndSendToLogin() {
        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void setupViewPager(ViewPager viewPager) {
        HomeActivity.ViewPagerAdapter adapter = new HomeActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(homescreen, "Features");
        adapter.addFragment(new Recents(), "Recents");
        viewPager.setAdapter(adapter);
    }

    private void addImageDialog() {
        phoneBookDbRef.orderByChild("email").equalTo(userEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.getChildrenCount()) == 0) {
                    final Long currTime = Calendar.getInstance().getTimeInMillis();
                    SharedPreferences addNumberDialogPref = getSharedPreferences("addNumberDialog", MODE_PRIVATE);
                    Boolean neverAddNumber = addNumberDialogPref.getBoolean("never", false);
                    if (!neverAddNumber || (currTime - addNumberDialogPref.getLong("date", 0) > 2 * 24 * 3600 * 1000)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                        builder.setTitle("Hi " + username)
                                .setMessage("Add your information and get discovered.")
                                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(HomeActivity.this, EditProfileActivity.class));
                                    }
                                }).setNegativeButton("Later", null)
                                .setNeutralButton("Lite :", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SharedPreferences sharedPref = getSharedPreferences("addNumberDialog", MODE_PRIVATE);
                                        SharedPreferences.Editor editInfo = sharedPref.edit();
                                        editInfo.putBoolean("never", true);
                                        editInfo.putLong("date", currTime);
                                        editInfo.apply();
                                    }
                                })
                                .setCancelable(false)
                                .create().show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: error message is " + connectionResult.getErrorMessage());
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
