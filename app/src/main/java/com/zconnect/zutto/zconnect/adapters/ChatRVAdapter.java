package com.zconnect.zutto.zconnect.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ChatActivity;
import com.zconnect.zutto.zconnect.commonModules.DBHelper;
import com.zconnect.zutto.zconnect.custom.MentionsClickableSpan;
import com.zconnect.zutto.zconnect.holders.EmptyRVViewHolder;
import com.zconnect.zutto.zconnect.holders.otherForumsRVViewHolder;
import com.zconnect.zutto.zconnect.itemFormats.ChatItemFormats;
import com.zconnect.zutto.zconnect.OpenUserDetail;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.utilities.ForumUtilities;
import com.zconnect.zutto.zconnect.utilities.MessageTypeUtilities;
import com.zconnect.zutto.zconnect.commonModules.viewImage;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<ChatItemFormats> chatFormats;
    Context ctx;

    private DatabaseReference databaseref;
    private DatabaseReference forumRef;
    private boolean isLastMessage = false ;
    private ValueEventListener loadMessagesListener;
    private ChatItemFormats delMessage;
    private ChatItemFormats delphoto;
    private String forumType;

    public ChatRVAdapter(ArrayList<ChatItemFormats> chatFormats, DatabaseReference databaseref,DatabaseReference reference,Context ctx, String forumType) {
        this.chatFormats = chatFormats;
        this.ctx = ctx;
        this.databaseref = databaseref;
        this.forumRef = reference;
        this.forumType = forumType;
    }

    @Override
    public int getItemViewType(int position) {

        if(chatFormats.get(position).getMessageType().equals(MessageTypeUtilities.KEY_MESSAGE_STR)){
            return MessageTypeUtilities.KEY_MESSAGE;
        }else if (chatFormats.get(position).getMessageType().equals(MessageTypeUtilities.KEY_PHOTO_STR)){
            return MessageTypeUtilities.KEY_PHOTO;
        }else if(chatFormats.get(position).getMessageType().equals(MessageTypeUtilities.KEY_POLL_STR)){
            return MessageTypeUtilities.KEY_POLL;
        }else if(chatFormats.get(position).getMessageType().equals(MessageTypeUtilities.KEY_QUESTION_STR)){
            return MessageTypeUtilities.KEY_QUESTION;
        }else if(chatFormats.get(position).getMessageType().equals(MessageTypeUtilities.KEY_SHOP_MESSAGE_STR)){
            return MessageTypeUtilities.KEY_SHOP_MESSAGE;
        }else if(chatFormats.get(position).getMessageType().equals(MessageTypeUtilities.KEY_SHOP_PHOTO_STR)){
            return MessageTypeUtilities.KEY_SHOP_PHOTO;
        }else if(chatFormats.get(position).getMessageType().equals(MessageTypeUtilities.KEY_ANONYMOUS_MESSAGE_STR)){
            return MessageTypeUtilities.KEY_ANONYMOUS_MESSAGE;
        }else if(chatFormats.get(position).getMessageType().equals(MessageTypeUtilities.KEY_MATCHED_MESSAGE_STR)){
            return MessageTypeUtilities.KEY_MATCHED_MESSAGE;
        }
//        else if(chatFormats.get(position).getMessageType().equals(MessageTypeUtilities.KEY_PHOTO_SENDING_STR)){
//            return MessageTypeUtilities.KEY_PHOTO_SENDING;
//        }
        else{
            return -1;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == MessageTypeUtilities.KEY_MESSAGE) {
            View messageContactView = inflater.inflate(R.layout.chat_message_format, parent, false);
            return new messageViewHolder(messageContactView, parent.getContext());
        } else if (viewType == MessageTypeUtilities.KEY_PHOTO) {
            View photoContactView = inflater.inflate(R.layout.chat_photo_format, parent, false);
            return new photoViewHolder(photoContactView, parent.getContext());
        } else if (viewType == MessageTypeUtilities.KEY_SHOP_MESSAGE) {
            View photoContactView = inflater.inflate(R.layout.chat_shop_message_format, parent, false);
            return new messageShopViewHolder(photoContactView, parent.getContext());
        } else if (viewType == MessageTypeUtilities.KEY_SHOP_PHOTO) {
            View photoContactView = inflater.inflate(R.layout.chat_shop_photo_format, parent, false);
            return new photoShopViewHolder(photoContactView, parent.getContext());
        } else if(viewType == MessageTypeUtilities.KEY_ANONYMOUS_MESSAGE){
            View messageContactView = inflater.inflate(R.layout.chat_message_format, parent, false);
            return new messageViewHolder(messageContactView, parent.getContext());
        } else if(viewType == MessageTypeUtilities.KEY_MATCHED_MESSAGE){
            View messageContactView = inflater.inflate(R.layout.chat_message_format, parent, false);
            return new messageViewHolder(messageContactView, parent.getContext());
        }
//        else if(viewType == MessageTypeUtilities.KEY_PHOTO_SENDING){
//            View photoContactView = inflater.inflate(R.layout.chat_photo_format, parent, false);
//            return new photoViewHolder(photoContactView, parent.getContext());
//        }
        else{
            View update_view = inflater.inflate(R.layout.row_other_forums, parent, false);
            return new otherForumsRVViewHolder(update_view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rvHolder, final int position) {
        rvHolder.itemView.setTag(position);

        final ChatItemFormats message = chatFormats.get(position);

        if(message.getMessageType().equals("message")){
            ((messageViewHolder) rvHolder).anonymousImage.setVisibility(View.GONE);


            messageViewHolder holder = (messageViewHolder) rvHolder;
            holder.usernameLayout.setVisibility(View.VISIBLE);
            holder.messageBubble.setVisibility(View.VISIBLE);
            holder.anonymousButton.setVisibility(View.GONE);
            long previousTs = 0;
            if(position>=1){
                ChatItemFormats pm = chatFormats.get(position-1);
                previousTs = pm.getTimeDate();
            }
            setTimeTextVisibility(message.getTimeDate(), previousTs, holder.timeGroupText);
            holder.message.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);

            if(message.getUuid()!=null)
            {

                if(position > 0 && chatFormats.get(position - 1).getUuid().equals(message.getUuid()))
                {
                    holder.name.setVisibility(View.GONE);
                }

                if(forumType.equals(ForumUtilities.VALUE_COMMENTS)){
                    holder.rightDummy.setVisibility(View.VISIBLE);
                    holder.usernameLayout.setGravity(Gravity.START);

                    holder.leftDummy.setVisibility(View.GONE);
                    holder.messageBubble.setBackground(holder.context.getResources().getDrawable(R.drawable.message_box));
                    holder.chatContainer.setGravity(Gravity.START);
                    holder.userAvatar.setVisibility(View.VISIBLE);
                    holder.name.setVisibility(View.VISIBLE);
                    holder.name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(ctx,OpenUserDetail.class);
                            i.putExtra("Uid",message.getUuid());
                            ctx.startActivity(i);
                        }
                    });
                }
                else{

                if(message.getUuid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    holder.rightDummy.setVisibility(View.GONE);
                    holder.leftDummy.setVisibility(View.VISIBLE);
                    holder.messageBubble.setBackground(holder.context.getResources().getDrawable(R.drawable.message_box_self));
                    holder.chatContainer.setGravity(Gravity.END);
                    holder.chatLayout.setGravity(Gravity.END);
                    holder.userAvatar.setVisibility(View.GONE);
                    holder.name.setVisibility(View.GONE);
                }
                else
                {
                    holder.rightDummy.setVisibility(View.VISIBLE);
                    holder.leftDummy.setVisibility(View.GONE);
                    holder.usernameLayout.setGravity(Gravity.START);

                    holder.messageBubble.setBackground(holder.context.getResources().getDrawable(R.drawable.message_box));
                    holder.chatContainer.setGravity(Gravity.START);
                    holder.userAvatar.setVisibility(View.VISIBLE);
                    holder.name.setVisibility(View.VISIBLE);
                    holder.name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(ctx,OpenUserDetail.class);
                            i.putExtra("Uid",message.getUuid());
                            ctx.startActivity(i);
                        }
                    });
                }
                }
                String time = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, Locale.US).format(message.getTimeDate());
                holder.time.setText(time);
                holder.name.setText(message.getName());
                holder.userAvatar.setImageURI(message.getImageThumb());

                String messageText = message.getMessage();
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
                            startIndexList.add(newMessageText.length()-endIndex+startIndex);
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
                    SpannableString spannableString = new SpannableString(newMessageText);
                    int i = 0;
                    for (String u : uid) {
                        spannableString.setSpan(new MentionsClickableSpan(holder.itemView.getContext(), u), startIndexList.get(i), endIndexList.get(i), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        i++;
                    }
                    holder.message.setText(spannableString);
                    holder.message.setMovementMethod(LinkMovementMethod.getInstance());
                    holder.message.setHighlightColor(Color.TRANSPARENT);
                    Linkify.addLinks(holder.message, Linkify.ALL);

                }
                catch (Exception e){
                    Log.e("MYERROR",e.toString());
                    holder.message.setText("Unable to load the message");

                }



            }

            if(forumType.equals(ForumUtilities.VALUE_ANONYMOUS_FORUM)){
                holder.message.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.white));
                holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.white));
                holder.time.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.white));
                holder.messageBubble.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.message_box_dark_mode));
            }
            else{
                holder.message.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black));
                holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black));
                holder.time.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black));
                holder.messageBubble.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.message_box));

            }
        }
        else if(message.getMessageType().equals("photo")) {


            final photoViewHolder holder = (photoViewHolder) rvHolder;
            long previousTs = 0;
            if(position>=1){
                ChatItemFormats pm = chatFormats.get(position-1);
                previousTs = pm.getTimeDate();
            }

            setTimeTextVisibility(message.getTimeDate(), previousTs, holder.timeGroupText);
            if(message.getUuid()!=null) {

                if (position > 0 && chatFormats.get(position - 1).getUuid().equals(message.getUuid())) {
                    holder.name.setVisibility(View.GONE);
                }
                if(forumType.equals(ForumUtilities.VALUE_COMMENTS)){
                    holder.rightDummy.setVisibility(View.VISIBLE);
                    holder.leftDummy.setVisibility(View.GONE);
                    holder.messageBubble.setBackground(holder.context.getResources().getDrawable(R.drawable.message_box));
                    holder.chatContainer.setGravity(Gravity.START);
                    holder.userAvatar.setVisibility(View.VISIBLE);
                    holder.name.setVisibility(View.VISIBLE);
                    holder.name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(ctx, OpenUserDetail.class);
                            i.putExtra("Uid", message.getUuid());
                            ctx.startActivity(i);
                        }
                    });
                }
                else{
                if (message.getUuid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    holder.rightDummy.setVisibility(View.GONE);
                    holder.leftDummy.setVisibility(View.VISIBLE);
                    holder.messageBubble.setBackground(holder.context.getResources().getDrawable(R.drawable.message_box_self));
                    holder.chatContainer.setGravity(Gravity.END);
                    holder.userAvatar.setVisibility(View.GONE);
                    holder.name.setVisibility(View.GONE);
                } else {
                    holder.rightDummy.setVisibility(View.VISIBLE);
                    holder.leftDummy.setVisibility(View.GONE);
                    holder.messageBubble.setBackground(holder.context.getResources().getDrawable(R.drawable.message_box));
                    holder.chatContainer.setGravity(Gravity.START);
                    holder.userAvatar.setVisibility(View.VISIBLE);
                    holder.name.setVisibility(View.VISIBLE);
                    holder.name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(ctx, OpenUserDetail.class);
                            i.putExtra("Uid", message.getUuid());
                            ctx.startActivity(i);
                        }
                    });
                }
                }
                String time = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, Locale.US).format(message.getTimeDate());
                holder.time.setText(time);
                holder.name.setText(message.getName());
                holder.userAvatar.setImageURI(message.getImageThumb());
                holder.photo.setBackground(holder.context.getResources().getDrawable(R.drawable.photo_background_chat));
                holder.photo.setImageURI(message.getPhotoURL());
                holder.photo.setClipToOutline(true);

                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ProgressDialog mProgress = new ProgressDialog(ctx);
                        mProgress.setMessage("Loading...");
                        mProgress.show();
                        animate((Activity)ctx, "Image", message.getPhotoURL(), holder.photo);
                        mProgress.dismiss();
                    }
                });
            }

            if(forumType.equals(ForumUtilities.VALUE_ANONYMOUS_FORUM)){
                holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.white));
                holder.time.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.white));
                holder.messageBubble.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.message_box_dark_mode));
            }
            else{
                holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black));
                holder.time.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black));
                holder.messageBubble.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.message_box));

            }
        }
        else if(message.getMessageType().equals(MessageTypeUtilities.KEY_SHOP_MESSAGE_STR)){

            messageShopViewHolder holder = (messageShopViewHolder) rvHolder;

            long previousTs = 0;
            if(position>=1){
                ChatItemFormats pm = chatFormats.get(position-1);
                previousTs = pm.getTimeDate();
            }
            holder.message.setTypeface(holder.message.getTypeface(),Typeface.NORMAL);
            holder.messageBubble.setVisibility(View.VISIBLE);

            setTimeTextVisibility(message.getTimeDate(), previousTs, holder.timeGroupText);
            if(message.getUuid()!=null)
            {

                if(position > 0 && chatFormats.get(position - 1).getUuid().equals(message.getUuid()))
                {
                    holder.name.setVisibility(View.GONE);
                }
                if(forumType.equals(ForumUtilities.VALUE_COMMENTS)){
                    holder.rightDummy.setVisibility(View.VISIBLE);
                    holder.leftDummy.setVisibility(View.GONE);
                    holder.messageBubble.setBackground(holder.context.getResources().getDrawable(R.drawable.message_box));
                    holder.chatContainer.setGravity(Gravity.START);
                    holder.userAvatar.setVisibility(View.VISIBLE);
                    holder.name.setVisibility(View.VISIBLE);
                }
                else {
                    if (message.getUuid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        holder.rightDummy.setVisibility(View.GONE);
                        holder.leftDummy.setVisibility(View.VISIBLE);
                        holder.messageBubble.setBackground(holder.context.getResources().getDrawable(R.drawable.message_box_self));
                        holder.chatContainer.setGravity(Gravity.END);
                        holder.userAvatar.setVisibility(View.GONE);
                        holder.name.setVisibility(View.GONE);
                    } else {
                        holder.rightDummy.setVisibility(View.VISIBLE);
                        holder.leftDummy.setVisibility(View.GONE);
                        holder.messageBubble.setBackground(holder.context.getResources().getDrawable(R.drawable.message_box));
                        holder.chatContainer.setGravity(Gravity.START);
                        holder.userAvatar.setVisibility(View.VISIBLE);
                        holder.name.setVisibility(View.VISIBLE);

                    }
                }
                String time = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, Locale.US).format(message.getTimeDate());
                holder.time.setText(time);
                holder.name.setText(message.getName()+"(Shop Owner)");
                holder.userAvatar.setImageURI(message.getImageThumb());

                String messageText = message.getMessage();

                messageText = messageText.substring(1,messageText.length()-1);
                holder.message.setText(messageText);
                Linkify.addLinks(holder.message, Linkify.ALL);
            }



            if(forumType.equals(ForumUtilities.VALUE_ANONYMOUS_FORUM)){
                holder.message.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.white));
                holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.white));
                holder.time.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.white));
                holder.messageBubble.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.message_box_dark_mode));
            }
            else{
                holder.message.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black));
                holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black));
                holder.time.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black));
                holder.messageBubble.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.message_box));

            }
        }
        else if(message.getMessageType().equals(MessageTypeUtilities.KEY_SHOP_PHOTO_STR)) {


            final photoShopViewHolder holder = (photoShopViewHolder) rvHolder;
            long previousTs = 0;
            if(position>=1){
                ChatItemFormats pm = chatFormats.get(position-1);
                previousTs = pm.getTimeDate();
            }
            setTimeTextVisibility(message.getTimeDate(), previousTs, holder.timeGroupText);
            if(message.getUuid()!=null) {

                if (position > 0 && chatFormats.get(position - 1).getUuid().equals(message.getUuid())) {
                    holder.name.setVisibility(View.GONE);
                }
                if(forumType.equals(ForumUtilities.VALUE_COMMENTS)) {
                    holder.rightDummy.setVisibility(View.VISIBLE);
                    holder.leftDummy.setVisibility(View.GONE);
                    holder.messageBubble.setBackground(holder.context.getResources().getDrawable(R.drawable.message_box));
                    holder.chatContainer.setGravity(Gravity.START);
                    holder.userAvatar.setVisibility(View.VISIBLE);
                    holder.name.setVisibility(View.VISIBLE);

                }
                else {
                    if (message.getUuid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        holder.rightDummy.setVisibility(View.GONE);
                        holder.leftDummy.setVisibility(View.VISIBLE);
                        holder.messageBubble.setBackground(holder.context.getResources().getDrawable(R.drawable.message_box_self));
                        holder.chatContainer.setGravity(Gravity.END);
                        holder.userAvatar.setVisibility(View.GONE);
                        holder.name.setVisibility(View.GONE);
                    } else {
                        holder.rightDummy.setVisibility(View.VISIBLE);
                        holder.leftDummy.setVisibility(View.GONE);
                        holder.messageBubble.setBackground(holder.context.getResources().getDrawable(R.drawable.message_box));
                        holder.chatContainer.setGravity(Gravity.START);
                        holder.userAvatar.setVisibility(View.VISIBLE);
                        holder.name.setVisibility(View.VISIBLE);

                    }
                }
                String time = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, Locale.US).format(message.getTimeDate());
                holder.time.setText(time);
                holder.name.setText(message.getName()+"(Shop Owner)");
                holder.userAvatar.setImageURI(message.getImageThumb());
                holder.photo.setBackground(holder.context.getResources().getDrawable(R.drawable.photo_background_chat));
                holder.photo.setImageURI(message.getPhotoURL());
                holder.photo.setClipToOutline(true);

                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ProgressDialog mProgress = new ProgressDialog(ctx);
                        mProgress.setMessage("Loading...");
                        mProgress.show();
                        animate((Activity)ctx, "Image", message.getPhotoURL(), holder.photo);
                        mProgress.dismiss();
                    }
                });
            }


            if(forumType.equals(ForumUtilities.VALUE_ANONYMOUS_FORUM)){
                holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.white));
                holder.time.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.white));
                holder.messageBubble.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.message_box_dark_mode));
            }
            else{
                holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black));
                holder.time.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black));
                holder.messageBubble.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.message_box));

            }
        }
        else if(message.getMessageType().equals(MessageTypeUtilities.KEY_ANONYMOUS_MESSAGE_STR)){
            messageViewHolder holder = (messageViewHolder) rvHolder;
            holder.usernameLayout.setVisibility(View.VISIBLE);
            holder.messageBubble.setVisibility(View.VISIBLE);
            holder.anonymousButton.setVisibility(View.VISIBLE);

            long previousTs = 0;
            if(position>=1){
                ChatItemFormats pm = chatFormats.get(position-1);
                previousTs = pm.getTimeDate();
            }
            setTimeTextVisibility(message.getTimeDate(), previousTs, holder.timeGroupText);
            if(message.getUuid()!=null)
            {
                    holder.name.setVisibility(View.GONE);
                    if(forumType.equals(ForumUtilities.VALUE_COMMENTS)){
                        holder.rightDummy.setVisibility(View.VISIBLE);
                        holder.leftDummy.setVisibility(View.GONE);
                        holder.usernameLayout.setGravity(Gravity.START);
                        holder.messageBubble.setBackground(holder.context.getResources().getDrawable(R.drawable.message_box));
                        holder.chatContainer.setGravity(Gravity.START);
                        holder.name.setVisibility(View.VISIBLE);
                        holder.name.setText(message.getUserName());
//                        holder.name.setTextColor(R.color.);
//                        holder.chatLayout.addView(holder.anonymousButton);
//                        holder.chatLayout.setGravity(Gravity.START);
                        holder.usernameLayout.setGravity(Gravity.START);
                        holder.anonymousImage.setVisibility(View.VISIBLE);
                    }
                    else {
                        if (message.getUuid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            holder.name.setVisibility(View.GONE);
                            holder.usernameLayout.setVisibility(View.GONE);
                            holder.chatLayout.removeAllViews();
                            holder.chatLayout.addView(holder.anonymousButton);
                            holder.chatLayout.addView(holder.messageBubble);
                            holder.chatLayout.setGravity(Gravity.END);
//                            holder.usernameLayout.setGravity(Gravity.END);
                            holder.anonymousImage.setVisibility(View.GONE);
//                            holder.name.setGravity(Gravity.END);
                            holder.rightDummy.setVisibility(View.GONE);
                            holder.leftDummy.setVisibility(View.VISIBLE);
                            holder.messageBubble.setBackground(holder.context.getResources().getDrawable(R.drawable.message_box_self));
                            holder.chatContainer.setGravity(Gravity.END);

                        } else {
                            holder.name.setVisibility(View.VISIBLE);
                            holder.name.setText(message.getUserName());
                            holder.chatLayout.removeAllViews();
                            holder.chatLayout.addView(holder.messageBubble);
                            holder.chatLayout.addView(holder.anonymousButton);
                            holder.chatLayout.setGravity(Gravity.START);
                            holder.usernameLayout.setGravity(Gravity.START);
                            holder.anonymousImage.setVisibility(View.VISIBLE);
                            holder.rightDummy.setVisibility(View.VISIBLE);
                            holder.leftDummy.setVisibility(View.GONE);
                            holder.messageBubble.setBackground(holder.context.getResources().getDrawable(R.drawable.message_box));
                            holder.chatContainer.setGravity(Gravity.START);

                        }
                    }
                holder.userAvatar.setVisibility(View.GONE);


                String time = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, Locale.US).format(message.getTimeDate());
                holder.time.setText(time);
                holder.message.setText("Message is hidden");
                holder.message.setTypeface(holder.message.getTypeface(),Typeface.BOLD_ITALIC);
                Linkify.addLinks(holder.message, Linkify.ALL);
            }
            if(forumType.equals(ForumUtilities.VALUE_ANONYMOUS_FORUM)){
                holder.message.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.white));
                holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.white));
                holder.time.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.white));
                holder.messageBubble.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.message_box_dark_mode));
                if(position>=1){
                    ChatItemFormats pm = chatFormats.get(position-1);
                    previousTs = pm.getTimeDate();
                }
                String messageText = message.getMessage();
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
                            startIndexList.add(newMessageText.length()-endIndex+startIndex);
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
                    SpannableString spannableString = new SpannableString(newMessageText);
                    int i = 0;
                    for (String u : uid) {
                        spannableString.setSpan(new MentionsClickableSpan(holder.itemView.getContext(), u), startIndexList.get(i), endIndexList.get(i), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        i++;
                    }
                    holder.message.setText(spannableString);
                    holder.message.setMovementMethod(LinkMovementMethod.getInstance());
                    holder.message.setHighlightColor(Color.TRANSPARENT);
                    Linkify.addLinks(holder.message, Linkify.ALL);
                    holder.name.setEnabled(false);
                    holder.name.setText(message.getUserName());
                    holder.userAvatar.setVisibility(View.GONE);
                    holder.message.setTypeface(holder.message.getTypeface(),Typeface.BOLD_ITALIC);
                    holder.name.setText(message.getUserName());
                    holder.name.setVisibility(View.VISIBLE);
                    holder.userAvatar.setVisibility(View.GONE);

                }
                catch (Exception e){
                    Log.e("MYERROR",e.toString());
                    holder.message.setText("Unable to load the message");

                }

            }
            else{
                holder.message.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black));
                holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black));
                holder.time.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black));
                holder.messageBubble.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.message_box));

            }
            holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black));


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String messageText = message.getMessage();
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
                                startIndexList.add(newMessageText.length()-endIndex+startIndex);
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
                        SpannableString spannableString = new SpannableString(newMessageText);
                        int i = 0;
                        for (String u : uid) {
                            spannableString.setSpan(new MentionsClickableSpan(holder.itemView.getContext(), u), startIndexList.get(i), endIndexList.get(i), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            i++;
                        }
                        holder.message.setText(spannableString);
                        holder.message.setMovementMethod(LinkMovementMethod.getInstance());
                        holder.message.setTypeface(holder.message.getTypeface(),Typeface.BOLD_ITALIC);
                        holder.message.setHighlightColor(Color.TRANSPARENT);
                        holder.anonymousButton.setVisibility(View.GONE);
                        Linkify.addLinks(holder.message, Linkify.ALL);

                    }
                    catch (Exception e){
                        Log.e("MYERROR",e.toString());
                        holder.message.setText("Unable to load the message");

                    }


//                    if(ctx instanceof ChatActivity){
//                       if(forumType.equals(ForumUtilities.VALUE_ANONYMOUS_FORUM)){
//                           return;
//                       }
//                       else {
//                           ((ChatActivity) ctx).setAnonymousChat();
//                       }
//                   }


                }
            });

        }
        else if(message.getMessageType().equals(MessageTypeUtilities.KEY_MATCHED_MESSAGE_STR)){
            ((messageViewHolder) rvHolder).anonymousImage.setVisibility(View.GONE);

            messageViewHolder holder = (messageViewHolder) rvHolder;
            long previousTs = 0;

            holder.time.setVisibility(View.GONE);
            holder.timeGroupText.setText(message.getMessage());
            holder.timeGroupText.setVisibility(View.VISIBLE);
            holder.usernameLayout.setVisibility(View.GONE);
            holder.messageBubble.setVisibility(View.GONE);
            holder.message.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);
            holder.chatContainer.setVisibility(View.GONE);


            if(forumType.equals(ForumUtilities.VALUE_ANONYMOUS_FORUM)){
                holder.message.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.white));
                holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.white));
                holder.time.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.white));
//                holder.messageBubble.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.message_box_dark_mode));
            }
            else{
                holder.message.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black));
                holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black));
                holder.time.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black));
//                holder.messageBubble.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.message_box));

            }

        }
        else{
            final otherForumsRVViewHolder otherForumsRVViewHolder = (otherForumsRVViewHolder) rvHolder;
            otherForumsRVViewHolder.itemView.setOnClickListener(v -> {
                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.zconnect.zutto.zconnect"));
                ctx.startActivity(i);
            });
        }




    }

    private void setTimeTextVisibility(long ts1, long ts2, TextView timeText){

        DateTimeZone indianZone = DateTimeZone.forID("Asia/Kolkata");
        if(ts2==0){
            timeText.setVisibility(View.VISIBLE);
            DateTime dt = new DateTime(ts1, indianZone);
            DateTime date = new DateTime(indianZone);
            if(dt.getYearOfEra()==date.getYearOfEra() && dt.getMonthOfYear() == date.getMonthOfYear() && dt.getDayOfMonth() == date.getDayOfMonth())
            {
                timeText.setText("Today");
            }
            else if(dt.getYearOfEra()==date.getYearOfEra() && dt.getMonthOfYear() == date.getMonthOfYear() && dt.getDayOfMonth() == date.getDayOfMonth()-1)
            {
                timeText.setText("Yesterday");
            }
            else {
                timeText.setText(dt.toString("MMMM") + " " + dt.getDayOfMonth() + " " + dt.getYearOfEra());
            }
        }else {
            timeText.setVisibility(View.GONE);
            DateTime dt1 = new DateTime(ts1, indianZone);
            DateTime dt2 = new DateTime(ts2, indianZone);
            boolean sameDay = (dt1.getYearOfEra() == dt2.getYearOfEra()) && (dt1.getMonthOfYear() == dt2.getMonthOfYear()) && (dt1.getDayOfMonth() == dt2.getDayOfMonth());
            if(sameDay){
                timeText.setVisibility(View.GONE);
                timeText.setText("");
            }else {
                timeText.setVisibility(View.VISIBLE);
                DateTime dt = new DateTime(ts1, indianZone);
                DateTime date = new DateTime(indianZone);
                if(dt.getYearOfEra()==date.getYearOfEra() && dt.getMonthOfYear() == date.getMonthOfYear() && dt.getDayOfMonth() == date.getDayOfMonth())
                {
                    timeText.setText("Today");
                }
                else if(dt.getYearOfEra()==date.getYearOfEra() && dt.getMonthOfYear() == date.getMonthOfYear() && dt.getDayOfMonth() == date.getDayOfMonth()-1)
                {
                    timeText.setText("Yesterday");
                }
                else {
                    timeText.setText(dt.toString("MMMM") + " " + dt.getDayOfMonth() + " " + dt.getYearOfEra());
                }
            }

        }
    }


    public void animate(final Activity activity, final String name, String url, ImageView productImage) {
        final Intent i = new Intent(ctx, viewImage.class);
        i.putExtra("currentEvent", name);
        i.putExtra("eventImage", url);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, productImage, ctx.getResources().getString(R.string.transition_string));

        ctx.startActivity(i, optionsCompat.toBundle());
    }

    @Override
    public int getItemCount() {

        Log.d("Came hear","ITEMCOUNT");
        return chatFormats.size();
    }

    class messageViewHolder extends RecyclerView.ViewHolder {

        TextView message, name, time, timeGroupText;
        LinearLayout usernameLayout;
        SimpleDraweeView userAvatar;
        LinearLayout rightDummy, leftDummy, messageBubble, chatContainer, chatItem, chatLayout;
        Context context;
        ImageView anonymousImage, anonymousButton;

        public messageViewHolder(final View itemView, final Context context) {
            super(itemView);
            this.context = context;
            userAvatar = (SimpleDraweeView) itemView.findViewById(R.id.chat_format_user_avatar);
            name = (TextView) itemView.findViewById(R.id.chat_format_name);
            message = (TextView) itemView.findViewById(R.id.chat_format_message);
            time = (TextView) itemView.findViewById(R.id.chat_format_timestamp);
            timeGroupText = (TextView) itemView.findViewById(R.id.time_group_text_chat_message_format);
            chatItem = (LinearLayout) itemView.findViewById(R.id.chat_format_chat_item);
            chatContainer =(LinearLayout) itemView.findViewById(R.id.chat_format_chat_container);
            leftDummy = (LinearLayout) itemView.findViewById(R.id.chat_format_leftdummy);
            rightDummy = (LinearLayout) itemView.findViewById(R.id.chat_format_rightdummy);
            messageBubble = (LinearLayout) itemView.findViewById(R.id.chat_format_message_bubble);
            anonymousImage = itemView.findViewById(R.id.anonymous_image);
            usernameLayout = itemView.findViewById(R.id.username_layout);
            chatLayout = itemView.findViewById(R.id.chat_layout_ll);
            anonymousButton = itemView.findViewById(R.id.anonymous_icon);

            Typeface quicksandRegular = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Quicksand-Regular.ttf");
            Typeface quicksandBold = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Quicksand-Bold.ttf");
            Typeface quicksandLight = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Quicksand-Light.ttf");
            name.setTypeface(quicksandBold);
            time.setTypeface(quicksandLight);

            itemView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    delMessage = chatFormats.get( (int) itemView.getTag());



                    if(delMessage.getUuid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked
                                        databaseref.child("deletedChat").push().setValue(delMessage);
                                        deleteFromDatabase(delMessage);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };

                        Dialog deletePostDialog = new Dialog(context);
                        deletePostDialog.setContentView(R.layout.new_dialog_box);
                        deletePostDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        deletePostDialog.findViewById(R.id.dialog_box_image_sdv).setBackground(ContextCompat.getDrawable(context,R.drawable.ic_message_white_24dp));
                        TextView heading =  deletePostDialog.findViewById(R.id.dialog_box_heading);
                        heading.setText("Confirm");
                        TextView body = deletePostDialog.findViewById(R.id.dialog_box_body);
                        body.setText("Are you sure you want to delete this message?");
                        Button positiveButton = deletePostDialog.findViewById(R.id.dialog_box_positive_button);
                        positiveButton.setText("CONFIRM");
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                        databaseref.child("deletedChat").push().setValue(delMessage);
                                        deleteFromDatabase(delMessage);
                                deletePostDialog.dismiss();



                            }
                        });
                        Button negativeButton = deletePostDialog.findViewById(R.id.dialog_box_negative_button);
                        negativeButton.setText("CANCEL");
                        negativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deletePostDialog.dismiss();
                            }
                        });
                        deletePostDialog.show();

                    }

                    return true;
                }
            });
        }
    }

    private void deleteFromDatabase(final ChatItemFormats Message) {

        try
        {
            forumRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("lastMessage"))
                {
                    if (dataSnapshot.child("lastMessage").child("key").getValue().toString().equals(Message.getKey()))
                    {
                        forumRef.child("lastMessage").removeValue();
                        isLastMessage = true;
                    }
                    else
                        isLastMessage = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
        catch (Exception e)
        { e.printStackTrace(); }

        if (isLastMessage)
        {
            forumRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("lastMessage"))
                    {
                        Toast.makeText(ctx, "Unable to delete message.Try again later.", Toast.LENGTH_SHORT).show();
                    }
                    else
                        databaseref.child("Chat").child(Message.getKey()).removeValue();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
        else
            databaseref.child("Chat").child(Message.getKey()).removeValue();

        // After deleting , chat needs to be refreshed
        loadMessagesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatFormats.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    ChatItemFormats temp = new ChatItemFormats();

                    temp = snapshot.getValue(ChatItemFormats.class);

                    temp.setKey(snapshot.getKey());

                    if (!snapshot.hasChild("messageType")) {
                        temp.setMessageType(MessageTypeUtilities.KEY_MESSAGE_STR);
                    }
                    chatFormats.add(temp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseref.child("Chat").addValueEventListener(loadMessagesListener);
    }


    class photoViewHolder extends RecyclerView.ViewHolder {

        TextView name, time, timeGroupText;
        SimpleDraweeView userAvatar,photo;
        LinearLayout rightDummy, leftDummy, messageBubble, chatContainer, chatItem;
        Context context;

        public photoViewHolder(final View itemView, final Context context) {
            super(itemView);
            this.context = context;
            userAvatar = (SimpleDraweeView) itemView.findViewById(R.id.chat_format_user_avatar);
            name = (TextView) itemView.findViewById(R.id.chat_format_name);
            time = (TextView) itemView.findViewById(R.id.chat_format_timestamp);
            timeGroupText = (TextView) itemView.findViewById(R.id.time_group_text_photo_message_format);
            chatItem = (LinearLayout) itemView.findViewById(R.id.chat_format_chat_item);
            chatContainer =(LinearLayout) itemView.findViewById(R.id.chat_format_chat_container);
            leftDummy = (LinearLayout) itemView.findViewById(R.id.chat_format_leftdummy);
            rightDummy = (LinearLayout) itemView.findViewById(R.id.chat_format_rightdummy);
            messageBubble = (LinearLayout) itemView.findViewById(R.id.chat_format_message_bubble);
            photo = (SimpleDraweeView) itemView.findViewById(R.id.chat_photo);


            Typeface quicksandRegular = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Quicksand-Regular.ttf");
            Typeface quicksandBold = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Quicksand-Bold.ttf");
            Typeface quicksandLight = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Quicksand-Light.ttf");
            name.setTypeface(quicksandBold);
            time.setTypeface(quicksandLight);

            itemView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    delphoto = chatFormats.get( (int) itemView.getTag());

                    if(delphoto.getUuid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    {


                        Dialog deletePostDialog = new Dialog(context);
                        deletePostDialog.setContentView(R.layout.new_dialog_box);
                        deletePostDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        deletePostDialog.findViewById(R.id.dialog_box_image_sdv).setBackground(ContextCompat.getDrawable(context,R.drawable.baseline_insert_photo_white_24));
                        TextView heading =  deletePostDialog.findViewById(R.id.dialog_box_heading);
                        heading.setText("Confirm");
                        TextView body = deletePostDialog.findViewById(R.id.dialog_box_body);
                        body.setText("Are you sure you want to delete this photo?");
                        Button positiveButton = deletePostDialog.findViewById(R.id.dialog_box_positive_button);
                        positiveButton.setText("CONFIRM");
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                databaseref.child("deletedChat").push().setValue(delphoto);
                                deleteFromDatabase(delphoto);
                                deletePostDialog.dismiss();



                            }
                        });
                        Button negativeButton = deletePostDialog.findViewById(R.id.dialog_box_negative_button);
                        negativeButton.setText("CANCEL");
                        negativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deletePostDialog.dismiss();
                            }
                        });
                        deletePostDialog.show();


                    }

                    return true;
                }
            });

        }
    }

    class messageShopViewHolder extends RecyclerView.ViewHolder {

        TextView message, name, time, timeGroupText;
        SimpleDraweeView userAvatar;
        LinearLayout rightDummy, leftDummy, messageBubble, chatContainer, chatItem;
        Context context;

        public messageShopViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            userAvatar = (SimpleDraweeView) itemView.findViewById(R.id.chat_format_user_avatar);
            name = (TextView) itemView.findViewById(R.id.chat_format_name);
            message = (TextView) itemView.findViewById(R.id.chat_format_message);
            time = (TextView) itemView.findViewById(R.id.chat_format_timestamp);
            timeGroupText = (TextView) itemView.findViewById(R.id.time_group_text_chat_message_format);
            chatItem = (LinearLayout) itemView.findViewById(R.id.chat_format_chat_item);
            chatContainer =(LinearLayout) itemView.findViewById(R.id.chat_format_chat_container);
            leftDummy = (LinearLayout) itemView.findViewById(R.id.chat_format_leftdummy);
            rightDummy = (LinearLayout) itemView.findViewById(R.id.chat_format_rightdummy);
            messageBubble = (LinearLayout) itemView.findViewById(R.id.chat_format_message_bubble);

            Typeface quicksandRegular = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Quicksand-Regular.ttf");
            Typeface quicksandBold = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Quicksand-Bold.ttf");
            Typeface quicksandLight = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Quicksand-Light.ttf");
            name.setTypeface(quicksandBold);
            time.setTypeface(quicksandLight);
        }
    }


    class photoShopViewHolder extends RecyclerView.ViewHolder {

        TextView name, time, timeGroupText;
        SimpleDraweeView userAvatar,photo;
        LinearLayout rightDummy, leftDummy, messageBubble, chatContainer, chatItem;
        Context context;

        public photoShopViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            userAvatar = (SimpleDraweeView) itemView.findViewById(R.id.chat_format_user_avatar);
            name = (TextView) itemView.findViewById(R.id.chat_format_name);
            time = (TextView) itemView.findViewById(R.id.chat_format_timestamp);
            timeGroupText = (TextView) itemView.findViewById(R.id.time_group_text_photo_message_format);
            chatItem = (LinearLayout) itemView.findViewById(R.id.chat_format_chat_item);
            chatContainer =(LinearLayout) itemView.findViewById(R.id.chat_format_chat_container);
            leftDummy = (LinearLayout) itemView.findViewById(R.id.chat_format_leftdummy);
            rightDummy = (LinearLayout) itemView.findViewById(R.id.chat_format_rightdummy);
            messageBubble = (LinearLayout) itemView.findViewById(R.id.chat_format_message_bubble);
            photo = (SimpleDraweeView) itemView.findViewById(R.id.chat_photo);


            Typeface quicksandRegular = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Quicksand-Regular.ttf");
            Typeface quicksandBold = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Quicksand-Bold.ttf");
            Typeface quicksandLight = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Quicksand-Light.ttf");
            name.setTypeface(quicksandBold);
            time.setTypeface(quicksandLight);
        }
    }
}
