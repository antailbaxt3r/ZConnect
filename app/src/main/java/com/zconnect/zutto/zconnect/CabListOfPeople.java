package com.zconnect.zutto.zconnect;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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

import java.util.ArrayList;
import java.util.Vector;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class CabListOfPeople extends BaseActivity {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    DatabaseReference pool;
    Button join;
    String key;
    String name, number, email;
    Vector<CabListItemFormat> cabListItemFormatVector = new Vector<>();
    CabItemFormat cabItemFormat, cabItemFormat1;
    CabPeopleRVAdapter adapter;
    private FirebaseAuth mAuth;
    private Boolean flag = false, numberFlag = false;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Phonebook");
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Cab");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_list_of_people);
        setToolbar();
        setToolbarTitle("List of people");
        showBackButton();
        setSupportActionBar(getToolbar());
        showProgressDialog();

        try {
            key = getIntent().getStringExtra("key");
        } catch (Exception e) {
            finish();
        }
        flag=false;
        pool = databaseReference.child(key);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null)
            email = mAuth.getCurrentUser().getEmail();
        pool.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(VISIBLE);
                join.setVisibility(INVISIBLE);
                cabListItemFormatVector.clear();


                cabItemFormat = dataSnapshot.getValue(CabItemFormat.class);

                cabListItemFormatVector.addAll(cabItemFormat.getCabListItemFormats());
                adapter.notifyDataSetChanged();
                join.setVisibility(VISIBLE);
                progressBar.setVisibility(INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(INVISIBLE);
            }
        });




        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    PhonebookDisplayItem phonebookDisplayItem = shot.getValue(PhonebookDisplayItem.class);
                    if (email != null) {
                        if (phonebookDisplayItem.getEmail().equals(email)) {
                            name = phonebookDisplayItem.getName();
                            number = phonebookDisplayItem.getNumber();
                            numberFlag = true;
                        }
                    }

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (email != null) {
            databaseReference.child("Users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    flag = dataSnapshot.getChildrenCount() != 0;

                    hideProgressDialog();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    hideProgressDialog();
                }
            });
        } else
            hideProgressDialog();
        recyclerView = (RecyclerView) findViewById(R.id.content_cabpeople_rv);
        progressBar = (ProgressBar) findViewById(R.id.content_cabpeople_progress);
        join = (Button) findViewById(R.id.join);
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");
        join.setTypeface(customFont);
        adapter = new CabPeopleRVAdapter(this, cabListItemFormatVector);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        recyclerView.setAdapter(adapter);
        pool.keepSynced(true);

        pool.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cabItemFormat1 = dataSnapshot.getValue(CabItemFormat.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(INVISIBLE);
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email != null) {
                        if (numberFlag) {
                            if (flag) {
                                Snackbar snack = Snackbar.make(join, "Already Joined", Snackbar.LENGTH_LONG);
                                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                                snackBarText.setTextColor(Color.WHITE);
                                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                                snack.show();
                            } else {
                                if (name != null && number != null) {
                                    CabListItemFormat cabListItemFormat = new CabListItemFormat();
                                    cabListItemFormat.setName(name);
                                    cabListItemFormat.setPhonenumber(number);

                                    ArrayList<CabListItemFormat> cabListItemFormats;
                                    if (cabItemFormat1 != null) {
                                        cabListItemFormats = cabItemFormat1.getCabListItemFormats();
                                        cabListItemFormats.add(cabListItemFormat);
                                        cabItemFormat1.setCabListItemFormats(cabListItemFormats);
                                        databaseReference.child(cabItemFormat1.getKey()).setValue(cabItemFormat1);
                                        Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT).show();

                                        RemoteMessage.Builder creator = new RemoteMessage.Builder(key);
                                        creator.addData("Type", "CabPool");
                                        creator.addData("Person", name);
                                        creator.addData("Contact", number);
                                        creator.addData("Pool", key);
                                        FirebaseMessaging.getInstance().send(creator.build());
                                        FirebaseMessaging.getInstance().subscribeToTopic(key);
                                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Topics").push().setValue(key);

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Try later !", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Snackbar snack = Snackbar.make(join, "Please add your contact to Infone before adding a pool.", Snackbar.LENGTH_LONG);
                                    TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                                    snackBarText.setTextColor(Color.WHITE);
                                    snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                                    snack.show();
                                }
                            }

                        } else {
                            showToast("Please enter your contact details to join");
                            startActivity(new Intent(CabListOfPeople.this, AddContact.class));
                        }
                    } else {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(CabListOfPeople.this);
                    dialog.setNegativeButton("Lite", null)
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent loginIntent = new Intent(CabListOfPeople.this, logIn.class);
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


}
