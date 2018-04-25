package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.zconnect.zutto.zconnect.ItemFormats.ChatItemFormats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class ChatRVAdapter extends RecyclerView.Adapter<ChatRVAdapter.ViewHolder> {

    ArrayList<ChatItemFormats> chatFormats;
    Context ctx;

    public ChatRVAdapter(ArrayList<ChatItemFormats> chatFormats, Context ctx) {
        this.chatFormats = chatFormats;
        this.ctx = ctx;
    }

    @Override
    public ChatRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View contactView = inflater.inflate(R.layout.chat_format, parent, false);
        return new ChatRVAdapter.ViewHolder(contactView, parent.getContext());
    }

    @Override
    public void onBindViewHolder(ChatRVAdapter.ViewHolder holder, final int position) {


        final ChatItemFormats message = chatFormats.get(position);
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
        }
    }

    @Override
    public int getItemCount() {
        return chatFormats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView message, name, time;
        SimpleDraweeView userAvatar;
        LinearLayout rightDummy, leftDummy, messageBubble, chatContainer, chatItem;
        Context context;

        public ViewHolder(View itemView, Context context) {
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
            message.setTypeface(quicksandRegular);
            time.setTypeface(quicksandLight);
        }
    }
}
