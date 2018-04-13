package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.zutto.zconnect.ChatActivity2;
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

        //holder.name.setText(messageTabRVItems.get(position).getName().substring(28,messageTabRVItems.get(position).getName().length()));
        holder.name.setText("Anonymous");
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recid=messageTabRVItems.get(position).getName().substring(0,28);
                DatabaseReference databaseReference;
                SharedPreferences communitySP;
                String communityReference;
                communitySP = context.getSharedPreferences("communityName", Context.MODE_PRIVATE);
                communityReference = communitySP.getString("communityReference", null);
                databaseReference= FirebaseDatabase.getInstance().getReference();
                FirebaseUser mauth = FirebaseAuth.getInstance().getCurrentUser();
                String myuid=mauth.getUid();
                databaseReference.child("communities").child(communityReference).child("features").child("messages").child("users").child(myuid).child(recid).removeValue();
                notifyDataSetChanged();
            }
        });
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, ChatActivity2.class);
                i.putExtra("s",messageTabRVItems.get(position).getName());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return messageTabRVItems.size();
    }
}
