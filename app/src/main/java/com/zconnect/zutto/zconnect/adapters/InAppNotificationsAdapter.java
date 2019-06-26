package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.holders.InAppNotificationsRVViewHolder;
import com.zconnect.zutto.zconnect.itemFormats.InAppNotificationsItemFormat;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;

import org.apache.commons.lang3.SystemUtils;

import java.awt.font.TextAttribute;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    public void onBindViewHolder(final InAppNotificationsRVViewHolder holder, int position) {

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
                DatabaseReference seenReference = FirebaseDatabase.getInstance().getReference()
                        .child("communities").child(communityRef).child("Users1")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("notifications").child(notificationsList.get(holder.getAdapterPosition()).getKey())
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
