package com.zconnect.zutto.zconnect;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.ShopDetailsItem;
import com.zconnect.zutto.zconnect.ItemFormats.ShopListItem;

import java.util.Vector;

public class ShopSearch extends AppCompatActivity {
    private android.support.v7.widget.RecyclerView searchShopList;
    private ShopListRV searchAdapter;
    private Toolbar toolbar;
    private TextView errorMessage;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Shop").child("Shops");

    private Vector<ShopListItem> searchShop = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        Log.v("tag", "s");
        setContentView(R.layout.activity_shop_search);

        errorMessage = (TextView) findViewById(R.id.errorMessage);

        CounterManager.shopSearch();
        Intent intent = getIntent();


        searchShopList = (android.support.v7.widget.RecyclerView) findViewById(R.id.searchActivityRecyclerView);
        databaseReference.keepSynced(true);
        searchAdapter = new ShopListRV(ShopSearch.this, searchShop);

        searchShopList.setHasFixedSize(true);
        searchShopList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        //Setup layout manager. VERY IMP ALWAYS
        searchShopList.setAdapter(searchAdapter);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {


            handleIntent(intent);
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


        if (query != "") {

            errorMessage.setVisibility(View.INVISIBLE);
            searchShopList.setVisibility(View.VISIBLE);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    searchShop.clear();

                    for (DataSnapshot childShot : dataSnapshot.getChildren()) {

                        //need to add a try catch block before release

                        // storage of EventFormat by individual de-serialization

                        ShopDetailsItem foo = new ShopDetailsItem();

                        foo.setCat(childShot.child("cat").getValue(String.class));
                        foo.setLat(childShot.child("lat").getValue(String.class));
                        foo.setLon(childShot.child("lon").getValue(String.class));
                        foo.setImageurl(childShot.child("imageurl").getValue(String.class));
                        foo.setName(childShot.child("name").getValue(String.class));
                        foo.setNumber(childShot.child("number").getValue(String.class));
                        foo.setMenuurl(childShot.child("menuurl").getValue(String.class));
                        foo.setDetails(childShot.child("details").getValue(String.class));
                        foo.setShopid(childShot.child("shopid").getValue(String.class));

                        String title = childShot.child("name").getValue(String.class);


                        String name = childShot.child("name").getValue(String.class);
                        String number = childShot.child("number").getValue(String.class);

                        String imageurl = childShot.child("imageurl").getValue(String.class);

                        String typeTemp = "";

                        //Type 1 and 2 check

                        if (title.toLowerCase().contains(query.toLowerCase())) {


                            ShopListItem temp = new ShopListItem(imageurl, name, foo);
                            searchShop.add(temp);

                        } else {
                            String wordSplit[] = title.split(" ");
                            int l = wordSplit.length;
                            for (int i = 0; i < l; i++) {

                                String t = wordSplit[i].toLowerCase();

                                String m = query.toLowerCase();
                                if (t.contains(m)) {

                                    ShopListItem temp = new ShopListItem(imageurl, name, foo);
                                    searchShop.add(temp);
                                }
                            }
                        }


                    }

                    // Need to add empty search result log message
                    if (searchShop.isEmpty()) {
                        errorMessage.setVisibility(View.VISIBLE);
                        searchShopList.setVisibility(View.INVISIBLE);

                    } else {

                        searchShopList.setAdapter(searchAdapter);
                        searchAdapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            searchShop.clear();
            searchAdapter.notifyDataSetChanged();
            errorMessage.setVisibility(View.VISIBLE);
            searchShopList.setVisibility(View.INVISIBLE);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_phonebook_search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setQueryHint("Search Shop...");
        searchItem.expandActionView();

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                ShopSearch.this.finish();
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
