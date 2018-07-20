package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.forumCategoriesItemFormat;
import com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.utilities.ForumTypeUtilities;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Vector;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class ForumCategoriesRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    Vector<forumCategoriesItemFormat> forumCategoriesItemFormats;
    String tabUID;

    StorageReference mStorage;

    public ForumCategoriesRVAdapter(Vector<forumCategoriesItemFormat> forumCategoriesItemFormats, Context context, String tabUID) {
        this.forumCategoriesItemFormats = forumCategoriesItemFormats;
        this.context = context;
        this.tabUID = tabUID;
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

        final forumCategoriesItemFormat forumCategory = forumCategoriesItemFormats.get(position);

        if(forumCategory.getForumType().equals(ForumTypeUtilities.KEY_CREATE_FORUM_STR)){

            createViewHolder holderMain = (createViewHolder) holder;
            holderMain.createForum(tabUID);
//            holderMain.createForumText.setTextColor(context.getResources().getColor(R.color.secondaryText));

        }else if(forumCategory.getForumType().equals(ForumTypeUtilities.KEY_JOINED_STR)){

            FirebaseMessaging.getInstance().subscribeToTopic(forumCategoriesItemFormats.get(position).getCatUID());

            joinedViewHolder holderMain = (joinedViewHolder) holder;
            holderMain.catName.setText(forumCategoriesItemFormats.get(position).getName());
            if(forumCategoriesItemFormats.get(position).getImageThumb()!=null)
            {
                holderMain.forumIcon.setImageURI(forumCategoriesItemFormats.get(position).getImageThumb());
                holderMain.defaultForumIcon.setVisibility(View.GONE);
            }
            try {
                holderMain.lastMessageMessage.setText(forumCategoriesItemFormats.get(position).getLastMessage().getMessage().substring(1, forumCategoriesItemFormats.get(position).getLastMessage().getMessage().length() - 1));
                holderMain.lastMessageUsername.setText(forumCategoriesItemFormats.get(position).getLastMessage().getName().substring(0, forumCategoriesItemFormats.get(position).getLastMessage().getName().indexOf(' ')) + " :");
                holderMain.lastMessageTime.setText(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, Locale.US).format(forumCategoriesItemFormats.get(position).getLastMessage().getTimeDate()));
            }
            catch (Exception e) {
                Log.d("Error alert ", e.getMessage());
                holderMain.lastMessageMessage.setVisibility(View.GONE);
                holderMain.lastMessageUsername.setVisibility(View.GONE);
                holderMain.lastMessageTime.setVisibility(View.GONE);
            }

            holderMain.openChat(forumCategoriesItemFormats.get(position).getCatUID(), forumCategoriesItemFormats.get(position).getTabUID(), forumCategoriesItemFormats.get(position).getName());
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
                holderMain.forumIcon.setImageURI(forumCategoriesItemFormats.get(position).getImageThumb());
                holderMain.defaultForumIcon.setVisibility(View.GONE);
            }

            try {
                holderMain.lastMessageMessage.setText(forumCategoriesItemFormats.get(position).getLastMessage().getMessage().substring(1, forumCategoriesItemFormats.get(position).getLastMessage().getMessage().length() - 1));
                holderMain.lastMessageUsername.setText(forumCategoriesItemFormats.get(position).getLastMessage().getName().substring(0, forumCategoriesItemFormats.get(position).getLastMessage().getName().indexOf(' ')) + " :");
                holderMain.lastMessageTime.setText(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, Locale.US).format(forumCategoriesItemFormats.get(position).getLastMessage().getTimeDate()));
            }
            catch (Exception e) {
                Log.d("Error alert ", e.getMessage());
                holderMain.lastMessageMessage.setVisibility(View.GONE);
                holderMain.lastMessageUsername.setVisibility(View.GONE);
                holderMain.lastMessageTime.setVisibility(View.GONE);
            }

            holderMain.openChat(forumCategoriesItemFormats.get(position).getCatUID(), forumCategoriesItemFormats.get(position).getTabUID(), forumCategoriesItemFormats.get(position).getName());
            holderMain.catName.setTextColor(context.getResources().getColor(R.color.primaryText));
            holderMain.joinForum(forumCategoriesItemFormats.get(position).getCatUID(),forumCategoriesItemFormats.get(position).getName());

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

        public void createForum(final String uid){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(context, CreateForum.class);
                    intent.putExtra("uid", uid);
                    context.startActivity(intent);
                }
            });
        }
    }

    private class joinedViewHolder extends RecyclerView.ViewHolder {

        TextView catName, lastMessageMessage, lastMessageUsername, lastMessageTime;
        View mView;
        LinearLayout forumRowItem;
        SimpleDraweeView forumIcon;
        ImageView defaultForumIcon;

        public joinedViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            catName = (TextView) itemView.findViewById(R.id.cat_name);
            lastMessageMessage = (TextView) itemView.findViewById(R.id.forums_cat_last_message);
            lastMessageUsername = (TextView) itemView.findViewById(R.id.forums_cat_last_message_username);
            lastMessageTime = (TextView) itemView.findViewById(R.id.forums_cat_last_message_timestamp);
            forumIcon = (SimpleDraweeView) itemView.findViewById(R.id.forums_group_icon_row_forums_sub_categories_joined);
            defaultForumIcon = (ImageView) itemView.findViewById(R.id.default_forums_group_icon_row_forums_sub_categories_joined);

            //changing fonts
            Typeface ralewayMedium = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Medium.ttf");
            Typeface ralewayRegular = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
            catName.setTypeface(ralewayMedium);
            lastMessageMessage.setTypeface(ralewayRegular);
            lastMessageUsername.setTypeface(ralewayRegular);
            lastMessageTime.setTypeface(ralewayRegular);
        }

        void openChat(final String uid, final String tabId, final String  name){
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(uid).toString());
                    intent.putExtra("type","forums");
                    intent.putExtra("name", name);
                    intent.putExtra("tab",tabUID);
                    intent.putExtra("key",uid);

                    context.startActivity(intent);
                }
            });

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
        TextView catName, lastMessageMessage, lastMessageUsername, lastMessageTime;
        View mView;
        ImageButton joinButton;
        ImageView defaultForumIcon;
        SimpleDraweeView forumIcon;
        LinearLayout forumRowItem;

        public notJoinedViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            catName = (TextView) itemView.findViewById(R.id.cat_name);
            lastMessageMessage = (TextView) itemView.findViewById(R.id.forums_cat_last_message);
            lastMessageUsername = (TextView) itemView.findViewById(R.id.forums_cat_last_message_username);
            lastMessageTime = (TextView) itemView.findViewById(R.id.forums_cat_last_message_timestamp);
            joinButton = (ImageButton) itemView.findViewById(R.id.joinCategory);
            forumIcon = (SimpleDraweeView) itemView.findViewById(R.id.forums_group_icon_row_forums_categories_not_joined);
            defaultForumIcon = (ImageView) itemView.findViewById(R.id.default_forums_group_icon_row_forums_categories_not_joined);

            //changing fonts
            Typeface ralewayMedium = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Medium.ttf");
            Typeface ralewayRegular = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
            catName.setTypeface(ralewayMedium);
            lastMessageMessage.setTypeface(ralewayRegular);
            lastMessageUsername.setTypeface(ralewayRegular);
            lastMessageTime.setTypeface(ralewayRegular);
        }

        void openChat(final String uid, final String tabId, final String  name){
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(uid).toString());
                    intent.putExtra("type","forums");
                    intent.putExtra("name", name);
                    intent.putExtra("tab",tabUID);
                    intent.putExtra("key",uid);

                    context.startActivity(intent);
                }
            });

        }

        public void joinForum(final String key,final String name){

            final DatabaseReference forumCategory = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(tabUID).child(key);

            joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                            forumCategory.child("users").child(userItemFormat.getUserUID()).setValue(userDetails);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

        }
    }

    private class  blankViewHolder extends  RecyclerView.ViewHolder{

        public blankViewHolder(View itemView) {
            super(itemView);
        }
    }
}