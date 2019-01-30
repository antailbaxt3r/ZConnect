package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.addActivities.CreateForum;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.holders.JoinedForumsRVViewHolder;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.ForumCategoriesItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;
import com.zconnect.zutto.zconnect.utilities.UserUtilities;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
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

        if(forumCategoriesItemFormats.get(position).getForumType().equals(ForumTypeUtilities.KEY_EXPLORER_FORUM_STR)) {
            return ForumTypeUtilities.KEY_EXPLORE_FORUM;
        }else if (forumCategoriesItemFormats.get(position).getForumType().equals(ForumTypeUtilities.KEY_JOINED_STR)) {
            return ForumTypeUtilities.KEY_JOINED;
        }else return -1;
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
        }else {
            View joinedContactView = layoutInflater.inflate(R.layout.row_forums_sub_categories_joined, parent, false);
            return new JoinedForumsRVViewHolder(joinedContactView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FirebaseMessaging.getInstance().subscribeToTopic(forumCategoriesItemFormats.get(position).getCatUID());
        final ForumCategoriesItemFormat forumCategory = forumCategoriesItemFormats.get(position);

        if(forumCategory.getForumType().equals(ForumTypeUtilities.KEY_EXPLORER_FORUM_STR)){

            JoinedForumsAdapter.exploreViewHolder holderMain = (JoinedForumsAdapter.exploreViewHolder) holder;
            holderMain.exploreForums();

        }else if(forumCategory.getForumType().equals(ForumTypeUtilities.KEY_JOINED_STR)) {

            JoinedForumsRVViewHolder holderMain = (JoinedForumsRVViewHolder) holder;
            holderMain.setDetails(forumCategoriesItemFormats.get(position));
            holderMain.openChat(forumCategoriesItemFormats.get(position).getCatUID(), forumCategoriesItemFormats.get(position).getTabUID(), forumCategoriesItemFormats.get(position).getName());

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
}
