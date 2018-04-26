package com.zconnect.zutto.zconnect;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.CabItemFormat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CabPoolAll#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CabPoolAll extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mUserStats, mFeaturesStats;
    String TotalEvents;

    // TODO: Rename and change types of parameters
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
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;


    public CabPoolAll() {
        // Required empty public constructor
    }

    public static CabPoolAll newInstance(String param1, String param2) {
        CabPoolAll fragment = new CabPoolAll();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the Menu; this adds items to the action bar if it is present.
        SharedPreferences sharedPref = getContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);
        if (!status){
            inflater.inflate(R.menu.menu_cabpool_all, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_storeroom) {
            startActivity(new Intent(getContext(), CabPoolLocations.class));
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_cab_pool_main, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.pool_main_rv);
        progressBar = (ProgressBar) view.findViewById(R.id.fragment_cab_pool_main_progress_circle);
        error = (TextView) view.findViewById(R.id.message);
        cabPoolRVAdapter = new CabPoolRVAdapter(getActivity(), vector_final);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.setAdapter(cabPoolRVAdapter);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        communitySP = getActivity().getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        databaseReference = firebaseDatabase.getReference().child("communities").child(communityReference).child("features").child("cabPool").child("allCabs");

        allPools= new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vector_fetched.clear();
                vector_final.clear();

                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    try {
                        vector_fetched.add(shot.getValue(CabItemFormat.class));
                    }catch (Exception e){}
                }

                Calendar c = Calendar.getInstance();
                SimpleDateFormat input = new SimpleDateFormat("dd/M/yyyy");
                SimpleDateFormat output = new SimpleDateFormat("yyyyMMdd");
                DecimalFormat decimalFormat = new DecimalFormat("00");

                String date = output.format(c.getTime());
//                Log.e("ABC", date);
//                Log.e("RV", "SIZE:"+String.valueOf(vector_fetched.size()));

                for (int i = 0; i < vector_fetched.size(); i++) {
                    Log.e("RV", "value of i:"+String.valueOf(i));
//                    try {
//
//                        fetchedDate = vector_fetched.get(i).getDate();
//                        Log.e("RV", "FETCHED DATE:" + fetchedDate);
//
//                    }catch (Exception e){
//
//                    }

//                    //check if DT is there or not
//                    //if not, then will add DT to it.
//                    if (vector_fetched.get(i).getDT() != null) {
                        DT = vector_fetched.get(i).getDT();
                        Log.e("RV", "value of DT:"+String.valueOf(DT));
//                    } else {
//
//                        //getting fetched date to required format
//                        try {
//
//                            fDate = input.parse(fetchedDate);
//
//                        } catch (ParseException e) {
//
//                            System.err.println("Could not parse date: " + fetchedDate);
//
//                        }
//
//
//                        String date1 = output.format(fDate);
//
//                        double T1 = Integer.valueOf((vector_fetched.get(i).getTime()).substring(0, 2));
//                        double T2 = Integer.valueOf((vector_fetched.get(i).getTime()).substring(9, 11));
//                        double Av = (T1 + T2) / 2;
//                        String time;
//
//                        if (Av == ((int) Av)) {
//                            time = decimalFormat.format((int)Av + 00) + ":00";
//                        } else {
//                            time = (decimalFormat.format((int) Av + 00) + ":30");
//                        }
//
//
//                        DT = date1 + " " + time;
//                        DatabaseReference newPost2 = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Cab").child(vector_fetched.get(i).getKey());
//                        newPost2.child("DT").setValue(DT);
//                        Log.e("ABC", DT);
//                        vector_fetched.get(i).setDT(DT);
//
//                    }
                    if (date.compareTo(DT) <= 0) {
                        treeMap.put(DT, vector_fetched.get(i));
                    } else {
                        String key = vector_fetched.get(i).getKey();
                        FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home").child(key).removeValue();
                        ArchivePool(firebaseDatabase.getReference().child("communities").child(communityReference).child("Cab").child(key), firebaseDatabase.getReference().child("communities").child(communityReference).child("archive/Cab").child(key));
                    }
                }

                vector_final.addAll(treeMap.values());
                Log.e("ABC1", String.valueOf(vector_final.size()));
                //
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
                Intent intent = new Intent(getActivity(), AddCabPool.class);
                startActivity(intent);
            }
        };

        SharedPreferences sharedPref = getContext().getSharedPreferences("guestMode", MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);

        if (!status) {
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();

            mUserStats = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users").child(user.getUid()).child("Stats");
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
        return view;
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
