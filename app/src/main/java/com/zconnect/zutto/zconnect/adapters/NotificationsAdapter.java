package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.holders.NotificationsViewHolder;
import com.zconnect.zutto.zconnect.itemFormats.NotificationsModel;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsViewHolder> {

    private ArrayList<NotificationsModel> notificationsList;
    private Context context;
    private String communityRef;

    public NotificationsAdapter(String communityRef, ArrayList<NotificationsModel> notificationsList, Context context) {
        this.notificationsList = notificationsList;
        this.context = context;
        this.communityRef = communityRef;
    }

    @Override
    public NotificationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_notification, parent, false);

        return new NotificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationsViewHolder holder, final int position) {
        holder.titletv.setText(notificationsList.get(position).getTitle());
        holder.desctv.setText(notificationsList.get(position).getDesc());

        holder.datetv.setText(notificationsList.get(position).getDate().toString().substring(11,16));

        holder.notificationsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Seenzone
                DatabaseReference seenReference = FirebaseDatabase.getInstance().getReference()
                        .child("communities").child(communityRef).child("Users1")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("notifications").child(notificationsList.get(position).getKey())
                        .child("seen");
                seenReference.setValue(true);

                //Open Metadata related stuff
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }
}
