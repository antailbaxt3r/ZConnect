package com.zconnect.zutto.zconnect;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.itemFormats.InfoneContactsRVItem;
import com.zconnect.zutto.zconnect.adapters.InfoneContactsRVAdpater;
import com.zconnect.zutto.zconnect.addActivities.AddInfoneCat;
import com.zconnect.zutto.zconnect.addActivities.AddInfoneContact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    private String catAdmin;
    ProgressBar progressBar;

    int totalContacts;

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
        catAdmin = getIntent().getExtras().getString("catAdmin");

        Log.e(TAG,"data: "+catId+" "+ catName);

        toolbar.setTitle(catName);
        setTitle(catName);
        communitySP = this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        databaseReferenceList = FirebaseDatabase.getInstance().getReference().child("communities")
                .child(communityReference).child("infone").child("categories").child(catId);
        progressBar = (ProgressBar) findViewById(R.id.infone_contact_list_progress_circle);
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewContacts = (RecyclerView) findViewById(R.id.rv_infone_contacts);
        recyclerViewContacts.setVisibility(View.GONE);
        fabAddContact = (FloatingActionButton) findViewById(R.id.fab_contacts_infone);

        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        fabAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact();
            }
        });

        setAdapter("lite",false);

        Log.e(TAG, "data :" + catId);
    }

    private  void setAdapter(final String queryString, final Boolean search) {


        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                contactsRVItems = new ArrayList<>();
                for (DataSnapshot childSnapshot :
                        dataSnapshot.getChildren()) {

                    try {

                        String name = childSnapshot.child("name").getValue(String.class);
                        String imageThumb = childSnapshot.child("thumbnail").getValue(String.class);
                        String infoneUserId = childSnapshot.getKey();

                        Log.e("tt", "data " + name);
                        Log.e("tt", "data " + imageThumb);

                        phoneNumbs = new ArrayList<>();

                        for (DataSnapshot grandChildShot :
                                childSnapshot.child("phone").getChildren()) {
                            try {
                                phoneNumbs.add(grandChildShot.getValue(String.class));
                            } catch (Exception e) {
                            }

                            Log.e("tt", "data " + phoneNumbs.toString());
                        }


                        if (search){
                            if(name.toLowerCase().trim().contains(queryString.toLowerCase())){
                                infoneContactsRVItem = new InfoneContactsRVItem(name, "0", imageThumb, phoneNumbs, infoneUserId);
                                contactsRVItems.add(infoneContactsRVItem);
                                if(contactsRVItems.size()>7){
                                    break;
                                }
                            }


                        }else {
                            infoneContactsRVItem = new InfoneContactsRVItem(name, "0", imageThumb, phoneNumbs, infoneUserId);
                            contactsRVItems.add(infoneContactsRVItem);
                        }


                        totalContacts = contactsRVItems.size();
                    }catch (Exception e){}
                }

                if(!search || queryString.trim().equals("")){
                    Collections.sort(contactsRVItems, new Comparator<InfoneContactsRVItem>() {
                        @Override
                        public int compare(InfoneContactsRVItem contact1, InfoneContactsRVItem contact2) {
                            return contact1.getName().trim().compareToIgnoreCase(contact2.getName().trim());
                        }
                    });
                }


                infoneContactsRVAdpater = new InfoneContactsRVAdpater(InfoneContactListActivity.this,
                        contactsRVItems, catId);
                recyclerViewContacts.setAdapter(infoneContactsRVAdpater);
                progressBar.setVisibility(View.GONE);
                recyclerViewContacts.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(InfoneContactListActivity.class.getName(), "database error" + databaseError.toString());
                progressBar.setVisibility(View.GONE);
                recyclerViewContacts.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        };
        databaseReferenceList.addListenerForSingleValueEvent(listener);

    }

    private void addContact() {

        Intent addContactIntent = new Intent(this, AddInfoneContact.class);
        addContactIntent.putExtra("catId", catId);
        addContactIntent.putExtra("catImageURL",catImageurl);
        addContactIntent.putExtra("catName", catName);
        addContactIntent.putExtra("totalContacts",totalContacts);
        startActivity(addContactIntent);

    }

    private void editCategory() {

        Intent editCatIntent=new Intent(InfoneContactListActivity.this,AddInfoneCat.class);
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
        getMenuInflater().inflate(R.menu.menu_infone_contact_list, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setAdapter(query,true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setAdapter(newText,true);
                return false;
            }
        });


        MenuItem menuItem = menu.findItem(R.id.search);
        MenuItemCompat.setOnActionExpandListener(menuItem,new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                //Toast.makeText(InfoneContactListActivity.this, "Expanded", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                setAdapter("lite",false);
                //Toast.makeText(InfoneContactListActivity.this, "Collapsed", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        return true;

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {

            if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(catAdmin)) {
                editCategory();
            }
        }
        return super.onOptionsItemSelected(item);
    }



}
