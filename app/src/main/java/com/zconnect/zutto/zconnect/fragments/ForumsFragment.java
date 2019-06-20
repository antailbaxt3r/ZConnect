package com.zconnect.zutto.zconnect.fragments;

import android.net.Uri;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.commonModules.DBHelper;
import com.zconnect.zutto.zconnect.itemFormats.ChatItemFormats;
import com.zconnect.zutto.zconnect.itemFormats.ForumCategoriesItemFormat;
import com.zconnect.zutto.zconnect.utilities.ForumsUserTypeUtilities;
import com.zconnect.zutto.zconnect.adapters.ForumCategoriesRVAdapter;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.utilities.ForumTypeUtilities;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;


public class ForumsFragment extends Fragment {
    RecyclerView recyclerView;
    String currenttab;
    private ForumCategoriesRVAdapter adapter;
    DatabaseReference tabsCategories;
    Vector<ForumCategoriesItemFormat> forumCategories = new Vector<ForumCategoriesItemFormat>();
    Vector<ForumCategoriesItemFormat> joinedForumCategories = new Vector<ForumCategoriesItemFormat>();
    Vector<ForumCategoriesItemFormat> notJoinedForumCategories = new Vector<ForumCategoriesItemFormat>();
    ForumCategoriesItemFormat addCategoryButton = new ForumCategoriesItemFormat();
    ForumCategoriesItemFormat titleNotJoined = new ForumCategoriesItemFormat();
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    DBHelper mydb;
    Boolean newUser =false;

    Vector<ForumCategoriesItemFormat> forumCategoriesItemFormats = new Vector<ForumCategoriesItemFormat>();

    Uri mImageUri;

    public ForumsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_infone, container, false);
        Bundle bundle = getArguments();
        currenttab = bundle.getString("UID");
        newUser = bundle.getBoolean("newUser",false);

        Log.v("TASDF", String.valueOf(currenttab));

        progressBar = (ProgressBar) view.findViewById(R.id.fragment_infone_progress_circle);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_infone_fragment);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance();

        tabsCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(currenttab);
        adapter = new ForumCategoriesRVAdapter(forumCategories, getContext(),currenttab,newUser);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mydb = new DBHelper(getContext());

        forumCategoriesItemFormats = mydb.getTabForums(currenttab);

        tabsCategories.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                forumCategories.clear();
                notJoinedForumCategories.clear();
                joinedForumCategories.clear();

                addCategoryButton.setName("+ create a forum");
                addCategoryButton.setCatUID("add");
                addCategoryButton.setTabUID("this");
                addCategoryButton.setForumType(ForumTypeUtilities.KEY_CREATE_FORUM_STR);

                titleNotJoined.setForumType(ForumTypeUtilities.KEY_NOT_JOINED_TITLE_STR);

                for (DataSnapshot shot: dataSnapshot.getChildren()){
                    ForumCategoriesItemFormat temp = new ForumCategoriesItemFormat();

                    if(shot.child("users").hasChild(mAuth.getCurrentUser().getUid())) {
                        temp = shot.getValue(ForumCategoriesItemFormat.class);
                        temp.setSeenMessages(totalSeenNumber(temp.getCatUID()));

                        if(!shot.hasChild("totalMessages")){
                            temp.setTotalMessages(0);
                        }

                        try {
                            if (temp.getName() != null) {
                                temp.setForumType(ForumTypeUtilities.KEY_JOINED_STR);
                                if(shot.child("users").child(mAuth.getCurrentUser().getUid()).hasChild("userType")){
                                    if(!(shot.child("users").child(mAuth.getCurrentUser().getUid()).child("userType").getValue().equals(ForumsUserTypeUtilities.KEY_BLOCKED))) {
                                        if (shot.hasChild("lastMessage")) {
                                            joinedForumCategories.add(temp);
                                        } else {
                                            ChatItemFormats lastMessage = new ChatItemFormats();
                                            lastMessage.setMessage(" ");
                                            lastMessage.setTimeDate(1388534400);
                                            lastMessage.setName(" ");
                                            temp.setLastMessage(lastMessage);
                                            joinedForumCategories.add(temp);
                                        }
                                    }
                                }else {
                                    if (shot.hasChild("lastMessage")) {
                                        joinedForumCategories.add(temp);
                                    } else {
                                        ChatItemFormats lastMessage = new ChatItemFormats();
                                        lastMessage.setMessage(" ");
                                        lastMessage.setTimeDate(1388534400);
                                        lastMessage.setName(" ");
                                        lastMessage.setMessageType("message");
                                        lastMessage.setUuid(" ");
                                        temp.setLastMessage(lastMessage);
                                        joinedForumCategories.add(temp);
                                    }
                                }


                            }
                        } catch (Exception e) {

                        }

                    }else {

                        temp=shot.getValue(ForumCategoriesItemFormat.class);
                        try {
                            if(temp.getName()!=null) {
                                temp.setForumType(ForumTypeUtilities.KEY_NOT_JOINED_STR);
                                notJoinedForumCategories.add(temp);
                            }
                        }catch (Exception e){}
                    }
                }

                forumCategories.add(addCategoryButton);

                Collections.sort(joinedForumCategories, new Comparator<ForumCategoriesItemFormat>() {
                    @Override
                    public int compare(ForumCategoriesItemFormat o1, ForumCategoriesItemFormat o2) {

                        return Long.valueOf((Long) o2.getLastMessage().getTimeDate()).compareTo((Long) o1.getLastMessage().getTimeDate()) ;
                    }
                });

                //Toast.makeText(getContext(), joinedForumCategories.toString(), Toast.LENGTH_SHORT).show();
                forumCategories.addAll(joinedForumCategories);
                forumCategories.add(titleNotJoined);

                Collections.sort(notJoinedForumCategories, new Comparator<ForumCategoriesItemFormat>() {
                    @Override
                    public int compare(ForumCategoriesItemFormat o1, ForumCategoriesItemFormat o2) {
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

    }

    public Integer totalSeenNumber(String catID){
        Integer seenMessages = 0;

        for (int i=0;i<forumCategoriesItemFormats.size(); i++){
            if(forumCategoriesItemFormats.get(i).getCatUID().equals(catID)){
                seenMessages = forumCategoriesItemFormats.get(i).getSeenMessages();
            }
        }

       return seenMessages;
    }

}
