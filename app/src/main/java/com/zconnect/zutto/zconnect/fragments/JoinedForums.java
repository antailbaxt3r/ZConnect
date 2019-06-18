package com.zconnect.zutto.zconnect.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.zconnect.zutto.zconnect.utilities.ForumsUserTypeUtilities;

import static com.zconnect.zutto.zconnect.R.menu.menu_infone_contact_list;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

import java.io.FileReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class JoinedForums extends Fragment {

    private RecyclerView joinedForumsRV;
    private LinearLayoutManager linearLayoutManager;
    private JoinedForumsAdapter adapter;

    private DatabaseReference forumsCategoriesRef;
    private Vector<ForumCategoriesItemFormat> forumCategoriesItemFormats = new Vector<>();
    private Vector<ForumCategoriesItemFormat> searchForumCategoriesItemFormats;
    ForumCategoriesItemFormat exploreButton = new ForumCategoriesItemFormat();
    private ValueEventListener joinedForumsListener;
    private DBHelper mydb;
    private Map<String,Integer> allForumsSeenMessages = new HashMap();

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;


    public JoinedForums() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=  inflater.inflate(R.layout.fragment_joined_forums, container, false);

        mAuth = FirebaseAuth.getInstance();

        forumsCategoriesRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories");
        joinedForumsRV =view.findViewById(R.id.joined_forums_rv);
        progressBar = view.findViewById(R.id.progress_bar);
        linearLayoutManager = new LinearLayoutManager(view.getContext());

        setAdapter("lite",false);
        return  view;
    }

    private  void setAdapter(final String queryString, final Boolean search) {

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

                for (DataSnapshot shot: dataSnapshot.getChildren()){
                    for(DataSnapshot shot2: shot.getChildren()){
                        try {
                            if (shot2.child("users").hasChild(FirebaseAuth.getInstance().getUid())){
                                ForumCategoriesItemFormat temp;
                                temp =  shot2.getValue(ForumCategoriesItemFormat.class);
                                if(allForumsSeenMessages.get(temp.getCatUID())!=null) {
                                    temp.setSeenMessages(allForumsSeenMessages.get(temp.getCatUID()));
                                }else{
                                    if(!shot2.hasChild("totalMessages")){
                                        temp.setSeenMessages(0);
                                    }else{
                                        temp.setSeenMessages(Integer.parseInt(shot2.child("totalMessages").getValue().toString()));
                                    }
                                }
                                if(!shot2.hasChild("totalMessages")){
                                    temp.setTotalMessages(0);
                                }
                                temp.setForumType(ForumTypeUtilities.KEY_JOINED_STR);
                                if(shot2.child("users").child(mAuth.getCurrentUser().getUid()).hasChild("userType")){
                                    if(!(shot2.child("users").child(mAuth.getCurrentUser().getUid()).child("userType").getValue().equals(ForumsUserTypeUtilities.KEY_BLOCKED))) {
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
                                }else {
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
                            }
                        } catch (Exception e){}
                    }
                }

                Collections.sort(forumCategoriesItemFormats, new Comparator<ForumCategoriesItemFormat>() {
                    @Override
                    public int compare(ForumCategoriesItemFormat o2, ForumCategoriesItemFormat o1) {

                        return Long.valueOf((Long) o2.getLastMessage().getTimeDate()).compareTo((Long) o1.getLastMessage().getTimeDate()) ;
                    }
                });

                forumCategoriesItemFormats.add(exploreButton);

                Collections.reverse(forumCategoriesItemFormats);

                adapter = new JoinedForumsAdapter(forumCategoriesItemFormats,getContext());
                joinedForumsRV.setLayoutManager(linearLayoutManager);
                joinedForumsRV.setAdapter(adapter);

                progressBar.setVisibility(View.GONE);
                joinedForumsRV.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
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
            forumsCategoriesRef.addValueEventListener(joinedForumsListener);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_joinedforum_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);

       SearchView searchView = new SearchView(((HomeActivity)getActivity()).getSupportActionBar().getThemedContext());
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        mydb = new DBHelper(getContext());
        allForumsSeenMessages = mydb.getAllForums();
        forumsCategoriesRef.addValueEventListener(joinedForumsListener);

    }

    @Override
    public void onPause() {
        super.onPause();
        forumsCategoriesRef.removeEventListener(joinedForumsListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

}
