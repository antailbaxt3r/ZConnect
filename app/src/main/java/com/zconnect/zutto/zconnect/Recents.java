package com.zconnect.zutto.zconnect;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.adapters.RecentsRVAdapter;
import com.zconnect.zutto.zconnect.itemFormats.CommunityFeatures;
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
    private CommunityFeatures communityFeatures;

    private SwipeRefreshLayout swipeContainer;

    private DatabaseReference homeDbRef,userReference,communityFeaturesRef;
    Query queryRef;
    private ValueEventListener homeListener,userListener,communityFeaturesListener;
    @BindView(R.id.recent_progress)
    ProgressBar progressBar;

    RecentsItemFormat addStatus = new RecentsItemFormat();
    RecentsItemFormat features = new RecentsItemFormat();

    RecentsItemFormat tempUser = new RecentsItemFormat();

    Vector<RecentsItemFormat> normalPostsHome = new Vector<RecentsItemFormat>();
    Vector<RecentsItemFormat> normalPostsUsers = new Vector<RecentsItemFormat>();
    Vector<RecentsItemFormat> normalPosts = new Vector<RecentsItemFormat>();

    private FirebaseAnalytics mFirebaseAnalytics;
    LinearLayoutManager productLinearLayoutManager;
    OnHomeIconListener mCallback;

    public void setOnHomeIconListener(Activity activity) {
        mCallback = (OnHomeIconListener) activity;
    }
    public interface OnHomeIconListener {
        public void getLayoutManager(LinearLayoutManager linearLayoutManager);
    }
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

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Yolo");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        communitySP = getActivity().getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        communityFeaturesRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("communityFeatures");
        homeDbRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home");
        userReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("personalHome");
        userReference.keepSynced(true);
        //Keep databaseReference in sync even without needing to call valueEventListener
        homeDbRef.keepSynced(true);
        queryRef = homeDbRef.limitToLast(100);
        queryRef.keepSynced(true);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshContainer);
        swipeContainer.setColorSchemeResources(R.color.colorPrimary, R.color.colorHighlight);

        progressBar.setVisibility(VISIBLE);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // implement Handler to wait for 3 seconds and then update UI means update value of TextView
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // cancel the Visual indication of a refresh
                        swipeContainer.setRefreshing(false);
                        queryRef.addListenerForSingleValueEvent(homeListener);
                        userReference.addListenerForSingleValueEvent(userListener);
                        communityFeaturesRef.addListenerForSingleValueEvent(communityFeaturesListener);
                    }
                }, 3000);
            }
        });
        recyclerView.setHasFixedSize(true);
        productLinearLayoutManager = new LinearLayoutManager(getContext());
        mCallback.getLayoutManager(productLinearLayoutManager);
        recyclerView.setLayoutManager(productLinearLayoutManager);
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recentsItemFormats.clear();
                normalPostsUsers.clear();
                normalPosts.clear();

                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    tempUser = new RecentsItemFormat();
                    tempUser = shot.getValue(RecentsItemFormat.class);
                    tempUser.setPostID(shot.getKey());
                    if(shot.hasChild("feature")) {
                        if (shot.hasChild("recentType")) {
                            normalPostsUsers.add(tempUser);
                        } else {

                            tempUser.setRecentType(RecentTypeUtilities.KEY_RECENT_NORMAL_POST_STR);
                            normalPostsUsers.add(tempUser);
                        }
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

        communityFeaturesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                communityFeatures = dataSnapshot.getValue(CommunityFeatures.class);

                adapter = new RecentsRVAdapter(getContext(), recentsItemFormats, (HomeActivity) getActivity() ,communityFeatures, productLinearLayoutManager, recyclerView);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                    tempUser.setPostID(shot.getKey());
                    if(shot.hasChild("feature")) {
                        if (shot.hasChild("recentType")) {
                            normalPostsHome.add(tempUser);
                        } else {
                            tempUser.setRecentType(RecentTypeUtilities.KEY_RECENT_NORMAL_POST_STR);
                            normalPostsHome.add(tempUser);
                        }
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

//        scrollToTopBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CounterItemFormat counterItemFormat = new CounterItemFormat();
//                HashMap<String, String> meta= new HashMap<>();
//                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
//                counterItemFormat.setUniqueID(CounterUtilities.KEY_RECENTS_SCROLL_TOP);
//                counterItemFormat.setTimestamp(System.currentTimeMillis());
//                counterItemFormat.setMeta(meta);
//                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
//                counterPush.pushValues();
//                productLinearLayoutManager.scrollToPositionWithOffset(0,0);
//            }
//        });
        adapter = new RecentsRVAdapter(getContext(), recentsItemFormats, (HomeActivity) getActivity() ,communityFeatures, productLinearLayoutManager, recyclerView);
        recyclerView.setAdapter(adapter);
        communityFeaturesRef.addListenerForSingleValueEvent(communityFeaturesListener);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        queryRef.addListenerForSingleValueEvent(homeListener);
        userReference.addListenerForSingleValueEvent(userListener);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_recents, menu);
    }

    @Override
    public void onDestroy() {
        queryRef.removeEventListener(homeListener);
        userReference.removeEventListener(userListener);
        communityFeaturesRef.removeEventListener(communityFeaturesListener);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

}
