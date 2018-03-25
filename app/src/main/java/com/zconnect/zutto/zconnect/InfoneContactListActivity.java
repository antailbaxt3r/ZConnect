package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.InfoneContactsRVItem;
import com.zconnect.zutto.zconnect.adapters.InfoneContactsRVAdpater;

import java.util.ArrayList;

public class InfoneContactListActivity extends AppCompatActivity {

    String categoryName;
    RecyclerView recyclerViewContacts;
    ArrayList<InfoneContactsRVItem> contactsRVItems;
    InfoneContactsRVAdpater infoneContactsRVAdpater;
    DatabaseReference databaseReferenceList;
    ValueEventListener listener;
    InfoneContactsRVItem infoneContactsRVItem;
    FloatingActionButton fabAddContact;
    private SharedPreferences communitySP;
    public String communityReference;
    ArrayList<String> phoneNumbs;
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infone_contact_list);

        categoryName = getIntent().getExtras().getString("category");


        communitySP = this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        databaseReferenceList = FirebaseDatabase.getInstance().getReference().child("communities")
                .child(communityReference).child("infone").child("categories").child(categoryName);

        recyclerViewContacts = (RecyclerView) findViewById(R.id.rv_infone_contacts);
        fabAddContact = (FloatingActionButton) findViewById(R.id.fab_contacts_infone);

        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        fabAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact();
            }
        });

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                contactsRVItems = new ArrayList<>();
                for (DataSnapshot childSnapshot :
                        dataSnapshot.getChildren()) {

                    String name = childSnapshot.child("name").getValue(String.class);
                    String imageThumb = childSnapshot.child("thumbnail").getValue(String.class);
                    String infoneUserId = childSnapshot.getKey();

                    Log.e("tt", "data " + name);
                    Log.e("tt", "data " + imageThumb);

                    phoneNumbs = new ArrayList<>();
                    for (DataSnapshot grandChildShot :
                            childSnapshot.child("phone").getChildren()) {
                        phoneNumbs.add(grandChildShot.getValue(String.class));
                        Log.e("tt", "data " + phoneNumbs.toString());
                    }

                    //Log.e("tt", "data"+phoneNumbs.toString());

                    infoneContactsRVItem = new InfoneContactsRVItem(name, "0", imageThumb, phoneNumbs, infoneUserId);
                    contactsRVItems.add(infoneContactsRVItem);
                }
                infoneContactsRVAdpater = new InfoneContactsRVAdpater(InfoneContactListActivity.this,
                        contactsRVItems);
                recyclerViewContacts.setAdapter(infoneContactsRVAdpater);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(InfoneContactListActivity.class.getName(), "database error" + databaseError.toString());
            }
        };
        databaseReferenceList.addValueEventListener(listener);

        Log.e(TAG, "data :" + categoryName);

    }

    private void addContact() {

        Intent addContactIntent = new Intent(this, InfoneAddContactActivity.class);
        addContactIntent.putExtra("categoryName", categoryName);
        startActivity(addContactIntent);

    }

    @Override
    protected void onStop() {
        super.onStop();
        //databaseReferenceList.removeEventListener(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReferenceList.removeEventListener(listener);
    }
}
