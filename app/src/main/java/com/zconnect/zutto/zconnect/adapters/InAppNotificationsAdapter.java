package com.zconnect.zutto.zconnect.adapters;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.zutto.zconnect.CabPoolAll;
import com.zconnect.zutto.zconnect.CabPoolListOfPeople;
import com.zconnect.zutto.zconnect.ExploreForumsActivity;
import com.zconnect.zutto.zconnect.InfoneProfileActivity;
import com.zconnect.zutto.zconnect.OpenEventDetail;
import com.zconnect.zutto.zconnect.OpenProductDetails;
import com.zconnect.zutto.zconnect.OpenStatus;
import com.zconnect.zutto.zconnect.OpenUserDetail;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.VerificationPage;
import com.zconnect.zutto.zconnect.holders.InAppNotificationsRVViewHolder;
import com.zconnect.zutto.zconnect.interfaces.OnLoadMoreListener;
import com.zconnect.zutto.zconnect.itemFormats.InAppNotificationsItemFormat;
import com.zconnect.zutto.zconnect.utilities.LoadMoreUtility;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;

import static android.graphics.Typeface.BOLD;

public class InAppNotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Vector<InAppNotificationsItemFormat> notificationsList = new Vector<>();
    private Context context;
    private String communityRef;
    private Intent intent;
    private DatabaseReference seenReference;
    String catID;

    private LoadMoreUtility loadMoreUtility;

    public InAppNotificationsAdapter(Context context, String communityRef, Vector<InAppNotificationsItemFormat> notificationsList, RecyclerView recyclerView) {
        this.notificationsList = notificationsList;
        this.context = context;
        this.communityRef = communityRef;
        loadMoreUtility = new LoadMoreUtility(2, recyclerView);
    }

    public LoadMoreUtility getLoadMoreUtility() {
        return loadMoreUtility;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        if(viewType == loadMoreUtility.VIEW_TYPE_LOADER)
        {
            view = layoutInflater.inflate(R.layout.row_more_loader, parent, false);
            return new LoadMoreUtility.LoadingViewHolder(view);

        }
        else if(viewType == loadMoreUtility.VIEW_TYPE_NORMAL)
        {
            view = layoutInflater.inflate(R.layout.item_in_app_notifications, parent, false);
            return new InAppNotificationsRVViewHolder(view);
        }
        return null;
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        if(viewHolder instanceof LoadMoreUtility.LoadingViewHolder)
        {
            LoadMoreUtility.LoadingViewHolder holder = (LoadMoreUtility.LoadingViewHolder) viewHolder;
            holder.setState();
        }
        else if(viewHolder instanceof InAppNotificationsRVViewHolder)
        {
            InAppNotificationsRVViewHolder holder = (InAppNotificationsRVViewHolder) viewHolder;
            if(!notificationsList.isEmpty()) {

                Log.d("AAAA",notificationsList.get(position).getScope() + " " );
                //           try{
                if (notificationsList.get(position).isSeen().get(FirebaseAuth.getInstance().getCurrentUser().getUid()) != null) {

                    if(!notificationsList.get(position).isSeen().get(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        holder.seen.setVisibility(View.VISIBLE);
                    }
                    else{
                        holder.seen.setVisibility(View.INVISIBLE);
                    }
                }
                else{
                    HashMap<String,Boolean> seenmap = new HashMap<>();
                    seenmap.put(FirebaseAuth.getInstance().getCurrentUser().getUid(),false);
                    if(notificationsList.get(position).getScope().equals(NotificationIdentifierUtilities.KEY_PERSONAL))
                        FirebaseDatabase.getInstance().getReference().child("communities").child(communityRef).child("Users1").child("notifications").child(notificationsList.get(position).getKey()).child("seen").setValue(seenmap);
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
                ClickableSpan normalSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        holder.notificationsLayout.performClick();
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setUnderlineText(false); // set to false to remove underline
                    }
                };

                StyleSpan styleSpan = new StyleSpan(BOLD);
                spannableString.setSpan(clickableSpan, 0, notificationsList.get(position).getNotifiedBy().getUsername().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                spannableString.setSpan(styleSpan, 0, notificationsList.get(position).getNotifiedBy().getUsername().length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                spannableString.setSpan(normalSpan,  notificationsList.get(position).getNotifiedBy().getUsername().length(),spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                holder.titletv.setText(spannableString);
                holder.titletv.setClickable(true);
                holder.titletv.setMovementMethod(LinkMovementMethod.getInstance());
                Uri uri = Uri.parse(notificationsList.get(position).getNotifiedBy().getImageURL());
                holder.simpleDraweeView.setImageURI(uri);
                TimeUtilities timeUtilities = new TimeUtilities(notificationsList.get(position).getPostTimeMillis(), System.currentTimeMillis());
                holder.timetv.setText(timeUtilities.calculateTimeAgo());
                holder.desctv.setText(notificationsList.get(position).getDesc());

                holder.simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, OpenUserDetail.class);
                        intent.putExtra("Uid", notificationsList.get(position).getNotifiedBy().getUserUID());
                        context.startActivity(intent);
                    }
                });

                holder.notificationsLayout.setOnClickListener(view -> {
                    //Seenzone
                    HashMap<String,Boolean> seenmap = new HashMap<>();
                    seenmap.put(FirebaseAuth.getInstance().getCurrentUser().getUid(),true);
                    Log.d("im the log msg onclick", notificationsList.get(position).getTitle());

                    if (notificationsList.get(position).getScope().equals(NotificationIdentifierUtilities.KEY_PERSONAL)) {
                        {
                            seenReference = FirebaseDatabase.getInstance().getReference()
                                    .child("communities").child(communityRef).child("Users1")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                    .child("notifications").child(notificationsList.get(position).getKey())
                                    .child("seen");
                            seenReference.setValue(seenmap);
                        }
                        holder.seen.setVisibility(View.GONE);
                        notificationsList.get(position).setSeen(seenmap);
                        String type = notificationsList.get(position).getType();
                        Log.d("NOTIFICATIONTYPE",type);
                        switch (type) {
                            case "acceptforum":
                                intent = new Intent(context, ExploreForumsActivity.class);
                                context.startActivity(intent);
                                break;
                            case "infonevalidate":
                                intent = new Intent(context, InfoneProfileActivity.class);
                                intent.putExtra("infoneUserId", notificationsList.get(position).getNotifiedBy().getUserUID());
                                intent.putExtra("catID", String.valueOf(notificationsList.get(position).getMetadata().get("catID")));
                                context.startActivity(intent);
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

                            case "cabpoolLeave":
                                intent = new Intent(context, CabPoolListOfPeople.class);
                                intent.putExtra("key", String.valueOf(notificationsList.get(position).getMetadata().get("key")));
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

                            case "cabpoolJoin":
                                intent = new Intent(context, CabPoolListOfPeople.class);

//                            Log.d("METADATAAA",String.valueOf(notificationsList.get(position).getMetadata()));
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
                                intent.putExtra("type", String.valueOf(notificationsList.get(position).getMetadata().get("type")));
                                context.startActivity(intent);
                                break;
                            case "infoneinvalidate":
                                intent = new Intent(context, InfoneProfileActivity.class);
                                intent.putExtra("infoneUserId", notificationsList.get(position).getNotifiedBy().getUserUID());
                                intent.putExtra("catID", String.valueOf(notificationsList.get(position).getMetadata().get("catID")));
                                context.startActivity(intent);
                                break;
                            case "verification":
                                intent = new Intent(context, VerificationPage.class);
                                context.startActivity(intent);
                                break;
                            case "statusComment":
                                intent = new Intent(context, OpenStatus.class);
                                intent.putExtra("key", String.valueOf(notificationsList.get(position).getMetadata().get("key")));
                                context.startActivity(intent);
                                break;
                            case "statusNestedComment":
                                intent = new Intent(context, OpenStatus.class);
                                intent.putExtra("key", String.valueOf(notificationsList.get(position).getMetadata().get("key")));
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

                    }           //Open Metadata related stuff
                });
                //  }catch (Exception e){}
            }
        }

    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return notificationsList.get(position) == null ? loadMoreUtility.VIEW_TYPE_LOADER : loadMoreUtility.VIEW_TYPE_NORMAL;
    }
}
