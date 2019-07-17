package com.zconnect.zutto.zconnect.holders;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.zutto.zconnect.ChatActivity;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.ForumCategoriesItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumUtilities;
import com.zconnect.zutto.zconnect.utilities.MessageTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class UserDetailsJoinedForumsRVViewHolder extends RecyclerView.ViewHolder {
    private String TAG = JoinedForumsRVViewHolder.class.getSimpleName();
    TextView catName;
    View mView;
    SimpleDraweeView forumIcon;
    ImageView defaultForumIcon;
    private int unseen_num;
    public UserDetailsJoinedForumsRVViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        catName = (TextView) itemView.findViewById(R.id.forum_name);
        forumIcon = (SimpleDraweeView) itemView.findViewById(R.id.forum_image);
        defaultForumIcon = (ImageView) itemView.findViewById(R.id.default_forums_group_icon_row_forums_sub_categories_joined);
    }

    public void setDetails(ForumCategoriesItemFormat forumCategoriesItemFormat){

        catName.setText(forumCategoriesItemFormat.getName());
//        Log.d("NOTIFICATIONNAME",forumCategoriesItemFormat.getName());

        if(forumCategoriesItemFormat.getImageThumb()!=null)
        {
            defaultForumIcon.setVisibility(View.GONE);
            forumIcon.setImageURI(forumCategoriesItemFormat.getImageThumb());
        } else {
            forumIcon.setImageResource(android.R.color.transparent);
            defaultForumIcon.setVisibility(View.VISIBLE);
            forumIcon.setBackground(mView.getContext().getResources().getDrawable(R.drawable.forum_circle));
        }




        catName.setTextColor(mView.getContext().getResources().getColor(R.color.primaryText));
    }
    public void openChat(final String uid, final String tabId, final String  name ){

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mView.getContext(), ChatActivity.class);

                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta= new HashMap<>();
                meta.put("type","fromFeature");
                meta.put("channelType","joined");
                meta.put("channelID",uid);
                meta.put("catID",tabId);

                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_CHANNEL_OPEN);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);

                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();

                intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(uid).toString());
                intent.putExtra("type","forums");
                intent.putExtra("name", name);
                intent.putExtra("tab",tabId);
                intent.putExtra("key",uid);
                intent.putExtra("unseen_num", String.valueOf(unseen_num));
                mView.getContext().startActivity(intent);
            }
        });

    }



    public void openChat(final String uid, final String tabId, final String name, final String message, final String messageType) {
        Log.d("inOpenChat",name);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mView.getContext(), ChatActivity.class);

                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta= new HashMap<>();
                meta.put("type","fromFeature");
                meta.put("channelType","joined");
                meta.put("channelID",uid);
                meta.put("catID",tabId);

                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_CHANNEL_OPEN);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);

                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();

                intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(uid).toString());
                intent.putExtra("type","forums");
                intent.putExtra("name", name);
                intent.putExtra("tab",tabId);
                intent.putExtra("key",uid);
                intent.putExtra("unseen_num", String.valueOf(unseen_num));
                intent.putExtra(ForumUtilities.KEY_MESSAGE_TYPE_STR,messageType);
                intent.putExtra(ForumUtilities.KEY_MESSAGE,message);
                mView.getContext().startActivity(intent);
            }
        });

    }
}
