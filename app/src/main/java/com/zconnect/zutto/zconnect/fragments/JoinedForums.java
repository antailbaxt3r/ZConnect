//package com.zconnect.zutto.zconnect.fragments;
//
//import android.app.SearchManager;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Parcelable;
//import android.support.annotation.NonNull;
//import android.support.design.widget.TabLayout;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.MenuItemCompat;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.support.v7.widget.SearchView;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ProgressBar;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.zconnect.zutto.zconnect.ChatActivity;
//import com.zconnect.zutto.zconnect.HomeActivity;
//import com.zconnect.zutto.zconnect.R;
//import com.zconnect.zutto.zconnect.adapters.JoinedForumsAdapter;
//import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
//import com.zconnect.zutto.zconnect.commonModules.CounterPush;
//import com.zconnect.zutto.zconnect.commonModules.DBHelper;
//import com.zconnect.zutto.zconnect.itemFormats.ChatItemFormats;
//import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
//import com.zconnect.zutto.zconnect.itemFormats.ForumCategoriesItemFormat;
//import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
//import com.zconnect.zutto.zconnect.utilities.ForumTypeUtilities;
//import com.zconnect.zutto.zconnect.utilities.ForumUtilities;
//import com.zconnect.zutto.zconnect.utilities.ForumsUserTypeUtilities;
//import com.zconnect.zutto.zconnect.utilities.UserUtilities;
//
//import static android.content.Context.POWER_SERVICE;
//import static com.zconnect.zutto.zconnect.R.menu.menu_infone_contact_list;
//import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Vector;
//
//
//public class JoinedForums extends Fragment {
//
//    private RecyclerView joinedForumsRV;
//    private LinearLayoutManager linearLayoutManager;
//    private JoinedForumsAdapter adapter = null;
//
////    private DatabaseReference forumsCategoriesRef;
//    private Vector<ForumCategoriesItemFormat> forumCategoriesItemFormats = new Vector<>();
//    private Vector<ForumCategoriesItemFormat> notifTabForum = new Vector<>();
//    private Vector<ForumCategoriesItemFormat> searchForumCategoriesItemFormats;
//    ForumCategoriesItemFormat exploreButton = new ForumCategoriesItemFormat();
//    private ValueEventListener joinedForumsListener;
//    private DBHelper mydb;
//    private Map<String,Integer> allForumsSeenMessages = new HashMap();
//
//    private FirebaseAuth mAuth;
//    private ProgressBar progressBar;
//
//    //New Forums
//    private DatabaseReference userForumsRef;
//    private ValueEventListener forumEventListener;
//
//    //Share Forums
//    boolean isShare =false;
//    String activityType;
//    String messageType;
//    String message;
//
//    boolean isUnread = false;
//
//    public JoinedForums() {
//        // Required empty public constructor
//    }
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            activityType = getArguments().getString(ForumUtilities.KEY_ACTIVITY_TYPE_STR);
//            if(activityType.equals(ForumUtilities.VALUE_SHARE_FORUM_STR)){
//                messageType = getArguments().getString(ForumUtilities.KEY_MESSAGE_TYPE_STR,null);
//                if(messageType != null){
//                    message = getArguments().getString(ForumUtilities.KEY_MESSAGE,null);
//                }
//            }
//        }
//
//        setHasOptionsMenu(true);
//
////        Log.d("USEROBJECT", UserUtilities.currentUser.toString());
//
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view=  inflater.inflate(R.layout.fragment_joined_forums, container, false);
//
//        mAuth = FirebaseAuth.getInstance();
//
////        forumsCategoriesRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories");
//        joinedForumsRV =view.findViewById(R.id.joined_forums_rv);
//        progressBar = view.findViewById(R.id.progress_bar);
//        linearLayoutManager = new LinearLayoutManager(view.getContext());
//        userForumsRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("userForums").child(mAuth.getCurrentUser().getUid()).child("joinedForums");
//
//        setAdapter("lite",false);
//        return  view;
//    }
//
//    private  void setAdapter(final String queryString, final Boolean search) {
//
//        joinedForumsListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                forumCategoriesItemFormats.clear();
//                Parcelable recyclerViewState = null;
//                isUnread = false;
//                try {
//                    if (joinedForumsRV != null) {
//                        recyclerViewState = joinedForumsRV.getLayoutManager().onSaveInstanceState();
//                    }
//                }
//                catch (Exception e){
//                    recyclerViewState = null;
//                }
//
//
//                ChatItemFormats fakeLastMessage = new ChatItemFormats();
//                fakeLastMessage.setMessage(" ");
//                fakeLastMessage.setTimeDate(0);
//                fakeLastMessage.setName(" ");
//
//                exploreButton.setName("Explore");
//                exploreButton.setCatUID("add");
//                exploreButton.setTabUID("this");
//                exploreButton.setLastMessage(fakeLastMessage);
//                exploreButton.setForumType(ForumTypeUtilities.KEY_EXPLORER_FORUM_STR);
//
////                for (DataSnapshot shot: dataSnapshot.getChildren()){
////                    for(DataSnapshot shot2: shot.getChildren()){
////                            if (shot2.child("users").hasChild(FirebaseAuth.getInstance().getUid())){
//                for(DataSnapshot shot2: dataSnapshot.getChildren()) {
//                    try {
//                        String name = null;
//                        String imageURL = null;
//                        Log.d("Try",shot2.toString());
//                        ForumCategoriesItemFormat temp = new ForumCategoriesItemFormat();
//                    try{
//                        temp = shot2.getValue(ForumCategoriesItemFormat.class);
//                    }
//                    catch (Exception e){
//                        Log.d("Try:ErrorFormat", e.toString());
//
//                    }
//                        if(temp==null) {
//                            temp.setTabUID(shot2.child("tabUID").getValue().toString());
//                            temp.setCatUID(shot2.child("catUID").getValue().toString());
//                        }
//
//
//
//                    if(shot2.child("TabUID").toString().equals("personalChats")){
//                        name = shot2.child("personalChatTitle").getValue().toString();
//                        imageURL = shot2.child("imageThumb").getValue().toString();
//                        shot2.child("personalChatTitle");
//                    }
//
//                    if (!shot2.hasChild("totalMessages")) {
//                        temp.setTotalMessages(0);
//                    }
//                        mydb = new DBHelper(getContext());
////                        Log.d("TabUID",)
//                        notifTabForum.clear();
//                        notifTabForum = mydb.getTabForums(shot2.child("tabUID").getValue().toString());
//                    temp.setSeenMessages(totalSeenNumber(temp.getCatUID()));
//
//
//                        if(activityType != null) {
//                        if (activityType.equals(ForumUtilities.VALUE_SHARE_FORUM_STR)) {
//                            Log.d("Setting message", message);
//                            temp.setForumType(ForumUtilities.VALUE_SHARE_FORUM_STR);
//                            temp.setMessage(message);
//                            temp.setMessageType(messageType);
//
//                        }
//                    }
//                    else {
//                        temp.setForumType(ForumTypeUtilities.KEY_JOINED_STR);
//                    }
//                    if(name!=null){
//                        temp.setName(name);
//                    }
//                    if(imageURL!=null){
//                        temp.setImage(imageURL);
//                        temp.setImageThumb(imageURL);
//                    }
//                    Log.d("Try",temp.getCatUID());
//                    Log.d("Try",temp.getForumType());
//                    Log.d("Try",temp.getLastMessage().getMessage());
//                    Log.d("Try",temp.getTotalMessages().toString());
////
////                        if(shot2.hasChild("isUnread")){
////                            temp.setUnread(shot2.child("isUnread").getValue(Boolean.class));
////                        }
////                        else{
////
////                        }
////                    if(temp.getTabUID().toString().equals("personalChats")){
////                        final DatabaseReference actualForum = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(temp.getTabUID()).child(temp.getCatUID()).child("users");
////                        actualForum.addListenerForSingleValueEvent(new ValueEventListener() {
////                            @Override
////                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                               for(DataSnapshot user: dataSnapshot.getChildren()){
////                                   if(user.child("userUID").getValue().toString().equals(mAuth.getCurrentUser().getUid())){
////                                       continue;
////                                   }
////                                   temp.setName(user.child("name").getValue().toString());
////                                   temp.setImageThumb(user.child("imageThumb").getValue().toString());
////                               }
////
////                            }
////
////                            @Override
////                            public void onCancelled(@NonNull DatabaseError databaseError) {
////
////                            }
////                        });
////                    }
//                        boolean unread = false;
//                    if(temp.getTotalMessages()>temp.getSeenMessages()){
//                        Log.d("UNREADMESSAGE",temp.getName());
//                        unread = true;
//                        isUnread = true;
//
//
//                    }
//                    if(unread){
//                        TabLayout tabs = getActivity().findViewById(R.id.navigation);
//                        tabs.getTabAt(1).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.VISIBLE);
////                        isUnread = false;
//                    }
//
//
//
//                    if (shot2.child("users").child(mAuth.getCurrentUser().getUid()).hasChild("userType")) {
//                        if (!(shot2.child("users").child(mAuth.getCurrentUser().getUid()).child("userType").getValue().equals(ForumsUserTypeUtilities.KEY_BLOCKED))) {
//                            if (shot2.hasChild("lastMessage")) {
//                                forumCategoriesItemFormats.add(temp);
//                            } else {
//                                ChatItemFormats lastMessage = new ChatItemFormats();
//                                lastMessage.setMessage(" ");
//                                lastMessage.setTimeDate(1388534400);
//                                lastMessage.setName(" ");
//                                temp.setLastMessage(lastMessage);
//                                forumCategoriesItemFormats.add(temp);
//                            }
//                        }
//                    } else {
//                        if (shot2.hasChild("lastMessage")) {
//                            forumCategoriesItemFormats.add(temp);
//                        } else {
//                            ChatItemFormats lastMessage = new ChatItemFormats();
//                            lastMessage.setMessage(" ");
//                            lastMessage.setTimeDate(1388534400);
//                            lastMessage.setName(" ");
//                            lastMessage.setMessageType("message");
//                            lastMessage.setUuid(" ");
//                            temp.setLastMessage(lastMessage);
//                            forumCategoriesItemFormats.add(temp);
//                        }
//
//                        }
//
//
//                }catch (Exception e){Log.e("Try:Outside Error",e.toString());}
//                        }
//
//
//
//                Collections.sort(forumCategoriesItemFormats, new Comparator<ForumCategoriesItemFormat>() {
//                    @Override
//                    public int compare(ForumCategoriesItemFormat o2, ForumCategoriesItemFormat o1) {
//
//                        return Long.valueOf((Long) o2.getLastMessage().getTimeDate()).compareTo((Long) o1.getLastMessage().getTimeDate()) ;
//                    }
//                });
//                if(activityType == null) {
//
//                    forumCategoriesItemFormats.add(exploreButton);
//                }
//
//                Collections.reverse(forumCategoriesItemFormats);
//
////                adapter = new JoinedForumsAdapter(forumCategoriesItemFormats,getContext());
////                joinedForumsRV.setLayoutManager(linearLayoutManager);
////                joinedForumsRV.setAdapter(adapter);
//
//                progressBar.setVisibility(View.GONE);
//                joinedForumsRV.setVisibility(View.VISIBLE);
//                Log.d("Adapter List",forumCategoriesItemFormats.toString());
//                if(adapter == null ) {
//                    Log.d("TryHere","Setting Adapter");
//                    adapter = new JoinedForumsAdapter(forumCategoriesItemFormats, getActivity());
//                    joinedForumsRV.setLayoutManager(linearLayoutManager);
//                    joinedForumsRV.setAdapter(adapter);
//
//                }
//                else{
////                    adapter.updateArrayListItems(new ArrayList<>(forumCategoriesItemFormats));
//                    adapter.notifyDataSetChanged();
//
//                }
//                if(joinedForumsRV != null && recyclerViewState != null) {
//                    joinedForumsRV.getLayoutManager().onRestoreInstanceState(recyclerViewState);
//                }
//
//                if(!isUnread){
//
//                    TabLayout tabs = getActivity().findViewById(R.id.navigation);
//                    tabs.getTabAt(1).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.GONE);
//                }
//                else{
//                    TabLayout tabs = getActivity().findViewById(R.id.navigation);
//                    tabs.getTabAt(1).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.VISIBLE);
//                    isUnread = false;
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//
//        userForumsRef.addValueEventListener(joinedForumsListener);
//
//
//        if(search){
//            if(!queryString.equals("")) {
//                searchForumCategoriesItemFormats = new Vector<ForumCategoriesItemFormat>();
//                for (int i = 0; i < forumCategoriesItemFormats.size(); i++) {
//
//                    if (forumCategoriesItemFormats.get(i).getName().toLowerCase().trim().contains(queryString.toLowerCase())) {
//                        searchForumCategoriesItemFormats.add(forumCategoriesItemFormats.get(i));
//                    }
//                    if (searchForumCategoriesItemFormats.size() > 7) {
//                        break;
//                    }
//                }
//
//                adapter = new JoinedForumsAdapter(searchForumCategoriesItemFormats,getContext());
//                joinedForumsRV.setAdapter(adapter);
//                progressBar.setVisibility(View.GONE);
//                joinedForumsRV.setVisibility(View.VISIBLE);
//            }else {
//                adapter = new JoinedForumsAdapter(forumCategoriesItemFormats,getContext());
//                joinedForumsRV.setAdapter(adapter);
//                progressBar.setVisibility(View.GONE);
//                joinedForumsRV.setVisibility(View.VISIBLE);
//            }
//
//        }else {
//            userForumsRef.addValueEventListener(joinedForumsListener);
////            forumsCategoriesRef.addValueEventListener(joinedForumsListener);
//        }
//
//    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//
//        inflater.inflate(R.menu.menu_joinedforum_search, menu);
//        MenuItem item = menu.findItem(R.id.action_search);
//
//
//       SearchView searchView = new SearchView(((HomeActivity)getActivity()).getSupportActionBar().getThemedContext());
//       MenuItemCompat.setShowAsAction(item,MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
//       MenuItemCompat.setActionView(item,searchView);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                setAdapter(query,true);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                setAdapter(newText,true);
//                return false;
//            }
//        });
//
//        MenuItem menuItem = menu.findItem(R.id.action_search);
//
//        MenuItemCompat.setOnActionExpandListener(menuItem,new MenuItemCompat.OnActionExpandListener() {
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem item) {
//
//                return true;
//            }
//
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem item) {
//                setAdapter("lite",false);
//                //Toast.makeText(InfoneContactListActivity.this, "Collapsed", Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });
//    }
//
//    public Integer totalSeenNumber(String catID){
//        Integer seenMessages = 0;
//        mydb = new DBHelper(getContext());
//
//        Log.d("ForumTabs",notifTabForum.toString());
//        Log.d("CATUID",catID);
//        for (int i=0;i<notifTabForum.size(); i++){
//            if(notifTabForum.get(i).getCatUID().equals(catID)){
//                seenMessages = notifTabForum.get(i).getSeenMessages();
//            }
//        }
//        Log.d("Seen message",Integer.toString(seenMessages));
//        return seenMessages;
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mydb = new DBHelper(getContext());
//        allForumsSeenMessages = mydb.getAllForums();
////        userForumsRef.addValueEventListener(joinedForumsListener);
////        forumsCategoriesRef.addValueEventListener(joinedForumsListener);
//
//    }
//
//    @Override
//    public void onPause() {
//        mydb = new DBHelper(getContext());
//
//        super.onPause();
////        userForumsRef.removeEventListener(joinedForumsListener);
////        forumsCategoriesRef.removeEventListener(joinedForumsListener);
//    }
//
//    @Override
//    public void onDetach() {
//        mydb = new DBHelper(getContext());
//
//        super.onDetach();
//
//    }
//
//}
package com.zconnect.zutto.zconnect.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.v7.widget.SearchView;
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
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.HomeActivity;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.adapters.JoinedForumsAdapter;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.DBHelper;
import com.zconnect.zutto.zconnect.itemFormats.ChatItemFormats;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.ForumCategoriesItemFormat;

import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumsUserTypeUtilities;
import static android.content.Context.POWER_SERVICE;
import static com.zconnect.zutto.zconnect.R.menu.menu_infone_contact_list;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

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
    private Vector<ForumCategoriesItemFormat> notifTabForum = new Vector<>();
    private Vector<ForumCategoriesItemFormat> searchForumCategoriesItemFormats;
    ForumCategoriesItemFormat exploreButton = new ForumCategoriesItemFormat();
    private ValueEventListener joinedForumsListener;
    private DBHelper mydb;
    private Map<String,Integer> allForumsSeenMessages = new HashMap();

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    //New Forums
    private DatabaseReference userForumsRef;
    private ValueEventListener forumEventListener;

    //Share Forums
    boolean isShare =false;
    String activityType;
    String messageType;
    String message;

    boolean isUnread;

    public JoinedForums() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activityType = getArguments().getString(ForumUtilities.KEY_ACTIVITY_TYPE_STR);
            if(activityType.equals(ForumUtilities.VALUE_SHARE_FORUM_STR)){
                messageType = getArguments().getString(ForumUtilities.KEY_MESSAGE_TYPE_STR,null);
                if(messageType != null){
                    message = getArguments().getString(ForumUtilities.KEY_MESSAGE,null);
                }
            }
        }

        setHasOptionsMenu(true);

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

        setAdapter("lite",false);
        return  view;
    }

    private  void setAdapter(final String queryString, final Boolean search) {

        joinedForumsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                forumCategoriesItemFormats.clear();
                Parcelable recyclerViewState = null;
                isUnread = false;
                try {
                    if (joinedForumsRV != null) {
                        recyclerViewState = joinedForumsRV.getLayoutManager().onSaveInstanceState();
                    }
                }
                catch (Exception e){
                    recyclerViewState = null;
                }
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

                        if (!shot2.hasChild("totalMessages")) {
                            temp.setTotalMessages(0);
                        }
                        mydb = new DBHelper(getContext());
//                        Log.d("TabUID",)
                        notifTabForum = mydb.getTabForums(shot2.child("tabUID").getValue().toString());
                        temp.setSeenMessages(totalSeenNumber(temp.getCatUID()));


                        if(activityType != null) {
                            if (activityType.equals(ForumUtilities.VALUE_SHARE_FORUM_STR)) {
                                Log.d("Setting message", message);
                                temp.setForumType(ForumUtilities.VALUE_SHARE_FORUM_STR);
                                temp.setMessage(message);
                                temp.setMessageType(messageType);

                            }
                        }
                        else {
                            temp.setForumType(ForumTypeUtilities.KEY_JOINED_STR);
                        }
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

                        if(shot2.hasChild("isUnread")){
                            temp.setUnread(shot2.child("isUnread").getValue(Boolean.class));
                        }
                        else{

                        }
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
                        boolean unread = false;
                        if(temp.getTotalMessages()>temp.getSeenMessages()){
                        Log.d("UNREADMESSAGE",temp.getName());
                         unread = true;
                        isUnread = true;


                    }
                    if(unread){
                        if(activityType == null) {

                            TabLayout tabs = getActivity().findViewById(R.id.navigation);
                            tabs.getTabAt(1).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.VISIBLE);
                        }
//                        isUnread = false;
                    }

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

                    }catch (Exception e){Log.e("Try:Outside Error",e.toString());}
                }



                Collections.sort(forumCategoriesItemFormats, new Comparator<ForumCategoriesItemFormat>() {
                    @Override
                    public int compare(ForumCategoriesItemFormat o2, ForumCategoriesItemFormat o1) {

                        return Long.valueOf((Long) o2.getLastMessage().getTimeDate()).compareTo((Long) o1.getLastMessage().getTimeDate()) ;
                    }
                });
                if(activityType == null) {

                    forumCategoriesItemFormats.add(exploreButton);
                }

                Collections.reverse(forumCategoriesItemFormats);

//                adapter = new JoinedForumsAdapter(forumCategoriesItemFormats,getContext());
//                joinedForumsRV.setLayoutManager(linearLayoutManager);
//                joinedForumsRV.setAdapter(adapter);
//
//                progressBar.setVisibility(View.GONE);
//                joinedForumsRV.setVisibility(View.VISIBLE);
//                Log.d("Adapter List",forumCategoriesItemFormats.toString());
//                adapter = new JoinedForumsAdapter(forumCategoriesItemFormats,getActivity());
//                joinedForumsRV.setLayoutManager(linearLayoutManager);
//                joinedForumsRV.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
                if(adapter == null ) {
                    Log.d("TryHere","Setting Adapter");
                    adapter = new JoinedForumsAdapter(forumCategoriesItemFormats, getActivity());
                    joinedForumsRV.setLayoutManager(linearLayoutManager);
                    joinedForumsRV.setAdapter(adapter);

                }
                else{
//                    adapter.updateArrayListItems(new ArrayList<>(forumCategoriesItemFormats));
                    adapter.notifyDataSetChanged();

                }

                if(!isUnread){
                    if(activityType == null) {
                        TabLayout tabs = getActivity().findViewById(R.id.navigation);
                        tabs.getTabAt(1).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.GONE);
                    }
                }
                else {
                    if (activityType == null) {

                        TabLayout tabs = getActivity().findViewById(R.id.navigation);
                        tabs.getTabAt(1).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.VISIBLE);
                        isUnread = false;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        if(search){
            if(!queryString.equals("")) {
                searchForumCategoriesItemFormats = new Vector<ForumCategoriesItemFormat>();
                for (int i = 0; i < forumCategoriesItemFormats.size(); i++) {

                    if (forumCategoriesItemFormats.get(i).getName().toLowerCase().trim().contains(queryString.toLowerCase())) {
                        searchForumCategoriesItemFormats.add(forumCategoriesItemFormats.get(i));
                    }
                    if (searchForumCategoriesItemFormats.size() > 7) {
                        break;
                    }
                }

                adapter = new JoinedForumsAdapter(searchForumCategoriesItemFormats,getContext());
                joinedForumsRV.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                joinedForumsRV.setVisibility(View.VISIBLE);
            }else {
                adapter = new JoinedForumsAdapter(forumCategoriesItemFormats,getContext());
                joinedForumsRV.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                joinedForumsRV.setVisibility(View.VISIBLE);
            }

        }else {
            userForumsRef.addValueEventListener(joinedForumsListener);
//            forumsCategoriesRef.addValueEventListener(joinedForumsListener);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_joinedforum_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);

        SearchView searchView = new SearchView(((BaseActivity)getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item,MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item,searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setAdapter(query,true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setAdapter(newText,true);
                return false;
            }
        });

        MenuItem menuItem = menu.findItem(R.id.action_search);

        MenuItemCompat.setOnActionExpandListener(menuItem,new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                setAdapter("lite",false);
                //Toast.makeText(InfoneContactListActivity.this, "Collapsed", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public Integer totalSeenNumber(String catID){
        Integer seenMessages = 0;
        Log.d("ForumTabs",notifTabForum.toString());
        Log.d("CATUID",catID);
        for (int i=0;i<notifTabForum.size(); i++){
            if(notifTabForum.get(i).getCatUID().equals(catID)){
                seenMessages = notifTabForum.get(i).getSeenMessages();
            }
        }
        Log.d("Seen message",Integer.toString(seenMessages));
        return seenMessages;
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