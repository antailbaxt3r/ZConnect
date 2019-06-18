package com.zconnect.zutto.zconnect;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.adapters.MakeAdminRVAdapter;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;

import java.util.Vector;

public class MakeAdmin extends BaseActivity {
    private SharedPreferences communitySP;
    String oldestPostId;
    public String communityReference;
    MakeAdminRVAdapter makeAdminRVAdapter;
    ProgressBar progressBar;
    ProgressBar progressBar2;
    RecyclerView recyclerViewContacts;
    DatabaseReference databaseReference;
    Vector<String> name = new Vector<>();
    Vector<String> image = new Vector<>();
    Vector<String> uid = new Vector<>();
    Vector<String> sname = new Vector<>();
    Vector<String> simage = new Vector<>();
    Vector<String> suid = new Vector<>();
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_admin);
        toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_make_admin);
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
        toolbar.setTitle("Promote to Admin");
        setTitle("Promote to Admin");
        communitySP = this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);
        progressBar = (ProgressBar) findViewById(R.id.make_admin_list_progress_circle);
        progressBar.setVisibility(View.VISIBLE);
        progressBar2 = findViewById(R.id.progressBar2);
        recyclerViewContacts = (RecyclerView) findViewById(R.id.rv_make_admin);
        recyclerViewContacts.setVisibility(View.GONE);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1");
        databaseReference.limitToFirst(15).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
for (DataSnapshot childsnap:dataSnapshot.getChildren()){
    if(!("admin").equals(childsnap.child("userType").getValue())) {
        oldestPostId = childsnap.getKey();
        name.add(String.valueOf(childsnap.child("username").getValue()));
        if (!("").equals(childsnap.child("imageURL").getValue())) {
            image.add(String.valueOf(childsnap.child("imageURL").getValue()));
        } else {
            image.add("https://lh6.googleusercontent.com/-idc9bXb9n-Q/AAAAAAAAAAI/AAAAAAAAAAA/AAN31DVg6FhNzc1jkN4eBCa6ESbBPmpl5g/s96-c/photo.jpg");
        }
        uid.add(String.valueOf(childsnap.child("userUID").getValue()));
    }
    }
                recyclerViewContacts.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                recyclerViewContacts.setLayoutManager(new LinearLayoutManager(MakeAdmin.this));
               makeAdminRVAdapter = new MakeAdminRVAdapter(MakeAdmin.this,image,name,uid);
                recyclerViewContacts.setAdapter(makeAdminRVAdapter);

                recyclerViewContacts.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (!recyclerView.canScrollVertically(1)) {
                            progressBar.setVisibility(View.VISIBLE);
                            Log.d("i scrolled", "onScrolled: ");
                            databaseReference.orderByKey().startAt(oldestPostId).limitToFirst(15).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    name.removeElementAt(name.size()-1);
                                    image.removeElementAt(name.size()-1);
                                    for (DataSnapshot childsnap : dataSnapshot.getChildren()) {
                                        if (!("admin").equals(childsnap.child("userType").getValue())) {
                                            oldestPostId = childsnap.getKey();
                                            name.add(String.valueOf(childsnap.child("username").getValue()));
                                            if (!("").equals(childsnap.child("imageURL").getValue())) {
                                                image.add(String.valueOf(childsnap.child("imageURL").getValue()));
                                            } else {
                                                image.add("https://lh6.googleusercontent.com/-idc9bXb9n-Q/AAAAAAAAAAI/AAAAAAAAAAA/AAN31DVg6FhNzc1jkN4eBCa6ESbBPmpl5g/s96-c/photo.jpg");
                                            }
                                            uid.add(String.valueOf(childsnap.child("userUID").getValue()));
                                        }
                                    }
                                    Log.d("DATA CHANGED", "onDataChange: ");
                                   makeAdminRVAdapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e(MakeAdmin.class.getName(), "database error" + databaseError.toString());
                                    progressBar.setVisibility(View.GONE);
                                    recyclerViewContacts.setVisibility(View.VISIBLE);
                                    Toast.makeText(getApplicationContext(), "Failed to load data", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(MakeAdmin.class.getName(), "database error" + databaseError.toString());
                progressBar.setVisibility(View.GONE);
                recyclerViewContacts.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Failed to load data", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void toolbarSetup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
            int colorDarkPrimary = ContextCompat.getColor(this, R.color.colorPrimaryDark);
//            getWindow().setStatusBarColor(colorDarkPrimary);
//            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
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
                if(!query.equals("")) {
                    sname = new Vector<>();
                    simage = new Vector<>();
                    suid = new Vector<>();
                    for (int i = 0; i < name.size(); i++) {

                        if (name.get(i).toLowerCase().trim().contains(query.toLowerCase())) {
                            sname.add(name.get(i));
                            simage.add(image.get(i));
                            suid.add(uid.get(i));
                        }
                        if (sname.size() > 7) {
                            break;
                        }
                    }
                    makeAdminRVAdapter = new MakeAdminRVAdapter(MakeAdmin.this,simage,sname,suid);
                    recyclerViewContacts.setAdapter(makeAdminRVAdapter);
                    recyclerViewContacts.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                else{
                    makeAdminRVAdapter = new MakeAdminRVAdapter(MakeAdmin.this,image,name,uid);
                    recyclerViewContacts.setAdapter(makeAdminRVAdapter);}
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.equals("")) {
                    sname = new Vector<>();
                    simage = new Vector<>();
                    suid = new Vector<>();
                    for (int i = 0; i < name.size(); i++) {

                        if (name.get(i).toLowerCase().trim().contains(newText.toLowerCase())) {
                            sname.add(name.get(i));
                            simage.add(image.get(i));
                            suid.add(uid.get(i));
                        }
                        if (sname.size() > 7) {
                            break;
                        }
                    }

                    makeAdminRVAdapter = new MakeAdminRVAdapter(MakeAdmin.this,simage,sname,suid);
                    recyclerViewContacts.setAdapter(makeAdminRVAdapter);
                    progressBar.setVisibility(View.GONE);
                }
                else{
                makeAdminRVAdapter = new MakeAdminRVAdapter(MakeAdmin.this,image,name,uid);
                recyclerViewContacts.setAdapter(makeAdminRVAdapter);}
                return false;
            }
        });
return true;

    }


}
