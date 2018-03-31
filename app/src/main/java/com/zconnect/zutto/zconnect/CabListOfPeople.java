package com.zconnect.zutto.zconnect;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.zconnect.zutto.zconnect.ItemFormats.CabItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.CabListItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookDisplayItem;

import org.json.JSONObject;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class CabListOfPeople extends BaseActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    DatabaseReference pool, chatRef;
    Button join;
    String key;
    String name, number, email, imageThumb, uid;
    Vector<CabListItemFormat> cabListItemFormatVector = new Vector<>();
    CabPeopleRVAdapter adapter;
    CabItemFormat cabItemFormat;
    Boolean flag, numberFlag;
    //numberFlag person is registered on infone
    //flag person is in cabpool
    String reference, reference_old = "archive/Cab", reference_default = "Cab";
    Long default_frequency;
    Long current_frequency;

    String formatted_date, Date;
    private FirebaseAuth mAuth;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Phonebook");
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
                Intent intent = new Intent(CabListOfPeople.this, ChatActivity.class);
                intent.putExtra("ref", databaseReference.child(key).toString());
                startActivity(intent);
                Log.e("msg",databaseReference.child(key).toString());

            }
        });

        mDatabaseViews = FirebaseDatabase.getInstance().getReference().child(ZConnectDetails.COMMUNITIES_DB).child(communityReference).child("Cab").child(key).child("views");
        updateViews();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    PhonebookDisplayItem phonebookDisplayItem = shot.getValue(PhonebookDisplayItem.class);
                    if (email != null) {
                        if (phonebookDisplayItem != null && phonebookDisplayItem.getEmail() != null) {
                            if (phonebookDisplayItem.getEmail().equals(email)) {
                                name = phonebookDisplayItem.getName();
                                number = phonebookDisplayItem.getNumber();
                                numberFlag = true;
                            }
                        }
                    }
                }

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
    Log.d("msg",reference);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child(reference);
        pool = databaseReference.child(key).child("cabListItemFormats");
        mAuth = FirebaseAuth.getInstance();
        recyclerView = (RecyclerView) findViewById(R.id.content_cabpeople_rv);
        progressBar = (ProgressBar) findViewById(R.id.content_cabpeople_progress);
        join = (Button) findViewById(R.id.join);
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");
        join.setTypeface(customFont);
        progressBar.setVisibility(VISIBLE);
        join.setVisibility(INVISIBLE);

        if (mAuth.getCurrentUser() != null)
            email = mAuth.getCurrentUser().getEmail();
        pool.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                flag = false;
                cabListItemFormatVector.clear();

                dataSnapshot.getChildrenCount();
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    cabListItemFormatVector.add(shot.getValue(CabListItemFormat.class));
                }
                int i = 0;
                while (i < cabListItemFormatVector.size() && !cabListItemFormatVector.get(i).getPhonenumber().equals(number)) {
                    i++;
                }
                if (i == cabListItemFormatVector.size()) {
                    //no number
                    join.setText("JOIN");
                } else {
                    //number
                    flag = true;
                    join.setText("LEAVE");
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


        adapter = new CabPeopleRVAdapter(this, cabListItemFormatVector);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        recyclerView.setAdapter(adapter);
        pool.keepSynced(true);

        join.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (email != null) {
                    if (numberFlag) {
                        if (flag) {
                            for (int i = 0; i < cabListItemFormatVector.size(); i++) {
                                Log.e("value i", String.valueOf(i));

                                if (cabListItemFormatVector.get(i).getPhonenumber().equals(number) &&
                                        cabListItemFormatVector.get(i).getName().equals(name)) {
                                    cabListItemFormatVector.remove(i);
                                    break;
                                }
                            }


                            pool.setValue(cabListItemFormatVector);
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(key);
                            Toast.makeText(getApplicationContext(), "Removed", Toast.LENGTH_SHORT).show();

                        } else {
                            if (name != null && number != null) {
                                cabListItemFormatVector.add(new CabListItemFormat(name, number, imageThumb, uid));
                                pool.setValue(cabListItemFormatVector);
                                FirebaseMessaging.getInstance().subscribeToTopic(key);
                                NotificationSender notification = new NotificationSender(key,number,null,null,null,null,"CabPool",false,true,getApplicationContext());
                                notification.execute();

                            } else {
                                Toast.makeText(getApplicationContext(), "Try later !", Toast.LENGTH_SHORT).show();
                            }

                        }

                    } else {
                        Snackbar snack = Snackbar.make(join, "Please add your contact to Infone before adding a pool.", Snackbar.LENGTH_LONG);
                        TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                        snackBarText.setTextColor(Color.WHITE);
                        snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                        snack.show();
                    }
/*
                } else {
                            showToast("Please enter your contact details to join");
                            startActivity(new Intent(CabListOfPeople.this, EditProfileActivity.class));
                        }*/
                } else {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(CabListOfPeople.this);
                    dialog.setNegativeButton("Lite", null)
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent loginIntent = new Intent(CabListOfPeople.this, LoginActivity.class);
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

    private void updateViews() {

        SharedPreferences sharedPref = this.getSharedPreferences("guestMode", MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);

        if (!status) {
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();

            listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    boolean userExists = false;
                    for (DataSnapshot childSnapshot :
                            dataSnapshot.getChildren()) {
                        if (childSnapshot.getKey().equals(user.getUid()) && childSnapshot.exists() &&
                                childSnapshot.getValue(Integer.class) != null) {
                            userExists = true;
                            int originalViews = childSnapshot.getValue(Integer.class);
                            mDatabaseViews.child(user.getUid()).setValue(originalViews + 1);

                            break;
                        } else {
                            userExists = false;
                        }
                    }
                    if (!userExists) {
                        mDatabaseViews.child(user.getUid()).setValue(1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            mDatabaseViews.addListenerForSingleValueEvent(listener);
        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseViews.removeEventListener(listener);
    }

}
