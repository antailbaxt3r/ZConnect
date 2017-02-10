package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class home extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {
    DatabaseReference mDatabase;
    // For Recycler
    LinearLayoutManager linearLayoutManager;
    RecyclerView mEverything;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
            getWindow().setStatusBarColor(colorPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        mDatabase = FirebaseDatabase.getInstance().getReference("ZConnect").child("everything");
        mDatabase.keepSynced(true);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser()==null)
                {
                    Intent loginIntent = new Intent(home.this, logIn.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }else {
                    checkUser();
                }

            }
        };

        //For Recycler
        mEverything = (RecyclerView) findViewById(R.id.everything);
        mEverything.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mEverything.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.infone) {
            Intent intent = new Intent(this, Phonebook.class);
            startActivity(intent);
        } else if (id == R.id.shop) {
            Intent intent = new Intent(this, Shop.class);
            startActivity(intent);
        } else if (id == R.id.storeRoom) {
            startActivity(new Intent(home.this, TabStoreRoom.class));

        } else if (id == R.id.events) {

            startActivity(new Intent(home.this, AllEvents.class));

        } else if (id == R.id.signOut) {

            logout();
        } else if (id == R.id.ad) {
            startActivity(new Intent(home.this, Advertisement.class));

        } else if (id == R.id.about) {
            startActivity(new Intent(home.this, AboutUs.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);


    }

    @Override
    protected void onResume() {
        super.onResume();
        makeRecyclerView();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mEverything.setAdapter(null);
    }

    private void logout(){
        mAuth.signOut();
    }

    private void checkUser()
    {
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(mAuth.getCurrentUser().getUid()))
                {
                    Intent setDetailsIntent = new Intent(home.this, setDetails.class);
                    setDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setDetailsIntent);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    void makeRecyclerView() {
        FirebaseRecyclerAdapter<homeRecyclerClass, everythingViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<homeRecyclerClass, everythingViewHolder>(
                homeRecyclerClass.class,
                R.layout.everything_row,
                everythingViewHolder.class,
                mDatabase) {


            @Override
            protected void populateViewHolder(everythingViewHolder viewHolder, homeRecyclerClass model, int position) {


                if (model.getType().equals("E") && model.getType() != null) {

                    viewHolder.removeView();
                    viewHolder.setTitle(model.getTitle());
                    viewHolder.setDescription(model.getDescription());
                    viewHolder.setImage(getApplicationContext(), model.getUrl());
                    viewHolder.setBarColor(getApplicationContext(), true);
                    viewHolder.setDate(model.getmultiUse2(), false, getApplicationContext());
                    viewHolder.makeButton(model.getTitle(), model.getDescription(), Long.parseLong(model.getmultiUse1()));
                } else if (model.getType().equals("Pro") && model.getType() != null) {
                    viewHolder.removeView();
                    viewHolder.setTitle(model.getTitle());
                    viewHolder.setDescription(model.getDescription());
                    viewHolder.setBarColor(getApplicationContext(), false);
                    viewHolder.setImage(getApplicationContext(), model.getUrl());
                    viewHolder.setDate(String.valueOf(model.getPhone_no()), true, getApplicationContext());

                } else if (model.getType().equals("P") && model.getType() != null) {

                    PhonebookDisplayItem displayItem = new PhonebookDisplayItem(model.getUrl(), model.getTitle(), model.getDescription(), String.valueOf(model.getPhone_no()), model.getmultiUse1(), model.getmultiUse2(), "Hostel");
                    viewHolder.makeContactView(getApplicationContext(), displayItem);

                }
                //Toast.makeText(getApplicationContext(),"Check 1",Toast.LENGTH_LONG);


            }


        };
        mEverything.setAdapter(firebaseRecyclerAdapter);
    }

}
