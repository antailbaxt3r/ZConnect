package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            message = (TextView) itemView.findViewById(R.id.message);
            time = (TextView) itemView.findViewById(R.id.time);

            Typeface quicksandLight = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
            Typeface quicksandMedium = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Medium.ttf");
            Typeface quicksandBold = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-ExtraLight.ttf");
            name.setTypeface(quicksandBold);
            message.setTypeface(quicksandMedium);
            time.setTypeface(quicksandLight);
        }
    }
}
