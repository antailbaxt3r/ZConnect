package com.zconnect.zutto.zconnect.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.adapters.CommunitiesAroundAdapter;
import com.zconnect.zutto.zconnect.itemFormats.CommunitiesItemFormat;

import java.util.ArrayList;
import java.util.Vector;

public class CommunitiesFragment extends Fragment {
    String currenttab;
    ValueEventListener eventListener;
    DatabaseReference dbRef;
    RecyclerView communitiesRecycler;
    Double lat,lon;
    CommunitiesAroundAdapter adapter;
    Vector<CommunitiesItemFormat> communitiesList = new Vector<>();
    RelativeLayout noCommunitiesLayout;
    ProgressBar loader;
    DatabaseReference communitiesReference = FirebaseDatabase.getInstance().getReference().child("communitiesInfo");
    DatabaseReference userCommunitiesReference = FirebaseDatabase.getInstance().getReference().child("userCommunities").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("communitiesJoined");


    public CommunitiesFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_comunities, container, false);
        Bundle bundle = getArguments();
        currenttab = bundle.getString("type");
        loader = view.findViewById(R.id.progress_bar_communities);
        loader.setIndeterminate(true);
        loader.setVisibility(View.VISIBLE);
        lat = bundle.getDouble("lat");
        noCommunitiesLayout = view.findViewById(R.id.no_communities_layout);
        adapter = new CommunitiesAroundAdapter(view.getContext(), communitiesList);
        communitiesRecycler = (RecyclerView) view.findViewById(R.id.communities_rv);
        communitiesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        communitiesRecycler.setAdapter(adapter);

        lon = bundle.getDouble("lon");
        if(currenttab.equals("nearby")){
            communitiesReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    communitiesList.clear();
                    loader.setVisibility(View.VISIBLE);



                    Boolean flagNoCommunity = true;

                    for (DataSnapshot shot : dataSnapshot.getChildren()) {
                        CommunitiesItemFormat communitiesItemFormat = shot.getValue(CommunitiesItemFormat.class);
                        try {
                            double comLat, comLon,totalDistance;
                            Integer radius;

                            radius = communitiesItemFormat.getRadius();

                            comLat = shot.child("location").child("lat").getValue(Double.class);
                            comLon = shot.child("location").child("lon").getValue(Double.class);

                            totalDistance = distance(lat,comLat,lon,comLon);
                            if(totalDistance<radius){

                                communitiesList.add(communitiesItemFormat);
                                flagNoCommunity = false;
                            }

                        }catch (Exception e){
                            Log.d("ERROR",e.toString());

                        }
                    }
                    if(flagNoCommunity){
                        noCommunitiesLayout.setVisibility(View.VISIBLE);
                        communitiesRecycler.setVisibility(View.GONE);
                    }
                    else{
                        noCommunitiesLayout.setVisibility(View.GONE);
                        communitiesRecycler.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                    loader.setVisibility(View.GONE);

                    Boolean finalFlagNoCommunity = flagNoCommunity;

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else{
            communitiesReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    communitiesList.clear();

                    userCommunitiesReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            loader.setVisibility(View.GONE);

                            communitiesList.clear();
                            boolean noJoinedCommunities = true;
                            if(dataSnapshot2.getValue() == null){
                                CommunitiesItemFormat noUsers = new CommunitiesItemFormat();
                                noUsers.setName("No communities found");
                                noCommunitiesLayout.setVisibility(View.VISIBLE);
                                communitiesRecycler.setVisibility(View.GONE);
                                return;
                            }

                            try {
                                for (DataSnapshot shot : dataSnapshot2.getChildren()) {
                                    CommunitiesItemFormat communitiesItemFormat2 = dataSnapshot.child(shot.getValue().toString()).getValue(CommunitiesItemFormat.class);
                                    communitiesList.add(communitiesItemFormat2);
                                    noJoinedCommunities = false;
                                }
                            }
                            catch (Exception e)
                            {
                                Log.e("Message","New User");
                            }
                            if(noJoinedCommunities){
                                noCommunitiesLayout.setVisibility(View.VISIBLE);
                                communitiesRecycler.setVisibility(View.GONE);
                            }
                            else{
                                noCommunitiesLayout.setVisibility(View.GONE);
                                communitiesRecycler.setVisibility(View.VISIBLE);
                            }

                            adapter.notifyDataSetChanged();
                            loader.setVisibility(View.GONE);

                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // in Kilometers



        return distance;
    }




}
