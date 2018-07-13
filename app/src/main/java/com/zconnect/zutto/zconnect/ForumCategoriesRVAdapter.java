package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zconnect.zutto.zconnect.ItemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.forumCategoriesItemFormat;
import com.zconnect.zutto.zconnect.Utilities.forumTypeUtilities;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Vector;

import static com.zconnect.zutto.zconnect.BaseActivity.communityReference;

/**
 * Created by shubhamk on 9/2/17.
 */

public class ForumCategoriesRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    Vector<forumCategoriesItemFormat> forumCategoriesItemFormats;
    String tabUID;

    public ForumCategoriesRVAdapter(Vector<forumCategoriesItemFormat> forumCategoriesItemFormats, Context context, String tabUID) {
        this.forumCategoriesItemFormats = forumCategoriesItemFormats;
        this.context = context;
        this.tabUID = tabUID;
    }

    @Override
    public int getItemViewType(int position) {

        if(forumCategoriesItemFormats.get(position).getForumType().equals(forumTypeUtilities.KEY_CREATE_FORUM_STR)) {
            return forumTypeUtilities.KEY_CREATE_FORUM;
        }else if (forumCategoriesItemFormats.get(position).getForumType().equals(forumTypeUtilities.KEY_JOINED_STR)) {
            return forumTypeUtilities.KEY_JOINED;
        }else if(forumCategoriesItemFormats.get(position).getForumType().equals(forumTypeUtilities.KEY_NOT_JOINED_TITLE_STR)){
            return forumTypeUtilities.KEY_NOT_JOINED_TITLE;
        }else if(forumCategoriesItemFormats.get(position).getForumType().equals(forumTypeUtilities.KEY_NOT_JOINED_STR)){
            return forumTypeUtilities.KEY_NOT_JOINED;
        }else return -1;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if(viewType== forumTypeUtilities.KEY_CREATE_FORUM){
            View createContactView = inflater.inflate(R.layout.row_forums_sub_categories_create, parent, false);
            return new ForumCategoriesRVAdapter.createViewHolder(createContactView);
        }else if( viewType == forumTypeUtilities.KEY_JOINED){
            View joinedContactView = inflater.inflate(R.layout.row_forums_sub_categories_joined, parent, false);
            return new ForumCategoriesRVAdapter.joinedViewHolder(joinedContactView);
        }else if(viewType== forumTypeUtilities.KEY_NOT_JOINED_TITLE){
            View titleContactView = inflater.inflate(R.layout.row_forums_sub_categories_type, parent, false);
            return new ForumCategoriesRVAdapter.titleViewHolder(titleContactView);
        }else if(viewType == forumTypeUtilities.KEY_NOT_JOINED){
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

        if(forumCategory.getForumType().equals(forumTypeUtilities.KEY_CREATE_FORUM_STR)){

            createViewHolder holderMain = (createViewHolder) holder;
            holderMain.createForum(tabUID);
//            holderMain.createForumText.setTextColor(context.getResources().getColor(R.color.secondaryText));

        }else if(forumCategory.getForumType().equals(forumTypeUtilities.KEY_JOINED_STR)){

            joinedViewHolder holderMain = (joinedViewHolder) holder;
            holderMain.catName.setText(forumCategoriesItemFormats.get(position).getName());
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

        }else if(forumCategory.getForumType().equals(forumTypeUtilities.KEY_NOT_JOINED_TITLE_STR)){

            titleViewHolder holderMain = (titleViewHolder) holder;
            holderMain.setTitle("Not Joined");

        }else if(forumCategory.getForumType().equals(forumTypeUtilities.KEY_NOT_JOINED_STR)){

            notJoinedViewHolder holderMain = (notJoinedViewHolder) holder;
            holderMain.catName.setText(forumCategoriesItemFormats.get(position).getName());

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
        }

        public void createForum(final String uid){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Enter Title");
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View view = inflater.inflate(R.layout.create_forum_alert, null);
                    builder.setView(view);
                    final MaterialEditText addForumName = (MaterialEditText) view.findViewById(R.id.add_name_create_forum_alert);
                    final FrameLayout addForumIcon = (FrameLayout) view.findViewById(R.id.add_icon_create_forum_alert);
//                    final EditText input = new EditText(context);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//                    input.setInputType(InputType.TYPE_CLASS_TEXT);
//                    builder.setView(input);

                    builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference tabName= FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabs").child(uid);
                            tabName.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d("FORUM NAME", addForumName.getText().toString());
//                                    addCategory(addForumName.getText().toString(),uid,dataSnapshot.child("name").getValue().toString());
//                                    addCategory(addForumIcon.get);
//                                    addCategory(input.getText().toString(),uid,dataSnapshot.child("name").getValue().toString());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });


                    builder.show();
                }
            });
        }

        public void addCategory(String catName, String uid, final String tabName){

            DatabaseReference databaseReferenceCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories");
            DatabaseReference databaseReferenceTabsCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(uid);
            final DatabaseReference databaseReferenceHome = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home");

            final DatabaseReference newPush=databaseReferenceCategories.push();
            DatabaseReference mPostedByDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            newPush.child("name").setValue(catName);
            Long postTimeMillis = System.currentTimeMillis();
            newPush.child("PostTimeMillis").setValue(postTimeMillis);
            newPush.child("UID").setValue(newPush.getKey());
            newPush.child("tab").setValue(uid);
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
                    newPush.child("users").child(userItemFormat.getUserUID()).setValue(userDetails);
                    CounterManager.forumsAddCategory(tabName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            databaseReferenceTabsCategories.child(newPush.getKey()).child("name").setValue(catName);
            databaseReferenceTabsCategories.child(newPush.getKey()).child("catUID").setValue(newPush.getKey());
            databaseReferenceTabsCategories.child(newPush.getKey()).child("tabUID").setValue(uid);


            //Home

            databaseReferenceHome.child(newPush.getKey()).child("feature").setValue("Forums");
            databaseReferenceHome.child(newPush.getKey()).child("name").setValue(catName);
            databaseReferenceHome.child(newPush.getKey()).child("id").setValue(uid);
            databaseReferenceHome.child(newPush.getKey()).child("desc").setValue(tabName);
            databaseReferenceHome.child(newPush.getKey()).child("Key").setValue(newPush.getKey());
            databaseReferenceHome.child(newPush.getKey()).child("PostTimeMillis").setValue(postTimeMillis);

            databaseReferenceHome.child(newPush.getKey()).child("PostedBy").child("UID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

            mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    newPush.child("PostedBy").child("Username").setValue(dataSnapshot.child("username").getValue().toString());
                    newPush.child("PostedBy").child("ImageThumb").setValue(dataSnapshot.child("imageURLThumbnail").getValue().toString());

                    databaseReferenceHome.child(newPush.getKey()).child("PostedBy").child("Username").setValue(dataSnapshot.child("username").getValue().toString());
                    databaseReferenceHome.child(newPush.getKey()).child("PostedBy").child("ImageThumb").setValue(dataSnapshot.child("imageURLThumbnail").getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private class joinedViewHolder extends RecyclerView.ViewHolder {

        TextView catName, lastMessageMessage, lastMessageUsername, lastMessageTime;
        View mView;
        LinearLayout forumRowItem;

        public joinedViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            catName = (TextView) itemView.findViewById(R.id.cat_name);
            lastMessageMessage = (TextView) itemView.findViewById(R.id.forums_cat_last_message);
            lastMessageUsername = (TextView) itemView.findViewById(R.id.forums_cat_last_message_username);
            lastMessageTime = (TextView) itemView.findViewById(R.id.forums_cat_last_message_timestamp);

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
        LinearLayout forumRowItem;

        public notJoinedViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            catName = (TextView) itemView.findViewById(R.id.cat_name);
            lastMessageMessage = (TextView) itemView.findViewById(R.id.forums_cat_last_message);
            lastMessageUsername = (TextView) itemView.findViewById(R.id.forums_cat_last_message_username);
            lastMessageTime = (TextView) itemView.findViewById(R.id.forums_cat_last_message_timestamp);
            joinButton = (ImageButton) itemView.findViewById(R.id.joinCategory);

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

                            NotificationSender notificationSender=new NotificationSender(key,name,FirebaseAuth.getInstance().getCurrentUser().getUid(),null,null,null,userItemFormat.getUsername(),KeyHelper.KEY_FORUMS_JOIN,false,true,itemView.getContext());
                            notificationSender.execute();
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