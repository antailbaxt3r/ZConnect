package com.zconnect.zutto.zconnect;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.CabItemFormat;
import com.zconnect.zutto.zconnect.adapters.CabPoolRVAdapter;
import com.zconnect.zutto.zconnect.addActivities.AddCabPool;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;


public class CabPoolAll extends BaseActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mUserStats, mFeaturesStats;
    String TotalEvents;

    RecyclerView recyclerView;
    CabPoolRVAdapter cabPoolRVAdapter;
    TreeMap<String, CabItemFormat> treeMap = new TreeMap<>();
    Vector<CabItemFormat> vector_fetched = new Vector<>();
    Vector<CabItemFormat> vector_final = new Vector<>();
    TextView error;
    String DT;
    View.OnClickListener onEmpty;
    ValueEventListener allPools;
    ProgressBar progressBar;

    private SharedPreferences communitySP;
    public String communityReference;


    String fetchedDate;
    Date fDate;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference, databaseReferenceCopy, databaseReferencePaste;


//    public CabPoolAll() {
//        // Required empty public constructor
//    }

//    public static CabPoolAll newInstance(String param1, String param2) {
//        CabPoolAll fragment = new CabPoolAll();
//        return fragment;
//    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        setHasOptionsMenu(true);
//
//        try {
//
//            int SDK_INT = android.os.Build.VERSION.SDK_INT;
//            if (SDK_INT > 8) {
//                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                        .permitAll().build();
//                StrictMode.setThreadPolicy(policy);
//                //your codes here
//                TrueTime.build().initialize();
//
//                Date dateTime = TrueTime.now();
//                java.sql.Timestamp timeStampDate = new Timestamp(dateTime.getTime());
//                Toast.makeText(getContext(), "This " + timeStampDate.toString(), Toast.LENGTH_SHORT).show();
//
//            }
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_cab_pool_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
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

        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_cab_pool_main, container, false);
        recyclerView = (RecyclerView) findViewById(R.id.pool_main_rv);
        progressBar = (ProgressBar) findViewById(R.id.fragment_cab_pool_main_progress_circle);
        error = (TextView) findViewById(R.id.message);
        cabPoolRVAdapter = new CabPoolRVAdapter(CabPoolAll.this, vector_final);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        recyclerView.setAdapter(cabPoolRVAdapter);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        communitySP = CabPoolAll.this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        databaseReference = firebaseDatabase.getReference().child("communities").child(communityReference).child("features").child("cabPool").child("allCabs");
//        databaseReferenceCopy = firebaseDatabase.getReference().child("communities").child(communityReference).child("features").child("cabPool").child("archives");
//        databaseReferencePaste = firebaseDatabase.getReference().child("communities").child(communityReference).child("features").child("cabPool").child("allCabs");

//        databaseReferenceCopy.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot shot : dataSnapshot.getChildren())
//                {
//                    copyPaste(databaseReferenceCopy.getRef().child(shot.getKey().toString()).child(shot.getKey().toString()), databaseReferencePaste.getRef().child(shot.getKey().toString()));
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        allPools = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vector_fetched.clear();
                vector_final.clear();

                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    try {
                        CabItemFormat cabItemFormatShot = shot.getValue(CabItemFormat.class);
                        if (!cabItemFormatShot.getDestination().equals(null) && !cabItemFormatShot.getSource().equals(null)) {
                            vector_fetched.add(shot.getValue(CabItemFormat.class));
                        }
                    } catch (Exception e) {
                    }
                }

                Calendar c = Calendar.getInstance();
                SimpleDateFormat input = new SimpleDateFormat("dd/M/yyyy");
                SimpleDateFormat output = new SimpleDateFormat("yyyyMMdd");
                DecimalFormat decimalFormat = new DecimalFormat("00");

                String date = output.format(c.getTime());

                for (int i = 0; i < vector_fetched.size(); i++) {

                    DT = vector_fetched.get(i).getDT();

                    if (date.compareTo(DT) <= 0) {
                        treeMap.put(DT, vector_fetched.get(i));
                    } else {
                        String key = vector_fetched.get(i).getKey();
                        FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home").child(key).removeValue();
                       //ArchivePool(firebaseDatabase.getReference().child("communities").child(communityReference).child("features").child("cabPool").child("allCabs").child(key), firebaseDatabase.getReference().child("communities").child(communityReference).child("features").child("cabPool").child("archives").child(key).child(key));
                    }
                }

                vector_final.addAll(treeMap.values());

                if (vector_final.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    error.setVisibility(View.VISIBLE);
                    error.setOnClickListener(onEmpty);

                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    error.setVisibility(View.GONE);
                    recyclerView.setAdapter(cabPoolRVAdapter);
                    cabPoolRVAdapter.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        };

        onEmpty = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CabPoolAll.this, AddCabPool.class);
                startActivity(intent);
            }
        };

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("guestMode", MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);

        if (!status) {
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();

            mUserStats = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(user.getUid()).child("Stats");
            mFeaturesStats = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Stats");
            mFeaturesStats.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        TotalEvents = dataSnapshot.child("TotalCabpools").getValue().toString();
                        DatabaseReference newPost = mUserStats;
                        Map<String, Object> taskMap = new HashMap<>();
                        taskMap.put("TotalCabpools", TotalEvents);
                        newPost.updateChildren(taskMap);
                    } catch (Exception e) {
                        Log.d("Error Alert: ", e.getMessage());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);
        if (!status) {
            getMenuInflater().inflate(R.menu.menu_cabpool_all, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_storeroom) {
            startActivity(new Intent(getApplicationContext(), CabPoolLocations.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void copyPaste(final DatabaseReference copyRef, final DatabaseReference pasteRef) {
        copyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pasteRef.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            System.out.println("Copy failed");
                        } else {
                            System.out.println("Success");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void ArchivePool(final DatabaseReference fromPath, final DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            System.out.println("Copy failed");


                        } else {
                            System.out.println("Success");
                            fromPath.setValue(null);
                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        databaseReference.addValueEventListener(allPools);
    }

    @Override
    public void onPause() {
        super.onPause();
        databaseReference.removeEventListener(allPools);
    }
}