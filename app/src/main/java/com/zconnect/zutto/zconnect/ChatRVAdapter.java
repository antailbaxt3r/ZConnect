package com.zconnect.zutto.zconnect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.zconnect.zutto.zconnect.ItemFormats.ChatItemFormats;
import com.zconnect.zutto.zconnect.Utilities.messageTypeUtilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class ChatRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<ChatItemFormats> chatFormats;
    Context ctx;


    public ChatRVAdapter(ArrayList<ChatItemFormats> chatFormats, Context ctx) {
        this.chatFormats = chatFormats;
        this.ctx = ctx;
    }

    @Override
    public int getItemViewType(int position) {

        if(chatFormats.get(position).getMessageType().equals(messageTypeUtilities.KEY_MESSAGE_STR)){
            return messageTypeUtilities.KEY_MESSAGE;
        }else if (chatFormats.get(position).getMessageType().equals(messageTypeUtilities.KEY_PHOTO_STR)){
            return messageTypeUtilities.KEY_PHOTO;
        }else if(chatFormats.get(position).getMessageType().equals(messageTypeUtilities.KEY_POLL_STR)){
            return messageTypeUtilities.KEY_POLL;
        }else if(chatFormats.get(position).getMessageType().equals(messageTypeUtilities.KEY_QUESTION_STR)){
            return messageTypeUtilities.KEY_QUESTION;
        }else return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if(viewType == messageTypeUtilities.KEY_MESSAGE || viewType == -1) {
            View messageContactView = inflater.inflate(R.layout.chat_message_format, parent, false);
            return new messageViewHolder(messageContactView, parent.getContext());
        }else if (viewType == messageTypeUtilities.KEY_PHOTO){
            View photoContactView = inflater.inflate(R.layout.chat_photo_format, parent, false);
            return new photoViewHolder(photoContactView, parent.getContext());
        }else{
            return null;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rvHolder, final int position) {



        final ChatItemFormats message = chatFormats.get(position);

        if(message.getMessageType().equals("message")){

            messageViewHolder holder = (messageViewHolder) rvHolder;
            if(message.getUuid()!=null)
            {

                if(position > 0 && chatFormats.get(position - 1).getUuid().equals(message.getUuid()))
                {
                    holder.name.setVisibility(View.GONE);
                }
                if(message.getUuid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    holder.rightDummy.setVisibility(View.GONE);
                    holder.leftDummy.setVisibility(View.VISIBLE);
                    holder.messageBubble.setBackground(holder.context.getResources().getDrawable(R.drawable.message_box_self));
                    holder.chatContainer.setGravity(Gravity.END);
                    holder.userAvatar.setVisibility(View.GONE);
                    holder.name.setVisibility(View.GONE);
                }
                else
                {
                    holder.rightDummy.setVisibility(View.VISIBLE);
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
                String time = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, Locale.US).format(message.getTimeDate());
                holder.time.setText(time);
                holder.name.setText(message.getName());
                holder.userAvatar.setImageURI(message.getImageThumb());

                String messageText = message.getMessage();

                messageText = messageText.substring(1,messageText.length()-1);
                holder.message.setText(messageText);
                Linkify.addLinks(holder.message, Linkify.ALL);
            }
        }else if(message.getMessageType().equals("photo")) {


            final photoViewHolder holder = (photoViewHolder) rvHolder;
            if(message.getUuid()!=null) {

                if (position > 0 && chatFormats.get(position - 1).getUuid().equals(message.getUuid())) {
                    holder.name.setVisibility(View.GONE);
                }
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
                String time = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, Locale.US).format(message.getTimeDate());
                holder.time.setText(time);
                holder.name.setText(message.getName());
                holder.userAvatar.setImageURI(message.getImageThumb());

                holder.photo.setImageURI(message.getPhotoURL());

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
        return chatFormats.size();
    }

    class messageViewHolder extends RecyclerView.ViewHolder {

        TextView message, name, time;
        SimpleDraweeView userAvatar;
        LinearLayout rightDummy, leftDummy, messageBubble, chatContainer, chatItem;
        Context context;

        public messageViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            userAvatar = (SimpleDraweeView) itemView.findViewById(R.id.chat_format_user_avatar);
            name = (TextView) itemView.findViewById(R.id.chat_format_name);
            message = (TextView) itemView.findViewById(R.id.chat_format_message);
            time = (TextView) itemView.findViewById(R.id.chat_format_timestamp);
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


    class photoViewHolder extends RecyclerView.ViewHolder {

        TextView name, time;
        SimpleDraweeView userAvatar,photo;
        LinearLayout rightDummy, leftDummy, messageBubble, chatContainer, chatItem;
        Context context;

        public photoViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            userAvatar = (SimpleDraweeView) itemView.findViewById(R.id.chat_format_user_avatar);
            name = (TextView) itemView.findViewById(R.id.chat_format_name);
            time = (TextView) itemView.findViewById(R.id.chat_format_timestamp);
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
