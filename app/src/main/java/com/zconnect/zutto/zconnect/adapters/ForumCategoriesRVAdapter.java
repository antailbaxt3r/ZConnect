package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zconnect.zutto.zconnect.ChatActivity;
import com.zconnect.zutto.zconnect.addActivities.CreateForum;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.ForumCategoriesItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumsUserTypeUtilities;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.utilities.ForumTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class ForumCategoriesRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    Vector<ForumCategoriesItemFormat> forumCategoriesItemFormats;
    String tabUID;
    Boolean newUser;

    StorageReference mStorage;

    public ForumCategoriesRVAdapter(Vector<ForumCategoriesItemFormat> forumCategoriesItemFormats, Context context, String tabUID, Boolean newUser) {
        this.forumCategoriesItemFormats = forumCategoriesItemFormats;
        this.context = context;
        this.tabUID = tabUID;
        this.newUser = newUser;
    }



    @Override
    public int getItemViewType(int position) {

        if(forumCategoriesItemFormats.get(position).getForumType().equals(ForumTypeUtilities.KEY_CREATE_FORUM_STR)) {
            return ForumTypeUtilities.KEY_CREATE_FORUM;
        }else if (forumCategoriesItemFormats.get(position).getForumType().equals(ForumTypeUtilities.KEY_JOINED_STR)) {
            return ForumTypeUtilities.KEY_JOINED;
        }else if(forumCategoriesItemFormats.get(position).getForumType().equals(ForumTypeUtilities.KEY_NOT_JOINED_TITLE_STR)){
            return ForumTypeUtilities.KEY_NOT_JOINED_TITLE;
        }else if(forumCategoriesItemFormats.get(position).getForumType().equals(ForumTypeUtilities.KEY_NOT_JOINED_STR)){
            return ForumTypeUtilities.KEY_NOT_JOINED;
        }else return -1;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if(viewType== ForumTypeUtilities.KEY_CREATE_FORUM){
            View createContactView = inflater.inflate(R.layout.row_forums_sub_categories_create, parent, false);
            return new ForumCategoriesRVAdapter.createViewHolder(createContactView);
        }else if( viewType == ForumTypeUtilities.KEY_JOINED){
            View joinedContactView = inflater.inflate(R.layout.row_forums_sub_categories_joined, parent, false);
            return new ForumCategoriesRVAdapter.joinedViewHolder(joinedContactView);
        }else if(viewType== ForumTypeUtilities.KEY_NOT_JOINED_TITLE){
            View titleContactView = inflater.inflate(R.layout.row_forums_sub_categories_type, parent, false);
            return new ForumCategoriesRVAdapter.titleViewHolder(titleContactView);
        }else if(viewType == ForumTypeUtilities.KEY_NOT_JOINED){
            View notJoinedViewHolder = inflater.inflate(R.layout.row_forums_categories_not_joined,parent,false);
            return new ForumCategoriesRVAdapter.notJoinedViewHolder(notJoinedViewHolder);
        }else {
            View blankLayout = inflater.inflate(R.layout.row_blank_layout, parent, false);
            return new ForumCategoriesRVAdapter.blankViewHolder(blankLayout);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final ForumCategoriesItemFormat forumCategory = forumCategoriesItemFormats.get(position);

        if(forumCategory.getForumType().equals(ForumTypeUtilities.KEY_CREATE_FORUM_STR)){

            createViewHolder holderMain = (createViewHolder) holder;
            holderMain.createForum(tabUID,newUser);
//            holderMain.createForumText.setTextColor(context.getResources().getColor(R.color.secondaryText));

        }else if(forumCategory.getForumType().equals(ForumTypeUtilities.KEY_JOINED_STR)){

            FirebaseMessaging.getInstance().subscribeToTopic(forumCategoriesItemFormats.get(position).getCatUID());

            joinedViewHolder holderMain = (joinedViewHolder) holder;
            holderMain.catName.setText(forumCategoriesItemFormats.get(position).getName());

            if(!newUser) {
                holderMain.setUnSeenMessages(forumCategoriesItemFormats.get(position).getTotalMessages(), forumCategoriesItemFormats.get(position).getSeenMessages());
            }else {
                holderMain.layoutUnseenMessages.setVisibility(View.GONE);
            }
            if(forumCategoriesItemFormats.get(position).getImageThumb()!=null)
            {
                holderMain.defaultForumIcon.setVisibility(View.GONE);
                holderMain.forumIcon.setImageURI(forumCategoriesItemFormats.get(position).getImageThumb());
            }
            else {
                holderMain.forumIcon.setImageResource(android.R.color.transparent);
                holderMain.defaultForumIcon.setVisibility(View.VISIBLE);
                holderMain.forumIcon.setBackground(context.getResources().getDrawable(R.drawable.forum_circle));
            }
            try {
                holderMain.lastMessageWithName.setVisibility(View.VISIBLE);
                holderMain.lastMessageTime.setVisibility(View.VISIBLE);
                String shortName = forumCategoriesItemFormats.get(position).getLastMessage().getName();
                if(shortName.indexOf(' ')>0)
                    shortName = shortName.substring(0, shortName.indexOf(' '));
                holderMain.lastMessageWithName.setText(shortName + ": " + forumCategoriesItemFormats.get(position).getLastMessage().getMessage().substring(1, forumCategoriesItemFormats.get(position).getLastMessage().getMessage().length() - 1));
                holderMain.lastMessageTime.setText(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, Locale.US).format(forumCategoriesItemFormats.get(position).getLastMessage().getTimeDate()));
                String timeStamp = new TimeUtilities().getTimeStamp(forumCategoriesItemFormats.get(position).getLastMessage().getTimeDate());
                if(timeStamp.length()!=0)
                {
                    holderMain.lastMessageTime.setText(timeStamp);
                }

            }
            catch (Exception e) {
                Log.d("Error alert ", e.getMessage());
                holderMain.lastMessageWithName.setVisibility(View.INVISIBLE);
                holderMain.lastMessageTime.setVisibility(View.INVISIBLE);
            }

            try {
                if(forumCategoriesItemFormats.get(position).getVerified())
                {
                    holderMain.verifiedForumIconLayout.setVisibility(View.VISIBLE);
                }
                else {
                    holderMain.verifiedForumIconLayout.setVisibility(View.GONE);
                }
            }
            catch (Exception e)
            {
                holderMain.verifiedForumIconLayout.setVisibility(View.GONE);
            }

            holderMain.openChat(forumCategoriesItemFormats.get(position).getCatUID(), forumCategoriesItemFormats.get(position).getTabUID(), forumCategoriesItemFormats.get(position).getName(),newUser);
            holderMain.catName.setTextColor(context.getResources().getColor(R.color.primaryText));

        }else if(forumCategory.getForumType().equals(ForumTypeUtilities.KEY_NOT_JOINED_TITLE_STR)){

            titleViewHolder holderMain = (titleViewHolder) holder;
            holderMain.setTitle("Not Joined");

        }else if(forumCategory.getForumType().equals(ForumTypeUtilities.KEY_NOT_JOINED_STR)){

            FirebaseMessaging.getInstance().unsubscribeFromTopic(forumCategoriesItemFormats.get(position).getCatUID());
            notJoinedViewHolder holderMain = (notJoinedViewHolder) holder;
            holderMain.catName.setText(forumCategoriesItemFormats.get(position).getName());
            if(forumCategoriesItemFormats.get(position).getImageThumb()!=null)
            {
                holderMain.defaultForumIcon.setVisibility(View.GONE);
                holderMain.forumIcon.setImageURI(forumCategoriesItemFormats.get(position).getImageThumb());
            }
            else {
                holderMain.forumIcon.setImageResource(android.R.color.transparent);
                holderMain.defaultForumIcon.setVisibility(View.VISIBLE);
                holderMain.forumIcon.setBackground(context.getResources().getDrawable(R.drawable.forum_circle));
            }


            try {
                if(forumCategoriesItemFormats.get(position).getVerified())
                {
                    holderMain.verifiedForumIconLayout.setVisibility(View.VISIBLE);
                }
                else {
                    holderMain.verifiedForumIconLayout.setVisibility(View.GONE);
                }
            }
            catch (Exception e)
            {
                holderMain.verifiedForumIconLayout.setVisibility(View.GONE);
            }
            holderMain.openChat(forumCategoriesItemFormats.get(position).getCatUID(), forumCategoriesItemFormats.get(position).getTabUID(), forumCategoriesItemFormats.get(position).getName(),newUser);
            holderMain.catName.setTextColor(context.getResources().getColor(R.color.primaryText));
            holderMain.joinForum(forumCategoriesItemFormats.get(position).getCatUID(),forumCategoriesItemFormats.get(position).getName());

            try {
                if(forumCategoriesItemFormats.get(position).getTotalMembers()!=null) {
                    holderMain.setTotalMembers(forumCategoriesItemFormats.get(position).getTotalMembers().toString() + " members");
                }else {
                    holderMain.setTotalMembers("");
                }
            }catch (Exception e){}
        }else {
            blankViewHolder holderMain = (blankViewHolder) holder;

        }
    }

    @Override
    public int getItemCount() {
        return forumCategoriesItemFormats.size();
    }

    private class createViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout createForum;
        public createViewHolder(View itemView) {
            super(itemView);
            createForum = (RelativeLayout) itemView.findViewById(R.id.create_forum);
            mStorage = FirebaseStorage.getInstance().getReference();
        }

        public void createForum(final String uid,Boolean newUser){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                    HashMap<String, String> meta= new HashMap<>();
                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                    counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_CREATE_FORUM_OPEN);
                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                    counterItemFormat.setMeta(meta);
                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                    counterPush.pushValues();
                    final Intent intent = new Intent(context, CreateForum.class);
                    intent.putExtra("uid", uid);
                    intent.putExtra("flag", "false");
                    context.startActivity(intent);
                }
            });

            if(newUser){
                itemView.setVisibility(View.GONE);
                itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }
        }
    }

    private class joinedViewHolder extends RecyclerView.ViewHolder {

        TextView catName, lastMessageTime, lastMessageWithName;
        TextView unSeenMessages;
        View mView;
        LinearLayout forumRowItem, layoutUnseenMessages;
        SimpleDraweeView forumIcon;
        ImageView defaultForumIcon;
        FrameLayout verifiedForumIconLayout;

        public joinedViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            catName = (TextView) itemView.findViewById(R.id.cat_name);
            lastMessageTime = (TextView) itemView.findViewById(R.id.forums_cat_last_message_timestamp);
            lastMessageWithName = (TextView) itemView.findViewById(R.id.forums_cat_last_message_with_username);

            unSeenMessages = (TextView) itemView.findViewById(R.id.forums_unseen_messages);
            layoutUnseenMessages = (LinearLayout) itemView.findViewById(R.id.layout_forums_unseen_messages);

            forumIcon = (SimpleDraweeView) itemView.findViewById(R.id.forums_group_icon_row_forums_sub_categories_joined);
            defaultForumIcon = (ImageView) itemView.findViewById(R.id.default_forums_group_icon_row_forums_sub_categories_joined);
            verifiedForumIconLayout = (FrameLayout) itemView.findViewById(R.id.verified_forum_icon_layout);

            //changing fonts
//            Typeface ralewayMedium = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Medium.ttf");
//            Typeface ralewayRegular = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
//            catName.setTypeface(ralewayMedium);
//            lastMessageTime.setTypeface(ralewayRegular);
//            lastMessageWithName.setTypeface(ralewayRegular);
        }

        void openChat(final String uid, final String tabId, final String  name, Boolean newUser){

            if(!newUser) {
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ChatActivity.class);

                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                        HashMap<String, String> meta = new HashMap<>();
                        meta.put("type", "fromFeature");
                        meta.put("channelType", "joined");
                        meta.put("channelID", uid);
                        meta.put("catID", tabId);

                        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                        counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_CHANNEL_OPEN);
                        counterItemFormat.setTimestamp(System.currentTimeMillis());
                        counterItemFormat.setMeta(meta);

                        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                        counterPush.pushValues();

                        intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(uid).toString());
                        intent.putExtra("type", "forums");
                        intent.putExtra("name", name);
                        intent.putExtra("tab", tabUID);
                        intent.putExtra("key", uid);

                        context.startActivity(intent);
                    }
                });
            }

        }

        void setUnSeenMessages(Integer totalMessages,Integer readMessages){

            if(totalMessages-readMessages>0){
                layoutUnseenMessages.setVisibility(View.VISIBLE);
                unSeenMessages.setText((totalMessages-readMessages) + "");
                lastMessageTime.setTextColor(context.getResources().getColor(R.color.colorHighlight));
            }else {
                layoutUnseenMessages.setVisibility(View.INVISIBLE);
                lastMessageTime.setTextColor(context.getResources().getColor(R.color.secondaryText));
            }

        }
    }

    private class titleViewHolder extends RecyclerView.ViewHolder{
        TextView titleText;
        public titleViewHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.topicText);
        }

        public void setTitle(String title){
            titleText.setText(title);
        }
    }

    private class notJoinedViewHolder extends RecyclerView.ViewHolder{
        TextView catName,totalMembers;
        View mView;
        Button joinButton;
        ImageView defaultForumIcon;
        SimpleDraweeView forumIcon;
        LinearLayout forumRowItem;
        FrameLayout verifiedForumIconLayout;

        public notJoinedViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            catName = (TextView) itemView.findViewById(R.id.cat_name);
            joinButton = (Button) itemView.findViewById(R.id.joinCategory);
            totalMembers = (TextView) itemView.findViewById(R.id.forums_cat_not_joined_total_members);
            forumIcon = (SimpleDraweeView) itemView.findViewById(R.id.forums_group_icon_row_forums_categories_not_joined);
            defaultForumIcon = (ImageView) itemView.findViewById(R.id.default_forums_group_icon_row_forums_categories_not_joined);
            verifiedForumIconLayout = (FrameLayout) itemView.findViewById(R.id.verified_forum_icon_layout);

            //changing fonts
//            Typeface ralewayMedium = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Medium.ttf");
//            Typeface ralewayRegular = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
//            catName.setTypeface(ralewayMedium);
//            lastMessageTime.setTypeface(ralewayRegular);
        }

        void setTotalMembers(String totalMembersInteger){
            totalMembers.setText(totalMembersInteger + " members");
        }

        void openChat(final String uid, final String tabId, final String  name,Boolean newUser){

            if(!newUser) {
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                        HashMap<String, String> meta = new HashMap<>();
                        meta.put("type", "fromFeature");
                        meta.put("channelType", "notJoined");
                        meta.put("channelID", uid);
                        meta.put("catID", tabId);

                        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                        counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_CHANNEL_OPEN);
                        counterItemFormat.setTimestamp(System.currentTimeMillis());
                        counterItemFormat.setMeta(meta);

                        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                        counterPush.pushValues();

                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(uid).toString());
                        intent.putExtra("type", "forums");
                        intent.putExtra("name", name);
                        intent.putExtra("tab", tabUID);
                        intent.putExtra("key", uid);

                        context.startActivity(intent);
                    }
                });
            }
        }

        public void joinForum(final String key,final String name){

            final DatabaseReference forumCategory = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(tabUID).child(key);

            joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                    HashMap<String, String> meta= new HashMap<>();

                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                    meta.put("type","fromFeature");
                    counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_JOINED);

                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                    counterItemFormat.setMeta(meta);
                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                    counterPush.pushValues();
                    final UsersListItemFormat userDetails = new UsersListItemFormat();
                    DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot2) {
                            UserItemFormat userItemFormat = dataSnapshot2.getValue(UserItemFormat.class);
                            userDetails.setImageThumb(userItemFormat.getImageURLThumbnail());
                            userDetails.setName(userItemFormat.getUsername());
                            userDetails.setPhonenumber(userItemFormat.getMobileNumber());
                            userDetails.setUserUID(userItemFormat.getUserUID());
                            userDetails.setUserType(ForumsUserTypeUtilities.KEY_USER);

                            forumCategory.child("users").child(userItemFormat.getUserUID()).setValue(userDetails);

//                            Intent intent = new Intent(context, ChatActivity.class);
//                            intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(key).toString());
//                            intent.putExtra("type", "forums");
//                            intent.putExtra("name", name);
//                            intent.putExtra("tab", tabUID);
//                            intent.putExtra("key", key);
//                            context.startActivity(intent);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

        }
    }

    public static class  blankViewHolder extends  RecyclerView.ViewHolder{

        public blankViewHolder(View itemView) {
            super(itemView);
        }
    }
}