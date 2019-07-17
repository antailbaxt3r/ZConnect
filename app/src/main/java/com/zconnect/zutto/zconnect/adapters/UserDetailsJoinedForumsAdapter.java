package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.flags.Flag;
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
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.holders.JoinedForumsRVViewHolder;
import com.zconnect.zutto.zconnect.holders.UserDetailsJoinedForumsRVViewHolder;
import com.zconnect.zutto.zconnect.holders.otherForumsRVViewHolder;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.ForumCategoriesItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumTypeUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class UserDetailsJoinedForumsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ForumCategoriesItemFormat> forumCategoriesItemFormats = new ArrayList<>();



    public UserDetailsJoinedForumsAdapter(ArrayList<ForumCategoriesItemFormat> forumCategoriesItemFormats) {
        this.forumCategoriesItemFormats = forumCategoriesItemFormats;
        Log.e("yolo", forumCategoriesItemFormats.toString());
    }


    @Override
    public int getItemViewType(int position) {
        Log.d(forumCategoriesItemFormats.get(position).getForumType(), ForumUtilities.VALUE_SHARE_FORUM_STR);
            return ForumTypeUtilities.KEY_JOINED;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == ForumTypeUtilities.KEY_JOINED) {
            View view = layoutInflater.inflate(R.layout.user_detail_joined_forum_item, parent, false);
            return new UserDetailsJoinedForumsRVViewHolder(view);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
//        FirebaseMessaging.getInstance().subscribeToTopic(forumCategoriesItemFormats.get(position).getCatUID());
        final ForumCategoriesItemFormat forumCategory = forumCategoriesItemFormats.get(position);


            final UserDetailsJoinedForumsRVViewHolder holderMain = (UserDetailsJoinedForumsRVViewHolder) holder;
            try{
            Log.d("In here", forumCategoriesItemFormats.get(position).getTabUID().toString());
            holderMain.setDetails(forumCategoriesItemFormats.get(position));
            holderMain.openChat(forumCategoriesItemFormats.get(position).getCatUID(), forumCategoriesItemFormats.get(position).getTabUID(), forumCategoriesItemFormats.get(position).getName());
            }
            catch( Exception e){
                Log.d("ERROR",position+" "+e.toString());
                }
//            }


    }

    @Override
    public int getItemCount() {
        return forumCategoriesItemFormats.size();
    }



}
