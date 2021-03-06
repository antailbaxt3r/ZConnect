package com.zconnect.zutto.zconnect.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.AdminHome;
import com.zconnect.zutto.zconnect.BuildConfig;
import com.zconnect.zutto.zconnect.CabPoolAll;
import com.zconnect.zutto.zconnect.CabPoolListOfPeople;
import com.zconnect.zutto.zconnect.ChatActivity;
import com.zconnect.zutto.zconnect.HomeActivity;
import com.zconnect.zutto.zconnect.InfoneProfileActivity;
import com.zconnect.zutto.zconnect.Internships;
import com.zconnect.zutto.zconnect.LeaderBoard;
import com.zconnect.zutto.zconnect.Links;
import com.zconnect.zutto.zconnect.LoginActivity;
import com.zconnect.zutto.zconnect.Notices;
import com.zconnect.zutto.zconnect.OnSingleClickListener;
import com.zconnect.zutto.zconnect.OpenEventDetail;
import com.zconnect.zutto.zconnect.OpenProductDetails;
import com.zconnect.zutto.zconnect.OpenStatus;
import com.zconnect.zutto.zconnect.OpenUserDetail;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.ReferralCode;
import com.zconnect.zutto.zconnect.TabStoreRoom;
import com.zconnect.zutto.zconnect.TabbedEvents;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.GlobalFunctions;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.commonModules.NumberNotificationForFeatures;
import com.zconnect.zutto.zconnect.commonModules.viewImage;
import com.zconnect.zutto.zconnect.fragments.UpdateAppActivity;
import com.zconnect.zutto.zconnect.itemFormats.CommunityFeatures;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.commonModules.newUserVerificationAlert;
import com.zconnect.zutto.zconnect.itemFormats.RecentsItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.pools.ActivePoolDetailsActivity;
import com.zconnect.zutto.zconnect.pools.PoolActivity;
import com.zconnect.zutto.zconnect.pools.UpcomingPoolDetailsActivity;
import com.zconnect.zutto.zconnect.pools.holders.PoolViewHolder;
import com.zconnect.zutto.zconnect.pools.models.Pool;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.FeatureDBName;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.ProductUtilities;
import com.zconnect.zutto.zconnect.utilities.RecentTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;
import com.zconnect.zutto.zconnect.addActivities.AddStatus;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

import static android.graphics.Typeface.BOLD;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityTitle;

public class RecentsRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    Vector<RecentsItemFormat> recentsItemFormats;
    private HomeActivity mHomeActivity;
    DatabaseReference mRef;
    OnSingleClickListener openUserProfileListener;
    boolean flag;
    private CommunityFeatures communityFeatures;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    String videoId;
    private Long count;


    public RecentsRVAdapter(Context context, Vector<RecentsItemFormat> recentsItemFormats, HomeActivity HomeActivity, CommunityFeatures communityFeatures, LinearLayoutManager linearLayoutManager, RecyclerView recyclerView) {
        this.context = context;
        this.recentsItemFormats = recentsItemFormats;
        this.mHomeActivity = HomeActivity;
        this.communityFeatures = communityFeatures;
        this.linearLayoutManager = linearLayoutManager;
        this.recyclerView = recyclerView;
    }

    //
    @Override
    public int getItemViewType(int position) {
        try {
            if (recentsItemFormats.get(position).getRecentType().equals(RecentTypeUtilities.KEY_RECENT_ADD_STATUS_STR)) {
                return RecentTypeUtilities.KEY_RECENT_ADD_STATUS_INT;
            } else if (recentsItemFormats.get(position).getRecentType().equals(RecentTypeUtilities.KEY_RECENT_FEATURES_STR)) {
                return RecentTypeUtilities.KEY_RECENT_FEATURES_INT;
            } else if (recentsItemFormats.get(position).getRecentType().equals(RecentTypeUtilities.KEY_RECENT_NORMAL_POST_STR)) {
                return RecentTypeUtilities.KEY_RECENT_NORMAL_POST_INT;
            } else if (recentsItemFormats.get(position).getRecentType().equals(RecentTypeUtilities.KEY_RECENT_SHOP_POST_STR)) {
                return RecentTypeUtilities.KEY_RECENT_SHOP_POST_INT;
            } else {
                return RecentTypeUtilities.KEY_UPDATE_APP_INT;
            }
        } catch (NullPointerException e) {
            Log.d("Exception", "adding type");
            recentsItemFormats.get(position).setRecentType(RecentTypeUtilities.KEY_RECENT_NORMAL_POST_STR);
            return RecentTypeUtilities.KEY_RECENT_NORMAL_POST_INT;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == RecentTypeUtilities.KEY_RECENT_ADD_STATUS_INT) {
            View addStatusView = inflater.inflate(R.layout.recents_add_status, parent, false);
            Log.d("VIEWTYPE", String.valueOf(viewType));
            return new RecentsRVAdapter.ViewHolderStatus(addStatusView);
        } else if (viewType == RecentTypeUtilities.KEY_RECENT_FEATURES_INT) {
            View featuresView = inflater.inflate(R.layout.recents_features_view, parent, false);
            return new RecentsRVAdapter.FeaturesViewHolder(featuresView);
        } else if (viewType == RecentTypeUtilities.KEY_RECENT_NORMAL_POST_INT) {
            View contactView = inflater.inflate(R.layout.recents_item_format, parent, false);
            return new RecentsRVAdapter.Viewholder(contactView);
        } else if  (viewType == RecentTypeUtilities.KEY_RECENT_SHOP_POST_INT) {
            Log.d("SSSSS", "Here");
            View shopPoolView = inflater.inflate(R.layout.item_pool, parent, false);
            return new RecentsRVAdapter.ShopPoolViewHolder(shopPoolView);
        } else {
            View updateAppLayout = inflater.inflate(R.layout.recents_update_app_item, parent, false);
            return new RecentsRVAdapter.UpdateAppViewHolder(updateAppLayout);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder2, final int position) {
        switch (holder2.getItemViewType()) {
            case 0:
                ViewHolderStatus holderStatus = (ViewHolderStatus) holder2;
                break;
            case 1:
                FeaturesViewHolder featuresViewHolder = (FeaturesViewHolder) holder2;
                featuresViewHolder.setFeatureVisibility(communityFeatures);
                break;
            case 3:
                ShopPoolViewHolder shopPoolViewHolder = (ShopPoolViewHolder) holder2;
                shopPoolViewHolder.populate(recentsItemFormats.get(position));
                break;
            case 2:
                final Viewholder holder = (Viewholder) holder2;
                openUserProfileListener = new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {

                        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(recentsItemFormats.get(position).getPostedBy().getUID())) {
                            Intent i = new Intent(context, OpenUserDetail.class);
                            i.putExtra("Uid", recentsItemFormats.get(position).getPostedBy().getUID());
                            context.startActivity(i);
//                            mHomeActivity.changeFragment(4);
                        } else {
                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                            HashMap<String, String> meta = new HashMap<>();

                            meta.put("type", "fromRecentsRV");
                            meta.put("userType", "openUserProfile");
                            meta.put("userUID", recentsItemFormats.get(position).getPostedBy().getUID());

                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                            counterItemFormat.setUniqueID(CounterUtilities.KEY_PROFILE_OPEN);
                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                            counterItemFormat.setMeta(meta);

                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                            counterPush.pushValues();

                            Intent i = new Intent(context, OpenUserDetail.class);
                            i.putExtra("Uid", recentsItemFormats.get(position).getPostedBy().getUID());
                            context.startActivity(i);
                        }
                    }
                };
                String name = "Somebody", posted = " posted a ", post = " post ";
                ClickableSpan clickableSpanPostedBy = null, clickableSpanFeature = null;

                holder.deleteButton.setVisibility(View.GONE);

                try {
                    if (recentsItemFormats.get(position).getPostTimeMillis() > 0) {
                        TimeUtilities ta = new TimeUtilities(recentsItemFormats.get(position).getPostTimeMillis(), System.currentTimeMillis());
                        holder.postTime.setText(ta.calculateTimeAgo());
                    }
                    if (recentsItemFormats.get(position).getPostedBy().getUsername() != null) {
                        holder.postedBy.setText(recentsItemFormats.get(position).getPostedBy().getUsername());
                        holder.postedBy.setOnClickListener(openUserProfileListener);
                        name = recentsItemFormats.get(position).getPostedBy().getUsername();
                        clickableSpanPostedBy = new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(recentsItemFormats.get(position).getPostedBy().getUID())) {
                                    Intent i = new Intent(context, OpenUserDetail.class);
                                    i.putExtra("Uid", recentsItemFormats.get(position).getPostedBy().getUID());
                                    context.startActivity(i);
//                                    mHomeActivity.changeFragment(4);
                                } else {

                                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                                    HashMap<String, String> meta = new HashMap<>();
                                    meta.put("type", "fromRecentsRV");
                                    meta.put("userType", "openUserProfile");
                                    meta.put("userUID", recentsItemFormats.get(position).getPostedBy().getUID());

                                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                    counterItemFormat.setUniqueID(CounterUtilities.KEY_PROFILE_OPEN);
                                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                                    counterItemFormat.setMeta(meta);

                                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                    counterPush.pushValues();

                                    Intent i = new Intent(context, OpenUserDetail.class);
                                    i.putExtra("Uid", recentsItemFormats.get(position).getPostedBy().getUID());
                                    context.startActivity(i);
                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                ds.setUnderlineText(false);
                            }
                        };
                        if (recentsItemFormats.get(position).getFeature().equals("Message") && recentsItemFormats.get(position).getDesc2().equals("y")) {
                            holder.postedBy.setOnClickListener(null);
                            clickableSpanPostedBy = null;
                        }
                    }
                    if (recentsItemFormats.get(position).getPostedBy().getImageThumb() != null) {
                        holder.avatarCircle.setImageURI(recentsItemFormats.get(position).getPostedBy().getImageThumb());
                        holder.avatarCircle.setOnClickListener(openUserProfileListener);
                    }

                    if (recentsItemFormats.get(position).getPostedBy().getUID().equals(FirebaseAuth.getInstance().getUid())) {
                        holder.deleteButton.setVisibility(View.VISIBLE);
                        holder.deletePost(recentsItemFormats.get(position).getPostID());
                    }
                } catch (Exception e) {
                    Log.d("Error Message", e.getMessage());
                }
                holder.post.setTextColor(context.getResources().getColor(R.color.link));

                if (recentsItemFormats.get(position).getFeature().equals("Banner")) {
                    holder.prePostDetails.setVisibility(View.GONE);
                    holder.infoneRecentItem.setVisibility(View.GONE);
                    holder.storeroomRecentItem.setVisibility(View.GONE);
                    holder.cabpoolRecentItem.setVisibility(View.GONE);
                    holder.eventsRecentItem.setVisibility(View.GONE);
                    holder.messagesRecentItem.setVisibility(View.GONE);
                    holder.forumsRecentItem.setVisibility(View.GONE);
                    holder.noticesRecentItem.setVisibility(View.GONE);
                    holder.bannerRecentItem.setVisibility(View.VISIBLE);
                    holder.youtubeLink.setVisibility(View.GONE);
                    holder.pollLinearLayout.setVisibility(View.GONE);
                    holder.updateAppRecentItem.setVisibility(View.GONE);

                    Picasso.with(context).load(recentsItemFormats.get(position).getImageurl()).into(holder.bannerImage);
//                    holder.bannerImage.setImageURI(recentsItemFormats.get(position).getImageurl());
                    holder.bannerImage.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            holder.bannerLinkLayout.setVisibility(View.VISIBLE);
                            holder.bannerLinkLayout.setOnClickListener(new OnSingleClickListener() {
                                @Override
                                public void onSingleClick(View v) {

                                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                                    HashMap<String, String> meta = new HashMap<>();

                                    meta.put("URL", recentsItemFormats.get(position).getDesc());
                                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                    counterItemFormat.setUniqueID(CounterUtilities.KEY_RECENTS_BANNER_CLICK);
                                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                                    counterItemFormat.setMeta(meta);

                                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                    counterPush.pushValues();

                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(recentsItemFormats.get(position).getDesc())));

                                }
                            });
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(3000);
                                    } catch (InterruptedException ie) {
                                        Log.d("Interrupted Error", ie.getMessage());
                                    }

                                    mHomeActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Do some stuff
                                            holder.bannerLinkLayout.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            };
                            thread.start();
                        }
                    });
                    holder.shimmerFrameLayout.stopShimmerAnimation();
                    holder.shimmerFrameLayout.setVisibility(View.GONE);

                }
                else if (recentsItemFormats.get(position).getFeature().equals("Infone")) {
                    holder.prePostDetails.setVisibility(View.VISIBLE);
                    holder.post.setVisibility(View.VISIBLE);
                    holder.infoneRecentItem.setVisibility(View.VISIBLE);
                    holder.storeroomRecentItem.setVisibility(View.GONE);
                    holder.cabpoolRecentItem.setVisibility(View.GONE);
                    holder.eventsRecentItem.setVisibility(View.GONE);
                    holder.messagesRecentItem.setVisibility(View.GONE);
                    holder.forumsRecentItem.setVisibility(View.GONE);
                    holder.bannerRecentItem.setVisibility(View.GONE);
                    holder.noticesRecentItem.setVisibility(View.GONE);
                    holder.youtubeLink.setVisibility(View.GONE);
                    holder.pollLinearLayout.setVisibility(View.GONE);
                    holder.updateAppRecentItem.setVisibility(View.GONE);

                    holder.featureIcon.setColorFilter(context.getResources().getColor(R.color.secondaryText), PorterDuff.Mode.SRC_ATOP);
                    holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_people_white_24dp));
                    holder.layoutFeatureIcon.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            mHomeActivity.changeFragment(3);
                        }
                    });
                    holder.postConjunction.setText(" added a ");
                    holder.post.setText("contact");
                    holder.infoneNameCategorySentence.setText("Contact of " +
                            recentsItemFormats.get(position).getInfoneContactName() +
                            " added in " +
                            recentsItemFormats.get(position).getInfoneContactCategoryName());
                    posted = " added a ";
                    post = "contact";
                    clickableSpanFeature = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            mHomeActivity.changeFragment(3);
                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                            HashMap<String, String> meta = new HashMap<>();

                            meta.put("type", "fromRecentsRV");

                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                            counterItemFormat.setUniqueID(CounterUtilities.KEY_INFONE_TAB_OPEN);
                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                            counterItemFormat.setMeta(meta);

                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                            counterPush.pushValues();
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            ds.setUnderlineText(false);
                        }
                    };
                    holder.shimmerFrameLayout.stopShimmerAnimation();
                    holder.shimmerFrameLayout.setVisibility(View.GONE);

                }
                else if (recentsItemFormats.get(position).getFeature().equals("Event")) {
                    holder.prePostDetails.setVisibility(View.VISIBLE);
                    holder.post.setVisibility(View.VISIBLE);
                    holder.infoneRecentItem.setVisibility(View.GONE);
                    holder.storeroomRecentItem.setVisibility(View.GONE);
                    holder.cabpoolRecentItem.setVisibility(View.GONE);
                    holder.messagesRecentItem.setVisibility(View.GONE);
                    holder.forumsRecentItem.setVisibility(View.GONE);
                    holder.eventsRecentItem.setVisibility(View.VISIBLE);
                    holder.bannerRecentItem.setVisibility(View.GONE);
                    holder.noticesRecentItem.setVisibility(View.GONE);
                    holder.youtubeLink.setVisibility(View.GONE);
                    holder.pollLinearLayout.setVisibility(View.GONE);
                    holder.updateAppRecentItem.setVisibility(View.GONE);

                    holder.featureIcon.setColorFilter(context.getResources().getColor(R.color.secondaryText), PorterDuff.Mode.SRC_ATOP);
                    holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_event_white_24dp));
                    holder.layoutFeatureIcon.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            FirebaseDatabase.getInstance().getReference().child("minimumClientVersion")
                                    .child("events").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Log.d("VERSIONN", dataSnapshot.getValue(Integer.class) + "");
                                            if (dataSnapshot.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                                Intent intent = new Intent(context, UpdateAppActivity.class);
                                                intent.putExtra("feature", "shops");
                                                context.startActivity(intent);

                                            } else {
                                                Intent intent = new Intent(context, TabbedEvents.class);
                                                context.startActivity(intent);
                                            }}});
                        }
                    });
                    holder.postConjunction.setText(" created an ");
                    holder.post.setText(recentsItemFormats.get(position).getFeature());
                    holder.post.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            Intent intent = new Intent(context, TabbedEvents.class);
                            context.startActivity(intent);
                        }
                    });
                    holder.eventName.setText(recentsItemFormats.get(position).getName());
                    try {
                        Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(recentsItemFormats.get(position).getDesc2());
                        holder.eventDate.setText(new SimpleDateFormat("EEE, dd MMM yyyy").format(date));
                        DateTimeZone indianZone = DateTimeZone.forID("Asia/Kolkata");
                        DateTime dt = new DateTime(date, indianZone);
                        String minOfHr = "";
                        minOfHr = dt.getMinuteOfHour() < 10 ? "0" + String.valueOf(dt.getMinuteOfHour()) : String.valueOf(dt.getMinuteOfHour());
                        if (dt.getHourOfDay() <= 12)
                            holder.eventTime.setText(dt.getHourOfDay() + ":" + minOfHr + " AM");
                        else
                            holder.eventTime.setText((dt.getHourOfDay() - 12) + ":" + minOfHr + " PM");
                    } catch (ParseException pe) {
                        Log.d("Error Alert ", pe.getMessage());
                        holder.eventDate.setText(recentsItemFormats.get(position).getDesc2());
                        holder.eventTime.setText("");
                    }
                    final String eventDescString = recentsItemFormats.get(position).getDesc();
                    if (eventDescString.length() < 55)
                        holder.eventDesc.setText(recentsItemFormats.get(position).getDesc());
                    else {
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View widget) {
                                FirebaseDatabase.getInstance().getReference().child("minimumClientVersion")
                                        .child("events").addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }

                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Log.d("VERSIONN", dataSnapshot.getValue(Integer.class) + "");
                                                if (dataSnapshot.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                                    Intent intent = new Intent(context, UpdateAppActivity.class);
                                                    intent.putExtra("feature", "shops");
                                                    context.startActivity(intent);

                                                } else {
                                                    holder.eventDesc.setMaxLines(Integer.MAX_VALUE);
                                                    holder.eventDesc.setText(eventDescString);
                                                }}});
                                                }

                            @Override
                            public void updateDrawState(@NonNull TextPaint ds) {
                                ds.setUnderlineText(false);
                            }
                        };
                        String withMore = eventDescString.substring(0, 54) + " more...";
                        SpannableString spannableString = new SpannableString(withMore);
                        spannableString.setSpan(clickableSpan, withMore.lastIndexOf("more..."), withMore.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        StyleSpan styleSpan = new StyleSpan(BOLD);
                        spannableString.setSpan(styleSpan, withMore.lastIndexOf("more..."), withMore.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        holder.eventDesc.setText(spannableString);
                        holder.eventDesc.setMovementMethod(LinkMovementMethod.getInstance());

                    }
                    Picasso.with(context).load(recentsItemFormats.get(position).getImageurl()).into(holder.eventImage);

                    posted = " created an ";
                    post = recentsItemFormats.get(position).getFeature();
                    clickableSpanFeature = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            FirebaseDatabase.getInstance().getReference().child("minimumClientVersion")
                                    .child("events").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Log.d("VERSIONN", dataSnapshot.getValue(Integer.class) + "");
                                            if (dataSnapshot.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                                Intent intent = new Intent(context, UpdateAppActivity.class);
                                                intent.putExtra("feature", "shops");
                                                context.startActivity(intent);

                                            } else {
                                                CounterItemFormat counterItemFormat = new CounterItemFormat();
                                                HashMap<String, String> meta = new HashMap<>();

                                                meta.put("type", "fromRecentsRV");

                                                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                                counterItemFormat.setUniqueID(CounterUtilities.KEY_EVENTS_OPEN);
                                                counterItemFormat.setTimestamp(System.currentTimeMillis());
                                                counterItemFormat.setMeta(meta);

                                                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                                counterPush.pushValues();

                                                Intent intent = new Intent(context, TabbedEvents.class);
                                                context.startActivity(intent);
                                            }}});
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            ds.setUnderlineText(false);
                        }
                    };
                    holder.shimmerFrameLayout.stopShimmerAnimation();
                    holder.shimmerFrameLayout.setVisibility(View.GONE);
                }
                else if (recentsItemFormats.get(position).getFeature().equals("createPoll"))
                {
                    holder.prePostDetails.setVisibility(View.VISIBLE);
                    holder.post.setVisibility(View.VISIBLE);
                    holder.pollLinearLayout.setVisibility(View.VISIBLE);
                    holder.infoneRecentItem.setVisibility(View.GONE);
                    holder.eventsRecentItem.setVisibility(View.GONE);
                    holder.cabpoolRecentItem.setVisibility(View.GONE);
                    holder.messagesRecentItem.setVisibility(View.GONE);
                    holder.forumsRecentItem.setVisibility(View.GONE);
                    holder.storeroomRecentItem.setVisibility(View.GONE);
                    holder.bannerRecentItem.setVisibility(View.GONE);
                    holder.noticesRecentItem.setVisibility(View.GONE);
                    holder.updateAppRecentItem.setVisibility(View.GONE);
                    holder.postConjunction.setText(" created a ");
                    holder.post.setText("Poll");
                    holder.post.setTypeface(Typeface.DEFAULT);
                    holder.pollQuestion.setText(recentsItemFormats.get(position).getQuestion());
                    holder.pollOptionA.setText(recentsItemFormats.get(position).getOptions().getOptionA());
                    holder.pollOptionB.setText(recentsItemFormats.get(position).getOptions().getOptionB());
                    holder.pollOptionC.setText(recentsItemFormats.get(position).getOptions().getOptionC());
                    holder.featureIcon.setColorFilter(context.getResources().getColor(R.color.secondaryText), PorterDuff.Mode.SRC_ATOP);
                    holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_outline_poll_24px));
                    holder.pollOptionsSelect(recentsItemFormats.get(position).getKey());
                    DatabaseReference databaseReferenceGetOptionSelected = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home/" + recentsItemFormats.get(position).getKey());

                    databaseReferenceGetOptionSelected.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {


                                //reset all views
                                holder.totalVoteCount.setVisibility(View.GONE);
                                holder.votePercentageA.setVisibility(View.GONE);
                                holder.votePercentageB.setVisibility(View.GONE);
                                holder.votePercentageC.setVisibility(View.GONE);
                                holder.pollAYes.setVisibility(View.GONE);
                                holder.pollANo.setVisibility(View.GONE);
                                holder.pollBYes.setVisibility(View.GONE);
                                holder.pollBNo.setVisibility(View.GONE);
                                holder.pollCYes.setVisibility(View.GONE);
                                holder.pollCNo.setVisibility(View.GONE);
                                holder.pollAResult.setVisibility(View.GONE);
                                holder.pollBResult.setVisibility(View.GONE);
                                holder.pollCResult.setVisibility(View.GONE);
                                holder.pollOptionA.setTypeface(null, Typeface.NORMAL);
                                holder.pollOptionB.setTypeface(null, Typeface.NORMAL);
                                holder.pollOptionC.setTypeface(null, Typeface.NORMAL);
                                holder.markerA.setTypeface(null, Typeface.NORMAL);
                                holder.markerB.setTypeface(null, Typeface.NORMAL);
                                holder.markerC.setTypeface(null, Typeface.NORMAL);
                                holder.votePercentageA.setTypeface(null, Typeface.NORMAL);
                                holder.votePercentageB.setTypeface(null, Typeface.NORMAL);
                                holder.votePercentageC.setTypeface(null, Typeface.NORMAL);
                                holder.pollALL.setBackground(context.getResources().getDrawable(R.drawable.round_ouline_poll_bg));
                                holder.pollBLL.setBackground(context.getResources().getDrawable(R.drawable.round_ouline_poll_bg));
                                holder.pollCLL.setBackground(context.getResources().getDrawable(R.drawable.round_ouline_poll_bg));
                                if (dataSnapshot.hasChild("usersList")) {
                                    if (dataSnapshot.child("usersList").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        holder.setPollResultsVisible(recentsItemFormats.get(position).getKey(),
                                                recentsItemFormats.get(position).getUsersList().get(FirebaseAuth.getInstance().getCurrentUser().getUid()).getOptionSelected());

                                    } else {
                                        Log.v("Create Poll", "No user found");
                                    }
                                } else {
                                    Log.v("Create Poll", "No user list found");
                                }

                            }catch (Exception e){}
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    holder.shimmerFrameLayout.stopShimmerAnimation();
                    holder.shimmerFrameLayout.setVisibility(View.GONE);
                }
                else if (recentsItemFormats.get(position).getFeature().equals("StoreRoom"))
                {

                    holder.prePostDetails.setVisibility(View.VISIBLE);
                    holder.post.setVisibility(View.VISIBLE);
                    holder.infoneRecentItem.setVisibility(View.GONE);
                    holder.eventsRecentItem.setVisibility(View.GONE);
                    holder.cabpoolRecentItem.setVisibility(View.GONE);
                    holder.messagesRecentItem.setVisibility(View.GONE);
                    holder.forumsRecentItem.setVisibility(View.GONE);
                    holder.storeroomRecentItem.setVisibility(View.VISIBLE);
                    holder.bannerRecentItem.setVisibility(View.GONE);
                    holder.noticesRecentItem.setVisibility(View.GONE);
                    holder.youtubeLink.setVisibility(View.GONE);
                    holder.pollLinearLayout.setVisibility(View.GONE);
                    holder.updateAppRecentItem.setVisibility(View.GONE);

//            Drawable[] layers = new Drawable[2];
//            layers[0] = context.getResources().getDrawable(R.drawable.feature_circle);
//            layers[0].setColorFilter(context.getResources().getColor(R.color.storeroom), PorterDuff.Mode.SRC_ATOP);
//            layers[1] = context.getResources().getDrawable(R.drawable.ic_local_mall_white_24dp);
//            LayerDrawable layerDrawable = new LayerDrawable(layers);
//            holder.featureCircle.setBackground(layerDrawable);
                    holder.featureIcon.setColorFilter(context.getResources().getColor(R.color.secondaryText), PorterDuff.Mode.SRC_ATOP);
                    holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_local_mall_white_24dp));
                    holder.layoutFeatureIcon.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            FirebaseDatabase.getInstance().getReference().child("minimumClientVersion").
                                    child("storeroom").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Log.d("VERSIONN", dataSnapshot.getValue(Integer.class) + "");
                                            if (dataSnapshot.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                                Intent intent = new Intent(context, UpdateAppActivity.class);
                                                intent.putExtra("feature", "shops");
                                                context.startActivity(intent);

                                            } else {
                                                Intent intent = new Intent(context, TabStoreRoom.class);
                                                context.startActivity(intent);
                                            }}});
                        }
                    });
                    if (recentsItemFormats.get(position).getProductType() != null && recentsItemFormats.get(position).getProductType().equals(ProductUtilities.TYPE_ASK_STR)) {
                        holder.postConjunction.setText(" asked for a ");
                    } else {
                        holder.postConjunction.setText(" added a ");
                    }
                    holder.post.setText("Product");
                    holder.post.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {

                            FirebaseDatabase.getInstance().getReference().child("minimumClientVersion")
                                    .child("storeroom").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Log.d("VERSIONN", dataSnapshot.getValue(Integer.class) + "");
                                            if (dataSnapshot.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                                Intent intent = new Intent(context, UpdateAppActivity.class);
                                                intent.putExtra("feature", "shops");
                                                context.startActivity(intent);

                                            } else {


                                                Intent intent = new Intent(context, TabStoreRoom.class);
                                                context.startActivity(intent);

                                            }}});
                        }
                    });
                    holder.productName.setText(recentsItemFormats.get(position).getName());
                    final String productDescString = recentsItemFormats.get(position).getDesc();
                    if (productDescString.length() < 55)
                        holder.productDesc.setText(productDescString);
                    else {
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View widget) {
                                FirebaseDatabase.getInstance().getReference().child("minimumClientVersion").
                                        child("storeroom").addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }

                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Log.d("VERSIONN", dataSnapshot.getValue(Integer.class) + "");
                                                if (dataSnapshot.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                                    Intent intent = new Intent(context, UpdateAppActivity.class);
                                                    intent.putExtra("feature", "shops");
                                                    context.startActivity(intent);

                                                } else {
                                                    holder.productDesc.setMaxLines(Integer.MAX_VALUE);
                                                    holder.productDesc.setText(productDescString);
                                                }}});
                            }

                            @Override
                            public void updateDrawState(@NonNull TextPaint ds) {
                                ds.setUnderlineText(false);
                            }
                        };

                        String withMore = productDescString.substring(0, 54) + " more...";
                        SpannableString spannableString = new SpannableString(withMore);
                        spannableString.setSpan(clickableSpan, withMore.lastIndexOf("more..."), withMore.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        StyleSpan styleSpan = new StyleSpan(BOLD);
                        spannableString.setSpan(styleSpan, withMore.lastIndexOf("more..."), withMore.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        holder.productDesc.setText(spannableString);
                        holder.productDesc.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                    if (recentsItemFormats.get(position).getImageurl() != null) {
                        holder.productImage.setVisibility(View.VISIBLE);
                        Picasso.with(context).load(recentsItemFormats.get(position).getImageurl()).into(holder.productImage);
                    } else
                        holder.productImage.setVisibility(View.GONE);
                    if (recentsItemFormats.get(position).getProductType() != null && recentsItemFormats.get(position).getProductType().equals(ProductUtilities.TYPE_ASK_STR)) {
                        holder.productPrice.setVisibility(View.GONE);
                        posted = " asked for a ";
                        post = "Product";
                    } else {
                        holder.productPrice.setVisibility(View.VISIBLE);
                        holder.productPrice.setText("₹" + recentsItemFormats.get(position).getProductPrice());
                        posted = " added a ";
                        post = "Product";
                    }
                    clickableSpanFeature = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {

                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                            HashMap<String, String> meta = new HashMap<>();

                            meta.put("type", "fromRecentsRV");
                            FirebaseDatabase.getInstance().getReference().child("minimumClientVersion").
                                    child("storeroom").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Log.d("VERSIONN", dataSnapshot.getValue(Integer.class) + "");
                                            if (dataSnapshot.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                                Intent intent = new Intent(context, UpdateAppActivity.class);
                                                intent.putExtra("feature", "shops");
                                                context.startActivity(intent);

                                            } else {

                                                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                                counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_OPEN);
                                                counterItemFormat.setTimestamp(System.currentTimeMillis());
                                                counterItemFormat.setMeta(meta);

                                                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                                counterPush.pushValues();
                                                Intent intent = new Intent(context, TabStoreRoom.class);
                                                context.startActivity(intent);
                                            }}});
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            ds.setUnderlineText(false);
                        }
                    };
                    holder.shimmerFrameLayout.stopShimmerAnimation();
                    holder.shimmerFrameLayout.setVisibility(View.GONE);
                    //set product price
                }
                else if (recentsItemFormats.get(position).getFeature().equals("CabPool")) {
                    holder.prePostDetails.setVisibility(View.VISIBLE);
                    holder.post.setVisibility(View.VISIBLE);
                    holder.infoneRecentItem.setVisibility(View.GONE);
                    holder.eventsRecentItem.setVisibility(View.GONE);
                    holder.storeroomRecentItem.setVisibility(View.GONE);
                    holder.messagesRecentItem.setVisibility(View.GONE);
                    holder.forumsRecentItem.setVisibility(View.GONE);
                    holder.cabpoolRecentItem.setVisibility(View.VISIBLE);
                    holder.bannerRecentItem.setVisibility(View.GONE);
                    holder.noticesRecentItem.setVisibility(View.GONE);
                    holder.youtubeLink.setVisibility(View.GONE);
                    holder.pollLinearLayout.setVisibility(View.GONE);
                    holder.updateAppRecentItem.setVisibility(View.GONE);

                    holder.postConjunction.setText(" started a ");
                    holder.post.setText(recentsItemFormats.get(position).getFeature());
                    holder.post.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            FirebaseDatabase.getInstance().getReference().child("minimumClientVersion").
                                    child("cabpool").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Log.d("VERSIONN", dataSnapshot.getValue(Integer.class) + "");
                                            if (dataSnapshot.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                                Intent intent = new Intent(context, UpdateAppActivity.class);
                                                intent.putExtra("feature", "shops");
                                                context.startActivity(intent);

                                            } else {
                                                CounterItemFormat counterItemFormat = new CounterItemFormat();
                                                HashMap<String, String> meta = new HashMap<>();
                                                meta.put("type", "fromRecentsRV");
                                                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                                counterItemFormat.setUniqueID(CounterUtilities.KEY_CABPOOL_OPEN);
                                                counterItemFormat.setTimestamp(System.currentTimeMillis());
                                                counterItemFormat.setMeta(meta);

                                                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                                counterPush.pushValues();
                                                Intent intent = new Intent(context, CabPoolAll.class);
                                                context.startActivity(intent);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    });
                    holder.cabpoolSource.setText(recentsItemFormats.get(position).getCabpoolSource());
                    holder.cabpoolDestination.setText(recentsItemFormats.get(position).getCabpoolDestination());
                    DateTimeZone indianZone = DateTimeZone.forID("Asia/Kolkata");
                    DateTime date = null;
                    try {
                        DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
                        date = dtf.parseDateTime(recentsItemFormats.get(position).getCabpoolDate());
                    } catch (Exception e) {
                    }
                    String dateText = date.toString("MMM") + " " + date.getDayOfMonth();
                    holder.cabpoolDate.setText(dateText);
                    if (recentsItemFormats.get(position).getCabpoolTimeFrom() != -1) {
                        String fromAmPm = recentsItemFormats.get(position).getCabpoolTimeFrom() < 12 ? "AM" : "PM";
                        int fromTime = recentsItemFormats.get(position).getCabpoolTimeFrom() <= 12 ? recentsItemFormats.get(position).getCabpoolTimeFrom() : recentsItemFormats.get(position).getCabpoolTimeFrom() - 12;
                        fromTime = fromTime == 0 ? 12 : fromTime;
                        String toAmPm = recentsItemFormats.get(position).getCabpoolTimeTo() < 12 ? "AM" : "PM";
                        int toTime = recentsItemFormats.get(position).getCabpoolTimeTo() <= 12 ? recentsItemFormats.get(position).getCabpoolTimeTo() : recentsItemFormats.get(position).getCabpoolTimeTo() - 12;
                        toTime = toTime == 0 ? 12 : toTime;
                        String timeText = fromTime + " " + fromAmPm + " - " + toTime + " " + toAmPm;
                        holder.cabpoolTime.setText(timeText);
                    } else {
                        String timeText = recentsItemFormats.get(position).getCabpoolTime();
                        int fromTime = Integer.parseInt(timeText.substring(0, timeText.indexOf(":")));
                        String fromAmPm = fromTime < 12 ? "AM" : "PM";
                        fromTime = fromTime <= 12 ? fromTime : fromTime - 12;
                        fromTime = fromTime == 0 ? 12 : fromTime;
                        int toTime = Integer.parseInt(timeText.substring(timeText.indexOf("to") + 3, timeText.lastIndexOf(":")));
                        String toAmPm = toTime < 12 ? "AM" : "PM";
                        toTime = toTime <= 12 ? toTime : toTime - 12;
                        toTime = toTime == 0 ? 12 : toTime;
                        timeText = fromTime + " " + fromAmPm + " - " + toTime + " " + toAmPm;
                        holder.cabpoolTime.setText(timeText);
                    }
//            Drawable[] layers = new Drawable[2];
//            layers[0] = context.getResources().getDrawable(R.drawable.feature_circle);
//            layers[0].setColorFilter(context.getResources().getColor(R.color.cabpool), PorterDuff.Mode.SRC_ATOP);
//            layers[1] = context.getResources().getDrawable(R.drawable.ic_local_taxi_white_18dp);
//            LayerDrawable layerDrawable = new LayerDrawable(layers);
//            holder.featureCircle.setBackground(layerDrawable);
                    holder.featureIcon.setColorFilter(context.getResources().getColor(R.color.secondaryText), PorterDuff.Mode.SRC_ATOP);
                    holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_local_taxi_white_24dp));
                    holder.layoutFeatureIcon.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {

                            FirebaseDatabase.getInstance().getReference().child("minimumClientVersion").
                                    child("cabpool").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Log.d("VERSIONN", dataSnapshot.getValue(Integer.class) + "");
                                            if (dataSnapshot.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                                Intent intent = new Intent(context, UpdateAppActivity.class);
                                                intent.putExtra("feature", "shops");
                                                context.startActivity(intent);

                                            }
                                            else {
                                                Intent intent = new Intent(context, CabPoolAll.class);
                                                context.startActivity(intent);
                                            }}});
                        }
                    });

                    posted = " started a ";
                    post = recentsItemFormats.get(position).getFeature();
                    clickableSpanFeature = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {

                            FirebaseDatabase.getInstance().getReference().child("minimumClientVersion").
                                    child("cabpool").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Log.d("VERSIONN", dataSnapshot.getValue(Integer.class) + "");
                                            if (dataSnapshot.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                                Intent intent = new Intent(context, UpdateAppActivity.class);
                                                intent.putExtra("feature", "shops");
                                                context.startActivity(intent);

                                            } else {

                                                CounterItemFormat counterItemFormat = new CounterItemFormat();
                                                HashMap<String, String> meta = new HashMap<>();

                                                meta.put("type", "fromRecentsRV");

                                                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                                counterItemFormat.setUniqueID(CounterUtilities.KEY_CABPOOL_OPEN);
                                                counterItemFormat.setTimestamp(System.currentTimeMillis());
                                                counterItemFormat.setMeta(meta);

                                                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                                counterPush.pushValues();

                                                Intent intent = new Intent(context, CabPoolAll.class);
                                                context.startActivity(intent);
                                            }
                                        }
                                    });

                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            ds.setUnderlineText(false);
                        }
                    };
                    //set text for source and destination...
                    holder.shimmerFrameLayout.stopShimmerAnimation();
                    holder.shimmerFrameLayout.setVisibility(View.GONE);
                }
//                else if (recentsItemFormats.get(position).getFeature().equals("Shop")) {
//                    holder.prePostDetails.setVisibility(View.VISIBLE);
//                    holder.post.setVisibility(View.VISIBLE);
//                    holder.infoneRecentItem.setVisibility(View.GONE);
//                    holder.eventsRecentItem.setVisibility(View.GONE);
//                    holder.storeroomRecentItem.setVisibility(View.GONE);
//                    holder.cabpoolRecentItem.setVisibility(View.GONE);
//                    holder.forumsRecentItem.setVisibility(View.GONE);
//                    holder.messagesRecentItem.setVisibility(View.GONE);
//                    holder.bannerRecentItem.setVisibility(View.GONE);
//                    holder.noticesRecentItem.setVisibility(View.GONE);
//                    holder.youtubeLink.setVisibility(View.GONE);
//                    holder.pollLinearLayout.setVisibility(View.GONE);
//                    holder.updateAppRecentItem.setVisibility(View.GONE);
//
//                    holder.featureIcon.setColorFilter(context.getResources().getColor(R.color.secondaryText), PorterDuff.Mode.SRC_ATOP);
//                    holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_store_white_24dp));
//                    holder.postConjunction.setText(" put an ");
//                    holder.post.setText("Offer");
//                    holder.layoutFeatureIcon.setOnClickListener(null);
//
//                    posted = " put an ";
//                    post = "Offer";
//                    clickableSpanFeature = null;
//                    holder.shimmerFrameLayout.stopShimmerAnimation();
//                    holder.shimmerFrameLayout.setVisibility(View.GONE);
//                }
                else if (recentsItemFormats.get(position).getFeature().equals("Message")) {
                    holder.prePostDetails.setVisibility(View.VISIBLE);
                    holder.post.setVisibility(View.VISIBLE);
                    holder.infoneRecentItem.setVisibility(View.GONE);
                    holder.eventsRecentItem.setVisibility(View.GONE);
                    holder.storeroomRecentItem.setVisibility(View.GONE);
                    holder.cabpoolRecentItem.setVisibility(View.GONE);
                    holder.forumsRecentItem.setVisibility(View.GONE);
                    holder.messagesRecentItem.setVisibility(View.VISIBLE);
                    holder.bannerRecentItem.setVisibility(View.GONE);
                    holder.noticesRecentItem.setVisibility(View.GONE);
                    holder.pollLinearLayout.setVisibility(View.GONE);
                    holder.updateAppRecentItem.setVisibility(View.GONE);
                    holder.setLike(recentsItemFormats.get(position).getKey());
                    if (recentsItemFormats.get(position).getDesc().length() <= 0)
                        holder.messagesMessage.setVisibility(View.GONE);
                    else
                        holder.messagesMessage.setVisibility(View.VISIBLE);
                    try {
                        if (!recentsItemFormats.get(position).getImageurl().equals(RecentTypeUtilities.KEY_RECENTS_NO_IMAGE_STATUS) && recentsItemFormats.get(position).getImageurl() != null) {
                            if (!recentsItemFormats.get(position).getImageurl().equals("https://www.iconexperience.com/_img/o_collection_png/green_dark_grey/512x512/plain/message.png")) {
                                holder.postImage.setVisibility(View.VISIBLE);
                                Picasso.with(context).load(recentsItemFormats.get(position).getImageurl()).into(holder.postImage);
                                holder.setOpenStatusImage(recentsItemFormats.get(position).getPostedBy().getUsername(), recentsItemFormats.get(position).getImageurl());
                            }
                        } else
                            holder.postImage.setVisibility(View.GONE);
                    } catch (Exception e) {
                    }

                    holder.featureIcon.setColorFilter(context.getResources().getColor(R.color.secondaryText), PorterDuff.Mode.SRC_ATOP);
                    holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_message_white_24dp));
                    holder.layoutFeatureIcon.setOnClickListener(null);
                    holder.postConjunction.setText(" wrote a ");
                    holder.post.setText("status");
                    holder.post.setTextColor(context.getResources().getColor(R.color.secondaryText));
                    holder.post.setTypeface(Typeface.DEFAULT);
                    final String statusMsg = recentsItemFormats.get(position).getDesc();
                    if (statusMsg.length() < 70 && holder.postImage.getVisibility() == View.GONE)
                        holder.messagesMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                    else
                        holder.messagesMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    if (statusMsg.length() < 180)
                        holder.messagesMessage.setText(recentsItemFormats.get(position).getDesc());
                    else {
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View widget) {
                                holder.messagesMessage.setMaxLines(Integer.MAX_VALUE);
                                holder.messagesMessage.setText(statusMsg);
                                Linkify.addLinks(holder.messagesMessage, Linkify.ALL);
                                holder.messagesMessage.setLinkTextColor(Color.BLUE);
                                holder.messagesMessage.setTypeface(Typeface.SANS_SERIF);
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                ds.setUnderlineText(false); // set to false to remove underline
                            }
                        };
                        String withMore = statusMsg.substring(0, 179) + " more...";
                        SpannableString spannableString = new SpannableString(withMore);
                        spannableString.setSpan(clickableSpan, withMore.lastIndexOf("more..."), withMore.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        StyleSpan styleSpan = new StyleSpan(BOLD);
                        spannableString.setSpan(styleSpan, withMore.lastIndexOf("more..."), withMore.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        holder.messagesMessage.setText(spannableString);
                        holder.messagesMessage.setMovementMethod(LinkMovementMethod.getInstance());
                        Linkify.addLinks(holder.messagesMessage, Linkify.ALL);
                    }
                    Linkify.addLinks(holder.messagesMessage, Linkify.ALL);
                    holder.messagesMessage.setLinkTextColor(Color.BLUE);
                    holder.messagesMessage.setTypeface(Typeface.SANS_SERIF);
                    if (recentsItemFormats.get(position).getDesc2().equals("y")) {
                        holder.name.setText("Anonymous");
                        holder.avatarCircle.setImageURI("https://firebasestorage.googleapis.com/v0/b/zconnectmulticommunity.appspot.com/o/Icons%2Fanonymous.jpg?alt=media&token=259d06b2-626d-4df8-b8cc-f525195473ab");
                        holder.avatarCircle.setOnClickListener(null);
//                holder.avatarCircle.setBackground(context.getResources().getDrawable(R.drawable.question_mark_icon));
                    } else {
                        //Message is not anonymous
                        holder.name.setText(recentsItemFormats.get(position).getName());
                    }
                    holder.setOpenComments();
                    holder.totalComments.setVisibility(View.GONE);
                    try {
                        if (recentsItemFormats.get(position).getMsgComments() != 0) {

                            holder.totalComments.setVisibility(View.VISIBLE);
                            holder.totalComments.setText("" + recentsItemFormats.get(position).getMsgComments());

                        }


                    }catch (Exception e){
                        Log.e("Error: ", e.getMessage());

                    }

                    //youtube Link code
                    if(recentsItemFormats.get(position).getDesc().length()>16) {
                        if ((recentsItemFormats.get(position).getDesc().substring(0, 17).equals("https://youtu.be/")) || (recentsItemFormats.get(position).getDesc().length() > 21 && recentsItemFormats.get(position).getDesc().substring(0,22).equals("https://m.youtube.com/"))) {
                            holder.youtubeLink.setVisibility(View.VISIBLE);
                            try {
                                videoId = recentsItemFormats.get(position).getDesc().substring(0, 17).equals("https://youtu.be/") ? recentsItemFormats.get(position).getDesc().substring(17) : recentsItemFormats.get(position).getDesc().substring(30);

                                Log.e("VideoId is->", "" + videoId);

                                String img_url = "http://img.youtube.com/vi/" + videoId + "/0.jpg"; // this is link which will give u thumnail image of that video

                                // picasso jar file download image for u and set image in imagview

                                Picasso.with(context).load(img_url).into(holder.iv_youtube_thumnail);

                            }
                    catch (Exception e) { e.printStackTrace(); }
                        }
                        else
                            holder.youtubeLink.setVisibility(View.GONE);
                    }

                    posted = "  wrote a ";
                    post = "status";
                    clickableSpanFeature = null;

                    holder.shimmerFrameLayout.stopShimmerAnimation();
                    holder.shimmerFrameLayout.setVisibility(View.GONE);
                }
                else if (recentsItemFormats.get(position).getFeature().equals("Notices")) {
                    holder.prePostDetails.setVisibility(View.VISIBLE);
                    holder.post.setVisibility(View.VISIBLE);
                    holder.infoneRecentItem.setVisibility(View.GONE);
                    holder.eventsRecentItem.setVisibility(View.GONE);
                    holder.storeroomRecentItem.setVisibility(View.GONE);
                    holder.cabpoolRecentItem.setVisibility(View.GONE);
                    holder.messagesRecentItem.setVisibility(View.GONE);
                    holder.forumsRecentItem.setVisibility(View.GONE);
                    holder.bannerRecentItem.setVisibility(View.GONE);
                    holder.noticesRecentItem.setVisibility(View.VISIBLE);
                    holder.youtubeLink.setVisibility(View.GONE);
                    holder.pollLinearLayout.setVisibility(View.GONE);
                    holder.updateAppRecentItem.setVisibility(View.GONE);

                    holder.featureIcon.setColorFilter(context.getResources().getColor(R.color.secondaryText), PorterDuff.Mode.SRC_ATOP);
                    holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.baseline_insert_photo_white_24));
                    holder.setOpenNoticeImage(recentsItemFormats.get(position).getName(), recentsItemFormats.get(position).getImageurl());
                    holder.layoutFeatureIcon.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                            HashMap<String, String> meta = new HashMap<>();
                            meta.put("type", "fromRecentsRV");
                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                            counterItemFormat.setUniqueID(CounterUtilities.KEY_NOTICES_OPEN);
                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                            counterItemFormat.setMeta(meta);

                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                            counterPush.pushValues();
                            Intent intent = new Intent(context, Notices.class);
                            context.startActivity(intent);

                        }
                    });

                    holder.noticesText.setText(recentsItemFormats.get(position).getName());
                    holder.noticesImage.setImageURI(recentsItemFormats.get(position).getImageurl());
//                    Picasso.with(context).load(recentsItemFormats.get(position).getImageurl()).into(holder.noticesImage);

                    holder.postConjunction.setText(" posted a ");
                    holder.post.setText("Notice");
                    holder.post.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                            HashMap<String, String> meta = new HashMap<>();
                            meta.put("type", "fromRecentsRV");
                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                            counterItemFormat.setUniqueID(CounterUtilities.KEY_NOTICES_OPEN);
                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                            counterItemFormat.setMeta(meta);

                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                            counterPush.pushValues();
                            Intent intent = new Intent(context, Notices.class);
                            context.startActivity(intent);
                        }
                    });

                    posted = " posted a ";
                    post = "Notice";
                    clickableSpanFeature = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            FirebaseDatabase.getInstance().getReference().child("minimumClientVersion").
                                    child("notices").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot d) {
                                            Log.d("VERSIONN", d.getValue(Integer.class) + "");
                                            if (d.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                                Intent intent = new Intent(context, UpdateAppActivity.class);
                                                intent.putExtra("feature", "shops");
                                                context.startActivity(intent);

                                            } else {

                                                CounterItemFormat counterItemFormat = new CounterItemFormat();
                                                HashMap<String, String> meta = new HashMap<>();

                                                meta.put("type", "fromRecentsRV");

                                                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                                counterItemFormat.setUniqueID(CounterUtilities.KEY_NOTICES_OPEN);
                                                counterItemFormat.setTimestamp(System.currentTimeMillis());
                                                counterItemFormat.setMeta(meta);

                                                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                                counterPush.pushValues();

                                                Intent intent = new Intent(context, Notices.class);
                                                context.startActivity(intent);
                                            }}});
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            ds.setUnderlineText(false);
                        }
                    };

                    holder.shimmerFrameLayout.stopShimmerAnimation();
                    holder.shimmerFrameLayout.setVisibility(View.GONE);
                }
                else if (recentsItemFormats.get(position).getFeature().equals("Forums")) {
                    holder.prePostDetails.setVisibility(View.VISIBLE);
                    holder.post.setVisibility(View.VISIBLE);
                    holder.infoneRecentItem.setVisibility(View.GONE);
                    holder.eventsRecentItem.setVisibility(View.GONE);
                    holder.storeroomRecentItem.setVisibility(View.GONE);
                    holder.cabpoolRecentItem.setVisibility(View.GONE);
                    holder.messagesRecentItem.setVisibility(View.GONE);
                    holder.forumsRecentItem.setVisibility(View.VISIBLE);
                    holder.bannerRecentItem.setVisibility(View.GONE);
                    holder.noticesRecentItem.setVisibility(View.GONE);
                    holder.youtubeLink.setVisibility(View.GONE);
                    holder.pollLinearLayout.setVisibility(View.GONE);
                    holder.updateAppRecentItem.setVisibility(View.GONE);

                    holder.featureIcon.setColorFilter(context.getResources().getColor(R.color.secondaryText), PorterDuff.Mode.SRC_ATOP);
                    holder.featureIcon.setImageDrawable(context.getDrawable(R.drawable.ic_forum_white_24dp));
                    holder.layoutFeatureIcon.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            mHomeActivity.changeFragment(1);
                        }
                    });
                    holder.postConjunction.setText(" created a ");
                    holder.post.setText(recentsItemFormats.get(position).getFeature());
                    holder.forumNameCategorySentence.setText(recentsItemFormats.get(position).getName());
                    holder.forumTabName.setText("in " + recentsItemFormats.get(position).getDesc());
                    if(recentsItemFormats.get(position).getImageThumb()!=null)
                    {
                        holder.forumDefaultIcon.setVisibility(View.GONE);
                        holder.forumImage.setImageURI(recentsItemFormats.get(position).getImageThumb());
                    }
                    else
                    {
                        holder.forumDefaultIcon.setVisibility(View.VISIBLE);
                        holder.forumImage.setImageResource(android.R.color.transparent);
                        holder.forumImage.setBackground(context.getResources().getDrawable(R.drawable.avatar_circle_128dp));
                    }
                    posted = " created a ";
                    post = "Forum";
                    clickableSpanFeature = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                            HashMap<String, String> meta = new HashMap<>();

                            meta.put("type", "fromRecentsRV");

                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                            counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_TAB_OPEN);
                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                            counterItemFormat.setMeta(meta);

                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                            counterPush.pushValues();

                            mHomeActivity.changeFragment(1);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            ds.setUnderlineText(false);
                        }
                    };
                    holder.shimmerFrameLayout.stopShimmerAnimation();
                    holder.shimmerFrameLayout.setVisibility(View.GONE);
                }
                else
                {
                    holder.updateAppRecentItem.setVisibility(View.VISIBLE);
                    holder.prePostDetails.setVisibility(View.GONE);
                    holder.infoneRecentItem.setVisibility(View.GONE);
                    holder.eventsRecentItem.setVisibility(View.GONE);
                    holder.cabpoolRecentItem.setVisibility(View.GONE);
                    holder.messagesRecentItem.setVisibility(View.GONE);
                    holder.forumsRecentItem.setVisibility(View.GONE);
                    holder.storeroomRecentItem.setVisibility(View.GONE);
                    holder.bannerRecentItem.setVisibility(View.GONE);
                    holder.noticesRecentItem.setVisibility(View.GONE);
                    holder.pollLinearLayout.setVisibility(View.GONE);
                    holder.shimmerFrameLayout.stopShimmerAnimation();
                    holder.shimmerFrameLayout.setVisibility(View.GONE);
                    holder.updateAppRecentItem.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                            i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.zconnect.zutto.zconnect"));
                            context.startActivity(i);

                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                            HashMap<String, String> meta = new HashMap<>();
                            meta.put("type", "fromRecentsRV");
                            meta.put("not", "feature");
                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                            counterItemFormat.setUniqueID(CounterUtilities.KEY_RECENTS_UPDATE_APP_CLICK);
                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                            counterItemFormat.setMeta(meta);
                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                            counterPush.pushValues();
                        }
                    });
                }
                String sentence = name + posted + post;
                SpannableString spannableString = new SpannableString(sentence);
                spannableString.setSpan(clickableSpanPostedBy, sentence.indexOf(name), sentence.indexOf(name) + name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                spannableString.setSpan(clickableSpanFeature, sentence.indexOf(post), sentence.indexOf(post) + post.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.black));
                spannableString.setSpan(foregroundColorSpan, sentence.indexOf(name), sentence.indexOf(name) + name.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                if(post.equals("status"))
                    foregroundColorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.black));

                else
                    foregroundColorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.black));
                spannableString.setSpan(foregroundColorSpan, sentence.indexOf(post), sentence.indexOf(post) + post.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                foregroundColorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.black));
                spannableString.setSpan(foregroundColorSpan, sentence.indexOf(posted), sentence.indexOf(posted) + posted.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new android.text.style.StyleSpan(BOLD), sentence.indexOf(name), sentence.indexOf(name) + name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new android.text.style.StyleSpan(BOLD), sentence.indexOf(post), sentence.indexOf(post) + post.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.sentence.setText(spannableString);
                holder.sentence.setMovementMethod(LinkMovementMethod.getInstance());
                break;
            default:
                UpdateAppViewHolder updateAppViewHolder = (UpdateAppViewHolder) holder2;
                break;
        }

    }

    @Override
    public int getItemCount() {
        return recentsItemFormats.size();
    }

    class Viewholder extends RecyclerView.ViewHolder {

        TextView feature, name, desc;
        //        SimpleDraweeView simpleDraweeView;
        Intent i;
        String nam;

        ShimmerFrameLayout shimmerFrameLayout;
        //new ui
        TextView postedBy, postConjunction, post, postTime,
                infoneNameCategorySentence,
                cabpoolSource, cabpoolDestination, cabpoolDate, cabpoolTime,
                eventName, eventDate, eventTime, eventDesc,
                productName, productPrice, productDesc,
                messagesMessage,
                forumNameCategorySentence, forumTabName,
                sentence,totalComments,
                noticesText,
                pollQuestion,pollOptionA,pollOptionB,pollOptionC,
                markerA, markerB, markerC,
                totalVoteCount, votePercentageA, votePercentageB, votePercentageC,
                poolName, poolDesc, poolDel;

        SimpleDraweeView featureCircle, avatarCircle,
                eventImage,
                postImage,
                productImage,
                bannerImage,
                noticesImage,
                forumImage,
                poolImage;
        ImageView featureIcon, iv_youtube_thumnail, iv_play, forumDefaultIcon;


        ImageButton deleteButton;


        LinearLayout infoneRecentItem, cabpoolRecentItem, eventsRecentItem, storeroomRecentItem, messagesRecentItem, forumsRecentItem, bannerRecentItem, prePostDetails, noticesRecentItem, pollLinearLayout,
                pollALL, pollAYes, pollANo, pollBLL, pollBYes, pollBNo, pollCLL, pollCYes, pollCNo, pollAResult, pollBResult, pollCResult, youtubeLink, updateAppRecentItem;

        FrameLayout layoutFeatureIcon, bannerLinkLayout, optionALayout, optionBLayout, optionCLayout;
        //
        long statusLikeCount;
        boolean statusLikeFlag;
        DatabaseReference mUserDetails;


        public Viewholder(View itemView) {
            super(itemView);
//            simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.recents_image);
            name = (TextView) itemView.findViewById(R.id.recentname);
            feature = (TextView) itemView.findViewById(R.id.featurename);
            desc = (TextView) itemView.findViewById(R.id.recentdesc);

            shimmerFrameLayout = itemView.findViewById(R.id.shimmer_view_container_recents_item_format);
            shimmerFrameLayout.startShimmerAnimation();

            pollQuestion = itemView.findViewById(R.id.poll_question);
            optionALayout = itemView.findViewById(R.id.optionA_Layout);
            pollOptionA = itemView.findViewById(R.id.optionA_option_text);
            markerA = itemView.findViewById(R.id.optionA_placeHolder);
            pollALL = itemView.findViewById(R.id.option_a_fullLayout);
            pollAYes = itemView.findViewById(R.id.optionA_result_success);
            pollANo = itemView.findViewById(R.id.optionA_result_failure);
            pollAResult = itemView.findViewById(R.id.poll_results_optionA);
            optionBLayout = itemView.findViewById(R.id.optionB_Layout);
            pollOptionB = itemView.findViewById(R.id.optionB_option_text);
            markerB = itemView.findViewById(R.id.optionB_placeHolder);
            pollBLL = itemView.findViewById(R.id.option_b_fullLayout);
            pollBYes = itemView.findViewById(R.id.optionB_result_success);
            pollBNo = itemView.findViewById(R.id.optionB_result_failure);
            pollBResult = itemView.findViewById(R.id.poll_results_optionB);
            optionCLayout = itemView.findViewById(R.id.optionC_Layout);
            pollOptionC = itemView.findViewById(R.id.optionC_option_text);
            markerC = itemView.findViewById(R.id.optionC_placeHolder);
            pollCLL = itemView.findViewById(R.id.option_c_fullLayout);
            pollCYes = itemView.findViewById(R.id.optionC_result_success);
            pollCNo = itemView.findViewById(R.id.optionC_result_failure);
            pollCResult = itemView.findViewById(R.id.poll_results_optionC);
            pollLinearLayout = itemView.findViewById(R.id.pollFormat);
            totalVoteCount = itemView.findViewById(R.id.total_vote_count);
            votePercentageA = itemView.findViewById(R.id.option_a_percent);
            votePercentageB = itemView.findViewById(R.id.option_b_percent);
            votePercentageC = itemView.findViewById(R.id.option_c_percent);

            //new ui
            postedBy = (TextView) itemView.findViewById(R.id.postedBy);
            postConjunction = (TextView) itemView.findViewById(R.id.postConjunction);
            post = (TextView) itemView.findViewById(R.id.post);
            postTime = (TextView) itemView.findViewById(R.id.postTime);
            featureCircle = (SimpleDraweeView) itemView.findViewById(R.id.featureCircle);
            featureIcon = (ImageView) itemView.findViewById(R.id.recents_featIcon);
            layoutFeatureIcon = (FrameLayout) itemView.findViewById(R.id.layout_feature_icon);
            avatarCircle = (SimpleDraweeView) itemView.findViewById(R.id.avatarCircle);
            infoneRecentItem = (LinearLayout) itemView.findViewById(R.id.infoneRecentItem);
            infoneNameCategorySentence = (TextView) itemView.findViewById(R.id.infone_name_with_category_sentence);
            cabpoolRecentItem = (LinearLayout) itemView.findViewById(R.id.cabpoolRecentItem);
            cabpoolSource = (TextView) itemView.findViewById(R.id.cabpoolRecentItem_source);
            cabpoolDestination = (TextView) itemView.findViewById(R.id.cabpoolRecentItem_destination);
            cabpoolDate = (TextView) itemView.findViewById(R.id.cabpoolRecentItem_date);
            cabpoolTime = (TextView) itemView.findViewById(R.id.cabpoolRecentItem_time);
            eventsRecentItem = (LinearLayout) itemView.findViewById(R.id.eventsRecentItem);
            eventName = (TextView) itemView.findViewById(R.id.eventsRecentItem_name);
            eventDate = (TextView) itemView.findViewById(R.id.eventsRecentItem_date);
            eventTime = (TextView) itemView.findViewById(R.id.eventsRecentItem_time);
            eventDesc = (TextView) itemView.findViewById(R.id.eventsRecentItem_description);
            eventImage = (SimpleDraweeView) itemView.findViewById(R.id.eventsRecentItem_image);
            storeroomRecentItem = (LinearLayout) itemView.findViewById(R.id.storeroomRecentItem);
            productName = (TextView) itemView.findViewById(R.id.storeroomRecentItem_name);
            productPrice = (TextView) itemView.findViewById(R.id.storeroomRecentItem_price);
            productDesc = (TextView) itemView.findViewById(R.id.storeroomRecentItem_description);
            productImage = (SimpleDraweeView) itemView.findViewById(R.id.storeroomRecentItem_image);
            messagesRecentItem = (LinearLayout) itemView.findViewById(R.id.messagesRecentItem);
            messagesMessage = (TextView) itemView.findViewById(R.id.messagesRecentItem_message);
            youtubeLink = itemView.findViewById(R.id.youtubelinklinearlayout);
            iv_youtube_thumnail=(ImageView)itemView.findViewById(R.id.img_thumnail);
            //iv_play=(ImageView)itemView.findViewById(R.id.iv_play_pause);
            forumsRecentItem = (LinearLayout) itemView.findViewById(R.id.forumsRecentItem);
            forumNameCategorySentence = (TextView) itemView.findViewById(R.id.forum_name_with_category_sentence);
            forumTabName = itemView.findViewById(R.id.forumsRecentItem_tab_name);
            forumImage = itemView.findViewById(R.id.forumsRecentItem_image);
            forumDefaultIcon = itemView.findViewById(R.id.forumsRecentItem_image_default_icon);
            postImage = (SimpleDraweeView) itemView.findViewById(R.id.messagesRecentItem_image);
            bannerRecentItem = (LinearLayout) itemView.findViewById(R.id.bannerRecentItem);
            bannerImage = (SimpleDraweeView) itemView.findViewById(R.id.bannerRecentItem_image);
            bannerLinkLayout = (FrameLayout) itemView.findViewById(R.id.bannerRecentItem_link_layout);
            prePostDetails = (LinearLayout) itemView.findViewById(R.id.prePostDetails);
            sentence = (TextView) itemView.findViewById(R.id.sentence_recents_item_format);
            noticesRecentItem = (LinearLayout) itemView.findViewById(R.id.noticesRecentItem);
            noticesImage = (SimpleDraweeView) itemView.findViewById(R.id.noticesRecentItem_image);
            noticesText = (TextView) itemView.findViewById(R.id.noticesRecentItem_text);
            updateAppRecentItem = itemView.findViewById(R.id.update_app_recent_item);


            mUserDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            deleteButton = (ImageButton) itemView.findViewById(R.id.recents_post_options);


            totalComments = (TextView) itemView.findViewById(R.id.comment_text_status);

            itemView.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    if (recentsItemFormats.get(getAdapterPosition()).getFeature().equals("Event")) {
                        FirebaseDatabase.getInstance().getReference().child("minimumClientVersion").
                                child("events").addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Log.d("VERSIONN", dataSnapshot.getValue(Integer.class) + "");
                                        if (dataSnapshot.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                            Intent intent = new Intent(context, UpdateAppActivity.class);
                                            intent.putExtra("feature", "shops");
                                            context.startActivity(intent);

                                        } else {
                                            i = new Intent(context, OpenEventDetail.class);
                                            try {
                                                CounterItemFormat counterItemFormat = new CounterItemFormat();
                                                HashMap<String, String> meta = new HashMap<>();
                                                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                                counterItemFormat.setUniqueID(CounterUtilities.KEY_EVENTS_OPEN_EVENT);
                                                counterItemFormat.setTimestamp(System.currentTimeMillis());
                                                meta.put("type", "fromRecentsRV");
                                                counterItemFormat.setMeta(meta);
                                                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                                counterPush.pushValues();
                                                i.putExtra("id", recentsItemFormats.get(getAdapterPosition()).getId());
                                                context.startActivity(i);
                                            } catch (Exception e) {
                                                Log.d("Error Alert: ", e.getMessage());
                                            }
                                        }}});

                        //context.startActivity(i);
                        //mHomeActivity.finish();
                        //mHome.finish();
                    }
                    else if(recentsItemFormats.get(getAdapterPosition()).getFeature().equals("createPoll"))
                    {
                        //Clicking on the layout item as a whole will cause nothing in the UI
//                        pollOptionsSelect(recentsItemFormats.get(getAdapterPosition()).getKey());
                    }
                    else if (recentsItemFormats.get(getAdapterPosition()).getFeature().equals("StoreRoom")) {
                          try{
                              FirebaseDatabase.getInstance().getReference().child("minimumClientVersion").
                                      child("storeroom").addListenerForSingleValueEvent(
                                      new ValueEventListener() {
                                          @Override
                                          public void onCancelled(@NonNull DatabaseError databaseError) {

                                          }

                                          @Override
                                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                              Log.d("VERSIONN", dataSnapshot.getValue(Integer.class) + "");
                                              if (dataSnapshot.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                                  Intent intent = new Intent(context, UpdateAppActivity.class);
                                                  intent.putExtra("feature", "shops");
                                                  context.startActivity(intent);

                                              } else {
                                                  CounterItemFormat counterItemFormat = new CounterItemFormat();
                                                  HashMap<String, String> meta = new HashMap<>();
                                                  counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                                  counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_OPEN_PRODUCT);
                                                  counterItemFormat.setTimestamp(System.currentTimeMillis());
                                                  meta.put("type", "fromRecentsRV");
                                                  counterItemFormat.setMeta(meta);
                                                  CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                                  counterPush.pushValues();
                                                  i = new Intent(context, OpenProductDetails.class);
                                                  i.putExtra("key", recentsItemFormats.get(getAdapterPosition()).getId());
                                                  String productType = recentsItemFormats.get(getAdapterPosition()).getProductType() != null ?
                                                          recentsItemFormats.get(getAdapterPosition()).getProductType() : ProductUtilities.TYPE_ADD_STR;
                                                  i.putExtra("type", productType);
                                                  context.startActivity(i);
                                              }}});

                          } catch(Exception e) {
                              Log.d("Error Alert: ", e.getMessage());
                            }
                    }
//                    else if (recentsItemFormats.get(getAdapterPosition()).getFeature().equals("Shop")) {
//                        try {
//                            FirebaseDatabase.getInstance().getReference().child("minimumClientVersion").
//                                    child("shop").addListenerForSingleValueEvent(
//                                    new ValueEventListener() {
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                        }
//
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                            Log.d("VERSIONN", dataSnapshot.getValue(Integer.class) + "");
//                                            if (dataSnapshot.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
//                                                Intent intent = new Intent(context, UpdateAppActivity.class);
//                                                intent.putExtra("feature", "shops");
//                                                context.startActivity(intent);
//
//                                            } else {
//                                                i = new Intent(context, Shop_detail.class);
//                                                i.putExtra("ShopId", recentsItemFormats.get(getAdapterPosition()).getId());
//                                                i.putExtra("Name", recentsItemFormats.get(getAdapterPosition()).getName());
//                                                i.putExtra("Imageurl", recentsItemFormats.get(getAdapterPosition()).getImageurl());
//                                                context.startActivity(i);
//                                            }}});
//                        } catch (Exception e) {
//                            Log.d("Error Alert: ", e.getMessage());
//                        }
//                    }
                    else if (recentsItemFormats.get(getAdapterPosition()).getFeature().equals("CabPool")) {

                        FirebaseDatabase.getInstance().getReference().child("minimumClientVersion").
                                child("cabpool").addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Log.d("VERSIONN", dataSnapshot.getValue(Integer.class) + "");
                                        if (dataSnapshot.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                            Intent intent = new Intent(context, UpdateAppActivity.class);
                                            intent.putExtra("feature", "shops");
                                            context.startActivity(intent);

                                        } else {

                                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                                            HashMap<String, String> meta = new HashMap<>();
                                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                            counterItemFormat.setUniqueID(CounterUtilities.KEY_CABPOOL_OPEN_LIST_OF_PEOPLE);
                                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                                            meta.put("type", "fromRecentsRV");
                                            counterItemFormat.setMeta(meta);
                                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                            counterPush.pushValues();


                                            i = new Intent(context, CabPoolListOfPeople.class);
                                            Log.e("check", "executed");
                                            i.putExtra("key", recentsItemFormats.get(getAdapterPosition()).getId());
                                            i.putExtra("date", recentsItemFormats.get(getAdapterPosition()).getDT());
                                            i.putExtra("sourceText", recentsItemFormats.get(getAdapterPosition()).getCabpoolSource());
                                            i.putExtra("destinationText", recentsItemFormats.get(getAdapterPosition()).getCabpoolDestination());
                                            i.putExtra("timeText", recentsItemFormats.get(getAdapterPosition()).getCabpoolTime());
                                            i.putExtra("dateText", recentsItemFormats.get(getAdapterPosition()).getCabpoolDate());
                                            i.putExtra("postedByText", recentsItemFormats.get(getAdapterPosition()).getPostedBy().getUsername());
                                            i.putExtra("postedByImageText", recentsItemFormats.get(getAdapterPosition()).getPostedBy().getImageThumb());
                                            context.startActivity(i);
                                        }}});
                    } else if (recentsItemFormats.get(getAdapterPosition()).getFeature().equals("Infone")){

                        try {
                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                            HashMap<String, String> meta = new HashMap<>();
                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                            counterItemFormat.setUniqueID(CounterUtilities.KEY_INFONE_CONTACT_OPEN);
                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                            meta.put("type", "fromRecentsRV");
                            meta.put("catID", recentsItemFormats.get(getAdapterPosition()).getDesc());
                            counterItemFormat.setMeta(meta);
                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                            counterPush.pushValues();

                            i = new Intent(context, InfoneProfileActivity.class);
                            i.putExtra("infoneUserId", recentsItemFormats.get(getAdapterPosition()).getId());
                            i.putExtra("catID", recentsItemFormats.get(getAdapterPosition()).getDesc());
                            context.startActivity(i);
                        } catch (Exception e) {
                        }
                    } else if (recentsItemFormats.get(getAdapterPosition()).getFeature().equals("Forums")) {

                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                        HashMap<String, String> meta = new HashMap<>();
                        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                        counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_CHANNEL_OPEN);
                        counterItemFormat.setTimestamp(System.currentTimeMillis());
                        meta.put("type", "fromRecentsRV");
                        meta.put("catID", recentsItemFormats.get(getAdapterPosition()).getId());
                        meta.put("channelID", recentsItemFormats.get(getAdapterPosition()).getKey());
                        counterItemFormat.setMeta(meta);
                        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                        counterPush.pushValues();

                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("type", "forums");
                        intent.putExtra("key", recentsItemFormats.get(getAdapterPosition()).getKey());
                        intent.putExtra("tab", recentsItemFormats.get(getAdapterPosition()).getId());
                        intent.putExtra("name", recentsItemFormats.get(getAdapterPosition()).getName());
                        intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(recentsItemFormats.get(getAdapterPosition()).getKey()).toString());
                        context.startActivity(intent);
                    }else if(recentsItemFormats.get(getAdapterPosition()).getFeature().equals("Message")){
                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                        HashMap<String, String> meta= new HashMap<>();
                        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                        counterItemFormat.setUniqueID(CounterUtilities.KEY_RECENTS_ADDED_STATUS);
                        counterItemFormat.setTimestamp(System.currentTimeMillis());
                        meta.put("type","fromRecentsRV");
                        meta.put("catID",recentsItemFormats.get(getAdapterPosition()).getId());
                        meta.put("channelID",recentsItemFormats.get(getAdapterPosition()).getKey());
                        counterItemFormat.setMeta(meta);
                        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                        counterPush.pushValues();

                        Intent intent = new Intent(context, OpenStatus.class);
                        intent.putExtra("key", recentsItemFormats.get(getAdapterPosition()).getKey());
                        System.out.println(recentsItemFormats.get(getAdapterPosition()).getKey());
                        context.startActivity(intent);
                    }
                }
            });

            Typeface quicksandLight = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
            Typeface quicksandMedium = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Medium.ttf");
            Typeface quicksandBold = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-ExtraLight.ttf");
            name.setTypeface(quicksandMedium);
            feature.setTypeface(quicksandBold);
            desc.setTypeface(quicksandLight);
        }

        public void deletePost(final String postID) {


            deleteButton.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    Dialog deletePostDialog = new Dialog(context);
                    deletePostDialog.setContentView(R.layout.new_dialog_box);
                    deletePostDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    deletePostDialog.findViewById(R.id.dialog_box_image_sdv).setBackground(ContextCompat.getDrawable(context,R.drawable.ic_message_white_24dp));
                    TextView heading =  deletePostDialog.findViewById(R.id.dialog_box_heading);
                    heading.setText("Confirm");
                    TextView body = deletePostDialog.findViewById(R.id.dialog_box_body);
                    body.setText("Please confirm to delete this post!");
                    Button positiveButton = deletePostDialog.findViewById(R.id.dialog_box_positive_button);
                    positiveButton.setText("CONFIRM");
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                            HashMap<String, String> meta = new HashMap<>();
                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                            counterItemFormat.setUniqueID(CounterUtilities.KEY_RECENTS_DELETE_POST);
                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                            counterItemFormat.setMeta(meta);
                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                            counterPush.pushValues();

                            FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home").child(postID).removeValue();
                            recentsItemFormats.remove(getAdapterPosition());
                            notifyDataSetChanged();
                            deletePostDialog.dismiss();

                        }
                    });
                    Button negativeButton = deletePostDialog.findViewById(R.id.dialog_box_negative_button);
                    negativeButton.setText("CANCEL");
                    negativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deletePostDialog.dismiss();
                        }
                    });



                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    String[] options = {"Delete this Post"};
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    deletePostDialog.show();
                            }
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorHighlight));
                }

            });

        }

        public void setOpenComments() {

            final RelativeLayout commentLayout = (RelativeLayout) itemView.findViewById(R.id.messagesRecentItem_comment_layout);

            commentLayout.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                    HashMap<String, String> meta = new HashMap<>();
                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                    counterItemFormat.setUniqueID(CounterUtilities.KEY_RECENTS_COMMENT);
                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                    counterItemFormat.setMeta(meta);
                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                    counterPush.pushValues();
                    Intent intent = new Intent(context, OpenStatus.class);
                    intent.putExtra("key", recentsItemFormats.get(getAdapterPosition()).getKey());
                    intent.putExtra("isFromCommentBtn", true);
                    Log.d("username" , " "+recentsItemFormats.get(getAdapterPosition()).getPostedBy().getUsername());
                    System.out.println(recentsItemFormats.get(getAdapterPosition()).getKey());
                    context.startActivity(intent);  }
                                    });

                }




        public void setOpenStatusImage(final String name, final String imageURL) {

            postImage.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {

                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                    HashMap<String, String> meta = new HashMap<>();
                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                    counterItemFormat.setUniqueID(CounterUtilities.KEY_RECENTS_OPEN_IMAGE);
                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                    counterItemFormat.setMeta(meta);
                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                    counterPush.pushValues();

                    ProgressDialog mProgress = new ProgressDialog(context);
                    mProgress.setMessage("Loading...");
                    mProgress.show();
                    animate((Activity) context, name, imageURL, postImage);
                    mProgress.dismiss();

                }
            });

        }

        public void setOpenNoticeImage(final String title, final String imageURL) {

            noticesImage.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {

                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                    HashMap<String, String> meta = new HashMap<>();

                    meta.put("type", "fromRecentsRV");
                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                    counterItemFormat.setUniqueID(CounterUtilities.KEY_NOTICES_OPEN_NOTICE);
                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                    counterItemFormat.setMeta(meta);

                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                    counterPush.pushValues();

                    ProgressDialog mProgress = new ProgressDialog(itemView.getContext());
                    mProgress.setMessage("Loading...");
                    mProgress.show();
                    animate((Activity) itemView.getContext(), title, imageURL, noticesImage);
                    mProgress.dismiss();

                }
            });

        }


        public void animate(final Activity activity, final String name, String url, ImageView productImage) {

            final Intent i = new Intent(context, viewImage.class);
            i.putExtra("currentEvent", name);
            i.putExtra("eventImage", url);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            final ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, productImage, context.getResources().getString(R.string.transition_string));
            context.startActivity(i, optionsCompat.toBundle());
        }

        public void setLike(final String key) {

            final DatabaseReference statusDatabase = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home").child(key);
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final RelativeLayout likeLayout = (RelativeLayout) itemView.findViewById(R.id.messagesRecentItem_like_layout);
            final ImageView likeIcon = (ImageView) itemView.findViewById(R.id.like_image_status);
            final TextView likeText = (TextView) itemView.findViewById(R.id.like_text_status);




            statusDatabase.child("likeUids").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    statusLikeCount = dataSnapshot.getChildrenCount();
                    statusDatabase.child("likeCount").setValue(dataSnapshot.getChildrenCount());

                    if (dataSnapshot.hasChild(user.getUid())) {
//                        boostBtn.setText(dataSnapshot.getChildrenCount() + " Boost");
                        if (dataSnapshot.getChildrenCount() > 0)
                            likeText.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                        else
                            likeText.setText("");
                        likeText.setTextColor(context.getResources().getColor(R.color.black));
                        likeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_thumb_up_alt_white_24));
                        likeIcon.setColorFilter(context.getResources().getColor(R.color.deepPurple500));
                        statusLikeFlag=true;
                    }else {

//                        boostBtn.setText(dataSnapshot.getChildrenCount() + " Boost");
                        if (dataSnapshot.getChildrenCount() > 0)
                            likeText.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                        else
                            likeText.setText("");
                        likeText.setTextColor(context.getResources().getColor(R.color.icon_color));
                        likeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.outline_thumb_up_alt_white_24));
//                        likeIcon.setBackground(context.getResources().getDrawable(R.drawable.outline_thumb_up_alt_24));
                        likeIcon.setColorFilter(itemView.getContext().getResources().getColor(R.color.icon_color));
                        statusLikeFlag = false;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if (user != null) {
                likeLayout.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        if(statusLikeFlag == true){
                            statusLikeFlag = false;
                            statusDatabase.child("likeUids").child(user.getUid()).removeValue();

                            likeText.setText(String.valueOf(Integer.valueOf(likeText.getText().toString())-1));
                            if(likeText.getText().toString().equals("0")){
                                likeText.setText("");
                            }
                            likeIcon.setColorFilter(itemView.getContext().getResources().getColor(R.color.icon_color));
                            likeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.outline_thumb_up_alt_white_24));
                        }else{
                            statusLikeFlag = true;
                            Map<String, Object> taskMap = new HashMap<String, Object>();
                            taskMap.put(user.getUid(), user.getUid());
                            statusDatabase.child("likeUids").updateChildren(taskMap);
                            final NotificationSender notificationSender = new NotificationSender(itemView.getContext(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                            final NotificationItemFormat statusLikeNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_STATUS_LIKED, FirebaseAuth.getInstance().getCurrentUser().getUid());
                            // HashMap<String,Object> hashmap=new HashMap<>();
                            // hashmap.put("meta",1);
                            statusLikeNotification.setItemKey(key);
                            mUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    UserItemFormat userItem = dataSnapshot.getValue(UserItemFormat.class);
                                    statusLikeNotification.setUserImage(userItem.getImageURLThumbnail());
                                    statusLikeNotification.setUserName(userItem.getUsername());
                                    statusLikeNotification.setUserKey(userItem.getUserUID());
                                    statusLikeNotification.setCommunityName(communityTitle);
                                    statusLikeNotification.setItemLikeCount(statusLikeCount);
                                    HashMap<String, Object> metadata = new HashMap<>();
                                    metadata.put("key", recentsItemFormats.get(getAdapterPosition()).getKey());
                                    metadata.put("featurePID", recentsItemFormats.get(getAdapterPosition()).getKey());
                                    GlobalFunctions.inAppNotifications("liked your status",recentsItemFormats.get(getAdapterPosition()).getDesc(),userItem,false,"status",metadata, recentsItemFormats.get(getAdapterPosition()).getPostedBy().getUID());
                                    notificationSender.execute(statusLikeNotification);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            likeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_thumb_up_alt_white_24));
                            likeIcon.setColorFilter(context.getResources().getColor(R.color.deepPurple500));
                            if(likeText.getText().toString().equals("")){
                                likeText.setText("1");
                            }else {
                                likeText.setText(String.valueOf(Integer.valueOf(likeText.getText().toString()) + 1));
                            }

                        }
                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                        HashMap<String, String> meta = new HashMap<>();
                        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                        counterItemFormat.setUniqueID(CounterUtilities.KEY_RECENTS_LIKE);
                        counterItemFormat.setTimestamp(System.currentTimeMillis());
                        counterItemFormat.setMeta(meta);
                        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                        counterPush.pushValues();

                    }
                });

            } else {
                likeLayout.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(itemView.getContext());
                        dialog.setNegativeButton("Lite", null)
                                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent loginIntent = new Intent(itemView.getContext(), LoginActivity.class);
                                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        itemView.getContext().startActivity(loginIntent);
                                    }
                                })
                                .setTitle("Please login to boost.")
                                .create().show();
                    }
                });
            }

//            Typeface customfont = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
//            eventNumLit.setTypeface(customfont);
        }

        public void setPollResultsVisible(String key, String selectedOption){

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home").child(key);
            ref.child("options").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int optionACount = Integer.parseInt(dataSnapshot.child("optionACount").getValue().toString());
                    int optionBCount = Integer.parseInt(dataSnapshot.child("optionBCount").getValue().toString());
                    int optionCCount = Integer.parseInt(dataSnapshot.child("optionCCount").getValue().toString());

                    int sum = optionACount + optionBCount + optionCCount;
                    totalVoteCount.setVisibility(View.VISIBLE);
                    totalVoteCount.setText(sum==1?"1 vote": sum+" votes");
                    float optionAFraction = 1;
                    float optionBFraction = 1;
                    float optionCFraction = 1;

                    optionAFraction = (float) optionACount / sum;
                    optionBFraction = (float) optionBCount / sum;
                    optionCFraction = (float) optionCCount / sum;

                    votePercentageA.setVisibility(View.VISIBLE);
                    votePercentageB.setVisibility(View.VISIBLE);
                    votePercentageC.setVisibility(View.VISIBLE);
                    votePercentageA.setText(Math.round(optionAFraction*100)+"%");
                    votePercentageB.setText(Math.round(optionBFraction*100)+"%");
                    votePercentageC.setText(Math.round(optionCFraction*100)+"%");

                    LinearLayout.LayoutParams paramsAYes = (LinearLayout.LayoutParams) pollAYes.getLayoutParams();
                    LinearLayout.LayoutParams paramsBYes = (LinearLayout.LayoutParams) pollBYes.getLayoutParams();
                    LinearLayout.LayoutParams paramsCYes = (LinearLayout.LayoutParams) pollCYes.getLayoutParams();
                    LinearLayout.LayoutParams paramsANo = (LinearLayout.LayoutParams) pollANo.getLayoutParams();
                    LinearLayout.LayoutParams paramsBNo = (LinearLayout.LayoutParams) pollBNo.getLayoutParams();
                    LinearLayout.LayoutParams paramsCNo = (LinearLayout.LayoutParams) pollCNo.getLayoutParams();

                    paramsAYes.weight = (float) optionAFraction;
                    paramsBYes.weight = (float) optionBFraction;
                    paramsCYes.weight = (float) optionCFraction;
                    paramsANo.weight = (float) 1 - optionAFraction;
                    paramsBNo.weight = (float) 1 - optionBFraction;
                    paramsCNo.weight = (float) 1 - optionCFraction;

                    pollAYes.setLayoutParams(paramsAYes);
                    pollBYes.setLayoutParams(paramsBYes);
                    pollCYes.setLayoutParams(paramsCYes);
                    pollANo.setLayoutParams(paramsANo);
                    pollBNo.setLayoutParams(paramsBNo);
                    pollCNo.setLayoutParams(paramsCNo);


                    //set background
                    pollALL.setBackgroundResource(0);
                    pollBLL.setBackgroundResource(0);
                    pollCLL.setBackgroundResource(0);
                    if (optionACount == 0){
                        pollANo.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner_purple_light));
                    }else{
                        pollANo.setBackground(context.getResources().getDrawable(R.drawable.right_rounded_corner_purple_border));
                    }

                    if (optionBCount == 0){
                        pollBNo.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner_purple_light));
                    }else{
                        pollBNo.setBackground(context.getResources().getDrawable(R.drawable.right_rounded_corner_purple_border));
                    }

                    if (optionCCount == 0){
                        pollCNo.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner_purple_light));
                    }else{
                        pollCNo.setBackground(context.getResources().getDrawable(R.drawable.right_rounded_corner_purple_border));
                    }
                    pollAYes.setBackground(context.getResources().getDrawable(R.drawable.left_rounded_corner_full_purple_border));

                    pollBYes.setBackground(context.getResources().getDrawable(R.drawable.left_rounded_corner_full_purple_border));

                    pollCYes.setBackground(context.getResources().getDrawable(R.drawable.left_rounded_corner_full_purple_border));

                    //make selected option bg purple and text bold
                    if(selectedOption.equals("optionA"))
                    {
                        pollAYes.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner_purple_dark));
                        pollOptionA.setTypeface(null, BOLD);
                        markerA.setTypeface(null, BOLD);
                        votePercentageA.setTypeface(null, BOLD);
                    }
                    else if(selectedOption.equals("optionB"))
                    {
                        pollBYes.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner_purple_dark));
                        pollOptionB.setTypeface(null, BOLD);
                        markerB.setTypeface(null, BOLD);
                        votePercentageB.setTypeface(null, BOLD);
                    }
                    else if(selectedOption.equals("optionC"))
                    {
                        pollCYes.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner_purple_dark));
                        pollOptionC.setTypeface(null, BOLD);
                        markerC.setTypeface(null, BOLD);
                        votePercentageC.setTypeface(null, BOLD);
                    }


                    pollAYes.setVisibility(View.VISIBLE);
                    pollANo.setVisibility(View.VISIBLE);
                    pollBYes.setVisibility(View.VISIBLE);
                    pollBNo.setVisibility(View.VISIBLE);
                    pollCYes.setVisibility(View.VISIBLE);
                    pollCNo.setVisibility(View.VISIBLE);
                    pollAResult.setVisibility(View.VISIBLE);
                    pollBResult.setVisibility(View.VISIBLE);
                    pollCResult.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public void pollOptionsSelect(String key) {

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home/" + key);

            pollALL.setOnClickListener(view -> {
                Log.d("SZCH", "clicked!");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("usersList"))
                        {
                            if (dataSnapshot.child("usersList").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            {
                                Log.e("Create Poll","User has already selected an option");
                            }
                            else
                            {
                                count = dataSnapshot.child("options").child("optionACount").getValue(Long.class);
                                count++;
                                reference.child("usersList").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("optionSelected").setValue("optionA");
                                reference.child("options").child("optionACount").setValue(count);
                                String key1 = recentsItemFormats.get(getAdapterPosition()).getKey();
                                setPollResultsVisible(key1, "optionA");

                                CounterItemFormat counterItemFormat = new CounterItemFormat();
                                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                counterItemFormat.setUniqueID(CounterUtilities.KEY_RECENTS_VOTE_POLL);
                                counterItemFormat.setTimestamp(System.currentTimeMillis());
                                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                counterPush.pushValues();
                            }
                        }
                        else {
                            count = dataSnapshot.child("options").child("optionACount").getValue(Long.class);
                            count++;
                            reference.child("usersList").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("optionSelected").setValue("optionA");
                            reference.child("options").child("optionACount").setValue(count);
                            String key1 = recentsItemFormats.get(getAdapterPosition()).getKey();
                            setPollResultsVisible(key1, "optionA");

                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                            counterItemFormat.setUniqueID(CounterUtilities.KEY_RECENTS_VOTE_POLL);
                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                            counterPush.pushValues();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            });

            pollBLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("usersList"))
                            {
                                if (dataSnapshot.child("usersList").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                {
                                    Log.e("Create Poll","User has already selected an option");
                                }
                                else
                                {
                                    count = dataSnapshot.child("options").child("optionBCount").getValue(Long.class);
                                    count++;
                                    reference.child("usersList").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("optionSelected").setValue("optionB");
                                    reference.child("options").child("optionBCount").setValue(count);
                                    String key  = recentsItemFormats.get(getAdapterPosition()).getKey();
                                    setPollResultsVisible(key, "optionB");

                                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                    counterItemFormat.setUniqueID(CounterUtilities.KEY_RECENTS_VOTE_POLL);
                                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                    counterPush.pushValues();

                                }
                            }
                            else {
                                count = dataSnapshot.child("options").child("optionBCount").getValue(Long.class);
                                count++;
                                reference.child("usersList").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("optionSelected").setValue("optionB");
                                reference.child("options").child("optionBCount").setValue(count);
                                String key  = recentsItemFormats.get(getAdapterPosition()).getKey();
                                setPollResultsVisible(key, "optionB");

                                CounterItemFormat counterItemFormat = new CounterItemFormat();
                                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                counterItemFormat.setUniqueID(CounterUtilities.KEY_RECENTS_VOTE_POLL);
                                counterItemFormat.setTimestamp(System.currentTimeMillis());
                                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                counterPush.pushValues();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });

            pollCLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild("usersList"))
                            {
                                if (dataSnapshot.child("usersList").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                {
                                    Log.e("Create Poll","User has already selected an option");
                                }
                                else
                                {
                                    count = dataSnapshot.child("options").child("optionCCount").getValue(Long.class);
                                    count++;
                                    reference.child("usersList").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("optionSelected").setValue("optionC");
                                    reference.child("options").child("optionCCount").setValue(count);
                                    String key  = recentsItemFormats.get(getAdapterPosition()).getKey();
                                    setPollResultsVisible(key, "optionC");

                                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                    counterItemFormat.setUniqueID(CounterUtilities.KEY_RECENTS_VOTE_POLL);
                                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                    counterPush.pushValues();
                                }
                            }
                            else {
                                count = dataSnapshot.child("options").child("optionCCount").getValue(Long.class);
                                count++;
                                reference.child("usersList").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("optionSelected").setValue("optionC");
                                reference.child("options").child("optionCCount").setValue(count);
                                String key  = recentsItemFormats.get(getAdapterPosition()).getKey();
                                setPollResultsVisible(key, "optionC");

                                CounterItemFormat counterItemFormat = new CounterItemFormat();
                                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                counterItemFormat.setUniqueID(CounterUtilities.KEY_RECENTS_VOTE_POLL);
                                counterItemFormat.setTimestamp(System.currentTimeMillis());
                                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                counterPush.pushValues();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
        }

    }

    class ViewHolderStatus extends RecyclerView.ViewHolder {

        LinearLayout textArea;
        SimpleDraweeView userAvatar;
        DatabaseReference mUserDetails;
        DatabaseReference totalMembersRef;
        TextView leaderBoardText;
        TextView totalMembers;
        LinearLayout totalMembersLayout;
        ShimmerFrameLayout shimmerFrameLayout;

        public ViewHolderStatus(final View itemView) {
            super(itemView);
            mUserDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            totalMembersRef = FirebaseDatabase.getInstance().getReference().child("communitiesInfo").child(communityReference).child("size");
            userAvatar = (SimpleDraweeView) itemView.findViewById(R.id.avatarCircle_recents_status_add);
            textArea = (LinearLayout) itemView.findViewById(R.id.text_area_recents_status_add);
            totalMembers = itemView.findViewById(R.id.total_members);
            leaderBoardText = itemView.findViewById(R.id.leader_board_text);
            totalMembersLayout = itemView.findViewById(R.id.total_members_layout);

            mUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final UserItemFormat user = dataSnapshot.getValue(UserItemFormat.class);
                    userAvatar.setImageURI(user.getImageURLThumbnail());

                    textArea.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!(user.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || user.getUserType().equals(UsersTypeUtilities.KEY_PENDING))) {

                                CounterItemFormat counterItemFormat = new CounterItemFormat();
                                HashMap<String, String> meta = new HashMap<>();
                                meta.put("type", "fromRecentsRV");
                                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                counterItemFormat.setUniqueID(CounterUtilities.KEY_RECENTS_ADD_STATUS);
                                counterItemFormat.setTimestamp(System.currentTimeMillis());
                                counterItemFormat.setMeta(meta);
                                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                counterPush.pushValues();

                                Intent intent = new Intent(context, AddStatus.class);
                                context.startActivity(intent);
                            } else {
                                newUserVerificationAlert.buildAlertCheckNewUser(user.getUserType(), "Add Status", context);
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            leaderBoardText.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), LeaderBoard.class);
                    itemView.getContext().startActivity(intent);
                }
            });

            totalMembersLayout.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    Toast.makeText(context, "Top the leader board by inviting your friends", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, ReferralCode.class);
                    context.startActivity(intent);
                }
            });

            totalMembersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        int members_num = dataSnapshot.getValue(Integer.class);
                        if (members_num >= 10 && members_num < 100)
                            members_num = (members_num / 10) * 10;
                        else if (members_num >= 100 && members_num < 1000)
                            members_num = (members_num / 100) * 100;
                        else if (members_num >= 1000 && members_num < 10000)
                            members_num = (members_num / 1000) * 1000;
                        else if (members_num >= 10000)
                            members_num = 10000;
                        totalMembers.setText(members_num + "+");
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
    }

    private class BlankViewHolder extends RecyclerView.ViewHolder {

        public BlankViewHolder(View itemView) {
            super(itemView);
        }

    }

    private class UpdateAppViewHolder extends RecyclerView.ViewHolder {

        public UpdateAppViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.zconnect.zutto.zconnect"));
                    context.startActivity(i);

                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                    HashMap<String, String> meta = new HashMap<>();
                    meta.put("type", "fromRecentsRV");
                    meta.put("not", "recentType");
                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                    counterItemFormat.setUniqueID(CounterUtilities.KEY_RECENTS_UPDATE_APP_CLICK);
                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                    counterItemFormat.setMeta(meta);
                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                    counterPush.pushValues();
                }
            });
        }
    }

    private class ShopPoolViewHolder extends RecyclerView.ViewHolder {

        private TextView name, description, count, orderDeadlineTime, orderDealineSubtext, deliveryDay, totalOrders;
        private Button activateBtn, orderBtn;
        private RelativeLayout countdownWrapper;
        private LinearLayout orderDeadlineInfoLayout;
        private SimpleDraweeView poolBgImage;

        public ShopPoolViewHolder(View itemView) {
            super(itemView);
            attachID();
            itemView.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    Intent intent = new Intent(context, PoolActivity.class);
                    context.startActivity(intent);

                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                    HashMap<String, String> meta = new HashMap<>();
                    meta.put("type", "fromRecentsRV");
                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                    counterItemFormat.setUniqueID(CounterUtilities.KEY_SHOPS_OPEN);
                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                    counterItemFormat.setMeta(meta);
                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                    counterPush.pushValues();
                }
            });
        }

        private void attachID() {
            name = itemView.findViewById(R.id.pool_name);
            totalOrders = itemView.findViewById(R.id.total_orders);
            description = itemView.findViewById(R.id.pool_description);
            count = itemView.findViewById(R.id.pool_count);
            orderDeadlineTime = itemView.findViewById(R.id.order_deadline_time);
            orderDealineSubtext = itemView.findViewById(R.id.order_deadline_subtext);
            deliveryDay = itemView.findViewById(R.id.delivery_day);
            activateBtn = itemView.findViewById(R.id.activate_btn);
            orderBtn = itemView.findViewById(R.id.order_btn);
            countdownWrapper = itemView.findViewById(R.id.countdown_wrapper_layout);
            orderDeadlineInfoLayout = itemView.findViewById(R.id.order_deadline_info_layout);
            poolBgImage = itemView.findViewById(R.id.pool_bg_image);

            //
            countdownWrapper.setVisibility(View.GONE);
            activateBtn.setVisibility(View.GONE);
            orderBtn.setVisibility(View.GONE);
            totalOrders.setVisibility(View.GONE);
            count.setVisibility(View.GONE);
        }

        private void setBackgroundColors() {
            switch (getAdapterPosition()%4)
            {
                case 0:
                    name.getBackground().setColorFilter(itemView.getContext().getResources().getColor(R.color.lightgreen800), PorterDuff.Mode.SRC_ATOP);
                    totalOrders.getBackground().setColorFilter(itemView.getContext().getResources().getColor(R.color.lightgreen800), PorterDuff.Mode.SRC_ATOP);
                    orderDeadlineInfoLayout.getBackground().setColorFilter(itemView.getContext().getResources().getColor(R.color.red700), PorterDuff.Mode.SRC_ATOP);
                    break;
                case 1:
                    name.getBackground().setColorFilter(itemView.getContext().getResources().getColor(R.color.indigo800), PorterDuff.Mode.SRC_ATOP);
                    totalOrders.getBackground().setColorFilter(itemView.getContext().getResources().getColor(R.color.indigo800), PorterDuff.Mode.SRC_ATOP);
                    orderDeadlineInfoLayout.getBackground().setColorFilter(itemView.getContext().getResources().getColor(R.color.green700), PorterDuff.Mode.SRC_ATOP);
                    break;
                case 2:
                    name.getBackground().setColorFilter(itemView.getContext().getResources().getColor(R.color.red400), PorterDuff.Mode.SRC_ATOP);
                    totalOrders.getBackground().setColorFilter(itemView.getContext().getResources().getColor(R.color.red400), PorterDuff.Mode.SRC_ATOP);
                    orderDeadlineInfoLayout.getBackground().setColorFilter(itemView.getContext().getResources().getColor(R.color.yellow700), PorterDuff.Mode.SRC_ATOP);
                    break;
                case 3:
                    name.getBackground().setColorFilter(itemView.getContext().getResources().getColor(R.color.lightblue600), PorterDuff.Mode.SRC_ATOP);
                    totalOrders.getBackground().setColorFilter(itemView.getContext().getResources().getColor(R.color.lightblue600), PorterDuff.Mode.SRC_ATOP);
                    orderDeadlineInfoLayout.getBackground().setColorFilter(itemView.getContext().getResources().getColor(R.color.deeppurple700), PorterDuff.Mode.SRC_ATOP);
                    break;
            }
        }

        public void populate(final RecentsItemFormat itemFormat) {
            name.setText(itemFormat.getName());
            description.setText(itemFormat.getDesc());
            poolBgImage.setImageURI(itemFormat.getImageurl());

            setBackgroundColors();
            TimeUtilities tu = new TimeUtilities(itemFormat.getTimestampOrderReceivingDeadline());
            String deliveryTimeText = tu.getWeekName("SHORT") + ", " + tu.getTimeInHHMMAPM();
            String deliveryDayText = tu.getMonthName("SHORT") + " " + tu.getDateTime().getDayOfMonth();
            orderDeadlineTime.setText(deliveryTimeText);
            orderDealineSubtext.setVisibility(View.VISIBLE);
            deliveryDay.setText(deliveryDayText);
        }
    }

    private class FeaturesViewHolder extends RecyclerView.ViewHolder {

        private String TAG = FeaturesViewHolder.class.getSimpleName();

        HorizontalScrollView hsv;
        RelativeLayout leftArrow, rightArrow;
        LinearLayout linearLayout,totalLinearLayout;
        RelativeLayout notices, events, cabpool, storeroom, shops, admin,internships, links;
        FrameLayout unreadCountStoreroomFL, unreadCountEventsFL, unreadCountShopsFL, unreadCountCabpoolFL, unreadCountAdminPanelFL, unreadCountNoticesFL,unreadCountInternshipsFL, unreadCountLinksFL;
        TextView unreadCountStoreroomTV, unreadCountEventsTV, unreadCountShopsTV, unreadCountCabpoolTV, unreadCountAdminPanelTV, unreadCountNoticesTV,unreadCountInternshipsTV, unreadCountLinksTV;
        Query mOtherFeatures;

        //for other features
        SimpleDraweeView otherFeatureIcon;
        TextView featureName;
        LinearLayout otherFeatureItemLayout;
        DatabaseReference mUserDetails;

        public FeaturesViewHolder(final View itemView) {
            super(itemView);
            flag = true;


            hsv = (HorizontalScrollView) itemView.findViewById(R.id.hsv_recents_features_view);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout_recents_features_view);
            totalLinearLayout = itemView.findViewById(R.id.total_linear_layout);
            events = (RelativeLayout) itemView.findViewById(R.id.events_recents_features_view);
            notices = itemView.findViewById(R.id.notices_recents_features_view);
            storeroom = (RelativeLayout) itemView.findViewById(R.id.storeroom_recents_features_view);
            cabpool = (RelativeLayout) itemView.findViewById(R.id.cabpool_recents_features_view);
            internships = itemView.findViewById(R.id.internships_recents_features_view);
            admin = (RelativeLayout) itemView.findViewById(R.id.admin_recents_features_view);
            shops = (RelativeLayout) itemView.findViewById(R.id.shops_recents_features_view);
            links = (RelativeLayout) itemView.findViewById(R.id.links_recents_features_view);
            leftArrow = itemView.findViewById(R.id.leftArrow);
            rightArrow = itemView.findViewById(R.id.rightArrow);

            //Notification Count

            unreadCountLinksFL = (FrameLayout) itemView.findViewById(R.id.links_unread_count_fl_recents_feature_item);
            unreadCountLinksTV = (TextView) itemView.findViewById(R.id.links_unread_count_text_recents_feature_item);

            unreadCountStoreroomFL = (FrameLayout) itemView.findViewById(R.id.storeroom_unread_count_fl_recents_feature_item);
            unreadCountStoreroomTV = (TextView) itemView.findViewById(R.id.storeroom_unread_count_text_recents_feature_item);

            unreadCountEventsFL = (FrameLayout) itemView.findViewById(R.id.events_unread_count_fl_recents_feature_item);
            unreadCountEventsTV = (TextView) itemView.findViewById(R.id.events_unread_count_text_recents_feature_item);

            unreadCountShopsFL = (FrameLayout) itemView.findViewById(R.id.shops_unread_count_fl_recents_feature_item);
            unreadCountShopsTV = (TextView) itemView.findViewById(R.id.shops_unread_count_text_recents_feature_item);

            unreadCountCabpoolFL = (FrameLayout) itemView.findViewById(R.id.cabpool_unread_count_fl_recents_feature_item);
            unreadCountCabpoolTV = (TextView) itemView.findViewById(R.id.cabpool_unread_count_text_recents_feature_item);

            unreadCountInternshipsFL = (FrameLayout) itemView.findViewById(R.id.internships_unread_count_fl_recents_feature_item);
            unreadCountInternshipsTV = (TextView) itemView.findViewById(R.id.internships_unread_count_text_recents_feature_item);

            unreadCountAdminPanelFL = (FrameLayout) itemView.findViewById(R.id.admin_unread_count_fl_recents_feature_item);
            unreadCountAdminPanelTV = (TextView) itemView.findViewById(R.id.admin_unread_count_text_recents_feature_item);

            unreadCountNoticesFL = (FrameLayout) itemView.findViewById(R.id.notices_unread_count_fl_recents_feature_item);
            unreadCountNoticesTV = (TextView) itemView.findViewById(R.id.notices_unread_count_text_recents_feature_item);

            mOtherFeatures = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("otherFeatures").orderByChild("pos");
            mUserDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


            rightArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rightArrow.setVisibility(View.GONE);
                    leftArrow.setVisibility(View.VISIBLE);
                    hsv.fullScroll(View.FOCUS_LEFT);

                }
            });

            leftArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rightArrow.setVisibility(View.VISIBLE);
                    leftArrow.setVisibility(View.GONE);
                    hsv.fullScroll(View.FOCUS_RIGHT);
                }
            });


            mUserDetails.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    final UserItemFormat userItem = dataSnapshot.getValue(UserItemFormat.class);

                    if (!dataSnapshot.hasChild("userType")) {
                        Log.d("Try", "Set not verified");
                        userItem.setUserType(UsersTypeUtilities.KEY_NOT_VERIFIED);
                    }
                    //for admin
                    if ( Objects.requireNonNull(dataSnapshot.child("userType").getValue()).toString().equals(UsersTypeUtilities.KEY_ADMIN)) {
                        if (dataSnapshot.child("featuresUnreadCount").hasChild(FeatureDBName.KEY_ADMIN_PANEL)) {
                            final long current = dataSnapshot.child("featuresUnreadCount").child(FeatureDBName.KEY_ADMIN_PANEL).getValue(Long.class);
                            NumberNotificationForFeatures numberNotificationForFeatures = new NumberNotificationForFeatures(FeatureDBName.KEY_ADMIN_PANEL);
                            numberNotificationForFeatures.getCount(new NumberNotificationForFeatures.MyCallBack() {
                                @Override
                                public void onCallBack(long value) {
                                    if (value - current > 0) {
                                        Log.d(TAG, String.valueOf(value - current));
                                        unreadCountAdminPanelTV.setText(String.valueOf(value - current));
                                        unreadCountAdminPanelFL.setVisibility(View.VISIBLE);
                                    } else {
                                        unreadCountAdminPanelFL.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            NumberNotificationForFeatures numberNotificationForFeatures = new NumberNotificationForFeatures(FeatureDBName.KEY_ADMIN_PANEL);
                            numberNotificationForFeatures.getCount(new NumberNotificationForFeatures.MyCallBack() {
                                @Override
                                public void onCallBack(long value) {
                                    mUserDetails.child("featuresUnreadCount").child(FeatureDBName.KEY_ADMIN_PANEL).setValue(value);
                                }
                            });
                        }
                    }

                    //for Links
                    if (dataSnapshot.child("featuresUnreadCount").hasChild(FeatureDBName.KEY_LINKS)) {
                        final long current = dataSnapshot.child("featuresUnreadCount").child(FeatureDBName.KEY_LINKS).getValue(Long.class);
                        NumberNotificationForFeatures numberNotificationForFeatures = new NumberNotificationForFeatures(FeatureDBName.KEY_LINKS);
                        numberNotificationForFeatures.getCount(value -> {
                            if (value - current > 0) {
                                unreadCountLinksTV.setText(String.valueOf(value - current));
                                unreadCountLinksFL.setVisibility(View.VISIBLE);
                            } else {
                                unreadCountLinksFL.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        NumberNotificationForFeatures numberNotificationForFeatures = new NumberNotificationForFeatures(FeatureDBName.KEY_LINKS);
                        numberNotificationForFeatures.getCount(value -> mUserDetails.child("featuresUnreadCount").child(FeatureDBName.KEY_LINKS).setValue(value));
                    }

                    //for storeroom
                    if (dataSnapshot.child("featuresUnreadCount").hasChild(FeatureDBName.KEY_STOREROOM)) {
                        final long current = dataSnapshot.child("featuresUnreadCount").child(FeatureDBName.KEY_STOREROOM).getValue(Long.class);
                        NumberNotificationForFeatures numberNotificationForFeatures = new NumberNotificationForFeatures(FeatureDBName.KEY_STOREROOM);
                        numberNotificationForFeatures.getCount(new NumberNotificationForFeatures.MyCallBack() {
                            @Override
                            public void onCallBack(long value) {
                                if (value - current > 0) {
                                    unreadCountStoreroomTV.setText(String.valueOf(value - current));
                                    unreadCountStoreroomFL.setVisibility(View.VISIBLE);
                                } else {
                                    unreadCountStoreroomFL.setVisibility(View.GONE);
                                }
                            }
                        });
                    } else {
                        NumberNotificationForFeatures numberNotificationForFeatures = new NumberNotificationForFeatures(FeatureDBName.KEY_STOREROOM);
                        numberNotificationForFeatures.getCount(new NumberNotificationForFeatures.MyCallBack() {
                            @Override
                            public void onCallBack(long value) {
                                mUserDetails.child("featuresUnreadCount").child(FeatureDBName.KEY_STOREROOM).setValue(value);
                            }
                        });
                    }
                    //for events
                    if (dataSnapshot.child("featuresUnreadCount").hasChild(FeatureDBName.KEY_EVENTS)) {
                        final long current = dataSnapshot.child("featuresUnreadCount").child(FeatureDBName.KEY_EVENTS).getValue(Long.class);
                        NumberNotificationForFeatures numberNotificationForFeatures = new NumberNotificationForFeatures(FeatureDBName.KEY_EVENTS);
                        numberNotificationForFeatures.getCount(new NumberNotificationForFeatures.MyCallBack() {
                            @Override
                            public void onCallBack(long value) {
                                if (value - current > 0) {
                                    unreadCountEventsTV.setText(String.valueOf(value - current));
                                    unreadCountEventsFL.setVisibility(View.VISIBLE);
                                } else {
                                    unreadCountEventsFL.setVisibility(View.GONE);
                                }
                            }
                        });
                    } else {
                        NumberNotificationForFeatures numberNotificationForFeatures = new NumberNotificationForFeatures(FeatureDBName.KEY_EVENTS);
                        numberNotificationForFeatures.getCount(new NumberNotificationForFeatures.MyCallBack() {
                            @Override
                            public void onCallBack(long value) {
                                mUserDetails.child("featuresUnreadCount").child(FeatureDBName.KEY_EVENTS).setValue(value);
                            }
                        });
                    }

                    //for shops
                    if (dataSnapshot.child("featuresUnreadCount").hasChild(FeatureDBName.KEY_SHOPS)) {
                        final long current = dataSnapshot.child("featuresUnreadCount").child(FeatureDBName.KEY_SHOPS).getValue(Long.class);
                        NumberNotificationForFeatures numberNotificationForFeatures = new NumberNotificationForFeatures(FeatureDBName.KEY_SHOPS);
                        numberNotificationForFeatures.getCount(new NumberNotificationForFeatures.MyCallBack() {
                            @Override
                            public void onCallBack(long value) {
                                if (value - current > 0) {
                                    unreadCountShopsTV.setText(String.valueOf(value - current));
                                    unreadCountShopsFL.setVisibility(View.VISIBLE);
                                } else {
                                    unreadCountShopsFL.setVisibility(View.GONE);
                                }
                            }
                        });
                    } else {
                        NumberNotificationForFeatures numberNotificationForFeatures = new NumberNotificationForFeatures(FeatureDBName.KEY_SHOPS);
                        numberNotificationForFeatures.getCount(new NumberNotificationForFeatures.MyCallBack() {
                            @Override
                            public void onCallBack(long value) {
                                mUserDetails.child("featuresUnreadCount").child(FeatureDBName.KEY_SHOPS).setValue(value);
                            }
                        });
                    }

                    //for cabpool
                    if (dataSnapshot.child("featuresUnreadCount").hasChild(FeatureDBName.KEY_CABPOOL)) {
                        final long current = dataSnapshot.child("featuresUnreadCount").child(FeatureDBName.KEY_CABPOOL).getValue(Long.class);
                        NumberNotificationForFeatures numberNotificationForFeatures = new NumberNotificationForFeatures(FeatureDBName.KEY_CABPOOL);
                        numberNotificationForFeatures.getCount(new NumberNotificationForFeatures.MyCallBack() {
                            @Override
                            public void onCallBack(long value) {
                                if (value - current > 0) {
                                    unreadCountCabpoolTV.setText(String.valueOf(value - current));
                                    unreadCountCabpoolFL.setVisibility(View.VISIBLE);
                                } else {
                                    unreadCountCabpoolFL.setVisibility(View.GONE);
                                }
                            }
                        });
                    } else {
                        NumberNotificationForFeatures numberNotificationForFeatures = new NumberNotificationForFeatures(FeatureDBName.KEY_CABPOOL);
                        numberNotificationForFeatures.getCount(new NumberNotificationForFeatures.MyCallBack() {
                            @Override
                            public void onCallBack(long value) {
                                mUserDetails.child("featuresUnreadCount").child(FeatureDBName.KEY_CABPOOL).setValue(value);
                            }
                        });
                    }

                    //for notices
                    if (dataSnapshot.child("featuresUnreadCount").hasChild(FeatureDBName.KEY_NOTICES)) {
                        final long current = dataSnapshot.child("featuresUnreadCount").child(FeatureDBName.KEY_NOTICES).getValue(Long.class);
                        NumberNotificationForFeatures numberNotificationForFeatures = new NumberNotificationForFeatures(FeatureDBName.KEY_NOTICES);
                        numberNotificationForFeatures.getCount(new NumberNotificationForFeatures.MyCallBack() {
                            @Override
                            public void onCallBack(long value) {
                                if (value - current > 0) {
                                    unreadCountNoticesTV.setText(String.valueOf(value - current));
                                    unreadCountNoticesFL.setVisibility(View.VISIBLE);
                                } else {
                                    unreadCountNoticesFL.setVisibility(View.GONE);
                                }
                            }
                        });
                    } else {
                        NumberNotificationForFeatures numberNotificationForFeatures = new NumberNotificationForFeatures(FeatureDBName.KEY_NOTICES);
                        numberNotificationForFeatures.getCount(new NumberNotificationForFeatures.MyCallBack() {
                            @Override
                            public void onCallBack(long value) {
                                mUserDetails.child("featuresUnreadCount").child(FeatureDBName.KEY_NOTICES).setValue(value);
                            }
                        });
                    }
                    if (userItem.getUsername() != null) {
                        if (userItem.getUserType().equals(UsersTypeUtilities.KEY_ADMIN)) {
                            admin.setVisibility(View.VISIBLE);
                            admin.setOnClickListener(new OnSingleClickListener() {
                                @Override
                                public void onSingleClick(View v) {
                                    Log.d(TAG, "clicked on admin");
                                    FirebaseDatabase.getInstance().getReference().child("minimumClientVersion").
                                            child("admin").addListenerForSingleValueEvent(
                                            new ValueEventListener() {
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }

                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                                    try {
                                                        if (dataSnapshot2.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                                            Intent intent = new Intent(context, UpdateAppActivity.class);
                                                            intent.putExtra("feature", "shops");
                                                            context.startActivity(intent);

                                                        } else {
                                                            resetFeaturesUnreadCount(FeatureDBName.KEY_ADMIN_PANEL, dataSnapshot);
                                                            context.startActivity(new Intent(context, AdminHome.class));
                                                        }
                                                    }catch (Exception e){}
                                                }
                                            }
                                    );
                                }
                            });
                        }
                    }
                    events.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            FirebaseDatabase.getInstance().getReference().child("minimumClientVersion")
                                    .child("events").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                            try {
                                                if (dataSnapshot2.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                                    Intent intent = new Intent(context, UpdateAppActivity.class);
                                                    intent.putExtra("feature", "shops");
                                                    context.startActivity(intent);

                                                } else {
                                                    if (!(userItem.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || userItem.getUserType().equals(UsersTypeUtilities.KEY_PENDING))) {
                                                        resetFeaturesUnreadCount(FeatureDBName.KEY_EVENTS, dataSnapshot);
                                                        Intent intent = new Intent(context, TabbedEvents.class);
                                                        context.startActivity(intent);

                                                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                                                        HashMap<String, String> meta = new HashMap<>();

                                                        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                                        counterItemFormat.setUniqueID(CounterUtilities.KEY_EVENTS_OPEN);
                                                        counterItemFormat.setTimestamp(System.currentTimeMillis());
                                                        counterItemFormat.setMeta(meta);

                                                        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                                        counterPush.pushValues();
                                                    } else {
                                                        newUserVerificationAlert.buildAlertCheckNewUser(userItem.getUserType(), "Events", context);
                                                    }
                                                }

                                            }catch (Exception e){}
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError e) {
                                        }
                                    });

                    }
                    });

                    storeroom.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            FirebaseDatabase.getInstance().getReference().child("minimumClientVersion")
                                    .child("storeroom").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                            try {
                                                if (dataSnapshot2.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                                    Intent intent = new Intent(context, UpdateAppActivity.class);
                                                    intent.putExtra("feature", "shops");
                                                    context.startActivity(intent);

                                                } else {
                                                    if (!(userItem.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || userItem.getUserType().equals(UsersTypeUtilities.KEY_PENDING))) {
                                                        resetFeaturesUnreadCount(FeatureDBName.KEY_STOREROOM, dataSnapshot);
                                                        Intent intent = new Intent(context, TabStoreRoom.class);
                                                        context.startActivity(intent);
                                                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                                                        HashMap<String, String> meta = new HashMap<>();


                                                        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                                        counterItemFormat.setUniqueID(CounterUtilities.KEY_STOREROOM_OPEN);
                                                        counterItemFormat.setTimestamp(System.currentTimeMillis());
                                                        counterItemFormat.setMeta(meta);

                                                        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                                        counterPush.pushValues();
                                                    } else {
                                                        newUserVerificationAlert.buildAlertCheckNewUser(userItem.getUserType(), "Storeroom", context);
                                                    }
                                                }
                                            }catch (Exception e){}
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    });

                    cabpool.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {

                            FirebaseDatabase.getInstance().getReference().child("minimumClientVersion").
                                    child("cabpool").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                            try {
                                                Log.d("VERSIONN", dataSnapshot2.getValue(Integer.class) + "");
                                                if (dataSnapshot2.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                                    Intent intent = new Intent(context, UpdateAppActivity.class);
                                                    intent.putExtra("feature", "shops");
                                                    context.startActivity(intent);

                                                } else {

                                                    if (!(userItem.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || userItem.getUserType().equals(UsersTypeUtilities.KEY_PENDING))) {
                                                        resetFeaturesUnreadCount(FeatureDBName.KEY_CABPOOL, dataSnapshot);
                                                        Intent intent = new Intent(context, CabPoolAll.class);
                                                        context.startActivity(intent);
                                                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                                                        HashMap<String, String> meta = new HashMap<>();

                                                        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                                        counterItemFormat.setUniqueID(CounterUtilities.KEY_CABPOOL_OPEN);
                                                        counterItemFormat.setTimestamp(System.currentTimeMillis());
                                                        counterItemFormat.setMeta(meta);

                                                        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                                        counterPush.pushValues();
                                                    } else {
                                                        newUserVerificationAlert.buildAlertCheckNewUser(userItem.getUserType(), "Cab Pool", context);
                                                    }

                                                }
                                            }catch (Exception e){}
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    });

                    internships.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            FirebaseDatabase.getInstance().getReference().child("minimumClientVersion")
                                    .child("internships").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {

                                            try {
                                                Log.d("VERSIONN", dataSnapshot2.getValue(Integer.class) + "");
                                                if (dataSnapshot2.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                                    Intent intent = new Intent(context, UpdateAppActivity.class);
                                                    intent.putExtra("feature", "shops");
                                                    context.startActivity(intent);

                                                } else {
                                                    if (!(userItem.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || userItem.getUserType().equals(UsersTypeUtilities.KEY_PENDING))) {
                                                        resetFeaturesUnreadCount(FeatureDBName.KEY_INTERNSHIPS, dataSnapshot);
                                                        Intent intent = new Intent(context, Internships.class);
                                                        context.startActivity(intent);

                                                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                                                        HashMap<String, String> meta = new HashMap<>();
                                                        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                                        counterItemFormat.setUniqueID(CounterUtilities.KEY_INTERNSHIPS_OPEN);
                                                        counterItemFormat.setTimestamp(System.currentTimeMillis());
                                                        counterItemFormat.setMeta(meta);

                                                        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                                        counterPush.pushValues();
                                                    } else {
                                                        newUserVerificationAlert.buildAlertCheckNewUser(userItem.getUserType(), "Internships", context);
                                                    }

                                                }
                                            }catch (Exception e){}
                                        }
                                    });
                        }});


                    notices.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            FirebaseDatabase.getInstance().getReference().child("minimumClientVersion").
                                    child("notices").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot d) {
                                            Log.d("VERSIONN", d.getValue(Integer.class) + "");
                                            if (d.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                                Intent intent = new Intent(context, UpdateAppActivity.class);
                                                intent.putExtra("feature", "shops");
                                                context.startActivity(intent);

                                            } else {
                                                if (!(userItem.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || userItem.getUserType().equals(UsersTypeUtilities.KEY_PENDING))) {
                                                    resetFeaturesUnreadCount(FeatureDBName.KEY_NOTICES, dataSnapshot);
                                                    Intent intent = new Intent(context, Notices.class);
                                                    context.startActivity(intent);
                                                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                                                    HashMap<String, String> meta = new HashMap<>();

                                                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                                    counterItemFormat.setUniqueID(CounterUtilities.KEY_NOTICES_OPEN);
                                                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                                                    counterItemFormat.setMeta(meta);

                                                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                                    counterPush.pushValues();
                                                } else {
                                                    newUserVerificationAlert.buildAlertCheckNewUser(userItem.getUserType(), "Notices", context);
                                                }
                                            }}});

                        }
                    });

                    shops.setOnClickListener(new OnSingleClickListener() {
                        @Override
                        public void onSingleClick(View v) {
                            Log.d("JJJJJ", "inside click");
                            FirebaseDatabase.getInstance().getReference().child("minimumClientVersion")
                                    .child("shops").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                            try {
                                                Log.d("JJJJJ", "inside try");
                                                Log.d("VERSIONN", dataSnapshot2.getValue(Integer.class) + "");
                                                if (dataSnapshot2.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                                    Log.d("JJJJJ", "inside if");
                                                    Intent intent = new Intent(context, UpdateAppActivity.class);
                                                    intent.putExtra("feature", "shops");
                                                    context.startActivity(intent);

                                                } else {
                                                    Log.d("JJJJJ", "inside else");
                                                    if (!(userItem.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || userItem.getUserType().equals(UsersTypeUtilities.KEY_PENDING))) {
                                                        resetFeaturesUnreadCount(FeatureDBName.KEY_SHOPS, dataSnapshot);
                                                        Intent intent = new Intent(context, PoolActivity.class);
                                                        context.startActivity(intent);

                                                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                                                        HashMap<String, String> meta = new HashMap<>();
                                                        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                                        counterItemFormat.setUniqueID(CounterUtilities.KEY_SHOPS_OPEN);
                                                        counterItemFormat.setTimestamp(System.currentTimeMillis());
                                                        counterItemFormat.setMeta(meta);

                                                        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                                        counterPush.pushValues();
                                                    } else {
                                                        newUserVerificationAlert.buildAlertCheckNewUser(userItem.getUserType(), "Shop", context);
                                                    }

                                                }
                                            }catch (Exception e){
                                                Log.d("JJJJJ", "inside catch" + e.getMessage());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    }
                            );


                        }
                    });

                    links.setOnClickListener(new OnSingleClickListener() {
                        @Override
                               public void onSingleClick(View v){
                            FirebaseDatabase.getInstance().getReference().child("minimumClientVersion")
                                    .child("links").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                            try {
                                                Log.d("VERSIONN", dataSnapshot2.getValue(Integer.class) + "");
                                                if (dataSnapshot2.getValue(Integer.class) > BuildConfig.VERSION_CODE) {
                                                    Intent intent = new Intent(context, UpdateAppActivity.class);
                                                    intent.putExtra("feature", "shops");
                                                    context.startActivity(intent);

                                                } else {
                                                    if (!(userItem.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || userItem.getUserType().equals(UsersTypeUtilities.KEY_PENDING))) {
                                                        resetFeaturesUnreadCount(FeatureDBName.KEY_LINKS, dataSnapshot);
                                                        Intent intent = new Intent(context, Links.class);
                                                        context.startActivity(intent);

                                                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                                                        HashMap<String, String> meta = new HashMap<>();
                                                        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                                        counterItemFormat.setUniqueID(CounterUtilities.KEY_LINKS_OPEN);
                                                        counterItemFormat.setTimestamp(System.currentTimeMillis());
                                                        counterItemFormat.setMeta(meta);

                                                        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                                        counterPush.pushValues();
                                                    } else {
                                                        newUserVerificationAlert.buildAlertCheckNewUser(userItem.getUserType(), "Links", context);
                                                    }
                                                }
                                            }catch (Exception e){}
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    }
                            );

                        }
                    });

                    mOtherFeatures.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot2) {
                            if (flag) {

                                for (final DataSnapshot shot : dataSnapshot2.getChildren()) {

                                    if (Integer.parseInt(shot.child("show").getValue().toString()) == 0)
                                        continue;

                                    otherFeatureItemLayout = (LinearLayout) View.inflate(context, R.layout.recents_features_view_item, null);
                                    featureName = (TextView) otherFeatureItemLayout.findViewById(R.id.name_recents_features_view_item);
                                    otherFeatureIcon = (SimpleDraweeView) otherFeatureItemLayout.findViewById(R.id.icon_recents_features_view_item);
                                    otherFeatureIcon.setImageURI(shot.child("image").getValue().toString());
                                    featureName.setText(shot.child("name").getValue(String.class));
                                    otherFeatureItemLayout.setOnClickListener(new OnSingleClickListener() {
                                        @Override
                                        public void onSingleClick(View v) {
                                            if (!(userItem.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || userItem.getUserType().equals(UsersTypeUtilities.KEY_PENDING))) {
                                                Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(shot.child("URL").getValue().toString()));
                                                context.startActivity(urlIntent);
                                            } else {
                                                newUserVerificationAlert.buildAlertCheckNewUser(userItem.getUserType(), shot.child("name").getValue(String.class), context);
                                            }

                                        }
                                    });
                                    linearLayout.addView(otherFeatureItemLayout);
                                }
                                flag = false;
                            }
                            linearLayout.setVerticalScrollbarPosition(0);
                            linearLayout.setHorizontalGravity(0);
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    hsv.getViewTreeObserver()
                            .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                                @Override
                                public void onScrollChanged() {
                                    if (!hsv.canScrollHorizontally(1)) {
                                        rightArrow.setVisibility(View.VISIBLE);
                                        leftArrow.setVisibility(View.GONE);

                                    }
                                    if (!hsv.canScrollHorizontally(-1)) {

                                        rightArrow.setVisibility(View.GONE);
                                        leftArrow.setVisibility(View.VISIBLE);

                                    }
                                }
                            });




                    if(linearLayout.getWidth()<totalLinearLayout.getWidth()){
                        rightArrow.setVisibility(View.GONE);
                        leftArrow.setVisibility(View.GONE);
                    }else if(linearLayout.getWidth()>totalLinearLayout.getWidth()) {
                        leftArrow.setVisibility(View.VISIBLE);
                        rightArrow.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public void setFeatureVisibility(CommunityFeatures communityFeatures) {

            try {

                try {

                    if (communityFeatures.getCabpool().equals("true")) {
                        cabpool.setVisibility(View.VISIBLE);
                    } else {
                        cabpool.setVisibility(View.GONE);
                    }
                }
                catch (Exception e){

                }



                try {
                    if (communityFeatures.getEvents().equals("true")) {
                        events.setVisibility(View.VISIBLE);
                    } else {
                        events.setVisibility(View.GONE);
                    }
                }
                catch(Exception e){

                }

                try {

                    if (communityFeatures.getNotices().equals("true")) {
                        notices.setVisibility(View.VISIBLE);
                    } else {
                        notices.setVisibility(View.GONE);
                    }
                }
                catch (Exception e){}

                try {

                    if (communityFeatures.getLinks().equals("true")) {
                        links.setVisibility(View.VISIBLE);
                    } else {
                        links.setVisibility(View.VISIBLE);
                    }
                }
                catch (Exception e){}

                try {

                    if (communityFeatures.getStoreroom().equals("true")) {
                        storeroom.setVisibility(View.VISIBLE);
                    } else {
                        storeroom.setVisibility(View.GONE);
                    }
                }
                catch (Exception e){}

                try {

                    if (communityFeatures.getShops().equals("true")) {
                        shops.setVisibility(View.VISIBLE);
                    } else {
                        shops.setVisibility(View.GONE);
                    }
                }
                catch (Exception e){}

                try {
                    if (communityFeatures.getInternships().equals("true")) {
                        internships.setVisibility(View.VISIBLE);
                    } else {
                        internships.setVisibility(View.GONE);
                    }

                }catch (Exception e){}


            } catch (Exception e) {

            }



        }


        public void resetFeaturesUnreadCount(final String featureDBName, DataSnapshot dataSnapshot)
        {
            if(dataSnapshot.child("featuresUnreadCount").hasChild(featureDBName) && dataSnapshot.child("featuresUnreadCount").child(featureDBName).getValue(Long.class) >= 0)
            {

                NumberNotificationForFeatures numberNotificationForFeatures = new NumberNotificationForFeatures(featureDBName);
                numberNotificationForFeatures.getCount(new NumberNotificationForFeatures.MyCallBack() {
                    @Override
                    public void onCallBack(long value) {
                        Log.d(TAG, String.valueOf(value) + " " + featureDBName);
                        mUserDetails.child("featuresUnreadCount").child(featureDBName).setValue(value).continueWithTask(new Continuation<Void, Task<Long>>() {
                            @Override
                            public Task<Long> then(@NonNull Task<Void> task) throws Exception {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "sucessful");
                                } else {
                                    Log.d(TAG, "unsuccessful");
                                    task.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, e.getMessage());
                                        }
                                    });
                                }
                                return null;
                            }
                        });
                    }
                });
            }

        }
    }
}
