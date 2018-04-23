package com.zconnect.zutto.zconnect.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.InfoneCategories;
import com.zconnect.zutto.zconnect.ItemFormats.forumCategoriesItemFormat;
import com.zconnect.zutto.zconnect.ForumCategoriesRVAdapter;
import com.zconnect.zutto.zconnect.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import static com.zconnect.zutto.zconnect.BaseActivity.communityReference;


public class ForumsFragment extends Fragment {
    RecyclerView recyclerView;
    String currenttab;
    private ArrayList<InfoneCategories> infoneCategories = new ArrayList<>();
    private ForumCategoriesRVAdapter adapter;
    DatabaseReference tabsCategories;
    Vector<forumCategoriesItemFormat> forumCategories = new Vector<forumCategoriesItemFormat>();
    forumCategoriesItemFormat addCategoryButton = new forumCategoriesItemFormat();

    public ForumsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_infone, container, false);
        Bundle bundle = getArguments();
        currenttab = bundle.getString("UID");
        Log.v("TASDF", String.valueOf(currenttab));
        tabsCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(currenttab);

        tabsCategories.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                forumCategories.clear();
                for (DataSnapshot shot: dataSnapshot.getChildren()){
                    forumCategories.add(shot.getValue(forumCategoriesItemFormat.class));
                }

                addCategoryButton.setName("+ create a forum");
                addCategoryButton.setCatUID("add");
                addCategoryButton.setTabUID("this");
                forumCategories.add(addCategoryButton);
                Collections.reverse(forumCategories);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_infone_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ForumCategoriesRVAdapter(forumCategories, getContext(),currenttab);
        recyclerView.setAdapter(adapter);
        return view;
    }
}
