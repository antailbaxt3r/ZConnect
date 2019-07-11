package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zconnect.zutto.zconnect.ExploreForumsActivity;
import com.zconnect.zutto.zconnect.JoinedForumsDiffCallback;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.DBHelper;
import com.zconnect.zutto.zconnect.holders.JoinedForumsRVViewHolder;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.ForumCategoriesItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumTypeUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class JoinedForumsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Vector<ForumCategoriesItemFormat> forumCategoriesItemFormats = new Vector<>();
    private Context context;
    StorageReference mStorage;



    public JoinedForumsAdapter(Vector<ForumCategoriesItemFormat> forumCategoriesItemFormats, Context context) {
        this.forumCategoriesItemFormats = forumCategoriesItemFormats;
        this.context = context;
        Log.e("yolo",forumCategoriesItemFormats.toString());
    }

    public JoinedForumsAdapter() {
    }

    @Override
    public int getItemViewType(int position) {
        Log.d(forumCategoriesItemFormats.get(position).getForumType(), ForumUtilities.VALUE_SHARE_FORUM_STR);
        if(forumCategoriesItemFormats.get(position).getForumType().equals(ForumTypeUtilities.KEY_EXPLORER_FORUM_STR)) {
            return ForumTypeUtilities.KEY_EXPLORE_FORUM;
        }else if (forumCategoriesItemFormats.get(position).getForumType().equals(ForumTypeUtilities.KEY_JOINED_STR)) {
            return ForumTypeUtilities.KEY_JOINED;
        }else if(forumCategoriesItemFormats.get(position).getForumType().equals(ForumUtilities.VALUE_SHARE_FORUM_STR)){
            return ForumTypeUtilities.KEY_SHARE_FORUM;
        }
        else return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if(viewType== ForumTypeUtilities.KEY_EXPLORE_FORUM){
            View createContactView = layoutInflater.inflate(R.layout.row_forums_sub_categories_explore, parent, false);
            return new JoinedForumsAdapter.exploreViewHolder(createContactView);
        }else if( viewType == ForumTypeUtilities.KEY_JOINED) {
            View joinedContactView = layoutInflater.inflate(R.layout.row_forums_sub_categories_joined, parent, false);
            return new JoinedForumsRVViewHolder(joinedContactView);
        }else if( viewType == ForumTypeUtilities.KEY_SHARE_FORUM) {
            View joinedContactView = layoutInflater.inflate(R.layout.row_forums_sub_categories_joined, parent, false);
            return new JoinedForumsRVViewHolder(joinedContactView);
        }
        else {
            View joinedContactView = layoutInflater.inflate(R.layout.row_forums_sub_categories_joined, parent, false);
            return new JoinedForumsRVViewHolder(joinedContactView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        FirebaseMessaging.getInstance().subscribeToTopic(forumCategoriesItemFormats.get(position).getCatUID());
        final ForumCategoriesItemFormat forumCategory = forumCategoriesItemFormats.get(position);

        if(forumCategory.getForumType().equals(ForumTypeUtilities.KEY_EXPLORER_FORUM_STR)){

            JoinedForumsAdapter.exploreViewHolder holderMain = (JoinedForumsAdapter.exploreViewHolder) holder;
            holderMain.exploreForums();

        }else if(forumCategory.getForumType().equals(ForumTypeUtilities.KEY_JOINED_STR)) {

            final JoinedForumsRVViewHolder holderMain = (JoinedForumsRVViewHolder) holder;
            Log.d("In here",forumCategoriesItemFormats.get(position).getTabUID().toString());
//            if(forumCategoriesItemFormats.get(position).getTabUID().toString().equals("personalChats")){
//                Log.d("In here inside",forumCategoriesItemFormats.get(position).getTabUID().toString());
//
//                final ForumCategoriesItemFormat itemFormat = forumCategoriesItemFormats.get(position);
//                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child("personalChats").child(forumCategoriesItemFormats.get(position).getCatUID());
//                db.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        DataSnapshot dataSnapshot1 = dataSnapshot.child("users");
//                        Log.d("user:details", dataSnapshot.toString());
//                        if (dataSnapshot1.getChildrenCount() == 1) {
//                            for (DataSnapshot user : dataSnapshot1.getChildren()) {
//                                ForumCategoriesItemFormat itemFormat1 = itemFormat;
//                                itemFormat1.setName(user.child("name").getValue().toString());
//                                itemFormat1.setImageThumb(user.child("imageThumb").getValue().toString());
//                                itemFormat1.setImage(user.child("imageThumb").getValue().toString());
//                                Log.d("Try:inside dataChange", user.child("name").getValue().toString());
//
//                                holderMain.setDetails(itemFormat1);
//                                holderMain.openChat(itemFormat1.getCatUID(), itemFormat1.getTabUID(), user.child("name").getValue().toString());
//
//                            }
//                        }
//
//
//
//                        for(DataSnapshot user: dataSnapshot1.getChildren()){
//                            Log.d("user:details",user.toString());
//                            if(user.child("userUID").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
//                                continue;
//                            }
//                            ForumCategoriesItemFormat itemFormat1 = itemFormat;
//                            itemFormat1.setName(user.child("name").getValue().toString());
//                            itemFormat1.setImageThumb(user.child("imageThumb").getValue().toString());
//                            itemFormat1.setImage(user.child("imageThumb").getValue().toString());
//                            Log.d("Try:inside dataChange",user.child("name").getValue().toString());
//
//                            holderMain.setDetails(itemFormat1);
//                            holderMain.openChat(itemFormat1.getCatUID(), itemFormat1.getTabUID(), user.child("name").getValue().toString());
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Log.d("error",databaseError.toString());
//                    }
//                });
//            }
//            else {
                holderMain.setDetails(forumCategoriesItemFormats.get(position));
                holderMain.openChat(forumCategoriesItemFormats.get(position).getCatUID(), forumCategoriesItemFormats.get(position).getTabUID(), forumCategoriesItemFormats.get(position).getName());
//            }

        } else if(forumCategory.getForumType().equals(ForumUtilities.VALUE_SHARE_FORUM_STR)){
            final JoinedForumsRVViewHolder holderMain = (JoinedForumsRVViewHolder) holder;
            Log.d("In here",forumCategoriesItemFormats.get(position).getTabUID().toString());
            if(forumCategoriesItemFormats.get(position).getTabUID().toString().equals("personalChats")){
                Log.d("In here inside",forumCategoriesItemFormats.get(position).getTabUID().toString());

                final ForumCategoriesItemFormat itemFormat = forumCategoriesItemFormats.get(position);
                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child("personalChats").child(forumCategoriesItemFormats.get(position).getCatUID());
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot = dataSnapshot.child("users");
                        Log.d("user:details",dataSnapshot.toString());

                        for(DataSnapshot user: dataSnapshot.getChildren()){
                            Log.d("user:details",user.toString());
                            if(user.child("userUID").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                continue;
                            }
                            ForumCategoriesItemFormat itemFormat1 = itemFormat;
                            itemFormat1.setName(user.child("name").getValue().toString());
                            itemFormat1.setImageThumb(user.child("imageThumb").getValue().toString());
                            itemFormat1.setImage(user.child("imageThumb").getValue().toString());
                            Log.d("Try:inside dataChange",user.child("name").getValue().toString());
                            holderMain.setDetailsForShare(itemFormat1);
                            holderMain.openChat(itemFormat1.getCatUID(), itemFormat1.getTabUID(), user.child("name").getValue().toString(),
                                    itemFormat1.getMessage(),itemFormat1.getMessageType());

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("error",databaseError.toString());
                    }
                });
            }
            else {
                            Log.d("Setting View",Integer.toString(position));

            holderMain.setDetailsForShare(forumCategoriesItemFormats.get(position));
            holderMain.openChat(forumCategoriesItemFormats.get(position).getCatUID()
                    , forumCategoriesItemFormats.get(position).getTabUID()
                    , forumCategoriesItemFormats.get(position).getName()
                    ,forumCategoriesItemFormats.get(position).getMessage()
                    ,forumCategoriesItemFormats.get(position).getMessageType());

            }
        }
    }

    @Override
    public int getItemCount() {
        return forumCategoriesItemFormats.size() ;
    }

    private class exploreViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout exploreForum;
        public exploreViewHolder(View itemView) {
            super(itemView);
            exploreForum = (RelativeLayout) itemView.findViewById(R.id.explore_forum);
            mStorage = FirebaseStorage.getInstance().getReference();
        }

        public void exploreForums(){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                    HashMap<String, String> meta= new HashMap<>();
                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                    counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_EXPLORE_FORUM_OPEN);
                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                    counterItemFormat.setMeta(meta);
                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                    counterPush.pushValues();
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("userType");
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Intent intent = new Intent(context, ExploreForumsActivity.class);
                            if(dataSnapshot.getValue()!=null)
                                intent.putExtra("userType", dataSnapshot.getValue().toString());
                            context.startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            });
        }
    }

    public void updateArrayListItems(ArrayList<ForumCategoriesItemFormat> forumCategoriesItemFormatList) {
        final JoinedForumsDiffCallback diffCallback = new JoinedForumsDiffCallback(new ArrayList<>(this.forumCategoriesItemFormats), forumCategoriesItemFormatList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.forumCategoriesItemFormats.clear();
        this.forumCategoriesItemFormats.addAll(forumCategoriesItemFormatList);
        diffResult.dispatchUpdatesTo(this);
    }
}
