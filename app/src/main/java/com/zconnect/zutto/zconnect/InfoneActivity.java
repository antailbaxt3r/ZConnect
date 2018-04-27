package com.zconnect.zutto.zconnect;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.Infone2CategoryModel;
import com.zconnect.zutto.zconnect.adapters.InfoneRVAdapter;

import java.util.ArrayList;

public class InfoneActivity extends BaseActivity {

    RecyclerView recyclerViewCat;
    FloatingActionButton fabCatAdd;
    ArrayList<Infone2CategoryModel> categoriesList = new ArrayList();
    DatabaseReference databaseReferenceCat;
    ValueEventListener listener;
    InfoneRVAdapter infoneRVAdapter;
    private static final int REQUEST_PHONE_CALL = 1;
    private SharedPreferences communitySP;
    public String communityReference;
    ProgressBar progressBar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infone2);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

        setActionBarTitle("Infone");

        progressBar = (ProgressBar) findViewById(R.id.infone2_progress_circle);
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewCat = (RecyclerView) findViewById(R.id.rv_cat_infone);
        recyclerViewCat.setVisibility(View.GONE);
        fabCatAdd = (FloatingActionButton) findViewById(R.id.fab_cat_infone);


        communitySP = this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        databaseReferenceCat = FirebaseDatabase.getInstance().getReference().child("communities")
                .child(communityReference).child("infone").child("categoriesInfo");

        recyclerViewCat.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                categoriesList = new ArrayList<>();
                for (DataSnapshot childSnapShot :
                        dataSnapshot.getChildren()) {
                    String name = childSnapShot.child("name").getValue(String.class);
                    String imageurl = childSnapShot.child("imageurl").getValue(String.class);
                    String admin = childSnapShot.child("admin").getValue(String.class);
                    String catId = childSnapShot.getKey();
                    Infone2CategoryModel infone2CategoryModel = new Infone2CategoryModel(name, imageurl, admin, catId);
                    categoriesList.add(infone2CategoryModel);
                    progressBar.setVisibility(View.GONE);
                    recyclerViewCat.setVisibility(View.VISIBLE);

                }

                infoneRVAdapter = new InfoneRVAdapter(categoriesList, InfoneActivity.this);
                recyclerViewCat.setAdapter(infoneRVAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(InfoneActivity.class.getName(), "database error" + databaseError.toString());
                progressBar.setVisibility(View.GONE);
                recyclerViewCat.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        };
        databaseReferenceCat.addValueEventListener(listener);

        fabCatAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("tt", "data fab");

                SharedPreferences sharedPref = InfoneActivity.this.getSharedPreferences("guestMode", MODE_PRIVATE);
                Boolean status = sharedPref.getBoolean("mode", false);

                if (!status) {
                    Intent addCatIntent = new Intent(InfoneActivity.this,InfoneAddCatActivity.class);
                    startActivity(addCatIntent);
                } else {
                    Toast.makeText(InfoneActivity.this, "Log in to use this function", Toast.LENGTH_SHORT).show();
                }
            }
        });


        askCallPermissions();

    }

//    private void addDialog() {
//
//        Log.e("tt", "data fab");
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
//        alertDialog.setTitle("Add new infone Category");
//        //alertDialog.setMessage("Category name");
//
//        final EditText newCategoryET = new EditText(this);
//        newCategoryET.setInputType(InputType.TYPE_CLASS_TEXT);
//        newCategoryET.setHint("Category name");
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        newCategoryET.setLayoutParams(lp);
//        alertDialog.setView(newCategoryET);
//        alertDialog.setIcon(R.drawable.ic_add_white_36dp);
//
//        alertDialog.setPositiveButton("YES",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        final String newCat = newCategoryET.getText().toString();
//
//                        if (!newCat.isEmpty()) {
//                            Toast.makeText(InfoneActivity.this, "Add a contact in your new category",
//                                    Toast.LENGTH_SHORT).show();
//                            Intent addContactIntent = new Intent(InfoneActivity.this,
//                                    InfoneAddContactActivity.class);
//                            addContactIntent.putExtra("categoryName", newCat);
//                            startActivity(addContactIntent);
//                        }
//                    }
//                });
//
//        alertDialog.show();
//
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReferenceCat.removeEventListener(listener);
    }

    private void askCallPermissions() {

        if (ContextCompat.checkSelfPermission(InfoneActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(InfoneActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        } else {

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //startActivity(intent);
                } else {
                    askCallPermissions();
                }
                return;
            }
        }
    }
}
