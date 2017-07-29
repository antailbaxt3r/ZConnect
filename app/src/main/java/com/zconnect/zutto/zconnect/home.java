package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookDisplayItem;

import java.util.ArrayList;
import java.util.List;

public class home extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    DatabaseReference mData;
    Homescreen homescreen ;
    com.google.firebase.database.Query mDatabase;
    // For Recycler
    LinearLayoutManager linearLayoutManager;
    RecyclerView mEverything;
    boolean checkuser = true;
    ActionBarDrawerToggle toggle;

    String email = null, name = null;
    Boolean flag=false;
    boolean doubleBackToExitPressedOnce = false;
    String number = null;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseUsers;
    private GoogleApiClient mGoogleApiClient;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Phonebook");
    private TextView username, useremail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sharedPref = getSharedPreferences("guestMode",MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            // <---- run your one time code here
            Intent tutIntent =new Intent(home.this,FullscreenActivity.class);
            tutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(tutIntent);
            //mark first time has runned.

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.apply();
        }
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            addImageDialog();
         homescreen = new Homescreen();
        //        Intent called = getIntent();
//        homescreen = called.hasExtra("type")?new Homescreen("new"):new Homescreen(null);
//        if (called.hasExtra("type"))
//            checkuser = false;
//        if (called.hasExtra("type")) {
//            if (called.getStringExtra("type").equals("new")) {
//                Log.d("Extra","this opens");
//                checkuser = false;
//                homescreen.type = "new" ;
//            }
//        }
//       homescreen.type = "new" ;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        if (status) {
            navigationView.getMenu().findItem(R.id.edit_profile).setVisible(false);
        }
        username = (TextView) header.findViewById(R.id.textView_1);
        useremail = (TextView) header.findViewById(R.id.textView_2);


        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        //mDatabase.keepSynced(true);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {

                    checkUser();
                }

            }
        };
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getDisplayName() != null)
            name = mAuth.getCurrentUser().getDisplayName();
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getEmail() != null)
            email = mAuth.getCurrentUser().getEmail();
        if (name != null)
            username.setText(name);
        if (email != null)
            useremail.setText(email);

        databaseReference.keepSynced(true);

        if (mAuth.getCurrentUser() == null)
        if(!status) {
            header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (number != null) {
                        Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), AddContact.class);
                        startActivity(intent);
                    }
                    }
            });

        }
        viewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        setSupportActionBar(toolbar);

        setupViewPager(viewPager);

        assert tabLayout != null;
        //Setup tablayout with viewpager
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);
        isNetworkAvailable(this);

        FirebaseMessaging.getInstance().subscribeToTopic("aweasd");

        RemoteMessage.Builder creator = new RemoteMessage.Builder("/topics/aweasd");
        creator.addData("Type", "CabPool");
        creator.addData("Person", " asdsad");
        creator.addData("Contact", "sadsad");
        creator.addData("Pool", "sefse");

        Log.d(creator.build().getFrom(), creator.build().getTo());
        FirebaseMessaging.getInstance().send(creator.build());


        //changing fonts
        Typeface ralewayBold = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Thin.ttf");
    }
//    @Override
//    protected void onResume() {
//    protected void onResume() {
//        super.onResume();
//
//        mAuth.addAuthStateListener(mAuthListener);
//
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id== R.id.edit_profile){

            if (number != null) {
                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(getApplicationContext(), AddContact.class);
                startActivity(intent);
            }


        }
        else if (id == R.id.infone) {
            CounterManager.InfoneOpen();
            Intent intent = new Intent(this, Phonebook.class);
            startActivity(intent);
        } else if (id == R.id.shop) {
            CounterManager.ShopOpen();
            Intent intent = new Intent(this, Shop.class);
            startActivity(intent);
        } else if (id == R.id.storeRoom) {
            CounterManager.StoreRoomOpen();
            startActivity(new Intent(home.this, TabStoreRoom.class));

        } else if (id == R.id.events) {
            CounterManager.EventOpen();
            startActivity(new Intent(home.this, AllEvents.class));

        } else if (id == R.id.cabpool) {

            startActivity(new Intent(home.this, CabPooling.class));

        } else if (id == R.id.signOut) {
            if (!isNetworkAvailable(getApplicationContext())) {

                Snackbar snack = Snackbar.make(username, "No Internet. Can't Log Out.", Snackbar.LENGTH_LONG);
                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackBarText.setTextColor(Color.WHITE);
                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                snack.show();

            } else {
                logout();
            }

        } else if (id == R.id.ad) {
            CounterManager.AdvertisementOpen();
            startActivity(new Intent(home.this, Advertisement.class));

        } else if (id == R.id.about) {
            startActivity(new Intent(home.this, AboutUs.class));

        } else if (id == R.id.mapActivity) {
            CounterManager.MapOpen();
            startActivity(new Intent(this, Campus_Map.class));
        } else if (id == R.id.bugReport) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(home.this);

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


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser()!=null &&mAuth.getCurrentUser().getEmail() != null)
            email = mAuth.getCurrentUser().getEmail();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot : dataSnapshot.getChildren()) {

                    PhonebookDisplayItem phonebookDisplayItem = shot.getValue(PhonebookDisplayItem.class);

                    if (email!=null&&phonebookDisplayItem.getEmail()!=null&&phonebookDisplayItem.getEmail().equals(email)) {
                        name = phonebookDisplayItem.getName();
                        number = phonebookDisplayItem.getNumber();
                        flag= true;
                        Log.v("Tag",number);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


    private void logout(){
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);

        if (mAuth.getCurrentUser() == null) {
            Intent loginIntent = new Intent(home.this, logIn.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mAuth.removeAuthStateListener(mAuthListener);
            startActivity(loginIntent);
            finish();
        }

    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


    private void checkUser()
    {
        if(checkuser) {
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                        if(!checkuser){
                        Intent setDetailsIntent = new Intent(home.this, setDetails.class);
                            setDetailsIntent.putExtra("caller","home");
                        setDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setDetailsIntent);

                    }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }


        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }

    private void setupViewPager(ViewPager viewPager) {
        home.ViewPagerAdapter adapter = new home.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(homescreen, "Features");
        adapter.addFragment(new Recents(), "Recents");
        viewPager.setAdapter(adapter);
    }

    private void addImageDialog() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Phonebook");
        ref.orderByChild("email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.getChildrenCount()) == 0) {
                    SharedPreferences sharedPref = getSharedPreferences("addNumberDialog", MODE_PRIVATE);
                    Boolean status = sharedPref.getBoolean("never", false);
                    if (!status) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(home.this);
                        builder.setTitle("Hi " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                                .setMessage("Add your information and get discovered.")
                                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(home.this, AddContact.class));
                                    }
                                }).setNegativeButton("Later", null)
                                .setNeutralButton("Lite :", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SharedPreferences sharedPref = getSharedPreferences("addNumberDialog", MODE_PRIVATE);

                                        SharedPreferences.Editor editInfo = sharedPref.edit();
                                        editInfo.putBoolean("never", true);
                                        editInfo.apply();
                                        editInfo.commit();
                                    }
                                })
                                .setCancelable(false)
                                .create().show();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
