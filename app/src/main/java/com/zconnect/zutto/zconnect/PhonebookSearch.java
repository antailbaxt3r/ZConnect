package com.zconnect.zutto.zconnect;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.PhonebookDisplayItem;
import com.zconnect.zutto.zconnect.itemFormats.PhonebookItem;

import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhonebookSearch extends BaseActivity {
    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.rv_search_activity)
    android.support.v7.widget.RecyclerView recyclerView;
    @BindView(R.id.errorMessage)
    TextView errorMessage;
    private PhonebookAdapter searchAdapter;
    private DatabaseReference phonebookDbRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Phonebook");
    Query queryRef = phonebookDbRef.orderByChild("name");
    private Vector<PhonebookItem> searchContact = new Vector<>();
    private boolean guestMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestFeature() must be called before adding content
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_phonebook_search);
        ButterKnife.bind(this);
        guestMode = getSharedPreferences("guestMode", MODE_PRIVATE).getBoolean("mode", false);
//        CounterManager.InfoneSearchClick();
        Intent callerIntent = getIntent();

        phonebookDbRef.keepSynced(true);
        queryRef.keepSynced(true);
        searchAdapter = new PhonebookAdapter(searchContact, PhonebookSearch.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        recyclerView.setAdapter(searchAdapter);
        if (Intent.ACTION_SEARCH.equals(callerIntent.getAction())) {
            handleIntent(callerIntent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        final String query = intent.getStringExtra(SearchManager.QUERY);
        Log.v("Query obtained", query);
        searchQuery(query);
    }

    private void searchQuery(String string) {
        final String query = string.trim();
        if (!TextUtils.isEmpty(query)) {
            errorMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            queryRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    searchContact.clear();

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (guestMode && "S".equals(child.child("category").getValue(String.class))) continue;
                        // storage of EventFormat by individual de-serialization
                        PhonebookDisplayItem item = new PhonebookDisplayItem();
                        item.setCategory(child.child("category").getValue(String.class));
                        item.setDesc(child.child("desc").getValue(String.class));
                        item.setEmail(child.child("uid").getValue(String.class));
                        item.setImageurl(child.child("imageurl").getValue(String.class));
                        item.setName(child.child("name").getValue(String.class));
                        item.setNumber(child.getKey());
                        item.setSkills(child.child("skills").getValue(String.class));
                        item.setUid(child.child("Uid").getValue(String.class));

                        String name = child.child("name").getValue(String.class);
                        String number = child.getKey();
                        //    Log.v("mobileNumber",mobileNumber);
                        String details = child.child("desc").getValue(String.class);
                        String skills = child.child("skills").getValue(String.class);
                        String uid = child.child("Uid").getValue(String.class);

                        String imageurl = child.child("imageurl").getValue(String.class);
                        String title = name + number + details + skills;

                        //Type 1 and 2 check
                        PhonebookItem temp = new PhonebookItem(imageurl, name, number,uid, item);
                        if (title.toLowerCase().contains(query.toLowerCase())) {
                            searchContact.add(temp);
                            Log.v("name", temp.getName());
                            // Log.v("mobileNumber",temp.getNumber());
//                            Log.v("uid",temp.getUid());
                            // Log.v("",temp.getName());
                        } else {
                            String wordSplit[] = title.split("");
                            for (String word : wordSplit) {
                                if (word.toLowerCase().contains(query.toLowerCase())) {
                                    searchContact.add(temp);
                                }
                            }
                        }
                    }

                    // Need to add empty search result log message
                    if (searchContact.isEmpty()) {
                        errorMessage.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        recyclerView.setAdapter(searchAdapter);
                        searchAdapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled: ", databaseError.toException());
                }
            });
        } else {
            searchContact.clear();
            searchAdapter.notifyDataSetChanged();
            errorMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_phonebook_search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Search Infone...");
        searchItem.expandActionView();

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                PhonebookSearch.this.finish();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                //handle search
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra(SearchManager.QUERY, query);
                onNewIntent(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //handle search
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra(SearchManager.QUERY, newText);
                onNewIntent(intent);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
