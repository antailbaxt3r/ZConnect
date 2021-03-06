package com.zconnect.zutto.zconnect.holders;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
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
import com.zconnect.zutto.zconnect.OnSingleClickListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.DBHelper;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.ForumCategoriesItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumUtilities;
import com.zconnect.zutto.zconnect.utilities.MessageTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class JoinedForumsRVViewHolder extends RecyclerView.ViewHolder {
    private String TAG = JoinedForumsRVViewHolder.class.getSimpleName();
    TextView catName, lastMessageTime, lastMessageWithName;
    TextView unSeenMessages;
    View mView;
    LinearLayout forumRowItem, layoutUnseenMessages;
    SimpleDraweeView forumIcon;
    ImageView defaultForumIcon;
    FrameLayout verifiedForumIconLayout;
    private int unseen_num;

    private boolean isShareToForum = false;
    public JoinedForumsRVViewHolder(View itemView) {
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
    }

    public void setDetails(ForumCategoriesItemFormat forumCategoriesItemFormat){

        catName.setText(forumCategoriesItemFormat.getName());
//        Log.d("NOTIFICATIONNAME",forumCategoriesItemFormat.getName());
        setUnSeenMessages(forumCategoriesItemFormat.getTotalMessages(),forumCategoriesItemFormat.getSeenMessages());

        if(forumCategoriesItemFormat.getImageThumb()!=null)
        {
            defaultForumIcon.setVisibility(View.GONE);
            forumIcon.setImageURI(forumCategoriesItemFormat.getImageThumb());
        } else {
           forumIcon.setImageResource(android.R.color.transparent);
            defaultForumIcon.setVisibility(View.VISIBLE);
            forumIcon.setBackground(mView.getContext().getResources().getDrawable(R.drawable.forum_circle));
        }

        if(forumCategoriesItemFormat.getLastMessage().getMessageType().equals(MessageTypeUtilities.KEY_ANONYMOUS_MESSAGE_STR)){
            lastMessageWithName.setVisibility(View.VISIBLE);
            lastMessageWithName.setText("Anonymous");
            lastMessageTime.setText(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, Locale.US).format(forumCategoriesItemFormat.getLastMessage().getTimeDate()));
        }
        else {

            try {
                lastMessageWithName.setVisibility(View.VISIBLE);
                lastMessageTime.setVisibility(View.VISIBLE);
                String shortName = forumCategoriesItemFormat.getLastMessage().getName();
                if (shortName.indexOf(' ') > 0) {
                    shortName = shortName.substring(0, shortName.indexOf(' '));
                }



                        String messageText = forumCategoriesItemFormat.getLastMessage().getMessage();
                        messageText = messageText.substring(1,messageText.length()-1);
                        //TODO IMPROVE EXTRACTION OF USERNAME AND UID
                        String newMessageText = "", token = "";
                        ArrayList<Integer> startIndexList = new ArrayList<>();
                        ArrayList<Integer> endIndexList = new ArrayList<>();
                        ArrayList<String> uid = new ArrayList<>();

                        int startIndex = 0;
                        int endIndex = 0;
                        boolean isToken = false;
                        try {
                            for (int i = 0; i < messageText.length(); i++) {
                                char letter = messageText.charAt(i);
                                if (letter == '@') {
                                    startIndex = i;
                                    isToken = true;
                                } else if (letter == '~') {
                                    endIndex = i;
                                    newMessageText += token;
                                    token = "";
                                } else if (letter == ';') {
                                    startIndexList.add(newMessageText.length() - endIndex + startIndex);
                                    endIndexList.add(newMessageText.length());
                                    Log.d("logtokrn", token);
                                    uid.add(token.substring(1));
                                    startIndex = 0;
                                    endIndex = 0;
                                    token = "";
                                    isToken = false;
                                    continue;
                                }

                                if (isToken) {
                                    token += letter;
                                } else {
                                    newMessageText += letter;
                                }

                            }
                        }catch (Exception e){
                            lastMessageWithName.setText(shortName + ": " + forumCategoriesItemFormat.getLastMessage().getMessage().substring(1, forumCategoriesItemFormat.getLastMessage().getMessage().length() - 1));

                        }

                lastMessageWithName.setText(shortName + ": " + newMessageText);
                lastMessageTime.setText(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, Locale.US).format(forumCategoriesItemFormat.getLastMessage().getTimeDate()));
                String timeStamp = new TimeUtilities().getTimeStamp(forumCategoriesItemFormat.getLastMessage().getTimeDate());

                if (timeStamp.length() != 0) {
                    lastMessageTime.setText(timeStamp);
                }

            } catch (Exception e) {
                Log.d("Error alert ", e.getMessage());
                lastMessageWithName.setVisibility(View.INVISIBLE);
                lastMessageTime.setVisibility(View.INVISIBLE);
            }

        }
        try
        {
            if(forumCategoriesItemFormat.getVerified())
            {
                Log.d(TAG, forumCategoriesItemFormat.getCatUID());
                Log.d(TAG, forumCategoriesItemFormat.getVerified().toString());
                verifiedForumIconLayout.setVisibility(View.VISIBLE);
            }
            else {
                verifiedForumIconLayout.setVisibility(View.GONE);
            }
        }
        catch (Exception e)
        {
            verifiedForumIconLayout.setVisibility(View.GONE);
        }

        catName.setTextColor(mView.getContext().getResources().getColor(R.color.primaryText));
    }
    public void openChat(final String uid, final String tabId, final String  name, final int totalMessages){

        mView.setOnClickListener(new OnSingleClickListener() {

            @Override
            public void onSingleClick(View view) {

                Intent intent = new Intent(mView.getContext(), ChatActivity.class);

                intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(uid).toString());
                intent.putExtra("type","forums");
                intent.putExtra("name", name);
                intent.putExtra("tab",tabId);
                intent.putExtra("key",uid);
                intent.putExtra("unseen_num", String.valueOf(unseen_num));
                intent.putExtra("isShareToForum", isShareToForum);
                mView.getContext().startActivity(intent);

                layoutUnseenMessages.setVisibility(View.GONE);

                lastMessageTime.setTextColor(mView.getContext().getResources().getColor(R.color.secondaryText));
                DBHelper mydb = new DBHelper(itemView.getContext());
                String key, tab;
                key = uid;
                tab = tabId;
                mydb.replaceForum(name, key, tab,totalMessages);
                mydb.close();

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
            }
        });

    }

    void setUnSeenMessages(Integer totalMessages,Integer readMessages){
        Log.d("NOTIFICATIONTOTAL",totalMessages.toString());
        Log.d("NOTIFICATIONREAD",readMessages.toString());


        if(totalMessages-readMessages>0){
            layoutUnseenMessages.setVisibility(View.VISIBLE);
            unSeenMessages.setText((totalMessages-readMessages) + "");
            unseen_num = totalMessages - readMessages;
            lastMessageTime.setTextColor(mView.getContext().getResources().getColor(R.color.colorHighlight));
        }else {
            layoutUnseenMessages.setVisibility(View.GONE);
            lastMessageTime.setTextColor(mView.getContext().getResources().getColor(R.color.secondaryText));
        }

    }

    public void setDetailsForShare(ForumCategoriesItemFormat forumCategoriesItemFormat, boolean isShareToForum) {
        this.isShareToForum = isShareToForum;
        catName.setText(forumCategoriesItemFormat.getName());
        Log.d("InsideSetDetails",catName.getText().toString());
        try
        {
            if(forumCategoriesItemFormat.getVerified())
            {
                Log.d(TAG, forumCategoriesItemFormat.getCatUID());
                Log.d(TAG, forumCategoriesItemFormat.getVerified().toString());
                verifiedForumIconLayout.setVisibility(View.VISIBLE);
            }
            else {
                verifiedForumIconLayout.setVisibility(View.GONE);
            }
        }
        catch (Exception e)
        {
            verifiedForumIconLayout.setVisibility(View.GONE);
        }

        if(forumCategoriesItemFormat.getImageThumb()!=null)
        {
            defaultForumIcon.setVisibility(View.GONE);
            forumIcon.setImageURI(forumCategoriesItemFormat.getImageThumb());
        } else {
            forumIcon.setImageResource(android.R.color.transparent);
            defaultForumIcon.setVisibility(View.VISIBLE);
            forumIcon.setBackground(mView.getContext().getResources().getDrawable(R.drawable.forum_circle));
        }
        unSeenMessages.setVisibility(View.GONE);
        lastMessageTime.setVisibility(View.GONE);
        lastMessageWithName.setVisibility(View.GONE);
        layoutUnseenMessages.setVisibility(View.GONE);

        catName.setTextColor(mView.getContext().getResources().getColor(R.color.primaryText));

    }

    public void openChat(final String uid, final String tabId, final String name, final String message, final String messageType) {
        Log.d("inOpenChat",name+"");
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
                intent.putExtra("isShareToForum", isShareToForum);
                intent.putExtra(ForumUtilities.KEY_MESSAGE_TYPE_STR,messageType);
                intent.putExtra(ForumUtilities.KEY_MESSAGE,message);
                mView.getContext().startActivity(intent);
                if(isShareToForum) {
                    ((Activity) mView.getContext()).finish();
                }
            }

        });

    }
}
