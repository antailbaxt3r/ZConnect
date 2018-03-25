package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.zutto.zconnect.ItemFormats.ChatTabRVItem;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.holders.ChatTabRVViewHolder;

import java.util.ArrayList;

/**
 * Created by tanmay on 25/3/18.
 */

public class ChatTabRVAdapter extends RecyclerView.Adapter<ChatTabRVViewHolder> {

    Context context;
    ArrayList<ChatTabRVItem> chatTabRVItems;

    public ChatTabRVAdapter(Context context, ArrayList<ChatTabRVItem> chatTabRVItems) {
        this.context = context;
        this.chatTabRVItems = chatTabRVItems;
    }

    @Override
    public ChatTabRVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_chat_message, parent, false);

        return new ChatTabRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatTabRVViewHolder holder, int position) {

        holder.nametv.setText(chatTabRVItems.get(position).getName());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
