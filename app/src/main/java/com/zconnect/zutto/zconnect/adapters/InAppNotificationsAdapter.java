package com.zconnect.zutto.zconnect.adapters;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.drm.DrmStore;
import android.graphics.Region;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.CabPoolAll;
import com.zconnect.zutto.zconnect.ChatActivity;
import com.zconnect.zutto.zconnect.ExploreForumsActivity;
import com.zconnect.zutto.zconnect.InfoneProfileActivity;
import com.zconnect.zutto.zconnect.OpenEventDetail;
import com.zconnect.zutto.zconnect.OpenProductDetails;
import com.zconnect.zutto.zconnect.OpenUserDetail;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.VerificationPage;
import com.zconnect.zutto.zconnect.commonModules.NotificationService;
import com.zconnect.zutto.zconnect.holders.InAppNotificationsRVViewHolder;
import com.zconnect.zutto.zconnect.itemFormats.InAppNotificationsItemFormat;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;

import org.apache.commons.lang3.SystemUtils;

import java.awt.font.TextAttribute;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

import static android.graphics.Typeface.BOLD;

public class InAppNotificationsAdapter extends RecyclerView.Adapter<InAppNotificationsRVViewHolder> {

    private ArrayList<InAppNotificationsItemFormat> notificationsList = new ArrayList<>();
    private Context context;
    private String communityRef;
    private Intent intent;
    private DatabaseReference seenReference;
    String catID;
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

        if(!notificationsList.isEmpty()) {

                    Log.d("AAAA",notificationsList.get(position).getScope() + " " );
                try{
                if (notificationsList.get(position).getScope().equals(NotificationIdentifierUtilities.KEY_GLOBAL)&&!notificationsList.get(position).getType().equals("adminNotification")) {
                    holder.simpleDraweeView.setVisibility(View.GONE);

                } else {
                    holder.simpleDraweeView.setVisibility(View.VISIBLE);
                }
                if (notificationsList.get(position).isSeen().get(FirebaseAuth.getInstance().getCurrentUser().getUid()) != null) {

                    if(!notificationsList.get(position).isSeen().get(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        holder.seen.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    HashMap<String,Boolean> seenmap = new HashMap<>();
                    seenmap.put(FirebaseAuth.getInstance().getCurrentUser().getUid(),false);
                    FirebaseDatabase.getInstance().getReference().child("globalNotifications").child(notificationsList.get(position).getKey()).child("seen").setValue(seenmap);
                    holder.seen.setVisibility(View.VISIBLE);
                }


            String text = notificationsList.get(position).getNotifiedBy().getUsername() + " " + notificationsList.get(position).getTitle();
            SpannableString spannableString = new SpannableString(text);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(context, OpenUserDetail.class);
                    intent.putExtra("Uid", notificationsList.get(position).getNotifiedBy().getUserUID());
                    context.startActivity(intent);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false); // set to false to remove underline
                }
            };
            StyleSpan styleSpan = new StyleSpan(BOLD);
            spannableString.setSpan(clickableSpan, 0, notificationsList.get(position).getNotifiedBy().getUsername().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            spannableString.setSpan(styleSpan, 0, notificationsList.get(position).getNotifiedBy().getUsername().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            holder.titletv.setText(spannableString);
            holder.titletv.setClickable(true);
            holder.titletv.setMovementMethod(LinkMovementMethod.getInstance());
            Uri uri = Uri.parse(notificationsList.get(position).getNotifiedBy().getImageURL());
            holder.simpleDraweeView.setImageURI(uri);
            TimeUtilities timeUtilities = new TimeUtilities(notificationsList.get(position).getPostTimeMillis(), System.currentTimeMillis());
            holder.timetv.setText(timeUtilities.calculateTimeAgo());
            holder.desctv.setText(notificationsList.get(position).getDesc());
            holder.notificationsLayout.setOnClickListener(view -> {
                //Seenzone
                if (notificationsList.get(position).getScope().equals(NotificationIdentifierUtilities.KEY_GLOBAL)) {
                    seenReference = FirebaseDatabase.getInstance().getReference()
                            .child("communities").child(communityRef).child("globalNotifications")
                            .child(notificationsList.get(position).getKey())
                            .child("seen").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    seenReference.setValue(true);

                } else {
                    seenReference = FirebaseDatabase.getInstance().getReference()
                            .child("communities").child(communityRef).child("Users1")
                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                            .child("notifications").child(notificationsList.get(position).getKey())
                            .child("seen");
                    seenReference.setValue(true);
                }
                holder.seen.setVisibility(View.INVISIBLE);
                HashMap<String,Boolean> seenmap = new HashMap<>();
                seenmap.put(FirebaseAuth.getInstance().getCurrentUser().getUid(),true);
                notificationsList.get(position).setSeen(seenmap);
                String type = notificationsList.get(position).getType();
                switch (type) {
                    case "acceptforum":
                        intent = new Intent(context, ExploreForumsActivity.class);
                        context.startActivity(intent);
                        break;
                    case "infonevalidate":
                        FirebaseDatabase.getInstance().getReference().child("communities").child(communityRef).child("Users1")
                                .child(Objects.requireNonNull(notificationsList.get(position).getNotifiedBy().getUserUID())).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                intent = new Intent(context, InfoneProfileActivity.class);
                                catID = (String) dataSnapshot.child("infoneTyoe").getValue();
                                intent.putExtra("infoneUserId", notificationsList.get(position).getNotifiedBy().getUserUID());
                                intent.putExtra("catID", catID);
                                context.startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        break;
                    case "addforum":
                        intent = new Intent(context, ExploreForumsActivity.class);
                        context.startActivity(intent);
                        break;
                    case "contactAdd":
                        intent = new Intent(context, InfoneProfileActivity.class);
                        intent.putExtra("infoneUserId", String.valueOf(notificationsList.get(position).getMetadata().get("infoneUserId")));
                        intent.putExtra("catID", String.valueOf(notificationsList.get(position).getMetadata().get("catID")));
                        context.startActivity(intent);
                        break;
                    case "productAdd":
                        intent = new Intent(context, OpenProductDetails.class);
                        intent.putExtra("key", String.valueOf(notificationsList.get(position).getMetadata().get("key")));
                        intent.putExtra("type", String.valueOf(notificationsList.get(position).getMetadata().get("type")));
                        context.startActivity(intent);
                        break;

                    case "eventAdd":
                        intent = new Intent(context, OpenEventDetail.class);
                        intent.putExtra("id", String.valueOf(notificationsList.get(position).getMetadata().get("id")));
                        context.startActivity(intent);
                        break;

                    case "cabpoolAdd":
                        intent = new Intent(context, CabPoolAll.class);
                        intent.putExtra("key", String.valueOf(notificationsList.get(position).getMetadata().get("key")));
                        context.startActivity(intent);
                        break;
                    case "eventBoost":
                        intent = new Intent(context, OpenEventDetail.class);
                        intent.putExtra("id", String.valueOf(notificationsList.get(position).getMetadata().get("key")));
                        context.startActivity(intent);
                        break;
                    case "productShortlist":
                        intent = new Intent(context, OpenProductDetails.class);
                        intent.putExtra("key", String.valueOf(notificationsList.get(position).getMetadata().get("key")));
                        context.startActivity(intent);
                        break;
                    case "infoneinvalidate":
                        intent = new Intent(context, InfoneProfileActivity.class);
                        intent.putExtra("infoneUserId", notificationsList.get(position).getNotifiedBy().getUserUID());
                        intent.putExtra("catID", catID);
                        context.startActivity(intent);
                        break;
                    case "verification":
                        intent = new Intent(context, VerificationPage.class);
                        context.startActivity(intent);
                        break;
                    case "statusComment":
                        intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("ref", String.valueOf(notificationsList.get(position).getMetadata().get("ref")));
                        intent.putExtra("key", String.valueOf(notificationsList.get(position).getMetadata().get("key")));
                        intent.putExtra("type", "post");
                        intent.putExtra("uid", String.valueOf(notificationsList.get(position).getMetadata().get("uid")));
                        context.startActivity(intent);
                        break;
                }

                if (notificationsList.get(position).getTitle().equals("tried contacting you")) {
                    Log.d("onclick", "calling ");
                    Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + notificationsList.get(position).getNotifiedBy().getMobileNumber()));
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, call, PendingIntent.FLAG_UPDATE_CURRENT);
                    try {
                        pendingIntent.send(context, 0, call);
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }

                //Open Metadata related stuff
            });
        } catch (Exception e){
                    Log.d("onBindViewHolder: ", String.valueOf(e));
                }
        }



    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }
}
