package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.zutto.zconnect.ItemFormats.MessageTabRVItem;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.holders.MessageTabRVViewHolder;

import java.util.ArrayList;

/**
 * Created by tanmay on 25/3/18.
 */

public class MessageTabRVAdapter extends RecyclerView.Adapter<MessageTabRVViewHolder> {

    Context context;
    ArrayList<MessageTabRVItem> messageTabRVItems;

    public MessageTabRVAdapter(Context context, ArrayList<MessageTabRVItem> messageTabRVItems) {
        this.context = context;
        this.messageTabRVItems = messageTabRVItems;
    }

    @Override
    public MessageTabRVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_message_messages, parent, false);

        return new MessageTabRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageTabRVViewHolder holder, final int position) {
        holder.message.setText(messageTabRVItems.get(position).getMessage());
        holder.openAlert(messageTabRVItems.get(position).getMessage(),messageTabRVItems.get(position).getSender(),messageTabRVItems.get(position).getChatUID());
    }

    @Override
    public int getItemCount() {
        return messageTabRVItems.size();
    }
}
