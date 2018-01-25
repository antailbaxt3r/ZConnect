package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.ShopCategoryItemCategory;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class Shop extends Fragment {
    ShopCategoryRV adapter;
    DatabaseReference mUserStats, mFeaturesStats;
    FirebaseUser user;
    String TotalOffers;

    private SharedPreferences communitySP;
    public String communityReference;

    private DatabaseReference databaseReference;
    private Vector<ShopCategoryItemCategory> shopCategoryItemCategories = new Vector<>();
    private RecyclerView recycleView;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        communitySP = getActivity().getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference)
                .child("Shop").child("Category");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(VISIBLE);
                shopCategoryItemCategories.clear();
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    shopCategoryItemCategories.add(shot.getValue(ShopCategoryItemCategory.class));
                }

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(INVISIBLE);
                Log.e("TAG", databaseError.getDetails());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_shop, container, false);
        recycleView = (RecyclerView) v.findViewById(R.id.content_shop_rv);
        progressBar = (ProgressBar) v.findViewById(R.id.content_shop_progress);

        recycleView.setHasFixedSize(true);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ShopCategoryRV(getContext(), shopCategoryItemCategories);
        recycleView.setAdapter(adapter);
        databaseReference.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();




        SharedPreferences sharedPref = getContext().getSharedPreferences("guestMode", MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);

        if (!status) {
            user = mAuth.getCurrentUser();

            mUserStats = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference)
                    .child("Users").child(user.getUid()).child("Stats");
            mFeaturesStats = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference)
                    .child("Stats");

            mFeaturesStats.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        TotalOffers = dataSnapshot.child("TotalOffers").getValue().toString();
                        DatabaseReference newPost = mUserStats;
                        Map<String, Object> taskMap = new HashMap<String, Object>();
                        taskMap.put("TotalOffers", TotalOffers);
                        newPost.updateChildren(taskMap);
                    }catch (Exception e) {
                        Log.d("Error Alert: ", e.getMessage());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_shop, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search_menu_phonebook) {
            CounterManager.shopSearch();
            Intent searchintent = new Intent(getContext(), ShopSearch.class);
            startActivity(searchintent);
        }
        return super.onOptionsItemSelected(item);
    }
}
