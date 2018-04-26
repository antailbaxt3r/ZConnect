package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.zutto.zconnect.ChatActivity;
import com.zconnect.zutto.zconnect.ItemFormats.MessageTabRVItem;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.holders.ChatTabRVViewHolder;

import java.util.ArrayList;

import static com.zconnect.zutto.zconnect.BaseActivity.communityReference;

/**
 * Created by tanmay on 25/3/18.
 */

public class ChatTabRVAdapter extends RecyclerView.Adapter<ChatTabRVViewHolder> {

    Context context;
    ArrayList<MessageTabRVItem> chatTabRVItems = new ArrayList<MessageTabRVItem>();

    DatabaseReference UsersReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("messages");

    public ChatTabRVAdapter(Context context, ArrayList<MessageTabRVItem> chatTabRVItems) {
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
    public void onBindViewHolder(ChatTabRVViewHolder holder, final int position) {
        holder.nametv.setText(chatTabRVItems.get(position).getMessage());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Intent i = new Intent(context, ChatActivity.class);
            i.putExtra("type","messages");
            i.putExtra("userKey",chatTabRVItems.get(position).getSender());
            i.putExtra("ref",UsersReference.child("chats").child(chatTabRVItems.get(position).getChatUID()).toString());
            context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatTabRVItems.size();
    }
}
