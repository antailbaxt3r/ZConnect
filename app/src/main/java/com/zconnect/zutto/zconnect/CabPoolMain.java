package com.zconnect.zutto.zconnect;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.CabItemFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CabPoolMain#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CabPoolMain extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    RecyclerView recyclerView;
    CabPoolRVAdapter cabPoolRVAdapter;
    TreeMap<String,CabItemFormat> treeMap=new TreeMap<>();
    Vector<CabItemFormat> vector_fetched=new Vector<>();
    Vector<CabItemFormat>  vector_final=new Vector<>();
    TextView error;
    String DT;
    View.OnClickListener onEmpty;


    String fetchedDate;
    Date fDate;
    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    DatabaseReference databaseReference=firebaseDatabase.getReference().child("Cab");



    public CabPoolMain() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CabPoolMain.
     */
    // TODO: Rename and change types and number of parameters
    public static CabPoolMain newInstance(String param1, String param2) {
        CabPoolMain fragment = new CabPoolMain();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_cab_pool_main, container, false);
        recyclerView=(RecyclerView) view.findViewById(R.id.pool_main_rv);
            error=(TextView)view.findViewById(R.id.message);
        cabPoolRVAdapter=new CabPoolRVAdapter(getActivity(),vector_final);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
        recyclerView.setAdapter(cabPoolRVAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vector_fetched.clear();
                vector_final.clear();
                Log.e("ABC", String.valueOf(dataSnapshot.getChildrenCount()));

                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    vector_fetched.add(shot.getValue(CabItemFormat.class));

                }

                Calendar c = Calendar.getInstance();
                SimpleDateFormat input = new SimpleDateFormat("dd/M/yyyy");
                SimpleDateFormat output = new SimpleDateFormat("yyyyMMdd");
                DecimalFormat decimalFormat = new DecimalFormat("00");

                String date = output.format(c.getTime());
                Log.e("ABC", date);

                for (int i = 0; i < vector_fetched.size(); i++) {


                    fetchedDate = vector_fetched.get(i).getDate();


                    //check if DT is there or not
                    //if not, then will add DT to it.
                    if (vector_fetched.get(i).getDT() != null) {

                        DT = vector_fetched.get(i).getDT();

                    } else {

                        //getting fetched date to required format
                        try {

                            Log.e("ABC1", fetchedDate);
                            fDate = input.parse(fetchedDate);

                        } catch (ParseException e) {

                            System.err.println("Could not parse date: " + fetchedDate);

                        }


                        String date1 = output.format(fDate);
                        Log.e("ABC", date1);


                        double T1 = Integer.valueOf((vector_fetched.get(i).getTime()).substring(0, 2));
                        double T2 = Integer.valueOf((vector_fetched.get(i).getTime()).substring(9, 11));
                        double Av = (T1 + T2) / 2;
                        String time;

                        if (Av == ((int) Av)) {
                            time = decimalFormat.format((int) Av + 00) + ":00";
                        } else {
                            time = (decimalFormat.format((int) Av + 00) + ":30");
                        }

                        Log.e("ABC", time);

                        DT = date1 + " " + time;
                        DatabaseReference newPost2 = FirebaseDatabase.getInstance().getReference().child("Cab").child(vector_fetched.get(i).getKey());
                        newPost2.child("DT").setValue(DT);
                        Log.e("ABC", DT);
                        vector_fetched.get(i).setDT(DT);

                    }


                    if (date.compareTo(DT) <= 0) {

                        treeMap.put(DT, vector_fetched.get(i));

                    } else {

                        String key = vector_fetched.get(i).getKey();
                        moveGameRoom(firebaseDatabase.getReference().child("Cab").child(key), firebaseDatabase.getReference().child("archive/Cab").child(key));

                    }
                }

                vector_final.addAll(treeMap.values());
                Log.e("ABC1", String.valueOf(vector_final.size()));

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
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
    });

        onEmpty=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),AddCabPool.class);
                startActivity(intent);
            }
        };





        return view;
    }






    private void moveGameRoom(final DatabaseReference fromPath, final DatabaseReference toPath) {
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



}
