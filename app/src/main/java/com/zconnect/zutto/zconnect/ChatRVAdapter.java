package com.zconnect.zutto.zconnect;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.ItemFormats.ChatItemFormats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ChatRVAdapter extends RecyclerView.Adapter<ChatRVAdapter.ViewHolder> {

    ArrayList<ChatItemFormats> chatFormats;


    public ChatRVAdapter(ArrayList<ChatItemFormats> chatFormats) {
        this.chatFormats = chatFormats;
    }

    @Override
    public ChatRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View contactView = inflater.inflate(R.layout.chat_format, parent, false);
        return new ChatRVAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ChatRVAdapter.ViewHolder holder, int position) {

        ChatItemFormats message = chatFormats.get(position);
        String time = SimpleDateFormat.getDateTimeInstance().format(message.getTimeDate());
        holder.time.setText(time);
        holder.name.setText(message.getName());
        holder.userAvatar.setImageURI(message.getImageThumb());

        String messageText = message.getMessage();
        messageText = messageText.substring(1,messageText.length()-1);
        holder.message.setText(messageText);
    }

    @Override
    public int getItemCount() {
        return chatFormats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView message, name, time;
        SimpleDraweeView userAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            userAvatar = (SimpleDraweeView) itemView.findViewById(R.id.chat_format_user_avatar);
            name = (TextView) itemView.findViewById(R.id.chat_format_name);
            message = (TextView) itemView.findViewById(R.id.chat_format_message);
            time = (TextView) itemView.findViewById(R.id.chat_format_timestamp);

            Typeface quicksandRegular = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Quicksand-Regular.ttf");
            Typeface quicksandBold = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Quicksand-Bold.ttf");
            Typeface quicksandLight = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Quicksand-Light.ttf");
            name.setTypeface(quicksandBold);
            message.setTypeface(quicksandRegular);
            time.setTypeface(quicksandLight);
        }
    }
}
