package com.zconnect.zutto.zconnect.fragments;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.adapters.JoinedForumsAdapter;
import com.zconnect.zutto.zconnect.commonModules.DBHelper;
import com.zconnect.zutto.zconnect.itemFormats.ChatItemFormats;
import com.zconnect.zutto.zconnect.itemFormats.ForumCategoriesItemFormat;
import com.zconnect.zutto.zconnect.utilities.ForumTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumsUserTypeUtilities;

import static android.content.Context.POWER_SERVICE;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class JoinedForums extends Fragment {

    private RecyclerView joinedForumsRV;
    private LinearLayoutManager linearLayoutManager;
    private JoinedForumsAdapter adapter;

//    private DatabaseReference forumsCategoriesRef;
    private Vector<ForumCategoriesItemFormat> forumCategoriesItemFormats = new Vector<>();
    ForumCategoriesItemFormat exploreButton = new ForumCategoriesItemFormat();
    private ValueEventListener joinedForumsListener;
    private DBHelper mydb;
    private Map<String,Integer> allForumsSeenMessages = new HashMap();

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    //New Forums
    private DatabaseReference userForumsRef;
    private ValueEventListener forumEventListener;

    public JoinedForums() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=  inflater.inflate(R.layout.fragment_joined_forums, container, false);

        mAuth = FirebaseAuth.getInstance();

//        forumsCategoriesRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories");
        joinedForumsRV =view.findViewById(R.id.joined_forums_rv);
        progressBar = view.findViewById(R.id.progress_bar);
        linearLayoutManager = new LinearLayoutManager(view.getContext());
        userForumsRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("userForums").child(mAuth.getCurrentUser().getUid()).child("joinedForums");

        joinedForumsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                forumCategoriesItemFormats.clear();

                ChatItemFormats fakeLastMessage = new ChatItemFormats();
                fakeLastMessage.setMessage(" ");
                fakeLastMessage.setTimeDate(0);
                fakeLastMessage.setName(" ");

                exploreButton.setName("Explore");
                exploreButton.setCatUID("add");
                exploreButton.setTabUID("this");
                exploreButton.setLastMessage(fakeLastMessage);
                exploreButton.setForumType(ForumTypeUtilities.KEY_EXPLORER_FORUM_STR);

//                for (DataSnapshot shot: dataSnapshot.getChildren()){
//                    for(DataSnapshot shot2: shot.getChildren()){
//                            if (shot2.child("users").hasChild(FirebaseAuth.getInstance().getUid())){
                for(DataSnapshot shot2: dataSnapshot.getChildren()) {
                    try {
                        String name = null;
                        String imageURL = null;
                        Log.d("Try",shot2.toString());
                        ForumCategoriesItemFormat temp = new ForumCategoriesItemFormat();
                    try{
                        temp = shot2.getValue(ForumCategoriesItemFormat.class);
                    }
                    catch (Exception e){
                        Log.d("Try:ErrorFormat", e.toString());

                    }
                        if(temp==null) {
                            temp.setTabUID(shot2.child("tabUID").getValue().toString());
                            temp.setCatUID(shot2.child("catUID").getValue().toString());
                        }

                    if(shot2.child("TabUID").toString().equals("personalChats")){
                        name = shot2.child("personalChatTitle").getValue().toString();
                        imageURL = shot2.child("imageThumb").getValue().toString();
                        shot2.child("personalChatTitle");
                    }
                    if (allForumsSeenMessages.get(temp.getCatUID()) != null) {
                        temp.setSeenMessages(allForumsSeenMessages.get(temp.getCatUID()));
                    } else {
                        if (!shot2.hasChild("totalMessages")) {
                            temp.setSeenMessages(0);
                        } else {
                            temp.setSeenMessages(Integer.parseInt(shot2.child("totalMessages").getValue().toString()));
                        }
                    }
                    if (!shot2.hasChild("totalMessages")) {
                        temp.setTotalMessages(0);
                    }
                    temp.setForumType(ForumTypeUtilities.KEY_JOINED_STR);
                    if(name!=null){
                        temp.setName(name);
                    }
                    if(imageURL!=null){
                        temp.setImage(imageURL);
                        temp.setImageThumb(imageURL);
                    }
                    Log.d("Try",temp.getCatUID());
                    Log.d("Try",temp.getForumType());
                    Log.d("Try",temp.getLastMessage().getMessage());
                    Log.d("Try",temp.getTotalMessages().toString());
//                    if(temp.getTabUID().toString().equals("personalChats")){
//                        final DatabaseReference actualForum = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(temp.getTabUID()).child(temp.getCatUID()).child("users");
//                        actualForum.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                               for(DataSnapshot user: dataSnapshot.getChildren()){
//                                   if(user.child("userUID").getValue().toString().equals(mAuth.getCurrentUser().getUid())){
//                                       continue;
//                                   }
//                                   temp.setName(user.child("name").getValue().toString());
//                                   temp.setImageThumb(user.child("imageThumb").getValue().toString());
//                               }
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//                    }

                    if (shot2.child("users").child(mAuth.getCurrentUser().getUid()).hasChild("userType")) {
                        if (!(shot2.child("users").child(mAuth.getCurrentUser().getUid()).child("userType").getValue().equals(ForumsUserTypeUtilities.KEY_BLOCKED))) {
                            if (shot2.hasChild("lastMessage")) {
                                forumCategoriesItemFormats.add(temp);
                            } else {
                                ChatItemFormats lastMessage = new ChatItemFormats();
                                lastMessage.setMessage(" ");
                                lastMessage.setTimeDate(1388534400);
                                lastMessage.setName(" ");
                                temp.setLastMessage(lastMessage);
                                forumCategoriesItemFormats.add(temp);
                            }
                        }
                    } else {
                        if (shot2.hasChild("lastMessage")) {
                            forumCategoriesItemFormats.add(temp);
                        } else {
                            ChatItemFormats lastMessage = new ChatItemFormats();
                            lastMessage.setMessage(" ");
                            lastMessage.setTimeDate(1388534400);
                            lastMessage.setName(" ");
                            lastMessage.setMessageType("message");
                            lastMessage.setUuid(" ");
                            temp.setLastMessage(lastMessage);
                            forumCategoriesItemFormats.add(temp);
                        }

                    }
                }catch (Exception e){Log.e("Try",e.toString());}
                        }



                Collections.sort(forumCategoriesItemFormats, new Comparator<ForumCategoriesItemFormat>() {
                    @Override
                    public int compare(ForumCategoriesItemFormat o2, ForumCategoriesItemFormat o1) {

                        return Long.valueOf((Long) o2.getLastMessage().getTimeDate()).compareTo((Long) o1.getLastMessage().getTimeDate()) ;
                    }
                });

                forumCategoriesItemFormats.add(exploreButton);

                Collections.reverse(forumCategoriesItemFormats);

                progressBar.setVisibility(View.GONE);
                joinedForumsRV.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        adapter = new JoinedForumsAdapter(forumCategoriesItemFormats,getContext());
        joinedForumsRV.setLayoutManager(linearLayoutManager);
        joinedForumsRV.setAdapter(adapter);
        return  view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        mydb = new DBHelper(getContext());
        allForumsSeenMessages = mydb.getAllForums();
        userForumsRef.addValueEventListener(joinedForumsListener);
//        forumsCategoriesRef.addValueEventListener(joinedForumsListener);

    }

    @Override
    public void onPause() {
        super.onPause();
        userForumsRef.removeEventListener(joinedForumsListener);
//        forumsCategoriesRef.removeEventListener(joinedForumsListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

}
