package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.adapters.RecentsRVAdapter;
import com.zconnect.zutto.zconnect.itemFormats.RecentsItemFormat;
import com.zconnect.zutto.zconnect.utilities.RecentTypeUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.VISIBLE;

public class Recents extends Fragment {

    Vector<RecentsItemFormat> recentsItemFormats = new Vector<>();
    private RecentsRVAdapter adapter;
    @BindView(R.id.recent_rv)
    RecyclerView recyclerView;
    List<String> storeroomProductList = new ArrayList<String>();
    private SharedPreferences communitySP;
    public String communityReference;


    private DatabaseReference homeDbRef,userReference;
    Query queryRef;
    private ValueEventListener homeListener,userListener;
    @BindView(R.id.recent_progress)
    ProgressBar progressBar;

    RecentsItemFormat addStatus = new RecentsItemFormat();
    RecentsItemFormat features = new RecentsItemFormat();

    RecentsItemFormat tempUser = new RecentsItemFormat();

    Vector<RecentsItemFormat> normalPostsHome = new Vector<RecentsItemFormat>();
    Vector<RecentsItemFormat> normalPostsUsers = new Vector<RecentsItemFormat>();
    Vector<RecentsItemFormat> normalPosts = new Vector<RecentsItemFormat>();

    public Recents() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recents, container, false);
        ButterKnife.bind(this, view);

        communitySP = getActivity().getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        homeDbRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home");
        userReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("personalHome");
        userReference.keepSynced(true);
        //Keep databaseReference in sync even without needing to call valueEventListener
        homeDbRef.keepSynced(true);
        queryRef = homeDbRef;
        queryRef.keepSynced(true);

        progressBar.setVisibility(VISIBLE);

        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recentsItemFormats.clear();
                normalPostsUsers.clear();
                normalPosts.clear();

                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    tempUser = new RecentsItemFormat();
                    tempUser = shot.getValue(RecentsItemFormat.class);
                    if(shot.hasChild("recentType")){
                        normalPostsUsers.add(tempUser);
                    }else {
                        tempUser.setRecentType(RecentTypeUtilities.KEY_RECENT_NORMAL_POST_STR);
                        normalPostsUsers.add(tempUser);
                    }

                }

                recentsItemFormats.add(features);
                recentsItemFormats.add(addStatus);

                normalPosts.addAll(normalPostsHome);
                normalPosts.addAll(normalPostsUsers);

                Collections.sort(normalPosts, new Comparator<RecentsItemFormat>() {
                    @Override
                    public int compare(RecentsItemFormat o1, RecentsItemFormat o2) {
                        return Long.valueOf((Long) o2.getPostTimeMillis()).compareTo((Long) o1.getPostTimeMillis()) ;
                    }
                });
                recentsItemFormats.addAll(normalPosts);

                addStatus.setRecentType(RecentTypeUtilities.KEY_RECENT_ADD_STATUS_STR);
                features.setRecentType(RecentTypeUtilities.KEY_RECENT_FEATURES_STR);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        homeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                recentsItemFormats.clear();
                normalPostsHome.clear();
                normalPosts.clear();
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    tempUser = new RecentsItemFormat();
                    tempUser = shot.getValue(RecentsItemFormat.class);
                    if(shot.hasChild("recentType")){
                        normalPostsHome.add(tempUser);
                    }else {
                        tempUser.setRecentType(RecentTypeUtilities.KEY_RECENT_NORMAL_POST_STR);
                        normalPostsHome.add(tempUser);
                    }
                }

                recentsItemFormats.add(features);
                recentsItemFormats.add(addStatus);

                normalPosts.addAll(normalPostsHome);
                normalPosts.addAll(normalPostsUsers);

                Collections.sort(normalPosts, new Comparator<RecentsItemFormat>() {
                    @Override
                    public int compare(RecentsItemFormat o1, RecentsItemFormat o2) {
                        return Long.valueOf((Long) o2.getPostTimeMillis()).compareTo((Long) o1.getPostTimeMillis()) ;
                    }
                });
                recentsItemFormats.addAll(normalPosts);
                addStatus.setRecentType(RecentTypeUtilities.KEY_RECENT_ADD_STATUS_STR);
                features.setRecentType(RecentTypeUtilities.KEY_RECENT_FEATURES_STR);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        };

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager productLinearLayout = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(productLinearLayout);

        adapter = new RecentsRVAdapter(getContext(), recentsItemFormats, (HomeActivity) getActivity());
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        queryRef.addValueEventListener(homeListener);
        userReference.addValueEventListener(userListener);


    }

    @Override
    public void onPause() {
        super.onPause();
        queryRef.removeEventListener(homeListener);
        userReference.removeEventListener(userListener);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_recents, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_open_menu_phonebook) {

            Intent infoneIntent = new Intent(getContext(), InfoneActivity.class);
            startActivity(infoneIntent);
            CounterManager.infoneOpen();
        }else if(id == R.id.admin){
            Intent infoneIntent = new Intent(getContext(), AdminHome.class);
            startActivity(infoneIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
