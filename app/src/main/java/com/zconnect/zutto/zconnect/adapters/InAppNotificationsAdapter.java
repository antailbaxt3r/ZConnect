package com.zconnect.zutto.zconnect.adapters;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.drm.DrmStore;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.NotificationService;
import com.zconnect.zutto.zconnect.holders.InAppNotificationsRVViewHolder;
import com.zconnect.zutto.zconnect.itemFormats.InAppNotificationsItemFormat;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;

import org.apache.commons.lang3.SystemUtils;

import java.awt.font.TextAttribute;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import static android.graphics.Typeface.BOLD;

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
    public void onBindViewHolder(final InAppNotificationsRVViewHolder holder, final int position) {
        final DatabaseReference seenReference = FirebaseDatabase.getInstance().getReference()
                .child("communities").child(communityRef).child("Users1")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("notifications").child(notificationsList.get(position).getKey())
                .child("seen");
                if(Boolean.compare(notificationsList.get(position).isSeen(),false)==0){
                    holder.seen.setVisibility(View.VISIBLE);
                }

        String text = notificationsList.get(position).getNotifiedby().getUsername() + " " + notificationsList.get(position).getTitle();
        SpannableString spannableString = new SpannableString(text);
        StyleSpan styleSpan = new StyleSpan(BOLD);
        spannableString.setSpan(styleSpan, 0 , notificationsList.get(position).getNotifiedby().getUsername().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        holder.titletv.setText(spannableString);
        Uri uri = Uri.parse(notificationsList.get(position).getNotifiedby().getImageURL());
        holder.simpleDraweeView.setImageURI(uri);
        TimeUtilities timeUtilities = new TimeUtilities(notificationsList.get(position).getPostTimeMillis(),System.currentTimeMillis());
        holder.timetv.setText(timeUtilities.calculateTimeAgo());
        holder.desctv.setText(notificationsList.get(position).getDesc());
        holder.notificationsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Seenzone
                holder.seen.setVisibility(View.INVISIBLE);
                seenReference.setValue(true);
                notificationsList.get(position).setSeen(true);
                if(notificationsList.get(position).getTitle().equals("tried contacting you")){
                    Log.d("onclick", "calling ");
                    Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + notificationsList.get(position).getNotifiedby().getMobileNumber()));
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, call, PendingIntent.FLAG_UPDATE_CURRENT);
                    try {
                        pendingIntent.send(context,0,call);
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }

                //Open Metadata related stuff
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }
}
