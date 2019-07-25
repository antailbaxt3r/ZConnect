package com.zconnect.zutto.zconnect.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zconnect.zutto.zconnect.FirebaseForumsViewHolder;
import com.zconnect.zutto.zconnect.HomeActivity;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.adapters.ForumCategoriesRVAdapter;
import com.zconnect.zutto.zconnect.adapters.JoinedForumsAdapter;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.DBHelper;
import com.zconnect.zutto.zconnect.holders.JoinedForumsRVViewHolder;
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
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;


//    private DatabaseReference forumsCategoriesRef;
    private ShimmerFrameLayout shimmerFrameLayout;
    private DatabaseReference forumsCategoriesRef;

    private Vector<ForumCategoriesItemFormat> forumCategoriesItemFormats = new Vector<>();
    private Vector<ForumCategoriesItemFormat> notifTabForum = new Vector<>();
    private Vector<ForumCategoriesItemFormat> searchForumCategoriesItemFormats;
    ForumCategoriesItemFormat exploreButton = new ForumCategoriesItemFormat();
    private ValueEventListener joinedForumsListener;
    private DBHelper mydb;
    private Map<String,Integer> allForumsSeenMessages = new HashMap();

    private FirebaseAuth mAuth;
//    private ProgressBar progressBar;

    //New Forums
    private DatabaseReference userForumsRef;
//    private ValueEventListener forumEventListener;.

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
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container_forums);
        shimmerFrameLayout.startShimmerAnimation();
        joinedForumsRV.setVisibility(View.INVISIBLE);
        linearLayoutManager = new LinearLayoutManager(view.getContext());
        userForumsRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("userForums").child(mAuth.getCurrentUser().getUid()).child("joinedForums");
//        setUpFirebaseAdapter();
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
                        temp.setSeenMessages(0);


                        if(activityType != null) {
                            if (activityType.equals(ForumUtilities.VALUE_SHARE_FORUM_STR)) {
                                Log.d("Setting message", message);
                                temp.setForumType(ForumUtilities.VALUE_SHARE_FORUM_STR);
                                temp.setMessage(message);
                                temp.setMessageType(messageType);

                            }
                        }
                        else {
                            if(shot2.child("forumType").getValue()==null)
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

                        boolean unread = false;
                        if(temp.getTotalMessages()>temp.getSeenMessages()){
                        Log.d("UNREADMESSAGE",temp.getName());
                         unread = true;
                        isUnread = true;


                    }
//                    if(unread){
//                        if(activityType == null) {
//
//                            TabLayout tabs = getActivity().findViewById(R.id.navigation);
//                            tabs.getTabAt(1).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.VISIBLE);
//                        }
////                        isUnread = false;
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
//                if(adapter == null ) {
                    Log.d("TryHere","Setting Adapter");
                    adapter = new JoinedForumsAdapter(forumCategoriesItemFormats, getActivity());
                    joinedForumsRV.setLayoutManager(linearLayoutManager);
                    joinedForumsRV.setAdapter(adapter);
                    if(joinedForumsRV != null && recyclerViewState != null) {
                        joinedForumsRV.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                    }

//                }
//                else{
//                    adapter.notifyDataSetChanged();
                adapter.updateList(forumCategoriesItemFormats);

//                }
//                try {
//                    if (!isUnread) {
//                        if (activityType == null) {
//                            TabLayout tabs = getActivity().findViewById(R.id.navigation);
//                            tabs.getTabAt(1).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.GONE);
//                        }
//                    } else {
//                        if (activityType == null) {
//
//                            TabLayout tabs = getActivity().findViewById(R.id.navigation);
//                            tabs.getTabAt(1).getCustomView().findViewById(R.id.notification_circle).setVisibility(View.VISIBLE);
//                            isUnread = false;
//                        }
//                    }
//                }
//                catch (Exception e){
//                    Log.d("JoinedForumDot",e.toString());
//                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        joinedForumsRV.setVisibility(View.VISIBLE);

                    }
                }, 500);
//                adapter.notifyDataSetChanged();
                adapter.updateList(forumCategoriesItemFormats);
//                customSimpleAdapterListView(forumCategoriesItemFormats);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        if(search){
            if(!queryString.equals("")) {
                searchForumCategoriesItemFormats = new Vector<ForumCategoriesItemFormat>();
                for (int i = 0; i < forumCategoriesItemFormats.size(); i++) {
                    try {

                        if (forumCategoriesItemFormats.get(i).getName().toLowerCase().trim().contains(queryString.toLowerCase())) {
                            searchForumCategoriesItemFormats.add(forumCategoriesItemFormats.get(i));
                        }
                        if (searchForumCategoriesItemFormats.size() > 7) {
                            break;
                        }
                    }
                    catch (Exception e){
                        Log.d("ERROR",e.toString());
                    }
                }

                adapter = new JoinedForumsAdapter(searchForumCategoriesItemFormats,getContext());
                joinedForumsRV.setAdapter(adapter);
//                progressBar.setVisibility(View.GONE);
                joinedForumsRV.setVisibility(View.VISIBLE);
            }else {
                adapter = new JoinedForumsAdapter(forumCategoriesItemFormats,getContext());
                joinedForumsRV.setAdapter(adapter);
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
        searchView.setVisibility(View.VISIBLE);
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
//        userForumsRef.addValueEventListener(joinedForumsListener);
//        forumsCategoriesRef.addValueEventListener(joinedForumsListener);

    }

    @Override
    public void onPause() {
        super.onPause();
//        userForumsRef.removeEventListener(joinedForumsListener);
//        forumsCategoriesRef.removeEventListener(joinedForumsListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void setUpFirebaseAdapter() {
        Log.d("FIREBASERV","Started");


        FirebaseRecyclerOptions<ForumCategoriesItemFormat> options =
                new FirebaseRecyclerOptions.Builder<ForumCategoriesItemFormat>()
                        .setQuery(userForumsRef, new SnapshotParser<ForumCategoriesItemFormat>() {
                            @NonNull
                            @Override
                            public ForumCategoriesItemFormat parseSnapshot(@NonNull DataSnapshot snapshot) {
                                if(snapshot.getValue(ForumCategoriesItemFormat.class) != null) {
                                    Log.d("NOTEMPTYFORMAT","EMPTYFORMATTT");

                                    return snapshot.getValue(ForumCategoriesItemFormat.class);
                                }
                                else{
                                    Log.d("EMPTYFORMATTTTT","EMPTYFORMATTT");
                                    return new ForumCategoriesItemFormat();
                                }
                            }
                        })
                        .build();


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ForumCategoriesItemFormat, FirebaseForumsViewHolder>(options)
                 {


            @Override
            public FirebaseForumsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                Log.d("FIREBASERV","View Created");
//                }
                View joinedContactView = layoutInflater.inflate(R.layout.row_forums_sub_categories_joined, parent, false);
            return new FirebaseForumsViewHolder(joinedContactView);


            }

            @Override
            protected void onBindViewHolder(@NonNull FirebaseForumsViewHolder holder, int position, @NonNull ForumCategoriesItemFormat model) {
                FirebaseMessaging.getInstance().subscribeToTopic(forumCategoriesItemFormats.get(position).getCatUID());
                final ForumCategoriesItemFormat forumCategory = forumCategoriesItemFormats.get(position);


                    final FirebaseForumsViewHolder holderMain = (FirebaseForumsViewHolder) holder;
                    Log.d("In here", forumCategoriesItemFormats.get(position).getTabUID().toString());

                    holderMain.setDetails(forumCategoriesItemFormats.get(position));
                    holderMain.openChat(forumCategoriesItemFormats.get(position).getCatUID(), forumCategoriesItemFormats.get(position).getTabUID(), forumCategoriesItemFormats.get(position).getName());

                }



        };
        joinedForumsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        joinedForumsRV.setAdapter(firebaseRecyclerAdapter);
        joinedForumsRV.setVisibility(View.VISIBLE);
        shimmerFrameLayout.setVisibility(View.GONE);
    }

    private void customSimpleAdapterListView(Vector<ForumCategoriesItemFormat> forumCategoriesItemFormats)
    {


        ArrayList<Map<String,Object>> itemDataList = new ArrayList<Map<String,Object>>();;

        int titleLen = forumCategoriesItemFormats.size();
        for(int i =0; i < titleLen; i++) {
            Map<String,Object> listItemMap = new HashMap<String,Object>();
            listItemMap.put("imageId", forumCategoriesItemFormats.get(i).getImage());
            listItemMap.put("title", forumCategoriesItemFormats.get(i).getName());
            listItemMap.put("description", forumCategoriesItemFormats.get(i).getLastMessage().getMessage());
            itemDataList.add(listItemMap);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(),itemDataList,R.layout.row_forums_sub_categories_joined,
                new String[]{"imageId","title","description"},new int[]{R.id.forums_group_icon_row_forums_sub_categories_joined,R.id.cat_name,R.id.forums_cat_last_message_with_username
                });

        ListView listView = (ListView)getView().findViewById(R.id.listView);
        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                Object clickItemObj = adapterView.getAdapter().getItem(index);
                HashMap clickItemMap = (HashMap)clickItemObj;
                String itemTitle = (String)clickItemMap.get("title");
                String itemDescription = (String)clickItemMap.get("description");

                Toast.makeText(getContext(), "You select item is  " + itemTitle + " , " + itemDescription, Toast.LENGTH_SHORT).show();
            }
        });

    }

}