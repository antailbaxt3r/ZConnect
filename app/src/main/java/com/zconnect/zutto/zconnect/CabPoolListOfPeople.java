package com.zconnect.zutto.zconnect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zconnect.zutto.zconnect.ItemFormats.CabItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.UserItemFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class CabPoolListOfPeople extends BaseActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    DatabaseReference pool, chatRef;
    Button join;
    String key;
    String name, number, uid, imageThumb, userUID;
    Vector<UsersListItemFormat> usersListItemFormatVector = new Vector<>();
    UsersListRVAdapter adapter;
    CabItemFormat cabItemFormat;
    Boolean flag, numberFlag;
    //numberFlag person is registered on infone
    //flag person is in cabpool
    String reference, reference_old = "archives", reference_default = "allCabs";
    Long default_frequency;
    Long current_frequency;

    String formatted_date, Date;
    private FirebaseAuth mAuth;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private DatabaseReference databaseReference;

    private FirebaseUser user;
    private ValueEventListener listener;
    private DatabaseReference mDatabaseViews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cab_list_of_people);
        setToolbar();
        setToolbarTitle("List of people");
        showBackButton();
        setSupportActionBar(getToolbar());
        //      showProgressDialog();

        try {
            key = getIntent().getStringExtra("key");
        } catch (Exception e) {
            finish();
        }

        flag = false;
        mAuth = FirebaseAuth.getInstance();


        findViewById(R.id.chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CabPoolListOfPeople.this, ChatActivity.class);
                intent.putExtra("type","cabPool");
                intent.putExtra("key",key);
                intent.putExtra("ref", databaseReference.child(key).toString());
                startActivity(intent);
                Log.e("msg", databaseReference.child(key).toString());
            }
        });

//        mDatabaseViews = FirebaseDatabase.getInstance().getReference().child(ZConnectDetails.COMMUNITIES_DB).child(communityReference).child("features").child("cabPool").child("allCabs").child(key).child("views");
//        updateViews();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    UserItemFormat userDisplayItem = dataSnapshot.getValue(UserItemFormat.class);
                    name = userDisplayItem.getUsername();
                    number = userDisplayItem.getMobileNumber();
                    numberFlag = true;
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //getting present dates and defining format for input and output date
        final Calendar c = Calendar.getInstance();
        SimpleDateFormat output = new SimpleDateFormat("yyyyMMdd");
        Date = output.format(c.getTime());

        formatted_date = getIntent().getStringExtra("date");

        //Setting old database or new database
        if (formatted_date == null) {

            reference = reference_default;

        } else {
            if (Date.compareTo(formatted_date) > 0) {
                reference = reference_old;
            } else {
                reference = reference_default;
            }
        }
        Log.d("msg", reference);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("cabPool").child(reference);
        pool = databaseReference.child(key).child("usersListItemFormats");
        mAuth = FirebaseAuth.getInstance();
        recyclerView = (RecyclerView) findViewById(R.id.content_cabpeople_rv);
        progressBar = (ProgressBar) findViewById(R.id.content_cabpeople_progress);
        join = (Button) findViewById(R.id.join);
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");
        join.setTypeface(customFont);
        progressBar.setVisibility(VISIBLE);
        join.setVisibility(INVISIBLE);

        if (mAuth.getCurrentUser() != null)
            uid = mAuth.getCurrentUser().getUid();
        pool.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                flag = false;
                usersListItemFormatVector.clear();

                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    UsersListItemFormat usersListItemFormat;
                    usersListItemFormat = shot.getValue(UsersListItemFormat.class);
                    if (usersListItemFormat.getUserUID().equals(uid)) {
                        flag = true;
                    }
                    usersListItemFormatVector.add(usersListItemFormat);
                }

                if (flag) {
                    join.setText("Leave");
                } else {
                    join.setText("Join");
                }

                adapter.notifyDataSetChanged();
                join.setVisibility(VISIBLE);
                progressBar.setVisibility(INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(INVISIBLE);
            }
        });


        adapter = new UsersListRVAdapter(this, usersListItemFormatVector);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        recyclerView.setAdapter(adapter);
        pool.keepSynced(true);

        SharedPreferences sharedPref = getSharedPreferences("guestMode", Context.MODE_PRIVATE);
        final Boolean status = sharedPref.getBoolean("mode", false);

        join.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!status) {
                    if (flag) {
                        databaseReference.child(key).child("usersListItemFormats").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(key);
                        Toast.makeText(getApplicationContext(), "Removed", Toast.LENGTH_SHORT).show();

                    } else {
                        final UsersListItemFormat userDetails = new UsersListItemFormat();
                        DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        user.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);
                                userDetails.setImageThumb(userItemFormat.getImageURLThumbnail());
                                userDetails.setName(userItemFormat.getUsername());
                                userDetails.setPhonenumber(userItemFormat.getMobileNumber());
                                userDetails.setUserUID(userItemFormat.getUserUID());
                                databaseReference.child(key).child("usersListItemFormats").child(userItemFormat.getUserUID()).setValue(userDetails);
                                NotificationSender notificationSender=new NotificationSender(getIntent().getStringExtra("key"),null,null,null,null,null,userItemFormat.getUsername(),KeyHelper.KEY_CABPOOL_JOIN,false,true,CabPoolListOfPeople.this);
                                notificationSender.execute();
                                FirebaseMessaging.getInstance().subscribeToTopic(getIntent().getStringExtra("key"));

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                } else {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(CabPoolListOfPeople.this);
                    dialog.setNegativeButton("Lite", null)
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent loginIntent = new Intent(CabPoolListOfPeople.this, LoginActivity.class);
                                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(loginIntent);
                                    finish();
                                }
                            })
                            .setTitle("Please login to join.")
                            .create().show();
                }
            }
        });
    }

//    private void updateViews() {
//
//        SharedPreferences sharedPref = this.getSharedPreferences("guestMode", MODE_PRIVATE);
//        Boolean status = sharedPref.getBoolean("mode", false);
//
//        if (!status) {
//            mAuth = FirebaseAuth.getInstance();
//            user = mAuth.getCurrentUser();
//
//            listener = new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    boolean userExists = false;
//                    for (DataSnapshot childSnapshot :
//                            dataSnapshot.getChildren()) {
//                        if (childSnapshot.getKey().equals(user.getUid()) && childSnapshot.exists() &&
//                                childSnapshot.getValue(Integer.class) != null) {
//                            userExists = true;
//                            int originalViews = childSnapshot.getValue(Integer.class);
//                            mDatabaseViews.child(user.getUid()).setValue(originalViews + 1);
//
//                            break;
//                        } else {
//                            userExists = false;
//                        }
//                    }
//                    if (!userExists) {
//                        mDatabaseViews.child(user.getUid()).setValue(1);
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            };
//
//            mDatabaseViews.addListenerForSingleValueEvent(listener);
//        }
//
//    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
