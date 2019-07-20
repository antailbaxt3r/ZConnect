package com.zconnect.zutto.zconnect;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.adapters.InfoneContactsRVAdapter;
import com.zconnect.zutto.zconnect.addActivities.CreateForum;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.itemFormats.ChatItemFormats;
import com.zconnect.zutto.zconnect.itemFormats.InfoneContactsRVItem;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.utilities.ForumsUserTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.MessageTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AddMembersToForumActivity extends BaseActivity {

    ValueEventListener listener;
    ArrayList<InfoneContactsRVItem> contactsRVItems = new ArrayList<>();
    ArrayList<String> phoneNumbs;
    int totalContacts;
    AllUsersRVAdapter allUsersRVAdapter;
    RecyclerView recyclerViewContacts;
    ArrayList<String> addedUserIds = new ArrayList<>();
    SearchView searchView;
    Toolbar toolbar;
    TextView members;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members_to_forum);
        recyclerViewContacts = findViewById(R.id.members_rv);
        recyclerViewContacts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        toolbar = findViewById(R.id.toolbar_add_members);
        setSupportActionBar(toolbar);
        setActionBarTitle("Add Users");
        members = findViewById(R.id.add_members_tv);
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    InfoneContactsRVItem temp = new InfoneContactsRVItem();

                    String name = childSnapshot.child("username").getValue(String.class);
                    String imageThumb = childSnapshot.child("imageURLThumbnail").getValue(String.class);
                    String infoneUserId = childSnapshot.getKey();
                    String desc = childSnapshot.child("desc").getValue(String.class);
                    temp.setName(name);
                    temp.setImageThumb(imageThumb);
                    temp.setInfoneUserId(infoneUserId);
                    temp.setDesc(desc);


                    Log.e("tt", "data " + temp.getName());
                    Log.e("tt", "data " + imageThumb);

                    Boolean contactHidden = false;
                    if(infoneUserId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        continue;
                    }
                    if(name != null){
                        contactsRVItems.add(temp);

                    }


                    totalContacts = contactsRVItems.size();
                    Log.d("Try: ",Integer.toString(totalContacts));

                }
                Log.d("Length",Integer.toString(contactsRVItems.size()));
                for(InfoneContactsRVItem a: contactsRVItems)
                {
                    if(a.getName()!=null){
                        Log.d("TT",a.getName());
                    }
                    else{
                        Log.d("TT","null");
                    }

                }


                Collections.sort(contactsRVItems, new Comparator<InfoneContactsRVItem>() {
                    @Override
                    public int compare(InfoneContactsRVItem contact1, InfoneContactsRVItem contact2) {
                        Log.d("Tag",contact1.getName());
                        return contact1.getName().compareToIgnoreCase(contact2.getName());
                    }
                });


                allUsersRVAdapter = new AllUsersRVAdapter(contactsRVItems, new AllUsersRVAdapter.OnItemCheckListener() {
                    @Override
                    public boolean onItemCheck(InfoneContactsRVItem item) {
                        Log.d("Hoi",item.getInfoneUserId());
                        if(addedUserIds.size() <20){

                            addedUserIds.add(item.getInfoneUserId());
                            Log.d("Hoi",addedUserIds.toString());
                            members.setText(20-addedUserIds.size()+" Remaining");
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public boolean onItemUncheck(InfoneContactsRVItem item) {
                        Log.d("Hoi",item.getInfoneUserId());
                        if(addedUserIds.size()>0){
                            Log.d("Hoi",item.getInfoneUserId());
                            addedUserIds.remove(item.getInfoneUserId());
                            members.setText(20-addedUserIds.size()+" Remaining");
                            return true;
                        }
                        return false;
                    }
                });

                recyclerViewContacts.setAdapter(allUsersRVAdapter);
//                progressBar.setVisibility(View.GONE);
                recyclerViewContacts.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(InfoneContactListActivity.class.getName(), "database error" + databaseError.toString());
//                progressBar.setVisibility(View.GONE);
                recyclerViewContacts.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        };
        FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").addListenerForSingleValueEvent(listener);



    }
    void addMembers(String UID){
        final DatabaseReference databaseReferenceTabsCategories = FirebaseDatabase.getInstance().getReferenceFromUrl(getIntent().getStringExtra("refForum"));
        DatabaseReference mPostedByDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(UID);

        mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserItemFormat userItem = dataSnapshot.getValue(UserItemFormat.class);

                UsersListItemFormat userDetails = new UsersListItemFormat();

                userDetails.setImageThumb(userItem.getImageURLThumbnail());
                userDetails.setName(userItem.getUsername());
                userDetails.setPhonenumber(userItem.getMobileNumber());
                userDetails.setUserUID(userItem.getUserUID());
                userDetails.setUserType(ForumsUserTypeUtilities.KEY_USER);
                databaseReferenceTabsCategories.child("users").child(userItem.getUserUID()).setValue(userDetails);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_users_forum, menu);
        MenuItem done = menu.findItem(R.id.done);
        done.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                for(String uid: addedUserIds){
                    addMembers(uid);
                }
//                Log.d("Stuff",intent.getStringExtra("refChat"))
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("ref", getIntent().getStringExtra("refChat"));
                intent.putExtra("type", getIntent().getStringExtra("type"));
                intent.putExtra("name", getIntent().getStringExtra("name"));
                intent.putExtra("tab", getIntent().getStringExtra("tab"));
                intent.putExtra("key", getIntent().getStringExtra("key"));
                startActivity(intent);
                finish();
                return true;
            }
        });


        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setDrawingCacheBackgroundColor(getResources().getColor(R.color.black));

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                allUsersRVAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                allUsersRVAdapter.getFilter().filter(query);
                return false;
            }
        });


        return true;


    }

}
