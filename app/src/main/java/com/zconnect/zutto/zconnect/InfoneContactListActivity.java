package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.InfoneContactsRVItem;
import com.zconnect.zutto.zconnect.adapters.InfoneContactsRVAdpater;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;

public class InfoneContactListActivity extends AppCompatActivity {

    Toolbar toolbar;
    String catId;
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
    private String catName, catImageurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infone_contact_list);

        toolbar=(Toolbar) findViewById(R.id.toolbar_app_bar_infone);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbarSetup();

        catId = getIntent().getExtras().getString("catId");
        catName = getIntent().getExtras().getString("catName");
        catImageurl = getIntent().getExtras().getString("catImageurl");

        Log.e(TAG,"data: "+catId+" "+ catName);

        toolbar.setTitle(catName);


        communitySP = this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        databaseReferenceList = FirebaseDatabase.getInstance().getReference().child("communities")
                .child(communityReference).child("infone").child("categories").child(catId);

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
                        contactsRVItems, catId);
                recyclerViewContacts.setAdapter(infoneContactsRVAdpater);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(InfoneContactListActivity.class.getName(), "database error" + databaseError.toString());
            }
        };
        databaseReferenceList.addValueEventListener(listener);

        Log.e(TAG, "data :" + catId);
    }

    private void addContact() {

        Intent addContactIntent = new Intent(this, InfoneAddContactActivity.class);
        addContactIntent.putExtra("catId", catId);
        startActivity(addContactIntent);

    }

    private void editCategory() {

        Intent editCatIntent=new Intent(InfoneContactListActivity.this,InfoneAddCatActivity.class);
        editCatIntent.putExtra("catId",catId);
        editCatIntent.putExtra("catName",catName);
        editCatIntent.putExtra("catImageurl",catImageurl);
        startActivity(editCatIntent);

    }

    private void toolbarSetup() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_infone_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {

            editCategory();
        }
        return super.onOptionsItemSelected(item);
    }


}
