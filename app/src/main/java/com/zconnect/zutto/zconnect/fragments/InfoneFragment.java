package com.zconnect.zutto.zconnect.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
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
import android.widget.Toast;


import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.OnSingleClickListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.addActivities.RequestInfoneCat;
import com.zconnect.zutto.zconnect.itemFormats.InfoneCategoryModel;
import com.zconnect.zutto.zconnect.adapters.InfoneCategoriesRVAdapter;
import com.zconnect.zutto.zconnect.addActivities.AddInfoneCat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.Context.MODE_PRIVATE;
import static com.zconnect.zutto.zconnect.R.layout.fragment_infone;

public class InfoneFragment extends Fragment {

    RecyclerView recyclerViewCat;
    FloatingActionButton fabCatAdd;
    ArrayList<InfoneCategoryModel> categoriesList = new ArrayList();
    DatabaseReference databaseReferenceCat, mUserDetails;
    ValueEventListener listener;
    InfoneCategoriesRVAdapter infoneCategoriesRVAdapter;
    private static final int REQUEST_PHONE_CALL = 1;
    private static final int REQUEST_READ_CONTACTS = 2;
    private SharedPreferences communitySP;
    public String communityReference;
    private ShimmerFrameLayout shimmerFrameLayout;
    Toolbar toolbar;
    public boolean isAdmin = false;

    public InfoneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(fragment_infone, container, false);
        recyclerViewCat = (RecyclerView) view.findViewById(R.id.rv_cat_infone);
        recyclerViewCat.setVisibility(View.GONE);
        fabCatAdd = getActivity().findViewById(R.id.fab_cat_infone);
//        fabCatAdd.setVisibility(View.VISIBLE);
        shimmerFrameLayout = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container_infone_cat);
        communitySP = getActivity().getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);
        shimmerFrameLayout.startShimmerAnimation();

        databaseReferenceCat = FirebaseDatabase.getInstance().getReference().child("communities")
                .child(communityReference).child("infone").child("categoriesInfo");
        mUserDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        recyclerViewCat.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

//                if(isAdmin){
//                    fabCatAdd.setVisibility(View.VISIBLE);
//
//                }
//                else{
//                    fabCatAdd.setVisibility(View.GONE);
//
//                }

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

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            shimmerFrameLayout.stopShimmerAnimation();
                            shimmerFrameLayout.setVisibility(View.INVISIBLE);
                            recyclerViewCat.setVisibility(View.VISIBLE);
                        }
                    }, 500);

                }

                Collections.sort(categoriesList, new Comparator<InfoneCategoryModel>() {
                    @Override
                    public int compare(InfoneCategoryModel cat1, InfoneCategoryModel cat2) {

                        if(cat1.getName()!=null && cat2.getName()!=null)
                            return cat1.getName().trim().compareToIgnoreCase(cat2.getName().trim());
                        else
                            return 0;
                    }
                });
                if(categoriesList.isEmpty()){

                }else{

                }
                infoneCategoriesRVAdapter = new InfoneCategoriesRVAdapter(categoriesList, getContext());
                recyclerViewCat.setAdapter(infoneCategoriesRVAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(InfoneFragment.class.getName(), "database error" + databaseError.toString());
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.INVISIBLE);
                recyclerViewCat.setVisibility(View.VISIBLE);

            }
        };


        databaseReferenceCat.addValueEventListener(listener);

        fabCatAdd.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                Log.e("tt", "data fab");

                SharedPreferences sharedPref = getActivity().getSharedPreferences("guestMode", MODE_PRIVATE);
                Boolean status = sharedPref.getBoolean("mode", false);

                if (!status) {
                    Intent addCatIntent = new Intent(getContext(),AddInfoneCat.class);
                    Intent reqCatIntent = new Intent(getContext(),RequestInfoneCat.class);

                    mUserDetails.child("userType").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue().toString().equalsIgnoreCase("admin"))
                                startActivity(addCatIntent);
                            else
                                startActivity(reqCatIntent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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
//                infoneCategoriesRVAdapter = new InfoneCategoriesRVAdapter(categoriesList, InfoneFragment.this);
//                recyclerViewCat.setAdapter(infoneCategoriesRVAdapter);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e(InfoneFragment.class.getName(), "database error" + databaseError.toString());
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
//                SharedPreferences sharedPref = InfoneFragment.this.getSharedPreferences("guestMode", MODE_PRIVATE);
//                Boolean status = sharedPref.getBoolean("mode", false);
//
//                if (!status) {
//                    Intent addCatIntent = new Intent(InfoneFragment.this,AddInfoneCat.class);
//                    startActivity(addCatIntent);
//                } else {
//                    Toast.makeText(InfoneFragment.this, "Log in to use this function", Toast.LENGTH_SHORT).show();
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
//                            Toast.makeText(InfoneFragment.this, "Add a contact in your new category",
//                                    Toast.LENGTH_SHORT).show();
//                            Intent addContactIntent = new Intent(InfoneFragment.this,
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
//                            Toast.makeText(InfoneFragment.this, "Add a contact in your new category",
//                                    Toast.LENGTH_SHORT).show();
//                            Intent addContactIntent = new Intent(InfoneFragment.this,
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
        }

        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS}, REQUEST_READ_CONTACTS);
        }
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_CONTACTS);
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
            case REQUEST_READ_CONTACTS: {
                if(grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    askCallPermissions();
                }
                return;
            }
        }
    }
}
