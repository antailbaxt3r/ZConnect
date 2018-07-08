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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.ChatItemFormats;
import com.zconnect.zutto.zconnect.ItemFormats.forumCategoriesItemFormat;
import com.zconnect.zutto.zconnect.ForumCategoriesRVAdapter;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.Utilities.forumTypeUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import static com.zconnect.zutto.zconnect.BaseActivity.communityReference;


public class ForumsFragment extends Fragment {
    RecyclerView recyclerView;
    String currenttab;
    private ForumCategoriesRVAdapter adapter;
    DatabaseReference tabsCategories;
    Vector<forumCategoriesItemFormat> forumCategories = new Vector<forumCategoriesItemFormat>();
    Vector<forumCategoriesItemFormat> joinedForumCategories = new Vector<forumCategoriesItemFormat>();
    Vector<forumCategoriesItemFormat> notJoinedForumCategories = new Vector<forumCategoriesItemFormat>();
    forumCategoriesItemFormat addCategoryButton = new forumCategoriesItemFormat();
    forumCategoriesItemFormat titleNotJoined = new forumCategoriesItemFormat();
    ProgressBar progressBar;
    FirebaseAuth mAuth;

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

        progressBar = (ProgressBar) view.findViewById(R.id.fragment_infone_progress_circle);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_infone_fragment);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance();

        tabsCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(currenttab);

        tabsCategories.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                forumCategories.clear();
                notJoinedForumCategories.clear();
                joinedForumCategories.clear();

                addCategoryButton.setName("+ create a forum");
                addCategoryButton.setCatUID("add");
                addCategoryButton.setTabUID("this");
                addCategoryButton.setForumType(forumTypeUtilities.KEY_CREATE_FORUM_STR);

                titleNotJoined.setForumType(forumTypeUtilities.KEY_NOT_JOINED_TITLE_STR);

                for (DataSnapshot shot: dataSnapshot.getChildren()){
                    forumCategoriesItemFormat temp = new forumCategoriesItemFormat();
                    if(shot.child("users").hasChild(mAuth.getCurrentUser().getUid())) {
                        temp = shot.getValue(forumCategoriesItemFormat.class);
                        try {
                            if(temp.getName()!=null) {
                                temp.setForumType(forumTypeUtilities.KEY_JOINED_STR);
                                if(shot.hasChild("lastMessage")){
                                    joinedForumCategories.add(temp);
                                }else {
                                    ChatItemFormats lastMessage = new ChatItemFormats();
                                    lastMessage.setMessage(" ");
                                    lastMessage.setTimeDate(1388534400);
                                    lastMessage.setName(" ");
                                    temp.setLastMessage(lastMessage);
                                    joinedForumCategories.add(temp);
                                }

                            }
                        }catch (Exception e){

                        }

                    }else {

                        temp=shot.getValue(forumCategoriesItemFormat.class);
                        try {
                            if(temp.getName()!=null) {
                                temp.setForumType(forumTypeUtilities.KEY_NOT_JOINED_STR);
                                notJoinedForumCategories.add(temp);
                            }
                        }catch (Exception e){}
                    }
                }

                forumCategories.add(addCategoryButton);


                Collections.sort(joinedForumCategories, new Comparator<forumCategoriesItemFormat>() {
                    @Override
                    public int compare(forumCategoriesItemFormat o1, forumCategoriesItemFormat o2) {
                        return Integer.valueOf((int) o1.getLastMessage().getTimeDate()).compareTo((int) o2.getLastMessage().getTimeDate()) ;
                    }
                });

                //Toast.makeText(getContext(), joinedForumCategories.toString(), Toast.LENGTH_SHORT).show();
                forumCategories.addAll(joinedForumCategories);
                forumCategories.add(titleNotJoined);

                Collections.sort(notJoinedForumCategories, new Comparator<forumCategoriesItemFormat>() {
                    @Override
                    public int compare(forumCategoriesItemFormat o1, forumCategoriesItemFormat o2) {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                });

                forumCategories.addAll(notJoinedForumCategories);

                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });



        adapter = new ForumCategoriesRVAdapter(forumCategories, getContext(),currenttab);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
