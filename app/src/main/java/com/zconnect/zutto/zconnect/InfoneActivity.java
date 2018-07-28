package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.InfoneCategoryModel;
import com.zconnect.zutto.zconnect.adapters.InfoneCategoriesRVAdapter;
import com.zconnect.zutto.zconnect.addActivities.AddInfoneCat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.Context.MODE_PRIVATE;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class InfoneActivity extends Fragment {

    RecyclerView recyclerViewCat;
    FloatingActionButton fabCatAdd;
    ArrayList<InfoneCategoryModel> categoriesList = new ArrayList();
    DatabaseReference databaseReferenceCat, mUserDetails;
    ValueEventListener listener;
    InfoneCategoriesRVAdapter infoneCategoriesRVAdapter;
    private static final int REQUEST_PHONE_CALL = 1;
    private SharedPreferences communitySP;
    public String communityReference;
    ProgressBar progressBar;
    Toolbar toolbar;

    public InfoneActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_infone2, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.infone2_progress_circle);
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewCat = (RecyclerView) view.findViewById(R.id.rv_cat_infone);
        recyclerViewCat.setVisibility(View.GONE);
        fabCatAdd = (FloatingActionButton) view.findViewById(R.id.fab_cat_infone);
        CounterManager.infoneOpen();

        communitySP = getActivity().getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        databaseReferenceCat = FirebaseDatabase.getInstance().getReference().child("communities")
                .child(communityReference).child("infone").child("categoriesInfo");
        mUserDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        recyclerViewCat.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                categoriesList = new ArrayList<>();
                for (DataSnapshot childSnapShot :
                        dataSnapshot.getChildren()) {
                    try {
                        String name = childSnapShot.child("name").getValue(String.class);
                        String imageurl = childSnapShot.child("imageurl").getValue(String.class);
                        String admin = childSnapShot.child("admin").getValue(String.class);
                        String catId = childSnapShot.getKey();
                        int totalContacts = childSnapShot.child("totalContacts").getValue(Integer.class);
                        String thumbImageurl = childSnapShot.child("thumbnail").getValue(String.class);
                        InfoneCategoryModel infoneCategoryModel = new InfoneCategoryModel(name, imageurl, admin, catId,thumbImageurl,totalContacts);
                        categoriesList.add(infoneCategoryModel);
                    }catch (Exception e){}

                    progressBar.setVisibility(View.GONE);
                    recyclerViewCat.setVisibility(View.VISIBLE);

                }

                Collections.sort(categoriesList, new Comparator<InfoneCategoryModel>() {
                    @Override
                    public int compare(InfoneCategoryModel cat1, InfoneCategoryModel cat2) {
                        return cat1.getName().trim().compareToIgnoreCase(cat2.getName().trim());
                    }
                });

                infoneCategoriesRVAdapter = new InfoneCategoriesRVAdapter(categoriesList, getContext());
                recyclerViewCat.setAdapter(infoneCategoriesRVAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(InfoneActivity.class.getName(), "database error" + databaseError.toString());
                progressBar.setVisibility(View.GONE);
                recyclerViewCat.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity().getApplicationContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        };
        databaseReferenceCat.addValueEventListener(listener);

        mUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);
                if(userItemFormat.getUserType().equals(UsersTypeUtilities.KEY_ADMIN)){
                    fabCatAdd.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        fabCatAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("tt", "data fab");

                SharedPreferences sharedPref = getActivity().getSharedPreferences("guestMode", MODE_PRIVATE);
                Boolean status = sharedPref.getBoolean("mode", false);

                if (!status) {
                    Intent addCatIntent = new Intent(getContext(),AddInfoneCat.class);
                    startActivity(addCatIntent);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Log in to use this function", Toast.LENGTH_SHORT).show();
                }
            }
        });


        askCallPermissions();
        return view;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_infone2);
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//
//        if (toolbar != null) {
//            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    onBackPressed();
//                }
//            });
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
//            int colorDarkPrimary = ContextCompat.getColor(this, R.color.colorPrimaryDark);
//            getWindow().setStatusBarColor(colorDarkPrimary);
//            getWindow().setNavigationBarColor(colorPrimary);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        }
//
//
//        setActionBarTitle("Infone");

//        progressBar = (ProgressBar) findViewById(R.id.infone2_progress_circle);
//        progressBar.setVisibility(View.VISIBLE);
//        recyclerViewCat = (RecyclerView) findViewById(R.id.rv_cat_infone);
//        recyclerViewCat.setVisibility(View.GONE);
//        fabCatAdd = (FloatingActionButton) findViewById(R.id.fab_cat_infone);
//
//
//        communitySP = this.getSharedPreferences("communityName", MODE_PRIVATE);
//        communityReference = communitySP.getString("communityReference", null);
//
//        databaseReferenceCat = FirebaseDatabase.getInstance().getReference().child("communities")
//                .child(communityReference).child("infone").child("categoriesInfo");
//
//        recyclerViewCat.setLayoutManager(new LinearLayoutManager(this,
//                LinearLayoutManager.VERTICAL, false));
//
//        listener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                categoriesList = new ArrayList<>();
//                for (DataSnapshot childSnapShot :
//                        dataSnapshot.getChildren()) {
//                    try {
//                        String name = childSnapShot.child("name").getValue(String.class);
//                        String imageurl = childSnapShot.child("imageurl").getValue(String.class);
//                        String admin = childSnapShot.child("admin").getValue(String.class);
//                        String catId = childSnapShot.getKey();
//                        int totalContacts = childSnapShot.child("totalContacts").getValue(Integer.class);
//                        String thumbImageurl = childSnapShot.child("thumbnail").getValue(String.class);
//                        InfoneCategoryModel infoneCategoryModel = new InfoneCategoryModel(name, imageurl, admin, catId,thumbImageurl,totalContacts);
//                        categoriesList.add(infoneCategoryModel);
//                    }catch (Exception e){}
//
//                    progressBar.setVisibility(View.GONE);
//                    recyclerViewCat.setVisibility(View.VISIBLE);
//
//                }
//
//                Collections.sort(categoriesList, new Comparator<InfoneCategoryModel>() {
//                    @Override
//                    public int compare(InfoneCategoryModel cat1, InfoneCategoryModel cat2) {
//                        return cat1.getName().trim().compareToIgnoreCase(cat2.getName().trim());
//                    }
//                });
//
//                infoneCategoriesRVAdapter = new InfoneCategoriesRVAdapter(categoriesList, InfoneActivity.this);
//                recyclerViewCat.setAdapter(infoneCategoriesRVAdapter);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e(InfoneActivity.class.getName(), "database error" + databaseError.toString());
//                progressBar.setVisibility(View.GONE);
//                recyclerViewCat.setVisibility(View.VISIBLE);
//                Toast.makeText(getApplicationContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
//            }
//        };
//        databaseReferenceCat.addValueEventListener(listener);
//
//        fabCatAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Log.e("tt", "data fab");
//
//                SharedPreferences sharedPref = InfoneActivity.this.getSharedPreferences("guestMode", MODE_PRIVATE);
//                Boolean status = sharedPref.getBoolean("mode", false);
//
//                if (!status) {
//                    Intent addCatIntent = new Intent(InfoneActivity.this,AddInfoneCat.class);
//                    startActivity(addCatIntent);
//                } else {
//                    Toast.makeText(InfoneActivity.this, "Log in to use this function", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//
//        askCallPermissions();

//    }

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
//                                    AddInfoneContact.class);
//                            addContactIntent.putExtra("categoryName", newCat);
//                            startActivity(addContactIntent);
//                        }
//                    }
//                });
//
//        alertDialog.show();
//
//    }

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
//                                    AddInfoneContact.class);
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
//    protected void onDestroy() {
    public void onDestroy() {
        super.onDestroy();
        databaseReferenceCat.removeEventListener(listener);
    }

    private void askCallPermissions() {

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
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
