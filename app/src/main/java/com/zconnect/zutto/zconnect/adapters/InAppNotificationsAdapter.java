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
import com.zconnect.zutto.zconnect.holders.InAppNotificationsRVViewHolder;
import com.zconnect.zutto.zconnect.itemFormats.InAppNotificationsItemFormat;

import java.util.ArrayList;

public class InAppNotificationsAdapter extends RecyclerView.Adapter<InAppNotificationsRVViewHolder> {

    private ArrayList<InAppNotificationsItemFormat> notificationsList;
    private Context context;
    private String communityRef;

    public InAppNotificationsAdapter(Context context, String communityRef, ArrayList<InAppNotificationsItemFormat> notificationsList) {
        this.notificationsList = notificationsList;
        this.context = context;
        this.communityRef = communityRef;
    }

    @Override
    public InAppNotificationsRVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_in_app_notifications, parent, false);

        return new InAppNotificationsRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InAppNotificationsRVViewHolder holder, final int position) {
        holder.titletv.setText(notificationsList.get(position).getTitle());
        holder.desctv.setText(notificationsList.get(position).getDesc());
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
